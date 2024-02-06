package com.asteral.asteralserver;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;

public class ProductManager {
    private ArrayList<Product> products;
    private String productDataPath;
    private String productBufferPath;

    public ProductManager(String productDataPath, String productBufferPath) {
        this.products = new ArrayList<>();
        this.productDataPath = productDataPath;
        this.productBufferPath = productBufferPath;
        
        File productData = new File(productDataPath), productBuffer = new File(productBufferPath);
        if (!productData.isFile())
            try {
                productData.createNewFile();
            } catch (IOException e) {
                System.err.println("ProductManager: Creating productData file failed");
            }
        if (!productBuffer.isFile())
            try {
                productBuffer.createNewFile();
            } catch (IOException e) {
                System.err.println("ProducManager: Creating productBuffer file failed");
            }
        File videos = new File("Videos");
        File music = new File("Music");
        File images = new File("Images");
        
        if (!videos.isDirectory())
            videos.mkdir();
        if (!music.isDirectory())
            music.mkdir();
        if (!images.isDirectory())
            images.mkdir();
    }
    
    public void load() { /* load the products from file into the ArrayList */
        try (BufferedReader reader = new BufferedReader(new FileReader(productDataPath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                Product product = new Product();
                product.readFromLine(line);
                products.add(product);
            }
        } catch (IOException e) {
            System.err.println("ProductManager.load() : IOException");
        }
    }
    
    public void flush() { /* move the changes saved in the buffer to file */
        File tmpFile = new File(productDataPath + ".tmp");
        HashMap<String, String> changes = new HashMap<>();
        
        try (BufferedReader buffer = new BufferedReader(new FileReader(productBufferPath))) {
            String line;
            while ((line = buffer.readLine()) != null)
                changes.put(line.split("\t")[0], line);
        } catch (IOException e) {
            System.err.println("ProductManager.flush() : IOException : Reading the buffer failed");
        }
        
        try (BufferedReader in = new BufferedReader(new FileReader(productDataPath));
                PrintWriter tmp = new PrintWriter(new BufferedWriter(new FileWriter(tmpFile)))) {
            String line;
            while ((line = in.readLine()) != null) {
                String newLine; /* updated line */
                if ((newLine = changes.get(line.split("\t")[0])) != null) {
                    Product product = new Product();
                    product.readFromLine(newLine);
                    if (!product.isDeleted())
                        tmp.println(newLine);
                } else {
                    tmp.println(line);
                }
            }
        } catch (IOException e) {
            System.err.println("ProdutManager.flush() : IOException");
        }
        
        try {
            File productData = new File(productDataPath);
            productData.delete();
            tmpFile.renameTo(productData);
            File productBuffer = new File(productBufferPath);
            productBuffer.delete();
            productBuffer.createNewFile();
        } catch (IOException e) {
            System.err.println("ProductManager.flush(): IOException : failed updating files");
        }
        
    }
    
    private void writeProduct2Buffer(Product product) {
        try (PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(productBufferPath, true)))) {
            out.println(product);
        } catch (IOException e) {
            System.err.println("ProductManager.writeProduct2Buffer(): IOException");
        }
    }
    
    private void writeProduct2Data(Product product) {
        try (PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(productDataPath, true)))) {
            out.println(product);
        } catch (IOException e) {
            System.err.println("ProductManager.writeProduct2Buffer(): IOException");
        }
    }
    
    public boolean addProduct(Product product) {
        if (products.indexOf(product) < 0) {
            products.add(product);
            writeProduct2Data(product);
            return true;
        } else 
            return false;
    }
    
    public boolean deleteProduct(Product product) {
        if ((product = getProduct(product)) == null)
            return false;
        product.setLevel(-2);
        products.remove(product);
        writeProduct2Buffer(product);
        return true;
    }
    
    public boolean updateProduct(Product product) {
        if (products.indexOf(product) >= 0) {
            products.remove(product);
            products.add(product);
            writeProduct2Buffer(product);
            return true;
        } else
            return false;
    }
    
    public boolean addReview(Product product, Review review) {
        if ((product = getProduct(product)) == null)
                return false;
        product.addReview(review);
        writeProduct2Buffer(product);
        return true;
    }
    
    public boolean addReview(String name, Review review) {
        return addReview(new Product(name), review);
    }
    
    public Product getProduct(Product product) {
        return getProduct(product.getName());
    }
    
    public Product getProduct(String name) {
        for (Product product: products)
            if (product.getName().equals(name))
                return product;
        return null;
    }

    public ArrayList<Product> search(ArrayList<String> queries) {
        ArrayList<Integer> sums = new ArrayList<>();
        ArrayList<Product> tmpResults = new ArrayList<>();
        
        for (Product product: products) {
            int sum = 0;
            for (String query: queries)
                if (product.toString().contains(query))
                    sum++;
            if (sum > 0) {
                sums.add(sum);
                tmpResults.add(product);
            }
        }
        
        ArrayList<Product> results = new ArrayList<>();
        while (!sums.isEmpty()) {
            int iol = indexOfLargest(sums);
            results.add(tmpResults.get(iol));
            tmpResults.remove(iol);
            sums.remove(iol);
        }
        
        return results;
    }
    
    private int indexOfLargest(ArrayList<Integer> v) {        
        int i = 0;
        for (int j = 0; j < v.size(); j++)
            if (v.get(i) < v.get(j))
                i = j;
        return i;
    }
}
