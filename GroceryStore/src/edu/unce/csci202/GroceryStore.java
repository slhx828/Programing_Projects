package edu.unce.csci202;

import java.util.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.List;
import java.util.Queue;
import java.util.Random;









public class GroceryStore<T extends Comparable<T>>{
	
	ArrayList<ArrayDeque<Customer>> lines ;
	
	private int numLines;
	private int totalCusts;
	private int maxItems;
	private int[] maxLenQ;
	
	private int simulationDuration ;
	private double arrivalProbability;
	private int timeToScanItem  ;
	
	
	
	
	public GroceryStore (int numLines) {
		
		this.numLines = numLines;
		this.totalCusts = 0;
		this.maxLenQ = new int [numLines];
		
		
		for(int l = 1; l <= numLines+1; l++) {	
			
			lines = new ArrayList<ArrayDeque<Customer>>();
			lines.add(new ArrayDeque<Customer>());
		}
		
		for(int j=0; j<numLines; j++) {
			this.maxLenQ[j] = 0;
		}
		
	
	}
		
	
	public  int getShortestQueue() {
					
		int queShortestLine = 0;
		int shortLineChkSize = 0;			
		int linesSize = numLines;
		int numQueues = 0;   
		int shortestLine = numLines + 1;  
			while (numQueues < linesSize) {
						
				ArrayDeque shortLineChk = new ArrayDeque<Customer>() ;   
				shortLineChk = lines.get(numQueues);   
				shortLineChkSize = shortLineChk.size();  
				
						if (shortLineChkSize < shortestLine) {   
							shortestLine = shortLineChkSize;   
							queShortestLine = numQueues;      	
						}
						
						numQueues++;
					
						
					}
					
					
						// returns the index of the shortest queue
						 
						 return queShortestLine;
					}
						 
	 
	public void run(int simulationDuration, double arrivalProbability, int timeToScanItem, int maxItems ) {
		
		this.simulationDuration = simulationDuration;
		this.arrivalProbability =  arrivalProbability;
		this.timeToScanItem = timeToScanItem;
		this.maxItems = maxItems;
		
		
		for (int timeSlice = 0; timeSlice <= simulationDuration; timeSlice++) {	
			
			int totalCust = 0;
			Random gencust = new Random();
			Random genItem = new Random();
			
			
			if (gencust.nextDouble() <= arrivalProbability) {
				
				if (lines.isEmpty() == true ||lines.size() < numLines) {
					
					ArrayDeque myLine = new ArrayDeque<Customer>() ;
					Customer myCust = new Customer( genItem.nextInt(maxItems) + 1,timeToScanItem);
					myLine.add(myCust);
					lines.add(myLine);
					
					this.totalCusts++;
				}else {
		
					
					ArrayDeque myLine = new ArrayDeque<Customer>();
					int shortestQueue;
					shortestQueue = getShortestQueue();
					myLine=lines.get(shortestQueue);
					Customer customer = new Customer( genItem.nextInt(maxItems) + 1,timeToScanItem);
					myLine.add(customer);
					lines.set(shortestQueue, myLine);
					
					this.totalCusts++;
				}
			}
		}
	
				
					 
				//process the customers out
			
			for (int numQueues = 0; numQueues < lines.size(); numQueues++) {
				
				ArrayDeque myLine;
				myLine = lines.get(numQueues); // grab a queue
				
				
				for (int numCusts = 0; numCusts < myLine.size(); numCusts++) { // grab a customer in the queue
					
					Customer myCust;
					myCust = (Customer) myLine.element();
					myCust.decrTimeRem();
					                                                      //check the customer out
					 //remove customer if time remaining is zero
					if (myCust.getTimeRem() == 0) {
                        myLine.remove();
					}
					
					lines.set(numQueues, myLine);       //if the customer is still being processed, they are still need to be in line
				}
				
				if ( myLine.size() > this.maxLenQ[numQueues] ) this.maxLenQ[numQueues] = myLine.size();  //this tracks the max length of a line
			}
			
			
		
	
		
		     
	}//end run
					
					


	
												
				 				
		
		

	
	public void PrintData() {
		System.out.println("Time Steps Run " + Integer.toString(this.simulationDuration));
		System.out.println("Number Of Lines " + Integer.toString(this.numLines));
		System.out.println("Customer Arrival Probability " + Double.toString(this.arrivalProbability));
		System.out.println("Time Per Item " + Integer.toString(this.timeToScanItem));
		System.out.println("Max Items Per Customer " + Integer.toString(this.maxItems));
		
		for (int numQueues = 0; numQueues < lines.size(); numQueues++) {
			System.out.println("Max Customers In Line " + Integer.toString(numQueues + 1) + " Is " + Integer.toString(this.maxLenQ[numQueues]));
		}
		
		for (int numQueues = 0; numQueues < lines.size(); numQueues++) {
			ArrayDeque myLine;
			myLine = lines.get(numQueues);
			System.out.println("Number of Customers Currently In Line " + Integer.toString(numQueues + 1) + " Is " + Integer.toString(myLine.size()));						
		}
		System.out.println("Total Customers Served " + Integer.toString(this.totalCusts));
		
	}	//end print
	
	
		
	
	
	
}//end class

         
		
    	
	
	
		
		
  

