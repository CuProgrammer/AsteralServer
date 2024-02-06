package com.asteral.asteralserver;

import java.util.Objects;

public class ShopUser {
    private String username;
    private String password;
    private String email;
    private double credit;
    private int level; // -2: delete, -1: deactive, 0: normal, 1: seller, 2: admin

    public ShopUser() {
    }

    public ShopUser(String username, String password, String email, double credit, int level) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.credit = credit;
        this.level = level;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getEmail() {
        return email;
    }

    public double getCredit() {
        return credit;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public void setCredit(double credit) {
        this.credit = credit;
    }
    
    @Override
    public String toString() {
        return username + "\t" + password + "\t" + email + "\t" + credit + "\t" + level;
    }

    public boolean isDeleted() {
        return level == -2;
    }
    
    public void readFromLine(String line, int offset) {
        String[] parts = line.split("\t");
        this.username = parts[offset];
        this.password = parts[offset+1];
        this.email = parts[offset+2];
        this.credit = Double.parseDouble(parts[offset+3]);
        this.level = Integer.parseInt(parts[offset+4]);
    }
    
    public void readFromLine(String line) {
        readFromLine(line, 0);
    }
    
    /**
     * Objects of ShopUser are considered equal if they have the same username
     */
    @Override
    public boolean equals(Object obj) {
        return obj instanceof ShopUser && this.username.equals(((ShopUser) obj).username);
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 71 * hash + Objects.hashCode(this.username);
        return hash;
    }
}
