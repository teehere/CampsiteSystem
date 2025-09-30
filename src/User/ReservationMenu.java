package User;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import Admin.Campsite;
import Admin.CampsiteService;


//menu for users to manage reservations
public class ReservationMenu extends Menu 
{ 
    private ReservationService reservationService;
    private CampsiteService campsiteService;
    private User user;

    public ReservationMenu(Scanner sc, ReservationService reservationService, CampsiteService campsiteService, User user) {
        super("RESERVATION MENU", sc);
        this.reservationService = reservationService;  
        this.campsiteService = campsiteService;
        this.user = user;
    } 

    @Override
    public void show() 
    {
        //the menu loop is handled by its methods directly
    }
    

    //guide the user through the process of searching and reserving a campsite
    public void search() 
    {
        while (true) { 
            displayAllCampsites();
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
                displayFilteredCampsites(region, campsiteType);
            } else {
                try {
                    int choice = Integer.parseInt(option);
                    List<Campsite> allCampsites = campsiteService.getAllCampsites();
                    if (choice >= 1 && choice <= allCampsites.size()) {
                        reserveCampsite(allCampsites.get(choice - 1));
                        pause();
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


    //display all available campsites
    public void displayAllCampsites() 
    {
        clear(); 
        setTitle("ALL AVAILABLE CAMPSITES");
        displayHeader();
        System.out.printf("%-34s | %-20s | %-15s | %-10s | %s\n","NAME", "REGION", "TYPE", "PRICE", "CAPACITY");
        System.out.println(createLine(130, '-')); 
        List<Campsite> allCampsites = campsiteService.getAllCampsites();
        if (allCampsites.isEmpty()) {
            System.out.println("No campsites available in the system");
        } else {
            for (int i = 0; i < allCampsites.size(); i++) {
                Campsite c = allCampsites.get(i); 
                System.out.printf("<%2d> %-29s | %-20s | %-15s | RM %-7.2f | %d\n", i+1, c.getName(), c.getRegion(), c.getCampsiteType(), c.getPricePerNight(), c.getCapacity());
            }
        }
    }


    //let the user to filter campsites by region
    public String filterRegion() 
    {
        while(true) {
            clear();
            setTitle("SELECT REGION");
            displayHeader();
            List<String> regions = reservationService.readRegion(MainMenu.REGION_FILE);
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
                    return regions.get(choice-1); 
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

    
    //let the user to filter campsites by type
    public String filterCampsiteTypes() {
        while(true) { 
            clear();
            setTitle("SELECT CAMPSITE TYPE");
            displayHeader();
            List<String> types = reservationService.readRegion(MainMenu.CAMPSITE_TYPE_FILE);
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
                    return types.get(choice-1); 
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


    //display out filtered list of campsites
    public void displayFilteredCampsites(String region, String type) 
    {
        List<Campsite> filteredCamps = campsiteService.filterCampsites(region, type, "");
        while(true) {
            clear();
            System.out.printf("\n%s IN %s\n", type.toUpperCase(), region.toUpperCase());
            System.out.println(createLine(130, '-'));
            System.out.printf("%-29s | %-8s | %-16s | %s\n", "NAME", "TYPE", "PRICE", "CAPACITY");
            System.out.println(createLine(130, '-'));
            if (filteredCamps.isEmpty()) {
                System.out.println("No campsites available"); 
            } else {
                for (int i = 0; i < filteredCamps.size(); i++) {
                    System.out.printf("<%d> %s\n", i+1, filteredCamps.get(i).toString());
                }
            }
            System.out.println("<B> Back");
            System.out.println(createLine(130, '-'));
            System.out.print("Select a campsite to reserve >> ");
            String option = sc.nextLine().trim().toUpperCase();
            if (option.equals("B")) {
                return;
            }
            try {
                int choice = Integer.parseInt(option); 
                if (choice >= 1 && choice <= filteredCamps.size()) {
                    reserveCampsite(filteredCamps.get(choice-1));
                    pause();
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

    //guides the user through the reservation process for a selected campsite
    private void reserveCampsite(Campsite campsite) {
        clear(); 
        System.out.println(createLine(50, '='));
        System.out.println("Reserving: " + campsite.getName()); 
        System.out.println(createLine(50, '='));
        
        while (true) {
            System.out.println("<Q>uit at any time");
            
            System.out.print("Enter check-in date (YYYY-MM-DD): ");
            String checkInInput = sc.nextLine().trim();
            if (checkInInput.equalsIgnoreCase("Q")) { 
                System.out.println("Reservation cancelled");
                return;
            }
            
            LocalDate checkIn;
            try {
                checkIn = LocalDate.parse(checkInInput);
            } catch (DateTimeParseException e) {
                System.out.println("--- Invalid Date Format! Please use YYYY-MM-DD ---");
                continue;
            }

            System.out.print("Enter check-out date (YYYY-MM-DD): ");
            String checkOutInput = sc.nextLine().trim();
            if (checkOutInput.equalsIgnoreCase("Q")) {
                System.out.println("Reservation cancelled");
                return;
            }
            
            LocalDate checkOut;
            try {
                checkOut = LocalDate.parse(checkOutInput);
            } catch (DateTimeParseException e) {
                System.out.println(createLine(4,'-') + " Invalid Date Format! Please use YYYY-MM-DD " + createLine(5,'-'));
                continue;
            }
            
            if (reservationService.validateDateRange(checkIn, checkOut)) {
                System.out.println(createLine(3,'-') + " Check-out date must be after check-in date " + createLine(3,'-'));
                pause();
                continue;   
            } 
            
            if (!reservationService.isCampsiteAvailable(campsite, checkIn, checkOut)) {
                System.out.println(createLine(14, '-') + " CAMPSITE UNAVAILABLE " + createLine(15, '-'));
                System.out.println("This campsite has been booked!");
                return;
            }     
            
            int people = 0;
            while (true) {
                System.out.print("Number of people: ");
                String peopleInput = sc.nextLine().trim();
                if (peopleInput.equalsIgnoreCase("Q")) {
                    System.out.println("Reservation cancelled");
                    return;
                }
                
                try {
                    people = Integer.parseInt(peopleInput);
                    if (people <= 0) {
                        System.out.println(createLine(12,'-') + " Number Must Be Positive! " + createLine(13,'-'));
                        continue;
                    } else if (people > campsite.getCapacity()) {
                        System.out.println(createLine(9,'-') + " Full Capacity! Maximum " + campsite.getCapacity() + " People " + createLine(9,'-'));
                        continue;
                    }
                    break;
                } catch (NumberFormatException e) {
                    System.out.println(createLine(3,'-') + " Invalid Number! Please enter a valid number " + createLine(3,'-'));
                }
            }
            
            long nights = Period.between(checkIn, checkOut).getDays();
            System.out.println("\n" + createLine(50, '-'));
            System.out.println("BOOKING SUMMARY");
            System.out.println(createLine(50, '-'));
            System.out.printf("%-15s: %s\n", "Campsite", campsite.getName());
            System.out.printf("%-15s: %s\n", "Type", campsite.getCampsiteType());
            System.out.printf("%-15s: %s\n", "Dates", checkIn + " to " + checkOut);
            System.out.printf("%-15s: %d nights\n", "Duration", nights);	
            System.out.printf("%-15s: %d guests\n", "Guests", people);
            System.out.printf("%-15s: RM %.2f\n", "Total", campsite.getPricePerNight() * nights * people);
            System.out.println(createLine(50, '-'));
            System.out.print("Confirm booking? (Y/N): ");
            String confirm = sc.nextLine().trim().toUpperCase();
            if (confirm.equals("Y")) {  
                ReservationData reservation = reservationService.makeReservation(user, campsite, checkIn, checkOut, people);
                System.out.println("\n" + createLine(50, '='));
                System.out.println("BOOKING CONFIRMED!\n");
                System.out.printf("Total Cost: RM %.2f\n", reservation.calculateTotalCost());
                System.out.println(createLine(50, '='));
                //pause();
                return; 
            } else if (confirm.equals("N")) {
                System.out.println(createLine(15, '-') + " BOOKING CANCELLED " + createLine(16, '-'));
                return;
            } else {
                System.out.println(createLine(9,'-') + " Invalid Option, Pls Try Again! " + createLine(9,'-'));
                pause();
            }        
        }
    }


    //display all user's past and pending reservations
    public void view() 
    {
        clear();
        setTitle("YOUR RESERVATIONS");
        displayHeader();
        List<ReservationData> allReservations = reservationService.getReservations(); 
        List<ReservationData> userReservations = new ArrayList<>();
        for (ReservationData r : allReservations) {
            if (r.getUserId().equals(user.getUserId())) {
                userReservations.add(r);
            }
        }
        if (userReservations.isEmpty()) {
            System.out.println("No Reservations Found For User ID - " + user.getUserId());
            System.out.println(createLine(70, '-'));
            pause();
            return;
        }
        for (int i = 0; i < userReservations.size(); i++) {
            ReservationData r = userReservations.get(i);
            System.out.println("Reservation #" + (i+1));
            System.out.println(createLine(70, '-'));
            System.out.println("Campsite  : " + r.getCampsite().getName());
            System.out.println("Region    : " + r.getCampsite().getRegion());
            System.out.println("Type      : " + r.getCampsite().getCampsiteType());
            System.out.println("Check-in  : " + r.getCheckIn());
            System.out.println("Check-out : " + r.getCheckOut());
            System.out.println("People    : " + r.getNumberOfPeople());
            System.out.println("Total Cost: RM " + r.calculateTotalCost());
            System.out.println(createLine(70, '-'));
        }
        pause();
    }

    //guides the user through the process of canceling a reservation
    public void cancel() 
    {
        clear();
        setTitle("CANCEL RESERVATION");
        displayHeader();
        List<ReservationData> allReservations = reservationService.getReservations(); 
        List<ReservationData> userReservations = new ArrayList<>();
        for (ReservationData r : allReservations) {
            if (r.getUserId().equals(user.getUserId())) {
                userReservations.add(r);
            }
        }
        if (userReservations.isEmpty()) {
            System.out.println("No Reservations Found To Cancel");
            System.out.println(createLine(130, '-'));
            pause();
            return;
        }
        for (int i = 0; i < userReservations.size(); i++) {
            ReservationData r = userReservations.get(i);
            System.out.printf("<%d> %s - %s to %s\n", i+1, r.getCampsite().getName(), r.getCheckIn(), r.getCheckOut());
        }
        System.out.println("<B>ack ");
        System.out.println(createLine(130, '-'));
        System.out.print("Enter Option >> ");
        String option = sc.nextLine().trim().toUpperCase();
        if (option.equals("B")) {
            return;
        }
        try {
            int choice = Integer.parseInt(option);
            if (choice >= 1 && choice <= userReservations.size()) {
                ReservationData toCancel = userReservations.get(choice-1);
                System.out.println("\nReservation Details:");
                System.out.println(createLine(70, '-'));
                System.out.printf("%-12s: %s\n", "Campsite", toCancel.getCampsite().getName());
                System.out.printf("%-12s: %s\n", "Region", toCancel.getCampsite().getRegion());
                System.out.printf("%-12s: %s\n", "Type", toCancel.getCampsite().getCampsiteType());
                System.out.printf("%-12s: %s\n", "Check-in", toCancel.getCheckIn());
                System.out.printf("%-12s: %s\n", "Check-out", toCancel.getCheckOut());
                System.out.printf("%-12s: %d\n", "People", toCancel.getNumberOfPeople());
                System.out.printf("%-12s: RM %.2f\n", "Total", toCancel.calculateTotalCost());
                System.out.println(createLine(70, '-'));
                System.out.print("Confirm cancellation? (Y/N): ");
                String confirm = sc.nextLine().trim().toUpperCase();
                if (confirm.equals("Y")) {
                    if (reservationService.cancelReservation(toCancel)) {
                        System.out.println(createLine(22, '-') + " CANCELLATION SUCCESSFUL " + createLine(23, '-'));
                    } else {
                        System.out.println(createLine(25, '-') + " CANCELLATION FAILED " + createLine(25, '-'));
                    }
                    pause();
                } else {
                    System.out.println(createLine(25, '-') + " CANCELLATION FAILED " + createLine(25, '-'));
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
