package com.patiun.batterymonitorer.service;

import com.patiun.batterymonitorer.structure.BATTERY_INFORMATION;
import com.patiun.batterymonitorer.structure.BATTERY_QUERY_INFORMATION;
import com.patiun.batterymonitorer.structure.SYSTEM_POWER_STATUS;
import com.sun.jna.Memory;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.Guid.GUID;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.SetupApi;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.ptr.IntByReference;

import javax.swing.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static com.sun.jna.platform.win32.SetupApi.DIGCF_DEVICEINTERFACE;
import static com.sun.jna.platform.win32.SetupApi.DIGCF_PRESENT;
import static com.sun.jna.platform.win32.WinNT.*;

public class Service {

    private static final int IOCTL_BATTERY_QUERY_TAG = 0x294040;
    private static final int IOCTL_BATTERY_QUERY_INFORMATION = 0x294044;

    private static final GUID DEV_CLASS_BATTERY = new GUID("72631E54-78A4-11D0-BCF7-00AA00B7B32A");

    private final Kernel32 kernel32 = Kernel32.INSTANCE;
    private final com.patiun.batterymonitorer.clibs.Kernel32 customKernel32 = com.patiun.batterymonitorer.clibs.Kernel32.INSTANCE;
    private final SetupApi setupApi = SetupApi.INSTANCE;

    private int timer = 0;

    public BATTERY_INFORMATION getBatteryInformation() {
        WinNT.HANDLE handleDevice = setupApi.SetupDiGetClassDevs(DEV_CLASS_BATTERY, null, null, DIGCF_PRESENT | DIGCF_DEVICEINTERFACE);

        SetupApi.SP_DEVICE_INTERFACE_DATA deviceInterfaceData = new SetupApi.SP_DEVICE_INTERFACE_DATA();
        deviceInterfaceData.cbSize = deviceInterfaceData.size();
        setupApi.SetupDiEnumDeviceInterfaces(handleDevice, null, DEV_CLASS_BATTERY, 0, deviceInterfaceData);

        IntByReference cbRequired = new IntByReference(0);
        setupApi.SetupDiGetDeviceInterfaceDetail(handleDevice, deviceInterfaceData, null, 0, cbRequired, null);

        Pointer deviceInterfaceDetailData = new Memory(cbRequired.getValue());
        deviceInterfaceDetailData.write(0, new int[]{6}, 0, 1);
        setupApi.SetupDiGetDeviceInterfaceDetail(handleDevice, deviceInterfaceData, deviceInterfaceDetailData, cbRequired.getValue(), cbRequired, null);

        char[] path = new char[cbRequired.getValue()];
        for (int i = 0; i < path.length; i++) {
            deviceInterfaceDetailData.read(4 + i, path, i, 1);
            if (path[i] == '\0') {
                break;
            }
        }
        WinNT.HANDLE handleBattery = kernel32.CreateFile(String.valueOf(path), GENERIC_READ | GENERIC_WRITE, FILE_SHARE_READ | FILE_SHARE_WRITE, null, OPEN_EXISTING, FILE_ATTRIBUTE_NORMAL, null);

        BATTERY_QUERY_INFORMATION batteryQueryInformation = new BATTERY_QUERY_INFORMATION();
        Pointer dwWait = new Memory(4);
        IntByReference dwOut = new IntByReference();
        kernel32.DeviceIoControl(handleBattery, IOCTL_BATTERY_QUERY_TAG, dwWait, 4, batteryQueryInformation.BatteryTag, 8, dwOut, null);

        BATTERY_INFORMATION batteryInformation = new BATTERY_INFORMATION();
        batteryQueryInformation.InformationLevel = 0;
        kernel32.DeviceIoControl(handleBattery, IOCTL_BATTERY_QUERY_INFORMATION, batteryQueryInformation.getPointer(), batteryQueryInformation.size(), batteryInformation.getPointer(), batteryInformation.size(), dwOut, null);

        return batteryInformation;
    }

    public void updateStatsAndTimer(JTextArea stats, JLabel timerLabel, String timerMessage) {
        SYSTEM_POWER_STATUS systemPowerStatus = new SYSTEM_POWER_STATUS();

        ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        scheduledExecutorService.scheduleAtFixedRate(() -> {
            customKernel32.GetSystemPowerStatus(systemPowerStatus);
            stats.setText(systemPowerStatus.toString());
            if (systemPowerStatus.ACLineStatus == 0) {
                timer++;
            } else {
                timer = 0;
            }
            timerLabel.setText(timerMessage + timer);
        }, 0, 1, TimeUnit.SECONDS);
    }

}
