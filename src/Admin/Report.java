package Admin;

import java.io.*;
import java.time.LocalDate;
import java.time.DateTimeException;
import java.util.*;

import User.Menu;


//menu for admin to manage reports, handle user interaction and pass all business logic to ReportService
public class Report extends Menu implements Filterable<ReportData>
{
	private ReportService reportService;
	private CampsiteService campsiteService;
	
	public Report(Scanner sc, ReportService reportService, CampsiteService campsiteService) 
	{
		super("REPORT PORTAL", sc);
		this.sc = sc;
		this.campsiteService = campsiteService;
	    this.reportService = reportService;
	}
	
	@Override
	public void show() {
		boolean loop = true;
        while (loop) {
            clear();
            setTitle("REPORT PORTAL");
            displayHeader();
            displayAllReports(reportService.getAllReports());
        	
            System.out.print(createLine(length,'='));
            System.out.println("\nOptions: <F>ilter  <A>dd  <M>odify  <D>elete  <Q>uit");
            System.out.println(createLine(length,'='));
            System.out.print("Choose an option: ");
            String choice = sc.nextLine().trim().toUpperCase();

            switch (choice) {
                case "F": filterInteractive(); pause();break;
                case "A": addReport(); break;
                case "M": modifyReport(); break;
                case "D": deleteReport(); break;
                case "Q": loop = false; break;
                default: System.out.println("Invalid option!"); pause(); break;
            }
        }
    }
	
	private void pause() {
		System.out.println("Press Enter to continue...");
		sc.nextLine();
	}
	
	//display all reports
	private void displayAllReports(List<ReportData> reports) {
	    System.out.println("All Reports: ");
	    System.out.println("No  | Date       | Region             | Type     | Name                           | Issue");
	    System.out.println(createLine(length,'-'));
	    for (int i = 0; i < reports.size(); i++) {
	        ReportData r = reports.get(i);
	        System.out.printf("%2d. | %s | %-18s | %-8s | %-30s | %s\n", i + 1, r.getDate(), r.getRegion(), r.getCampsiteType(), r.getName(), r.getIssue());
	    }
	}
	
