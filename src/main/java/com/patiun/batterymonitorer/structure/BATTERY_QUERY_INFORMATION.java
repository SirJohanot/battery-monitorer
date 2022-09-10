package com.patiun.batterymonitorer.structure;

import com.sun.jna.Memory;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.platform.win32.WinDef;

import java.util.ArrayList;
import java.util.List;

public class BATTERY_QUERY_INFORMATION extends Structure {

    public Pointer BatteryTag;
    public char InformationLevel;
    public WinDef.LONG atRate;

    public BATTERY_QUERY_INFORMATION() {
        BatteryTag = new Memory(8);
    }

    @Override
    protected List<String> getFieldOrder() {
        List<String> fields = new ArrayList<>();
        fields.add("BatteryTag");
        fields.add("InformationLevel");
        fields.add("atRate");
        return fields;
    }

}
