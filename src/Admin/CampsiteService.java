package Admin;

import java.io.*;
import java.util.*;
import User.MainMenu;

//handle all busines logic related to campsites (file I/O, data, filtering)
public class CampsiteService 
{
    private static final String CAMPSITE_FILE = MainMenu.CAMPSITE_FILE;
    private List<Campsite> campsiteList;

    public CampsiteService() {
        this.campsiteList = loadCampsites();
    }


    //load campsite data from file and return a list of Campsite objects
    public List<Campsite> loadCampsites() 
    {
        File file = new File(CAMPSITE_FILE);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } 
            catch (IOException e) {
                System.err.println("Error creating file: " + e.getMessage());
            }
        }
        
        List<Campsite> list = new ArrayList<>();
        try (Scanner fileScanner = new Scanner(file)) {
            while (fileScanner.hasNextLine()) {
                String[] parts = fileScanner.nextLine().split("\\|");
                if (parts.length >= 5) {
                    list.add(new Campsite(parts[0], parts[1], parts[2],
                            Double.parseDouble(parts[3]), Integer.parseInt(parts[4])));
                }
            }
        } catch (IOException e) {
            System.out.println("Error loading campsites: " + e.getMessage());
        }
        return list;
    }

    //save all campsites to file
    private void saveCampsites() {
        try (PrintWriter pw = new PrintWriter(new FileOutputStream(CAMPSITE_FILE))) {
            for (Campsite c : campsiteList) {
                pw.println(c.getRegion() + "|" + c.getCampsiteType() + "|" + c.getName() +
                        "|" + c.getPricePerNight() + "|" + c.getCapacity());
            }
        } catch (IOException e) {
            System.out.println("Error saving campsites: " + e.getMessage());
        }
    }


    //add a new campsite and save the list
    public void addCampsite(Campsite newCampsite) {
        campsiteList.add(newCampsite);
        saveCampsites();
        campsiteList = loadCampsites();
    }


    //delete campsite and save the list
    public void deleteCampsite(Campsite campsiteToDelete) {
        campsiteList.remove(campsiteToDelete);
        saveCampsites();
    }


    //update existing campsite
    public void editCampsite(Campsite campsiteToEdit) {
        saveCampsites();
    }

    //filter the list of camsites based on the criteria, region, type, name
    public List<Campsite> filterCampsites(String region, String type, String name) {
        List<Campsite> filtered = new ArrayList<>();
        for (Campsite c : campsiteList) {
            boolean matches = true;
            if (!region.isEmpty() && !c.getRegion().toLowerCase().contains(region.toLowerCase())) {
                matches = false;
            }
            if (!type.isEmpty() && !c.getCampsiteType().toLowerCase().contains(type.toLowerCase())) {
                matches = false;
            }
            if (!name.isEmpty() && !c.getName().toLowerCase().contains(name.toLowerCase())) {
                matches = false;
            }
            if (matches) {
                filtered.add(c);
            }
        }
        return filtered;
    }
    

    //get all available campsite
    public List<Campsite> getAllCampsites() {
    	campsiteList = loadCampsites();
        return campsiteList;
    }
    
    //capitalize the first letter of each word
    public String upperCase(String input) {
        if (input == null || input.isEmpty()) return input;
        String[] words = input.toLowerCase().split("\\s+");
        StringBuilder sb = new StringBuilder();
        for (String word : words) {
            if (!word.isEmpty()) {
                sb.append(Character.toUpperCase(word.charAt(0)))
                  .append(word.substring(1))
                  .append(" ");
            }
        }
        return sb.toString().trim();
    }
}
