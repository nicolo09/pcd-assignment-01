package pcd.ass01;

import java.util.Optional;

public abstract class BoidsSimulator {

    private BoidsModel model;
    private Optional<BoidsView> view;
    private SimulatorStateMonitor stateMonitor;
    

    protected static final int FRAMERATE = 25;

    public BoidsSimulator(BoidsModel model) {
        this.model = model;
        view = Optional.empty();
        stateMonitor = new SimulatorStateMonitor();
    }

    public void attachView(BoidsView view) {
        this.view = Optional.of(view);
    }

    protected BoidsModel getModel() {
        return model;
    }

    protected Optional<BoidsView> getView() {
        return view;
    }

    public void pauseSimulation() {
        stateMonitor.pauseSimulation();
    }

    public void resumeSimulation() {
        stateMonitor.resumeSimulation();
    }

    public void stopSimulation() {
        stateMonitor.stopSimulation();
    }

    protected boolean isStopped() {
        return stateMonitor.isStopped();
    }

    protected boolean isPaused() {
        return stateMonitor.isPaused();
    }

    public abstract void runSimulation();

}
