/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package data;

import static data.TimeZone_utility.tzAdjust;
import java.sql.ResultSet;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.Appointment;

/**
 *
 * @author markwartman
 */
public class Appt_utility {
    
    private static ObservableList<Appointment> allAppts = FXCollections.observableArrayList();
    
    // min & max ARE DATE VALUES
    public static ObservableList<Appointment> getAllAppts(String min, String max){
        
        allAppts.clear();
        
        try
        {
            
            
            DBConnection.makeConnection();
            
            // write sql statement
            String sqlStatement = "select * FROM U05flP.appointment where start between '"+ min +"' and '"+ max +"';";
            Query.makeQuery(sqlStatement);
            
            // execute statement and create resultset object
            ResultSet result = Query.getResult();
            
            // get all records with ResultSet obj
            while(result.next())
            {
                int appointmentId = result.getInt("appointmentId");
                int customerId = result.getInt("customerId");
                int userId = result.getInt("userId");
                String title = result.getString("title");
                String description = result.getString("description");
                String location = result.getString("location");
                String contact = result.getString("contact");
                String type = result.getString("type");
                String url = result.getString("url");
                
                String start = tzAdjust(result.getString("start").substring(0, 10), result.getString("start").substring(11, 19), "toLocal");		// or String
                String end = tzAdjust(result.getString("end").substring(0, 10), result.getString("end").substring(11, 19), "toLocal");                   // or String
                String createDate = result.getString("createDate");     // Calendar Date
                String createdBy = result.getString("createdBy");
                String lastUpdate = result.getString("lastUpdate");
                String lastUpdateBy = result.getString("lastUpdateBy");
                
                Appointment appt = new Appointment( appointmentId, customerId, userId, title, description, location, contact, type, url, start, end, createDate, createdBy, lastUpdate, lastUpdateBy);
                allAppts.add(appt);
            }
            DBConnection.closeConnection();
        }
        catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("Error: " + ex.getMessage());
        }
        
        return  allAppts;
    }
    
    
    public static void deleteAppointment(Appointment appointment){
        
        try
        {
            DBConnection.makeConnection();
            
            String sqlStatement = "delete from appointment where appointmentId = '" + appointment.getAppointmentId() + "';";
            Query.makeQuery(sqlStatement);
            
            DBConnection.closeConnection();
            
        } 
        catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("Error: " + ex.getMessage());
        }
        
        allAppts.remove(appointment);
    }
    
}
