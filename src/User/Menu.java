package User;

import java.util.Scanner;

//super class for all ui menu
public abstract class Menu 
{  
	 protected Scanner sc; 
	 protected String title; 
	 protected int length = 130; 


 public Menu(String title, Scanner sc) 
 {
     this.title = title;
     this.sc = sc;
 }



 public void setTitle(String title)
 {
	 this.title = title;
 }
 

 public String createLine(int length, char ch) 
 {
     return new String(new char[length]).replace('\0', ch);
 }

 //clear screen contents
 public void clear() 
 {
     System.out.print("\033[H\033[2J");
     System.out.flush();
 }

 //display header
 public void displayHeader() 
 {
	 System.out.println(createLine(length, '='));
     System.out.println(String.format("%" + (length/2 + title.length()/2) + "s", title)); 
     System.out.println(createLine(length, '='));
 } 

 public abstract void show();
}
