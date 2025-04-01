package pcd.ass01.simulators.utils;

public class SimulatorStateMonitor {

    private volatile boolean isPaused = false;
    private volatile boolean isStopped = false;

    public synchronized void pauseSimulation() {
        isPaused = true;
    }

    public synchronized void resumeSimulation() {
        if (isPaused) {
            isPaused = false;
            this.notifyAll();
        }
    }

    public synchronized void stopSimulation() {
        if (!isStopped) {
            isStopped = true;
            if (isPaused) {
                this.notifyAll();
            }
        }
    }

    public synchronized boolean isStopped() {
        return isStopped;
    }

    public synchronized boolean isPaused() {
        return isPaused;
    }
}
