package com.dev;

import com.dev.objects.*;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.*;


@Component
public class Persist {
    final int empty = 0;
    final int firstObject = 0;
    private final SessionFactory sessionFactory;

    @Autowired
    public Persist(SessionFactory sf) {
        this.sessionFactory = sf;
    }


    @PostConstruct
    public void createConnectionToDatabase() {
//        Session session = sessionFactory.openSession();
//        Transaction transaction = session.beginTransaction();
//
//        List<OrganizationObject> orgList = new ArrayList<>();
//        orgList.add(new OrganizationObject("Hever"));
//        orgList.add(new OrganizationObject("Kranot - Police"));
//        orgList.add(new OrganizationObject("Tov Club"));
//        orgList.add(new OrganizationObject("Yoter Club"));
//        orgList.add(new OrganizationObject("Teachers Union"));
//        for(OrganizationObject o : orgList)
//            session.save(o);
//
//        List<ShopObjects> shopsList = new ArrayList<>();
//        shopsList.add(new ShopObjects("Ace"));
//        shopsList.add(new ShopObjects("fox home"));
//        shopsList.add(new ShopObjects("Golf & Co"));
//        shopsList.add(new ShopObjects("Soltam"));
//        shopsList.add(new ShopObjects("ToysRus"));
//        shopsList.add(new ShopObjects("Laline"));
//        shopsList.add(new ShopObjects("Delta"));
//        shopsList.add(new ShopObjects("American Eagles"));
//        shopsList.add(new ShopObjects("H&O"));
//        for(ShopObjects o : shopsList)
//            session.save(o);
//
//        SaleObject sale = new SaleObject(shopsList.get(1),
//                "20% off on pillows",
//                LocalDateTime.of(2022, 1, 5, 10, 20),
//                LocalDateTime.of(2022, 1, 6, 10, 20), false);
//
//        orgList.get(1).getSales().add(sale);
//        orgList.get(2).getSales().add(sale);
//        session.save(sale);
//
//        transaction.commit();
//        session.close();
    }

    public boolean createAccount(UserObject userObject) {
        Session session = sessionFactory.openSession();
        Transaction transaction = session.beginTransaction();
        if (!isUsernameTaken(userObject.getUsername())) {
            session.save(userObject);
        }

        transaction.commit();
        session.close();
        return userObject.getId() != empty;
    }

    public boolean isUsernameTaken(String username) {
        Session session = sessionFactory.openSession();
        Transaction transaction = session.beginTransaction();

        List list = session.createQuery("FROM UserObject WHERE username = :username ")
                .setParameter("username", username)
                .list();

        transaction.commit();
        session.close();
        return list.size() != empty;
    }

    public String getTokenByUsernameAndPassword(String username, String password) {
        Session session = sessionFactory.openSession();
        Transaction transaction = session.beginTransaction();

        List list = session.createQuery("FROM UserObject WHERE username = :username AND password = :password")
                .setParameter("username", username)
                .setParameter("password", password)
                .list();

        transaction.commit();
        session.close();
        return list.size() != empty ? ((UserObject) list.get(firstObject)).getToken() : null;
    }

    public List<HashMap> getOrganizationsForOneUser(String token) {
        Session session = sessionFactory.openSession();
        Transaction transaction = session.beginTransaction();

        UserObject user = (UserObject) session.createQuery("FROM UserObject WHERE token =: token")
                .setParameter("token", token).getSingleResult();
        List<HashMap> finalList = new ArrayList<>();

        if(user != null){
            Set<OrganizationObject> userOrganizations = user.getOrganizations();
            List<OrganizationObject> organizations = session.createQuery("FROM OrganizationObject").list();
            for(OrganizationObject org : organizations){
                HashMap temp = new HashMap();
                temp.put("organization", org);
                temp.put("isAMember", userOrganizations.contains(org));
                finalList.add(temp);
            }
        }

        transaction.commit();
        session.close();
        return finalList.size()!= empty? finalList : null;
    }

    public void addMemberToOrganization(String token, int orgId){              //////////////////////////////////////////////////
        Session session = sessionFactory.openSession();
        Transaction transaction = session.beginTransaction();
        UserObject user = (UserObject) session.createQuery("FROM UserObject WHERE token = :token" )
                .setParameter("token", token).getSingleResult();
        OrganizationObject organization = session.load(OrganizationObject.class, orgId);
        if (user != null && organization != null) {
            user.getOrganizations().add(organization);
            organization.getUsers().add(user);
        }
        transaction.commit();
        session.close();
    }

