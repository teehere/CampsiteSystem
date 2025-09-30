package Admin;

import User.MainMenu;
import User.AuthService;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.Scanner;


//handles the business logic for administrative tasks,
//such as registering new admin, managing user data, and file operations.

public class AdminService 
{
	private AuthService authService;
	
	public AdminService(AuthService authService) 
	{
        this.authService = authService;
    }
    
    public void registerNewAdmin(String newUsername, String newPassword) {
        String newAdminId = generateNewAdminId();
        try (PrintWriter pw = new PrintWriter(new FileOutputStream(MainMenu.USER_INFO_FILE, true))) {
            pw.println(newAdminId + "|" + newUsername + "|" + newPassword + "|admin");
            System.out.println("New admin account registered successfully with ID: " + newAdminId);
        } catch (FileNotFoundException e) {
            System.out.println("Error writing to user info file: " + e.getMessage());
        }
    }


    private String generateNewAdminId() {
        int highestNumber = 0;
        try (Scanner fileScanner = new Scanner(new File(MainMenu.USER_INFO_FILE))) {
            while (fileScanner.hasNextLine()) {
                String line = fileScanner.nextLine();
                String[] parts = line.split("\\|");
                if (parts.length > 0 && parts[0].startsWith("A")) {
                    try 
                    {
                        int currentNumber = Integer.parseInt(parts[0].substring(1));
                        if (currentNumber > highestNumber) 
                        {
                            highestNumber = currentNumber;
                        }
                    } 
                    catch (NumberFormatException e) 
                    {
                        //ignore malformed IDs
                    }
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("User info file not found. Starting ID from A001");
        }
        highestNumber++;
        return String.format("A%03d", highestNumber);
    }

    public boolean isPasswordValid(String password) {
        if (password.length() < 8) return false;
        if (!password.matches(".*[A-Z].*")) return false;
        if (!password.matches(".*[a-z].*")) return false;
        if (!password.matches(".*[0-9].*")) return false;
        return true;
    }
}
