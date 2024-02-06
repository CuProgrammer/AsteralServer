package com.asteral.asteralserver;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Objects;

public class Product {
    private String name;
    private String sellerUsername;
    private double price;
    private double off;
    private int stock;
    private int level; /* 0: normal, 1: digital */
    private ArrayList<Review> reviews;

    public Product(String name, String sellerUsername, double price, double off, int stock, int level) {
        this.name = name;
        this.sellerUsername = sellerUsername;
        this.price = price;
        this.off = off;
        this.stock = stock;
        this.level = level;
        this.reviews = new ArrayList<>();
    }

    public Product() {
        this(null, null, 0, 0, 0, -1);
    }
    
    public Product(String name) {
        this();
        this.name = name;
    }
    
    public void addReview(Review review) {
        if (reviews.indexOf(review) >= 0) {
            reviews.remove(review);
            reviews.add(review);
        } else {
            reviews.add(review);
        }
    }
    
    public void clearReviews() {
        reviews.clear();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Product && this.name.equals(((Product) obj).name);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 71 * hash + Objects.hashCode(this.name);
        return hash;
    }

    public int getLevel() {
        return level;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public String getSellerUsername() {
        return sellerUsername;
    }

    public int getStock() {
        return stock;
    }
    
    public void setLevel(int level) {
        this.level = level;
    }

    public void setOff(double off) {
        this.off = off;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }
    
    public boolean isDigital() {
        return level == 1;
    }
    
    public boolean isNormal() {
        return level == 0;
    }
    
    public boolean isDeleted() {
        return level == -2;
    }
    
    @Override
    public String toString() {
        String base = name + "\t" + sellerUsername + "\t" + price + "\t" + off + "\t" + stock + "\t" + level;
        String rest = "";
        for (Review review: reviews)
            rest += "\t" + review;
        return base + rest;
    }
    
    public void readFromLine(String line) {
        readFromLine(line, 0);
    }
    
    public void readFromLine(String line, int offset) {
        String[] parts = line.split("\t");
        this.name = parts[offset];
        this.sellerUsername = parts[offset + 1];
        this.price = Double.parseDouble(parts[offset+2]);
        this.off = Double.parseDouble(parts[offset+3]);
        this.stock = Integer.parseInt(parts[offset+4]);
        this.level = Integer.parseInt(parts[offset+5]);
        
        for (int i = offset+6; i < parts.length; i += 3) {
            addReview(new Review(parts[i], parts[i+1], Integer.parseInt(parts[i+2])));
        }
    }
    
    public String getFilePath(String type) {
        return switch (type) {
            case "Video" -> "Videos\\" + hashString(this.name + type) + ".mp4";
            case "Music" -> "Music\\" + hashString(this.name + type) + ".mp3";
            case "Image" -> "Images\\" + hashString(this.name + type) + ".jpg";
            default -> null;
        };
    }
    
    private static String hashString(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("sha256");
            byte[] hashBytes = digest.digest(input.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hashBytes) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() ==1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public double getOff() {
        return off;
    }
}