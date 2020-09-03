package e2e;

import base.BaseTest;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.*;

import static io.restassured.RestAssured.given;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class MoveCardBetweenLists extends BaseTest {

    private static String boardId;
    private static String firstListId;
    private static String secondListId;
    private static String cardId;

    @Test
    @Order(1)
    public void createNewBoard() {
        Response response = given()
                .spec(reqSpec)
                .queryParam("name", "E2E Board")
                .queryParam("defaultLists", false)
                .when()
                .post(BASE_URL + BOARDS)
                .then()
                .statusCode(200)
                .extract()
                .response();
        JsonPath json = response.jsonPath();
        Assertions.assertThat(json.getString("name")).isEqualTo("E2E Board");
        boardId = json.getString("id");

    }

    @Test
    @Order(2)
    public void createFirstList(){
        Response response = given()
                .spec(reqSpec)
                .queryParam("name", "First List")
                .queryParam("idBoard", boardId)
                .when()
                .post(BASE_URL+ LISTS)
                .then()
                .statusCode(200)
                .extract()
                .response();

        JsonPath json = response.jsonPath();
        Assertions.assertThat(json.getString("name")).isEqualTo("First List");
        firstListId = json.getString("id");
    }

    @Test
    @Order(3)
    public void createSecondList(){
        Response response = given()
                .spec(reqSpec)
                .queryParam("name", "Second List")
                .queryParam("idBoard", boardId)
                .contentType(ContentType.JSON)
                .when()
                .post(BASE_URL+ LISTS)
                .then()
                .statusCode(200)
                .extract()
                .response();

        JsonPath json = response.jsonPath();
        Assertions.assertThat(json.getString("name")).isEqualTo("Second List");
        secondListId = json.getString("id");
    }

    @Test
    @Order(4)
    public void addCardToFirstList(){
        Response response = given()
                .spec(reqSpec)
                .queryParam("name", "First Card")
                .queryParam("idList", firstListId)
                .when()
                .post(BASE_URL + CARDS)
                .then()
                .statusCode(200)
                .extract()
                .response();

        JsonPath json = response.jsonPath();
        Assertions.assertThat(json.getString("name")).isEqualTo("First Card");
        cardId = json.getString("id");
    }

    @Test
    @Order(5)
    public void moveCardToSecondList(){
        Response response = given()
                .spec(reqSpec)
                .queryParam("idList", secondListId)
                .when()
                .put(BASE_URL + CARDS + "/" + cardId)
                .then()
                .statusCode(200)
                .extract()
                .response();
    }
    @Test
    @Order(6)
    public void deleteBoard(){
        given()
                .spec(reqSpec)
                .when()
                .delete(BASE_URL + BOARDS + "/" + boardId)
                .then()
                .statusCode(200);
    }
}
