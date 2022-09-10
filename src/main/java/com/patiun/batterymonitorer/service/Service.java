package com.patiun.batterymonitorer.service;

import com.patiun.batterymonitorer.structure.BATTERY_INFORMATION;
import com.patiun.batterymonitorer.structure.BATTERY_QUERY_INFORMATION;
import com.sun.jna.Memory;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.Guid.GUID;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.SetupApi;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.ptr.IntByReference;

import static com.patiun.batterymonitorer.window.Window.IOCTL_BATTERY_QUERY_INFORMATION;
import static com.patiun.batterymonitorer.window.Window.IOCTL_BATTERY_QUERY_TAG;
import static com.sun.jna.platform.win32.SetupApi.DIGCF_DEVICEINTERFACE;
import static com.sun.jna.platform.win32.SetupApi.DIGCF_PRESENT;
import static com.sun.jna.platform.win32.WinNT.*;

public class Service {

    private final Kernel32 kernel32 = Kernel32.INSTANCE;
    private final SetupApi setupApi = SetupApi.INSTANCE;

    private static final GUID DEV_CLASS_BATTERY = new GUID("72631E54-78A4-11D0-BCF7-00AA00B7B32A");

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
        batteryInformation.Technology = 1;
        batteryInformation.Chemistry = new byte[]{'L', 'i', 'P'};
        batteryInformation.DesignedCapacity = new ULONG(45000);
        batteryInformation.FullChargedCapacity = new ULONG(39290);

        return batteryInformation;
    }

}
