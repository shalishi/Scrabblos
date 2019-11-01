package thread;

public class Politician implements Runnable {

	@Override
	public void run() {
		try
        { 
            // Displaying the thread that is running 
            System.out.println ("Thread Politician" + 
            		Thread.currentThread().getId() + 
                                " is running"); 
            while(true) {
            readLetterPool();
            }
  
        } 
        catch (Exception e) 
        { 
            // Throwing an exception 
            System.out.println ("Exception is caught : " + e.toString() + e.getCause().getMessage()); 
        } 
		
	}
	
	protected void readLetterPool() {
		System.out.println ("Thread Politician is reading letter pool*********************************************");
		  synchronized(MotorA.getMotorA()) {
	            MotorA motor = MotorA.getMotorA();	     
	            motor.showLetterPool();
	        }
		System.out.println ("Thread Politician is finished of reading letter pool**********************************");

	}

}