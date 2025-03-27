package pcd.ass01;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class BoidsTasksSimulator {

    private BoidsModel model;
    private Optional<BoidsView> view;
    private volatile boolean isPaused = false;
    private volatile boolean isStopped = false;

    private static final int FRAMERATE = 25;
    private int framerate;
    
    public BoidsTasksSimulator(BoidsModel model) {
        this.model = model;
        view = Optional.empty();
    }

    public synchronized void attachView(BoidsView view) {
        this.view = Optional.of(view);
    }

    public synchronized void pauseSimulation() {
        isPaused = true;
    }

    public synchronized void resumeSimulation() {
        if (isPaused) {
            isPaused = false;
            notify();
        }
    }

    public synchronized void stopSimulation() {
        if (!isStopped) {
            isStopped = true;
            if (isPaused) {
                notify();
            }
        }
    }

    public synchronized boolean isStopped() {
        return isStopped;
    }

    public void runSimulation() {
        ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors()+1);

    	while (true) {
            var t0 = System.currentTimeMillis();
    		var boids = model.getBoids();
            BoidsModel modelCopy = new BoidsModel(model);
    		    		
    		/* 
    		 * Improved correctness: first update velocities...
    		 */
            List<Future<?>> velocityFutures = new ArrayList<>();
    		boids.forEach(boid -> velocityFutures.add(executor.submit(() -> boid.updateVelocity(modelCopy))));
            velocityFutures.forEach(f -> {
                try {
                    f.get();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

    		/* 
    		 * ..then update positions
    		 */
            List<Future<?>> posFutures = new ArrayList<>();
    		boids.forEach(boid -> posFutures.add(executor.submit(() -> boid.updatePos(modelCopy))));
            posFutures.forEach(f -> {
                try {
                    f.get();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
            
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

            // Pause simulation if requested
            synchronized (this) {
                while (isPaused) {
                    try {
                        wait();
                    } catch (Exception ex) {
                    }
                }
            }
        }
    }
}
