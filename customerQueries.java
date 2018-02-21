/***************************************
* EECS3421: Project 2                  *
* File name: customerQueries.java      *
* Author: Thillanayagam, Lindan        *
* Email: lindan4@my.yorku.ca           *
* Login ID: lindan4                    *
****************************************/


import java.net.*;
import java.sql.*;
import java.util.Scanner;
import java.util.Date;
import java.util.Random;
import java.util.TreeMap;
import java.util.ArrayList;
import java.io.PrintStream;



public class customerQueries
{

  final private static Random numGen = new Random();

  private Connection conDB;
  private String url = "jdbc:db2:c3421m";
  
  //Customer info
  private static ArrayList<String> customerUserInfo;
  private static ArrayList<String> purchaseFromClubs;
  private static String purchaseFromClub;
  
  
  //Here we set up the basic necessities required for inputting queries, and obtaining their results
  //Goal: close every possible connection
  public customerQueries()
  {
      try
	    {
			Class.forName("com.ibm.db2.jcc.DB2Driver").newInstance();
	        //conDB = DriverManager.getConnection(url);
	    }
	    catch (ClassNotFoundException e)
	    {
	          e.printStackTrace();
	          System.exit(0);
	    }
		catch (InstantiationException e)
	    {
	          e.printStackTrace();
	          System.exit(0);
	    }
		catch (IllegalAccessException e)
	    {
	          e.printStackTrace();
	          System.exit(0);
	    }
		
		try
		{
			conDB = DriverManager.getConnection(url);
		}
		catch (SQLException e)
		{
			System.out.println("Failed to connect to the database.\n");
			System.out.println(e.toString());
			System.exit(0);
		}
		
		try
		{
			conDB.setAutoCommit(true);
		}
		catch (SQLException e)
		{
			System.out.println("Could not adjust the auto commit setting.\n");
			System.out.println(e.toString());
			System.exit(0);
		}		
  }
  
  private static boolean checkInt(String intAsString)
  {
  	  try
  	  {
  	  	  Integer.parseInt(intAsString);
  	  }
  	  catch (Exception e)
  	  {
  	  	  System.out.println("\n'"+ intAsString + "' is not a number. Please try again.\n");
  	  	  return false;
  	  }
  	  return true;
  }
  //Check whether the customer exists given their CID
  public boolean find_customer(int customerID)
	{
  
  		String queryText;
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        String resultOutcome = "";
        
        float custCID = 0;
        String cName = "";
        String cCity = "";
        
        queryText = "SELECT * FROM YRB_CUSTOMER WHERE CID=" + customerID;
        
        try
        {
          ps = conDB.prepareStatement(queryText);
        } 
        catch (SQLException e)
        {
            System.out.println("SQL failed to execute.");
            e.printStackTrace();
            System.exit(0);
        }
        
        try
        {
            rs = ps.executeQuery();
        }
        catch (SQLException e)
        {
            System.out.println("SQL failed to execute.");
            e.printStackTrace();
            System.exit(0);
        }
        
        try
        {
            if (rs.next())
            {
                custCID = rs.getFloat("CID");
                cName = rs.getString("NAME");
                cCity = rs.getString("CITY");
            }
            else
            {
            	return false;
            }
        }
        catch (SQLException e)
        {
            System.out.println("SQL failed to execute.");
            e.printStackTrace();
            System.exit(0);
        }
        
        try
        {
            rs.close();
        }
        catch (SQLException e)
        {
            System.out.println("ResultSet failed to close.");
            e.printStackTrace();
            System.exit(0);
        }
        
        try
        {
            ps.close();
        }
        catch (SQLException e)
        {
            System.out.println("PreparedStatement failed to close.");
            e.printStackTrace();
            System.exit(0);
        }
        
        System.out.println("\nLogin successful!\n\nCustomer Info: \n\n\tCustomer ID: " + (int) custCID + "\n\tName: " + cName + "\n\tCity: " + cCity + "\n");
        
        customerUserInfo = this.storeCustomerInfo(customerID);
        
        System.out.println("\tMember of the following clubs: ");
        for (int j = 3; j < customerUserInfo.size(); j++)
        {
        	System.out.println("\t\t. " + customerUserInfo.get(j));
        }
        System.out.print("\n");
        
        return true;
	}
	
