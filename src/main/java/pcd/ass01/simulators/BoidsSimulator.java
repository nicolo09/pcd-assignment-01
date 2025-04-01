package pcd.ass01.simulators;

import java.util.Optional;

import pcd.ass01.model.BoidsModel;
import pcd.ass01.simulators.utils.SimulatorStateMonitor;
import pcd.ass01.view.BoidsView;

/**
 * Abstract class for the Boids simulator. It contains the model, the view,
 * and the state monitor. It provides methods to attach a view, pause, resume,
 * and stop the simulation.
 */
public abstract class BoidsSimulator {

    private final SimulatorStateMonitor stateMonitor;
    private final BoidsModel model;
    private Optional<BoidsView> view;

    protected static final int FRAMERATE = 25;

    public BoidsSimulator(BoidsModel model) {
        this.model = model;
        view = Optional.empty();
        stateMonitor = new SimulatorStateMonitor();
    }

    /**
     * Attaches a view to the simulator.
     *
     * @param view the view to attach
     */
    public void attachView(BoidsView view) {
        this.view = Optional.of(view);
    }

    /**
     * Returns the model of the simulator.
     * 
     * @return the model of the simulator
     */
    protected BoidsModel getModel() {
        return model;
    }

    /**
     * Stops the simulation.
     */
    public void stopSimulation() {
        stateMonitor.stopSimulation();
    }

    /**
     * Pauses the simulation.
     */
    public void pauseSimulation() {
        stateMonitor.pauseSimulation();
    }

    /**
     * Resumes the simulation.
     */
    public void resumeSimulation() {
        stateMonitor.resumeSimulation();
    }

    /**
     * Returns the view of the simulator.
     * 
     * @return the view of the simulator
     */
    protected Optional<BoidsView> getView() {
        return view;
    }

    /**
     * @return the simulator state monitor
     */
    protected SimulatorStateMonitor getStateMonitor() {
        return stateMonitor;
    }

    /**
     * Runs the simulation. This method should be implemented by the subclasses.
     * It should contain the main loop of the simulation, which updates the model
     * and the view at each iteration. The loop should check if the simulation is
     * paused or stopped and act accordingly. The loop should also check if the
     * simulation is paused and wait for the resume signal before continuing.
     */
    public abstract void runSimulation();

}
