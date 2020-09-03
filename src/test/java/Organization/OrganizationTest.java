package Organization;

import base.BaseTest;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;

public class OrganizationTest extends BaseTest {
    private static String organizationId;

    @Test
    public void createNewOrganization(){
        Response response = given()
                .spec(reqSpec)
                .queryParam("displayName", "Name of the Organization")
                .queryParam("website", "https://neworganization.org")
                .when()
                .post(BASE_URL + ORGANIZATION)
                .then()
                .statusCode(200)
                .extract()
                .response();
        JsonPath json = response.jsonPath();
        Assertions.assertThat(json.getString("displayName")).isEqualTo("Name of the Organization");
        Assertions.assertThat(json.getString("website")).isEqualTo("https://neworganization.org").startsWith("https://");
        organizationId = json.getString("id");

        given()
                .spec(reqSpec)
                .when()
                .delete(BASE_URL + ORGANIZATION + "/" + organizationId)
                .then()
                .statusCode(200);


    }

    @Test
    public void createOrganizationWithEmptyDisplayName(){
        given()
                .spec(reqSpec)
                .queryParam("displayName" ,"")
                .when()
                .post(BASE_URL + ORGANIZATION)
                .then()
                .statusCode(400)
                .extract()
                .response();
    }

    @Test
    public void createOrganizationWithShortName(){
        Response responseShort = given()
                .spec(reqSpec)
                .queryParam("displayName", "Name of the Organization")
                .queryParam("name", "B")
                .when()
                .post(BASE_URL + ORGANIZATION)
                .then()
                .statusCode(200)
                .extract()
                .response();

        JsonPath json = responseShort.jsonPath();
        Assertions.assertThat(json.getString("name")).containsIgnoringCase("B").hasSizeGreaterThan(2);
        organizationId = json.getString("id");
        System.out.println(json.prettyPrint());

        given()
                .spec(reqSpec)
                .when()
                .delete(BASE_URL + ORGANIZATION + "/" + organizationId)
                .then()
                .statusCode(200);
    }

}
