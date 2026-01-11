package com.example.a1;

public class ResponseModel {
    private String responseId, responderName, responderPhone, itemName, type;

    public ResponseModel(String responseId, String responderName, String responderPhone, String itemName, String type) {
        this.responseId = responseId;
        this.responderName = responderName;
        this.responderPhone = responderPhone;
        this.itemName = itemName;
        this.type = type;
    }

    public String getResponseId() { return responseId; }
    public String getResponderPhone() { return responderPhone; }

    public String getDisplayMessage() {
        String action = "LOST".equalsIgnoreCase(type) ? " found your " : " claimed your ";
        return responderName + action + itemName;
    }
}
