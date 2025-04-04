package pcd.ass01.simulators;

import java.util.List;

import org.apache.commons.collections4.ListUtils;

import pcd.ass01.model.Boid;
import pcd.ass01.model.BoidsModel;
import pcd.ass01.simulators.utils.SimpleBarrier;

public class BoidsPlatformThreadsSimulator extends BoidsSimulator {

    public BoidsPlatformThreadsSimulator(BoidsModel model) {
        super(model);

    }

    public void runSimulation() {
        int framerate = 0;
        var boids = this.getModel().getBoids();

        List<List<Boid>> threadBoids = ListUtils.partition(boids,
                boids.size() / (Runtime.getRuntime().availableProcessors() + 1));
        SimpleBarrier barrier = new SimpleBarrier(threadBoids.size() + 1);
        List<BoidPlatformThreadsUpdateRunnable> runnables = threadBoids.stream()
                .map(boidsList -> new BoidPlatformThreadsUpdateRunnable(boidsList, barrier)).toList();
        List<Thread> threads = runnables.stream()
                .map(runnable -> new Thread(runnable)).toList();
        threads.forEach(Thread::start);

        var t0 = System.currentTimeMillis();
        while (!this.getStateMonitor().isStopped()) {
            runnables.forEach(runnable -> runnable.setModel(new BoidsModel(this.getModel())));

            try {
                barrier.await();
            } catch (Exception e) {
                e.printStackTrace();
            }

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

            try {
                barrier.await();
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                barrier.await();
            } catch (Exception e) {
                e.printStackTrace();
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

        threads.forEach(Thread::interrupt);
        threads.forEach(t -> {
            try {
                t.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
    }
}
