package pcd.ass01.simulators.utils;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class SimpleBarrier {

    private volatile int count = 0;
    private final int totalThreads;
    private final ReentrantLock lock = new ReentrantLock();
    private final Condition condition = lock.newCondition();
    private Object layer = new Object(); // Used to track the current layer of threads

    public SimpleBarrier(int totalThreads) {
        this.totalThreads = totalThreads;
    }

    /**
     * This method is called by each thread to wait at the barrier.
     * It will block until all threads have called this method.
     * After all threads have called this method, they will be released.
     *
     * @throws InterruptedException if the current thread is interrupted while
     *                              waiting
     */
    public void await() throws InterruptedException {
        lock.lock();
        try {
            final Object localLayer = this.layer; // Get the current layer
            count++;
            if (count == totalThreads) {
                count = 0; // Reset for next use
                this.layer = new Object(); // Create a new lock for the next barrier
                condition.signalAll(); // Notify all waiting threads
            } else {
                while (localLayer == this.layer) { // Wait until the barrier is released
                    condition.await(); // Wait until all threads reach the barrier
                }
            }
        } finally {
            lock.unlock();
        }
    }
}
