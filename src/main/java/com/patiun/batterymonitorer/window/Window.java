package com.patiun.batterymonitorer.window;

import com.patiun.batterymonitorer.actionlistener.SetSuspendStateActionListener;
import com.patiun.batterymonitorer.service.Service;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class Window extends JFrame {

    private static final Color BACKGROUND_COLOR = Color.BLACK;
    private static final Color MAIN_COLOR = Color.GREEN;
    private static final Font FONT = new Font("Consolas", Font.BOLD, 20);

    private final Service service;

    private final JLabel timerLabel = buildLabel("Timer");
    private static final String TIMER_MESSAGE = "Time since last plugged in(seconds): ";

    private final JTextArea stats = buildTextArea();

    public Window(Service service) {
        this.service = service;
    }

    public void setUpMyself() {
        this.setTitle("Battery Monitoring");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
        getContentPane().setBackground(BACKGROUND_COLOR);
        this.setBackground(BACKGROUND_COLOR);
        this.setUndecorated(false);
    }

    private void styleComponent(JComponent component) {
        component.setOpaque(true);
        component.setBackground(BACKGROUND_COLOR);
        component.setFont(FONT);
        component.setForeground(MAIN_COLOR);
    }

    private JPanel buildPanel() {
        JPanel panel = new JPanel();
        styleComponent(panel);
        return panel;
    }

    private JLabel buildLabel(String text) {
        JLabel label = new JLabel(text);
        styleComponent(label);
        return label;
    }

    private JTextArea buildTextArea() {
        JTextArea textArea = new JTextArea();
        textArea.setPreferredSize(new Dimension(800, 250));
        textArea.setEditable(false);
        styleComponent(textArea);
        return textArea;
    }

    private JButton buildButton(String name, ActionListener actionListener) {
        JButton button = new JButton(name);
        button.addActionListener(actionListener);
        button.setPreferredSize(new Dimension(200, 75));
        styleComponent(button);
        return button;
    }

    private JPanel setUpStatsPanel() {
        JPanel statsPanel = buildPanel();
        statsPanel.setLayout(new BoxLayout(statsPanel, BoxLayout.Y_AXIS));

        JLabel statsLabel = buildLabel("Battery stats");

        statsPanel.add(statsLabel);
        statsPanel.add(stats);

        return statsPanel;
    }

    private JPanel setUpInfoPanel() {
        JPanel infoPanel = buildPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));

        JLabel infoLabel = buildLabel("Battery info");

        JTextArea info = buildTextArea();

        info.setText(new Service().getBatteryInformation().toString());

        infoPanel.add(infoLabel);
        infoPanel.add(info);

        return infoPanel;
    }

    private JPanel setUpUpperPanel() {
        JPanel upperPanel = buildPanel();
        upperPanel.setLayout(new GridLayout(1, 2));

        upperPanel.add(setUpStatsPanel());
        upperPanel.add(setUpInfoPanel());

        return upperPanel;
    }

    private JPanel setUpTimer() {
        JPanel timerPanel = buildPanel();
        timerPanel.setLayout(new BoxLayout(timerPanel, BoxLayout.X_AXIS));
        timerPanel.add(timerLabel);

        return timerPanel;
    }

    private JPanel setUpButtonsPanel() {
        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setLayout(new FlowLayout());
        buttonsPanel.setBackground(BACKGROUND_COLOR);

        JButton suspendButton = buildButton("Suspend", new SetSuspendStateActionListener(false));
        buttonsPanel.add(suspendButton);

        JButton hibernateButton = buildButton("Hibernate", new SetSuspendStateActionListener(true));
        buttonsPanel.add(hibernateButton);

        return buttonsPanel;
    }

    private void launch() {
        this.pack();
        this.setVisible(true);

        service.updateStatsAndTimer(stats, timerLabel, TIMER_MESSAGE);
    }

    public void setUpAndLaunch() {
        setUpMyself();
        add(setUpUpperPanel());
        add(setUpTimer());
        add(setUpButtonsPanel());
        launch();
    }
}
