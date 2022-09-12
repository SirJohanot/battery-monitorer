package com.patiun.batterymonitorer.structure;

import com.sun.jna.Structure;
import com.sun.jna.platform.win32.WinDef.ULONG;

import java.util.ArrayList;
import java.util.List;

public class BATTERY_INFORMATION extends Structure {

    public ULONG Capabilities;
    public byte Technology;
    public byte[] Reserved = new byte[3];
    public byte[] Chemistry = new byte[4];
    public ULONG DesignedCapacity;
    public ULONG FullChargedCapacity;
    public ULONG DefaultAlert1;
    public ULONG DefaultAlert2;
    public ULONG CriticalBias;
    public ULONG CycleCount;

    @Override
    protected List<String> getFieldOrder() {
        List<String> fields = new ArrayList<>();
        fields.add("Capabilities");
        fields.add("Technology");
        fields.add("Reserved");
        fields.add("Chemistry");
        fields.add("DesignedCapacity");
        fields.add("FullChargedCapacity");
        fields.add("DefaultAlert1");
        fields.add("DefaultAlert2");
        fields.add("CriticalBias");
        fields.add("CycleCount");
        return fields;
    }

    public String getTechnologyString() {
        return switch (Technology) {
            case (0) -> "Non rechargeable";
            case (1) -> "Rechargeable";
            default -> "Unknown";
        };
    }

    public String getChemistryString() {
        StringBuilder result = new StringBuilder();
        for (byte b : Chemistry) {
            result.append((char) b);
        }
        return result.toString();
    }

    @Override
    public String toString() {
        return "Battery technology: " + getTechnologyString() + "\n" +
                "Battery chemistry: " + getChemistryString() + "\n" +
                "Designed capacity(mWh): " + DesignedCapacity + "\n" +
                "Full charged capacity(mWh): " + FullChargedCapacity + "\n";
    }

}
