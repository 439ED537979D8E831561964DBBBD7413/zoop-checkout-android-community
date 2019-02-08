package com.zoop.checkout.app.Model;


/**
 * Created by mainente on 02/01/17.
 */

public class Buyer  {
    String first_name = "";

    String last_name = "";

    String taxpayer_id = "";

    String description = "";

    String email = "";

    String address_number = "";

    String address_complement = "";

    String neighborhood = "";

    String address = "";

    String state = "";

    String postal_code = "";

    String country_code = "";

    String city = "";


    private static Buyer instance = null;


    public static Buyer getInstance() {
        if (null == instance) {
            instance = new Buyer();
        }
        return instance;
    }


    public static void setInstance(Buyer instance) {
        Buyer.instance = instance;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public String getTaxpayer_id() {
        return taxpayer_id;
    }

    public void setTaxpayer_id(String taxpayer_id) {
        this.taxpayer_id = taxpayer_id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }


    public String getAddress_number() {
        return address_number;
    }

    public void setAddress_number(String address_number) {
        this.address_number = address_number;
    }

    public String getAddress_complement() {
        return address_complement;
    }

    public void setAddress_complement(String address_complement) {
        this.address_complement = address_complement;
    }

    public String getNeighborhood() {
        return neighborhood;
    }

    public void setNeighborhood(String neighborhood) {
        this.neighborhood = neighborhood;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getPostal_code() {
        return postal_code;
    }

    public void setPostal_code(String postal_code) {
        this.postal_code = postal_code;
    }

    public String getCountry_code() {
        return country_code;
    }

    public void setCountry_code(String country_code) {
        this.country_code = country_code;
    }
}
