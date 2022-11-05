package com.patiun.batterymonitorer.clibs;

import com.sun.jna.Native;
import com.sun.jna.win32.StdCallLibrary;

public interface PowrProf extends StdCallLibrary {

    PowrProf INSTANCE = Native.load("PowrProf", PowrProf.class);

    boolean SetSuspendState(boolean hibernate, boolean forceCritical, boolean disableWakeEvent);

}