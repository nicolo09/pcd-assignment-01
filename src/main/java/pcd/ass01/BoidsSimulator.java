package pcd.ass01;

import java.util.Optional;

public abstract class BoidsSimulator {

    private BoidsModel model;
    private Optional<BoidsView> view;

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

    public abstract void pauseSimulation();

    public abstract void resumeSimulation();

    public abstract void stopSimulation();

    public abstract void runSimulation();

}
