package edu.unca.csci202;

import java.util.Scanner;
import java.util.Random;
import java.util.Stack;
/**
 * Game board configures the game play of mine sweeper. The objective of the game is to correctly guess where all 
 * of the mines on the game board are located and clear them out.
 * - are un guessed locations
 * M represent where mines are located
 * 0 represent cells that have no mines in any touching cells
 * cells that have no mines but have mines touching that cell will have the 
 * number of adjacent mines displayed in the cell.
 *  
 * @author Sarah Hendricks
 *
 */
public class Gameboard {
	
	  
	  private static final int BOARDSIZE = 8;	
	  private static final int MINES = 10;	
	  
	  /**
	   *  class for Cell 
	   *  it will have a value that gets displayed and indicate if it contains a mine
	   *  
	   * @author Sarah Hendricks
	   *
	   */
	  private static class Cell {
		    
		   
		    private char showChar;
		    private boolean mineCellQ;
		    private boolean processedQ;  
		    private int numAdjMines;  
		    
		    
		    public Cell() {
		    	showChar = '-';
		    	mineCellQ = false;
		    	processedQ = false;   
		    	numAdjMines = 0;  
		    }
		  }	  
	  
	  /**
	   *   array that will match the boardsize (square) and will contain Cell objects
	   */
	  private Cell[][] board;
	  
	  /**
	   *  variables to determine if game is won if all mines are guessed
	   */
	  private int numberOfMines = 0;
	  private int guessedMines = 0;	  
	  
	  
	  
