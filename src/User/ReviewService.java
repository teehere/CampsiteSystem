package User;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import Admin.Review;

//handle all business logic related to reviews
public class ReviewService 
{
    private static final String REVIEW_FILE = MainMenu.REVIEW_FILE;
    private FileService fileService;

    public ReviewService(FileService fileService) 
    {
        this.fileService = fileService;
    }

    public List<Review> loadAllReviews() 
    {
        List<Review> reviews = new ArrayList<>();
        try (Scanner fileScanner = new Scanner(new File(REVIEW_FILE))) {
            while (fileScanner.hasNextLine()) {
                String line = fileScanner.nextLine();
                String[] parts = line.split("\\|");
                if (parts.length >= 4) {
                    try {
                        String campsiteName = parts[0];
                        String userId = parts[1];
                        int rating = Integer.parseInt(parts[2]);
                        String comment = parts[3];
                        reviews.add(new Review(campsiteName, userId, rating, comment));
                    } catch (NumberFormatException e) {
                        System.err.println("Skipping malformed review line: " + line);
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading feedback file: " + e.getMessage());
        }
        return reviews;
    }

    public boolean addReview(Review review) { 
        try (PrintWriter writer = new PrintWriter(new FileWriter(REVIEW_FILE, true))) {
            writer.println(review.getCampsiteName() + "|" + review.getUserId() + "|" + review.getRating() + "|" + review.getComment());
            return true;
        } catch (IOException e) {
            System.err.println("Error saving review: " + e.getMessage());
            return false; 
        }
    }

    public List<Review> getReviewsByCampsite(String campsiteName) {
        List<Review> allReviews = loadAllReviews();
        List<Review> filteredReviews = new ArrayList<>();
        for (Review r : allReviews) {
            if (r.getCampsiteName().equalsIgnoreCase(campsiteName)) {
                filteredReviews.add(r);
            }
        }
        return filteredReviews;
    }

    public List<Review> filterReviews(String campsite, String rating) 
    {
        List<Review> allReviews = loadAllReviews();
        List<Review> filtered = new ArrayList<>();
        for (Review r : allReviews) {
            boolean matches = true;
            if (!campsite.isEmpty() && !r.getCampsiteName().toLowerCase().contains(campsite.toLowerCase())) {
                matches = false;
            }
            if (!rating.isEmpty() && r.getRating() != Integer.parseInt(rating)) {
                matches = false;
            }
            if (matches) {
                filtered.add(r);
            }
        }
        return filtered;
    }
    

    //calcualte the average rating for specific campsite
    public double getAverageRating(String campsiteName) 
    {
        List<Review> reviews = getReviewsByCampsite(campsiteName); 
        if (reviews.isEmpty()) {
            return 0.0;
        }
        return reviews.stream().mapToInt(Review::getRating).average().orElse(0.0);
    }
    

    public List<String> readRegion(String filename) 
    {
        return fileService.readFileByLine(filename);
    }
}