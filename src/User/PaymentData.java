package User;

public class PaymentData 
{
    private String userId;
    private double amount;
    private String paymentMethod; 
    private String paymentStatus;
    
    public PaymentData(String userId, double amount, String paymentMethod, String paymentStatus) 
    {
        this.userId = userId;
        this.amount = amount;
        this.paymentMethod = paymentMethod;
        this.paymentStatus = paymentStatus;
    }
    
    //getters
    public String getUserId() 
    {
    	return userId;
    }
    
    public double getAmount() 
    {
    	return amount;
    }
    
    public String getPaymentMethod() 
    {
    	return paymentMethod;
    }
    
    public String getPaymentStatus() 
    {
    	return paymentStatus;
    }

    
    //setter
    public void setPaymentStatus(String status) 
    { 
        this.paymentStatus = status; 
    }
    
    @ Override
    public String toString() 
    { 
        return String.format("Payment %s: RM %.2f via %-20s - %s", 
            userId, amount, paymentMethod, paymentStatus);
    }
}