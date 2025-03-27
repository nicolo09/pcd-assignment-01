package pcd.ass01;

import java.util.Optional;

public class BoidsSimulator {

    private BoidsModel model;
    private Optional<BoidsView> view;
    private volatile boolean isPaused = false;
    private volatile boolean isStopped = false;

    private static final int FRAMERATE = 25;
    private int framerate;

    public BoidsSimulator(BoidsModel model) {
        this.model = model;
        view = Optional.empty();
    }

    public synchronized void attachView(BoidsView view) {
        this.view = Optional.of(view);
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
        while (!isStopped()) {
            var t0 = System.currentTimeMillis();
            var boids = model.getBoids();

            /*
             * Improved correctness: first update velocities...
             */
            for (Boid boid : boids) {
                boid.updateVelocity(model);
            }

            /*
             * ..then update positions
             */
            for (Boid boid : boids) {
                boid.updatePos(model);
            }

            if (view.isPresent()) {
                view.get().update(framerate);
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
