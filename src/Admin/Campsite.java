package Admin;

public class Campsite 
{
    private String region; 
    private String campsiteType;
    private String name;
    private double pricePerNight;
    private int capacity;

    public Campsite(String region, String campsiteType, String name, double price, int capacity) 
    { 
        this.region = region; 
        this.campsiteType = campsiteType;
        this.name = name;
        this.pricePerNight = price;
        this.capacity = capacity;
    }

    //getters
    public String getRegion() 
    {
    	return region;
    }
    
    public String getCampsiteType() 
    {
    	return campsiteType;
    }
    
    public String getName() 
    { 
    	return name;
    }
    
    public double getPricePerNight() 
    { 
    	return pricePerNight;
    }
    
    public int getCapacity() 
    { 
    	return capacity;
    }
    
    //setters
    public void setRegion(String region) 
    {
        this.region = region;
    }

    public void setCampsiteType(String campsiteType) 
    {
        this.campsiteType = campsiteType;
    }

    public void setName(String name) 
    {
        this.name = name;
    }

    public void setPricePerNight(double pricePerNight) 
    {
        this.pricePerNight = pricePerNight;
    }

    public void setCapacity(int capacity) 
    {
        this.capacity = capacity;
    } 
    
    @ Override 
    public String toString() 
    {
        return String.format("%-25s | %-8s | RM %7.2f/night | Max %d", name, campsiteType, pricePerNight, capacity);
    }
}
