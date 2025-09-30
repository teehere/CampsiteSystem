package User;

import java.util.List;
import java.util.Scanner;
import Admin.Campsite;
import Admin.CampsiteService;
import Admin.Review;


//menu for user to give feedback on campsites
public class ReviewMenu extends Menu 
{ 
    private ReviewService reviewService;
    private CampsiteService campsiteService;
    private User user;

    public ReviewMenu(Scanner sc, ReviewService reviewService, CampsiteService campsiteService, User user) {
        super("GIVE REVIEW", sc);
        this.reviewService = reviewService;
        this.campsiteService = campsiteService;
        this.user = user;
    }

    @Override
    public void show() {
        while(true) {
            clear();
            displayHeader();
 
            List<Campsite> allCampsites = campsiteService.getAllCampsites();
            if (allCampsites.isEmpty()) {
                System.out.println("No Campsites Available For Review");
                System.out.println(createLine(70, '-'));
            } else {
                for (int i = 0; i < allCampsites.size(); i++) { 
                    Campsite c = allCampsites.get(i);
                    System.out.printf("<%2d> %-30s (Avg Rating: %.1f)\n", i+1, c.getName(), reviewService.getAverageRating(c.getName()));
                } 
            }

            System.out.println(createLine(130, '-'));
            System.out.println("<F>ilter    <B>ack");
            System.out.println(createLine(130, '-'));
            System.out.print("Enter Option >> ");
            String option = sc.next().trim().toUpperCase();
            sc.nextLine();

            if (option.equals("B")) {
                return;
            } else if (option.equals("F")) {
                String region = filterRegion();
                if (region == null) {continue;} 
                String campsiteType = filterCampsiteTypes();
                if (campsiteType == null) {continue;}
                filterCamp(region, campsiteType); 
            } else {
                try {
                    int choice = Integer.parseInt(option);
                    if (choice >= 1 && choice <= allCampsites.size()) {
                        manageCampsiteReviews(allCampsites.get(choice - 1));
                        return;
                    } else {
                        System.out.println(createLine(49,'-') + " Invalid Option, Pls Try Again! " + createLine(49,'-'));
                        pause();
                    }
                } catch (NumberFormatException e) {
                    System.out.println(createLine(49,'-') + " Invalid Option, Pls Try Again! " + createLine(49,'-'));
                    pause();
                }
            }
        }
    }
 
    private String filterRegion() {
        while(true) {
            clear();
            setTitle("SELECT REGION");
            displayHeader();
            List<String> regions = reviewService.readRegion(MainMenu.REGION_FILE);
            for (int i = 0; i < regions.size(); i++) {
                System.out.printf("<%d> %s\n", i+1, regions.get(i));
            }
            System.out.println("<B> Back");
            System.out.println(createLine(130, '-'));
            System.out.print("Enter Option >> ");
            String option = sc.nextLine().trim().toUpperCase();
            if (option.equals("B")) {
                return null;
            }
            try {
                int choice = Integer.parseInt(option);
                if (choice >= 1 && choice <= regions.size()) {
                    return regions.get(choice - 1); 
                } else {
                    System.out.println(createLine(49,'-') + " Invalid Option, Pls Try Again! " + createLine(49,'-'));
                    pause();
                }
            } catch (NumberFormatException e) {
                System.out.println(createLine(49,'-') + " Invalid Option, Pls Try Again! " + createLine(49,'-'));
                pause();
            }
        }
    }

    private String filterCampsiteTypes() {
        while(true) { 
            clear();
            setTitle("SELECT CAMPSITE TYPE");
            displayHeader();
            List<String> types = reviewService.readRegion(MainMenu.CAMPSITE_TYPE_FILE);
            for (int i = 0; i < types.size(); i++) {
                System.out.printf("<%d> %s\n", i+1, types.get(i));
            }
            System.out.println("<B> Back");
            System.out.println(createLine(130, '-'));
            System.out.print("Enter Option >> ");
            String option = sc.nextLine().trim().toUpperCase();
            if (option.equals("B")) { 
                return null;
            } 
            try {
                int choice = Integer.parseInt(option);
                if (choice >= 1 && choice <= types.size()) {
                    return types.get(choice - 1); 
                } else { 
                    System.out.println(createLine(49,'-') + " Invalid Option, Pls Try Again! " + createLine(49,'-'));
                    pause();
                }
            } catch (NumberFormatException e) {
                System.out.println(createLine(49,'-') + " Invalid Option, Pls Try Again! " + createLine(49,'-'));
                pause();
            }
        }
    }
 
