package pcd.ass01;

import java.util.List;
import java.util.concurrent.CyclicBarrier;

public class UpdateMultipleBoidsRunnable implements Runnable {

    private final List<Boid> boids;
    private final BoidsModel model;
    private final CyclicBarrier threadsBarrier;
    private final CyclicBarrier mainBarrier;

    public UpdateMultipleBoidsRunnable(List<Boid> boids, BoidsModel model, CyclicBarrier threadsBarrier, CyclicBarrier mainBarrier) {
        this.boids = boids;
        this.model = model;
        this.threadsBarrier = threadsBarrier;
        this.mainBarrier = mainBarrier;
    }

    @Override
    public void run() {
        while (true) {
            /* The main thread signals when the threads can start */
            waitMain();

            var modelCopy = new BoidsModel(model);
            waitThreads();

            boids.forEach(boid -> boid.updateVelocity(modelCopy));
            /* Waiting all threads to update velocity */
            waitThreads();
    
            boids.forEach(boid -> boid.updatePos(model));
    
            /* All threads signal the main thread that all boids have been updated */
            waitMain();
        }
    }

    private void waitThreads() {
        try {
            threadsBarrier.await();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void waitMain() {
        try {
            mainBarrier.await();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
}