	//For later use, store customer info in an array list. Club association info is stored from index three
	private ArrayList<String> storeCustomerInfo(int customerID)
	{
		ArrayList<String> customerInfo = new ArrayList<>();
		
		String custQuery = "SELECT * FROM YRB_CUSTOMER, YRB_MEMBER WHERE YRB_CUSTOMER.CID=" + customerID + " AND YRB_CUSTOMER.CID = YRB_MEMBER.CID";
		PreparedStatement psCust = null;
		ResultSet rsCust = null;
		
		try
		{
			psCust = conDB.prepareStatement(custQuery);
		}
		catch (SQLException e)
		{
			System.out.println("The query could not be processed.");
			System.exit(0);
		}
		
		try
		{
			rsCust = psCust.executeQuery();
		}
		catch (SQLException e)
		{
			System.out.println("The query could not be executed.");
			System.exit(0);
		}
		try
		{
			if (rsCust.next())
			{
				customerInfo.add(""+ (int) rsCust.getFloat("CID"));
				customerInfo.add(rsCust.getString("NAME"));
				customerInfo.add(rsCust.getString("CITY"));
				customerInfo.add(rsCust.getString("CLUB"));
			}
			while (rsCust.next())
			{
				customerInfo.add(rsCust.getString("CLUB"));
			}
			
		}
		catch (SQLException e)
		{
			System.out.println("The results of the query could not be returned.");
			System.exit(0);
		}
		try
        {
            rsCust.close();
        }
        catch (SQLException e)
        {
            System.out.println("ResultSet failed to close.");
            e.printStackTrace();
            System.exit(0);
        }
        try
        {
            psCust.close();
        }
        catch (SQLException e)
        {
            System.out.println("PreparedStatement failed to close.");
            e.printStackTrace();
            System.exit(0);
        }
		return customerInfo;
	}
	
	
	private TreeMap<Integer, String> fetch_categories()
	{
		TreeMap<Integer, String> selectionMap = new TreeMap<Integer, String>();
		
		String queryFetchCat;
        PreparedStatement psCat = null;
        ResultSet rsCat = null;
        
        int selectionCounter = 1;
        
        queryFetchCat  = "SELECT * FROM YRB_CATEGORY";
        
        try
        {
        	psCat = conDB.prepareStatement(queryFetchCat);
        }
        catch (SQLException e)
        {
            System.out.println("SQL failed to execute.");
            e.printStackTrace();
            System.exit(0);
        }
        
        try
        {
        	rsCat = psCat.executeQuery();
        }
        catch (SQLException e)
        {
        	System.out.println("SQL failed to execute.");
            e.printStackTrace();
            System.exit(0);
        }
        
        try
        {
        	while (rsCat.next())
        	{
        		 selectionMap.put(selectionCounter++, rsCat.getString("CAT"));
        	}
        	
        }
        catch (SQLException e)
        {
        	System.out.println("The output of the query could not be fetched.");
        }
        
        try
        {
            rsCat.close();
        }
        catch (SQLException e)
        {
            System.out.println("ResultSet failed to close.");
            e.printStackTrace();
            System.exit(0);
        }
        try
        {
            psCat.close();
        }
        catch (SQLException e)
        {
            System.out.println("PreparedStatement failed to close.");
            e.printStackTrace();
            System.exit(0);
        }
        
        return selectionMap;
	}
	
