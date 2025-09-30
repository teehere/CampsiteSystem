package Admin;

public class Review 
{
	
    private String campsiteName;
    private String userId;
    private int rating;
    private String comment;

    public Review(String campsiteName, String userId, int rating, String comment) 
    {
        this.campsiteName = campsiteName;
        this.userId = userId;
        this.rating = rating;
        this.comment = comment;
    }

    public String getCampsiteName() 
    { 
    	return campsiteName; 
    }
    
    public String getUserId() 
    { 
    	return userId; 
    }
    
    public int getRating() 
    { 
    	return rating; 
    }
    
    public String getComment() 
    { 
    	return comment; 
    }

    @ Override
    public String toString() 
    {
        return String.format("%s rated %d: %s", userId, rating, comment);
    }
}
