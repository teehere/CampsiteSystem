package User;

import java.util.Scanner;
import Admin.CampsiteService;

public class UserPortal extends Menu 
{ 
    private ReservationMenu reservationMenu;
    private ReviewMenu reviewMenu;
    private PaymentMenu paymentMenu;

    public UserPortal (Scanner sc, User user, CampsiteService campsiteService, ReservationService reservationService, ReviewService reviewService, PaymentService paymentService) 
    {
        super("USER PORTAL", sc);
        this.reservationMenu = new ReservationMenu(sc, reservationService, campsiteService, user);
        
        this.reviewMenu = new ReviewMenu(sc, reviewService, campsiteService, user);

        this.paymentMenu = new PaymentMenu(sc, reservationService, paymentService, user);
    } 

    @Override 
    public void show() 
    {
        while (true) {
            clear();
            displayHeader();

            System.out.println("<1> Search and Reserve Campsite");
            System.out.println("<2> View Reservation");
            System.out.println("<3> Cancel Reservation");
            System.out.println("<4> Give Feedbacks");
            System.out.println("<5> View Payment History");
            System.out.println("<6> Make Payment");
            System.out.println("<L> Logout");
            System.out.println(createLine(130, '-'));
            System.out.print("Enter Option >> ");
            String option = sc.nextLine().trim().toUpperCase();

            switch (option) {
                case "1":
                    reservationMenu.search();
                    break;
                case "2":  
                    reservationMenu.view();
                    break; 
                case "3": 
                    reservationMenu.cancel();
                    break;
                case "4": 
                    reviewMenu.show();
                    break; 
                case "5":
                    paymentMenu.viewPaymentHistory();
                    break;
                case "6":
                    paymentMenu.makePayment();
                    break;
                case "L":
                    return;     
                default:
                    System.out.println(createLine(49,'-') + " Invalid Option, Pls Try Again! " + createLine(49,'-'));
                    sc.nextLine();
            }
        }
    }
}