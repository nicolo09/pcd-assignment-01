package pcd.ass01.simulators;

import pcd.ass01.model.Boid;
import pcd.ass01.model.BoidsModel;
import pcd.ass01.simulators.utils.SimpleBarrier;

public class BoidUpdateVirtualThreadsRunnable implements Runnable {

    private final Boid boid;
    private BoidsModel localModel;
    private final SimpleBarrier barrier; // barrier for updating velocity and position

    public BoidUpdateVirtualThreadsRunnable(Boid boid, SimpleBarrier barrier) {
        this.boid = boid;
        this.barrier = barrier;
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
                barrier.await();
            } catch (Exception ex) {
                return;
            }
            boid.updateVelocity(localModel);
            try {
                barrier.await();
            } catch (Exception ex) {
                return;
            }
            boid.updatePos(localModel);
            try {
                barrier.await();
            } catch (Exception ex) {
                return;
            }
            // the thread will wait on the model barrier until the model is updated
        }
    }
}
