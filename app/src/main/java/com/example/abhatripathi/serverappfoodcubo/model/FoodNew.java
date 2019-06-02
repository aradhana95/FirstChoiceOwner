package com.example.abhatripathi.serverappfoodcubo.model;

public class FoodNew {

    private String discount,image,price,productId,productName,quantity,restaurantName,userPhone,vegType;

    public FoodNew() {
    }

    public FoodNew(String discount, String image, String price, String productId,
                   String productName, String quantity, String restaurantName, String userPhone, String vegType) {
        this.discount=discount;
        this.image=image;
        this.price=price;
        this.productId=productId;

        this.productName=productName;
        this.quantity=quantity;
        this.restaurantName=restaurantName;
        this.userPhone=userPhone;
        this.vegType=vegType;

    }


    public String getDiscount() {
        return discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getRestaurantName() {
        return restaurantName;
    }

    public void setRestaurantName(String restaurantName) {
        this.restaurantName = restaurantName;
    }

    public String getUserPhone() {
        return userPhone;
    }

    public void setUserPhone(String userPhone) {
        this.userPhone = userPhone;
    }

    public String getVegType() {
        return vegType;
    }

    public void setVegType(String vegType) {
        this.vegType = vegType;
    }
}


