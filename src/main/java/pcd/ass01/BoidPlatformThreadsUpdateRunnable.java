package pcd.ass01;

import java.util.List;

public class BoidPlatformThreadsUpdateRunnable implements Runnable {

    private final List<Boid> boids;
    private BoidsModel model;
    private final MyBarrier barrier;

    public BoidPlatformThreadsUpdateRunnable(List<Boid> boids, MyBarrier barrier) {
        this.boids = boids;
        this.barrier = barrier;
    }

    public void setModel(BoidsModel model) {
        this.model = model;
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                // the model will be updated by the main thread here
                barrier.await();
            } catch (Exception e) {
                return;
            }
            boids.forEach(boid -> boid.updateVelocity(model));
            try {
                barrier.await();
            } catch (Exception e) {
                return;
            }
            boids.forEach(boid -> boid.updatePos(model));
            try {
                barrier.await();
            } catch (Exception e) {
                return;
            }
        }
    }
}
