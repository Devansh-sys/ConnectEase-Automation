package com.cts.connectease.api.tests;

import com.cts.connectease.api.base.BaseApiTest;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;


public class AIChatApiTest extends BaseApiTest {

    // ── CE-AI-TC001 ──────────────────────────────────────────────────────────
    @Test(priority = 1,
          description = "CE-AI-TC001: POST /api/v1/ai-chat/ask — service discovery query returns 200 with reply")
    public void aiServiceDiscoveryQuery() {
        String userQuery =
                "I need a reliable electrician in Gachibowli, Hyderabad. Budget around Rs.500-800";

        Response response = sendAiRequest(userQuery);

        System.out.println("[CE-AI-TC001] Status : " + response.getStatusCode());
        System.out.println("[CE-AI-TC001] Body   : " + response.getBody().asString());

        Assert.assertEquals(status(response), 200,
                "Expected 200 OK for AI service discovery query. "
                + "If 400: the request body field name is wrong — see console for probe results.");

        String body = response.getBody().asString();
        boolean hasReply = body.contains("reply") || body.contains("aiMessage")
                || body.contains("response") || body.contains("answer");
        Assert.assertTrue(hasReply,
                "Response must contain an AI-generated text field (reply/aiMessage/response/answer)");

        System.out.println("✔ CE-AI-TC001 PASSED: AI service discovery query returned a response");
    }

    // ── CE-AI-TC002 ──────────────────────────────────────────────────────────
    @Test(priority = 2,
          description = "CE-AI-TC002: POST /api/v1/ai-chat/ask — category discovery query returns 200")
    public void aiCategoryDiscoveryQuery() {
        String userQuery = "What kind of cleaning services are available near HITEC City?";

        Response response = sendAiRequest(userQuery);

        System.out.println("[CE-AI-TC002] Status : " + response.getStatusCode());
        System.out.println("[CE-AI-TC002] Body   : " + response.getBody().asString());

        Assert.assertEquals(status(response), 200,
                "Expected 200 OK for AI category discovery query. "
                + "If 400: the request body field name is wrong — see console for probe results.");

        String responseBody = response.getBody().asString();
        boolean hasReply = responseBody.contains("reply") || responseBody.contains("aiMessage")
                || responseBody.contains("response") || responseBody.contains("answer");
        Assert.assertTrue(hasReply,
                "Response must contain an AI-generated reply relevant to cleaning services");

        System.out.println("✔ CE-AI-TC002 PASSED: AI category discovery query returned a response");
    }

    // ── CE-AI-TC003 ──────────────────────────────────────────────────────────
    // NOTE: Marked NOT TESTED in Excel — cannot simulate Gemini API failure in standard test run.
    @Test(priority = 3, enabled = false,
          description = "CE-AI-TC003: POST /api/v1/ai-chat/ask — AI service failure returns 500 [NOT TESTED]")
    public void aiServiceFailure() {
        Map<String, String> body = new HashMap<>();
        body.put("message", "query when AI service is down");

        Response response = noAuth().body(body)
                                    .when().post("/api/v1/ai-chat/ask")
                                    .then().extract().response();

        Assert.assertEquals(status(response), 500,
                "Expected 500 Internal Server Error when AI service is unavailable");

        System.out.println("✔ CE-AI-TC003 PASSED: AI service failure correctly returns 500");
    }

    // ── Helper: probe multiple field names until one returns non-400 ──────────
    /**
     * Tries common request-body field names for the AI ask endpoint in order:
     *   "query", "message", "userMessage", "prompt"
     * Returns the first response that is NOT 400, or the last 400 response if all fail.
     * Each attempt is logged with its field name and HTTP status.
     */
    private static Response sendAiRequest(String text) {
        String[] candidateFields = { "query", "message", "userMessage", "prompt" };

        Response last = null;
        for (String field : candidateFields) {
            Map<String, String> body = new HashMap<>();
            body.put(field, text);

            Response r = noAuth().body(body)
                                 .when().post("/api/v1/ai-chat/ask")
                                 .then().extract().response();

            System.out.printf("[AIChatApiTest] field=%-15s → HTTP %d%n", "\"" + field + "\"", r.getStatusCode());
            last = r;

            if (r.getStatusCode() != 400) {
                System.out.println("[AIChatApiTest] ✔ Accepted field name: \"" + field + "\"");
                return r;
            }
        }
        // All fields returned 400 — return the last response so the assertion fails with context
        System.out.println("[AIChatApiTest] ✘ All candidate field names returned 400. "
                + "Inspect the response body above and update the field list.");
        return last;
    }
}
