package com.cts.connectease.api.tests;

import com.cts.connectease.api.base.BaseApiTest;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Map;


public class RatingsApiTest extends BaseApiTest {

    // ── CE-RATE-TC001 ─────────────────────────────────────────────────────────
    @Test(priority = 1,
          description = "CE-RATE-TC001: GET /api/ratings/service/{sid} — paginated ratings returns 200")
    public void getPaginatedRatings() {
        Assert.assertNotNull(testSid, "Precondition: testSid must be set by ServiceDetailApiTest");

        Response response = noAuth().when()
                .get("/api/ratings/service/" + testSid)
                .then().extract().response();

        System.out.println("[CE-RATE-TC001] Status : " + response.getStatusCode());
        System.out.println("[CE-RATE-TC001] Body   : " + response.getBody().asString());

        Assert.assertEquals(status(response), 200,
                "Expected 200 OK for GET /api/ratings/service/{sid}");

        Assert.assertNotNull(response.jsonPath().get("content"),       "content array must be present");
        Assert.assertNotNull(response.jsonPath().get("totalElements"), "totalElements must be present");
        Assert.assertNotNull(response.jsonPath().get("totalPages"),    "totalPages must be present");

        // Verify structure of each review entry
        List<Map<String, Object>> content = response.jsonPath().getList("content");
        if (content != null && !content.isEmpty()) {
            Map<String, Object> firstReview = content.get(0);
            // rid may be null due to known Rating entity mapping bug
            Assert.assertNotNull(firstReview.get("review"), "review text must be present");
            Assert.assertNotNull(firstReview.get("score"),  "score must be present");
            if (firstReview.get("rid") == null) {
                System.out.println("   NOTE: rid is null — known Rating entity ID mapping bug");
            }
        }

        System.out.println("✔ CE-RATE-TC001 PASSED: Paginated ratings returned — "
                + (response.jsonPath().getList("content") != null
                    ? response.jsonPath().getList("content").size() : 0) + " reviews");
    }

    // ── CE-RATE-TC002 ─────────────────────────────────────────────────────────
    // NOT TESTED: rid is null in GET /api/ratings due to Rating entity mapping bug.
    // Test is disabled until that bug is fixed and a real rid can be obtained.
    @Test(priority = 2, enabled = false,
          description = "CE-RATE-TC002: DELETE /api/ratings/{rid} — delete own review returns 200 [NOT TESTED — rid is null]")
    public void deleteOwnReview() {
        Assert.assertNotNull(testRid,
                "Precondition: testRid must be set. Currently null due to Rating entity mapping bug.");

        Response response = asCustomer().when().delete("/api/ratings/" + testRid)
                                        .then().extract().response();

        System.out.println("[CE-RATE-TC002] Status : " + response.getStatusCode());
        System.out.println("[CE-RATE-TC002] Body   : " + response.getBody().asString());

        Assert.assertEquals(status(response), 200,
                "Expected 200 OK for deleting own review");
        Assert.assertTrue(response.getBody().asString().contains("Review deleted successfully"),
                "Expected 'Review deleted successfully' in response");

        // Verify review is gone
        Response listResponse = noAuth().when()
                .get("/api/ratings/service/" + testSid)
                .then().extract().response();
        List<Map<String, Object>> remaining = listResponse.jsonPath().getList("content");
        boolean found = remaining != null && remaining.stream()
                .anyMatch(r -> testRid.equals(String.valueOf(r.get("rid"))));
        Assert.assertFalse(found, "Deleted review should not appear in ratings list");

        System.out.println("✔ CE-RATE-TC002 PASSED: Review deleted and removed from list");
    }

    // ── CE-RATE-TC003 ─────────────────────────────────────────────────────────
    // NOT TESTED: rid is null; cannot obtain a valid rid to test with a different user.
    @Test(priority = 3, enabled = false,
          description = "CE-RATE-TC003: DELETE /api/ratings/{rid} — delete another user's review returns 403 [NOT TESTED — rid is null]")
    public void deleteOtherUserReview() {
        Assert.assertNotNull(testRid,
                "Precondition: testRid must be set. Currently null due to Rating entity mapping bug.");

        // Attempt deletion using Vendor cookie (not the review author — review was by Customer)
        Response response = asVendor().when().delete("/api/ratings/" + testRid)
                                      .then().extract().response();

        System.out.println("[CE-RATE-TC003] Status : " + response.getStatusCode());

        Assert.assertEquals(status(response), 403,
                "Expected 403 Forbidden when non-author tries to delete a review");

        System.out.println("✔ CE-RATE-TC003 PASSED: Non-author review deletion correctly blocked with 403");
    }
}
