package com.asteral.asteralserver;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

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
        
    }
}
