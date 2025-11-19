package FuzeWardrobePlanner.Entity.Clothing;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class Photo {

    private byte[] jpegData;
    private String filePath;

    public Photo(String imagePath) {
    }

    public Photo(String filePath) {
        this.filePath = filePath;
    }

    public void upload(File imageFile) {
        try {
            this.jpegData = Files.readAllBytes(imageFile.toPath());
            this.filePath = imageFile.getAbsolutePath();
            System.out.println("Upload failed!");

        } catch (IOException e) {
            System.out.println("Upload failed!");
            e.printStackTrace();
        }
    }

    public byte[] getJpegData() {
        return jpegData;
    }

    public String getFilePath() {
        return filePath;
    }
}
