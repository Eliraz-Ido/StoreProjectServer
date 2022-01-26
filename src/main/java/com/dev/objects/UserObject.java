package com.dev.objects;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name= "users")
public class UserObject {

    @Id
    @Column(name="id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name="username")
    private String username;

    @Column(name="password")
    private String password;

    @Column(name="token")
    private String token;

    @ManyToMany(
            fetch = FetchType.LAZY,
            cascade= {
                    CascadeType.DETACH,
                    CascadeType.MERGE,
                    CascadeType.REFRESH,
                    CascadeType.PERSIST
            },
            targetEntity = OrganizationObject.class)
    @JoinTable(name="users_Organizations",
            joinColumns=@JoinColumn(name="userId"),
            inverseJoinColumns=@JoinColumn(name="organizationId"),
            uniqueConstraints =@UniqueConstraint(columnNames =  {"userId", "organizationId"}))
    @JsonIgnoreProperties("users")
    private Set<OrganizationObject> organizations = new HashSet<>();


    //Constructors
    public UserObject() {}
    public UserObject(String username, String password, String token){
        this.username = username;
        this.password = password;
        this.token = token;
    }

    //Getters
    public int getId() {
        return id;
    }
    public String getUsername() { return username; }
    public String getToken() {
        return token;
    }
    public Set<OrganizationObject> getOrganizations() { return organizations; }

    //Setters
    public void setUsername(String username) {
        this.username = username;
    }
    public void setPassword(String password) { this.password = password;}
    public void setToken(String token) {
        this.token = token;
    }
    public void setOrganizations(Set<OrganizationObject> organizations) { this.organizations = organizations; }



}
