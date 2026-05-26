package com.cts.connectease.api.tests;

import com.cts.connectease.api.base.BaseApiTest;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Map;


public class ServiceListingsApiTest extends BaseApiTest {

    // ── CE-LIST-TC001 ─────────────────────────────────────────────────────────
    @Test(priority = 1,
          description = "CE-LIST-TC001: GET /api/v1/listings/filter — default params returns 200 paginated list")
    public void filterDefault() {
        Response response = noAuth().when()
                .get("/api/v1/listings/filter?sortType=newest&page=0&size=10")
                .then().extract().response();

        System.out.println("[CE-LIST-TC001] Status : " + response.getStatusCode());
        System.out.println("[CE-LIST-TC001] Body   : " + response.getBody().asString());

        Assert.assertEquals(status(response), 200,
                "Expected 200 OK for default listing filter");

        Assert.assertNotNull(response.jsonPath().get("content"),      "content array must be present");
        Assert.assertNotNull(response.jsonPath().get("totalElements"), "totalElements must be present");
        Assert.assertNotNull(response.jsonPath().get("totalPages"),    "totalPages must be present");

        // Save first sid for service-detail tests if testSid not already set
        List<Map<String, Object>> content = response.jsonPath().getList("content");
        if (content != null && !content.isEmpty() && testSid == null) {
            testSid = String.valueOf(content.get(0).get("sid"));
            System.out.println("   testSid saved from listings: " + testSid);
        }

        System.out.println("✔ CE-LIST-TC001 PASSED: Paginated listing returned successfully");
    }

    // ── CE-LIST-TC002 ─────────────────────────────────────────────────────────
    @Test(priority = 2,
          description = "CE-LIST-TC002: GET /api/v1/listings/filter — city=Chennai returns only Chennai services")
    public void filterByCity() {
        // Using Chennai (confirmed to have active services from actual test results)
        Response response = noAuth().when()
                .get("/api/v1/listings/filter?city=Chennai")
                .then().extract().response();

        System.out.println("[CE-LIST-TC002] Status : " + response.getStatusCode());
        System.out.println("[CE-LIST-TC002] Body   : " + response.getBody().asString());

        Assert.assertEquals(status(response), 200,
                "Expected 200 OK for city filter");

        List<Map<String, Object>> content = response.jsonPath().getList("content");
        Assert.assertNotNull(content, "content array must be present");

        // Verify all returned items belong to Chennai
        for (Map<String, Object> item : content) {
            Assert.assertEquals(item.get("city"), "Chennai",
                    "All filtered results must have city=Chennai, but found: " + item.get("city"));
        }

        System.out.println("✔ CE-LIST-TC002 PASSED: City filter works — " + content.size()
                + " Chennai results");
    }

    // ── CE-LIST-TC003 ─────────────────────────────────────────────────────────
    @Test(priority = 3,
          description = "CE-LIST-TC003: GET /api/v1/listings/filter — sortType=price_asc returns ascending prices")
    public void filterSortPriceAsc() {
        Response response = noAuth().when()
                .get("/api/v1/listings/filter?sortType=price_asc")
                .then().extract().response();

        System.out.println("[CE-LIST-TC003] Status : " + response.getStatusCode());

        Assert.assertEquals(status(response), 200,
                "Expected 200 OK for price_asc sort");

        List<Map<String, Object>> content = response.jsonPath().getList("content");
        Assert.assertNotNull(content, "content array must be present");

        // Verify ascending price order
        if (content.size() > 1) {
            for (int i = 0; i < content.size() - 1; i++) {
                double price1 = ((Number) content.get(i).get("price")).doubleValue();
                double price2 = ((Number) content.get(i + 1).get("price")).doubleValue();
                Assert.assertTrue(price1 <= price2,
                        "Prices must be in ascending order: " + price1 + " > " + price2
                        + " at index " + i);
            }
        }

        System.out.println("✔ CE-LIST-TC003 PASSED: Results returned in ascending price order");
    }

    // ── CE-LIST-TC004 ─────────────────────────────────────────────────────────
    @Test(priority = 4,
          description = "CE-LIST-TC004: GET /api/v1/listings/filter — combined filters return correctly filtered results")
    public void filterCombined() {
        Response response = noAuth().when()
                .get("/api/v1/listings/filter?city=Chennai&maxPrice=5000&minRating=3&sortType=price_asc")
                .then().extract().response();

        System.out.println("[CE-LIST-TC004] Status : " + response.getStatusCode());
        System.out.println("[CE-LIST-TC004] Body   : " + response.getBody().asString());

        Assert.assertEquals(status(response), 200,
                "Expected 200 OK for combined filters");

        List<Map<String, Object>> content = response.jsonPath().getList("content");
        Assert.assertNotNull(content, "content array must be present");

        for (Map<String, Object> item : content) {
            // City filter
            Assert.assertEquals(item.get("city"), "Chennai",
                    "All results must be from Chennai");
            // Price filter
            double price = ((Number) item.get("price")).doubleValue();
            Assert.assertTrue(price <= 5000,
                    "Price " + price + " exceeds maxPrice=5000");
            // Rating filter
            if (item.get("averageRating") != null) {
                double rating = ((Number) item.get("averageRating")).doubleValue();
                Assert.assertTrue(rating >= 3.0,
                        "averageRating " + rating + " is below minRating=3");
            }
        }

        System.out.println("✔ CE-LIST-TC004 PASSED: Combined filters applied correctly");
    }

    // ── CE-LIST-TC005 ─────────────────────────────────────────────────────────
    @Test(priority = 5,
          description = "CE-LIST-TC005: GET /api/services — paginated all services returns 200")
    public void getAllServicesPaginated() {
        Response response = noAuth().when()
                .get("/api/services?page=0&size=10")
                .then().extract().response();

        System.out.println("[CE-LIST-TC005] Status : " + response.getStatusCode());

        Assert.assertEquals(status(response), 200,
                "Expected 200 OK for GET /api/services");

        Assert.assertNotNull(response.jsonPath().get("content"),      "content array must be present");
        Assert.assertNotNull(response.jsonPath().get("totalElements"), "totalElements must be present");
        Assert.assertNotNull(response.jsonPath().get("totalPages"),    "totalPages must be present");

        System.out.println("✔ CE-LIST-TC005 PASSED: Paginated services list returned");
    }

    // ── CE-LIST-TC006 ─────────────────────────────────────────────────────────
    @Test(priority = 6,
          description = "CE-LIST-TC006: GET /api/services/vendor/{vendorId} — vendor's public listings")
    public void getVendorPublicListings() {
        Assert.assertNotNull(vendorUid, "Precondition: vendorUid must be set by AuthApiTest");

        Response response = noAuth().when()
                .get("/api/services/vendor/" + vendorUid)
                .then().extract().response();

        System.out.println("[CE-LIST-TC006] Status : " + response.getStatusCode());
        System.out.println("[CE-LIST-TC006] Body   : " + response.getBody().asString());

        Assert.assertEquals(status(response), 200,
                "Expected 200 OK for vendor public listings");

        // Response is an array (may be empty for newly created vendor)
        Assert.assertNotNull(response.jsonPath().get("$"),
                "Response body must be a non-null array");

        System.out.println("✔ CE-LIST-TC006 PASSED: Vendor public listings fetched");
    }
}
