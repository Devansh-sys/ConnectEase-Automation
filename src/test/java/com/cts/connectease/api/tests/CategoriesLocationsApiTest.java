package com.cts.connectease.api.tests;

import com.cts.connectease.api.base.BaseApiTest;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.List;

public class CategoriesLocationsApiTest extends BaseApiTest {

    // ── CE-CAT-TC001 ─────────────────────────────────────────────────────────
    @Test(priority = 1,
          description = "CE-CAT-TC001: GET /api/categories — returns 200 with array of cid+name objects")
    public void getAllCategories() {
        Response response = noAuth().when().get("/api/categories")
                                    .then().extract().response();

        System.out.println("[CE-CAT-TC001] Status : " + response.getStatusCode());
        System.out.println("[CE-CAT-TC001] Body   : " + response.getBody().asString());

        Assert.assertEquals(status(response), 200,
                "Expected 200 OK for GET /api/categories");

        List<?> categories = response.jsonPath().getList("$");
        Assert.assertNotNull(categories, "Response body should be a non-null array");
        Assert.assertFalse(categories.isEmpty(), "Categories list should not be empty");

        // Each entry must have cid and name
        Assert.assertNotNull(response.jsonPath().getString("[0].cid"),  "First category must have cid");
        Assert.assertNotNull(response.jsonPath().getString("[0].name"), "First category must have name");

        // Save first cid for service-creation tests
        testCategoryId = response.jsonPath().getString("[0].cid");
        System.out.println("   testCategoryId saved: " + testCategoryId);

        System.out.println("✔ CE-CAT-TC001 PASSED: " + categories.size() + " categories returned");
    }

    // ── CE-CAT-TC002 ─────────────────────────────────────────────────────────
    @Test(priority = 2,
          description = "CE-CAT-TC002: GET /api/locations/cities — returns 200 with active city names")
    public void getActiveCities() {
        Response response = noAuth().when().get("/api/locations/cities")
                                    .then().extract().response();

        System.out.println("[CE-CAT-TC002] Status : " + response.getStatusCode());
        System.out.println("[CE-CAT-TC002] Body   : " + response.getBody().asString());

        Assert.assertEquals(status(response), 200,
                "Expected 200 OK for GET /api/locations/cities");

        List<String> cities = response.jsonPath().getList("$");
        Assert.assertNotNull(cities, "Cities list should not be null");
        Assert.assertFalse(cities.isEmpty(),
                "At least one city with an active service must be returned");

        System.out.println("✔ CE-CAT-TC002 PASSED: Cities returned — " + cities);
    }

    // ── CE-CAT-TC003 ─────────────────────────────────────────────────────────
    @Test(priority = 3,
          description = "CE-CAT-TC003: GET /api/locations/cities/{city}/areas — returns 200 with area names")
    public void getAreasForCity() {
        // Use the city that has active services (confirmed from actual results: Chennai)
        String city = "Chennai";

        Response response = noAuth().when().get("/api/locations/cities/" + city + "/areas")
                                    .then().extract().response();

        System.out.println("[CE-CAT-TC003] Status : " + response.getStatusCode());
        System.out.println("[CE-CAT-TC003] Body   : " + response.getBody().asString());

        Assert.assertEquals(status(response), 200,
                "Expected 200 OK for GET /api/locations/cities/" + city + "/areas");

        List<String> areas = response.jsonPath().getList("$");
        Assert.assertNotNull(areas, "Areas list should not be null");
        Assert.assertFalse(areas.isEmpty(),
                "At least one area should be returned for " + city);

        System.out.println("✔ CE-CAT-TC003 PASSED: " + areas.size() + " areas returned for " + city);
    }
}
