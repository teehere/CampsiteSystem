package Admin;

import User.Menu;
import User.FileService;
import User.ReviewService;
import java.util.*;


//menu for admin to view user feedback
public class ViewReview extends Menu implements Filterable<Review> 
{
    private ReviewService reviewService;

    public ViewReview(Scanner sc, ReviewService reviewService) 
    {
        super("USER FEEDBACK", sc);
        this.reviewService = reviewService;
    }

    @Override
    public void show() {
        boolean loop = true;
        while (loop) {
            clear();
            displayHeader();
            List<Review> allReviews = reviewService.loadAllReviews();
            if (allReviews.isEmpty()) {
                System.out.println("No User Feedback Found!\n");
            } else {
                displayAllReviews(allReviews);
            }

            System.out.print("Options: <F>ilter  <Q>uit: ");
            String choice = sc.nextLine().trim().toUpperCase();

            switch (choice) {
                case "F":
                    filterInteractive();
                    pause();
                    break;
                case "Q":
                    loop = false;
                    break;
                default:
                    System.out.println("Invalid choice!");
                    pause();
            }
        }
    }

    private void pause() {
        System.out.println("Press Enter to continue...");
        sc.nextLine();
    }

    //display reviews
    private void displayAllReviews(List<Review> list) 
    {
    	System.out.println(); 
        System.out.printf("%-27s | %-15s | %-6s | %s\n", "Campsite", "User ID", "Rating", "Comment");
        System.out.println(createLine(length, '-'));

        for (Review r : list) {
            System.out.printf("%-27s | %-15s | %-6d | %s\n", r.getCampsiteName(), r.getUserId(), r.getRating(), r.getComment());
        }
        System.out.println(createLine(length, '='));
    }

    //filter
    @Override
    public List<Review> filterInteractive() {
        String campsite = "";
        String rating = "";
    	clear();
        while (true) {
            System.out.print("Choose field to filter: <C>ampsite <R>ating <S>how results <Q>uit: ");
            String choice = sc.nextLine().trim().toUpperCase();

            switch (choice) {
                case "Q": return Collections.emptyList();
                case "C":
                    System.out.print("Enter campsite name or leave blank: ");
                    campsite = sc.nextLine().trim();
                    break;
                case "R":
                    while (true) {
                        System.out.print("Enter rating (1-5) or leave blank: ");
                        String input = sc.nextLine().trim();
                        if (input.isEmpty()) {
                            rating = "";
                            break;
                        }
                        try {
                            int val = Integer.parseInt(input);
                            if (val >= 1 && val <= 5) {
                                rating = input;
                                break;
                            } else {
                                System.out.println("Rating must be between 1 and 5!");
                            }
                        } catch (NumberFormatException e) {
                            System.out.println("Invalid number!");
                        }
                    }
                    break;
                case "S":
                    List<Review> filtered = reviewService.filterReviews(campsite, rating);
                    displayFiltered(filtered);
                    return filtered;
                default:
                    System.out.println("Invalid choice! Try again");
            }
        }
    }

    @Override
    public void displayFiltered(List<Review> filtered) {
    	clear();
        if (filtered.isEmpty()) {
            System.out.println("\nNo reviews found for the selected filters");
        } else {
            displayAllReviews(filtered);
        }
    }

    @Override
    public List<Review> getAllItems() 
    {
        return reviewService.loadAllReviews();
    }
}