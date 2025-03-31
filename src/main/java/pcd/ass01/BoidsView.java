package pcd.ass01;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import java.awt.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.Hashtable;

public class BoidsView implements ChangeListener {

	private JFrame frame;
	private BoidsPanel boidsPanel;
	private JSlider cohesionSlider, separationSlider, alignmentSlider;
	private BoidsModel model;
	private int width, height;

	public BoidsView(BoidsModel model, int width, int height, Runnable onPause, Runnable onResume,
			Runnable onBeforeStop) {
		this.model = model;
		this.width = width;
		this.height = height;

		frame = new JFrame("Boids Simulation");
		frame.setSize(width, height);
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		frame.addWindowListener(new WindowListener() {

			@Override
			public void windowOpened(WindowEvent e) {
				return;
			}

			@Override
			public void windowClosing(WindowEvent e) {
				onBeforeStop.run();
				SwingUtilities.invokeLater(frame::dispose);
			}

			@Override
			public void windowClosed(WindowEvent e) {
				return;
			}

			@Override
			public void windowIconified(WindowEvent e) {
				return;
			}

			@Override
			public void windowDeiconified(WindowEvent e) {
				return;
			}

			@Override
			public void windowActivated(WindowEvent e) {
				return;
			}

			@Override
			public void windowDeactivated(WindowEvent e) {
				return;
			}
		});

		JPanel cp = new JPanel();
		LayoutManager layout = new BorderLayout();
		cp.setLayout(layout);

		boidsPanel = new BoidsPanel(this, model);
		cp.add(BorderLayout.CENTER, boidsPanel);

		JPanel slidersPanel = new JPanel();

		cohesionSlider = makeSlider();
		separationSlider = makeSlider();
		alignmentSlider = makeSlider();

		slidersPanel.add(new JLabel("Separation"));
		slidersPanel.add(separationSlider);
		slidersPanel.add(new JLabel("Alignment"));
		slidersPanel.add(alignmentSlider);
		slidersPanel.add(new JLabel("Cohesion"));
		slidersPanel.add(cohesionSlider);

		cp.add(BorderLayout.SOUTH, slidersPanel);

		JPanel buttonsPanel = new JPanel();
		JButton pauseButton = new JButton("Pause");
		pauseButton.addActionListener(e -> onPause.run());
		buttonsPanel.add(pauseButton);
		JButton resumeButton = new JButton("Resume");
		resumeButton.addActionListener(e -> onResume.run());
		buttonsPanel.add(resumeButton);
		JButton stopButton = new JButton("Stop");
		stopButton.addActionListener(e -> {
			onBeforeStop.run();
			SwingUtilities.invokeLater(frame::dispose);
		});
		buttonsPanel.add(stopButton);
		cp.add(BorderLayout.NORTH, buttonsPanel);

		frame.setContentPane(cp);

		frame.setVisible(true);
	}

	private JSlider makeSlider() {
		var slider = new JSlider(JSlider.HORIZONTAL, 0, 20, 10);
		slider.setMajorTickSpacing(10);
		slider.setMinorTickSpacing(1);
		slider.setPaintTicks(true);
		slider.setPaintLabels(true);
		Hashtable<Integer, JLabel> labelTable = new Hashtable<>();
		labelTable.put(0, new JLabel("0"));
		labelTable.put(10, new JLabel("1"));
		labelTable.put(20, new JLabel("2"));
		slider.setLabelTable(labelTable);
		slider.setPaintLabels(true);
		slider.addChangeListener(this);
		return slider;
	}

	public void update(int frameRate) {
		boidsPanel.setFrameRate(frameRate);
		boidsPanel.repaint();
	}

	@Override
	public void stateChanged(ChangeEvent e) {
		if (e.getSource() == separationSlider) {
			var val = separationSlider.getValue();
			model.setSeparationWeight(0.1 * val);
		} else if (e.getSource() == cohesionSlider) {
			var val = cohesionSlider.getValue();
			model.setCohesionWeight(0.1 * val);
		} else {
			var val = alignmentSlider.getValue();
			model.setAlignmentWeight(0.1 * val);
		}
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public void close() {
		frame.dispose();
	}

}
