package thread;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class Main {

	public static void main(String args[]) {
		int n = 18; // Number of clients 
		ArrayList<Thread> clients = new ArrayList<Thread>();
        for (int i=0; i<n; i++) 
        { 
        	try {
				TimeUnit.MILLISECONDS.sleep(10);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
            Thread object = new Thread(new Client()); 
            clients.add(object);
            object.start(); 
        }         
    	
        int m =3; // Number of politicians 
        for (int i=0; i<m; i++) 
        { 

        	try {
				TimeUnit.MILLISECONDS.sleep(10);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
            Thread object = new Thread(new Politician()); 
            object.start(); 
        } 
        
    }
}