    private void filterCamp(String region, String campsiteType) { 
        List<Campsite> filterCamp = campsiteService.filterCampsites(region, campsiteType, "");
        while(true) {
            clear();
            System.out.printf("\n%s IN %s\n", campsiteType.toUpperCase(), region.toUpperCase());
            System.out.println(createLine(130, '-'));
            System.out.printf("%-29s | %-8s | %-16s | %s\n", "NAME", "TYPE", "PRICE", "CAPACITY");
            System.out.println(createLine(130, '-'));
            if (filterCamp.isEmpty()) {
                System.out.println("No campsites available"); 
            } else {
                for (int i = 0; i < filterCamp.size(); i++) {
                    System.out.printf("<%d> %s\n", i+1, filterCamp.get(i).toString());
                }
            }
            System.out.println("<B> Back");
            System.out.println(createLine(130, '-'));
            System.out.print("Select Campsite to View/Add Reviews: "); 
            String opt = sc.nextLine().trim().toUpperCase();
            if (opt.equals("B")) {
                return;
            }
            try {
                int choice = Integer.parseInt(opt); 
                if (choice >= 1 && choice <= filterCamp.size()) {
                    manageCampsiteReviews(filterCamp.get(choice - 1));
                    return; 
                } else {
                    System.out.println(createLine(49,'-') + " Invalid Option, Pls Try Again! " + createLine(49,'-'));
                    pause();
                }
            } catch (NumberFormatException e) {
                System.out.println(createLine(49,'-') + " Invalid Option, Pls Try Again! " + createLine(49,'-'));
                pause();
            }
        }
    }

    private void manageCampsiteReviews(Campsite campsite) {
        while (true) {
            clear();
            setTitle("REVIEWS FOR: " + campsite.getName());
            displayHeader();
            List<Review> reviews = reviewService.getReviewsByCampsite(campsite.getName());  
            if (reviews.isEmpty()) {
                System.out.println("No reviews yet for this campsite");
            } else {
                System.out.printf("%-15s | %-6s | %s\n", "User ID", "Rating", "Comment");
                System.out.println(createLine(70, '-'));
                for (Review r: reviews) {
                    System.out.printf("%-15s | %-6d | %s\n", r.getUserId(), r.getRating(), r.getComment());
                }
            }
            System.out.println(createLine(70, '-'));
            System.out.println("<1> Add New Review");
            System.out.println("<B> Back to Campsite List");
            System.out.println(createLine(70, '-'));
            System.out.print("Enter option: ");
            String option = sc.nextLine().trim().toUpperCase();
            if (option.equals("B")) {
                return;
            } else if (option.equals("1")) {
                submitNewReview(campsite);
            } else {
                System.out.println(createLine(19,'-') + " Invalid Option, Pls Try Again! " + createLine(19,'-'));
                pause();
            }
        }
    } 

    private void submitNewReview(Campsite campsite) {
        System.out.println("\n" + createLine(70, '='));
        System.out.println(String.format("%" + (70/2 + "SUBMIT NEW REVIEW".length()/2) + "s", "SUBMIT NEW REVIEW"));
        System.out.println(createLine(70, '='));
        int rating = 0; 
        while (true) {
            try {
            	System.out.println("<Q>uit at any time");
                System.out.print("Enter your rating (1-5 stars): ");
                String input = sc.nextLine().trim(); 
                
                if (input.equalsIgnoreCase("Q")) {
                    System.out.println(createLine(28,'-') + "Review Cancelled" + createLine(27,'-'));
                    pause(); 
                    return;
                }
                
                rating = Integer.parseInt(input);
                if (rating >= 1 && rating <= 5) {
                    break;
                } else {
                    System.out.println(createLine(19,'-')+" Rating must be between 1 and 5 "+createLine(19,'-'));
                }
            } catch (NumberFormatException e) {
                System.out.println(createLine(19,'-') + " Invalid Option, Pls Try Again! " + createLine(19,'-'));
                pause();
            }
        }
        System.out.print("Enter your review comment: ");
        String comment = sc.nextLine().trim();
        if (comment.equalsIgnoreCase("Q")) {
        	System.out.println(createLine(28,'-') + "Review Cancelled" + createLine(27,'-'));
            pause();
            return;
        }
        System.out.print("\nSubmit this review? (Y/N): ");
        String confirm = sc.nextLine().trim().toUpperCase();
        if (confirm.equals("Y")) { 
            if (reviewService.addReview(new Review(campsite.getName(), user.getUserId(), rating, comment))) {
                System.out.println(createLine(19,'-') +" Review Submitted Successfully! " + createLine(19,'-'));
                pause();
            } else {
                System.out.println(createLine(24, '-') + " Failed To Submit Review " + createLine(24, '-'));
                pause();
            }
        } else if (confirm.equals("N")) {
            System.out.println(createLine(27,'-') + " Review Cancelled " +createLine(26,'-'));
            pause();
        } else {
            System.out.println(createLine(19,'-') + " Invalid Option, Pls Try Again! " + createLine(19,'-'));
            pause();
        }
    }
    
    private void pause() {
        System.out.println("Press Enter to continue...");
        sc.nextLine();
    }
}