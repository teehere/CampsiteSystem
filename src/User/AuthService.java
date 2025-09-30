package User;

import java.io.*;
import java.util.*;

import Admin.Admin;


//serve as a service class that handle all business logic for user authentication and register
public class AuthService 
{
    public static final String USER_INFO_FILE = "user_Info.txt";
    private Scanner sc;

    public AuthService(Scanner sc) 
    {
        this.sc = sc;
    }

    //create a default admin acc if none
    public void initializeUserInfoFile() 
    {
        File userInfo = new File(USER_INFO_FILE);
        if (!userInfo.exists() || userInfo.length() == 0) {
            try (PrintWriter pw = new PrintWriter(new FileOutputStream(USER_INFO_FILE, true))) {
                pw.println("A001|admin|Admin123123|admin");
                System.out.println("Default admin account created in " + USER_INFO_FILE);
            } catch (FileNotFoundException e) {
                System.out.println("Error creating user info file: " + e.getMessage());
            }
        }
    }

    //authenticate a user based on user id/username and password
    public User authenticateUser(String userInput, String password) {
        try (Scanner fileScanner = new Scanner(new File(USER_INFO_FILE))) {
            while (fileScanner.hasNextLine()) {
                String line = fileScanner.nextLine();
                String[] parts = line.split("\\|");
                if (parts.length == 4) {
                    String userId = parts[0];
                    String username = parts[1];
                    String storedPassword = parts[2];
                    String type = parts[3];

                    if ((userId.equals(userInput) || username.equals(userInput)) && storedPassword.equals(password)) {
                        if (type.equals("admin")) {
                            return new Admin(userId, username, storedPassword);
                        } else {
                            return new User(userId, username, storedPassword, type);
                        }
                    }
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("Error: User info file not found. " + e.getMessage());
        }
        return null;
    }

    //create new acc
    public void registerUser() 
    {
    	System.out.println("\n------ Register New User Account ------");
    	System.out.println("<Q> to quit at any time");
        System.out.print("Enter new Username: ");
        String newUsername = sc.nextLine();
        
        if (newUsername.toUpperCase().equalsIgnoreCase("Q")) {
            System.out.println("Registration cancelled");
            return;
        }
        
        if (isUsernameExists(newUsername)) {
            System.out.println("Username already exists. Please choose a different one.");
            return;
        }

        String newPassword;
        String confirmPassword;

        while (true) {
            newPassword = readHiddenPassword("Enter new Password: ");
            
            if (newPassword.toUpperCase().equalsIgnoreCase("Q")) {
                System.out.println("Registration cancelled");
                return;
            }
            
            if (!isPasswordValid(newPassword)) {
                System.out.println("\nPassword must be at least 8 characters long and contain " +
                                   "at least one uppercase letter, one lowercase letter, and one number.");
                continue;
            }
            
            confirmPassword = readHiddenPassword("Confirm new Password: ");
            
            if (confirmPassword.toUpperCase().equalsIgnoreCase("Q")) {
                System.out.println("Registration cancelled");
                return;
            }
            
            if (newPassword.equals(confirmPassword)) {
                break;
            } else {
                System.out.println("\nPasswords Do Not Match. Please Try Again");
            }
        }
        
        String newUserId = generateNewUserId();
        User newUser = new User(newUserId, newUsername, newPassword, "user");
        if (saveUser(newUser)) {
            System.out.println("\nNew user account registered successfully with ID: " + newUserId);
        } else {
            System.out.println("\nError registering new user.");
        }
    }
    

    public boolean saveUser(User user) {
        try (PrintWriter pw = new PrintWriter(new FileOutputStream(USER_INFO_FILE, true))) {
            pw.println(user.toString());
            return true;
        } catch (FileNotFoundException e) {
            System.out.println("Error writing to user info file: " + e.getMessage());
            return false;
        }
    }
    
    //hide password 
    public String readHiddenPassword(String prompt) 
    {
        System.out.print(prompt);
        String password = "";
        try {
            if (System.console() != null) {
                char[] passwordChars = System.console().readPassword();
                password = new String(passwordChars);
            } else {
                System.out.print("\033[8m");
                password = sc.nextLine();
                System.out.print("\033[0m");
            }
        } catch (Exception e) {
            System.err.println("Error reading password: " + e.getMessage());
            password = sc.nextLine();
        }
        return password;
    }

    //password validation 
    private boolean isPasswordValid(String password) {
        if (password.length() < 8) return false;
        if (!password.matches(".*[A-Z].*")) return false;
        if (!password.matches(".*[a-z].*")) return false;
        if (!password.matches(".*[0-9].*")) return false;
        return true;
    }

    //check if username exists
    public boolean isUsernameExists(String username) {
        try (Scanner fileScanner = new Scanner(new File(USER_INFO_FILE))) {
            while (fileScanner.hasNextLine()) {
                String line = fileScanner.nextLine();
                String[] parts = line.split("\\|");
                if (parts.length > 1 && parts[1].equals(username)) {
                    return true;
                }
            }
        } catch (FileNotFoundException e) {
            return false;
        }
        return false;
    }

    //generate new user id
    private String generateNewUserId() {
        int highestNumber = 0;
        try (Scanner fileScanner = new Scanner(new File(USER_INFO_FILE))) {
            while (fileScanner.hasNextLine()) {
                String line = fileScanner.nextLine();
                String[] parts = line.split("\\|");
                if (parts.length > 0 && parts[0].startsWith("U")) {
                    try {
                        int currentNumber = Integer.parseInt(parts[0].substring(1));
                        if (currentNumber > highestNumber) {
                            highestNumber = currentNumber;
                        }
                    } catch (NumberFormatException e) {}
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("User info file not found. Starting ID from U001");
        }
        highestNumber++;
        return String.format("U%03d", highestNumber);
    }
}
