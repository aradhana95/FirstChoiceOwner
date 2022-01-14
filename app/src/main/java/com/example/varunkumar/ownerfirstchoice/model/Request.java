package com.example.varunkumar.ownerfirstchoice.model;

import java.util.ArrayList;

/**
 * Created by 123456 on 2017/11/20.
 */

public class Request {
    private String phone;
    private String name;
    private String address;
    private String total;
    private String status;
    private String comment;
    private String paymentMethod;
    private String paymentState;
    private boolean partial = false;
    private String latLng;
    private String restaurantId;
    private String orderDateTime;
    private ArrayList<FoodNew> foods;
    private boolean isShipperAssigned=false;
    private String shipperNumber="";
    private String key;

    public Request() {
    }

    public Request(String phone, String name, String address, String total, String status,
                   String comment, String paymentMethod, String paymentState, String latLng,
                   String restaurantId, ArrayList<FoodNew> foods, String orderDateTime) {
        this.phone = phone;
        this.name = name;
        this.address = address;
        this.total = total;
        this.status = status;
        this.comment = comment;
        this.paymentMethod = paymentMethod;
        this.paymentState = paymentState;
        this.latLng = latLng;
        this.orderDateTime = orderDateTime;
        this.restaurantId = restaurantId;
        this.foods = foods;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public ArrayList<FoodNew> getFoods() {
        return foods;
    }

    public void setFoods(ArrayList<FoodNew> foods) {
        this.foods = foods;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getPaymentState() {
        return paymentState;
    }

    public void setPaymentState(String paymentState) {
        this.paymentState = paymentState;
    }

    public boolean isPartial() {
        return partial;
    }

    public void setPartial(boolean partial) {
        this.partial = partial;
    }

    public String getLatLng() {
        return latLng;
    }

    public void setLatLng(String latLng) {
        this.latLng = latLng;
    }

    public String getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(String restaurantId) {
        this.restaurantId = restaurantId;
    }

    public String getOrderDateTime() {
        return orderDateTime;
    }

    public void setOrderDateTime(String orderDateTime) {
        this.orderDateTime = orderDateTime;
    }

    public String getShipperNumber() {
        return shipperNumber;
    }

    public void setShipperNumber(String shipperNumber) {
        this.shipperNumber = shipperNumber;
    }

    public boolean isShipperAssigned() {
        return isShipperAssigned;
    }

    public void setShipperAssigned(boolean shipperAssigned) {
        isShipperAssigned = shipperAssigned;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
