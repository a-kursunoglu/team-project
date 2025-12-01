package fuze.usecases.uploadclothing;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class UploadClothingOutputDataTest {
    @Test
    void testConstructor() {
        UploadClothingOutputData data = new UploadClothingOutputData("name", "message");
        assertEquals("name", data.getName());
        assertEquals("message", data.getMessage());
    }
}
