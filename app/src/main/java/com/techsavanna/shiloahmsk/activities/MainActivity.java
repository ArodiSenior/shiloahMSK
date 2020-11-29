package com.techsavanna.shiloahmsk.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.databinding.DataBindingUtil;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.techsavanna.shiloahmsk.R;
import com.techsavanna.shiloahmsk.databinding.ActivityMainBinding;
import com.techsavanna.shiloahmsk.fragments.MaintenanceFragment;

import java.util.List;

import at.nineyards.anyline.core.LicenseException;
import cn.pedant.SweetAlert.SweetAlertDialog;
import io.anyline.AnylineSDK;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    ActivityMainBinding binding;
    boolean doubleBackToExitPressedOnce = false;

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        checkPermissions();
        setSupportActionBar(this.binding.Maintoolbar);

        try {
            AnylineSDK.init(getString(R.string.anyline_license_key), this);
        } catch (Exception e) {
            System.out.println("cncncjjjf"+e);
        }

        this.binding.LogoutButton.setOnClickListener(this);
        this.binding.Maintoolbar.setNavigationOnClickListener(new NavigationIconClickListener(this, this.binding.FrameLayout, new LinearInterpolator(), ResourcesCompat.getDrawable(getResources(), R.drawable.ic_sort, (Resources.Theme) null), ResourcesCompat.getDrawable(getResources(), R.drawable.close_menu, null)));
        getSupportFragmentManager().beginTransaction().replace(R.id.FrameLayout, new MaintenanceFragment()).commit();
    }

    public int stackCount() {
        return getSupportFragmentManager().getBackStackEntryCount();
    }

    public void onBackPressed() {
        if (!(getSupportFragmentManager().findFragmentById(R.id.FrameLayout) instanceof MaintenanceFragment)) {
            getSupportFragmentManager().beginTransaction().replace(R.id.FrameLayout, new MaintenanceFragment()).commit();
        } else if (!this.doubleBackToExitPressedOnce || stackCount() != 0) {
            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(this, "Press back again to logout", Toast.LENGTH_LONG).show();
            new Handler().postDelayed(new Runnable() {
                public void run() {
                    MainActivity.this.doubleBackToExitPressedOnce = false;
                }
            }, 3500);
        } else {
            super.onBackPressed();
            finish();
            return;
        }
        if (stackCount() != 0) {
            super.onBackPressed();
        }
    }

    public void checkPermissions() {
        Dexter.withActivity(this).withPermissions("android.permission.CAMERA", "android.permission.VIBRATE").withListener(new MultiplePermissionsListener() {
            public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {
                multiplePermissionsReport.areAllPermissionsGranted();
                if (multiplePermissionsReport.isAnyPermissionPermanentlyDenied()) {
                    //Toast.makeText(MainActivity.this, "You should accept permission", Toast.LENGTH_LONG).show();
                }
            }

            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {
                permissionToken.continuePermissionRequest();
            }
        }).withErrorListener(new PermissionRequestErrorListener() {
            public void onError(DexterError dexterError) {
                MainActivity mainActivity = MainActivity.this;
                Toast.makeText(mainActivity, "Error occurred!" + dexterError.toString(), Toast.LENGTH_LONG).show();
            }
        }).check();
    }

    public void onClick(View view) {
        if (view.getId() == R.id.LogoutButton) {
            new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE).setTitleText("You are about to sign out!").setConfirmText("Sign Out!").setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                public void onClick(SweetAlertDialog sweetAlertDialog) {
                    Intent intent = new Intent(MainActivity.this, Login.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    MainActivity.this.startActivity(intent);
                    MainActivity.this.finish();
                }
            }).show();
        }
    }
}
