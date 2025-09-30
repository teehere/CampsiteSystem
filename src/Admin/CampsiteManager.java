package Admin;

import java.util.*;
import User.Menu;

//menu to manage campsites
public class CampsiteManager extends Menu implements Filterable<Campsite> 
{
    private CampsiteService campsiteService;

    public CampsiteManager(Scanner sc, CampsiteService campsiteService) 
    {
        super("CAMPSITE MANAGER", sc);
        this.sc = sc;
        this.campsiteService = new CampsiteService();
    }

    @Override
    public void show() {
        boolean loop = true;
        while (loop) {
            clear();
            setTitle("CAMPSITE MANAGER");
            displayHeader();
            displayAllCampsites(campsiteService.getAllCampsites());

            System.out.print(createLine(100, '='));
            System.out.println("\nOptions: <F>ilter  <A>dd  <M>odify  <D>elete  <Q>uit");
            System.out.println(createLine(100, '='));
            System.out.print("Choose an option: ");
            String choice = sc.nextLine().trim().toUpperCase();

            switch (choice) {
                case "F": 
                    filterInteractive();
                    pause(); 
                    break;
                case "A": addCampingSites(); break;
                case "M": editCampingSites(); break;
                case "D": deleteCampingSites(); break;
                case "Q": loop = false; break;
                default: System.out.println("Invalid option!"); pause(); break;
            }
        }
    }

    private void pause() {
        System.out.println("Press Enter to continue...");
        sc.nextLine();
    }

    //displays a list of campsites.
    private void displayAllCampsites(List<Campsite> campsites) {
        System.out.println("All Campsites: ");
        System.out.printf("%-3s| %-18s | %-12s | %-30s | %-11s | %-8s\n",
                "No.", "Region", "Type", "Site Name", "Price", "Capacity");
        System.out.println(createLine(100, '-'));

        for (int i = 0; i < campsites.size(); i++) {
            Campsite c = campsites.get(i);
            System.out.printf("%-3d| %-18s | %-12s | %-30s | RM%-9.2f | %-8d\n",
                    i + 1, c.getRegion(), c.getCampsiteType(),
                    c.getName(), c.getPricePerNight(), c.getCapacity());
        }
    }

    private Campsite selectCampsite() {
        List<Campsite> options = new ArrayList<>(campsiteService.getAllCampsites());
        boolean justFiltered = false;

        while (true) {
            if (options.isEmpty()) {
                System.out.println("\nNo campsites available");
                return null;
            }

            if (!justFiltered) {
            	clear();
                System.out.println("\nAvailable Campsites:");
                System.out.printf("%-3s| %-18s | %-12s | %-30s | %-10s | %-8s\n",
                        "No.", "Region", "Type", "Site Name", "Price", "Capacity");
                for (int i = 0; i < options.size(); i++) {
                    Campsite c = options.get(i);
                    
                    System.out.printf("%-3d| %-18s | %-12s | %-30s | RM%-9.2f | %-8d\n",
                            i + 1, c.getRegion(), c.getCampsiteType(),
                            c.getName(), c.getPricePerNight(), c.getCapacity());
                }
            }
            justFiltered = false;

            System.out.println(createLine(110,'='));
            System.out.println("Enter number to select <F>ilter <Q>uit: ");
            System.out.println(createLine(110,'='));
            System.out.print("Enter Option: ");
            String input = sc.nextLine().trim().toUpperCase();

            if (input.equals("Q")) return null;

            if (input.equals("F")) {
                options = filterInteractive(); 
                justFiltered = true;
                continue;
            }
            
            try {
                int idx = Integer.parseInt(input) - 1;
                if (idx >= 0 && idx < options.size()) {
                    return options.get(idx);
                } else {
                    System.out.println("Invalid Number. Please Try Again");
                    pause();
                }
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number");
                pause();
            }
        }
    }
    
    //filter
    @Override
    public List<Campsite> filterInteractive() {
    	clear();
        System.out.println("Filter Campsites (<Q>uit at any time)\n");
        String region = "";
        String type = "";
        String name = "";

        while (true) {
            System.out.print("Choose filter <R>egion <T>ype <N>Name <S>how results <Q>uit: ");
            String choice = sc.nextLine().trim().toUpperCase();

            switch (choice) {
                case "Q":
                    return Collections.emptyList();
                case "R":
                    System.out.print("Enter region blank to skip: ");
                    region = sc.nextLine().trim();
                    if (region.equalsIgnoreCase("Q")) return Collections.emptyList();
                    break;
                case "T":
                    System.out.print("Enter type blank to skip: ");
                    type = sc.nextLine().trim();
                    if (type.equalsIgnoreCase("Q")) return Collections.emptyList();
                    break;
                case "N":
                    System.out.print("Enter name blank to skip: ");
                    name = sc.nextLine().trim();
                    if (name.equalsIgnoreCase("Q")) return Collections.emptyList();
                    break;
                case "S":
                    List<Campsite> filtered = campsiteService.filterCampsites(region, type, name);
                    displayFiltered(filtered);
                    return filtered;
                default:
                    System.out.println("Invalid choice! Try again");
            }
        }
    }


    @Override
    public void displayFiltered(List<Campsite> filtered) {
    	clear();
        if (filtered.isEmpty()) {
            System.out.println("\nNo campsites found with the selected filters.");
            return;
        }

        System.out.println("\nFiltered Campsites:");
        System.out.printf("%-3s| %-18s | %-12s | %-30s | %-10s | %-8s\n",
                "No.", "Region", "Type", "Site Name", "Price", "Capacity");
        System.out.println(createLine(100, '-'));

        for (int i = 0; i < filtered.size(); i++) {
            Campsite c = filtered.get(i);
            System.out.printf("%-2d | %-18s | %-12s | %-30s | RM%-9.2f | %-8d\n",
                    i + 1, c.getRegion(), c.getCampsiteType(),
                    c.getName(), c.getPricePerNight(), c.getCapacity());
        }
    }

