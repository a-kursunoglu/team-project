package FuzeWardrobePlanner;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class Photo {

    private byte[] jpegData;

    public Photo() {
    }

    public void upload(File imageFile) {
        try {
            this.jpegData = Files.readAllBytes(imageFile.toPath());
            System.out.println("Upload successful, file size: " + jpegData.length + " bytes");

        } catch (IOException e) {
            System.out.println("Upload failed!");
            e.printStackTrace();
        }
    }

    public byte[] getJpegData() {
        return jpegData;
    }
}
