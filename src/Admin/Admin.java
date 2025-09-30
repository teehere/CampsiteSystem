package Admin;

import User.User;

public class Admin extends User 
{
    public Admin(String userId, String username, String password) 
    {
        super(userId, username, password, "admin");
    }

}