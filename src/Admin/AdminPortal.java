package Admin;

import java.util.*;
import User.Menu;


//serves as the main menu for a logged-in administrator.
//handles the navigation logic and administrative functions.

public class AdminPortal extends Menu 
{
    private Admin adminUser;
    private AdminManager adminManager;
    private CampsiteManager campsiteManager;
    private ReservationViewer reservationViewer;
    private ViewReview reviewPortal;
    private Report reportPortal;


    public AdminPortal(Scanner sc, Admin adminUser, AdminManager adminManager, CampsiteManager campsiteManager, ReservationViewer reservationViewer, ViewReview reviewPortal, Report reportPortal) {
        super("ADMIN PORTAL", sc);
        this.adminUser = adminUser;
        this.adminManager = adminManager;
        this.campsiteManager = campsiteManager;
        this.reservationViewer = reservationViewer;
        this.reviewPortal = reviewPortal;
        this.reportPortal = reportPortal;
    }

    @Override
    public void show() {
        System.out.println("Admin Menu - Logged in as: " + adminUser.getUsername());
        while (true) {
            clear();
            displayHeader();
            System.out.println("1) Register New Admin Account");
            System.out.println("2) Modify Camping Stuffs");
            System.out.println("3) View All Reservations");
            System.out.println("4) View User Feedback");
            System.out.println("5) Manage Reports");
            System.out.println("L) Logout");
            System.out.print("Enter your choice: ");

            String choice = sc.nextLine().trim().toUpperCase();
            switch (choice) {
                case "1":
                    adminManager.registerNewAdmin();
                    break;
                case "2":
                    campsiteManager.show();
                    break;
                case "3":
                    reservationViewer.show();
                    break;
                case "4":
                    reviewPortal.show();
                    break;
                case "5":
                    reportPortal.show();
                    break;
                case "L":
                    System.out.println("\nLogging out from Admin account.\n");
                    return;
                default:
                    System.out.println("\nInvalid choice. Please enter a number from 1 to 5, or 'L' to logout.");
                    pause();
            }
        }
    }

    private void pause() {
        System.out.println("Press Enter to continue...");
        sc.nextLine();
    }
}
