package edu.unce.csci202;



public class StoreDriver {

	public static <E> void main(String[] args) {
		
		int simulationDuration = 10;
		double probCustAppear = 0.34;
		int timeToScanItem = 1;
		int maxCustItems = 3;
		
		GroceryStore myStore = new GroceryStore(4);
		myStore.run(simulationDuration, probCustAppear, timeToScanItem, maxCustItems);
		myStore.PrintData();
		
	}
	

}
