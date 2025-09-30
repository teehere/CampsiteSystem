package User;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import Admin.Campsite;
import Admin.CampsiteService;
import Admin.Review;


//handle all business logic related to reservations, manage data persistence, validation, and
//communicate with other services
public class ReservationService 
{
    private List<Campsite> availableCampsites;
    private List<ReservationData> reservations;
    private CampsiteService campsiteService;
    private PaymentService paymentService;
    private ReviewService reviewService;
    private FileService fileService;
    
    private static final String RESERVATION_FILE = MainMenu.RESERVATION_FILE;

    public ReservationService(CampsiteService campsiteService, PaymentService paymentService, ReviewService reviewService, FileService fileService) {
        this.campsiteService = campsiteService;
        this.paymentService = paymentService;
        this.reviewService = reviewService;
        this.fileService = fileService;
        this.availableCampsites = campsiteService.getAllCampsites();
        this.reservations = loadReservations();
    }
    
    public List<Campsite> getAvailableCampsites() 
    {
        return availableCampsites;
    }

    public List<ReservationData> getReservations() 
    {
        return reservations;
    }

    //loads reservation data from the file and converts it into ReservationData objects
    public List<ReservationData> loadReservations() 
    {
    	File file = new File(RESERVATION_FILE);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                System.err.println("Error creating file: " + e.getMessage());
            }
        }
    	
    	List<ReservationData> list = new ArrayList<>();
        try (Scanner fileScanner = new Scanner(new File(RESERVATION_FILE))) {
            while (fileScanner.hasNextLine()) {
                String line = fileScanner.nextLine();
                String[] parts = line.split("\\|");
                if (parts.length == 8) { //expecting exactly 8 parts
                    String userId = parts[0];
                    String name = parts[3];
                    int people = Integer.parseInt(parts[5]);
                    LocalDate checkIn = LocalDate.parse(parts[6]);
                    LocalDate checkOut = LocalDate.parse(parts[7]);
                    
                    Campsite campsite = findCampsiteByName(name);
                    if (campsite != null) {
                        ReservationData reservation = new ReservationData(userId, campsite, checkIn, checkOut, people);
                        list.add(reservation);
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading reservations: " + e.getMessage());
        }
        return list;
    }
    
    private Campsite findCampsiteByName(String name) 
    {
        for (Campsite c : availableCampsites) {
            if (c.getName().equals(name)) {
                return c;
            }
        }
        return null;
    }


    //read from file line by line
    public List<String> readRegion(String filename) 
    {  
        return fileService.readFileByLine(filename);
    }


    //save all reservations to file
    public void saveAllReservations() 
    {
        try (PrintWriter writer = new PrintWriter(new FileWriter(RESERVATION_FILE))) {
            for (ReservationData r : reservations) {
                 writer.println(r.getUserId() + '|' + 
                          r.getCampsite().getRegion() + '|' + 
                          r.getCampsite().getCampsiteType() + '|' + 
                          r.getCampsite().getName() + '|' + 
                          r.calculateTotalCost() + '|' + 
                          r.getNumberOfPeople() + '|' + 
                          r.getCheckIn() + '|' + 
                          r.getCheckOut());
            }
        } catch (IOException e) {
            System.err.println("Error Saving Reservation: " + e.getMessage());
        }
    }


    //create new reservation and save into file
    public ReservationData makeReservation(User user, Campsite campsite, LocalDate checkIn, LocalDate checkOut, int people) {
        ReservationData reservation = new ReservationData(user.getUserId(), campsite, checkIn, checkOut, people);
        reservations.add(reservation);
        saveAllReservations();
        return reservation;
    }


    //validate the checkout date is after the checkin date
    public boolean validateDateRange(LocalDate checkIn, LocalDate checkOut) 
    {
        return checkOut.isBefore(checkIn) || checkOut.isEqual(checkIn);
    }


    //checks if a campsite is available for a given date range
    public boolean isCampsiteAvailable(Campsite campsite, LocalDate checkIn, LocalDate checkOut) 
    {
        for (ReservationData reservation : reservations) {
            if (reservation.getCampsite().getName().equals(campsite.getName())) {
                LocalDate start = reservation.getCheckIn();
                LocalDate end = reservation.getCheckOut();
                if (!(checkOut.isBefore(start) || checkIn.isAfter(end))) {
                    return false;
                }
            }
        }
        return true;
    }

    
    //cancel reservation
    public boolean cancelReservation(ReservationData toCancel) 
    {
        boolean removed = reservations.removeIf(r ->
            r.getUserId().equals(toCancel.getUserId()) &&
            r.getCampsite().getName().equals(toCancel.getCampsite().getName()) &&
            r.getCheckIn().equals(toCancel.getCheckIn()) &&
            r.getCheckOut().equals(toCancel.getCheckOut()));
        if (removed) {
            saveAllReservations();
        }
        return removed;
    }
    
    //filters a list of reservations based on various criteria
    public List<ReservationData> filterReservations(String userId, String region, String type, String name, Integer inYear, Integer inMonth, Integer inDay, Integer outYear, Integer outMonth, Integer outDay){
        List<ReservationData> filtered = new ArrayList<>();
        for (ReservationData r : reservations) {
            boolean matches = true;
            
            if (!userId.isEmpty() && !r.getUserId().toLowerCase().contains(userId.toLowerCase())) matches = false;
            if (!region.isEmpty() && !r.getCampsite().getRegion().toLowerCase().contains(region.toLowerCase())) matches = false;
            if (!type.isEmpty() && !r.getCampsite().getCampsiteType().toLowerCase().contains(type.toLowerCase())) matches = false;
            if (!name.isEmpty() && !r.getCampsite().getName().toLowerCase().contains(name.toLowerCase())) matches = false;
            
            LocalDate checkIn = r.getCheckIn();
            if (inYear != null && checkIn.getYear() != inYear) matches = false;
            if (inMonth != null && checkIn.getMonthValue() != inMonth) matches = false;
            if (inDay != null && checkIn.getDayOfMonth() != inDay) matches = false;

            // Flexible check-out filter
            LocalDate checkOut = r.getCheckOut();
            if (outYear != null && checkOut.getYear() != outYear) matches = false;
            if (outMonth != null && checkOut.getMonthValue() != outMonth) matches = false;
            if (outDay != null && checkOut.getDayOfMonth() != outDay) matches = false;
            
            if (matches) filtered.add(r);
        }
        return filtered;
    }
    
    //process the reservation payment and save into file
    public boolean processResPayment(ReservationData reservation, String paymentMethod) 
    {
        PaymentData newPayment = new PaymentData(
            reservation.getUserId(),
            reservation.calculateTotalCost(),
            paymentMethod,
            "COMPLETED"
        );
        
        paymentService.addPayment(newPayment);
        reservation.setPaymentData(newPayment);
        reservation.setStatus("CONFIRMED");
        saveAllReservations();
        cancelReservation(reservation);
        return true;
    }
}