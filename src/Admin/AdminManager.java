package Admin;

import User.Menu;
import User.AuthService;
import java.util.Scanner;


//handle the user interface flow for admin tasks
//display menus and business logic to other service classes
public class AdminManager extends Menu 
{
    private AdminService adminService;
    private AuthService authService;

    public AdminManager(Scanner sc, AdminService adminService) 
    {
        super("ADMIN MANAGER", sc);
        this.sc = sc;
        this.authService = new AuthService(sc);
        this.adminService = new AdminService(this.authService);
    }

    @Override
    public void show() 
    {
        //this menu is part of the AdminPortal, so the show method is not used directly.
    }

    //handle new admin acc register
    public void registerNewAdmin() 
    {
        setTitle("REGISTER NEW ADMIN");
        displayHeader();
        System.out.println("<Q> to quit at any time");
        System.out.print("Enter new Username: ");
        String newUsername = sc.nextLine();

        if (newUsername.equalsIgnoreCase("Q")) 
        {
            System.out.println("Registration cancelled");
            pause();
            return;
        }

        if (authService.isUsernameExists(newUsername)) 
        {
            System.out.println("Username already exists. Please choose a different one.");
            pause();
            return;
        }

        String newPassword;
        String confirmPassword;

        while (true) 
        {
            newPassword = authService.readHiddenPassword("Enter new Password: ");
            if (newPassword.equalsIgnoreCase("Q")) 
            {
                System.out.println("Registration cancelled");
                pause();
                return;
            }

            if (!adminService.isPasswordValid(newPassword)) 
            {
                System.out.println("\nPassword must be at least 8 characters long and contain " +
                                   "at least one uppercase letter, one lowercase letter, and one number.");
                pause();
                continue;
            }

            confirmPassword = authService.readHiddenPassword("Confirm new Password: ");
            if (confirmPassword.equalsIgnoreCase("Q")) 
            {
                System.out.println("Registration cancelled");
                pause();
                return;
            }

            if (newPassword.equals(confirmPassword)) 
            {
                break;
            } else 
            {
                System.out.println("\nPasswords Do Not Match. Please Try Again");
                pause();
            }
        }

        //send the registration and saving logic to the new AdminService class
        adminService.registerNewAdmin(newUsername, newPassword);
        pause();
    }
    
    private void pause() 
    {
        System.out.println("Press Enter to continue...");
        sc.nextLine();
    }
}
