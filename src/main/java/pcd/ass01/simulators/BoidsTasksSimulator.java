package pcd.ass01.simulators;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import pcd.ass01.model.BoidsModel;

public class BoidsTasksSimulator extends BoidsSimulator {

    public BoidsTasksSimulator(BoidsModel model) {
        super(model);
    }

    public void runSimulation() {
        ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() + 1);
        int framerate = 0;

        while (!this.getStateMonitor().isStopped()) {
            var t0 = System.currentTimeMillis();
            var boids = this.getModel().getBoids();
            BoidsModel modelCopy = new BoidsModel(this.getModel());

            /*
             * Improved correctness: first update velocities...
             */
            List<Future<?>> velocityFutures = new ArrayList<>();
            boids.forEach(boid -> velocityFutures.add(executor.submit(() -> boid.updateVelocity(modelCopy))));
            velocityFutures.forEach(f -> {
                try {
                    f.get();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

            /*
             * ..then update positions
             */
            List<Future<?>> posFutures = new ArrayList<>();
            boids.forEach(boid -> posFutures.add(executor.submit(() -> boid.updatePos(modelCopy))));
            posFutures.forEach(f -> {
                try {
                    f.get();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

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

        executor.shutdown();
    }
}
