package pcd.ass01;

import java.util.concurrent.CyclicBarrier;

public class UpdateBoidRunnable implements Runnable {

    private final Boid boid;
    private final BoidsModel model;
    private final CyclicBarrier threadsBarrier;
    private final CyclicBarrier mainBarrier;

    public UpdateBoidRunnable(Boid boid, BoidsModel model, CyclicBarrier threadsBarrier, CyclicBarrier mainBarrier) {
        this.boid = boid;
        this.model = model;
        this.threadsBarrier = threadsBarrier;
        this.mainBarrier = mainBarrier;
    }

    @Override
    public void run() {
        while (true) {
            /* The main thread signals when the threads can start */
            waitMain();

            //TODO: uncomment to use
            //boid.updateVelocity(model, threadsBarrier);
            /* Waiting all threads to update velocity */
            //waitThreads();
    
            boid.updatePos(model);
    
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
