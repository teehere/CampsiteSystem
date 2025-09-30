package User;

import Admin.*;
import java.util.Scanner;

public class MainMenu extends Menu 
{
	//all files will be put at here to centralize
    public static final String USER_INFO_FILE = "user_Info.txt";
    public static final String CAMPSITE_FILE = "campsites_Info.txt";
    public static final String RESERVATION_FILE = "reserves.txt";
    public static final String REVIEW_FILE = "review.txt";
    public static final String PAYMENT_FILE = "payments.txt";
    public static final String REGION_FILE = "regions.txt";
    public static final String CAMPSITE_TYPE_FILE = "campsite_Types.txt";
    public static final String REPORT_FILE = "report.txt";

    private AuthService authService;
    private CampsiteService campsiteService;
    private PaymentService paymentService;
    private ReviewService reviewService;
    private ReservationService reservationService;
    private AdminService adminService;
    private ReportService reportService;
    private FileService fileService;

    public static void main(String[] args) 
    {
        MainMenu mainMenu = new MainMenu();
        mainMenu.authService.initializeUserInfoFile();
        mainMenu.show();
    }

    public MainMenu() 
    {
        super("WELCOME TO THE CAMPING RESERVATION SYSTEM", new Scanner(System.in));
        this.fileService = new FileService();
        this.authService = new AuthService(sc);
        this.campsiteService = new CampsiteService();
        this.paymentService = new PaymentService();
        this.reviewService = new ReviewService(fileService); // Updated
        this.reservationService = new ReservationService(campsiteService, paymentService, reviewService, fileService); // Updated
        this.adminService = new AdminService(authService);
        this.reportService = new ReportService(campsiteService);
    }

    @Override
    public void show() 
    {
        while (true) {
            clear();
            setTitle("WELCOME TO THE CAMPING RESERVATION SYSTEM");
            displayHeader();
            System.out.println("1) Login");
            System.out.println("2) Register new user account");
            System.out.println("3) Exit");
            System.out.print("Enter your choice: ");

            String choice = sc.nextLine().trim();
            switch (choice) {
                case "1":
                    loginUser();
                    break;
                case "2":
                    authService.registerUser();
                    pause();
                    break;
                case "3":
                    System.out.println("\nThank you for using the Camping Reservation System. Goodbye!");
                    return;
                default:
                    System.out.println("\nInvalid Choice. Please Enter Option '1', '2', or '3'");
                    pause();
            }
        }
    }

    //user Login
    private void loginUser() 
    {
        clear();
    	setTitle("USER LOGIN");
    	displayHeader();

        System.out.print("Enter User ID or Username: ");
        String userInput = sc.nextLine();
        
        String password = authService.readHiddenPassword("Enter Password: ");

        User loggedInUser = authService.authenticateUser(userInput, password);

        if (loggedInUser != null) {
            System.out.println("\nLogin Successful! Welcome, " + loggedInUser.getUsername() + ".\n");
            
            if (loggedInUser instanceof Admin) {
                AdminPortal adminPortal = new AdminPortal(
                    sc,
                    (Admin) loggedInUser,
                    new AdminManager(sc, adminService),
                    new CampsiteManager(sc, campsiteService),
                    new ReservationViewer(sc, reservationService),
                    new ViewReview(sc, reviewService),
                    new Report(sc, reportService, campsiteService)
                );
                adminPortal.show();
            } else {
                UserPortal userPortal = new UserPortal(
                    sc,
                    loggedInUser,
                    campsiteService,
                    reservationService,
                    reviewService,
                    paymentService
                );
                userPortal.show();
            }
        } else {
            System.out.println("Invalid User ID/Username or Password. Please try again.");
            pause();
        }
    }
    
    private void pause() {
        System.out.println("Press Enter to continue...");
        sc.nextLine();
    }
}