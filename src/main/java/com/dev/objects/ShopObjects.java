package com.dev.objects;

import javax.persistence.*;

@Entity
@Table(name = "shops")
public class ShopObjects {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "shopName")
    private String shopName;

    //Constructors

    public ShopObjects() { }
    public ShopObjects(String shopName) {
        this.shopName = shopName;
    }

    //Getters
    public int getId() {
        return id;
    }
    public String getShopName() {
        return shopName;
    }

    //Setter
    public void setShopName(String shopName) {
        this.shopName = shopName;
    }
}
