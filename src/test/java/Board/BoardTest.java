package Board;

import base.BaseTest;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import java.util.List;
import static io.restassured.RestAssured.given;

public class BoardTest extends BaseTest {

    @Test
    public void createNewBoard(){
        Response response = given()
                .spec(reqSpec)
                .queryParam("name", "First Board")
                .when()
                .post(BASE_URL + BOARDS)
                .then()
                .statusCode(200)
                .extract()
                .response();
        JsonPath json = response.jsonPath();
        Assertions.assertThat(json.getString("name")).isEqualTo("First Board");

        String boardId = json.get("id");

        given()
                .spec(reqSpec)
                .when()
                .delete(BASE_URL + BOARDS + "/" + boardId)
                .then()
                .statusCode(200);

    }
    @Test
    public void createBoardWithEmptyBoardName(){
        given()
                .spec(reqSpec)
                .queryParam("name" ,"")
                .when()
                .post(BASE_URL + BOARDS)
                .then()
                .statusCode(400)
                .extract()
                .response();
    }
    @Test
    public void createBoardWithoutDefaultList(){
        Response response = given()
                .spec(reqSpec)
                .queryParam("name" ,"Board without default list")
                .queryParam("defaultLists", false)
                .when()
                .post(BASE_URL + BOARDS)
                .then()
                .statusCode(200)
                .extract()
                .response();

        JsonPath json = response.jsonPath();
        Assertions.assertThat(json.getString("name")).isEqualTo("Board without default list");

        String boardId = json.get("id");

        Response responseGet = given()
                .spec(reqSpec)
                .when()
                .get(BASE_URL + BOARDS + "/" + boardId + "/" + LISTS)
                .then()
                .statusCode(200)
                .extract()
                .response();

        JsonPath jsonGet = responseGet.jsonPath();
        List<String> idList = jsonGet.getList("id");
        Assertions.assertThat(idList).hasSize(0);


        given()
               .spec(reqSpec)
                .when()
                .delete(BASE_URL + BOARDS + "/" + boardId)
                .then()
                .statusCode(200);
    }

    @Test
    public void createBoardWithDefaultList(){
        Response response = given()
                .spec(reqSpec)
                .queryParam("name" ,"Board with default list")
                .queryParam("defaultLists", true)
                .when()
                .post(BASE_URL + BOARDS)
                .then()
                .statusCode(200)
                .extract()
                .response();


        JsonPath json = response.jsonPath();
        Assertions.assertThat(json.getString("name")).isEqualTo("Board with default list");

        String boardId = json.get("id");
        Response responseGet = given()
               .spec(reqSpec)
                .when()
                .get(BASE_URL + BOARDS +  "/" + boardId + "/" + LISTS)
                .then()
                .statusCode(200)
                .extract()
                .response();

        JsonPath jsonGet = responseGet.jsonPath();
        List<String> nameList = jsonGet.getList("name");

        Assertions.assertThat(nameList).hasSize(3)
                .contains("To Do", "Doing", "Done");

        given()
                .spec(reqSpec)
                .when()
                .delete(BASE_URL + BOARDS + "/" + boardId)
                .then()
                .statusCode(200);
    }
}
