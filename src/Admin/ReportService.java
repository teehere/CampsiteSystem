package Admin;

import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import User.MainMenu;

//handle the file I/O, data loading, saving, and filtering.
public class ReportService 
{
    private static final String REPORT_FILE = MainMenu.REPORT_FILE;
    private List<ReportData> reports;
    private CampsiteService campsiteService;

    public ReportService(CampsiteService campsiteService) 
    {
        this.campsiteService = campsiteService;
        this.reports = loadReports();
    }


    //load report data from the file and converts it into a list of ReportData objects.
    public List<ReportData> loadReports() 
    {
    	File file = new File(MainMenu.REPORT_FILE);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                System.err.println("Error creating file: " + e.getMessage());
            }
        }
    	
    	List<ReportData> list = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE;
        try (Scanner fileScanner = new Scanner(new File(REPORT_FILE))) {
            while (fileScanner.hasNextLine()) {
                String line = fileScanner.nextLine();
                String[] parts = line.split("\\|");
                if (parts.length >= 5) {
                    list.add(new ReportData(
                        LocalDate.parse(parts[0], formatter),
                        parts[1],
                        parts[2],
                        parts[3],
                        parts[4]
                    ));
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading reports: " + e.getMessage());
        }
        return list;
    }

    //save all reports into file
    public void saveAllReports() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(REPORT_FILE))) {
            for (ReportData r : reports) {
                writer.println(String.format("%s|%s|%s|%s|%s",
                    r.getDate().toString(),
                    r.getRegion(),
                    r.getCampsiteType(),
                    r.getName(),
                    r.getIssue()
                ));
            }
        } catch (IOException e) {
            System.err.println("Error saving reports: " + e.getMessage());
        }
    }

    //add new report
    public void addReport(ReportData report) {
        reports.add(report);
        saveAllReports();
    }
    
    //delete report
    public void deleteReport(ReportData reportToDelete) {
        reports.remove(reportToDelete);
        saveAllReports();
    }
    
    //update report
    public void modifyReport() {
        saveAllReports();
    }
    

    //filter list of reports based on the date, region, type, or name
    public List<ReportData> filterReports(Integer year, Integer month, Integer day, String region, String type, String name) {
        List<ReportData> filtered = new ArrayList<>();
        for (ReportData r : reports) {
            boolean matches = true;
            LocalDate rd = r.getDate();

            if (year != null && rd.getYear() != year) {
                matches = false;
            }
            if (month != null && rd.getMonthValue() != month) {
                matches = false;
            }
            if (day != null && rd.getDayOfMonth() != day) {
                matches = false;
            }

            if (region != null && !region.isEmpty() && !r.getRegion().toLowerCase().contains(region.toLowerCase())) matches = false;
            if (type != null && !type.isEmpty() && !r.getCampsiteType().toLowerCase().contains(type.toLowerCase())) matches = false;
            if (name != null && !name.isEmpty() && !r.getName().toLowerCase().contains(name.toLowerCase())) matches = false;
            if (matches) filtered.add(r);
        }
        return filtered;
    }

    public List<ReportData> getAllReports() {
        return reports;
    }
    

    public String capitalizeWords(String input) {
        if (input == null || input.trim().isEmpty()) return input;
        String[] words = input.trim().toLowerCase().split("\\s+");
        StringBuilder sb = new StringBuilder();
        for (String w : words) {
            sb.append(Character.toUpperCase(w.charAt(0)))
              .append(w.substring(1))
              .append(" ");
        }
        return sb.toString().trim();
    }

    public List<Campsite> getCampsiteList() {
        return campsiteService.getAllCampsites();
    }
}
