package Admin;

import java.time.LocalDate;
import java.util.*;

import User.Menu;
import User.ReservationService;
import User.ReservationData;
import User.User;


//menu for admin to view and filter reservations
public class ReservationViewer extends Menu implements Filterable<ReservationData> 
{
    private ReservationService reservationService;

    public ReservationViewer(Scanner sc, ReservationService reservationService) 
    {
        super("ALL RESERVATIONS", sc);
        this.reservationService = reservationService;
    }

    
    @Override
    public void show() 
    {
        boolean loop = true;
        while (loop) {
            clear();
            displayHeader();
            displayAllReservations(reservationService.getReservations());

            System.out.println(createLine(130, '='));
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

    //display reservations
    private void displayAllReservations(List<ReservationData> list) 
    {
        System.out.println(createLine(130, '='));
        System.out.printf("%-8s | %-18s | %-12s | %-28s | %-10s | %-8s | %-12s | %-12s\n",
                "User ID", "Region", "Type", "Site Name", "Price", "Guests", "Check-in", "Check-out");
        System.out.println(createLine(130, '-'));

        for (ReservationData r : list) {
            System.out.printf("%-8s | %-18s | %-12s | %-28s | %-10s | %-8d | %-12s | %-12s\n",
                    r.getUserId(), r.getCampsite().getRegion(), r.getCampsite().getCampsiteType(), r.getCampsite().getName(),
                    "RM" + String.format("%.2f", r.calculateTotalCost()), r.getNumberOfPeople(), r.getCheckIn(), r.getCheckOut());
        }
        System.out.println(createLine(130, '='));
    }

    private String getValidatedDate(String prompt, int min, int max, boolean allowBlank) {
        while (true) {
            System.out.print(prompt);
            String input = sc.nextLine().trim();
            if (input.equalsIgnoreCase("Q")) return "Q";
            if (input.equalsIgnoreCase("B")) return "B";
            if (input.isEmpty() && allowBlank) return "";

            try {
                int value = Integer.parseInt(input);
                if (value >= min && value <= max) return input;
            } catch (NumberFormatException e) { }
            System.out.printf("Invalid input! Enter a number between %d and %d, B to go back.\n", min, max);
        }
    }

    //date filter input
    private List<String> getDateFilters() {
        String year = "", month = "", day = "";
        dateLoop: while (true) {
            yearLoop: while (true) {
                String yearInput = getValidatedDate("Enter year (YYYY) [Leave blank to skip <B>ack <Q>uit]: ",
                        1900, LocalDate.now().getYear(), true);
                if (yearInput.equalsIgnoreCase("Q")) return Collections.emptyList();
                if (yearInput.equalsIgnoreCase("B")) break dateLoop;
                year = yearInput;

                monthLoop: while (true) {
                    String monthInput = getValidatedDate("Enter month (MM) [Leave blank to skip <B>ack <Q>uit]: ",
                            1, 12, true);
                    if (monthInput.equalsIgnoreCase("Q")) return Collections.emptyList();
                    if (monthInput.equalsIgnoreCase("B")) continue yearLoop;
                    month = monthInput;

                    while (true) {
                        int maxDay = 31;
                        if (!year.isEmpty() && !month.isEmpty()) {
                            maxDay = LocalDate.of(Integer.parseInt(year), Integer.parseInt(month), 1).lengthOfMonth();
                        }
                        String dayInput = getValidatedDate("Enter day (DD) [Leave blank to skip <B>ack <Q>uit]: ",
                                1, maxDay, true);
                        if (dayInput.equalsIgnoreCase("Q")) return Collections.emptyList();
                        if (dayInput.equalsIgnoreCase("B")) continue monthLoop;
                        day = dayInput;
                        break;
                    }
                    break;
                }
                break;
            }
            break;
        }
        return Arrays.asList(year, month, day);
    }
    
    //filter
    @Override
    public List<ReservationData> filterInteractive() {
        String userId = "", region = "", type = "", name = "";
        Integer inYear = null, inMonth = null, inDay = null;
        Integer outYear = null, outMonth = null, outDay = null;

        String dateChoice = "";
    	clear();
        while (true) {
            System.out.print("Choose field to filter: <D>ate <R>egion <T>ype <N>ame <U>UserID <S>how results <Q>uit: ");
            String choice = sc.nextLine().trim().toUpperCase();

            switch (choice) {
                case "Q": return Collections.emptyList();
                case "D":
                	while (true) {
                        System.out.print("Filter by <I>n Check-in or <O>ut Check-out? ");
                        dateChoice = sc.nextLine().trim().toUpperCase();
                        if (dateChoice.equals("I") || dateChoice.equals("O")) break;
                        System.out.println("Invalid choice! Enter I or O.");
                    }
                    List<String> dateFilters = getDateFilters();
                    if (!dateFilters.isEmpty()) {
                        String y = dateFilters.get(0);
                        String m = dateFilters.get(1);
                        String d = dateFilters.get(2);

                        Integer year = (y == null || y.isEmpty()) ? null : Integer.parseInt(y);
                        Integer month = (m == null || m.isEmpty()) ? null : Integer.parseInt(m);
                        Integer day = (d == null || d.isEmpty()) ? null : Integer.parseInt(d);

                        if (dateChoice.equals("I")) {
                            inYear = year; inMonth = month; inDay = day;
                        } else {
                            outYear = year; outMonth = month; outDay = day;
                        }
                    }
                    break;
                case "R":
                    System.out.print("Enter region or leave blank: ");
                    region = sc.nextLine().trim();
                    break;
                case "T":
                    System.out.print("Enter type or leave blank: ");
                    type = sc.nextLine().trim();
                    break;
                case "N":
                    System.out.print("Enter site name or leave blank: ");
                    name = sc.nextLine().trim();
                    break;
                case "U":
                    System.out.print("Enter UserID or leave blank: ");
                    userId = sc.nextLine().trim();
                    break;
                case "S":
                    List<ReservationData> filtered = reservationService.filterReservations(
                        userId, region, type, name,
                        inYear, inMonth, inDay,
                        outYear, outMonth, outDay
                    );
                    displayFiltered(filtered);
                    return filtered;
                default:
                    System.out.println("Invalid choice! Try again.");
            }
        }
    }

    //display results of filtering
    @Override
    public void displayFiltered(List<ReservationData> filtered) {
    	clear();
        if (filtered.isEmpty()) {
            System.out.println("\nNo reservations found for the selected filters");
        } else {
            displayAllReservations(filtered);
        }
    }

    @Override
    public List<ReservationData> getAllItems() 
    {
        return reservationService.getReservations();
    }
}