package com.techsavanna.shiloahmsk.models;

public class BuildingModel {

    public String id;
    public String name;

    public BuildingModel(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }
}
