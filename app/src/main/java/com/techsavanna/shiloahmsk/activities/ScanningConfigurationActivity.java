package com.techsavanna.shiloahmsk.activities;

import android.content.Context;
import android.graphics.Bitmap;
import androidx.appcompat.app.AppCompatActivity;

import at.nineyards.anyline.models.AnylineScanResult;
import io.anyline.view.ScanView;

public abstract class ScanningConfigurationActivity extends AppCompatActivity {
    public static void setupScanProcessView(Context context, String str, ScanModuleEnum.ScanModule scanModule, Bitmap bitmap) {
    }

    public static void setupScanProcessView(Context context, String str, ScanModuleEnum.ScanModule scanModule, String str2, Bitmap bitmap) {
    }

    public abstract ScanModuleEnum.ScanModule getScanModule();
    public abstract ScanView getScanView();
    public <T extends AnylineScanResult> void setupScanProcessView(Context context, T t, ScanModuleEnum.ScanModule scanModule) {
    }
    public void setupScanResult() {
    }
}
