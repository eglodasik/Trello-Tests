package Organization;

import base.BaseTest;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.apache.http.HttpStatus.*;

public class OrganizationTest extends BaseTest {
    static List<String> orgIds = new ArrayList<String>();

    @Test
    public void createFakeOrganization(){
        String fakeCompany = faker.company().name();
        int fakeNumber = faker.number().numberBetween(1, 10);
        String fakeName = faker.name().username().concat(String.valueOf(fakeNumber)).replace(".", "");
        String fakeWebsite = faker.internet().url().replaceFirst("www", "http://www");

        Response response = given()
                .spec(reqSpec)
                .queryParam("displayName", fakeCompany)
                .queryParam("name", fakeName)
               .queryParam("website", fakeWebsite)
                .when()
                .post(BASE_URL + ORGANIZATION)
                .then()
                .statusCode(SC_OK)
                .extract()
                .response();
        JsonPath json = response.jsonPath();
        Assertions.assertThat(json.getString("displayName")).isEqualTo(fakeCompany);
        Assertions.assertThat(json.getString("name")).isEqualTo(fakeName);
       Assertions.assertThat(json.getString("website")).isEqualTo(fakeWebsite);

       orgIds.add(json.getString("id"));
    }

    @Test
    public void createOrganizationWithEmptyDisplayName(){
        given()
                .spec(reqSpec)
                .queryParam("displayName" ,"")
                .when()
                .post(BASE_URL + ORGANIZATION)
                .then()
                .statusCode(SC_BAD_REQUEST)
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
                .statusCode(SC_OK)
                .extract()
                .response();

        JsonPath json = responseShort.jsonPath();
        //Trello automatically adds missing characters
        Assertions.assertThat(json.getString("name")).containsIgnoringCase("B").hasSizeGreaterThan(2);
        orgIds.add(json.getString("id"));
         }

    @Test
    public void createOrganizationWithHTTPS(){
                Response response = given()
                .spec(reqSpec)
                .queryParam("displayName", "Company with Https Website")
                .queryParam("website", "https://neworganization.org")
                .when()
                .post(BASE_URL + ORGANIZATION)
                .then()
                .statusCode(SC_OK)
                .extract()
                .response();
        JsonPath json = response.jsonPath();
        Assertions.assertThat(json.getString("displayName")).isEqualTo("Company with Https Website");
        Assertions.assertThat(json.getString("website")).isEqualTo("https://neworganization.org").startsWith("https://");
                orgIds.add(json.getString("id"));
    }
    @Test
    public void createOrganizationWithHTTP(){
        Response response = given()
                .spec(reqSpec)
                .queryParam("displayName", "Company with Http Website")
                .queryParam("website", "http://neworganization.org")
                .when()
                .post(BASE_URL + ORGANIZATION)
                .then()
                .statusCode(SC_OK)
                .extract()
                .response();
        JsonPath json = response.jsonPath();
        Assertions.assertThat(json.getString("displayName")).isEqualTo("Company with Http Website");
        Assertions.assertThat(json.getString("website")).isEqualTo("http://neworganization.org").startsWith("http://");
                orgIds.add(json.getString("id"));
    }

    @Test
    public void createOrganizationsWithNonUniqueNames() {
        String fakeName = faker.name().username().concat("6").replace(".", "");

                Response response1 = given()
                .spec(reqSpec)
                .queryParam("displayName", "Trello Organization1")
                .queryParam("name", fakeName)
                .when()
                .post(BASE_URL + ORGANIZATION)
                .then()
                .statusCode(SC_OK)
                .extract()
                .response();
        JsonPath json = response1.jsonPath();
        Assertions.assertThat(json.getString("displayName")).isEqualTo("Trello Organization1");
        Assertions.assertThat(json.getString("name")).isEqualTo(fakeName);
        orgIds.add(json.getString("id"));

        Response response2 = given()
                .spec(reqSpec)
                .queryParam("displayName", "Trello Organization2")
                .queryParam("name", fakeName)
                .when()
                .post(BASE_URL + ORGANIZATION)
                .then()
                .statusCode(SC_OK)
                .extract()
                .response();
        JsonPath json2 = response2.jsonPath();
        Assertions.assertThat(json2.getString("displayName")).isEqualTo("Trello Organization2");
        Assertions.assertThat(json2.getString("name")).contains(fakeName);
        orgIds.add(json2.getString("id"));
           }

           @AfterAll
           public static void deleteOrganizations() {
               for (String org : orgIds) {
                   deleteOrganization(org, reqSpec, BASE_URL, ORGANIZATION);
               }
           }
}
