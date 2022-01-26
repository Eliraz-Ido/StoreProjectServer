package com.dev.objects;
import javax.persistence.*;

@Entity
@Table(name = "Sales_organizations")
public class SalesOrganizations {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "organization")
    private OrganizationObject organization;

    @ManyToOne
    @JoinColumn(name = "sale")
    private SaleObject sale;

    //Constructors
    public SalesOrganizations() { }
    public SalesOrganizations(OrganizationObject organization, SaleObject sale) {
        this.organization = organization;
        this.sale = sale;
    }

    //Getters
    public int getId() { return id; }
    public OrganizationObject getOrganization() { return organization; }
    public SaleObject getSale() { return sale; }

    //Setters
    public void setOrganization(OrganizationObject organization) { this.organization = organization; }
    public void setSale(SaleObject sale) { this.sale = sale; }


}
