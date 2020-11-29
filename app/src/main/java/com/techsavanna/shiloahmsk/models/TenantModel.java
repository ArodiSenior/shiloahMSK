package com.techsavanna.shiloahmsk.models;

public class TenantModel {
    public String code;
    public String electricity_meter_number;
    public String id;
    public String last_electricity_reading;
    public String last_water_reading;
    public String shop_id;
    public String water_meter_number;

    public TenantModel(String code, String electricity_meter_number, String id, String last_electricity_reading, String last_water_reading, String shop_id, String water_meter_number) {
        this.code = code;
        this.electricity_meter_number = electricity_meter_number;
        this.id = id;
        this.last_electricity_reading = last_electricity_reading;
        this.last_water_reading = last_water_reading;
        this.shop_id = shop_id;
        this.water_meter_number = water_meter_number;
    }

    public String getCode() {
        return code;
    }

    public String getElectricity_meter_number() {
        return electricity_meter_number;
    }

    public String getId() {
        return id;
    }

    public String getLast_electricity_reading() {
        return last_electricity_reading;
    }

    public String getLast_water_reading() {
        return last_water_reading;
    }

    public String getShop_id() {
        return shop_id;
    }

    public String getWater_meter_number() {
        return water_meter_number;
    }
}
