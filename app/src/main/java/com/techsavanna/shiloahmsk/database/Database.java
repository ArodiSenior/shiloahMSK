package com.techsavanna.shiloahmsk.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;
import com.techsavanna.shiloahmsk.models.BuildingModel;
import com.techsavanna.shiloahmsk.models.MaintenanceModel;
import com.techsavanna.shiloahmsk.models.ShopModel;
import com.techsavanna.shiloahmsk.models.TenantModel;
import com.techsavanna.shiloahmsk.models.UserModel;
import com.techsavanna.shiloahmsk.session.SessionManager;

import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;

public class Database extends SQLiteAssetHelper {
    public static final String DB_NAME = "shiloahmsk.db";
    public static final int DB_VER = 1;

    public void onUpgrade(SQLiteDatabase sQLiteDatabase, int i, int i2) {
    }

    public Database(Context context) {
        super(context, DB_NAME, (SQLiteDatabase.CursorFactory) null, 1);
    }

    public JSONObject get_users(String str, String str2) {
        SQLiteDatabase readableDatabase = getReadableDatabase();
        JSONObject jSONObject = new JSONObject();
        Cursor rawQuery = readableDatabase.rawQuery("SELECT * FROM users WHERE username =? AND password =? limit 1", new String[]{str, str2});
        if (rawQuery.getCount() != 0 && rawQuery.moveToFirst()) {
            try {
                jSONObject.put(SessionManager.KEY_USER_ID, rawQuery.getString(rawQuery.getColumnIndex(SessionManager.KEY_USER_ID)));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        rawQuery.close();
        return jSONObject;
    }

    public void add_user(UserModel userModel) {
        getReadableDatabase().execSQL(String.format("INSERT INTO users(username, password, user_id) VALUES ('%s', '%s', '%s');", new Object[]{userModel.getUsername(), userModel.getPassword(), userModel.getUser_id()}));
    }

    public void clear_users() {
        SQLiteDatabase readableDatabase = getReadableDatabase();
        readableDatabase.delete("SQLITE_SEQUENCE", "NAME=?", new String[]{"users"});
        readableDatabase.execSQL("DELETE FROM users");
    }

    public void add_building(BuildingModel buildingModel) {
        SQLiteDatabase readableDatabase = getReadableDatabase();
        Cursor rawQuery = readableDatabase.rawQuery("SELECT * FROM building WHERE building_id=?", new String[]{buildingModel.getId()});
        if (rawQuery.getCount() == 0) {
            readableDatabase.execSQL(String.format("INSERT INTO building (building_id, name) VALUES ('%s','%s')", new Object[]{buildingModel.getId(), buildingModel.getName()}));
        }
        rawQuery.close();
    }

    public List<BuildingModel> fetch_building() {
        SQLiteDatabase readableDatabase = getReadableDatabase();
        ArrayList arrayList = new ArrayList();
        Cursor rawQuery = readableDatabase.rawQuery("SELECT * FROM building", new String[0]);
        rawQuery.moveToFirst();
        if (rawQuery.moveToFirst()) {
            do {
                arrayList.add(new BuildingModel(rawQuery.getString(rawQuery.getColumnIndex("building_id")), rawQuery.getString(rawQuery.getColumnIndex("name"))));
            } while (rawQuery.moveToNext());
        }
        rawQuery.close();
        return arrayList;
    }

    public void clear_building() {
        SQLiteDatabase readableDatabase = getReadableDatabase();
        readableDatabase.delete("SQLITE_SEQUENCE", "NAME=?", new String[]{"building"});
        readableDatabase.execSQL("DELETE FROM building");
    }

    public void add_shop(ShopModel shopModel) {
        SQLiteDatabase readableDatabase = getReadableDatabase();
        Cursor rawQuery = readableDatabase.rawQuery("SELECT * FROM shop WHERE shop_id=?", new String[]{shopModel.getId()});
        if (rawQuery.getCount() == 0) {
            readableDatabase.execSQL(String.format("INSERT INTO shop (shop_id, code, building_id) VALUES ('%s', '%s', '%s')", new Object[]{shopModel.getId(), shopModel.getCode(), shopModel.getBuilding_id()}));
        }
        rawQuery.close();
    }

    public List<ShopModel> fetch_shop(String str) {
        SQLiteDatabase readableDatabase = getReadableDatabase();
        ArrayList arrayList = new ArrayList();
        Cursor rawQuery = readableDatabase.rawQuery("SELECT * FROM shop WHERE building_id=?", new String[]{str});
        rawQuery.moveToFirst();
        if (rawQuery.moveToFirst()) {
            do {
                arrayList.add(new ShopModel(rawQuery.getString(rawQuery.getColumnIndex("shop_id")), rawQuery.getString(rawQuery.getColumnIndex("code")), rawQuery.getString(rawQuery.getColumnIndex("building_id"))));
            } while (rawQuery.moveToNext());
        }
        rawQuery.close();
        return arrayList;
    }

    public void clear_shop() {
        SQLiteDatabase readableDatabase = getReadableDatabase();
        readableDatabase.delete("SQLITE_SEQUENCE", "NAME=?", new String[]{"shop"});
        readableDatabase.execSQL("DELETE FROM shop");
    }

    public void add_tenant(TenantModel tenantModel) {
        SQLiteDatabase readableDatabase = getReadableDatabase();
        Cursor rawQuery = readableDatabase.rawQuery("SELECT * FROM tenant WHERE tenant_id=?", new String[]{tenantModel.getId()});
        if (rawQuery.getCount() == 0) {
            readableDatabase.execSQL(String.format("INSERT INTO tenant (tenant_id, code, last_water_reading, last_electricity_reading, shop_id, water_meter_number, electricity_meter_number) VALUES ('%s', '%s', '%s', '%s', '%s', '%s', '%s')", new Object[]{tenantModel.getId(), tenantModel.getCode(), tenantModel.getLast_water_reading(), tenantModel.getLast_electricity_reading(), tenantModel.getShop_id(), tenantModel.getWater_meter_number(), tenantModel.getElectricity_meter_number()}));
        }
        rawQuery.close();
    }

    public List<TenantModel> fetch_tenant(String str) {
        SQLiteDatabase readableDatabase = getReadableDatabase();
        ArrayList arrayList = new ArrayList();
        Cursor rawQuery = readableDatabase.rawQuery("SELECT * FROM tenant WHERE shop_id=?", new String[]{str});
        rawQuery.moveToFirst();
        if (rawQuery.moveToFirst()) {
            do {
                arrayList.add(new TenantModel(rawQuery.getString(rawQuery.getColumnIndex("tenant_id")), rawQuery.getString(rawQuery.getColumnIndex("code")), rawQuery.getString(rawQuery.getColumnIndex("last_water_reading")), rawQuery.getString(rawQuery.getColumnIndex("last_electricity_reading")), rawQuery.getString(rawQuery.getColumnIndex("shop_id")), rawQuery.getString(rawQuery.getColumnIndex("water_meter_number")), rawQuery.getString(rawQuery.getColumnIndex("electricity_meter_number"))));
            } while (rawQuery.moveToNext());
        }
        rawQuery.close();
        return arrayList;
    }

    public void clear_tenant() {
        SQLiteDatabase readableDatabase = getReadableDatabase();
        readableDatabase.delete("SQLITE_SEQUENCE", "NAME=?", new String[]{"tenant"});
        readableDatabase.execSQL("DELETE FROM tenant");
    }

    public void add_maintenance(MaintenanceModel maintenanceModel) {
        getReadableDatabase().execSQL(String.format("INSERT INTO maintenance (user_id, direct_consumption, consumption_id,meter_reading,building,shop,tenant,comment, meter_number, last_reading) VALUES('%s','%s','%s','%s','%s','%s','%s','%s','%s','%s')", new Object[]{maintenanceModel.getUser_id(), maintenanceModel.getDirect_consumption(), maintenanceModel.getConsumption_id(), maintenanceModel.getMeter_reading(), maintenanceModel.getBuilding(), maintenanceModel.getShop(), maintenanceModel.getTenant(), maintenanceModel.getComment(), maintenanceModel.getMeter_number(), maintenanceModel.getLast_reading()}));
    }

    public JSONArray fetch_maintenance() {
        SQLiteDatabase readableDatabase = getReadableDatabase();
        JSONArray jSONArray = new JSONArray();
        Cursor rawQuery = readableDatabase.rawQuery("SELECT * FROM maintenance", new String[0]);
        rawQuery.moveToFirst();
        if (rawQuery.moveToFirst()) {
            do {
                JSONObject jSONObject = new JSONObject();
                try {
                    jSONObject.put("MeterNumber", rawQuery.getString(rawQuery.getColumnIndex("meter_number")));
                    jSONObject.put("LastReading", rawQuery.getString(rawQuery.getColumnIndex("last_reading")));
                    jSONObject.put("UserId", rawQuery.getString(rawQuery.getColumnIndex(SessionManager.KEY_USER_ID)));
                    jSONObject.put("DirectConsumption", rawQuery.getString(rawQuery.getColumnIndex("direct_consumption")));
                    jSONObject.put("ConsumptionId", rawQuery.getString(rawQuery.getColumnIndex("consumption_id")));
                    jSONObject.put("MeterReading", rawQuery.getString(rawQuery.getColumnIndex("meter_reading")));
                    jSONObject.put("Building", rawQuery.getString(rawQuery.getColumnIndex("building")));
                    jSONObject.put("Shop", rawQuery.getString(rawQuery.getColumnIndex("shop")));
                    jSONObject.put("Tenant", rawQuery.getString(rawQuery.getColumnIndex("tenant")));
                    jSONObject.put("Comment", rawQuery.getString(rawQuery.getColumnIndex("comment")));
                    jSONArray.put(jSONObject);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } while (rawQuery.moveToNext());
        }
        rawQuery.close();
        return jSONArray;
    }

    public void clear_maintenance() {
        SQLiteDatabase readableDatabase = getReadableDatabase();
        readableDatabase.delete("SQLITE_SEQUENCE", "NAME=?", new String[]{"maintenance"});
        readableDatabase.execSQL("DELETE FROM maintenance");
    }
}
