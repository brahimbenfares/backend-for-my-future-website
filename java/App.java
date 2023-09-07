import java.io.File;
import java.io.IOException;

import Controller.SocialMediaController;

public class App {
    public static void main(String[] args) throws IOException {
        try {
            System.out.println("Current directory: " + new File(".").getCanonicalPath());
        } catch (IOException e) {
            e.printStackTrace();
        }

        SocialMediaController socialMediaController = new SocialMediaController();
        socialMediaController.startAPI();
    }
}