	  private boolean continueGame = true ;
	  private boolean loseGame = false;
	  
	
	  private String answer = ""; 
	  private int myRow = 0;
	  private int myCol = 0;
	  
	  
	  private Scanner getResponse = new Scanner(System.in);	  

	  
	  /**
	   *  main processing routine
	   */
	  public void run() {
		  
		    System.out.println("Starting MineSweeper.\n");
		    
		    /**
		     *  initialize the board array
		     */
		    board = new Cell[BOARDSIZE][BOARDSIZE];
		    
		    
		    numberOfMines = MINES;
		    guessedMines = 0;	   
		    initializeBoard();
		    
		    /**
		     *  show the masked board
		     */
		    showBoard();
		    
		    /**
		     *  put mines in cells
		     */
		    setMines();
		    
		   
		    
		    /**
		     *  main loop for program
		     */
		    while (continueGame == true)
		    {
		    
			    
			    System.out.print("Would you like to peek? (Type 'yes' or 'no'): ");
			    String answer = getResponse.nextLine().toLowerCase();
			    if (answer.equals("yes") == true)
			    	{
			    	showBoardPeek();     
			    }
			    else {
			    	if (answer.equals("no") == false) {
			    		System.out.print("Invalid response, must be 'yes' or 'no'.  Ignoring input...\n");
			    	}
			    }
			    
			    
			    System.out.print("Enter Row 1 - 8: ");
			    String row = getResponse.nextLine().trim();
			    if (row.equals("1") || row.equals("2") || row.equals("3") || row.equals("4") || row.equals("5") || row.equals("6") || row.equals("7") || row.equals("8") )
			    	{
			    	myRow = Integer.parseInt(row);   
			    }
			    else {
			    		System.out.print("Invalid response, must be 1 - 8.  Defaulting input to 1 ...\n");
			    		myRow = 1;
			    		row = "1";
			    }
			    
			  
			    System.out.print("Enter Column 1 - 8: ");
			    String col = getResponse.nextLine().trim();
			    if (col.equals("1") || col.equals("2") || col.equals("3") || col.equals("4") || col.equals("5") || col.equals("6") || col.equals("7") || col.equals("8") )
			    	{
			    	myCol = Integer.parseInt(col);   
			    }
			    else {
			    		System.out.print("Invalid response, must be 1 - 8.  Defaulting input to 1 ...\n");
			    		myCol = 1;
			    		col = "1";
			    }
			    
			   
			    boolean mineCheck = false;
			    System.out.print("Does the cell at row " + row + " and column " + col + " contain a mine? (Type 'yes' or 'no'): ");
			    String doesCellHaveMine = getResponse.nextLine().toLowerCase();
			    if (doesCellHaveMine.equals("yes") == true)
			    	{
			    	mineCheck = true;   
			    }
			    else {
			    	mineCheck = false;
			    	if (doesCellHaveMine.equals("no") == false) {
			    		System.out.print("Invalid response, must be 'yes' or 'no'.  Assuming answer is 'no'...\n");
			    	}
			    }
			    
			    
			    /**
			     *  check cell based on user input of row and column and if they think cell contains mine
			     */
			    Cell myCell = board[myRow - 1][myCol - 1];
			    if (mineCheck == true) {
			    	if (myCell.mineCellQ == true) {
			    		
			    		/**
			    		 *  correct guess - has mine, change show character to "M"
			    		 */
			    		myCell.showChar = 'M';   
			    		
			    		/**
			    		 * update the array
			    		 */
			    		board[myRow - 1][myCol - 1] = myCell;
			    		
			    		/**
			    		 *  add one to the number of mines found
			    		 */
			    		guessedMines = guessedMines + 1;
			    		System.out.print("Correct.\n");			    				    		
			    	}
			    	else
			    	{
			    		/**
			    		 *  cell did not contain mine and they guessed it did 
			    		 */
			    		loseGame = true;
			    	}	    	
			    }
			    if (mineCheck == false) {
			    	int countAdj = 0;
			    	char charCountAdj;
			    	if (myCell.mineCellQ == false) {
			    		/**
			    		 *  correct guess - check for number of adjacent mines, cast as character
			    		 */
			    		countAdj = scanAreaForMines(myRow - 1,myCol - 1); 
			    		if (countAdj != 0) {
				    		charCountAdj = (char)(countAdj + '0');
				    		myCell.showChar = charCountAdj;		
				    		myCell.processedQ = true;
				    		board[myRow - 1][myCol - 1] = myCell;
			    		}
			    		
			    		
			    		/**
			    		 * expands: clears out adjacent cells containing zeros
			    		 */
			    		if (countAdj == 0) {
				    		MyPosition mypos = new MyPosition();
				    		mypos.row = myRow - 1;				    		
				    		mypos.col = myCol - 1;
				    		expand(mypos);
				    		getZeroEdge();
				    		 			    			
			    		}
			    		
			    		
			    		System.out.print("Correct.\n");			    				    		
			    	}
			    	else
			    	{
			    		/**
			    		 * Cell contained mine and they guessed it did not
			    		 */
			    		loseGame = true;
			    	}	    	
			    }
			    
			    /**
			     *  check to see if game lost by incorrect guess
			     */
			    
			    if (loseGame == true) {
			    	/**
			    	 *  show board with mines showing to show user why the lost
			    	 */
			    	showBoardPeek();
			    	
				    System.out.print("Sorry you lost game. \n");	{			    	
				    }
				    continueGame = false;
			    }
			    else {
			    	/**
			    	 *  show the board with the new updated cell information
			    	 */
			    	showBoard();
			    	
			    	/**
			    	 *  check to see if game is won by number of mines found
			    	 */
			    	if (guessedMines == numberOfMines) {
					    System.out.print("Congratulations, you won! \n");				    	
			    		continueGame = false;
			    	}
			    }
			    	
			    
			   
			    if (loseGame == true || guessedMines == numberOfMines) {
				    System.out.print("Would you like to play another game? (Type 'yes' or 'no'): ");
				    String newGame = getResponse.nextLine().toLowerCase();
				    if (newGame.equals("yes") == true)
				    	{
				    	continueGame = true;   
					    
					    /**
					     *  initialize number of mines and how many guessed (zero to start game)
					     */
					    numberOfMines = MINES;
					    guessedMines = 0;	   
					    initializeBoard();
					    
					    /**
					     *  show the masked board
					     */
					    showBoard();
					    /**
					     *  put mines in cells
					     */
					    setMines();
					    
				    }
		    	
			    }

		    }  
	  
	  } /**
	  * end main run routine
	  */
	  
	  /**
	   *  helper routines 
	   */
	  