	private TreeMap<Integer, ArrayList<String>> find_book(String title, String category)
	{
		String fetchBook = "";
		
		PreparedStatement psBook = null;
		ResultSet rsBook = null;
		int bookCounter = 1;
		
		TreeMap<Integer, ArrayList<String>> queryOutput = new TreeMap<Integer, ArrayList<String>>();
		ArrayList<String> bookInfo;
		
		
		fetchBook = "SELECT TITLE, YEAR, LANGUAGE, CAT, WEIGHT FROM YRB_BOOK WHERE TITLE='" + title + "' AND CAT='" + category + "' AND TITLE IN (SELECT TITLE from YRB_OFFER, YRB_MEMBER WHERE YRB_OFFER.CLUB = YRB_MEMBER.CLUB AND CID = " + Integer.parseInt(customerUserInfo.get(0)) + ")";
		
		try
		{
			psBook = conDB.prepareStatement(fetchBook);
		}
		
		catch (SQLException e)
		{
			System.out.println("The query failed to execute.");
			e.printStackTrace();
            System.exit(0);
		}
		
		try
		{
			rsBook = psBook.executeQuery();
		}
		catch (SQLException e)
		{
			System.out.println("The output of the query could not be fetched.");
			e.printStackTrace();
            System.exit(0);
		}
		
		try
		{
			while (rsBook.next())
			{
				bookInfo = new ArrayList<String>();
				bookInfo.add(rsBook.getString("TITLE"));
				bookInfo.add("" + rsBook.getInt("YEAR"));
				bookInfo.add(rsBook.getString("LANGUAGE"));
				bookInfo.add("" + rsBook.getInt("WEIGHT"));
				bookInfo.add(rsBook.getString("CAT"));
				
				queryOutput.put(bookCounter++, bookInfo);
			}
		}
		catch (SQLException e)
        {
        	System.out.println("The output of the query could not be fetched.");
        	e.printStackTrace();
        	System.exit(0);
        }
        
        try
        {
            rsBook.close();
        }
        catch (SQLException e)
        {
            System.out.println("ResultSet failed to close.");
            e.printStackTrace();
            System.exit(0);
        }
        try
        {
            psBook.close();
        }
        catch (SQLException e)
        {
            System.out.println("PreparedStatement failed to close.");
            e.printStackTrace();
            System.exit(0);
        }
        
        return queryOutput;
		
	}
	
	//Gets category, book title, and the year
	private float min_price(String title, int year, String category, int CID)
	{
		String minPriceQuery = "SELECT TITLE, CLUB, PRICE FROM YRB_OFFER WHERE (TITLE, PRICE) IN (SELECT YRB_BOOK.TITLE, MIN(YRB_OFFER.PRICE) AS PRICE FROM YRB_BOOK, YRB_OFFER, YRB_MEMBER where YRB_BOOK.TITLE = YRB_OFFER.TITLE AND YRB_BOOK.YEAR = YRB_OFFER.YEAR AND YRB_BOOK.CAT='" + category + "' AND YRB_BOOK.TITLE='" + title + "' AND YRB_BOOK.YEAR=" + year + " AND YRB_OFFER.CLUB = YRB_MEMBER.CLUB AND YRB_MEMBER.CID=" + CID + " GROUP BY YRB_BOOK.TITLE)";
		PreparedStatement psMin = null;
		ResultSet rsMin = null;
		
		purchaseFromClubs = new ArrayList<String>();
		
		float minimumPrice = 0;
		
		try
		{
			psMin = conDB.prepareStatement(minPriceQuery);
		}
		
		catch (SQLException e)
		{
			System.out.println("The query could not be processed.");
			e.printStackTrace();
			System.exit(0);
		}
		
		try
		{
			rsMin = psMin.executeQuery();
			
			while (rsMin.next())
			{
				minimumPrice = rsMin.getFloat("PRICE");
				//purchaseFromClub = rsMin.getString("CLUB");
				purchaseFromClubs.add(rsMin.getString("CLUB"));
			}
		}
		catch (SQLException e)
		{
			System.out.println("The query could not be executed.");
			e.printStackTrace();
			System.exit(0);
		}
		
		try
        {
            rsMin.close();
        }
        catch (SQLException e)
        {
            System.out.println("ResultSet failed to close.");
            e.printStackTrace();
            System.exit(0);
        }
        try
        {
            psMin.close();
        }
        catch (SQLException e)
        {
            System.out.println("PreparedStatement failed to close.");
            e.printStackTrace();
            System.exit(0);
        }
		
		return minimumPrice;
	}
	
