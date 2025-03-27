package pcd.ass01;

import java.util.List;
import java.util.concurrent.CyclicBarrier;

public class BoidPlatformThreadsUpdateRunnable implements Runnable {

    private final List<Boid> boids;
    private BoidsModel model;
    private final CyclicBarrier barrier;

    public BoidPlatformThreadsUpdateRunnable(List<Boid> boids, CyclicBarrier barrier) {
        this.boids = boids;
        this.barrier = barrier;
    }

    public void setModel(BoidsModel model) {
        this.model = model;
    }

    @Override
    public void run() {
        while (true) {
            try {
                // the model will be updated by the main thread here
                barrier.await();
            } catch (Exception e) {
                e.printStackTrace();
            }
            boids.forEach(boid -> boid.updateVelocity(model));
            try {
                barrier.await();
            } catch (Exception e) {
                e.printStackTrace();
            }
            boids.forEach(boid -> boid.updatePos(model));
            try {
                barrier.await();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
