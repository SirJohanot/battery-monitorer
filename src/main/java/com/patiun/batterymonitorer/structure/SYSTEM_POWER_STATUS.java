package com.patiun.batterymonitorer.structure;

import com.sun.jna.Structure;

import java.util.ArrayList;
import java.util.List;

public class SYSTEM_POWER_STATUS extends Structure {

    public byte ACLineStatus;
    public byte BatteryFlag;
    public byte BatteryLifePercent;
    public byte SystemStatusFlag;
    public int BatteryLifeTime;
    public int BatteryFullLifeTime;

    @Override
    protected List<String> getFieldOrder() {
        ArrayList<String> fields = new ArrayList<>();
        fields.add("ACLineStatus");
        fields.add("BatteryFlag");
        fields.add("BatteryLifePercent");
        fields.add("SystemStatusFlag");
        fields.add("BatteryLifeTime");
        fields.add("BatteryFullLifeTime");
        return fields;
    }

    public String getACLineStatusString() {
        return switch (ACLineStatus) {
            case (0) -> "Battery";
            case (1) -> "AC";
            default -> "Unknown";
        };
    }

    public String getBatteryFlagString() {
        return switch (BatteryFlag) {
            case (1) -> "High, the battery capacity is at more than 66 percent";
            case (2) -> "Low, the battery capacity is at less than 33 percent";
            case (4) -> "Critical, the battery capacity is at less than five percent";
            case (8) -> "Charging";
            case ((byte) 128) -> "No system battery";
            case ((byte) 255) -> "Unknown status—unable to read the battery flag information";
            default -> "Unknown";
        };
    }

    public String getBatteryLifePercentString() {
        return (BatteryLifePercent == (byte) 255) ? "Unknown" : Byte.toString(BatteryLifePercent);
    }

    public String getSystemStatusFlagString() {
        return ((SystemStatusFlag == 1) ? "on" : "off");
    }

    public String getBatteryLifeTimeString() {
        return (BatteryLifeTime == -1) ? "Unknown" : Integer.toString(BatteryLifeTime);
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Battery working mode: ").append(getACLineStatusString()).append("\n")
                .append("Battery flag: ").append(getBatteryFlagString()).append("\n")
                .append("Battery left(%): ").append(getBatteryLifePercentString()).append("\n")
                .append("Battery saver mode: ").append(getSystemStatusFlagString()).append("\n")
                .append("Battery left(seconds): ").append(getBatteryLifeTimeString()).append("\n");
        return stringBuilder.toString();
    }
}
