package pcd.ass01.simulators;

import java.util.ArrayList;
import java.util.List;

import pcd.ass01.model.Boid;
import pcd.ass01.model.BoidsModel;
import pcd.ass01.simulators.utils.MyBarrier;

public class BoidsVirtualThreadsSimulator extends BoidsSimulator {

    public BoidsVirtualThreadsSimulator(BoidsModel model) {
        super(model);
    }

    public void runSimulation() {
        int framerate = 0;
        var boids = this.getModel().getBoids();
        final MyBarrier barrier = new MyBarrier(boids.size() + 1);
        final MyBarrier modelBarrier = new MyBarrier(boids.size() + 1);
        BoidsModel localModel = null;
        List<BoidUpdateVirtualThreadsRunnable> runnables = new ArrayList<>();
        List<Thread> threads = new ArrayList<>();

        for (Boid boid : boids) {
            runnables.add(new BoidUpdateVirtualThreadsRunnable(boid, barrier, modelBarrier));
            threads.add(Thread.ofVirtual().start(runnables.get(runnables.size() - 1)));
        }

        var t0 = System.currentTimeMillis();
        while (!this.isStopped()) {
            localModel = new BoidsModel(this.getModel());
            for (BoidUpdateVirtualThreadsRunnable runnable : runnables) {
                runnable.setBoidModel(localModel);
            }

            try {
                modelBarrier.await();
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            // Threads are updating velocities
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

            t0 = System.currentTimeMillis();

            // Wait for threads to update velocities
            try {
                barrier.await();
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            // Wait for threads to update positions
            try {
                barrier.await();
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            // Pause simulation if requested
            while (this.isPaused()) {
                try {
                    wait();
                } catch (Exception ex) {
                }
            }
        }

        // Interrupt all threads and wait for them to finish
        threads.forEach(thread -> thread.interrupt());
        threads.forEach(thread -> {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
    }
}
