package User;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


//display the payment menu
public class PaymentMenu extends Menu 
{
    private ReservationService reservationService;
    private PaymentService paymentService;
    private User user;

    public PaymentMenu(Scanner sc, ReservationService reservationService, PaymentService paymentService, User user) {
        super("PAYMENT MENU", sc);
        this.reservationService = reservationService;
        this.paymentService = paymentService;
        this.user = user;
    }

    @Override
    public void show() {
        //the menu loop is handled by its methods directly.
    }
    
    //view payment history
    public void viewPaymentHistory() 
    {
        clear();
        setTitle("PAYMENT HISTORY");
        displayHeader();
        List<PaymentData> userPayments = paymentService.getPaymentsForUser(user.getUserId());
        if (userPayments.isEmpty()) {
            System.out.println("No payment history found");
        } else {
            for (PaymentData payment : userPayments) {
                System.out.println(payment.toString());
            }
        }
        System.out.println(createLine(130, '-'));
        pause();
    }

    //make payment for unpaid reservations
    //assume that as long as user choose payment method and pay, then it will be successfully paid
    public void makePayment() 
    {
        clear();
        setTitle("MAKE PAYMENT");
        displayHeader();
        
        List<ReservationData> unpaidReservations = new ArrayList<>();
        for (ReservationData res : reservationService.getReservations()) 
        {
            if (res.getUserId().equals(user.getUserId()) && res.getPaymentData() == null) 
            {
                unpaidReservations.add(res);
            }
        }
        
        if (unpaidReservations.isEmpty()) {
            System.out.println("No unpaid reservations found");
        } else {
            for (int i = 0; i < unpaidReservations.size(); i++) {
                ReservationData res = unpaidReservations.get(i);
                System.out.printf("<%d> %s\n", i + 1, res.toString());
            }
        }
        System.out.println("<B> Back");
        System.out.println(createLine(130, '-'));
        System.out.print("Enter Option >> ");
        String option = sc.nextLine().trim().toUpperCase();
        if (option.equals("B")) {
            return;
        } 
        
        try {
            int choice = Integer.parseInt(option); 
            if (choice >= 1 && choice <= unpaidReservations.size()) {
                ReservationData selectedRes = unpaidReservations.get(choice - 1);
                
                System.out.println("\nSelect Payment Method:");
                System.out.println("<1> Credit Card");
                System.out.println("<2> Debit Card");
                System.out.println("<3> Online Banking");
                System.out.println("<4> E-wallet TNG");
                System.out.println("<B> Back");
                System.out.print("Enter Option >> ");
                
                String methodChoice = sc.nextLine();
                String paymentMethod = "";
                
                switch (methodChoice) {
                    case "1": paymentMethod = "Credit Card"; break;
                    case "2": paymentMethod = "Debit Card"; break;
                    case "3": paymentMethod = "Online Banking"; break;
                    case "4": paymentMethod = "E-wallet TNG"; break;
                    case "B": case "b": return;
                    default: 
                        System.out.println(createLine(49,'-') + " Invalid Option, Pls Try Again! " + createLine(49,'-'));
                        pause();
                        return;
                }
                
                if (reservationService.processResPayment(selectedRes, paymentMethod)) 
                {
                    System.out.println(createLine(55,'-')+"Payment successful!"+createLine(56,'-'));
                    pause();
                } else {
                    System.out.println(createLine(40,'-')+"Payment failed!"+createLine(40,'-'));
                    pause();
                }
            } else {
                 System.out.println(createLine(49,'-') + " Invalid Option, Pls Try Again! " + createLine(49,'-'));
                 pause();
            }
        } catch (NumberFormatException e) {
            System.out.println(createLine(49,'-') + " Invalid Option, Pls Try Again! " + createLine(49,'-'));
            pause();
        }
    }
    
    private void pause() {
        System.out.println("Press Enter to continue...");
        sc.nextLine();
    }
}