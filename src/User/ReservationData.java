package User;

import java.time.LocalDate;
import java.time.Period;

import Admin.Campsite;


public class ReservationData 
{
    private final String userId;
    private final Campsite campsite;
    private final LocalDate checkIn;
    private final LocalDate checkOut;
    private final int numberOfPeople;
    private PaymentData paymentData;
    private String status;

    public ReservationData(String userId, Campsite campsite, LocalDate checkIn, LocalDate checkOut, int numberOfPeople) 
    {
        this.userId = userId;
        this.campsite = campsite;
        this.checkIn = checkIn;
        this.checkOut = checkOut;
        this.numberOfPeople = numberOfPeople;
        this.status = "PENDING";
        this.paymentData = null;
    }
    
    //getters
    public String getUserId() 
    { 
    	return userId; 
    }
    
    public Campsite getCampsite() 
    { 
    	return campsite; 
    }
    
    public LocalDate getCheckIn() 
    { 
    	return checkIn; 
    }
    
    public LocalDate getCheckOut() 
    { 
    	return checkOut; 
    }
    
    public int getNumberOfPeople() 
    { 
    	return numberOfPeople; 
    }
    
    public PaymentData getPaymentData() 
    { 
    	return paymentData; 
    }
    
    public String getStatus() 
    { 
    	return status; 
    }
    
    //setters
    public void setPaymentData(PaymentData paymentData) 
    { 
    	this.paymentData = paymentData; 
    }
    
    public void setStatus(String status) 
    { 
    	this.status = status; 
    }
    

    public double calculateTotalCost() 
    {
        Period period = Period.between(checkIn, checkOut);
        long days = period.getDays();
        return days * campsite.getPricePerNight() * numberOfPeople;
    }

    @Override
    public String toString() 
    {
        return String.format("%s (%d people) from %s to %s - Total: RM %.2f",
            campsite.getName(), numberOfPeople, checkIn, checkOut, calculateTotalCost());
    }
}