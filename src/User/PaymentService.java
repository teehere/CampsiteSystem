package User;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


//handle all business logic related to payments
public class PaymentService 
{
    private static final String PAYMENT_FILE = MainMenu.PAYMENT_FILE;
    private List<PaymentData> payments;

    public PaymentService() 
    {
        this.payments = loadPayments();
    }
    
    //load payment data from file and make it into a list of PaymentData objects
    public List<PaymentData> loadPayments() 
    {
    	File file = new File(PAYMENT_FILE);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                System.err.println("Error creating file: " + e.getMessage());
            }
        }
    	
    	List<PaymentData> list = new ArrayList<>();
        try (Scanner fileScanner = new Scanner(new File(PAYMENT_FILE))) 
        {
            while (fileScanner.hasNextLine()) 
            {
                String line = fileScanner.nextLine();
                String[] parts = line.split("\\|");
                if (parts.length >= 4) {
                    list.add(new PaymentData(
                        parts[0],           				//userId
                        Double.parseDouble(parts[1]), 		//amount 
                        parts[2],           				//paymentMethod
                        parts[3]            				//paymentStatus
                    ));
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading payments: " + e.getMessage());
        }
        return list;
    }


    //save all payments to file
    public boolean saveAllPayments() 
    {
        try (PrintWriter writer = new PrintWriter(new FileWriter(PAYMENT_FILE))) {
            for (PaymentData p : payments) {
                writer.println(String.format("%s|%.2f|%s|%s",
                    p.getUserId(),
                    p.getAmount(),
                    p.getPaymentMethod(),
                    p.getPaymentStatus()
                ));
            }
            return true;
        } catch (IOException e) {
            System.err.println("Error saving payments: " + e.getMessage());
            return false;
        }
    }
    
    
    //retrieve payments for a specific user
    public List<PaymentData> getPaymentsForUser(String userId) 
    {
        List<PaymentData> userPayments = new ArrayList<>();
        for (PaymentData p : payments) {
            if (p.getUserId().equals(userId)) {
                userPayments.add(p);
            }
        }
        return userPayments;
    }
    

    //add new payment to the list and save the list
    public void addPayment(PaymentData payment) 
    {
        payments.add(payment);
        saveAllPayments();
    }
    
    //process a payment by changing its status to "COMPLETED" and saves it
    public boolean processPayment(PaymentData paymentToProcess) 
    {
        paymentToProcess.setPaymentStatus("COMPLETED");
        return saveAllPayments();
    }
}