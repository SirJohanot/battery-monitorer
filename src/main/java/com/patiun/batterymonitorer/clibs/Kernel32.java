package com.patiun.batterymonitorer.clibs;

import com.patiun.batterymonitorer.structure.SYSTEM_POWER_STATUS;
import com.sun.jna.Native;
import com.sun.jna.win32.StdCallLibrary;

public interface Kernel32 extends StdCallLibrary {

    Kernel32 INSTANCE = Native.load("Kernel32", Kernel32.class);

    int GetSystemPowerStatus(SYSTEM_POWER_STATUS result);
}
