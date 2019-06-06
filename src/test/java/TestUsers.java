import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.json.simple.JSONObject;
import org.junit.Test;
import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.equalTo;

public class TestUsers extends TestBase{

    public JSONObject userBodyJson(){
        JSONObject body = new JSONObject();
        body.put("firstName", FAKER.name().firstName());
        body.put("lastName", FAKER.name().lastName());
        body.put("age", FAKER.number().numberBetween(18,99));
        return body;
    }


    private int createUser(JSONObject userBody){
        String body = userBody.toJSONString();
        Response createNewUser = given().body(body).contentType(ContentType.JSON).post("/users");  //tworzenie nowego użytkownika o powyższych danych
        createNewUser.then().statusCode(201);
        return createNewUser.jsonPath().getInt("id");
    }

    private Response getUserWithId(int userId){
        return given().get("/users/"+ userId);
    }

    private Response deleteUserWithId(int userId) {
        return given().delete("/users/" + userId);
    }

    private int updateUser(JSONObject userBody, int userId){
        String body = userBody.toJSONString();

        Response createUserResponse  = given().body(body)
                .contentType(ContentType.JSON).put("/users/" + userId);
        createUserResponse.then().statusCode(200);

        return createUserResponse.jsonPath().getInt("id");
    }



    @Test
    public void shouldReturnOkCodeForListOfAllUsers(){

        Response allUsersResponse = given().get("/users");
        System.out.println(allUsersResponse.getStatusCode());
        System.out.println(allUsersResponse.asString());

        allUsersResponse.then().statusCode(200);
    }
    @Test
    public void shouldReturnOkCodeForFirstUser(){
        Response firstUserResponse = given().get("/users/1");
        System.out.println(firstUserResponse.getStatusCode());
        System.out.println(firstUserResponse.asString());
        firstUserResponse.then().statusCode(200);

        firstUserResponse.then().body("firstName", equalTo("Kasia"));
        firstUserResponse.then().body("lastName", equalTo("Słomka"));

        firstUserResponse.then()   // inna wersja w podejściu Fluent Way
                .statusCode(200)
                .body("firstName", equalTo("Kasia"))
                .body("lastName", equalTo("Słomka"));
    }


    @Test
    public void shouldCreateNewUser() {
        JSONObject newUser = this.userBodyJson(); //generujemy object newPet jako nową zmienną
        String body = newUser.toJSONString();
        System.out.println("Created new User body contains: " + newUser);

        Response createNewUser = given().body(body).contentType(ContentType.JSON).post("/users");  //tworzenie nowego użytkownika o powyższych danych
        createNewUser.then().statusCode(201);

        int createdId = createNewUser.jsonPath().getInt("id"); //bz odpowiedzi wyciągam Id
        System.out.println("created ID for this new User is: " + createdId);

        Response userWithIdResponse = given().get("/users/" + createdId);
        System.out.println(userWithIdResponse.asString());

        userWithIdResponse.then()
                .body("firstName", equalTo(newUser.get("firstName"))) //upewniam się czy w polu name znajduje się to co wygenerowalismy w linii 32
                .body("lastName", equalTo(newUser.get("lastName")))
                .body("age", equalTo(newUser.get("age")));
    }

    @Test
    public void shouldDeleteUser(){
        JSONObject newUser = this.userBodyJson(); //generujemy object newPet jako nową zmienną
        int createdID = this.createUser(newUser);
        System.out.println("Created user with Body:"+newUser);
        Response deleteUserWithIdResponse = deleteUserWithId(createdID);
        System.out.println("User Body after delete: "+deleteUserWithIdResponse.asString());
        deleteUserWithIdResponse.then().statusCode(200);

        Response userAfterDeleteId = getUserWithId(createdID);
        userAfterDeleteId.then().body("isEmpty()", is(true));
    }

    @Test
    public void shouldUpdateUser(){
        JSONObject oldUser = this.userBodyJson(); //generujemy object newPet jako nową zmienną
        JSONObject newUser = this.userBodyJson();

        int createdID = this.createUser(oldUser);
        updateUser(newUser, createdID);
        System.out.println("Old User:"+oldUser);
        System.out.println("New User:"+newUser);
        Response userAfterUpdate = getUserWithId(createdID);
        userAfterUpdate.then()
                .body("firstName", equalTo(newUser.get("firstName")))
                .body("lastName", equalTo(newUser.get("lastName")))
                .body("age", equalTo(newUser.get("age")));
    }
}
