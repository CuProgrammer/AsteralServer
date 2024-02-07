package com.asteral.asteralserver;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

public class RequestManager extends Thread {
    private static ProductManager productManager;
    private static UserManager userManager;
    private static GiftcardManager giftcardManager;
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    

    public RequestManager(Socket socket) {
        this.socket = socket;
        try {
            this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.out = new PrintWriter(socket.getOutputStream(), true);
        } catch (IOException e) {
            System.err.println("RequestManager: IOException");
        }
    }

    public static void setProductManager(ProductManager productManager) {
        RequestManager.productManager = productManager;
    }

    public static void setUserManager(UserManager userManager) {
        RequestManager.userManager = userManager;
    }

    public static void setGiftcardManager(GiftcardManager giftcardManager) {
        RequestManager.giftcardManager = giftcardManager;
    }
    
    @Override
    public void run() {
        String request = listen();
        System.out.println("request: " + request);
        String[] parts = request.split("\t");
        
        switch (parts[0]) {
            case "SearchProducts" -> {
                ArrayList<String> queries = new ArrayList<>();
                for (int i = 1; i < parts.length; i++)
                    queries.add(parts[i]);
                ArrayList<Product> results = productManager.search(queries);
                System.out.println(results);
                for (Product result: results)
                    tell(result);
            }
            
            case "SignUp" -> {
                ShopUser user = new ShopUser();
                user.readFromLine(request, 1);
                user.setCredit(0);
                user.setLevel(0);
                tell((userManager.addUser(user) ? "SignUp Successful" : "User Already Exists"));
            }
            
            case "Login" -> {
                ShopUser user = new ShopUser();
                user.readFromLine(request, 1);
                ShopUser tmp;
                if ((tmp = userManager.getUser(user)) == null)
                    tell("User does not exist");
                else if (tmp.getPassword().equals(user.getPassword()))
                    tell("Login Successful", tmp);
                else
                    tell("Incorrect username or password");
            }
            
            case "GenerateGiftcard" -> {
                ShopUser user = new ShopUser();
                user.readFromLine(request, 1);
                if (userManager.validate(user.getUsername(), user.getPassword()) && userManager.getUser(user).getLevel() >= 2)
                    tell("GenerateGiftcard Successful", giftcardManager.generateGiftcard(Double.parseDouble(parts[6])));
                else
                    tell("Invalid Admin Account");
            }
            
            
            case "AddProduct" -> {
                ShopUser user = new ShopUser(), tmp;
                user.readFromLine(request, 1);
                Product product = new Product();
                product.readFromLine(request, 6);
                
                if ((tmp = userManager.getUser(user)) != null && tmp.getPassword().equals(user.getPassword()) && tmp.getLevel() >= 1) {
                    product.clearReviews();
                    tell((productManager.addProduct(product) ? "AddProduct Successful" : "Product Already Exists"));
                } else {
                    tell("Invalid Seller Account");
                }
            }
            
            case "UpdateProduct" -> {
                ShopUser user = new ShopUser(), tmp;
                user.readFromLine(request, 1);
                Product product = new Product(), tmpProduct;
                product.readFromLine(request, 6);
                
                if ((tmp = userManager.getUser(user)) != null && tmp.getPassword().equals(user.getPassword()) &&
                        (tmpProduct = productManager.getProduct(product)) != null && tmpProduct.getSellerUsername().equals(user.getUsername())) {
                    tmpProduct.setPrice(product.getPrice());
                    tmpProduct.setStock(product.getStock());
                    tmpProduct.setLevel(product.getLevel());
                    tmpProduct.setOff(product.getOff());
                    productManager.updateProduct(tmpProduct);
                    tell("UpdateProduct Successful");
                } else {
                    tell("Invalid Seller Account");
                }
            }
            
            case "AddReview" -> {
                ShopUser user = new ShopUser(), tmp;
                user.readFromLine(request, 1);
                String productName = parts[6];
                Review review = Review.readReviewFromLine(request, 7);
                if ((tmp = userManager.getUser(user)) != null && tmp.getPassword().equals(user.getPassword()) && tmp.getLevel() >= 0
                        && review.getPoster().equals(user.getUsername())) {
                    tell((productManager.addReview(productName, review) ? "AddReview Successful" : "Product does not exist")); 
                } else {
                    tell("Invalid Reviewer Account");
                }
            }
            
            case "UpdateUserStatus" -> {
                ShopUser adminUser = new ShopUser(), tmp;
                adminUser.readFromLine(request, 1);
                if ((tmp = userManager.getUser(adminUser)) != null && tmp.getPassword().equals(adminUser.getPassword()) && tmp.getLevel() >= 2) {
                    ShopUser subjectUser = userManager.getUser(parts[6]);
                    int newLevel = Integer.parseInt(parts[7]);
                    if (subjectUser != null) {
                        subjectUser.setLevel(newLevel);
                        tell(userManager.updateUser(subjectUser) ? "UpdateUserStatus Successful" : "Unknown Error");
                    } else {
                        tell("User doesn't exist");
                    }
                } else {
                    tell("Invalid Admin Account");
                }
            }
            
            case "UploadProductFile" -> {
                ShopUser sellerUser = new ShopUser(), tmp;
                sellerUser.readFromLine(request, 1);
                Product product = productManager.getProduct(parts[6]);
                String type = parts[7];
                
                if ((tmp = userManager.getUser(sellerUser)) != null && tmp.getPassword().equals(sellerUser.getPassword()) && product != null &&
                        product.getSellerUsername().equals(sellerUser.getUsername())) {
                    tell("UploadProductFile Successful");
                    downloadFile(product.getFilePath(type));
                } else {
                    tell("Invalid Seller Username/Product doesn't exist");
                }
            }
            
            case "DownloadProductFile" -> {
                Product product = productManager.getProduct(parts[1]);
                String type = parts[2];
                if (product != null) {
                    uploadFile(product.getFilePath(type));
                }
            }
            
            case "GetSellerProducts" -> {
                ShopUser seller;
                if ((seller = userManager.getUser(parts[1])) != null && seller.getLevel() >= 1) {
                    ArrayList<Product> results = productManager.getSellerProducts(parts[1]);
                    for (Product result:results)
                        tell(result);
                }
            }
            
            default -> {
                tell("UnknownRequest");
            }
        }
        
        disconnect();
    }
    
    public String listen() {
        try {
            return in.readLine();
        } catch (IOException e) {
            System.err.println("RequestManager.listen() : IOException");
            return null;
        }
    }
    
    public void tell(Object... objects) {
        String saying = "";
        for (int i = 0; i < objects.length; i++) {
            saying += objects[i];
            if (i < objects.length-1)
                saying += "\t";
        }
        
        out.println(saying); // add flush if ran into problem
        //out.flush();
    }
    
    public void downloadFile(String path) {
        try {
            if (new File(path).isFile())
                new File(path).delete();
            Files.copy(socket.getInputStream(), Path.of(path));
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("RequestManager.downloadFile() : IOException");
        }
    }
    
    public void uploadFile(String path) {
        try {
            Files.copy(Path.of(path), socket.getOutputStream());
        } catch (IOException e) {
            System.err.println("RequestManager.uploadFile(): IOException");
        }
    }
    
    private void disconnect() {
        try {
            out.close();
            in.close();
            this.socket.close();
        } catch (IOException e) {
            System.err.println("RequestManager.disconnect(): IOException");
        }
    }

}
