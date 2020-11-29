package com.techsavanna.shiloahmsk.activities;

import android.os.Bundle;
import android.view.MenuItem;
import com.techsavanna.shiloahmsk.R;

import java.io.File;
import java.io.IOException;

import at.nineyards.anyline.models.AnylineImage;

public abstract class ScanActivity extends ScanningConfigurationActivity {
    protected long timeStarted;
    public abstract ScanModuleEnum.ScanModule getScanModule();
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView((int) R.layout.activity_scan);
        getWindow().addFlags(128);
    }
    public void onResume() {
        super.onResume();
        resetTime();
    }

    /* access modifiers changed from: protected */
    public void resetTime() {
        this.timeStarted = System.currentTimeMillis();
    }

    /* access modifiers changed from: protected */
    public long milliSecondsPassedSinceStartedScanning() {
        return System.currentTimeMillis() - this.timeStarted;
    }

    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.fade_in, R.anim.activity_close_translate);
    }

    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() != 16908332) {
            return super.onOptionsItemSelected(menuItem);
        }
        finish();
        overridePendingTransition(R.anim.fade_in, R.anim.activity_close_translate);
        return true;
    }

    /* access modifiers changed from: protected */
    public void onPause() {
        super.onPause();
    }

    /* access modifiers changed from: protected */
    public String setupImagePath(AnylineImage anylineImage) {
        String str = "";
        long currentTimeMillis = System.currentTimeMillis();
        try {
            if (getExternalFilesDir((String) null) != null) {
                str = getExternalFilesDir((String) null).toString() + "/results/mrz_image" + currentTimeMillis;
            } else if (getFilesDir() != null) {
                str = getFilesDir().toString() + "/results/mrz_image" + currentTimeMillis;
            }
            File file = new File(str);
            file.mkdirs();
            anylineImage.save(file, 100);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e2) {
            e2.printStackTrace();
        }
        return str;
    }
}
