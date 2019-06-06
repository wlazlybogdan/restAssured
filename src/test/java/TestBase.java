import com.github.javafaker.Faker;
import io.restassured.RestAssured;
import org.junit.Before;

public class TestBase {

    Faker FAKER = new Faker();

    @Before
    public void setUp() {
        RestAssured.baseURI = "http://134.209.233.51";
    }
}
