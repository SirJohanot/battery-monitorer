package com.patiun.batterymonitorer.main;

import com.patiun.batterymonitorer.service.Service;
import com.patiun.batterymonitorer.window.Window;

public class Main {

    public static void main(String[] args) {
        new Window(new Service()).setUpAndLaunch();
    }
}
