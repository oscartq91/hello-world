import java.util.Scanner;
import java.util.ArrayList;

public class Videorentalstore
{
	public static void main(String[] args)
	{
		int price;
		int addedpoints;
		int pay;
		int rentaldays;
		int totalpoints;
		Movie choosenmovie;
		String customer_name;
		String exit = "N";
		String category;
		String choice;
		String movietitle;
		Scanner sc = new Scanner (System.in);
		Customer active;
		boolean usedpoints;

		MovieDB.loadmovieDB();
		CustomerDB.loadcustomerDB();

		while(exit.equals("N"))			//Keep running the menu after each rental
		{
			usedpoints=false;
			addedpoints = 0;
			totalpoints = 0;
			active = null;
			choice = null;
			customer_name = "Not registred";

			System.out.println("1. Rent movie\n2. Return movie\n3. Manage movies\n4. Manage customers\n\nPress any other key to exit program");
			
			choice = sc.next();
			if(choice.equals("1"))
			{
				System.out.println("Registred customer? (Y/N)");
				if(sc.next().equals("Y"))
				{
					active = getCustomer();
				}

				choosenmovie = movieChoice();
				if(choosenmovie.getRentstatus()==false) //checks that the movie isn't already rented out
				{
					rentaldays = getDays();

					price = choosenmovie.getRent(rentaldays);

					if(active!=null)		//Only do if there is a registred customer
					{
						if(active.getPoints()>=25)
						{
							System.out.println("You have " + active.getPoints() + " points. Do you want to use 25 points for a free rental day? (Y/N)");
							if(sc.next().equals("Y"))
							{
								usedpoints = true;
								active.usePoints();
							}
							
						}
					}

					if(usedpoints == true)		//If customer wants to use points, remove 1 day off price
					{
						price = choosenmovie.getRent(rentaldays-1);
					}
					else
					{
						price = choosenmovie.getRent(rentaldays);
					}

					System.out.println("\nAmount to pay: \n" + price);
					pay = payment(active);

					if(pay >= price)		//Check if customer has paid enough to complete transaction
					{
						if(active!=null)	//Add bonus points to customer's account depending on movie category
						{
							category = choosenmovie.getCategory();
							if(category.equals("N"))
							{
								active.addPoints(2);
								addedpoints = 2;
							}
							else
							{
								active.addPoints(1);
								addedpoints = 1;
							}
							customer_name = active.getName();
							totalpoints = active.getPoints();
						}

						Receipt r = new Receipt(customer_name, choosenmovie.getTitle(), price, pay, addedpoints, usedpoints, totalpoints, rentaldays);
						choosenmovie.rentOutmovie(r);
						r.printRentReceipt();
					}
					else
					{
						System.out.println("Rental incomplete");
					}
				}
				else
				{
					System.out.println("\nMovie is already rented out\n");
				}

			}
			else if(choice.equals("2"))
			{
				System.out.println("Select movie to return");
				choosenmovie = movieChoice();
				if(choosenmovie.getRentstatus()==true)
				{

					Receipt r = choosenmovie.returnMovie();
					r.printReturnReceipt();
				}
				else
				{
					System.out.println("\nSelected movie has not been rented out and cannot be returned.\n");
				}
			}

			else if(choice.equals("3"))
			{
				System.out.println("1. Add movie\n2. Remove movie\n3. Change category of movie\n");
				choice = sc.next();
				if(choice.equals("1"))
				{
					System.out.println("Enter title of movie");
					movietitle = sc.next();
					System.out.println("Enter category (N for new, R for regular, O for old)");
					category = sc.next();
					Movie m = new Movie(movietitle, category);
					MovieDB.addMovie(m);
				}
				else if(choice.equals("2"))
				{
					System.out.println("Select movie to remove.");
					choosenmovie = movieChoice();
					MovieDB.removeMovie(choosenmovie);
				}
				else if(choice.equals("3"))
				{
					System.out.println("Select movie to change.");
					choosenmovie = movieChoice();
					System.out.println("Enter new category (N for new, R for regular, O for old)");
					category = sc.next();
					choosenmovie.changeCategory(category);
				}
			}

			else if(choice.equals("4"))
			{
				System.out.println("1. Add customer\n2. Remove customer");
				choice = sc.next();
				if(choice.equals("1"))
				{
					System.out.println("Enter new customer name");
					customer_name = sc.next();
					Customer c = new Customer(customer_name);
					CustomerDB.addCustomer(c);
				}
				else if(choice.equals("2"))
				{
					Customer c = getCustomer();
					CustomerDB.removeCustomer(c);
				}
			}

			else 
			{
				exit = "Y";
			}

		}
	}

	public static Customer getCustomer()	
	{															//Gets specific customer from database
		Customer cust = null;
		String customer_name;

		System.out.println("Enter name");
		Scanner sc = new Scanner (System.in);
		customer_name = sc.next();

		ArrayList<Customer> allcustomers = new ArrayList<Customer>();
		allcustomers = CustomerDB.getAllCustomers();
				
		for(int i=0;i<allcustomers.size();i++)
		{
			if(customer_name.equals(allcustomers.get(i).getName()))
			{
				cust = allcustomers.get(i);
			}
		}
				
		System.out.println("Active Customer: " + cust);
		return cust;
	}

	public static Movie movieChoice()
	{										//Allows customer to choose movie to rent
		int choice = 0;
		int rentaldays;
		String choosenmovie = null;
		Movie choosenmovieret = null;
		ArrayList<Movie> allmovies = new ArrayList<Movie>();

		System.out.println("Select movie");
		allmovies = MovieDB.getAllMovies();

		for(int i=0; i<allmovies.size();i++)
		{
			System.out.println(i + ". " + allmovies.get(i).getTitle());
		}

		Scanner scan = new Scanner (System.in);
		
		choice = scan.nextInt();
		
		try
		{
			choosenmovie = allmovies.get(choice).getTitle();
			choosenmovieret = allmovies.get(choice);
			System.out.println("\nYour movie choice: \n" + choosenmovie);
		}
		catch(Exception e)
		{
			System.out.println("Choice doesn't exist");
		}
		return choosenmovieret;
		
	}
	public static int getDays()
	{															//Ask customer how many days to rent movie for
		int rentaldays;
		System.out.println("Enter number of days to rent: ");

		Scanner scandays = new Scanner (System.in);
		rentaldays = scandays.nextInt();

		System.out.println("\nDays = " + rentaldays + "\n");	
		return rentaldays;
	}
	public static int payment(Customer c)
	{															//Allows customer to pay for rental
		Scanner sc = new Scanner(System.in);
		int paid = 0;

		System.out.println("Enter amount: ");
		paid = sc.nextInt();
	
		return paid;
	}

}


