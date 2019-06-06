import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.json.simple.JSONObject;
import org.junit.Test;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

public class TestPets extends TestBase{

    public JSONObject petBodyJson(){
        JSONObject body = new JSONObject();
        body.put("name", FAKER.name().name());
        body.put("age", FAKER.number().numberBetween(1,10));
        body.put("userId", "1");
        return body;
    }

    private int createPet(JSONObject petBody){
        String body = petBody.toJSONString();
        Response createNewPet = given().body(body).contentType(ContentType.JSON).post("/pets");
        createNewPet.then().statusCode(201);
        return createNewPet.jsonPath().getInt("id");
    }

    private Response getPetWithId(int petId){
        return given().get("/pets/"+ petId);
    }

    private Response deletePetWithId(int petId) {
        return given().delete("/pets/" + petId);
    }

    private int updatePet(JSONObject petBody, int petId){
        String body = petBody.toJSONString();
        Response createPetResponse  = given().body(body)
                .contentType(ContentType.JSON).put("/pets/" + petId);
        createPetResponse.then().statusCode(200);
        return createPetResponse.jsonPath().getInt("id");
    }

    @Test
    public void shouldReturnOkCodeForFirstPet(){
        Response firstPetResponse = given().get("/pets/1");
        System.out.println(firstPetResponse.getStatusCode());
        System.out.println(firstPetResponse.asString());

        firstPetResponse.then()
                .statusCode(200)
                .body("name", equalTo("Azor"))
                .body("age", equalTo(2));
    }

    @Test
    public void shouldCreateNewPet(){
        JSONObject newPet = this.petBodyJson();
        String body = newPet.toJSONString();
        System.out.println("Created new Pet body contains: "+ newPet);
        //String petName = (String) newPet.get("name");
        //String body = newPet.toJSONString();

        Response createNewPet = given().body(body).contentType(ContentType.JSON).post("/pets");
        createNewPet.then().statusCode(201);

        int createdId = createNewPet.jsonPath().getInt("id");
        System.out.println("created ID for this new Pet is: " +createdId);

        Response petWithIdResponse = given().get("/pets/" + createdId);
        System.out.println(petWithIdResponse.asString());

        petWithIdResponse.then()
                .body("name", equalTo(newPet.get("name")))
                .body("age", equalTo(newPet.get("age")));
    }

    @Test
    public void shouldDeletePet(){
        JSONObject newPet = this.petBodyJson();
        int createdId = this.createPet(newPet);

        Response deletePetWithIdResponse = deletePetWithId(createdId);
        deletePetWithIdResponse.then().statusCode(200);

        Response petAfterDelete = getPetWithId(createdId);
        petAfterDelete.then().body("isEmpty()", is(true));
    }

    @Test
    public void shouldUpdatePet(){
        JSONObject oldPet = this.petBodyJson();
        JSONObject newPet = this.petBodyJson();

        int createdID = this.createPet(oldPet);
        updatePet(newPet, createdID);

        Response petAfterUpdate = getPetWithId(createdID);
        petAfterUpdate.then()
                .body("name", equalTo(newPet.get("name")))
                .body("age", equalTo(newPet.get("age")));
    }
}
