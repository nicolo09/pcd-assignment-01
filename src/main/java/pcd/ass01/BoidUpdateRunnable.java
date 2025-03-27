package pcd.ass01;

import java.util.List;
import java.util.concurrent.CyclicBarrier;

public class BoidUpdateRunnable implements Runnable {

    private final List<Boid> boids;
    private BoidsModel model;
    private final CyclicBarrier barrier;

    public BoidUpdateRunnable(List<Boid> boids, CyclicBarrier barrier) {
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
