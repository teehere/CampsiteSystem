package Admin;

import java.time.LocalDate;

public class ReportData 
{
    private LocalDate date;
    private String region;
    private String campsiteType;
    private String name;
    private String issue;

    public ReportData(LocalDate date, String region, String campsiteType, String name, String issue) 
    {
        this.date = date;
        this.region = region;
        this.campsiteType = campsiteType;
        this.name = name;
        this.issue = issue;
    }

    //getters
    public LocalDate getDate() 
    { 
    	return date; 
    }
    
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
    
    public String getIssue() 
    { 
    	return issue; 
    }
    

    //setter
    public void setIssue(String issue) 
    {
        this.issue = issue;
    }
}
