package pcd.ass01;

import java.util.Optional;
import java.util.concurrent.CyclicBarrier;

public class BoidsSimulator {

    private BoidsModel model;
    private Optional<BoidsView> view;
    
    private static final int FRAMERATE = 25;
    private int framerate;
    
    public BoidsSimulator(BoidsModel model) {
        this.model = model;
        view = Optional.empty();
    }

    public void attachView(BoidsView view) {
    	this.view = Optional.of(view);
    }
      
    public void runSimulation() {
        var boids = model.getBoids();
        final CyclicBarrier barrier = new CyclicBarrier(boids.size()+1);

        for (Boid boid : boids) {
            Thread.ofVirtual().start(() -> {
                while (true) {
                    BoidsModel localModel = new BoidsModel(model);
                    boid.updateVelocity(localModel);
                    try {
                        barrier.await();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                    boid.updatePos(localModel);
                    try {
                        barrier.await();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });
        }

        var t0 = System.currentTimeMillis();
    	while (true) {
            
            // Threads are updating velocities
            if (view.isPresent()) {
                view.get().update(framerate);
            	var t1 = System.currentTimeMillis();
                var dtElapsed = t1 - t0;
                var framratePeriod = 1000/FRAMERATE;
                
                if (dtElapsed < framratePeriod) {		
                    try {
                        Thread.sleep(framratePeriod - dtElapsed);
                	} catch (Exception ex) {}
                	framerate = FRAMERATE;
                } else {
                    framerate = (int) (1000/dtElapsed);
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
            
    	}
    }
}