	  private void initializeBoard() {
		    for (int i=0; i<board.length; i++) 
		      for (int j=0; j<board.length; j++) 
		        board[i][j] = new Cell();
		  }  
	  /**
	   * initialize display line for each row before building the column display information
	   *  go through each column for row and build the display string
	   *   when all columns have been added to display line, print it out for each row
	   */
	  private void showBoard() {
		   
		    System.out.print(" \n");
		    
		  	String spacer = "  ";
		  	String displayLine = "";
		    
		  	for (int i=0; i<board.length; i++) {
		    	  
		    	  displayLine = "";
		    	  
			      for (int j=0; j<board.length; j++) {
			    	  	Cell myCell = board[i][j];
			    	  	
				        displayLine = displayLine + myCell.showChar + spacer;    				    	 
			      }
			      
			      System.out.println(displayLine);		
			      
		    }		    	  		  
	  }
       /**
        * allows user to see mine location
        */
	  private void showBoardPeek() {
		   
		    System.out.print(" \n");
		    
     	  	String spacer = "  ";
		  	String displayLine = "";
		    for (int i=0; i<board.length; i++) {
		    	  displayLine = "";
			      for (int j=0; j<board.length; j++) {
			    	  	Cell myCell = board[i][j];
			    	  	if (myCell.mineCellQ == true) {
					        displayLine = displayLine + "M" + spacer;    				    	 			    	  		
			    	  	}
			    	  	else {
					        displayLine = displayLine + myCell.showChar + spacer;    				    	 			    	  		
			    	  	}
			      }
			      System.out.println(displayLine);					      
		    }		    	  		  
	  }
	  /**
	   * check to see if the location has a mine already, if not then set cell mine attribute to true
	   */
	  public void setMines() {
		    Random mineRow = new Random();
		    Random mineCol = new Random();
		    int numMinesCnt = 0;
		    int row, column;
		    
		    while (numMinesCnt < numberOfMines) { 
		      row = mineRow.nextInt(BOARDSIZE);
		      column = mineCol.nextInt(BOARDSIZE);
      	  	  Cell myCell = board[row][column];
		      
		      if (myCell.mineCellQ == false) {
		    	myCell.mineCellQ = true;  
		    	myCell.numAdjMines = -1;   
		        board[row][column] = myCell;
		        numMinesCnt++;
		        
		       /**
		        * above
		        */
		        Cell adjCell;
		        if (row > 0) { 
		        	adjCell = board[row-1][column];
		        	adjCell.numAdjMines++;  
		        	board[row-1][column] = adjCell;
		        }
		          /**
			        * below
			        */
		        if (row < BOARDSIZE-1) { 
		        	adjCell = board[row+1][column];
		        	adjCell.numAdjMines++;   
		        	board[row+1][column] = adjCell;
		        }
		          /**
			        * right
			        */
		        if (column < BOARDSIZE-1) { 
		        	adjCell = board[row][column+1];
		        	adjCell.numAdjMines++;  
		        	board[row][column+1] = adjCell;
		        }
		      /**
		       * left
		       * 
		       */
		        if (column > 0) { 
		        	adjCell = board[row][column-1];
		        	adjCell.numAdjMines++;   
		        	board[row][column-1] = adjCell;
		        }
		        
		        
		        
		      }
		    }
		  }

