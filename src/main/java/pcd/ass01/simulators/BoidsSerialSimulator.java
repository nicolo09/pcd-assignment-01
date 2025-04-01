package pcd.ass01.simulators;

import pcd.ass01.model.Boid;
import pcd.ass01.model.BoidsModel;

public class BoidsSerialSimulator extends BoidsSimulator {

    public BoidsSerialSimulator(BoidsModel model) {
        super(model);
    }

    public void runSimulation() {
        int framerate = 0;
        while (!this.getStateMonitor().isStopped()) {
            var t0 = System.currentTimeMillis();
            var boids = this.getModel().getBoids();

            /*
             * Improved correctness: first update velocities...
             */
            for (Boid boid : boids) {
                boid.updateVelocity(this.getModel());
            }

            /*
             * ..then update positions
             */
            for (Boid boid : boids) {
                boid.updatePos(this.getModel());
            }

            if (this.getView().isPresent()) {
                this.getView().get().update(framerate);
                var t1 = System.currentTimeMillis();
                var dtElapsed = t1 - t0;
                var framratePeriod = 1000 / FRAMERATE;

                if (dtElapsed < framratePeriod) {
                    try {
                        Thread.sleep(framratePeriod - dtElapsed);
                    } catch (Exception ex) {
                    }
                    framerate = FRAMERATE;
                } else {
                    framerate = (int) (1000 / dtElapsed);
                }
            }

            // Pause simulation if requested
            while (this.getStateMonitor().isPaused()) {
                synchronized (this.getStateMonitor()) {
                    try {
                        this.getStateMonitor().wait();
                    } catch (Exception ex) {
                    }
                }
            }
        }
    }
}