package base;

import com.github.javafaker.Faker;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.BeforeAll;
import static io.restassured.RestAssured.given;


public class BaseTest {
    protected static final String KEY = "";
    protected static final String TOKEN = "";
    protected static final String BASE_URL = "https://api.trello.com/1/";
    protected static final  String BOARDS = "boards";
    protected static final String LISTS = "lists";
    protected static final String CARDS = "Cards";
    protected static final String ORGANIZATION = "organizations";

    protected static RequestSpecBuilder reqBuilder;
    protected static RequestSpecification reqSpec;


    protected static Faker faker;


    @BeforeAll
    public static void beforeAll(){
        reqBuilder = new RequestSpecBuilder();
        reqBuilder.addQueryParam("key", KEY);
        reqBuilder.addQueryParam("token", TOKEN);
        reqBuilder.setContentType(ContentType.JSON);
        reqSpec = reqBuilder.build();

        faker = new Faker();

    }

    public static void deleteOrganization(String orgId, RequestSpecification reqSpec, final String BASE_URL, final String ORGANIZATION) {
        given()
                .spec(reqSpec)
                .when()
                .delete(BASE_URL + ORGANIZATION + "/" + orgId)
                .then()
                .statusCode(200);
    }

   }
