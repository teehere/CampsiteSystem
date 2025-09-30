package Admin;

import java.util.*;

public interface Filterable<T> 
{
    //method for an interactive filtering process
    List<T> filterInteractive();

    //method to display a list of filtered results
    void displayFiltered(List<T> filtered);

    //method to retrieve all available items
    List<T> getAllItems();
}
