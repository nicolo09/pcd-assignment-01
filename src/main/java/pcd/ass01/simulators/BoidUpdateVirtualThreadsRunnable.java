package pcd.ass01.simulators;

import pcd.ass01.model.Boid;
import pcd.ass01.model.BoidsModel;
import pcd.ass01.simulators.utils.MyBarrier;

public class BoidUpdateVirtualThreadsRunnable implements Runnable {

    private final Boid boid;
    private BoidsModel localModel;
    private final MyBarrier updateBarrier; // barrier for updating velocity and position
    private final MyBarrier modelBarrier; // barrier for updating the model

    public BoidUpdateVirtualThreadsRunnable(Boid boid, MyBarrier barrier, MyBarrier modelBarrier) {
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
        while (!Thread.currentThread().isInterrupted()) {
            try {
                modelBarrier.await();
            } catch (Exception ex) {
                return;
            }
            boid.updateVelocity(localModel);
            try {
                updateBarrier.await();
            } catch (Exception ex) {
                return;
            }
            boid.updatePos(localModel);
            try {
                updateBarrier.await();
            } catch (Exception ex) {
                return;
            }
            // the thread will wait on the model barrier until the model is updated
        }
    }
}
