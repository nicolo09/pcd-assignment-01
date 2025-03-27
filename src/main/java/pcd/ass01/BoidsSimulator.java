package pcd.ass01;

import java.util.Optional;

public abstract class BoidsSimulator {

    private BoidsModel model;
    private Optional<BoidsView> view;
    private volatile boolean isPaused = false;
    private volatile boolean isStopped = false;

    protected static final int FRAMERATE = 25;

    public BoidsSimulator(BoidsModel model) {
        this.model = model;
        view = Optional.empty();
    }

    public synchronized void attachView(BoidsView view) {
        this.view = Optional.of(view);
    }

    protected BoidsModel getModel() {
        return model;
    }

    protected Optional<BoidsView> getView() {
        return view;
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

    protected synchronized boolean isStopped() {
        return isStopped;
    }

    protected synchronized boolean isPaused() {
        return isPaused;
    }

    public abstract void runSimulation();

}
