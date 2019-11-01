package thread;

import java.util.ArrayList;
import java.util.concurrent.locks.Lock;

class MotorA {

    private static MotorA motor = new MotorA(); 
    private static ArrayList<String> letter_pool =new ArrayList<String>();
    private static final java.util.concurrent.locks.Lock lock = new java.util.concurrent.locks.ReentrantLock();

    private MotorA() { }

    public static MotorA getMotorA() {
    return motor;
    }

    public static Lock getLock() {
    return lock;
    }
    public static void addLetter(String s) {
    	lock.lock();
    	letter_pool.add(s);
    	lock.unlock();
        }
    
    public void showLetterPool() {
		lock.lock();
		try {
			System.out.println("show letter pool-------------------------------------------------");
			if (letter_pool.size() > 0) {
				for (String s : letter_pool) {
					 System.out.println(s);
				}
			}
			System.out.println("finish show letter pool------------------------------------------");
		} finally {
			lock.unlock();
		}

	}
    /* here go business methods for MotorA */

 }
