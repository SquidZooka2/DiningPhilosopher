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
	private int pepperShaker;
	private int[] pepperShakerStates = {0, 0}; // 0 = available, 1 = in use

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

	/*\\
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

	// synchronized method to use the pepper shaker.

	public synchronized void use_pepper_shaker(int piTID) {
		// if you are eating and you want to use the pepper shaker, you can only use it if it is available. Check PS0 and PS1.
		if(State[piTID] == philosopherState.eating && pepperShakerStates[0] == 0) {
			pepperShakerStates[0] = 1; // set the pepper shaker to in use
		}
		else if(State[piTID] == philosopherState.eating && pepperShakerStates[1] == 0) {
			pepperShakerStates[1] = 1; // set the pepper shaker to in use
		}
	}

	public synchronized void putdown_pepper_shaker(int piTID) {
		// if you are eating and you want to use the pepper shaker, you can only use it if it is available. Check PS0 and PS1.
		if(State[piTID] == philosopherState.eating && pepperShakerStates[0] == 1) {
			pepperShakerStates[0] = 0; // set the pepper shaker to available
		}
		else if(State[piTID] == philosopherState.eating && pepperShakerStates[1] == 1) {
			pepperShakerStates[1] = 0; // set the pepper shaker to available
		}
	}
	

	/**
	 * Grants request (returns) to eat when both chopsticks/forks are available.
	 * Else forces the philosopher to wait()
	 */
	public synchronized void pickUp(final int piTID)
	{
		// when a philosopher wants to eat, set their state to hungry and check if they can eat (if both neighbors are not eating)
		State[piTID] = philosopherState.hungry; // set the philosopher's state to hungry 
		
		test(piTID); // check if the philosopher can eat (if both neighbors are not eating)
		
		// if the philosopher cannot eat, they will wait until they are notified that they can eat
		while (State[piTID] != philosopherState.eating) {
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
		// current philosopher is done eating, so set their state to thinking. 
		// Then, check if the left and right neighbors can eat now that the current philosopher is done eating.
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
		// if someone else is talking, wait else -> talk
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
		// when done talking, set state to thinking and notify all waiting philosophers that they can talk now.
		State[id] = philosopherState.thinking;
		talking = false;
		notifyAll();
	}
}

// EOF
