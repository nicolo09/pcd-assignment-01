package pcd.ass01;

import java.util.concurrent.CyclicBarrier;

public class BoidUpdateRunnable implements Runnable {

    private final Boid boid;
    private BoidsModel localModel;
    private final CyclicBarrier updateBarrier; // barrier for updating velocity and position
    private final CyclicBarrier modelBarrier; // barrier for updating the model

    public BoidUpdateRunnable(Boid boid, CyclicBarrier barrier, CyclicBarrier modelBarrier) {
        this.boid = boid;
        this.updateBarrier = barrier;
        this.modelBarrier = modelBarrier;
    }

    public void setBoidModel(BoidsModel model) {
        // set the local model to the current model, this method does not need to be
        // synchronized as it is called only when this thread is waiting on the model
        // barrier
        this.localModel = model;
    }

    @Override
    public void run() {
        while (true) {
            try {
                modelBarrier.await();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            boid.updateVelocity(localModel);
            try {
                updateBarrier.await();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            boid.updatePos(localModel);
            try {
                updateBarrier.await();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            // the thread will wait on the model barrier until the model is updated
        }
    }
}
