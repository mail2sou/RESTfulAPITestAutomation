package com.restApi.stepDefinitions;

import com.restApi.util.Utilities;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Given;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.testng.Assert;

import java.lang.reflect.Field;
import java.util.*;

public class UserDefinedStepDefinitions {

    Utilities utilities = new Utilities();
    public static RequestSpecification requestSpec;
    public static int orderId;
    public static int petId;
    public static String userName;
    public static Response response;
    public static String requestBody;
    public static ArrayList<Response> responseMapList = new ArrayList<Response>();
    public static ArrayList<HashMap<Object, Object>> requestMapList = new ArrayList<HashMap<Object, Object>>();
    protected static final Logger log = LogManager.getLogger();


    @Given("^I Perform get operation for (login|logout) url$")
    public void performGetOperation(String url, DataTable params) throws Throwable {
        url = "/user/" + url;
        Response response;
        List<Map<String, String>> data = params.asMaps(String.class, String.class);
        Map<String, String> dataMap = data.get(0);
        System.out.println(dataMap);
        if (url.contains("logout")) {
            response = requestSpec.get(url);
        } else {
            response = getWithQueryParams(url, dataMap);
        }
    }


    @Given("^I create a new (store order|single user|multiple user|pet) with url \"([^\"]*)\" and (.*) variable$")
    public void performCreateOperation(String creationType, String url, String variableName, DataTable table) throws Throwable {
        Response response;
        this.requestBody = "";
        List<Map<String, String>> createStoreMap = table.transpose().asMaps();
        if (!creationType.equalsIgnoreCase("pet")) {
            HashMap<Object, Object> updatedDataMap = new HashMap<Object, Object>();
            ArrayList<HashMap<Object, Object>> dataMapList = new ArrayList<HashMap<Object, Object>>();
            for (Map<String, String> map : createStoreMap) {
                updatedDataMap = utilities.generateDataFromHashMap(map);
                dataMapList.add(updatedDataMap);

            }

            if (dataMapList.size() > 1) {
                List<JSONObject> jsonObj = new ArrayList<JSONObject>();

                for (HashMap<Object, Object> data : dataMapList) {
                    JSONObject obj = new JSONObject(data);
                    jsonObj.add(obj);
                    requestMapList.add(data);
                }

                this.requestBody = jsonObj.toString();
            } else {
                JSONObject json = new JSONObject(updatedDataMap);
                this.requestBody = json.toString();
            }
        } else {
            HashMap<Object, Object> updatedDataMap = new HashMap<Object, Object>();
            updatedDataMap = utilities.generateDataFromHashMap(createStoreMap.get(0));
            String requestBody = "{\r\n" +
                    "  \"id\": " + updatedDataMap.get("id") + ",\r\n" +
                    "  \"name\": \"" + updatedDataMap.get("name") + "\",\r\n" +
                    "  \"category\": {\r\n" +
                    "    \"id\": " + updatedDataMap.get("category_id") + ",\r\n" +
                    "    \"name\": \"" + updatedDataMap.get("category_name") + "\"\r\n" +
                    "  },\r\n" +
                    "  \"photoUrls\": [\r\n" +
                    "    \"" + updatedDataMap.get("photoUrls") + "\"\r\n" +
                    "  ],\r\n" +
                    "  \"tags\": [\r\n" +
                    "    {\r\n" +
                    "      \"id\": " + updatedDataMap.get("tags_id") + ",\r\n" +
                    "      \"name\": \"" + updatedDataMap.get("tags_name") + "\"\r\n" +
                    "    }\r\n" +
                    "  ],\r\n" +
                    "  \"status\": \"" + updatedDataMap.get("status") + "\"\r\n" +
                    "}";

            this.requestBody = requestBody;
        }
        response = postOpsWithBodyParams(url, this.requestBody);
        System.out.println(response.asPrettyString());
        Field field = getClass().getDeclaredField(variableName);
        if (creationType.contains("user")) {
            field.set(this, response.jsonPath().getString("username"));
        } else {
            field.setInt(this, response.jsonPath().getInt("id"));
        }
        field.setAccessible(true);
    }

    //I Perform get operation to fetch order details for url
    @Given("^I perform (get|delete) operation to (fetch|delete) (order|user|pet) details for url \"([^\"]*)\" with (.*) variable$")
    public void getOrderDetails(String operation, String action, String type, String url, String variableName) throws Throwable {
        Response response;
        Object field = getClass().getDeclaredField(variableName).get(variableName);
        if (field.toString().contains(",")) {
            String text = field.toString().replaceAll("\\[", "");
            text = text.replaceAll("\\]", "");
            ArrayList<String> variableNamesList = new ArrayList<String>(Arrays.asList(text.split(",")));
            for (String varName : variableNamesList) {
                String updatedUrl = "";
                updatedUrl = url + varName.trim();
                this.response = requestSpec.get(updatedUrl);
                responseMapList.add(this.response);
            }
        } else {
            url = url + String.valueOf(field);
            buildRequest();
            if (operation.contains("delete")) {
                if (type.equalsIgnoreCase("pet")) {
                    requestSpec.header("api_key", "test");
                }
                this.response = requestSpec.delete(url);
            } else {

                this.response = requestSpec.get(url);
            }
        }

    }

