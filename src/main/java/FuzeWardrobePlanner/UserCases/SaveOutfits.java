package FuzeWardrobePlanner.UserCases;

import org.json.JSONObject;

public class SaveOutfits {

    private String filename;
    private JSONObject file;   // JSON object to store outfit data
    
    public SaveOutfits(String filename, JSONObject file) {
        this.filename = filename;
        this.file = file;
    }

    public void saveToFile() {
        try {
            java.nio.file.Files.write(
                    java.nio.file.Paths.get(filename),
                    file.toString(4).getBytes()
            );
            System.out.println("Save successful!");
        } catch (Exception e) {
            System.out.println("Save failed!");
            e.printStackTrace();
        }
    }

    public String getFilename() {
        return filename;
    }

    public JSONObject getFile() {
        return file;
    }

    public void setFile(JSONObject file) {
        this.file = file;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }
}
