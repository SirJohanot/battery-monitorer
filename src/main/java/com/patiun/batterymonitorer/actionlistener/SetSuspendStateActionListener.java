package com.patiun.batterymonitorer.actionlistener;

import com.patiun.batterymonitorer.clibs.PowrProf;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SetSuspendStateActionListener implements ActionListener {

    private final boolean hibernate;

    public SetSuspendStateActionListener(boolean hibernate) {
        this.hibernate = hibernate;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        PowrProf.INSTANCE.SetSuspendState(hibernate, false, false);
    }
}
