package com.techsavanna.shiloahmsk.models;

public class ShopModel {
    public String id;
    public String code;
    public String building_id;

    public ShopModel(String id, String code, String building_id) {
        this.id = id;
        this.code = code;
        this.building_id = building_id;
    }

    public String getId() {
        return id;
    }

    public String getCode() {
        return code;
    }

    public String getBuilding_id() {
        return building_id;
    }
}
