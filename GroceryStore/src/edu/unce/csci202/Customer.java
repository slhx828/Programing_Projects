package edu.unce.csci202;

public class Customer {
	
	private int items;
	private int timeRem;
	private int timeToProcess;
	
	//
	public Customer(int items, int timeToProcess) {
		this.items = items;
		this.timeToProcess = timeToProcess;
		this.timeRem = items * timeToProcess;
	}

	public int getItems() {
		return items;
	}

	public int getTimeRem() {
		return timeRem;
	}
	
	public void decrTimeRem() {
		this.timeRem = this.timeRem - 1;	
	}

}
