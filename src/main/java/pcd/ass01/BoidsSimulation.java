package pcd.ass01;

import pcd.ass01.model.BoidsModel;
import pcd.ass01.view.StartView;

public class BoidsSimulation {

	// Constants for the initial window size
	final static int START_WIDTH = 300;
	final static int START_HEIGHT = 280;

	final static double SEPARATION_WEIGHT = 1.0;
    final static double ALIGNMENT_WEIGHT = 1.0;
    final static double COHESION_WEIGHT = 1.0;

    final static int ENVIRONMENT_WIDTH = 1000;
    final static int ENVIRONMENT_HEIGHT = 1000;
    static final double MAX_SPEED = 4.0;
    static final double PERCEPTION_RADIUS = 50.0;
    static final double AVOID_RADIUS = 20.0;

	public static void main(String[] args) {
		new StartView(START_WIDTH, START_HEIGHT);
	}

	public static BoidsModel createModel(int nBoids) {
        return new BoidsModel(
                nBoids,
                SEPARATION_WEIGHT, ALIGNMENT_WEIGHT, COHESION_WEIGHT,
                ENVIRONMENT_WIDTH, ENVIRONMENT_HEIGHT,
                MAX_SPEED,
                PERCEPTION_RADIUS,
                AVOID_RADIUS);
    }
}
