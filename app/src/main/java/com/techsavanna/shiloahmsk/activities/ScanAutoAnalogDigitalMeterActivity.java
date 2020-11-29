package com.techsavanna.shiloahmsk.activities;

import android.os.Bundle;

import io.anyline.plugin.meter.MeterScanMode;
import io.anyline.plugin.meter.MeterScanViewPlugin;
import io.anyline.view.ScanView;

public class ScanAutoAnalogDigitalMeterActivity extends AbstractEnergyActivity {
    public ScanView getScanView() {
        return null;
    }

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        ((MeterScanViewPlugin) this.energyScanView.getScanViewPlugin()).setScanMode(MeterScanMode.AUTO_ANALOG_DIGITAL_METER);
    }

    public ScanModuleEnum.ScanModule getScanModule() {
        return ScanModuleEnum.ScanModule.ENERGY_AUTO_ANALOG_DIGITAL;
    }
}
