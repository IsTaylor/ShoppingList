package com.example.owner.shoppinglist.data;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

/**
 * Created by owner on 11/10/17.
 */

public class Item extends RealmObject{

    public static final int FOOD = 0;
    public static final int ELECTRONIC = 1;
    public static final int BOOK = 2;

    private int category;
    private String name;
    private String description;
    private int price;
    private boolean status;

    @PrimaryKey
    @Required
    private String itemID;


    public Item(){

    }

    public Item(int category, String name, String description, int price, boolean status) {
        this.category = category;
        this.name = name;
        this.description = description;
        this.price = price;
        this.status = status;
    }

    public int getCategory() {
        return category;
    }

    public void setCategory(int category) {
        this.category = category;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public boolean getStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getItemID() {
        return itemID;
    }
}
