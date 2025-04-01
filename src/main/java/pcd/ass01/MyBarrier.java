package pcd.ass01;

public class MyBarrier {

    private volatile int count = 0;
    private final int totalThreads;
    private volatile Object layer = new Object();

    public MyBarrier(int totalThreads) {
        this.totalThreads = totalThreads;
    }

    /**
     * This method is called by each thread to wait at the barrier.
     * It will block until all threads have called this method.
     * After all threads have called this method, they will be released.
     *
     * @throws InterruptedException if the current thread is interrupted while waiting
     */
    public void await() throws InterruptedException {
        final var localLayer = this.layer;
        synchronized (this) {
            count++;
            if (count == totalThreads) {
                count = 0; // Reset for next use
                this.layer = new Object(); // Create a new lock for the next barrier
                this.notifyAll(); // Notify all waiting threads
            } else {
                while (localLayer == this.layer) { // Wait until the barrier is released
                    this.wait(); // Wait until all threads reach the barrier
                }
            }
        }
    }
}
