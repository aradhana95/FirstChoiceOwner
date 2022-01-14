package com.example.varunkumar.ownerfirstchoice.model;

import java.util.List;

public class RequestOld {
    private String phone;
    private String name;
    private String address;
    private String total;
    private String status;
    private List<Order> foods;
    private String comment;
    private String paymentMethod;
    private String paymentState;
    private boolean partial = false;
    private String latLng;
    private String restaurantId;
    private String tempShipper;
    private String restaurantPhone;
    private String shipperNumber="";
    private String key;

    public RequestOld() {
    }

    public RequestOld(String phone, String name, String address, String total,
                      String status, String comment, String paymentMethod, String paymentState, String latLng,
                      String restaurantId, List<Order> foods,String tempShipper) {
        this.phone = phone;
        this.name = name;
        this.address = address;
        this.total = total;
        this.status = status;
        this.comment = comment;
        this.paymentMethod=paymentMethod;
        this.paymentState=paymentState;
        this.latLng = latLng;
        this.restaurantId = restaurantId;
        this.foods = foods;
        this.tempShipper = tempShipper;
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

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public List<Order> getFoods() {
        return foods;
    }

    public void setFoods(List<Order> foods) {
        this.foods = foods;
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

    public String getTempShipper() {
        return tempShipper;
    }

    public void setTempShipper(String tempShipper) {
        this.tempShipper = tempShipper;
    }

    public String getShipperNumber() {
        return shipperNumber;
    }

    public void setShipperNumber(String shipperNumber) {
        this.shipperNumber = shipperNumber;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getRestaurantPhone() {
        return restaurantPhone;
    }

    public void setRestaurantPhone(String restaurantPhone) {
        this.restaurantPhone = restaurantPhone;
    }
}
