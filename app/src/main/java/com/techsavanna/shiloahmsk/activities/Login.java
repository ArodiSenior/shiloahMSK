package com.techsavanna.shiloahmsk.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.telecom.Call;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.techsavanna.shiloahmsk.R;
import com.techsavanna.shiloahmsk.api.BaseURL;
import com.techsavanna.shiloahmsk.database.Database;
import com.techsavanna.shiloahmsk.databinding.ActivityLoginBinding;
import com.techsavanna.shiloahmsk.models.UserModel;
import com.techsavanna.shiloahmsk.session.SessionManager;
import com.techsavanna.shiloahmsk.utils.NetworkState;

import org.json.JSONObject;

import java.io.IOException;
import java.io.PrintStream;
import java.util.concurrent.TimeUnit;

import cn.pedant.SweetAlert.SweetAlertDialog;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Login extends AppCompatActivity {
    ActivityLoginBinding binding;
    SessionManager session;
    SharedPreferences sharedPref;

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        binding = DataBindingUtil.setContentView(Login.this, R.layout.activity_login);
        session = new SessionManager(getApplicationContext());
        binding.Login.setOnClickListener( view -> {
            if (validate()) {
                try {
                    JSONObject jSONObject = new Database(getBaseContext()).get_users(binding.Username.getEditText().getText().toString().trim(), binding.Password.getEditText().getText().toString().trim());
                    PrintStream printStream = System.out;
                    printStream.println("arrayy" + jSONObject);
                    if (jSONObject.has(SessionManager.KEY_USER_ID)) {
                        if (this.binding.RememberMe.isChecked()) {
                            SharedPreferences.Editor edit = this.sharedPref.edit();
                            edit.putString("username", this.binding.Username.getEditText().getText().toString().trim());
                            edit.putString("password", this.binding.Password.getEditText().getText().toString().trim());
                            edit.apply();
                        } else {
                            SharedPreferences.Editor edit2 = this.sharedPref.edit();
                            edit2.clear();
                            edit2.apply();
                        }
                        this.session.createLoginSession(jSONObject.getString(SessionManager.KEY_USER_ID));
                        startActivity(new Intent(this, MainActivity.class));
                    } else if (NetworkState.getInstance(this).isConnected()) {
                        startActivity(new Intent(this, MainActivity.class));
                       // loginAdmin();
                    } else {
                        Toast.makeText(this, "No registration data found offline... Please connect to internet to proceed!!!", Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    PrintStream printStream2 = System.out;
                    printStream2.println("errrrr" + e.toString());
                    e.printStackTrace();
                }
            }
        });

        SharedPreferences sharedPreferences = getSharedPreferences("LOGIN_DETAILS", 0);
        this.sharedPref = sharedPreferences;
        if (sharedPreferences.contains("username") && this.sharedPref.contains("password")) {
            this.binding.Username.getEditText().setText(this.sharedPref.getString("username", ""));
            this.binding.Password.getEditText().setText(this.sharedPref.getString("password", ""));
            this.binding.RememberMe.setChecked(true);
        }
    }

    public void loginAdmin() {
        final SweetAlertDialog dialog = new SweetAlertDialog(Login.this, SweetAlertDialog.PROGRESS_TYPE);
        dialog.setCancelable(false);
        dialog.setTitleText("Please Wait...");
        dialog.show();

        OkHttpClient client = new OkHttpClient().newBuilder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .build();
        Request request = new Request.Builder()
                .url(BaseURL.SERVER_URL+"login?action=login_user&username=" + binding.Username.getEditText().getText().toString().trim() +
                "&password=" + binding.Password.getEditText().getText().toString().trim())
                .method("GET",  null)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(okhttp3.Call call, IOException e) {
                runOnUiThread(() -> {
                    dialog.dismiss();
                    Toast.makeText(Login.this, "An error occurred", Toast.LENGTH_LONG).show();
                    System.out.println("error" + e.toString());
                });
            }

            @Override
            public void onResponse(okhttp3.Call call, Response response) {
                runOnUiThread(() -> {
                    dialog.dismiss();
                    try {
                        JSONObject jSONObject = new JSONObject(response.body().string());
                        PrintStream printStream = System.out;
                        printStream.println("jsonObject" + jSONObject);
                        new Database(Login.this.getBaseContext()).clear_users();
                        if (jSONObject.getString("success_code").equals("1")) {
                            if (Login.this.binding.RememberMe.isChecked()) {
                                SharedPreferences.Editor edit = Login.this.sharedPref.edit();
                                edit.putString("username", Login.this.binding.Username.getEditText().getText().toString().trim());
                                edit.putString("password", Login.this.binding.Password.getEditText().getText().toString().trim());
                                edit.apply();
                            } else {
                                SharedPreferences.Editor edit2 = Login.this.sharedPref.edit();
                                edit2.clear();
                                edit2.apply();
                            }
                            new Database(Login.this.getBaseContext()).add_user(new UserModel(Login.this.binding.Username.getEditText().getText().toString().trim(), Login.this.binding.Password.getEditText().getText().toString().trim(), jSONObject.getString(SessionManager.KEY_USER_ID)));
                            Login.this.session.createLoginSession(jSONObject.getString(SessionManager.KEY_USER_ID));
                            Login.this.startActivity(new Intent(Login.this, MainActivity.class));
                            return;
                        }
                        Toast.makeText(Login.this, jSONObject.getString("message"), Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        Toast.makeText(Login.this, "An error occurred", Toast.LENGTH_LONG).show();
                        System.out.println("gdhdhgdsbcjbhd"+e.toString());
                        e.printStackTrace();
                    }
                });
            }

        });
    }

    public boolean validate() {
        boolean valid;
        if (this.binding.Username.getEditText().getText().toString().trim().equals("")) {
            this.binding.Username.setError("Please Enter Username");
            valid = false;
        } else {
            this.binding.Username.setError("");
            valid = true;
        }
        if (this.binding.Password.getEditText().getText().toString().trim().equals("")) {
            this.binding.Password.setError("Please Enter Password");
            return false;
        }
        this.binding.Password.setError("");
        return valid;
    }
}