	//date validation
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
	        } catch (NumberFormatException e) {
	            // ignored
	        }

	        System.out.printf("Invalid input! Enter a number between %d and %d, B to go back.\n", min, max);
	    }
	}

	//input cannot be blank
	private String getRequiredInput(String fieldName) {
	    while (true) {
	        System.out.print("Enter " + fieldName + ": ");
	        String input = sc.nextLine().trim();
	        if (input.equalsIgnoreCase("Q")) return "Q";
	        if (input.isEmpty()) {
	            System.out.println(fieldName + " cannot be blank!");
	            continue;
	        }
	        return input;
	    }
	}

	//filter date by year month and day
	private List<String> getDateFilters() {
	    String year = "";
	    String month = "";
	    String day = "";

	    dateLoop: while (true) {
	        yearLoop: while (true) {
	            String yearInput = getValidatedDate(
	                "Enter year (YYYY) [Leave blank to skip <B>ack <Q>uit]: ",
	                1900, LocalDate.now().getYear(), true
	            );
	            if (yearInput.equalsIgnoreCase("Q")) return Collections.emptyList();
	            if (yearInput.equalsIgnoreCase("B")) break dateLoop;
	            year = yearInput;

	            monthLoop: while (true) {
	                String monthInput = getValidatedDate(
	                    "Enter month (MM) [Leave blank to skip <B>ack <Q>uit]: ",
	                    1, 12, true
	                );
	                if (monthInput.equalsIgnoreCase("Q")) return Collections.emptyList();
	                if (monthInput.equalsIgnoreCase("B")) continue yearLoop;
	                month = monthInput;

	                while (true) {
	                    int maxDay = 31;
	                    if (!year.isEmpty() && !month.isEmpty()) {
	                        maxDay = LocalDate.of(
	                            Integer.parseInt(year), 
	                            Integer.parseInt(month), 
	                            1
	                        ).lengthOfMonth();
	                    }
	                    String dayInput = getValidatedDate(
	                        "Enter day (DD) [Leave blank to skip <B>ack <Q>uit]: ",
	                        1, maxDay, true
	                    );
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
	public List<ReportData> filterInteractive() {
		clear();
	    System.out.println("Filter Reports (<Q>uit at any time)\n");

	    String region = "";
	    String type = "";
	    String name = "";
	    Integer year = null;
	    Integer month = null;
	    Integer day = null;

	    while (true) {
	        System.out.print("Choose field to filter: <D>ate <R>egion <T>ype <N>ame <S>how results <Q>uit: ");
	        String choice = sc.nextLine().trim().toUpperCase();

	        switch (choice) {
	            case "Q":
	                return Collections.emptyList();
	            case "D":
	            	List<String> dateFilters = getDateFilters();
	                if (dateFilters.isEmpty()) {
	                    //user quit or cancelled - keep year/month/day as null (no date filter)
	                    year = null;
	                    month = null;
	                    day = null;
	                } else {
	                    String y = dateFilters.get(0);
	                    String m = dateFilters.get(1);
	                    String d = dateFilters.get(2);

	                    year = (y == null || y.trim().isEmpty()) ? null : Integer.parseInt(y.trim());
	                    month = (m == null || m.trim().isEmpty()) ? null : Integer.parseInt(m.trim());
	                    day = (d == null || d.trim().isEmpty()) ? null : Integer.parseInt(d.trim());
	                }
	                break;
	            case "R":
	                System.out.print("Enter region or leave blank: ");
	                region = sc.nextLine().trim();
	                if (region.equalsIgnoreCase("Q")) return Collections.emptyList();
	                break;
	            case "T":
	                System.out.print("Enter type or leave blank: ");
	                type = sc.nextLine().trim();
	                if (type.equalsIgnoreCase("Q")) return Collections.emptyList();
	                break;
	            case "N":
	                System.out.print("Enter name or leave blank: ");
	                name = sc.nextLine().trim();
	                if (name.equalsIgnoreCase("Q")) return Collections.emptyList();
	                break;
	            case "S":
	            	clear();
	                List<ReportData> filtered = reportService.filterReports(year, month, day, region, type, name);
	                displayFiltered(filtered);
	                return filtered;
	            default:
	                System.out.println("Invalid choice! Try again.");
	        }
	    }

	}

	@Override
	public void displayFiltered(List<ReportData> filtered) {
		clear();
	    if (filtered.isEmpty()) {
	        System.out.println("\nNo reports found with the selected filters.");
	        pause();
	        return;
	    }

	    System.out.println("\nFiltered Reports:");
	    System.out.println("No  | Date       | Region             | Type     | Name                           | Issue");
	    System.out.println(createLine(length,'-'));
	    for (int i = 0; i < filtered.size(); i++) {
	        ReportData r = filtered.get(i);
	        System.out.printf("%2d. | %s | %-18s | %-8s | %-30s | %s\n",
	                          i + 1, r.getDate(), r.getRegion(), r.getCampsiteType(), r.getName(), r.getIssue());
	    }
	}
	
	@Override
	public List<ReportData> getAllItems() {
		return reportService.getAllReports();
	}
	
	//add report
	private void addReport() {
	    while (true) {
	        clear();
	        setTitle("ADD REPORT");
	        displayHeader();
	        List<Campsite> campsiteList = campsiteService.loadCampsites();

	        if (campsiteList.isEmpty()) {
	            System.out.println("No campsites available to add reports.");
	            pause();
	            return;
	        }

	        System.out.println("Add New Report (<Q>uit to cancel at any time)");
	        System.out.println(createLine(length, '='));
	        displayCampsites(campsiteList);

	        System.out.println("\nSelect campsite <F>ilter <Q>uit: ");
	        System.out.print("Enter option: ");
	        String input = sc.nextLine().trim().toUpperCase();

	        if (input.equals("Q")) return;

	        List<Campsite> targetList = campsiteList;

	        if (input.equals("F")) {
	        	clear();
	            List<Campsite> filtered = filterCampsites();
	            if (filtered.isEmpty()) {
	                System.out.println("No campsites match your filter.");
	                pause();
	                continue;
	            }
	            clear();
	            displayCampsites(filtered);
	            targetList = filtered;
	            System.out.print("Enter campsite number to select (or Q to cancel): ");
	            input = sc.nextLine().trim().toUpperCase();
	            if (input.equals("Q")) return;
	        }

	        try {
	            int selectedIndex = Integer.parseInt(input) - 1;
	            if (selectedIndex < 0 || selectedIndex >= targetList.size()) {
	                System.out.println("Invalid campsite selection! Try again.");
	                pause();
	                continue;
	            }

	            Campsite selected = targetList.get(selectedIndex);

	            //ask for date
	            LocalDate date = null;
	            while (true) {
	                LocalDate today = LocalDate.now();

	                String yearStr = getValidatedDate(
	                    "Enter year (YYYY) [Leave blank for today]: ",
	                    1900, today.getYear(), true
	                );
	                if (yearStr.equalsIgnoreCase("Q")) return;
	                int year = yearStr.isEmpty() ? today.getYear() : Integer.parseInt(yearStr);

	                String monthStr = getValidatedDate(
	                    "Enter month (MM) [Leave blank for today]: ",
	                    1, 12, true
	                );
	                if (monthStr.equalsIgnoreCase("Q")) return;
	                int month = monthStr.isEmpty() ? today.getMonthValue() : Integer.parseInt(monthStr);

	                int maxDay = LocalDate.of(year, month, 1).lengthOfMonth();
	                String dayStr = getValidatedDate(
	                    "Enter day (DD) [Leave blank for today]: ",
	                    1, maxDay, true
	                );
	                if (dayStr.equalsIgnoreCase("Q")) return;
	                int day = dayStr.isEmpty() ? today.getDayOfMonth() : Integer.parseInt(dayStr);

	                try {
	                    date = LocalDate.of(year, month, day);
	                    break;
	                } catch (DateTimeException e) {
	                    System.out.println("Invalid date combination! Try again.");
	                }
	            }

	            //ask for issues
	            String issue = getRequiredInput("issue description");
	            if (issue.equalsIgnoreCase("Q")) return;
	            
	            String finalIssue = reportService.capitalizeWords(issue);

	            //save report
	            ReportData newReport = new ReportData(date, selected.getRegion(), selected.getCampsiteType(), selected.getName(), finalIssue);
	            reportService.addReport(newReport);
	            System.out.println("Report added successfully!");
	            pause();
	            return;

	        } catch (NumberFormatException e) {
	            System.out.println("Invalid input! Please enter a number, 'F', or 'Q'.");
	            pause();
	        }
	    }
	}

	//display campsites
	private void displayCampsites(List<Campsite> campsites) {
	    System.out.println("\nAvailable Campsites:");
	    System.out.println("No  | Region             | Type     | Name");
	    System.out.println(createLine(length, '-'));
	    for (int i = 0; i < campsites.size(); i++) {
	        Campsite c = campsites.get(i);
	        System.out.printf("%2d. | %-18s | %-8s | %-30s\n",
	                          i + 1, c.getRegion(), c.getCampsiteType(), c.getName());
	    }
	}

	//filter campsite
	private List<Campsite> filterCampsites() {
	    String region = "";
	    String type = "";
	    String name = "";

	    while (true) {
	        System.out.println("Filter Campsites by <R>egion <T>ype <N>ame <S>how results <Q>uit: ");
	        System.out.print("Enter option: ");
	        String choice = sc.nextLine().trim().toUpperCase();

	        switch (choice) {
	            case "Q":
	                return Collections.emptyList();
	            case "R":
	                System.out.print("Enter region or leave blank: ");
	                region = sc.nextLine().trim();
	                if (region.equalsIgnoreCase("Q")) return Collections.emptyList();
	                break;
	            case "T":
	                System.out.print("Enter type or leave blank: ");
	                type = sc.nextLine().trim();
	                if (type.equalsIgnoreCase("Q")) return Collections.emptyList();
	                break;
	            case "N":
	                System.out.print("Enter name or leave blank: ");
	                name = sc.nextLine().trim();
	                if (name.equalsIgnoreCase("Q")) return Collections.emptyList();
	                break;
	            case "S":
	                return campsiteService.filterCampsites(region, type, name);
	            default:
	                System.out.println("Invalid choice! Try again.");
	        }
	    }
	}



	
	//modify report
	private void modifyReport() {
	    while (true) {
	        clear();
	        setTitle("MODIFY REPORT");
	        displayHeader();
	        List<ReportData> reports = reportService.getAllReports();
	        displayAllReports(reports);

	        if (reports.isEmpty()) {
	            System.out.println("No reports available.");
	            pause();
	            return;
	        }
	        
	        System.out.println(createLine(length,'='));
	        System.out.println("Modify Report Issue\nEnter report number to modify <F>ilter <Q>uit");
	        System.out.println(createLine(length,'='));
	        System.out.print("Enter option: ");
	        String input = sc.nextLine().trim().toUpperCase();

	        if (input.equals("Q")) return;
	        if (input.equals("F")) {
	        	clear();
	            List<ReportData> filtered = filterInteractive();
	            if (filtered.isEmpty()) continue;
	            int selectedIndex = selectReportFromList(filtered, "modify");
	            if (selectedIndex == -1) continue;
	            modifyIssue(filtered.get(selectedIndex));
	            return;
	        }

	        try {
	            int selectedIndex = Integer.parseInt(input) - 1;
	            if (selectedIndex < 0 || selectedIndex >= reports.size()) {
	                System.out.println("Invalid selection! Try again.");
	                pause();
	                continue;
	            }
	            modifyIssue(reports.get(selectedIndex));
	            return;
	        } catch (NumberFormatException e) {
	            System.out.println("Invalid input! Enter a number, 'F', or 'Q'.");
	            pause();
	        }
	    }
	}


	private int selectReportFromList(List<ReportData> list, String action) {
	    while (true) {
	        System.out.print("Enter report number to " + action + " (or Q to quit): ");
	        String input = sc.nextLine().trim();
	        if (input.equalsIgnoreCase("Q")) return -1;

	        try {
	            int selectedIndex = Integer.parseInt(input) - 1;
	            if (selectedIndex >= 0 && selectedIndex < list.size()) return selectedIndex;
	        } catch (NumberFormatException e) {
	            // ignore
	        }
	        System.out.println("Invalid selection! Try again.");
	    }
	}


	private void modifyIssue(ReportData report) {
	    System.out.printf("Current issue: %s\n", report.getIssue());
	    System.out.print("Enter new issue (leave blank to keep current, Q to cancel): ");
	    String issue = sc.nextLine().trim();
	    if (issue.equalsIgnoreCase("Q")) return;
	    if (!issue.isEmpty()) report.setIssue(reportService.capitalizeWords(issue));

	    reportService.modifyReport(); //save report
	    System.out.println("Report issue updated successfully!");
	    pause();
	}


	//delete report
	private void deleteReport() {
	    while (true) {
	        clear();
	        setTitle("DELETE REPORT");
	        displayHeader();
	        List<ReportData> reports = reportService.getAllReports();
	        displayAllReports(reports);

	        if (reports.isEmpty()) {
	            System.out.println("No reports available.");
	            pause();
	            return;
	        }
	        
	        System.out.println(createLine(length,'='));
	        System.out.println("Delete Report\nEnter report number to delete <F>ilter <Q>uit");
	        System.out.println(createLine(length,'='));
	        System.out.print("Enter option: ");
	        String input = sc.nextLine().trim().toUpperCase();

	        if (input.equals("Q")) return;
	        if (input.equals("F")) {
	        	clear();
	            List<ReportData> filtered = filterInteractive();
	            if (filtered.isEmpty()) continue;
	            int selectedIndex = selectReportFromList(filtered, "delete");
	            if (selectedIndex == -1) continue;
	            deleteSelectedReport(filtered.get(selectedIndex));
	            return;
	        }

	        try {
	            int selectedIndex = Integer.parseInt(input) - 1;
	            if (selectedIndex < 0 || selectedIndex >= reports.size()) {
	                System.out.println("Invalid selection! Try again.");
	                pause();
	                continue;
	            }
	            deleteSelectedReport(reports.get(selectedIndex));
	            return;
	        } catch (NumberFormatException e) {
	            System.out.println("Invalid input! Enter a number, 'F', or 'Q'.");
	            pause();
	        }
	    }
	}

	private void deleteSelectedReport(ReportData report) {
	    System.out.println("Deleting report: " + report.getName() + " | " + report.getIssue());
	    System.out.print("Are you sure? (Y/N): ");
	    String confirm = sc.nextLine().trim().toUpperCase();
	    if (confirm.equals("Y")) {
	        reportService.deleteReport(report);
	        System.out.println("Report deleted successfully!");
	    } else {
	        System.out.println("Deletion cancelled.");
	    }
	    pause();
	}
}
