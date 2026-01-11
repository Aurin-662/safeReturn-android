package com.example.a1;

public class ResponseModel {
    private String responderName, responderPhone, itemName, type;

    public ResponseModel(String responderName, String responderPhone, String itemName, String type) {
        this.responderName = responderName;
        this.responderPhone = responderPhone;
        this.itemName = itemName;
        this.type = type;
    }

    public String getDisplayMessage() {
        String action = "LOST".equalsIgnoreCase(type) ? " found your " : " claimed your ";
        // নাম, আইটেম এবং ফোন নম্বর একসাথে দেখাবে
        return responderName + action + itemName + "\nContact: " + responderPhone;
    }
}
