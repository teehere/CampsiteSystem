package User;

public class User 
{
    private String userId;
    private String username;
    private String password;
    private String type; // "admin" or "user"

    public User(String userId, String username, String password, String type) {
        this.userId = userId;
        this.username = username;
        this.password = password;
        this.type = type;
    }

    public String getUserId() {
        return userId;
    }
    
    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getType() {
        return type;
    }

    @Override
    public String toString() 
    {
        return userId + "|" + username + "|" + password + "|" + type;
    }
}