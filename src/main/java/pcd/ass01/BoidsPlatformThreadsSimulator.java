package pcd.ass01;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CyclicBarrier;

import org.apache.commons.collections4.ListUtils;

public class BoidsPlatformThreadsSimulator {

    private BoidsModel model;
    private Optional<BoidsView> view;

    private static final int FRAMERATE = 30;
    private int framerate;

    public BoidsPlatformThreadsSimulator(BoidsModel model) {
        this.model = model;
        view = Optional.empty();
    }

    public void attachView(BoidsView view) {
        this.view = Optional.of(view);
    }

    public void runSimulation() {
        var boids = model.getBoids();

        List<List<Boid>> threadBoids = ListUtils.partition(boids,
                boids.size() / Runtime.getRuntime().availableProcessors());
        CyclicBarrier barrier = new CyclicBarrier(threadBoids.size() + 1);
        List<BoidPlatformThreadsUpdateRunnable> runnables = threadBoids.stream()
                .map(boidsList -> new BoidPlatformThreadsUpdateRunnable(boidsList, barrier)).toList();

        runnables.forEach(runnable -> new Thread(runnable).start());

        var t0 = System.currentTimeMillis();
        while (true) {
            runnables.forEach(runnable -> runnable.setModel(new BoidsModel(model)));

            try {
                barrier.await();
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (view.isPresent()) {
                view.get().update(framerate);
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
        }
    }
}