    @Given("^I verify response (details|details list)$")
    public void verifyResponseData(String type) throws Throwable {
        JSONParser parser = new JSONParser();
        JSONObject requestjson = null;
        JSONObject responsejson = null;
        if (type.equalsIgnoreCase("details list")) {
            int i = 0;
            for (Response map : responseMapList) {
                responsejson = (JSONObject) parser.parse(map.asPrettyString());
                for (Object key : responsejson.keySet()) {
                    String expected = requestMapList.get(i).get(key).toString();
                    String actual = responsejson.get(key).toString();
                    Assert.assertTrue(actual.equalsIgnoreCase(expected), "Expected Response value for " + key + " is: " + expected + " But Actual is: " + actual);
                }
                i++;
            }
        } else {
            requestjson = (JSONObject) parser.parse(this.requestBody);
            responsejson = (JSONObject) parser.parse(this.response.asPrettyString());

            for (Object key : responsejson.keySet()) {
                String expected = String.valueOf(requestjson.get(key));
                String actual = responsejson.get(key).toString();
                if (key.toString().contains("Date")) {
                    Assert.assertTrue(actual.contains(expected), "Expected Response value for " + key + " is: " + expected + " But Actual is: " + actual);
                } else {
                    Assert.assertTrue(actual.equalsIgnoreCase(expected), "Expected Response value for " + key + " is: " + expected + " But Actual is: " + actual);
                }
            }
        }

    }

    @Given("^I perform get operation to fetch (order|pet) details for url \"([^\"]*)\" with (orderId|tags|status|petId) value (.*)$")
    public void getOrderDetailsById(String type, String url, String attribute, String variableName) throws Throwable {
        Response response;
        buildRequest();
        if (url.contains("findBy")) {
            this.requestSpec.queryParam(attribute, variableName);
        } else {
            url = url + variableName;
        }
        this.response = this.requestSpec.get(url);
    }

    @Given("^I perform get operation to fetch inventory details for url \"([^\"]*)\"$")
    public void getInventoryDetailsById(String url) throws Throwable {
        buildRequest();
        this.response = requestSpec.get(url);
    }

    @Given("^I verify response has (.*) with status code (.*)$")
    public void verifyResponseValues(String message, int code) throws Throwable {

        Assert.assertTrue(this.response.getStatusCode() == code, "Expected status code: " + code + " Actual: " + this.response.getStatusCode());
        if (!message.isEmpty()) {
            message = message.replaceAll("\"", "");
            Assert.assertTrue(this.response.asPrettyString().contains(message), "Expected Error Message: " + message + " Actual: " + this.response.asPrettyString());

        }
    }

    @Given("^I verify response with status code (.*)$")
    public void verifyResponseValues(int code) throws Throwable {
        Assert.assertTrue(this.response.getStatusCode() == code, "Expected status code: " + code + " Actual: " + this.response.getStatusCode());

    }

    @Given("^I verify response received has (.*)$")
    public void verifyResponseValues(String messageList) throws Throwable {
        messageList = messageList.replaceAll("\"", "");
        for (String message : messageList.split("\\|")) {
            Assert.assertTrue(this.response.asPrettyString().contains(message), "Expected Error Message: " + message + " Actual: " + this.response.asPrettyString());

        }
    }


    @Given("^I perform put operation for url \"([^\"]*)\" with (.*) variable updating (.*) as (.*)$")
    public void performUpdateUserOperation(String url, String userName, String identifier, String value) throws Throwable {
        Response response;
        Object field = getClass().getDeclaredField(userName).get(userName);
        if (!url.contains("pet")) {
            url = url + String.valueOf(field);
        }
        JSONParser parser = new JSONParser();
        JSONObject json = (JSONObject) parser.parse(this.requestBody);
        json.put(identifier, value);
        this.requestBody = json.toString();
        response = putOpsWithBodyParams(url, this.requestBody);
    }

    @Given("^I perform post operation for url \"([^\"]*)\" with (.*) variable updating name as (.*) and status as (.*)$")
    public void performUpdateUserFormDataOperation(String url, String userName, String name, String status) throws Throwable {
        Response response;
        Object field = getClass().getDeclaredField(userName).get(userName);
        url = url + String.valueOf(field);
        HashMap<String, String> queryParamMap = new HashMap<String, String>();
        queryParamMap.put("name", name);
        queryParamMap.put("status", status);
        JSONParser parser = new JSONParser();
        JSONObject requestjson = (JSONObject) parser.parse(this.requestBody);
        requestjson.put("name", name);
        requestjson.put("status", status);
        this.requestBody = requestjson.toString();
        response = postWithQueryParams(url, queryParamMap);
    }

    public static void buildRequest() {
        RequestSpecBuilder builder = new RequestSpecBuilder();
        builder.setBaseUri("http://localhost:8080/api/v3");
        requestSpec = builder.build();
        requestSpec = RestAssured.given().spec(requestSpec);
    }

    public static Response postOpsWithBodyParams(String url, String body) {
        buildRequest();
        requestSpec.contentType(ContentType.JSON);
        requestSpec.body(body);
        return requestSpec.post(url);
    }

    public static Response getWithQueryParams(String url, Map<String, String> pathParams) {
        buildRequest();
        requestSpec.queryParams(pathParams);
        return requestSpec.get(url);
    }

    public static Response putOpsWithBodyParams(String url, String body) {
        requestSpec.body(body);
        return requestSpec.put(url);
    }

    public static Response postWithQueryParams(String url, Map<String, String> pathParams) {
        requestSpec.queryParams(pathParams);
        return requestSpec.post(url);
    }

}
