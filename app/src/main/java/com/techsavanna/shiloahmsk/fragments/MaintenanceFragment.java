package com.techsavanna.shiloahmsk.fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.google.android.material.button.MaterialButtonToggleGroup;
import com.techsavanna.shiloahmsk.R;
import com.techsavanna.shiloahmsk.activities.ScanAutoAnalogDigitalMeterActivity;
import com.techsavanna.shiloahmsk.api.BaseURL;
import com.techsavanna.shiloahmsk.database.Database;
import com.techsavanna.shiloahmsk.databinding.FragmentMaintenanceBinding;
import com.techsavanna.shiloahmsk.models.BuildingModel;
import com.techsavanna.shiloahmsk.models.MaintenanceModel;
import com.techsavanna.shiloahmsk.models.ShopModel;
import com.techsavanna.shiloahmsk.models.TenantModel;
import com.techsavanna.shiloahmsk.session.SessionManager;
import com.techsavanna.shiloahmsk.utils.BitmapUtil;
import com.techsavanna.shiloahmsk.utils.NetworkState;

import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import cn.pedant.SweetAlert.SweetAlertDialog;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import org.json.JSONArray;
import org.json.JSONObject;

public class MaintenanceFragment extends Fragment {
    FragmentMaintenanceBinding binding;
    Bitmap bitmap;
    ArrayList<String> buildingList = new ArrayList<>();
    ArrayList<BuildingModel> buildingModel = new ArrayList<>();
    public String consumption_id;
    SweetAlertDialog dialog;
    public String direct_consumption;
    public String selected_building;
    public String selected_shop;
    public String selected_tenant;
    SessionManager sessionManager;
    SharedPreferences sharedPreferences;
    ArrayList<String> shopList = new ArrayList<>();
    ArrayList<ShopModel> shopModel = new ArrayList<>();
    ArrayList<String> tenantList = new ArrayList<>();
    ArrayList<TenantModel> tenantModel = new ArrayList<>();
    HashMap<String, String> user;

    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        this.binding = DataBindingUtil.inflate(layoutInflater, R.layout.fragment_maintenance, viewGroup, false);
        this.direct_consumption = "Direct Electricity Consumption";
        this.consumption_id = "29";
        if (getActivity() != null) {
            if (NetworkState.getInstance(getActivity()).isConnected()) {
                fetchBuildings();
                fetchMaintenance();
            } else {
                populateBuildings();
            }
        }
        this.binding.toggleGroup.addOnButtonCheckedListener((materialButtonToggleGroup, i, z) -> { if (i != R.id.Electricity) {
            if (i == R.id.Water && z) {
                this.direct_consumption = "Direct Water Consumption";
                this.consumption_id = "34";
                if (getActivity() == null) {
                    return;
                }
                if (NetworkState.getInstance(getActivity()).isConnected()) {
                    fetchBuildings();
                } else {
                    populateBuildings();
                }
            }
        } else if (z) {
            this.direct_consumption = "Direct Electricity Consumption";
            this.consumption_id = "29";
            if (getActivity() == null) {
                return;
            }
            if (NetworkState.getInstance(getActivity()).isConnected()) {
                fetchBuildings();
            } else {
                populateBuildings();
            }
        }});
        this.sessionManager = new SessionManager(getActivity());
        this.user = new SessionManager(getActivity()).getLoginDetails();
        this.binding.Building.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onNothingSelected(AdapterView<?> adapterView) {
            }

            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long j) {
                BuildingModel buildingModel = MaintenanceFragment.this.buildingModel.get(MaintenanceFragment.this.binding.Building.getSelectedItemPosition());
                MaintenanceFragment.this.selected_building = buildingModel.getId();
                if (MaintenanceFragment.this.getActivity() == null) {
                    return;
                }
                if (NetworkState.getInstance(MaintenanceFragment.this.getActivity()).isConnected()) {
                    MaintenanceFragment.this.fetchShops(buildingModel.getId());
                } else {
                    MaintenanceFragment.this.populateShops(buildingModel.getId());
                }
            }
        });
        this.binding.ScanMeter.setOnClickListener(view -> startActivity(new Intent(getActivity(), ScanAutoAnalogDigitalMeterActivity.class)));
        this.binding.Shop.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onNothingSelected(AdapterView<?> adapterView) {
            }

            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long j) {
                ShopModel shopModel = MaintenanceFragment.this.shopModel.get(MaintenanceFragment.this.binding.Shop.getSelectedItemPosition());
                MaintenanceFragment.this.selected_shop = shopModel.getId();
                if (MaintenanceFragment.this.getActivity() == null) {
                    return;
                }
                if (NetworkState.getInstance(MaintenanceFragment.this.getActivity()).isConnected()) {
                    MaintenanceFragment.this.fetchTenants(shopModel.getId());
                } else {
                    MaintenanceFragment.this.populateTenants(shopModel.getId());
                }
            }
        });
        this.binding.Tenant.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onNothingSelected(AdapterView<?> adapterView) {
            }

            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long j) {
                TenantModel tenantModel = MaintenanceFragment.this.tenantModel.get(MaintenanceFragment.this.binding.Tenant.getSelectedItemPosition());
                MaintenanceFragment.this.selected_tenant = tenantModel.getId();
                if (MaintenanceFragment.this.consumption_id.equals("29")) {
                    MaintenanceFragment.this.binding.LastMeterReading.getEditText().setText(tenantModel.getLast_electricity_reading());
                    if (!tenantModel.getElectricity_meter_number().isEmpty()) {
                        MaintenanceFragment.this.binding.MeterNumber.getEditText().setText(tenantModel.getElectricity_meter_number());
                        return;
                    }
                    return;
                }
                MaintenanceFragment.this.binding.LastMeterReading.getEditText().setText(tenantModel.getLast_water_reading());
                if (!tenantModel.getWater_meter_number().isEmpty()) {
                    MaintenanceFragment.this.binding.MeterNumber.getEditText().setText(tenantModel.getWater_meter_number());
                }
            }
        });
        this.binding.Submit.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                if (validate()) {
                    dialog = new SweetAlertDialog(getActivity(), SweetAlertDialog.PROGRESS_TYPE);
                    dialog.setCancelable(false);
                    dialog.setTitleText("Submitting Info...");
                    dialog.show();

                    if (getActivity() != null) {
                        if (NetworkState.getInstance(getActivity()).isConnected()) {
                            submitMaintenance();
                        } else {
                            submitOfflineMaintenance();
                        }
                    }
                }
            }
        });
        return binding.getRoot();
    }



    public void onResume() {
        super.onResume();
        if (getActivity() != null) {
            SharedPreferences sharedPreferences2 = getActivity().getSharedPreferences("METER_READING", 0);
            this.sharedPreferences = sharedPreferences2;
            if (sharedPreferences2.contains("READING") && this.sharedPreferences.contains("IMAGE")) {
                this.binding.MeterReading.getEditText().setText(this.sharedPreferences.getString("READING", ""));
                this.binding.MeterReading.getEditText().setFocusable(false);
                this.bitmap = BitmapUtil.getBitmap(this.sharedPreferences.getString("IMAGE", ""));
                this.binding.MeterImage.setImageBitmap(this.bitmap);
            }
        }
    }

    private void fetchBuildings() {
        this.buildingList.clear();
        this.buildingModel.clear();
        this.shopList.clear();
        this.shopModel.clear();
        this.tenantList.clear();
        this.tenantModel.clear();
        this.binding.LastMeterReading.getEditText().setText((CharSequence) null);
        this.binding.MeterNumber.getEditText().setText((CharSequence) null);
        new OkHttpClient().newBuilder().connectTimeout(60, TimeUnit.SECONDS).readTimeout(60, TimeUnit.SECONDS).writeTimeout(60, TimeUnit.SECONDS).build().newCall(new Request.Builder().url("http://197.254.14.222/shiloahmsk/asset?action=fetch_buildings").method("GET", (RequestBody) null).build()).enqueue(new Callback() {
            public void onFailure(Call call, final IOException iOException) {
                if (MaintenanceFragment.this.getActivity() != null) {
                    MaintenanceFragment.this.getActivity().runOnUiThread(new Runnable() {
                        public void run() {
                            MaintenanceFragment.this.binding.LoadBuildings.setVisibility(View.VISIBLE);
                            MaintenanceFragment.this.binding.LoadBuildings.setText("Your request for buildings failed!");
                            MaintenanceFragment.this.binding.LoadShops.setVisibility(View.VISIBLE);
                            MaintenanceFragment.this.binding.LoadShops.setText("Your request for shops failed!");
                            MaintenanceFragment.this.binding.LoadTenants.setVisibility(View.VISIBLE);
                            MaintenanceFragment.this.binding.LoadTenants.setText("Your request for tenants failed!");
                            PrintStream printStream = System.out;
                            printStream.println("error" + iOException.toString());
                        }
                    });
                }
            }

            public void onResponse(Call call, final Response response) throws IOException {
                if (MaintenanceFragment.this.getActivity() != null) {
                    MaintenanceFragment.this.getActivity().runOnUiThread(new Runnable() {
                        public void run() {
                            try {
                                JSONArray jSONArray = new JSONArray(response.body().string());
                                PrintStream printStream = System.out;
                                printStream.println("jsonArray" + jSONArray);
                                for (int i = 0; i < jSONArray.length(); i++) {
                                    JSONObject jSONObject = jSONArray.getJSONObject(i);
                                    BuildingModel buildingModel = new BuildingModel(jSONObject.getString("id"), jSONObject.getString("name"));
                                    new Database(MaintenanceFragment.this.getActivity().getBaseContext()).add_building(buildingModel);
                                    MaintenanceFragment.this.buildingModel.add(buildingModel);
                                }
                                MaintenanceFragment.this.buildingAdapter();
                            } catch (Exception e) {
                                MaintenanceFragment.this.binding.LoadBuildings.setVisibility(View.VISIBLE);
                                MaintenanceFragment.this.binding.LoadBuildings.setText("Your request for buildings failed!");
                                MaintenanceFragment.this.binding.LoadShops.setVisibility(View.VISIBLE);
                                MaintenanceFragment.this.binding.LoadShops.setText("Your request for shops failed!");
                                MaintenanceFragment.this.binding.LoadTenants.setVisibility(View.VISIBLE);
                                MaintenanceFragment.this.binding.LoadTenants.setText("Your request for tenants failed!");
                                PrintStream printStream2 = System.out;
                                printStream2.println("error" + e.toString());
                                e.printStackTrace();
                            }
                        }
                    });
                }
            }
        });
    }

    public void buildingAdapter() {
        if (getActivity() != null) {
            for (int i = 0; i < this.buildingModel.size(); i++) {
                this.buildingList.add(this.buildingModel.get(i).getName());
            }
            this.binding.LoadBuildings.setVisibility(View.GONE);
            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, this.buildingList);
            arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            this.binding.Building.setAdapter(arrayAdapter);
            if (this.buildingList.size() == 0) {
                this.binding.LoadBuildings.setVisibility(View.VISIBLE);
                this.binding.LoadBuildings.setText("No building available!");
                this.binding.LoadShops.setVisibility(View.VISIBLE);
                this.binding.LoadShops.setText("No shop available!");
                this.binding.LoadTenants.setVisibility(View.VISIBLE);
                this.binding.LoadTenants.setText("No tenant available!");
                return;
            }
            this.binding.LoadBuildings.setVisibility(View.GONE);
        }
    }

    public void fetchShops(String building_id) {
        this.shopList.clear();
        this.shopModel.clear();
        this.tenantList.clear();
        this.tenantModel.clear();
        this.binding.LastMeterReading.getEditText().setText((CharSequence) null);
        this.binding.MeterNumber.getEditText().setText((CharSequence) null);
        OkHttpClient build = new OkHttpClient().newBuilder().connectTimeout(60, TimeUnit.SECONDS).readTimeout(60, TimeUnit.SECONDS).writeTimeout(60, TimeUnit.SECONDS).build();
        Request.Builder builder = new Request.Builder();
        build.newCall(
                builder.url(BaseURL.SERVER_URL+"asset?action=fetch_shops&building_id=" + building_id)
                        .method("GET", null)
                        .build())
                .enqueue(new Callback() {
            public void onFailure(Call call, final IOException iOException) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(new Runnable() {
                        public void run() {
                            MaintenanceFragment.this.binding.LoadShops.setVisibility(View.VISIBLE);
                            MaintenanceFragment.this.binding.LoadShops.setText("Your request for shops failed!");
                            MaintenanceFragment.this.binding.LoadTenants.setVisibility(View.VISIBLE);
                            MaintenanceFragment.this.binding.LoadTenants.setText("Your request for tenants failed!");
                            PrintStream printStream = System.out;
                            printStream.println("error" + iOException.toString());
                        }
                    });
                }
            }

            public void onResponse(Call call, final Response response) throws IOException {
                if (MaintenanceFragment.this.getActivity() != null) {
                    MaintenanceFragment.this.getActivity().runOnUiThread(new Runnable() {
                        public void run() {
                            try {
                                JSONArray jSONArray = new JSONArray(response.body().string());
                                PrintStream printStream = System.out;
                                printStream.println("jsonArray" + jSONArray);
                                for (int i = 0; i < jSONArray.length(); i++) {
                                    JSONObject jSONObject = jSONArray.getJSONObject(i);
                                    ShopModel shopModel = new ShopModel(
                                            jSONObject.getString("id"),
                                            jSONObject.getString("code"),
                                            jSONObject.getString("building_id"));
                                    MaintenanceFragment.this.shopModel.add(shopModel);
                                    new Database(MaintenanceFragment.this.getActivity().getBaseContext()).add_shop(shopModel);
                                }
                                MaintenanceFragment.this.shopAdapter();
                            } catch (Exception e) {
                                MaintenanceFragment.this.binding.LoadShops.setVisibility(View.VISIBLE);
                                MaintenanceFragment.this.binding.LoadShops.setText("Your request for shops failed!");
                                MaintenanceFragment.this.binding.LoadTenants.setVisibility(View.VISIBLE);
                                MaintenanceFragment.this.binding.LoadTenants.setText("Your request for tenants failed!");
                                PrintStream printStream2 = System.out;
                                printStream2.println("error" + e.toString());
                                e.printStackTrace();
                            }
                        }
                    });
                }
            }
        });
    }

    public void shopAdapter() {
        if (getActivity() != null) {
            for (int i = 0; i < this.shopModel.size(); i++) {
                this.shopList.add(this.shopModel.get(i).getCode());
            }
            this.binding.LoadShops.setVisibility(View.GONE);
            ArrayAdapter<String> arrayAdapter = new ArrayAdapter(getActivity(), android.R.layout.simple_spinner_item, this.shopList);
            arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            this.binding.Shop.setAdapter(arrayAdapter);
            if (this.shopList.size() == 0) {
                this.binding.LoadShops.setVisibility(View.VISIBLE);
                this.binding.LoadShops.setText("No shop available!");
                this.binding.LoadTenants.setVisibility(View.VISIBLE);
                this.binding.LoadTenants.setText("No tenant available!");
                return;
            }
            this.binding.LoadShops.setVisibility(View.GONE);
        }
    }

    public void fetchTenants(String shop_id) {
        this.tenantList.clear();
        this.tenantModel.clear();
        this.binding.LastMeterReading.getEditText().setText(null);
        this.binding.MeterNumber.getEditText().setText(null);
        OkHttpClient build = new OkHttpClient().newBuilder().connectTimeout(60, TimeUnit.SECONDS).readTimeout(60, TimeUnit.SECONDS).writeTimeout(60, TimeUnit.SECONDS).build();
        Request.Builder builder = new Request.Builder();
        build.newCall(builder.url(BaseURL.SERVER_URL+"asset?action=fetch_tenants&shop_id=" + shop_id)
                .method("GET",null)
                .build())
                .enqueue(new Callback() {
            public void onFailure(Call call, final IOException iOException) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                       binding.LoadTenants.setVisibility(View.VISIBLE);
                        binding.LoadTenants.setText("Your request for tenants failed!");
                        System.out.println("error" + iOException.toString());
                    });
                }
            }

            public void onResponse(Call call, final Response response) throws IOException {
                if (MaintenanceFragment.this.getActivity() != null) {
                    MaintenanceFragment.this.getActivity().runOnUiThread(new Runnable() {
                        public void run() {
                            try {
                                JSONArray jSONArray = new JSONArray(response.body().string());
                                PrintStream printStream = System.out;
                                printStream.println("jsonArray" + jSONArray);
                                for (int i = 0; i < jSONArray.length(); i++) {
                                    JSONObject jSONObject = jSONArray.getJSONObject(i);
                                    TenantModel tenantModel = new TenantModel(jSONObject.getString("id"), jSONObject.getString("code"), jSONObject.getString("last_water_reading"), jSONObject.getString("last_electricity_reading"), jSONObject.getString("shop_id"), jSONObject.getString("water_meter_number"), jSONObject.getString("electricity_meter_number"));
                                    MaintenanceFragment.this.tenantModel.add(tenantModel);
                                    new Database(MaintenanceFragment.this.getActivity().getBaseContext()).add_tenant(tenantModel);
                                }
                                MaintenanceFragment.this.tenantAdapter();
                            } catch (Exception e) {
                                MaintenanceFragment.this.binding.LoadTenants.setVisibility(View.VISIBLE);
                                MaintenanceFragment.this.binding.LoadTenants.setText("Your request for tenants failed!");
                                PrintStream printStream2 = System.out;
                                printStream2.println("error" + e.toString());
                                e.printStackTrace();
                            }
                        }
                    });
                }
            }
        });
    }

    public void tenantAdapter() {
        for (int i = 0; i < this.tenantModel.size(); i++) {
            this.tenantList.add(this.tenantModel.get(i).getCode());
        }
        this.binding.LoadTenants.setVisibility(View.GONE);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, this.tenantList);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        this.binding.Tenant.setAdapter(arrayAdapter);
        if (this.tenantList.size() == 0) {
            this.binding.LoadTenants.setVisibility(View.VISIBLE);
            this.binding.LoadTenants.setText("No tenant available!");
            return;
        }
        this.binding.LoadTenants.setVisibility(View.GONE);
    }

    public boolean validate() {
        boolean z;
        if (this.binding.MeterReading.getEditText().getText().toString().trim().equals("")) {
            this.binding.MeterReading.setError("Please input meter reading");
            z = false;
        } else {
            this.binding.MeterReading.setError("");
            z = true;
        }
        if (this.binding.MeterNumber.getEditText().getText().toString().trim().equals("")) {
            this.binding.MeterNumber.setError("Please input meter number");
            z = false;
        } else {
            this.binding.MeterNumber.setError("");
        }
        if (this.binding.Comments.getEditText().getText().toString().trim().equals("")) {
            this.binding.Comments.setError("Please input comment");
            z = false;
        } else {
            this.binding.Comments.setError("");
        }
        if (this.binding.Building.getSelectedItem() == null) {
            Toast.makeText(getActivity(), "Please select building", Toast.LENGTH_LONG).show();
            z = false;
        }
        if (this.binding.Shop.getSelectedItem() == null) {
            Toast.makeText(getActivity(), "Please select shop", Toast.LENGTH_LONG).show();
            z = false;
        }
        if (this.binding.Tenant.getSelectedItem() == null) {
            Toast.makeText(getActivity(), "Please select tenant", Toast.LENGTH_LONG).show();
            z = false;
        }
        if (!this.binding.MeterReading.getEditText().getText().toString().trim().isEmpty()) {
            if (Double.compare(ParseDouble(this.binding.MeterReading.getEditText().getText().toString().trim()), ParseDouble(this.binding.LastMeterReading.getEditText().getText().toString().trim())) < 0) {
                this.binding.MeterReading.setError("Meter reading cannot be null or less than the previous reading");
                return false;
            }
            this.binding.MeterReading.setError("");
        }
        return z;
    }

    public double ParseDouble(String str) {
        if (str == null || str.length() <= 0) {
            return 0.0d;
        }
        try {
            return Double.parseDouble(str);
        } catch (Exception unused) {
            return -1.0d;
        }
    }

    public void submitMaintenance() {
        if (getActivity() != null) {
            JSONArray jSONArray = new JSONArray();
            JSONObject jSONObject = new JSONObject();
            try {
                jSONObject.put("MeterNumber", this.binding.MeterNumber.getEditText().getText().toString().trim());
                jSONObject.put("UserId", this.user.get(SessionManager.KEY_USER_ID));
                jSONObject.put("LastReading", this.binding.LastMeterReading.getEditText().getText().toString().trim());
                jSONObject.put("DirectConsumption", this.direct_consumption);
                jSONObject.put("ConsumptionId", this.consumption_id);
                jSONObject.put("MeterReading", this.binding.MeterReading.getEditText().getText().toString().trim());
                jSONObject.put("Building", this.selected_building);
                jSONObject.put("Shop", this.selected_shop);
                jSONObject.put("Tenant", this.selected_tenant);
                jSONObject.put("Comment", this.binding.Comments.getEditText().getText().toString().trim());
                jSONArray.put(jSONObject);
                PrintStream printStream = System.out;
                printStream.println("hdhsjhgfhdd" + jSONArray.toString());
            } catch (Exception e) {
                e.printStackTrace();
            }
            new OkHttpClient().newBuilder().connectTimeout(60, TimeUnit.SECONDS).readTimeout(60, TimeUnit.SECONDS).writeTimeout(60, TimeUnit.SECONDS).build().newCall(new Request.Builder().url("http://197.254.14.222/shiloahmsk/asset?action=create_maintenance").method("POST", RequestBody.create(MediaType.parse("application/json"), jSONArray.toString())).build()).enqueue(new Callback() {
                public void onFailure(Call call, final IOException iOException) {
                    if (MaintenanceFragment.this.getActivity() != null) {
                        MaintenanceFragment.this.getActivity().runOnUiThread(new Runnable() {
                            public void run() {
                                MaintenanceFragment.this.dialog.changeAlertType(1);
                                MaintenanceFragment.this.dialog.setTitle((CharSequence) "No connection to host");
                                MaintenanceFragment.this.dialog.setConfirmText("OK").setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                                        sweetAlertDialog.dismiss();
                                    }
                                });
                                PrintStream printStream = System.out;
                                printStream.println("error" + iOException.toString());
                            }
                        });
                    }
                }

                public void onResponse(Call call, final Response response) {
                    if (MaintenanceFragment.this.getActivity() != null) {
                        MaintenanceFragment.this.getActivity().runOnUiThread(new Runnable() {
                            public void run() {
                                try {
                                    JSONObject jSONObject = new JSONObject(response.body().string());
                                    PrintStream printStream = System.out;
                                    printStream.println("jsonObject" + jSONObject);
                                    if (jSONObject.getString("success").equals("1")) {
                                        MaintenanceFragment.this.dialog.changeAlertType(2);
                                        MaintenanceFragment.this.dialog.setTitle((CharSequence) jSONObject.getString("message"));
                                        MaintenanceFragment.this.dialog.setConfirmText("OK").setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                                sweetAlertDialog.dismiss();
                                                SharedPreferences.Editor edit = MaintenanceFragment.this.sharedPreferences.edit();
                                                edit.clear();
                                                edit.apply();
                                                if (MaintenanceFragment.this.getFragmentManager() != null) {
                                                    MaintenanceFragment.this.getFragmentManager().beginTransaction().replace(R.id.FrameLayout, new MaintenanceFragment()).commit();
                                                }
                                            }
                                        });
                                        return;
                                    }
                                    MaintenanceFragment.this.dialog.changeAlertType(1);
                                    MaintenanceFragment.this.dialog.setTitle((CharSequence) jSONObject.getString("message"));
                                    MaintenanceFragment.this.dialog.setConfirmText("OK").setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                                            sweetAlertDialog.dismiss();
                                        }
                                    });
                                } catch (Exception e) {
                                    PrintStream printStream2 = System.out;
                                    printStream2.println("errorooc" + e.toString());
                                    MaintenanceFragment.this.dialog.changeAlertType(1);
                                    MaintenanceFragment.this.dialog.setTitle((CharSequence) "An error occurred");
                                    MaintenanceFragment.this.dialog.setConfirmText("OK").setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                                            sweetAlertDialog.dismiss();
                                        }
                                    });
                                    e.printStackTrace();
                                }
                            }
                        });
                    }
                }
            });
        }
    }

    public void fetchMaintenance() {
        if (getActivity() != null) {
            JSONArray fetch_maintenance = new Database(getActivity().getBaseContext()).fetch_maintenance();
            PrintStream printStream = System.out;
            printStream.println("hdycbncjdjcjj" + fetch_maintenance);
            if (fetch_maintenance.length() != 0) {
                new OkHttpClient().newBuilder().connectTimeout(60, TimeUnit.SECONDS).readTimeout(60, TimeUnit.SECONDS).writeTimeout(60, TimeUnit.SECONDS).build().newCall(new Request.Builder().url("http://197.254.14.222/shiloahmsk/asset?action=create_maintenance").method("POST", RequestBody.create(MediaType.parse("application/json"), fetch_maintenance.toString())).build()).enqueue(new Callback() {
                    public void onFailure(Call call, final IOException iOException) {
                        if (MaintenanceFragment.this.getActivity() != null) {
                            MaintenanceFragment.this.getActivity().runOnUiThread(new Runnable() {
                                public void run() {
                                    PrintStream printStream = System.out;
                                    printStream.println("error" + iOException.toString());
                                }
                            });
                        }
                    }

                    public void onResponse(Call call, final Response response) {
                        if (MaintenanceFragment.this.getActivity() != null) {
                            MaintenanceFragment.this.getActivity().runOnUiThread(new Runnable() {
                                public void run() {
                                    try {
                                        JSONObject jSONObject = new JSONObject(response.body().string());
                                        PrintStream printStream = System.out;
                                        printStream.println("jsonObjectuuyggh" + jSONObject);
                                        if (jSONObject.getString("success").equals("1")) {
                                            new Database(MaintenanceFragment.this.getActivity().getBaseContext()).clear_maintenance();
                                        }
                                    } catch (Exception e) {
                                        PrintStream printStream2 = System.out;
                                        printStream2.println("errorooc" + e.toString());
                                        e.printStackTrace();
                                    }
                                }
                            });
                        }
                    }
                });
            }
        }
    }

    public void populateTenants(String str) {
        if (getActivity() != null) {
            this.tenantList.clear();
            this.tenantModel.clear();
            this.binding.LastMeterReading.getEditText().setText((CharSequence) null);
            this.binding.MeterNumber.getEditText().setText((CharSequence) null);
            PrintStream printStream = System.out;
            printStream.println("nbxhvxghb" + str);
            this.tenantModel = (ArrayList) new Database(getActivity().getBaseContext()).fetch_tenant(str);
            tenantAdapter();
        }
    }

    public void populateShops(String str) {
        if (getActivity() != null) {
            this.shopList.clear();
            this.shopModel.clear();
            this.tenantList.clear();
            this.tenantModel.clear();
            this.binding.LastMeterReading.getEditText().setText((CharSequence) null);
            this.binding.MeterNumber.getEditText().setText((CharSequence) null);
            this.shopModel = (ArrayList) new Database(getActivity().getBaseContext()).fetch_shop(str);
            shopAdapter();
        }
    }

    public void populateBuildings() {
        if (getActivity() != null) {
            this.buildingList.clear();
            this.buildingModel.clear();
            this.shopList.clear();
            this.shopModel.clear();
            this.tenantList.clear();
            this.tenantModel.clear();
            this.binding.LastMeterReading.getEditText().setText((CharSequence) null);
            this.binding.MeterNumber.getEditText().setText((CharSequence) null);
            this.buildingModel = (ArrayList) new Database(getActivity().getBaseContext()).fetch_building();
            buildingAdapter();
        }
    }

    public void submitOfflineMaintenance() {
        if (getActivity() != null) {
            new Database(getActivity().getBaseContext()).add_maintenance(new MaintenanceModel(this.user.get(SessionManager.KEY_USER_ID), this.direct_consumption, this.consumption_id, this.binding.MeterReading.getEditText().getText().toString().trim(), this.selected_building, this.selected_shop, this.selected_tenant, this.binding.Comments.getEditText().getText().toString().trim(), this.binding.MeterNumber.getEditText().getText().toString().trim(), this.binding.LastMeterReading.getEditText().getText().toString().trim()));
            this.dialog.changeAlertType(2);
            this.dialog.setTitle((CharSequence) "Meter reading submitted successfully");
            this.dialog.setConfirmText("OK").setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                public void onClick(SweetAlertDialog sweetAlertDialog) {
                    sweetAlertDialog.dismiss();
                    SharedPreferences.Editor edit = MaintenanceFragment.this.sharedPreferences.edit();
                    edit.clear();
                    edit.apply();
                    if (MaintenanceFragment.this.getFragmentManager() != null) {
                        MaintenanceFragment.this.getFragmentManager().beginTransaction().replace(R.id.FrameLayout, new MaintenanceFragment()).commit();
                    }
                }
            });
        }
    }
}