    @Override
    public List<Campsite> getAllItems() {
        return campsiteService.getAllCampsites();
    }

    private void addCampingSites() {
        clear();
        setTitle("ADD NEW CAMPING SITE");
        displayHeader();

        String type = "";
        while (true) {
            System.out.println("Select type:");
            System.out.println("1) Tent");
            System.out.println("2) Cabin");
            System.out.println("3) Glamping");
            System.out.println("4) Back");
            System.out.print("Enter choice (1-4): ");
            String typeChoice = sc.nextLine().trim();

            switch (typeChoice) {
                case "1": type = "Tent"; break;
                case "2": type = "Cabin"; break;
                case "3": type = "Glamping"; break;
                case "4": return;
                default:
                    System.out.println("Invalid type. Please choose 1–4");
                    continue;
            }
            break;
        }
        clear();
        System.out.println("Enter Q to quit at any time");
        
        String name = "";
        while(true) {
	        System.out.print("Enter site name: ");
	        name = sc.nextLine().trim();
	        if(name.equalsIgnoreCase("Q")) return;
	        if(name.isEmpty()) {
	        	System.out.println("Name cannot be empty");
	        	continue;
	        }
	        break;
        }

        List<String> allowedRegions = Arrays.asList(
                "Cameron Highlands", "Genting Highlands", "Langkawi",
                "Perhentian Islands", "Taman Negara", "Tioman Island"
        );
        String region = "";
        while (true) {
            System.out.print("Enter region (or Q to cancel): ");
            region = sc.nextLine().trim();

            if (region.equalsIgnoreCase("Q")) return;
            if (region.isEmpty()) {
                System.out.println("Region cannot be empty");
                continue;
            }
            
            boolean valid = false;
            for (String r : allowedRegions) {
                if (r.equalsIgnoreCase(region)) {
                    region = r;
                    valid = true;
                    break;
                }
            }

            if (!valid) {
                System.out.println("Invalid region!");
                continue;
            }
            break;
        }

        double price = 0;
        while (true) {
            System.out.print("Enter price: ");
            String priceInput = sc.nextLine().trim();
            try {
                price = Double.parseDouble(priceInput);
                if (price < 0) {
                    System.out.println("Price cannot be negative");
                    continue;
                }
                break;
            } catch (NumberFormatException e) {
                System.out.println("Invalid price! Please enter a number");
            }
        }

        int capacity = 0;
        while (true) {
            System.out.print("Enter capacity: ");
            String capacityInput = sc.nextLine().trim();
            try {
                capacity = Integer.parseInt(capacityInput);
                if (capacity <= 0) {
                    System.out.println("Capacity must be greater than 0");
                    continue;
                }
                break;
            } catch (NumberFormatException e) {
                System.out.println("Invalid capacity! Please enter a whole number.");
            }
        }

        campsiteService.addCampsite(new Campsite(region, type, name, price, capacity));
        System.out.println("New " + type + " in " + region + " added successfully!");
        pause();
    }


    private void deleteCampingSites() {
    	clear();
        Campsite toDelete = selectCampsite();
        if (toDelete == null) return;
        
        System.out.println("Are you sure you want to delete " + toDelete.getName() + "? (Y/N): ");
        System.out.print("Enter option: ");
        String confirmation = sc.nextLine().trim().toUpperCase();

        if (confirmation.equals("Y")) {
            campsiteService.deleteCampsite(toDelete);
            System.out.println("Campsite deleted successfully!");
        } else {
            System.out.println("Deletion cancelled");
        }
        pause();
    }

    private void editCampingSites() {
    	clear();
        Campsite toEdit = selectCampsite();
        if (toEdit == null) return;

        System.out.println("\nEditing campsite: " + toEdit.getName());

        System.out.print("Enter new name (or press Enter to keep current): ");
        String newName = sc.nextLine().trim();
        if (!newName.isEmpty()) {
            toEdit.setName(campsiteService.upperCase(newName));
        }

        while (true) {
            System.out.print("Enter new price (or press Enter to keep current): ");
            String newPriceStr = sc.nextLine().trim();
            if (newPriceStr.isEmpty()) break;

            try {
                double newPrice = Double.parseDouble(newPriceStr);
                if (newPrice < 0) {
                    System.out.println("Price cannot be negative. Try again");
                    continue;
                }
                toEdit.setPricePerNight(newPrice);
                break;
            } catch (NumberFormatException e) {
                System.out.println("Invalid price format! Please enter a number");
            }
        }

        while (true) {
            System.out.print("Enter new capacity (or press Enter to keep current): ");
            String newCapacityStr = sc.nextLine().trim();
            if (newCapacityStr.isEmpty()) break;

            try {
                int newCapacity = Integer.parseInt(newCapacityStr);
                if (newCapacity <= 0) {
                    System.out.println("Capacity must be greater than 0. Try again");
                    continue;
                }
                toEdit.setCapacity(newCapacity);
                break;
            } catch (NumberFormatException e) {
                System.out.println("Invalid capacity format! Please enter a whole number");
            }
        }
        
        campsiteService.editCampsite(toEdit);
        System.out.println("Campsite updated successfully!");
        pause();
    }
}
