package com.techsavanna.shiloahmsk.activities;

public class ScanModuleEnum {
    private ScanModuleEnum scanModule;

    public enum ScanModule {
        BARCODE,
        MRZ,
        IBAN,
        RED_BULL_CODE,
        ISBN,
        DOCUMENT,
        BOTTLECAP,
        RECORD,
        SCRABBLE,
        VOUCHER,
        ENERGY_ELECTRIC_DIGITS,
        ENERGY_AUTO_ANALOG_DIGITAL,
        ENERGY_SERIAL_NUMBER,
        LICENSE_PLATE,
        DRIVER_LICENSE,
        GERMAN_ID_FRONT,
        VEHICLE_IDENTIFICATION_NUMBER,
        SHIPPING_CONTAINER,
        TIN,
        OCR,
        UID,
        ENERGY_ELECTRIC_BACKGROUND,
        ENERGY_GAS,
        ENERGY_WATER_METER,
        ENERGY_HEAT_METER,
        ENERGY_DIGITAL_METER,
        ENERGY_DIAL_METER
    }

    public ScanModuleEnum getScanModule() {
        return this.scanModule;
    }

    public void setScanModule(ScanModuleEnum scanModuleEnum) {
        this.scanModule = scanModuleEnum;
    }
}
