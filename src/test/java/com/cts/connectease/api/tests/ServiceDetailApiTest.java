package com.cts.connectease.api.tests;

import com.cts.connectease.api.base.ApiConstants;
import com.cts.connectease.api.base.BaseApiTest;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ServiceDetailApiTest extends BaseApiTest {

    @BeforeClass(alwaysRun = true)
    public void ensureTestSid() {
        if (testSid == null) {
            Response r = noAuth().when()
                    .get("/api/v1/listings/filter?sortType=newest&page=0&size=1")
                    .then().extract().response();
            if (r.getStatusCode() == 200) {
                List<Map<String, Object>> content = r.jsonPath().getList("content");
                if (content != null && !content.isEmpty()) {
                    testSid = String.valueOf(content.get(0).get("sid"));
                    System.out.println("   [ServiceDetailApiTest] testSid fetched: " + testSid);
                }
            }
        }
    }

    // ── CE-SERV-TC001 ─────────────────────────────────────────────────────────
    @Test(priority = 1,
          description = "CE-SERV-TC001: GET /api/services/{sid} — full detail returns 200 with all fields")
    public void getServiceDetail() {
        Assert.assertNotNull(testSid, "Precondition: testSid must be available");

        Response response = noAuth().when().get("/api/services/" + testSid)
                                    .then().extract().response();

        System.out.println("[CE-SERV-TC001] Status : " + response.getStatusCode());
        System.out.println("[CE-SERV-TC001] Body   : " + response.getBody().asString());

        Assert.assertEquals(status(response), 200,
                "Expected 200 OK for GET /api/services/{sid}");

        Assert.assertNotNull(response.jsonPath().getString("sid"),          "sid field missing");
        Assert.assertNotNull(response.jsonPath().getString("name"),         "name field missing");
        Assert.assertNotNull(response.jsonPath().get("price"),              "price field missing");
        Assert.assertNotNull(response.jsonPath().get("totalViews"),         "totalViews field missing");
        Assert.assertNotNull(response.jsonPath().getString("vendorName"),   "vendorName field missing");
        Assert.assertNotNull(response.jsonPath().getString("vendorId"),     "vendorId field missing");
        Assert.assertNotNull(response.jsonPath().getString("categoryName"), "categoryName field missing");
        Assert.assertNotNull(response.jsonPath().get("reviews"),            "reviews array missing");
        Assert.assertNotNull(response.jsonPath().get("images"),             "images array missing");

        System.out.println("✔ CE-SERV-TC001 PASSED: Full service detail returned with all required fields");
    }

    // ── CE-SERV-TC002 ─────────────────────────────────────────────────────────
    // KNOWN DEFECT: CE-DEF-004 — Returns 403 instead of 404 for invalid SID
    @Test(priority = 2,
          description = "CE-SERV-TC002: GET /api/services/{sid} — invalid SID returns 404 [DEFECT CE-DEF-004]")
    public void getServiceDetailInvalidSid() {
        Response response = noAuth().when()
                .get("/api/services/" + ApiConstants.INVALID_SID)
                .then().extract().response();

        System.out.println("[CE-SERV-TC002] Status : " + response.getStatusCode());
        System.out.println("[CE-SERV-TC002] Body   : " + response.getBody().asString());

        // DEFECT CE-DEF-004: API returns 403; expected 404
        Assert.assertEquals(status(response), 404,
                "DEFECT CE-DEF-004 — Expected 404 Not Found for invalid SID but got "
                + status(response) + ". Root cause: RuntimeException not mapped to 404");

        System.out.println("✔ CE-SERV-TC002 PASSED: Invalid SID returns 404");
    }

    // ── CE-SERV-TC003 ─────────────────────────────────────────────────────────
    @Test(priority = 3,
          description = "CE-SERV-TC003: POST /api/services/{sid}/view — view count increments by 1")
    public void incrementViewCount() {
        Assert.assertNotNull(testSid, "Precondition: testSid must be available");

        // Step 1: Record baseline totalViews
        Response baselineResponse = noAuth().when().get("/api/services/" + testSid)
                                            .then().extract().response();
        int baselineViews = baselineResponse.jsonPath().getInt("totalViews");

        // Step 2: POST /view
        Response viewResponse = noAuth().when()
                .post("/api/services/" + testSid + "/view")
                .then().extract().response();

        System.out.println("[CE-SERV-TC003] POST /view status: " + viewResponse.getStatusCode());
        Assert.assertEquals(status(viewResponse), 200,
                "Expected 200 OK for POST /view");

        // Step 3: Verify totalViews incremented
        Response afterResponse = noAuth().when().get("/api/services/" + testSid)
                                         .then().extract().response();
        int afterViews = afterResponse.jsonPath().getInt("totalViews");

        Assert.assertEquals(afterViews, baselineViews + 1,
                "totalViews should have incremented by 1 (was " + baselineViews
                + ", now " + afterViews + ")");

        System.out.println("✔ CE-SERV-TC003 PASSED: View count incremented from " + baselineViews
                + " to " + afterViews);
    }

    // ── CE-SERV-TC004 ─────────────────────────────────────────────────────────
    @Test(priority = 4,
          description = "CE-SERV-TC004: POST /api/services/{sid}/reviews — authenticated Customer review returns 200")
    public void submitReviewAuthenticated() {
        Assert.assertNotNull(testSid, "Precondition: testSid must be available");

        Map<String, Object> body = new HashMap<>();
        body.put("review", "Excellent service! Highly recommended.");
        body.put("score",  5);

        Response response = asCustomer().body(body)
                                        .when().post("/api/services/" + testSid + "/reviews")
                                        .then().extract().response();

        System.out.println("[CE-SERV-TC004] Status : " + response.getStatusCode());
        System.out.println("[CE-SERV-TC004] Body   : " + response.getBody().asString());

        Assert.assertEquals(status(response), 200,
                "Expected 200 OK for authenticated review submission");
        Assert.assertTrue(response.getBody().asString().contains("Review added successfully"),
                "Expected 'Review added successfully!' in response");

        // Attempt to retrieve the rid from the service detail (note: known mapping bug may return null rid)
        Response detailResponse = noAuth().when().get("/api/services/" + testSid)
                                          .then().extract().response();
        List<Map<String, Object>> reviews = detailResponse.jsonPath().getList("reviews");
        if (reviews != null && !reviews.isEmpty()) {
            Object rid = reviews.get(reviews.size() - 1).get("rid");
            if (rid != null) {
                testRid = String.valueOf(rid);
                System.out.println("   testRid saved: " + testRid);
            } else {
                System.out.println("   NOTE: rid is null in GET response (known Rating mapping bug)");
            }
        }

        System.out.println("✔ CE-SERV-TC004 PASSED: Review submitted successfully");
    }

    // ── CE-SERV-TC005 ─────────────────────────────────────────────────────────
    // KNOWN DEFECT: CE-DEF-005 — Returns 403 instead of 401 for unauthenticated requests
    @Test(priority = 5,
          description = "CE-SERV-TC005: POST /api/services/{sid}/reviews — unauthenticated returns 401 [DEFECT CE-DEF-005]")
    public void submitReviewUnauthenticated() {
        Assert.assertNotNull(testSid, "Precondition: testSid must be available");

        Map<String, Object> body = new HashMap<>();
        body.put("review", "Unauthorized review attempt");
        body.put("score",  3);

        Response response = noAuth().body(body)
                                    .when().post("/api/services/" + testSid + "/reviews")
                                    .then().extract().response();

        System.out.println("[CE-SERV-TC005] Status : " + response.getStatusCode());

        // DEFECT CE-DEF-005: API returns 403; expected 401
        Assert.assertEquals(status(response), 401,
                "DEFECT CE-DEF-005 — Expected 401 Unauthorized for unauthenticated review submission but got "
                + status(response) + ". Root cause: no custom AuthenticationEntryPoint in SecurityConfig");

        System.out.println("✔ CE-SERV-TC005 PASSED: Unauthenticated review submission returns 401");
    }
}
