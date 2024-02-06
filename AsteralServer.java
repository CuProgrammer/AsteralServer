package com.asteral.asteralserver;

public class AsteralServer {

    public static void main(String[] args) {
        UserManager userManager = new UserManager("data.txt", "buffer.txt");
        
        /*ProductManager productManager = new ProductManager("komo.txt", "jumo.txt");
        productManager.flush();
        productManager.load();
        /*RequestManager.setProductManager(productManager);
        
        /*try (ServerSocket soc = new ServerSocket(8080)) {
            while (true) {
                RequestManager ma = new RequestManager(soc.accept());
                ma.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        /*ProductManager manager = new ProductManager("komo.txt", "jumo.txt");
        manager.flush();
        manager.load();
        
        System.out.println(manager.getProduct("this is so good"));
        System.out.println(manager.getProduct("bruh"));
        manager.getProduct("bruh").addReview(new Review("JIO", "This is working!", 500));
        manager.updateProduct(manager.getProduct("bruh"));
        System.out.println(manager.getProduct(new Product("kokokokoko")));
        System.out.println(manager.getProduct("bruh").getFilePath("Video"));
        System.out.println(manager.getProduct("bruh").getFilePath("Music"));
        System.out.println(manager.getProduct("bruh").getFilePath("Image"));*/
        /*UserHandler handler = new UserHandler("data.txt", "buffer.txt");
        SettingReader setting = new SettingReader(handler);
        ShopUser admin = new ShopUser("admin_zero", "123456789", "cuprogrammer@gmail.com", 0, 2);
        setting.addDefaultUser(admin);
        ProductHandler phandler = new ProductHandler("ProductData.product", "ProductBuffer.product", handler);
        handler.flushBuffer();
        handler.load();
        phandler.flushBuffer();
        phandler.load();
        RequestHandler.handler = handler;
        RequestHandler.phandler = phandler;
        
        try (ServerSocket asteralServerSocket = new ServerSocket(8080)) {
            while (true) {
                RequestHandler asteral = new RequestHandler(asteralServerSocket.accept());
                asteral.start();
                setting.save();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        */
    }
}
