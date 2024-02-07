package com.asteral.asteralserver;

import java.io.IOException;
import java.net.ServerSocket;

public class AsteralServer {

    public static void main(String[] args) {
        UserManager userManager = new UserManager("data.txt", "buffer.txt");
        userManager.flush();
        userManager.load();
        ProductManager productManager = new ProductManager("pdata.txt", "pbuffer.txt");
        productManager.flush();
        productManager.load();
        GiftcardManager giftcardManager = new GiftcardManager("gdata.txt", "gbuffer.txt");
        giftcardManager.flush();
        giftcardManager.load();
        
        RequestManager.setUserManager(userManager);
        RequestManager.setProductManager(productManager);
        RequestManager.setGiftcardManager(giftcardManager);
        
        try (ServerSocket serverSocket = new ServerSocket(8080)) {
            while (true) {
                RequestManager requestManager = new RequestManager(serverSocket.accept());
                requestManager.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
