package pcd.ass01.simulators;

import pcd.ass01.model.Boid;
import pcd.ass01.model.BoidsModel;

public class BoidsSerialSimulator extends BoidsSimulator {

    private volatile boolean isPaused = false;
    private volatile boolean isStopped = false;

    public BoidsSerialSimulator(BoidsModel model) {
        super(model);
    }

    public synchronized void pauseSimulation() {
        isPaused = true;
    }

    public synchronized void resumeSimulation() {
        if (isPaused) {
            isPaused = false;
            notify();
        }
    }

    public synchronized void stopSimulation() {
        if (!isStopped) {
            isStopped = true;
            if (isPaused) {
                notify();
            }
        }
    }

    public synchronized boolean isStopped() {
        return isStopped;
    }

    public void runSimulation() {
        int framerate = 0;
        while (!isStopped()) {
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
            synchronized (this) {
                while (isPaused) {
                    try {
                        wait();
                    } catch (Exception ex) {
                    }
                }
            }
        }
    }
}