	  /**
	   * for debuging purposes
	   */
	  private void showAdjCounts() {
		    int numAdjMines = 0;
		    String displayLine = "";
		    for (int i=0; i<BOARDSIZE; i++) {
			      for (int j=0; j<=BOARDSIZE; j++) {
			    	  try {
			    		  numAdjMines = board[i][j].numAdjMines;
			    		  if (numAdjMines != 0) {
			    			  displayLine = "i = " + Integer.toString(i) + ", j = " + Integer.toString(j) + ", numAdj = " + Integer.toString(numAdjMines);
			    			  System.out.println(displayLine);	 
			    		  }
				        
			    	  }
			    	  catch(ArrayIndexOutOfBoundsException ignored) {
			    	  }		
			      }
		    }
		  
	  }
	  /**
	   * scans cell neighbors for mines 
	   * @param row
	   * @param column
	   * @return
	   */
	  private int scanAreaForMines(int row, int column) {
		    int mineCount = 0;
		    for (int i=row-1; i<=row+1; i++) {
			      for (int j=column-1; j<=column+1; j++) {
			    	  try {
				        if (board[i][j].mineCellQ) {
					          mineCount++;
				        }
			    	  }
			    	  catch(ArrayIndexOutOfBoundsException ignored) {
			    	  }		
			      }
		    }
		    /**
		     *  don't count current cell
		     */
		    if (board[row][column].mineCellQ)
		      mineCount--;	    
		    return mineCount;
		  }	  
	  /**
	   * this method scans the wall between the zeroed out cells and where mines lay 
	   * it allows those cells to display the numbers of the surrounding mines
	   */
	  private void getZeroEdge() {
		    int countAdj = 0;
		    char charCountAdj;
		    Cell myCell;
		    int newRow = 0;
		    int newCol = 0;
		    for (int row=0; row<=BOARDSIZE-1; row++) {
			      for (int col=0; col<=BOARDSIZE-1; col++) {
			    	  String charValue = Character.toString(board[row][col].showChar);
			    	  if (charValue.equals("0") == true) {
			    		  
			    		  if (row > 0) { 
			    			    /**
			    			     *  above
			    			     */
			    			  	newRow = row - 1;
			    			  	newCol = col;			    			  	
					    		countAdj = scanAreaForMines(newRow,newCol); 
					    		if (countAdj != 0) {
					    			myCell = board[newRow][newCol];
						    		charCountAdj = (char)(countAdj + '0');
						    		myCell.showChar = charCountAdj;		
						    		myCell.processedQ = true;
						    		board[newRow][newCol] = myCell;
					    		}	
			    			    /**
			    			     *  above, left
			    			     */
					    		if (col > 0) {
				    			  	newCol = col - 1;			    			  	
						    		countAdj = scanAreaForMines(newRow,newCol); 
						    		if (countAdj != 0) {
						    			myCell = board[newRow][newCol];
							    		charCountAdj = (char)(countAdj + '0');
							    		myCell.showChar = charCountAdj;		
							    		myCell.processedQ = true;
							    		board[newRow][newCol] = myCell;
						    		}					    							    			
					    		}
			    			    /**
			    			     *  above right
			    			     */
					    		if (col < BOARDSIZE - 1) {
				    			  	newCol = col + 1;			    			  	
						    		countAdj = scanAreaForMines(newRow,newCol); 
						    		if (countAdj != 0) {
						    			myCell = board[newRow][newCol];
							    		charCountAdj = (char)(countAdj + '0');
							    		myCell.showChar = charCountAdj;		
							    		myCell.processedQ = true;
							    		board[newRow][newCol] = myCell;
						    		}					    							    			
					    		}
				    		
			    		  }  /**
			    		  * end of top row
			    		  */
			    		  
			    		  
			    		  if (row < BOARDSIZE - 1) {  
			    			    /**
			    			     *  below
			    			     */
			    			  	newRow = row + 1;
			    			  	newCol = col;			    			  	
					    		countAdj = scanAreaForMines(newRow,newCol); 
					    		if (countAdj != 0) {
					    			myCell = board[newRow][newCol];
						    		charCountAdj = (char)(countAdj + '0');
						    		myCell.showChar = charCountAdj;		
						    		myCell.processedQ = true;
						    		board[newRow][newCol] = myCell;
					    		}	
			    			    /**
			    			     *  below, left
			    			     */
					    		if (col > 0) {
				    			  	newCol = col - 1;			    			  	
						    		countAdj = scanAreaForMines(newRow,newCol); 
						    		if (countAdj != 0) {
						    			myCell = board[newRow][newCol];
							    		charCountAdj = (char)(countAdj + '0');
							    		myCell.showChar = charCountAdj;		
							    		myCell.processedQ = true;
							    		board[newRow][newCol] = myCell;
						    		}					    							    			
					    		}
			    			    /**
			    			     *  below, right
			    			     */
					    		if (col < BOARDSIZE - 1) {
				    			  	newCol = col + 1;			    			  	
						    		countAdj = scanAreaForMines(newRow,newCol); 
						    		if (countAdj != 0) {
						    			myCell = board[newRow][newCol];
							    		charCountAdj = (char)(countAdj + '0');
							    		myCell.showChar = charCountAdj;		
							    		myCell.processedQ = true;
							    		board[newRow][newCol] = myCell;
						    		}					    							    			
					    		}
				    		
			    		  }  /**
			    		  * end of bottom row
			    		  */
			    		  
			    		  /**
			    		   *  check left
			    		   */
			    		  if (col > 0) {
			    			  	newRow = row;
			    			  	newCol = col - 1;			    			  	
					    		countAdj = scanAreaForMines(newRow,newCol); 
					    		if (countAdj != 0) {
					    			myCell = board[newRow][newCol];
						    		charCountAdj = (char)(countAdj + '0');
						    		myCell.showChar = charCountAdj;		
						    		myCell.processedQ = true;
						    		board[newRow][newCol] = myCell;
					    		}					    							    			
			    			  
			    		  }
			    		  
			    		  
			    		  /**
			    		   *  check right
			    		   */
			    		  if (col < BOARDSIZE-1) {
			    			  	newRow = row;
			    			  	newCol = col + 1;			    			  	
					    		countAdj = scanAreaForMines(newRow,newCol); 
					    		if (countAdj != 0) {
					    			myCell = board[newRow][newCol];
						    		charCountAdj = (char)(countAdj + '0');
						    		myCell.showChar = charCountAdj;		
						    		myCell.processedQ = true;
						    		board[newRow][newCol] = myCell;
					    		}					    							    			
			    			  
			    		  }			    		  
			    		  
			    	  }
			      }
		    }
			  
	  }
	  
	  
	  /**
	   *  new routine added for stack./ use stack for traversing grid and putting "work" on "to do list" collects un guessed cells around a zero cell(cell with no mines surrounding it)
	   * @param mypos
	   */
	    public void expand(MyPosition mypos)
	    {
	    	
	    	
	    	int scanMinesCnt = 0;
	    	char charCountAdj;
	    	
	    	
	    	/**
	    	 * initialize stack with current position cell
	    	 */
	    	Stack<MyPosition> mystack = new Stack<MyPosition>();
	    	mystack.push(mypos);
	    	/**
	    	 *  set current position on grid
	    	 */
	    	while(!mystack.empty()) {
	    		mypos = mystack.pop();  
	    		Cell mycell = board[mypos.row][mypos.col];
	    		
	    		if (mycell.mineCellQ == false && mycell.processedQ == false && mycell.numAdjMines == 0) {
	    			mycell.processedQ = true;
	    			if (scanAreaForMines(mypos.row,mypos.col) == 0) {
		    			mycell.showChar = '0';	    				
	    			}
	    			board[mypos.row][mypos.col] = mycell;
	    			
	    			
	    			/**
	    			 *  now get the next one to process
	    			 *   check above
	    			 */
	    			MyPosition chkpos;
	    			if (mypos.row > 0) { 
	    				
	    				chkpos = new MyPosition(mypos.row - 1, mypos.col);
	    				Cell chkcell = board[chkpos.row][chkpos.col];
	    				/**
	    				 *  add more work to the stack - unprocessed non mine cell
	    				 */
	    				if (chkcell.numAdjMines == 0 && chkcell.processedQ == false) {
	    	    			if (scanAreaForMines(chkpos.row,chkpos.col) == 0) {
		    					mystack.push(chkpos);   
	    	    			}
	    	    			/**
	    	    			 * there are adj mines
	    	    			 */
	    				} else if (chkcell.processedQ == false) {  
	    					chkcell.processedQ = true;
	    					board[chkpos.row][chkpos.col] = chkcell;
	    				}
	    				
	    				/**
	    				 * // top left above
	    				 */
	    				if (mypos.col > 0) {  
		    				
		    				chkpos = new MyPosition(mypos.row - 1, mypos.col - 1);
		    				chkcell = board[chkpos.row][chkpos.col];
		    				/**
		    				 * // add more work to the stack - unprocessed non mine cell
		    				 */
		    				if (chkcell.numAdjMines == 0 && chkcell.processedQ == false) {
		    	    			if (scanAreaForMines(chkpos.row,chkpos.col) == 0) {
			    					mystack.push(chkpos);   
		    	    			}
		    	    			/**
		    	    			 *  //there are adj mines
		    	    			 */
		    				} else if (chkcell.processedQ == false) { 
		    					chkcell.processedQ = true;
		    					board[chkpos.row][chkpos.col] = chkcell;
		    				}
	    					
	    				}
	    				
	    				/**
	    				 *  // top right above
	    				 */
	    				if (mypos.col < BOARDSIZE - 1) { 
		    				
		    				chkpos = new MyPosition(mypos.row - 1, mypos.col + 1);
		    				chkcell = board[chkpos.row][chkpos.col];
		    				/**
		    				 * // add more work to the stack - unprocessed non mine cell
		    				 */
		    				if (chkcell.numAdjMines == 0 && chkcell.processedQ == false) {
		    	    			if (scanAreaForMines(chkpos.row,chkpos.col) == 0) {
			    					mystack.push(chkpos);   
		    	    			}
		    	    			/**
		    	    			 *  //there are adj mines
		    	    			 */
		    				} else if (chkcell.processedQ == false) { 
		    					chkcell.processedQ = true;
		    					board[chkpos.row][chkpos.col] = chkcell;
		    				}
	    					
	    				}
	    				/**
		    			 * // end top row checking
		    			 */
	    			}  
	    			/**
	    			 * //not bottom row, check bottom
	    			 */
	    			if (mypos.row < BOARDSIZE - 1) { 
	    				
	    				chkpos = new MyPosition(mypos.row + 1, mypos.col);
	    				Cell chkcell = board[chkpos.row][chkpos.col];
	    				/**
	    				 *  // add more work to the stack - unprocessed non mine cell
	    				 */
	    				if (chkcell.numAdjMines == 0 && chkcell.processedQ == false) {
	    	    			if (scanAreaForMines(chkpos.row,chkpos.col) == 0) {
		    					mystack.push(chkpos);  
	    	    			}
	    	    			/**
	    	    			 * //there are adj mines
	    	    			 */
	    				} else if (chkcell.processedQ == false) {  //there are adj mines
	    					chkcell.processedQ = true;
	    					board[chkpos.row][chkpos.col] = chkcell;
	    				}
	    				
	    				/**
	    				 *  // bottom left 
	    				 */
	    				if (mypos.col > 0) { 
		    				
		    				chkpos = new MyPosition(mypos.row + 1, mypos.col - 1);
		    				chkcell = board[chkpos.row][chkpos.col];
		    				/**
		    				 * // add more work to the stack - unprocessed non mine cell
		    				 */
		    				if (chkcell.numAdjMines == 0 && chkcell.processedQ == false) {
		    	    			if (scanAreaForMines(chkpos.row,chkpos.col) == 0) {
			    					mystack.push(chkpos);   
		    	    			}
		    	    			/**
		    	    			 * //there are adj mines
		    	    			 */
		    				} else if (chkcell.processedQ == false) {  
		    					chkcell.processedQ = true;
		    					board[chkpos.row][chkpos.col] = chkcell;
		    				}
	    					
	    				}
	    				
	    				/**
	    				 * // bottom right
	    				 */
	    				if (mypos.col < BOARDSIZE - 1) {  
		    				
		    				chkpos = new MyPosition(mypos.row + 1, mypos.col + 1);
		    				chkcell = board[chkpos.row][chkpos.col];
		    				/**
		    				 * // add more work to the stack - unprocessed non mine cell
		    				 */
		    				if (chkcell.numAdjMines == 0 && chkcell.processedQ == false) {
		    	    			if (scanAreaForMines(chkpos.row,chkpos.col) == 0) {
			    					mystack.push(chkpos);   
		    	    			}
		    	    			/**
		    	    			 * //there are adj mines
		    	    			 */
		    				} else if (chkcell.processedQ == false) {  
		    					chkcell.processedQ = true;
		    					board[chkpos.row][chkpos.col] = chkcell;
		    				}
	    					
	    				}
	    				
	    			}  /** end bottom row checking
	    			/*
	    			
    				/**
    				 * //left
    				 */
    				if (mypos.col > 0) {  
	    				
	    				chkpos = new MyPosition(mypos.row, mypos.col - 1);
	    				Cell chkcell = board[chkpos.row][chkpos.col];
	    				/**
	    				 * // add more work to the stack - unprocessed non mine cell
	    				 */
	    				if (chkcell.numAdjMines == 0 && chkcell.processedQ == false) {
	    	    			if (scanAreaForMines(chkpos.row,chkpos.col) == 0) {
		    					mystack.push(chkpos);   
	    	    			}
	    	    			/**
	    	    			 * //there are adj mines
	    	    			 */
	    				} else if (chkcell.processedQ == false) {  
	    					chkcell.processedQ = true;
	    					board[chkpos.row][chkpos.col] = chkcell;
	    				}
    					
    				}
	    			
	    			/**
	    			 *  //right
	    			 */
    				if (mypos.col < BOARDSIZE - 1) { 
	    				
	    				chkpos = new MyPosition(mypos.row, mypos.col + 1);
	    				Cell chkcell = board[chkpos.row][chkpos.col];
	    				/**
	    				 *  // add more work to the stack - unprocessed non mine cell
	    				 */
	    				if (chkcell.numAdjMines == 0 && chkcell.processedQ == false) {
	    	    			if (scanAreaForMines(chkpos.row,chkpos.col) == 0) {
		    					mystack.push(chkpos);  
	    	    			}
	    	    			/**
	    	    			 * //there are adj mines
	    	    			 */
	    				} else if (chkcell.processedQ == false) {  
	    					chkcell.processedQ = true;
	    					board[chkpos.row][chkpos.col] = chkcell;
	    				}
    					
    				}
	    			
	    		}
	    	}
	    	
	    }
	  

}
