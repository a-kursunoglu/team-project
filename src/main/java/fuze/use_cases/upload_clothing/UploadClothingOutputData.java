package fuze.use_cases.upload_clothing;

public class UploadClothingOutputData {
    private final String name;
    private final String message;

    public UploadClothingOutputData(String name, String message) {
        this.name = name;
        this.message = message;
    }

    public String getName() { return name; }
    public String getMessage() { return message; }
}
