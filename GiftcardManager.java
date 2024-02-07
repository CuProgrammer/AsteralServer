package com.asteral.asteralserver;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.HashSet;

public class GiftcardManager {
    private HashMap<String, Double> giftcards;
    private String giftcardDataPath;
    private String giftcardBufferPath;

    public GiftcardManager(String giftcardDataPath, String giftcardBufferPath) {
        this.giftcards = new HashMap<>();
        this.giftcardDataPath = giftcardDataPath;
        this.giftcardBufferPath = giftcardBufferPath;
        
        File giftcardData = new File(giftcardDataPath);
        File giftcardBuffer = new File(giftcardBufferPath);
        
        if (!giftcardData.isFile())
            try {
                giftcardData.createNewFile();
            } catch (IOException e) {
                System.err.println("GiftcardManager: Creating giftcardData file failed");
            }
        if (!giftcardBuffer.isFile())
            try {
                giftcardBuffer.createNewFile();
            } catch (IOException e) {
                System.err.println("GiftcardManager: Creating giftcardBuffer file failed");
            }
    }
    
    public void load() {
        try (BufferedReader in = new BufferedReader(new FileReader(giftcardDataPath))) {
            String line;
            while ((line = in.readLine()) != null)
                giftcards.put(line.split("\t")[0], Double.valueOf(line.split("\t")[1]));
        } catch (IOException e) {
            System.err.println("GiftcardManager.load(): IOException");
        }
    }
    
    public void flush() {
        HashSet<String> deleted = new HashSet<>();
        
        try (BufferedReader in = new BufferedReader(new FileReader(giftcardBufferPath))) {
            String giftcard;
            while ((giftcard = in.readLine()) != null)
                deleted.add(giftcard);
        } catch (IOException e) {
            System.err.println("GiftcardManager.flush() : IOException : failed to read deleted giftcards");
        }
        
        try (BufferedReader in = new BufferedReader(new FileReader(giftcardDataPath));
             PrintWriter tmpOut = new PrintWriter(new BufferedWriter(new FileWriter("gtmp.txt")))) {
            String line;
            while ((line = in.readLine()) != null) {
                if (!deleted.contains(line.split("\t")[0]))
                    tmpOut.println(line);
            }
        } catch (IOException e) {
            System.err.println("GiftcardManager.flush(): IOException");
        }
        
        File tmp = new File("gtmp.txt");
        File giftcardData = new File(giftcardDataPath);
        try {
            giftcardData.delete();
            tmp.renameTo(giftcardData);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}