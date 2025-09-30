package User;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

//handle all generic file-reading operations
public class FileService 
{

    public List<String> readFileByLine(String filename) 
    {
        List<String> lines = new ArrayList<>(); 
        try (Scanner fileScanner = new Scanner(new File(filename))) {
            while (fileScanner.hasNextLine()) {
                lines.add(fileScanner.nextLine());
            }
        } 
        catch (IOException e) 
        {
            System.err.println("Error reading file: " + filename + " | " + e.getMessage());
        }
        return lines;
    }
}