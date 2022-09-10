package com.patiun.batterymonitorer.window;

import com.patiun.batterymonitorer.clibs.Kernel32;
import com.patiun.batterymonitorer.clibs.PowrProf;
import com.patiun.batterymonitorer.service.Service;
import com.patiun.batterymonitorer.structure.SYSTEM_POWER_STATUS;

import javax.swing.*;
import java.awt.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Window extends JFrame {

    public static Color BACKGROUND_COLOR = Color.BLACK;
    public static Color MAIN_COLOR = Color.GREEN;

    public static final int IOCTL_BATTERY_QUERY_TAG = 0x294040;
    public static final int IOCTL_BATTERY_QUERY_INFORMATION = 0x294044;

    private int timer = 0;
    private JLabel timerLabel = new JLabel();
    private static final String TIMER_MESSAGE = "Time since last plugged in (sec): ";

    private JTextArea stats;

    public Window() {
    }

    public void setUpMyself() {
        this.setTitle("Battery Monitoring");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
        getContentPane().setBackground(BACKGROUND_COLOR);
        this.setBackground(BACKGROUND_COLOR);
        this.setUndecorated(false);
    }

    public JPanel setUpStatsPanel() {
        JPanel statsPanel = new JPanel();
        statsPanel.setLayout(new BoxLayout(statsPanel, BoxLayout.Y_AXIS));
        statsPanel.setBackground(BACKGROUND_COLOR);

        JLabel statsLabel = new JLabel("Battery stats");
        statsLabel.setHorizontalTextPosition(SwingConstants.CENTER);
        statsLabel.setOpaque(true);
        statsLabel.setBackground(BACKGROUND_COLOR);
        statsLabel.setFont(new Font("Consolas", Font.BOLD, 20));
        statsLabel.setForeground(MAIN_COLOR);

        stats = new JTextArea();
        stats.setPreferredSize(new Dimension(800, 250));
        stats.setBackground(BACKGROUND_COLOR);
        stats.setFont(new Font("Consolas", Font.BOLD, 20));
        stats.setForeground(MAIN_COLOR);

        statsPanel.add(statsLabel);
        statsPanel.add(stats);

        return statsPanel;
    }

    public JPanel setUpInfoPanel() {
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setBackground(BACKGROUND_COLOR);

        JLabel infoLabel = new JLabel("Battery info");
        infoLabel.setHorizontalTextPosition(SwingConstants.CENTER);
        infoLabel.setOpaque(true);
        infoLabel.setBackground(BACKGROUND_COLOR);
        infoLabel.setFont(new Font("Consolas", Font.BOLD, 20));
        infoLabel.setForeground(MAIN_COLOR);

        JTextArea info = new JTextArea();
        info.setPreferredSize(new Dimension(500, 250));
        info.setBackground(BACKGROUND_COLOR);
        info.setFont(new Font("Consolas", Font.BOLD, 20));
        info.setForeground(MAIN_COLOR);

        info.setText(new Service().getBatteryInformation().toString());

        infoPanel.add(infoLabel);
        infoPanel.add(info);

        return infoPanel;
    }

    public JPanel setUpUpperPanel() {
        JPanel upperPanel = new JPanel();
        upperPanel.setLayout(new GridLayout(1, 2));

        upperPanel.add(setUpStatsPanel());
        upperPanel.add(setUpInfoPanel());

        return upperPanel;
    }

    public JPanel setUpTimer() {
        JPanel timerPanel = new JPanel();
        timerPanel.setLayout(new BoxLayout(timerPanel, BoxLayout.Y_AXIS));
        timerPanel.setBackground(BACKGROUND_COLOR);

        timerLabel.setHorizontalTextPosition(SwingConstants.CENTER);
        timerLabel.setOpaque(true);
        timerLabel.setBackground(BACKGROUND_COLOR);
        timerLabel.setFont(new Font("Consolas", Font.BOLD, 20));
        timerLabel.setForeground(MAIN_COLOR);
        timerPanel.add(timerLabel);

        return timerPanel;
    }

    public JPanel setUpButtonsPanel() {
        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setLayout(new FlowLayout());
        buttonsPanel.setBackground(BACKGROUND_COLOR);

        PowrProf powrprof = PowrProf.INSTANCE;

        JButton suspendButton = new JButton("Suspend");
        suspendButton.addActionListener(e -> powrprof.SetSuspendState(false, false, false));
        suspendButton.setBackground(Color.GREEN);
        suspendButton.setPreferredSize(new Dimension(200, 75));
        suspendButton.setMaximumSize(new Dimension(200, 75));
        buttonsPanel.add(suspendButton);

        JButton hibernateButton = new JButton("Hibernate");
        hibernateButton.addActionListener(e -> powrprof.SetSuspendState(true, false, false));
        hibernateButton.setBackground(Color.GREEN);
        hibernateButton.setPreferredSize(new Dimension(200, 75));
        hibernateButton.setMaximumSize(new Dimension(200, 75));
        buttonsPanel.add(hibernateButton);

        return buttonsPanel;
    }

    public void launch() throws InterruptedException {
        this.pack();
        this.setVisible(true);

        Kernel32 kernel32 = Kernel32.INSTANCE;
        SYSTEM_POWER_STATUS systemPowerStatus = new SYSTEM_POWER_STATUS();

        ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        scheduledExecutorService.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                kernel32.GetSystemPowerStatus(systemPowerStatus);
                stats.setText(systemPowerStatus.toString());
                if (systemPowerStatus.ACLineStatus == 0) {
                    timer++;
                } else {
                    timer = 0;
                }
                timerLabel.setText(TIMER_MESSAGE + timer);
            }
        }, 0, 1, TimeUnit.SECONDS);
    }

    public void setUpAndLaunch() throws InterruptedException {
        setUpMyself();
        this.add(setUpUpperPanel());
        this.add(setUpTimer());
        this.add(setUpButtonsPanel());
        launch();
    }
}