	private void insert_purchase(int cid, String club, String title, int year, int quantity)
	{
		Date currentDate = new Date();
		Timestamp ts = new Timestamp(currentDate.getTime());
		
		String insertQuery = "INSERT INTO YRB_PURCHASE VALUES (?, ?, ?, ?, ?, ?)";
		PreparedStatement purchaseInsertion = null;
		
		try
		{
			purchaseInsertion = conDB.prepareStatement(insertQuery);
			purchaseInsertion.setInt(1, cid);
			purchaseInsertion.setString(2, club);
			purchaseInsertion.setString(3, title);
			purchaseInsertion.setInt(4, year);
			purchaseInsertion.setTimestamp(5, ts);
			purchaseInsertion.setInt(6, quantity);
			
			//System.out.println(cid + " " + club + " " + title + " " + year + " " + ts.toString() + " " + quantity);
			
			purchaseInsertion.executeUpdate();
			purchaseInsertion.close();
			System.out.println("\n\nYour order has been processed. Thank you for your purchase.");
		}
		catch (SQLException e)
		{
			System.out.println("Unfortunately the order could not be processed due to a technical error. Please restart the application and try again.\n");
			e.printStackTrace();
			System.exit(0);
		}
	}
	

	public static void main(String[] args) throws SQLException 
	{
		Scanner input = new Scanner(System.in);
		PrintStream output = new PrintStream(System.out);
		
		output.println("\nWelcome to the YRB Online Bookstore.\n");
		
		customerQueries cq = new customerQueries();
		
		output.print("\nLogin using your customer ID (Press ENTER without typing to quit): ");
		
		//Use to retrieve customer information
		String CIDStr = input.nextLine();
   
    if (CIDStr.isEmpty())
    {
        output.println("\nYou have exited the application. Thank you for visiting.\n");
        System.exit(0);
    } 
   
		while (!checkInt(CIDStr))
		{
			output.print("\nLogin using your customer ID (Press ENTER without typing to quit): ");
			CIDStr = input.nextLine();
      if (CIDStr.isEmpty())
      {
        output.println("You have exited the application. Thank you for visiting.\n");
        System.exit(0);
      } 
		}
		int cID = Integer.parseInt(CIDStr);
		
		
		while (!cq.find_customer(cID))
		{
			output.print("\nThe customer could not be found. Please try again.\n\n\nLogin using your customer ID (Press ENTER without typing to quit): ");
			CIDStr = input.nextLine();
      if (CIDStr.isEmpty())
      {
        output.println("\nYou have exited the application. Thank you for visiting.\n");
        System.exit(0);
      } 
			while (!checkInt(CIDStr))
			{
				output.print("\nLogin using your customer ID (Press ENTER without typing to quit): ");
				CIDStr = input.nextLine();
        if (CIDStr.isEmpty())
        {
          output.println("\nYou have exited the application. Thank you for visiting.\n");
          System.exit(0);
        } 
			}
			cID = Integer.parseInt(CIDStr);
		}
		
		int i;
		TreeMap<Integer, String> categorySelection = cq.fetch_categories();
		
		output.println("Book Categories:\n");
		
		for (i = 1; i <= categorySelection.size(); i++)
		{
			output.println("\t" + i + ". " + categorySelection.get(i));
		}
		output.print("\nEnter a number to select a category (Press ENTER without typing to quit): ");
		String getCat = "";
		
		String categoryInputString = input.nextLine();
    
    if (categoryInputString.isEmpty())
    {
        output.println("You have exited the application. Thank you for visiting.\n");
        System.exit(0);
    } 
		while (!checkInt(categoryInputString))
		{
			output.print("\nEnter a number to select a category (Press ENTER without typing to quit): ");
			categoryInputString = input.nextLine();
      if (categoryInputString.isEmpty())
      {
        output.println("You have exited the application. Thank you for visiting.\n");
        System.exit(0);
      } 
		}
		int categoryInput = Integer.parseInt(categoryInputString);
		while (categoryInput > categorySelection.size() || categoryInput < 1)
		{
			output.println("\nThe category does not exist! Please try again.\n\n");
			output.println("Book categories:\n");
			for (i = 1; i <= categorySelection.size(); i++)
			{
				output.println("\t" + i + ". " + categorySelection.get(i));
			}
			output.print("\nEnter a number to select a category: ");
			categoryInputString = input.nextLine();
      if (categoryInputString.isEmpty())
      {
        output.println("You have exited the application. Thank you for visiting.\n");
        System.exit(0);
      } 
			while (!checkInt(categoryInputString))
			{
				output.print("\nEnter a number to select a category (Press ENTER without typing to quit): ");
				categoryInputString = input.nextLine();
        if (categoryInputString.isEmpty())
        {
          output.println("You have exited the application. Thank you for visiting.\n");
          System.exit(0);
        } 
        
			}
			categoryInput = Integer.parseInt(categoryInputString);
		}
		getCat = categorySelection.get(categoryInput);
   
    output.println("Selected category: " + getCat);
		
		//User has to input the title of the book he/she is searching for
		output.print("\nEnter the title of the book to be searched for (case sensitive) (Press ENTER without typing to quit): ");
		
		
		String titleSearch = input.nextLine();
		
		//If the user presses 'enter' with no input
		if (titleSearch.isEmpty())
		{
			output.println("You have exited the application. Thank you for visiting.\n");
      System.exit(0);
		}
		
		TreeMap<Integer, ArrayList<String>> getTitles = cq.find_book(titleSearch, getCat);
		
		//If the title does not exist
		while (getTitles.size() == 0)
		{
			output.println("\nThe title does not exist! Please try again.\n\n");
			output.println("Book categories:\n");
			for (i = 1; i <= categorySelection.size(); i++)
			{
				output.println("\t" + i + ". " + categorySelection.get(i));
			}
			output.print("\nEnter a number to select a category (Press ENTER without typing to quit): ");
			getCat = "";
			categoryInputString = input.nextLine();
      if (categoryInputString.isEmpty())
      {
          output.println("You have exited the application. Thank you for visiting.\n");
          System.exit(0);
      } 
			while (!checkInt(categoryInputString))
			{
				output.print("\nEnter a number to select a category (Press ENTER without typing to quit): ");
				categoryInputString = input.nextLine();
        if (categoryInputString.isEmpty())
        {
          output.println("You have exited the application. Thank you for visiting.\n");
          System.exit(0);
        } 
			}
			categoryInput = Integer.parseInt(categoryInputString);
			
			while (categoryInput > categorySelection.size() || categoryInput < 1)
			{
				output.println("\nThe category does not exist! Please try again.\n\n");
				output.println("Book categories:\n");
				for (i = 1; i <= categorySelection.size(); i++)
				{
					output.println("\t" + i + ". " + categorySelection.get(i));
				}
				output.print("\nEnter a number to select a category (Press ENTER without typing to quit): ");
				categoryInputString = input.nextLine();
        if (categoryInputString.isEmpty())
        {
          output.println("You have exited the application. Thank you for visiting.\n");
          System.exit(0);
        } 
				while (!checkInt(categoryInputString))
				{
					output.print("\nEnter a number to select a category (Press ENTER without typing to quit): ");
					categoryInputString = input.nextLine();
				}
				categoryInput = Integer.parseInt(categoryInputString);
			}
			getCat = categorySelection.get(categoryInput);
		
			
			output.print("\nEnter the title of the book to be searched for (case sensitive)(Press ENTER without typing to quit): ");
			titleSearch = input.nextLine();
		
			while (titleSearch.isEmpty())
			{
				output.println("You have exited the application. Thank you for visiting.\n");
        System.exit(0);
			}
			getTitles = cq.find_book(titleSearch, getCat);
			
		}
		
		
		ArrayList<String> singleBookInfo;
		output.print("\n");
		for (int k = 1; k <= getTitles.size(); k++)
		{
			singleBookInfo = getTitles.get(k);
			output.println(k + ". " + "\tTitle: "+ singleBookInfo.get(0) + "\n\tYear: " + singleBookInfo.get(1) + "\n\tLanguage: " + singleBookInfo.get(2) + "\n\tWeight: " + singleBookInfo.get(3));
		}
		output.print("\nSelect the book to purchase by entering its corresponding number (Press ENTER without typing to quit): ");
		String bookSelectNumString = input.nextLine();
   
    if (bookSelectNumString.isEmpty())
    {
      output.println("You have exited the application. Thank you for visiting.\n");
      System.exit(0);
    }
		
		while (!checkInt(bookSelectNumString))
		{	
			output.print("\nSelect the book to purchase by entering its corresponding number (Press ENTER without typing to quit): ");
			bookSelectNumString = input.nextLine();
      if (bookSelectNumString.isEmpty())
      {
        output.println("You have exited the application. Thank you for visiting.\n");
        System.exit(0);
      }
		}
			
		int bookSelectNum = Integer.parseInt(bookSelectNumString);
		while (bookSelectNum > getTitles.size() || bookSelectNum < 1)
		{
			output.println("Invalid number! Please try again.\n");
			output.print("\nSelect the book to purchase by entering its corresponding number (Press ENTER without typing to quit): ");
			bookSelectNumString = input.nextLine();
      if (bookSelectNumString.isEmpty())
      {
        output.println("You have exited the application. Thank you for visiting.\n");
        System.exit(0);
      }
		
			while (!checkInt(bookSelectNumString))
			{	
				output.print("\nSelect the book to purchase by entering its corresponding number (Press ENTER without typing to quit): ");
				bookSelectNumString = input.nextLine();
        if (bookSelectNumString.isEmpty())
        {
          output.println("You have exited the application. Thank you for visiting.\n");
          System.exit(0);
        }
			}
			bookSelectNum = Integer.parseInt(bookSelectNumString);
		}
		
		float smallestPrice = cq.min_price(getTitles.get(bookSelectNum).get(0), Integer.parseInt(getTitles.get(bookSelectNum).get(1)), getTitles.get(bookSelectNum).get(4), Integer.parseInt(customerUserInfo.get(0)));
		output.println("\n\tMinimum price per book: $" + smallestPrice + "\n");
		
		
		
		purchaseFromClub = purchaseFromClubs.get(numGen.nextInt(purchaseFromClubs.size()));
   
    output.println("\tClub selling the book: " + purchaseFromClub);
   
		output.print("\nEnter the quantity to be purchased (Enter 0 to exit): ");
		String bookQtyString = input.nextLine();
		
		while (!checkInt(bookQtyString))
		{	
			output.print("\nEnter the quantity to be purchased (Enter 0 to exit): ");
			bookQtyString = input.nextLine();
		}
		
		int bookQty = Integer.parseInt(bookQtyString);
		if (bookQty == 0)
		{
			output.println("You have exited the application. Thank you for visiting.\n");
			System.exit(0);
		}
		while (bookQty < 0)
		{
			output.println("Invalid quantity!\n");
			output.print("Enter the quantity to be purchased (Enter 0 to exit): ");
			bookQtyString = input.nextLine();
		
			while (!checkInt(bookQtyString))
			{	
				output.print("\nEnter the quantity to be purchased (Enter 0 to exit): ");
				bookQtyString = input.nextLine();
			}
			bookQty = Integer.parseInt(bookQtyString);
		}
     if (bookQty == 0)
		{
			output.println("You have exited the application. Thank you for visiting.\n");
			System.exit(0);
    }
		float totalPrice = smallestPrice * bookQty;
		
    if (bookQty == 1)
    {
        output.print("\nWould you like to purchase a copy of " + getTitles.get(bookSelectNum).get(0) + " for $" + String.format("%.2f", totalPrice) +"? (Y/N): ");
    }
    else
    {
        output.print("\nWould you like to purchase " + bookQty + " copies of " + getTitles.get(bookSelectNum).get(0) + " for $" + String.format("%.2f", totalPrice) +"? (Y/N): ");
    }
		
		String purchaseDecision = input.nextLine();
		while (!purchaseDecision.equals("Y") && !purchaseDecision.equals("y") && !purchaseDecision.equals("N") && !purchaseDecision.equals("n"))
		{
			output.print("\nInvalid input.\n");
      if (bookQty == 1)
      {
        output.print("\nWould you like to purchase a copy of " + getTitles.get(bookSelectNum).get(0) + " for $" + String.format("%.2f", totalPrice) +"? (Y/N): ");
      }
      else
      {
        output.print("\nWould you like to purchase " + bookQty + " copies of " + getTitles.get(bookSelectNum).get(0) + " for $" + String.format("%.2f", totalPrice) +"? (Y/N): ");
      }
			purchaseDecision = input.nextLine();
		}
		if (purchaseDecision.equals("Y") || purchaseDecision.equals("y"))
		{
			ArrayList<String> purchaserInfo = cq.storeCustomerInfo(cID);
			cq.insert_purchase(Integer.parseInt(purchaserInfo.get(0)), purchaseFromClub, getTitles.get(bookSelectNum).get(0), Integer.parseInt(getTitles.get(bookSelectNum).get(1)), bookQty);
			
		}
		else
		{
			output.println("\nThe order has been cancelled.\n");
		}
		
		
		
	}
}