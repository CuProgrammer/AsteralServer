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

public class UserManager {
    private HashMap<String, ShopUser> users;
    private ArrayList<String> usernames;
    private String userDataPath;
    private String userBufferPath;

    public UserManager(String userDataPath, String userBufferPath) {
        this.users = new HashMap<>();
        this.usernames = new ArrayList<>();
        this.userDataPath = userDataPath;
        this.userBufferPath = userBufferPath;
        
        File userData = new File(userDataPath), userBuffer = new File(userBufferPath);
        if (!userData.isFile())
            try {
                userData.createNewFile();
            } catch (IOException e) {
                System.err.println("UserManager: Creating userData file failed");
            }
        if (!userBuffer.isFile())
            try {
                userBuffer.createNewFile();
            } catch (IOException e) {
                System.err.println("UserManager: Creating userBuffer file failed");
            }
    }
    
    public void load() { /* load the users from file into the ArrayList */
        try (BufferedReader reader = new BufferedReader(new FileReader(userDataPath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                ShopUser user = new ShopUser();
                user.readFromLine(line);
                users.put(user.getUsername(), user);
                usernames.add(user.getUsername());
            }
        } catch (IOException e) {
            System.err.println("UserManager.load() : IOException");
        }
    }
    
    public void flush() { /* move the changes saved in the buffer to file */
        File tmpFile = new File(userDataPath + ".tmp");
        HashMap<String, String> changes = new HashMap<>();
        
        try (BufferedReader buffer = new BufferedReader(new FileReader(userBufferPath))) {
            String line;
            while ((line = buffer.readLine()) != null)
                changes.put(line.split("\t")[0], line);
        } catch (IOException e) {
            System.err.println("UserManager.flush() : IOException : Reading the buffer failed");
        }
        
        try (BufferedReader in = new BufferedReader(new FileReader(userDataPath));
                PrintWriter tmp = new PrintWriter(new BufferedWriter(new FileWriter(tmpFile)))) {
            String line;
            while ((line = in.readLine()) != null) {
                String newLine; /* updated line */
                if ((newLine = changes.get(line.split("\t")[0])) != null) {
                    ShopUser user = new ShopUser();
                    user.readFromLine(newLine);
                    if (!user.isDeleted())
                        tmp.println(newLine);
                } else {
                    tmp.println(line);
                }
            }
        } catch (IOException e) {
            System.err.println("UserManager.flush() : IOException");
        }
        
        try {
            File productData = new File(userDataPath);
            productData.delete();
            tmpFile.renameTo(productData);
            File productBuffer = new File(userBufferPath);
            productBuffer.delete();
            productBuffer.createNewFile();
        } catch (IOException e) {
            System.err.println("UserManager.flush(): IOException : failed updating files");
        }
    }
    
    private void writeUser2Data(ShopUser user) {
        try (PrintWriter out = new PrintWriter(new FileWriter(userDataPath, true))) {
            out.println(user);
        } catch (IOException e) {
            System.err.println("UserManager.writeUser2Data() : IOException");
        }
    }
    
    private void writeUser2Buffer(ShopUser user) {
        try (PrintWriter out = new PrintWriter(new FileWriter(userBufferPath, true))) {
            out.println(user);
        } catch (IOException e) {
            System.err.println("UserManager.writeUser2Buffer() : IOException");
        }
    }
    
    public boolean addUser(ShopUser user) {
        if (users.get(user.getUsername()) != null)
            return false;
        usernames.add(user.getUsername());
        users.put(user.getUsername(), user);
        writeUser2Data(user);
        return true;
    }
    
    public boolean deleteUser(ShopUser user) {
        if ((user = users.get(user.getUsername())) == null)
            return false;
        user.setLevel(-2);
        writeUser2Buffer(user);
        users.remove(user.getUsername());
        usernames.remove(user.getUsername());
        return true;
    }
    
    public boolean updateUser(ShopUser user) {
        if (getUser(user) == null)
            return false;
        users.put(user.getUsername(), user);
        writeUser2Buffer(user);
        return true;
    }
    
    public boolean deleteUser(String username) {
        return deleteUser(new ShopUser(username, null, null, 0, 0));
    }
    
    public ShopUser getUser(String username) {
        return users.get(username);
    }
    
    public ShopUser getUser(ShopUser user) {
        return getUser(user.getUsername());
    }
}
