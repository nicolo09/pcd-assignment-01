package pcd.ass01;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class MyBarrier {

    private volatile int count = 0;
    private final int totalThreads;
    private volatile Lock lock = new ReentrantLock();

    public MyBarrier(int totalThreads) {
        this.totalThreads = totalThreads;
    }

    public void await() throws InterruptedException {
        final Lock localLock = this.lock; 
        synchronized(this){
            count++;
            if (count == totalThreads) {
                count = 0; // Reset for next use
                this.lock = new ReentrantLock(); // Create a new lock for the next barrier
                this.notifyAll(); // Notify all waiting threads
            } else {
                while (localLock == this.lock) { // Wait until the barrier is released
                    this.wait(); // Wait until all threads reach the barrier     
                }
            }
        }
    }
}
