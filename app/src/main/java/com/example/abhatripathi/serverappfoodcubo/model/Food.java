package com.example.abhatripathi.serverappfoodcubo.model;

public class Food {

    private String Name, Image, Description, fullprice, halfprice, Discount, MenuId, FoodId
            , AvailabilityFlag, pieceType, vegType;

    public Food() {
    }

    public Food(String name, String image, String description, String fullprice,
                String discount, String menuId, String foodId, String availabilityFlag) {
        Name = name;
        Image = image;
        Description = description;
        this.fullprice = fullprice;
        Discount = discount;
        MenuId = menuId;
        FoodId = foodId;
        AvailabilityFlag = availabilityFlag;
    }



    public String getPieceType() {
        return this.pieceType;
    }

    public void setPieceType(String pieceType) {
        this.pieceType = pieceType;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        Image = image;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }


    public String getDiscount() {
        return Discount;
    }

    public void setDiscount(String discount) {
        Discount = discount;
    }

    public String getMenuId() {
        return MenuId;
    }

    public void setMenuId(String menuId) {
        MenuId = menuId;
    }

    public String getFoodId() {
        return FoodId;
    }

    public void setFoodId(String foodId) {
        FoodId = foodId;
    }

    public String getAvailabilityFlag() {
        return AvailabilityFlag;
    }

    public void setAvailabilityFlag(String availabilityFlag) {
        AvailabilityFlag = availabilityFlag;
    }

    public String getFullprice() {
        return this.fullprice;
    }

    public void setFullprice(String fullprice) {
        this.fullprice = fullprice;
    }

    public String getHalfprice() {
        return halfprice;
    }

    public void setHalfprice(String halfprice) {
        this.halfprice = halfprice;
    }

    public String getVegType() {
        return vegType;
    }

    public void setVegType(String vegType) {
        this.vegType = vegType;
    }
}


