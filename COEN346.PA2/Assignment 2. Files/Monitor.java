/**
 * Class Monitor
 * To synchronize dining philosophers.
 *
 * @author Serguei A. Mokhov, mokhov@cs.concordia.ca
 */
public class Monitor
{
	
	/*
	 * ------------
	 * Data members
	 * ------------
	 */
	
	private int philosopherNumber;
	private enum philosopherState{thinking, eating, talking, hungry};
	private philosopherState[] State;
	private boolean talking = false;


	/**
	 * Constructor
	 */
	public Monitor(int piNumberOfPhilosophers)
	{
		// TODO: set appropriate number of chopsticks based on the # of philosophers
		philosopherNumber = piNumberOfPhilosophers;
		State = new philosopherState[philosopherNumber];
		for (int i = 0; i <philosopherNumber; i++) {
			State[i] = philosopherState.thinking;
		}
	}

	/*
	 * -------------------------------
	 * User-defined monitor procedures
	 * You may need to add more procedures for task 5
	 * -------------------------------
	 */
	
	private int left(int i)
	{
	    return (i + philosopherNumber - 1) % philosopherNumber;
	}

	private int right(int i)
	{
	    return (i + 1) % philosopherNumber;
	}
	
	private void test(int piTID) {
			if (State[left(piTID)] != philosopherState.eating
					&& State[right(piTID)] != philosopherState.eating
					&& State[piTID] == philosopherState.hungry) {
				State[piTID] = philosopherState.eating;
		}
	}
	
	private void test_talking(int piTID) {
		if ()
	}
	

	/**
	 * Grants request (returns) to eat when both chopsticks/forks are available.
	 * Else forces the philosopher to wait()
	 */
	public synchronized void pickUp(final int piTID)
	{
		State[piTID] = philosopherState.hungry;
		
		test(piTID);
		
		if (State[piTID] != philosopherState.eating) {
			try {
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
	}

	/**
	 * When a given philosopher's done eating, they put the chopstiks/forks down
	 * and let others know they are available.
	 */
	public synchronized void putDown(final int piTID)
	{
		State[piTID] = philosopherState.thinking;
		test(left(piTID));
		test(right(piTID));
		
	}

	/**
	 * Only one philopher at a time is allowed to philosophy
	 * (while she is not eating).
	 */
	public synchronized void requestTalk(int id)
	{
		if (talking){
			try {
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		talking = true;
		State[id] = philosopherState.talking;
	}

	/**
	 * When one philosopher is done talking stuff, others
	 * can feel free to start talking.
	 */
	public synchronized void endTalk(int id)
	{
		State[id] = philosopherState.thinking;
		talking = false;
		notifyAll();
	}
}

// EOF
