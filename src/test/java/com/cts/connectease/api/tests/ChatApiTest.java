package com.cts.connectease.api.tests;

import com.cts.connectease.api.base.BaseApiTest;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ChatApiTest extends BaseApiTest {

    // ── CE-CHAT-TC001 ─────────────────────────────────────────────────────────
    @Test(priority = 1,
          description = "CE-CHAT-TC001: GET /api/chat/start/{participantId} — create/get session returns 200")
    public void startChatSession() {
        Assert.assertNotNull(vendorUid, "Precondition: vendorUid must be set by AuthApiTest");

        // Customer starts a chat session with the Vendor
        Response response = asCustomer().when()
                .get("/api/chat/start/" + vendorUid)
                .then().extract().response();

        System.out.println("[CE-CHAT-TC001] Status : " + response.getStatusCode());
        System.out.println("[CE-CHAT-TC001] Body   : " + response.getBody().asString());

        Assert.assertEquals(status(response), 200,
                "Expected 200 OK for GET /api/chat/start/{participantId}");

        testSessionId = response.jsonPath().getString("sessionId");
        Assert.assertNotNull(testSessionId,           "sessionId must be in response");
        Assert.assertNotNull(response.jsonPath().getString("currentUserId"),  "currentUserId must be in response");
        Assert.assertNotNull(response.jsonPath().getString("participantName"),"participantName must be in response");
        Assert.assertNotNull(response.jsonPath().get("messages"),             "messages array must be in response");

        System.out.println("✔ CE-CHAT-TC001 PASSED: Chat session created — sessionId=" + testSessionId);
    }

    // ── CE-CHAT-TC002 ─────────────────────────────────────────────────────────
    @Test(priority = 2,
          description = "CE-CHAT-TC002: POST /api/chat/{sessionId}/messages — send message returns 200 with ChatMessageDto",
          dependsOnMethods = "startChatSession")
    public void sendMessage() {
        Assert.assertNotNull(testSessionId, "Precondition: testSessionId must be set by startChatSession()");

        Map<String, String> body = new HashMap<>();
        body.put("content", "Hello, is your electrical service available this weekend?");

        Response response = asCustomer().body(body)
                                        .when().post("/api/chat/" + testSessionId + "/messages")
                                        .then().extract().response();

        System.out.println("[CE-CHAT-TC002] Status : " + response.getStatusCode());
        System.out.println("[CE-CHAT-TC002] Body   : " + response.getBody().asString());

        Assert.assertEquals(status(response), 200,
                "Expected 200 OK for sending chat message");

        Assert.assertNotNull(response.jsonPath().getString("content"),    "content field missing");
        Assert.assertNotNull(response.jsonPath().getString("createdAt"),  "createdAt field missing");
        Assert.assertNotNull(response.jsonPath().getString("senderId"),   "senderId field missing");
        Assert.assertNotNull(response.jsonPath().getString("senderName"), "senderName field missing");

        // Verify senderId is the Customer (identity taken from JWT, not body)
        Assert.assertEquals(response.jsonPath().getString("senderId"), customerUid,
                "senderId must match the authenticated user's uid (from JWT), not a request field");

        System.out.println("✔ CE-CHAT-TC002 PASSED: Message sent — senderId=" + customerUid);
    }

    // ── CE-CHAT-TC003 ─────────────────────────────────────────────────────────
    // KNOWN DEFECT: CE-DEF-008 — Returns 403 instead of 401 for unauthenticated requests
    @Test(priority = 3,
          description = "CE-CHAT-TC003: POST /api/chat/{sessionId}/messages — unauthenticated returns 401 [DEFECT CE-DEF-008]",
          dependsOnMethods = "startChatSession")
    public void sendMessageUnauthenticated() {
        Assert.assertNotNull(testSessionId, "Precondition: testSessionId must be set");

        Map<String, String> body = new HashMap<>();
        body.put("content", "Unauthorized message attempt");

        Response response = noAuth().body(body)
                                    .when().post("/api/chat/" + testSessionId + "/messages")
                                    .then().extract().response();

        System.out.println("[CE-CHAT-TC003] Status : " + response.getStatusCode());

        // DEFECT CE-DEF-008: API returns 403; expected 401
        Assert.assertEquals(status(response), 401,
                "DEFECT CE-DEF-008 — Expected 401 Unauthorized for unauthenticated chat message but got "
                + status(response) + ". Root cause: no custom AuthenticationEntryPoint in SecurityConfig");

        System.out.println("✔ CE-CHAT-TC003 PASSED: Unauthenticated chat message correctly rejected");
    }

    // ── CE-CHAT-TC004 ─────────────────────────────────────────────────────────
    @Test(priority = 4,
          description = "CE-CHAT-TC004: GET /api/chat/sessions — list all sessions for current user returns 200")
    public void getAllChatSessions() {
        Response response = asCustomer().when().get("/api/chat/sessions")
                                        .then().extract().response();

        System.out.println("[CE-CHAT-TC004] Status : " + response.getStatusCode());
        System.out.println("[CE-CHAT-TC004] Body   : " + response.getBody().asString());

        Assert.assertEquals(status(response), 200,
                "Expected 200 OK for GET /api/chat/sessions");

        List<Map<String, Object>> sessions = response.jsonPath().getList("$");
        Assert.assertNotNull(sessions, "Sessions response must be a non-null array");
        Assert.assertFalse(sessions.isEmpty(),
                "At least one session should exist (created in CE-CHAT-TC001)");

        Map<String, Object> first = sessions.get(0);
        Assert.assertNotNull(first.get("sessionId"),       "sessionId field missing");
        Assert.assertNotNull(first.get("participantName"), "participantName field missing");

        System.out.println("✔ CE-CHAT-TC004 PASSED: " + sessions.size() + " chat session(s) returned");
    }
}
