package com.dev.controllers;

import com.dev.Persist;
import com.dev.objects.SaleObject;
import com.dev.objects.ShopObjects;
import com.dev.objects.UserObject;
import com.dev.utils.MessagesHandler;
import com.dev.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.*;

@RestController
public class TestController {
    List<Timer> timers = new ArrayList<>();

    @Autowired
    private final MessagesHandler messagesHandler = new MessagesHandler();

    @Autowired
    private Persist persist;

    @PostConstruct
    private void init() {
        List<SaleObject> sales = persist.getAllSales();
        for (SaleObject sale : sales) {
            initializeTimersForSale(sale);
        }

        System.out.println("&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&");
        System.out.println((sales.get(1).getEndDate().minus(10, ChronoUnit.MINUTES)));
        System.out.println("&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&");

    }

    @RequestMapping(value = "/sign-in")
    public String signIn(String username, String password) {
        return persist.getTokenByUsernameAndPassword(username, password);
    }

    @RequestMapping(value = "/create-account", method = RequestMethod.POST)
    public boolean createAccount(String username, String password) {
        boolean success = false;
        boolean alreadyExists = persist.getTokenByUsernameAndPassword(username, password) != null;
        if (!alreadyExists) {
            UserObject userObject = new UserObject();
            userObject.setUsername(username);
            userObject.setPassword(password);
            String hash = Utils.createHash(username, password);
            userObject.setToken(hash);
            success = persist.createAccount(userObject);
        }

        return success;
    }

    @RequestMapping(value = "/get-organizations-for-one-user", method = RequestMethod.GET)
    public List<HashMap> getOrganizationsForOneUser(String token) {
        return persist.getOrganizationsForOneUser(token);
    }

    @RequestMapping(value = "/add-member-to-organization", method = RequestMethod.POST)
    public void addMemberToOrganization(String token, int orgId) {
        persist.addMemberToOrganization(token, orgId);
    }

    @RequestMapping(value = "/remove-member-from-organization", method = RequestMethod.POST)
    public void removeMemberFromOrganization(String token, int orgId) {
        persist.removeMemberFromOrganization(token, orgId);
    }

    @RequestMapping(value = "/is-username-taken", method = RequestMethod.GET)
    public boolean isUsernameTaken(String username) {
        return persist.isUsernameTaken(username);
    }

    @RequestMapping(value = "/get-all-shops", method = RequestMethod.GET)
    public List<ShopObjects> getAllShops(String token){
        return persist.getAllShops(token);
    }

    @RequestMapping(value = "/get-sales-for-user", method = RequestMethod.GET)
    public Set<SaleObject> getSalesForUser(String token){
        return persist.getSalesForUser(token);
    }

    @RequestMapping(value = "/get-sales-for-one-shop", method = RequestMethod.GET)
    public List<SaleObject> getSalesOfOneShop(String token, int shopId){
        return persist.getSalesForOneShop(token, shopId);
    }

    @RequestMapping(value = "/get-all-sales-for-user", method = RequestMethod.GET)
    public List<HashMap> getAllSalesForUser(String token){
        return persist.getAllSalesForUser(token);
    }

    private void initializeTimersForSale(SaleObject sale){
        long saleStartMilli = sale.getStartDate().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        long saleEndMilli = sale.getEndDate().minus(10, ChronoUnit.MINUTES).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();

        Timer startTimer = new Timer();
        Timer endTimer = new Timer();

        TimerTask startTask = new TimerTask() {
            @Override
            public void run() {
                try {
                    messagesHandler.sendStartSaleNotifications(persist.getUsersForOneSale(sale.getId()), sale);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        TimerTask endTask = new TimerTask() {
            @Override
            public void run() {
                try {
                    messagesHandler.sendEndSaleNotifications(persist.getUsersForOneSale(sale.getId()), sale);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };

        startTimer.schedule(startTask, new Date(saleStartMilli));
        endTimer.schedule(endTask, new Date(saleEndMilli));

        timers.add(startTimer);
        timers.add(endTimer);
    }

}