    public void removeMemberFromOrganization(String token, int orgId){                   ///////////////////////////////////////////
        Session session = sessionFactory.openSession();
        Transaction transaction = session.beginTransaction();
        UserObject user = (UserObject) session.createQuery("FROM UserObject WHERE token = :token" )
                .setParameter("token", token).getSingleResult();
        OrganizationObject organization = session.load(OrganizationObject.class, orgId);
        if (user != null && organization != null) {
            user.getOrganizations().remove(organization);
            organization.getUsers().remove(user);
        }
        transaction.commit();
        session.close();
    }

    public Set<SaleObject> getSalesForUser(String token) {
        Session session = sessionFactory.openSession();
        Transaction transaction = session.beginTransaction();

        UserObject user = (UserObject) session.createQuery("FROM UserObject WHERE token =: token")
                .setParameter("token", token).getSingleResult();
        Set<SaleObject> finalList = null;

        if (user != null) {
            Set<OrganizationObject> userOrganizations = user.getOrganizations();
            finalList = new HashSet<SaleObject>(session.createQuery("FROM SaleObject WHERE isToAllUsers = true").list());
            for (Object saleOrgObject : session.createQuery("FROM SalesOrganizations").list()) {
                if(userOrganizations.contains(((SalesOrganizations)saleOrgObject).getOrganization())){
                    finalList.add(((SalesOrganizations) saleOrgObject).getSale());
                }
            }
        }
        transaction.commit();
        session.close();
        return finalList;
    }

    public List<ShopObjects> getAllShops(String token) {
        Session session = sessionFactory.openSession();
        Transaction transaction = session.beginTransaction();

        UserObject user = (UserObject) session.createQuery("FROM UserObject WHERE token =: token")
                .setParameter("token", token).getSingleResult();
        List<ShopObjects> shops = null;
        if (user != null) {
            shops = session.createQuery("FROM ShopObjects").list();
        }

        transaction.commit();
        session.close();
        return shops;
    }

    public List<SaleObject> getSalesForOneShop(String token, int shopId) {
        Session session = sessionFactory.openSession();
        Transaction transaction = session.beginTransaction();

        UserObject user = (UserObject) session.createQuery("FROM UserObject WHERE token =: token")
                .setParameter("token", token).getSingleResult();
        List<SaleObject> finalList = null;
        if (user != null) {
            finalList = session.createQuery("FROM SaleObject WHERE shop.id = :shopId ORDER BY id ")
                    .setParameter("shopId", shopId).list();
        }
        transaction.commit();
        session.close();
        return finalList;
    }


    public List<HashMap> getAllSalesForUser(String token) {
        Session session = sessionFactory.openSession();
        Transaction transaction = session.beginTransaction();

        UserObject user = (UserObject) session.createQuery("FROM UserObject WHERE token =: token")
                .setParameter("token", token).getSingleResult();
        List<SaleObject> allSales = session.createQuery("FROM SaleObject ").list();
        List<HashMap> finalList = new ArrayList<>();

        if(user != null){
           for(SaleObject sale : allSales) {
               boolean isRelevantToUser = sale.isToAllUsers();
               if (!sale.isToAllUsers()) {
                   for (Object saleOrgObject : session.createQuery("FROM SalesOrganizations WHERE sale =:sale")
                           .setParameter("sale", sale).list()) {
                       if (user.getOrganizations().contains(((SalesOrganizations) saleOrgObject).getOrganization())) {
                           isRelevantToUser = true;
                           break;
                       }
                   }
               }
               HashMap temp = new HashMap();
               temp.put("sale",sale);
               temp.put("isRelevantToUser",isRelevantToUser);
               finalList.add(temp);

           }
       }
        transaction.commit();
        session.close();
        return finalList;
    }

    public Set<UserObject> getUsersForOneSale(int saleId) {
        Session session = sessionFactory.openSession();
        Transaction transaction = session.beginTransaction();

        Set<UserObject> users = new HashSet<>();
        SaleObject sale = session.load(SaleObject.class, saleId);

        if(sale.isToAllUsers()) {
            users.addAll(session.createQuery("FROM UserObject ").list());
        }
        else {
            List salesOrganizations = session.createQuery("FROM SalesOrganizations WHERE sale.id = :saleId")
                    .setParameter("saleId", saleId).list();
            for (Object saleOrg : salesOrganizations) {
                users.addAll(((SalesOrganizations) saleOrg).getOrganization().getUsers());
            }
        }

        transaction.commit();
        session.close();
        return users;
    }

    public List<SaleObject> getAllSales() {
        Session session = sessionFactory.openSession();
        Transaction transaction = session.beginTransaction();
        List<SaleObject> sales = new ArrayList<>();
        List temp = session.createQuery("FROM SaleObject ").list();
        for(Object o : temp)
            sales.add((SaleObject) o);
        transaction.commit();
        session.close();
        return sales;
    }
}