package thread;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class Main {

	public static void main(String args[]) {

        for (int i=0; i<MotorA.CLIENT_QTY; i++) 
        { 
        	try {
				TimeUnit.MILLISECONDS.sleep(10);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
            Thread object = new Thread(new Client());        
            object.start(); 
        }         
    	
        for (int i=0; i<MotorA.POLITTICIAN_QTY; i++) 
        { 

        	try {
        		TimeUnit.MILLISECONDS.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
            Thread object = new Thread(new Politician()); 
            object.start(); 
        } 
        
    }
}
