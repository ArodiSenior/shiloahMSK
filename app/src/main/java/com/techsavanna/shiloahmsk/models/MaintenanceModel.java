package com.techsavanna.shiloahmsk.models;

public class MaintenanceModel {
    public String building;
    public String comment;
    public String consumption_id;
    public String direct_consumption;
    public String last_reading;
    public String meter_number;
    public String meter_reading;
    public String shop;
    public String tenant;
    public String user_id;

    public MaintenanceModel(String building, String comment, String consumption_id, String direct_consumption, String last_reading, String meter_number, String meter_reading, String shop, String tenant, String user_id) {
        this.building = building;
        this.comment = comment;
        this.consumption_id = consumption_id;
        this.direct_consumption = direct_consumption;
        this.last_reading = last_reading;
        this.meter_number = meter_number;
        this.meter_reading = meter_reading;
        this.shop = shop;
        this.tenant = tenant;
        this.user_id = user_id;
    }

    public String getBuilding() {
        return building;
    }

    public String getComment() {
        return comment;
    }

    public String getConsumption_id() {
        return consumption_id;
    }

    public String getDirect_consumption() {
        return direct_consumption;
    }

    public String getLast_reading() {
        return last_reading;
    }

    public String getMeter_number() {
        return meter_number;
    }

    public String getMeter_reading() {
        return meter_reading;
    }

    public String getShop() {
        return shop;
    }

    public String getTenant() {
        return tenant;
    }

    public String getUser_id() {
        return user_id;
    }
}
