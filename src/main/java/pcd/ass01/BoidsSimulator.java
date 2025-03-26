package pcd.ass01;

import java.util.ArrayList;
import java.util.List;
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

        var numThreads = Runtime.getRuntime().availableProcessors() + 1;

        var threadsBarrier = new CyclicBarrier(numThreads);
        var viewBarrier = new CyclicBarrier(numThreads + 1); // Considering also main thread

        List<Thread> threads = new ArrayList<>();

        for(int i = 0; i < numThreads; i++) {
            var boids = model.getBoids().subList(i * model.getBoids().size() / numThreads, (i + 1) * model.getBoids().size() / numThreads);
            var thread = new UpdateMultipleBoidsThread(boids, model, threadsBarrier, viewBarrier);
            threads.add(thread);
        }

        for (var thread : threads) {
            thread.start();
        }

        try {
            viewBarrier.await();
        } catch (Exception e) {
            e.printStackTrace();
        }

    	while (true) {
            var t0 = System.currentTimeMillis();

            try {
                viewBarrier.await();
            } catch (Exception e) {
                e.printStackTrace();
            }
            
    		if (view.isPresent()) {
            	view.get().update(framerate);
            	var t1 = System.currentTimeMillis();
                var dtElapsed = t1 - t0;
                var framratePeriod = 1000/FRAMERATE;
                
                if (dtElapsed < framratePeriod) {		
                	try {
                		Thread.sleep(framratePeriod - dtElapsed);
                	} catch (Exception ex) {
                        ex.printStackTrace();
                    }
                	framerate = FRAMERATE;
                } else {
                	framerate = (int) (1000/dtElapsed);
                }
    		}

            try {
                viewBarrier.await();
            } catch (Exception e) {
                e.printStackTrace();
            }
            
    	}
    }
}
