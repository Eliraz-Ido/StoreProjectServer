package com.dev.objects;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "organizations")
public class OrganizationObject {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "organizationName")
    private String organizationName;

    @ManyToMany(
            fetch = FetchType.LAZY,
            cascade= {
                    CascadeType.DETACH,
                    CascadeType.MERGE,
                    CascadeType.REFRESH,
                    CascadeType.PERSIST
            },
            targetEntity = UserObject.class,
            mappedBy = "organizations")
    @JsonIgnoreProperties("organizations")
    private Set<UserObject> users = new HashSet<>();

    //Constructors
    public OrganizationObject() { }
    public OrganizationObject(String organizationName) {
        this.organizationName = organizationName;
    }

    //Getters
    public int getId() {
        return id;
    }
    public String getOrganizationName() {
        return organizationName;
    }
    public Set<UserObject> getUsers() { return users; }

    //Setter
    public void setOrganizationName(String organizationName) {
        this.organizationName = organizationName;
    }


}
