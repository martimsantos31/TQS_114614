package pt.ua.deti.tqs;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

public class ToDoApiTest {

    @BeforeAll
    public static void setup() {
        RestAssured.baseURI = "https://jsonplaceholder.typicode.com";
    }

    @Test
    public void testGetAllTodos_StatusCode() {
        given()
                .when()
                .get("/todos")
                .then()
                .statusCode(200);
    }

    @Test
    public void testGetTodo4_Title() {
        given()
                .when()
                .get("/todos/4")
                .then()
                .statusCode(200)
                .body("title", equalTo("et porro tempora"));
    }

    @Test
    public void testGetAllTodos_ContainsIds() {
        given()
                .when()
                .get("/todos")
                .then()
                .statusCode(200)
                .body("id", hasItems(198, 199));
    }

    @Test
    public void testGetAllTodos_ResponseTime() {
        given()
                .when()
                .get("/todos")
                .then()
                .time(lessThan(2000L));
    }
}