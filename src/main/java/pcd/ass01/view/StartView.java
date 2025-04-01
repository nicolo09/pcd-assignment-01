package pcd.ass01.view;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import pcd.ass01.model.BoidsModel;
import pcd.ass01.simulators.BoidsPlatformThreadsSimulator;
import pcd.ass01.simulators.BoidsSerialSimulator;
import pcd.ass01.simulators.BoidsSimulator;
import pcd.ass01.simulators.BoidsTasksSimulator;
import pcd.ass01.simulators.BoidsVirtualThreadsSimulator;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class StartView {

    final static double SEPARATION_WEIGHT = 1.0;
    final static double ALIGNMENT_WEIGHT = 1.0;
    final static double COHESION_WEIGHT = 1.0;

    final static int ENVIRONMENT_WIDTH = 1000;
    final static int ENVIRONMENT_HEIGHT = 1000;
    static final double MAX_SPEED = 4.0;
    static final double PERCEPTION_RADIUS = 50.0;
    static final double AVOID_RADIUS = 20.0;

    final static int SCREEN_WIDTH = 800;
    final static int SCREEN_HEIGHT = 800;

    private JFrame startFrame;

    public StartView(int width, int height) {
        startFrame = new JFrame("Boids Simulation");
        startFrame.setSize(width, height);
        startFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel cp = new JPanel();
        LayoutManager layout = new BoxLayout(cp, BoxLayout.Y_AXIS);
        cp.setLayout(layout);

        cp.add(Box.createRigidArea(new Dimension(0, 10)));
        JLabel boidsNumberLabel = new JLabel("Number of boids: ");
        cp.add(boidsNumberLabel);
        cp.add(Box.createRigidArea(new Dimension(0, 10)));
        boidsNumberLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JTextField boidsNumberField = new JTextField();
        cp.add(boidsNumberField);
        cp.add(Box.createRigidArea(new Dimension(0, 10)));
        boidsNumberField.setAlignmentX(Component.CENTER_ALIGNMENT);
        boidsNumberField.setMaximumSize(new Dimension(200, 20));

        final JButton startSerialButton = new JButton("Serial");
        final JButton startPTButton = new JButton("Platform Threads");
        final JButton startTasksButton = new JButton("Task based");
        final JButton startVTButton = new JButton("Virtual Threads");
        startSerialButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        startPTButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        startTasksButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        startVTButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        cp.add(Box.createRigidArea(new Dimension(0, 10)));
        cp.add(startSerialButton);
        cp.add(Box.createRigidArea(new Dimension(0, 10)));
        cp.add(startPTButton);
        cp.add(Box.createRigidArea(new Dimension(0, 10)));
        cp.add(startTasksButton);
        cp.add(Box.createRigidArea(new Dimension(0, 10)));
        cp.add(startVTButton);
        cp.add(Box.createRigidArea(new Dimension(0, 10)));

        ActionListener buttonEventListener = new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                int nBoids = 0;
                try {
                    nBoids = Integer.parseInt(boidsNumberField.getText());
                    if (nBoids <= 0) {
                        throw new NumberFormatException();
                    }
                } catch (NumberFormatException ex) {
                    SwingUtilities.invokeLater(() -> {
                        JOptionPane.showMessageDialog(startFrame,
                                boidsNumberField.getText() + " is not a valid number of boids",
                                "Error", JOptionPane.ERROR_MESSAGE);
                    });
                    return;
                }
                BoidsModel model = createModel(nBoids);
                BoidsSimulator sim = null;
                if (e.getSource().equals(startSerialButton)) {
                    sim = new BoidsSerialSimulator(model);
                } else if (e.getSource().equals(startPTButton)) {
                    sim = new BoidsPlatformThreadsSimulator(model);
                } else if (e.getSource().equals(startTasksButton)) {
                    sim = new BoidsTasksSimulator(model);
                } else if (e.getSource().equals(startVTButton)) {
                    sim = new BoidsVirtualThreadsSimulator(model);
                }
                getThread(sim, model, startFrame).start();
            }
        };

        startSerialButton.addActionListener(buttonEventListener);
        startPTButton.addActionListener(buttonEventListener);
        startTasksButton.addActionListener(buttonEventListener);
        startVTButton.addActionListener(buttonEventListener);

        startFrame.setContentPane(cp);
        startFrame.setVisible(true);
    }

    private BoidsModel createModel(int nBoids) {
        return new BoidsModel(
                nBoids,
                SEPARATION_WEIGHT, ALIGNMENT_WEIGHT, COHESION_WEIGHT,
                ENVIRONMENT_WIDTH, ENVIRONMENT_HEIGHT,
                MAX_SPEED,
                PERCEPTION_RADIUS,
                AVOID_RADIUS);
    }

    public static Thread getThread(BoidsSimulator sim, BoidsModel model, JFrame startFrame) {
        return new Thread(() -> {
            BoidsView view = new BoidsView(model, SCREEN_WIDTH, SCREEN_HEIGHT, sim::pauseSimulation,
                    sim::resumeSimulation, () -> {
                        sim.stopSimulation();
                        SwingUtilities.invokeLater(() -> {
                            startFrame.setVisible(true);
                        });
                    });
            sim.attachView(view);
            SwingUtilities.invokeLater(() -> {
                startFrame.setVisible(false);
            });
            sim.runSimulation();
        }, "simulation-thread");
    }
}
