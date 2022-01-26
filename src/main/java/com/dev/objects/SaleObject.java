package com.dev.objects;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "sales")
public class SaleObject {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "shop")
    private ShopObjects shop;

    @Column(name = "description")
    private String description;

    @Column(name = "startDate")
    private LocalDateTime startDate;

    @Column(name = "endDate")
    private LocalDateTime endDate;

    @Column(name = "isToAllUsers")
    private boolean isToAllUsers;

    //Constructors
    public SaleObject() {
    }

    public SaleObject(ShopObjects shop) {
        this.shop = shop;
    }

    public SaleObject(ShopObjects shop, String description, LocalDateTime startDate, LocalDateTime endDate) {
        this(shop, description, startDate, endDate, true);
    }

    public SaleObject(ShopObjects shop, String description, LocalDateTime startDate, LocalDateTime endDate, boolean isToAllUsers) {
        this.shop = shop;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.isToAllUsers = isToAllUsers;
    }

    //Getters
    public int getId() {
        return id;
    }
    public ShopObjects getShop() {
        return shop;
    }
    public String getDescription() {
        return description;
    }
    public LocalDateTime getStartDate() {
        return startDate;
    }
    public LocalDateTime getEndDate() {
        return endDate;
    }
    public boolean isToAllUsers() {
        return isToAllUsers;
    }

    //Setters
    public void setShop(ShopObjects shop) {
        this.shop = shop;
    }
    public void setDescription(String description) { this.description = description; }
    public void setStartDate(LocalDateTime startDate) { this.startDate = startDate; }
    public void setEndDate(LocalDateTime endDate) { this.endDate = endDate; }
    public void setToAllUsers(boolean toAllUsers) { isToAllUsers = toAllUsers; }

}