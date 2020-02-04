/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package data;

import static data.DBConnection.conn;
import static data.TimeZone_utility.tzAdjust;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Iterator;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.DatePicker;

/**
 *
 * @author markwartman
 */
public class Query {
    
    private static String query;
    private static Statement stmt;
    private static ResultSet result;
    private static ObservableList<String> country = FXCollections.observableArrayList();
    private static ObservableList<String> city = FXCollections.observableArrayList();
    private static ResultSet rs;
    
    public static void makeQuery(String q){
        query = q;
        try{
            stmt = conn.createStatement();      // databaseName, DB_URL, username, password, driver
            // determine query execution
            if(query.toLowerCase().startsWith("select"))
                result = stmt.executeQuery(q);
             if(query.toLowerCase().startsWith("delete")|| query.toLowerCase().startsWith("insert")|| query.toLowerCase().startsWith("update"))
                stmt.executeUpdate(q);
            
        }
        catch(Exception ex){
            System.out.println("Error: "+ ex.getMessage());
        }
    }
    public static ResultSet getResult(){
        return result;
    }
    
    public static ObservableList<String> selectCountry(){
        
        country.clear();
        
        try{
            DBConnection.makeConnection();
            
            Query.makeQuery("select country from U05flP.country");
            rs = Query.getResult();
            while(rs.next())
                country.add(rs.getString("country"));
            
            DBConnection.closeConnection();
        }
        catch(Exception ex){
            System.out.println("Error: "+ ex.getMessage());
        }
        
        return country;
    }
    
    public static ObservableList<String> selectCity_inThisCountry(String referenceCountry){
        
        city.clear();
        
        try{
            DBConnection.makeConnection();
            
            Query.makeQuery("SELECT city FROM U05flP.city WHERE countryId = (SELECT countryId FROM U05flP.country WHERE country = "
                    + "'"+ referenceCountry +"')");
            rs = Query.getResult();
            while(rs.next())
                city.add(rs.getString("city"));
            
            DBConnection.closeConnection();
        }
        catch(Exception ex){
            System.out.println("Error: "+ ex.getMessage());
        }
        
        return city;
    }
    
    private static ObservableList<String> hourObservableList = FXCollections.observableArrayList("09:00:00", "10:00:00", "11:00:00", "12:00:00", "13:00:00", "14:00:00", "15:00:00", "16:00:00");
    private static ObservableList<String> half_hourObservableList = FXCollections.observableArrayList("09:00:00","09:30:00","10:00:00","10:30:00","11:00:00","11:30:00","12:00:00","12:30:00","13:00:00","13:30:00","14:00:00","14:30:00","15:00:00","15:30:00","16:00:00","16:30:00");
    
    /**
     * if passed an 'id' with length, it is from modAppointment and uses 'id' for the query
     * if padded an 'id' with no-length, it is from addAppointment and does not use an 'id' for it's query
     */
    public static ObservableList<String> getStartTimes(String length_Min, DatePicker datePicker, String id) throws SQLException, Exception{
        
        
        System.out.println("Accessing while loop!");
        System.out.println("datePicker to-string: " + datePicker.getValue().toString());
        String sqlTimeQuery;
        //sqlTimeQuery = "SELECT * FROM U05flP.appointment where ( date_add('2019-01-07 16:00:00', INTERVAL 1 SECOND) between start and end or date_sub('2019-01-07 17:00:00', INTERVAL 1 SECOND) between start and end);";
        // and: != itself while modifying to avoid self conflict

//        try{
//            DBConnection.makeConnection();
//
////            Iterator<String> iter = hourObservableList.iterator();
////            if(length_Min.contains("30"))
////                iter = half_hourObservableList.iterator(); 
//            Iterator<String> iter = getStartSet(length_Min).iterator();
//            
//
//
//
//            while (iter.hasNext()) {
//                String s = iter.next();
//
//                System.out.println("this is s: " + s);
//
//                sqlTimeQuery = "SELECT * FROM U05flP.appointment where ( "
//                + "date_add('"+ tzAdjust(datePicker.getValue().toString(), s, "toUTC")+"', INTERVAL 1 SECOND) between start and end or "
//                + "date_add(date_sub('"+ tzAdjust(datePicker.getValue().toString(), s, "toUTC")+"', INTERVAL 1 SECOND), INTERVAL "+ length_Min +" MINUTE) between start and end);";
//
//                //System.out.println(sqlTimeQuery);
//                Query.makeQuery(sqlTimeQuery);
//                rs = Query.getResult();
//                // measure length of arg returned
//                // if no-length: that obj goes in the offical-hourObservableList
//
//
//
//                while(rs.next()){
//
//                    System.out.println("start len: " + rs.getDate("start").toString().length());
//
//                    if( rs.getDate("start").toString().length() != 0 && rs.getDate("end").toString().length() != 0)
//                    {
//                        iter.remove();
//                    }
//                }
//
//            }
//
//            DBConnection.closeConnection();
//        } catch(Exception ex){
//            ex.printStackTrace();
//            System.out.println("Error: " + ex.getMessage());
//        }
//        
//        // Return appropriate ObservableList
//        if(length_Min.contains("30"))
//            return half_hourObservableList;
//        else
//            return hourObservableList;
            
        
                ObservableList<String> returnObservableList = FXCollections.observableArrayList();
                
                
                    
                
                DBConnection.makeConnection();
        
                for(String s : getStartSet(length_Min)){

                    System.out.println("this is s: in for-loop: " + s);

                    
                    
                    if(id.length() > 0){
                        // from modAppointment: id needed
                        sqlTimeQuery = "SELECT * FROM U05flP.appointment where ( "
                        + "date_add('"+ tzAdjust(datePicker.getValue().toString(), s, "toUTC")+"', INTERVAL 1 SECOND) between start and end or "
                        + "date_add(date_sub('"+ tzAdjust(datePicker.getValue().toString(), s, "toUTC")+"', INTERVAL 1 SECOND), INTERVAL "+ length_Min +" MINUTE) between start and end) and appointmentId != "+id+";";
                    }
                    else{
                        // from addAppointment:   NO id needed
                        sqlTimeQuery = "SELECT * FROM U05flP.appointment where ( "
                        + "date_add('"+ tzAdjust(datePicker.getValue().toString(), s, "toUTC")+"', INTERVAL 1 SECOND) between start and end or "
                        + "date_add(date_sub('"+ tzAdjust(datePicker.getValue().toString(), s, "toUTC")+"', INTERVAL 1 SECOND), INTERVAL "+ length_Min +" MINUTE) between start and end);";
                    }
                    
                    Query.makeQuery(sqlTimeQuery);
                    rs = Query.getResult();
                    // measure length of arg returned
                    // if no-length: that obj goes in the offical-startOL



                    //while(rs.next()){

                        //System.out.println("start len: " + rs.getDate("start").toString().length());
                        System.out.println("this is s: in inner-while-loop: " + s);

                        if( !rs.next() )
                            returnObservableList.add(s);
                            
                    //}
                        
                }
                DBConnection.closeConnection();

                return returnObservableList;
    }
    
    public static ObservableList<String> getStartSet(String times){
        
        if(times.contains("30"))
            return half_hourObservableList;
        else
            return hourObservableList;
    
    }
}
