/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package data;

import javafx.scene.control.Alert;

/**
 *
 * @author markwartman
 */
public class Except_utility {
    
    public static boolean validation(String name, String address, String zip, String phone, boolean country, boolean city){
        
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Validate Fields: All fields must be filled out");
        alert.setHeaderText(null);
        int errors = 6;
        
        try
        {
            String nums = "[0-9]+";
            String numPn = "^(\\+\\d{1,2}\\s)?\\(?\\d{3}\\)?[\\s.-]\\d{3}[\\s.-]\\d{4}$";
            if(name != null && name.length() > 0 && !name.matches(nums))
            errors--;
            else
            {
                alert.setContentText("Error in Name field\n Can not be only numbers\n\n Please enter letters in the Name field");
                alert.showAndWait();
            }

            if(address != null && address.length() > 0)
            errors--;
            else
            {
                alert.setContentText("Error in 1st-Address field:\n\n Please enter an address");
                alert.showAndWait();
            }

            if(zip != null && zip.length() == 5 && zip.matches(nums))
            errors--;
            else
            {
                alert.setContentText("Error in Zip Code field:\n\n Must be 5 numbers\n\n Must be filled out");
                alert.showAndWait();
            }
            
            if(phone != null && phone.length() >= 10 && phone.matches(numPn))
            errors--;
            else
            {
                alert.setContentText("Error in Phone field:\n\n Must be 7 numbers\n\n Must be filled out");
                alert.showAndWait();
            }
            
            if(!country)
            errors--;
            else
            {
                alert.setContentText("Error: Please select a Country");
                alert.showAndWait();
            }
            
            if(!city)
            errors--;
            else
            {
                alert.setContentText("Error: Please select a City");
                alert.showAndWait();
            }
           
        }
        catch(NumberFormatException E)
        {
            alert.setContentText("Error: NumberFormatException\n Fill in all fields\n" + E.getMessage());
            alert.showAndWait();
        }
    
        if(errors == 0)
            return true;
        else
            return false;
    }
    
    public static boolean apptVal(boolean cust, boolean type, boolean location, boolean date, boolean length, boolean start){
        
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Validate Fields: All fields must be filled out");
        alert.setHeaderText(null);
        int errors = 6;
        String errorOutput = "";
        

        
        try{
            if(cust)
            errors--;
            else
                errorOutput += "Error: Select a customer from the:\nupper-right table";
            
            if(type)
            errors--;
            else
                errorOutput += "\n\nError: Type field must be selected";
            
            if(location)
            errors--;
            else
                errorOutput += "\n\nError: Location field must be selected";
            
            if(date)
            errors--;
            else
                errorOutput += "\n\nError: Date field must be selected";
            
            if(length)
            errors--;
            else
                errorOutput += "\n\nError: Length field must be selected";
            
            if(start)
            errors--;
            else
                errorOutput += "\n\nError: Start field must be selected";
            
            if(errorOutput.length() > 0){
                alert.setContentText(errorOutput);
                alert.showAndWait();
            }
        }
        catch(NumberFormatException E)
        {
            alert.setContentText("Error: NumberFormatException\n Fill in all fields\n" + E.getMessage());
            alert.showAndWait();
        }
        
        return (errors == 0);
    }
    
    public static boolean modApptVal(boolean date, boolean length, boolean start){
        
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Validate Fields: All fields must be filled out");
        alert.setHeaderText(null);
        int errors = 3;
        String errorOutput = "";
     
        
        try{
            
            if(date)
            errors--;
            else
                errorOutput += "\n\nError: Date field must be selected";
            
            if(length)
            errors--;
            else
                errorOutput += "\n\nError: Length field must be selected";
            
            if(start)
            errors--;
            else
                errorOutput += "\n\nError: Start field must be selected";
            
            if(errorOutput.length() > 0){
                alert.setContentText(errorOutput);
                alert.showAndWait();
            }
        }
        catch(NumberFormatException E)
        {
            alert.setContentText("Error: NumberFormatException\n Fill in all fields\n" + E.getMessage());
            alert.showAndWait();
        }
        
        return (errors == 0);
    }
    
}
