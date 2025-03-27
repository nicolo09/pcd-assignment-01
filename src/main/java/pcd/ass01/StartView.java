package pcd.ass01;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import java.awt.*;
import java.util.Optional;

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

        JLabel boidsNumberLabel = new JLabel("Number of boids: ");
        cp.add(boidsNumberLabel);

        JTextField boidsNumberField = new JTextField();
        cp.add(boidsNumberField);

        JButton startSerialButton = new JButton("Serial");
        JButton startPTButton = new JButton("Platform Threads");
        JButton startTasksButton = new JButton("Task based");
        JButton startVTButton = new JButton("Virtual Threads");
        cp.add(startSerialButton);
        cp.add(startPTButton);
        cp.add(startTasksButton);
        cp.add(startVTButton);

        startSerialButton.addActionListener(e -> {
            createModel(boidsNumberField).ifPresent(model -> {
                getThread(new BoidsSerialSimulator(model), model, startFrame).start();
            });
        });
        startPTButton.addActionListener(e -> {
            createModel(boidsNumberField).ifPresent(model -> {
                getThread(new BoidsPlatformThreadsSimulator(model), model, startFrame).start();
            });
        });
        startTasksButton.addActionListener(e -> {
            createModel(boidsNumberField).ifPresent(model -> {
                getThread(new BoidsTasksSimulator(model), model, startFrame).start();
            });
        });
        startVTButton.addActionListener(e -> {
            createModel(boidsNumberField).ifPresent(model -> {
                getThread(new BoidsSerialSimulator(model), model, startFrame).start(); //TODO: Fix this
            });
        });

        startFrame.setContentPane(cp);
        startFrame.setVisible(true);
    }

    private Optional<BoidsModel> createModel(JTextField boidsNumberField) {
        int nBoids = 0;
        try {
            nBoids = Integer.parseInt(boidsNumberField.getText());
            if (nBoids <= 0) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException ex) {
            // TODO: Move message dialog to optional ifEmpty
            JOptionPane.showMessageDialog(startFrame,
                    boidsNumberField.getText() + " is not a valid number of boids",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return Optional.empty();
        }
        return Optional.of(new BoidsModel(
                nBoids,
                SEPARATION_WEIGHT, ALIGNMENT_WEIGHT, COHESION_WEIGHT,
                ENVIRONMENT_WIDTH, ENVIRONMENT_HEIGHT,
                MAX_SPEED,
                PERCEPTION_RADIUS,
                AVOID_RADIUS));
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
