/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package data;

//import static mw_scheduler_fpc195.DBConnection.conn;
import java.sql.ResultSet;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.Customer;

/**
 *
 * @author markwartman
 */
public class Customer_Implementation {
    
    private static ObservableList<Customer> allCustomers = FXCollections.observableArrayList();
    
    public static ObservableList<Customer> getAllCustomers(){
        
        /**
         * construct Customer (customerName, addressId, active, createDate, createdBy, lastUpdate, lastUpdateBy)
         *   values(            'fullNameHere', 1,          1,      now(),      'test',     now(),      'test');
         */
        
        
        allCustomers.clear();
        
        try {
            
            DBConnection.makeConnection();
            
            // write sql statement
            //"SELECT * FROM customer"
            //String sqlStatement = "select * from customer inner join address where customer.addressId = address.addressId;";
            String sqlStatement = "select * from U05flP.customer inner join U05flP.address on customer.addressId = address.addressId inner join U05flP.city on address.cityId = city.cityId inner join U05flP.country on city.countryId = country.countryId;";
            Query.makeQuery(sqlStatement);
            
            // execute statement and create resultset object
            ResultSet result = Query.getResult();
            
            // get all records with ResultSet obj
            while(result.next())
            {
                int customerId = result.getInt("customerId" );
                String customerName = result.getString("customerName");
                int addressId = result.getInt("addressId");
                boolean active = result.getBoolean("active");
                String createDate = result.getString("createDate");
                String createdBy = result.getString("createdBy");
                String lastUpdate = result.getString("lastUpdate");
                String lastUpdateBy = result.getString("lastUpdateBy");
                
                String address = result.getString("address");
                String address2 = result.getString("address2");
                String postalCode = result.getString("postalCode");
                String phone = result.getString("phone");
                
                String country = result.getString("country");
                String city = result.getString("city");
                
                //sql construct =  (customerName, addressId, active, createDate, createdBy, lastUpdate, lastUpdateBy)
                //    + "values('" +           "customerName" + "', 1,          1,      now(),      'test',     now(),      'test')";
                
                //Customer data construct =  (customerId, customerName, addressId, active, createDate, createdBy, lastUpdate, lastUpdateBy)
                //    + "values('" +           "customerName" + "', 1,          1,      now(),      'test',     now(),      'test')";
                
                Customer customer = new Customer(customerId, customerName, addressId, active, createDate, createdBy, lastUpdate, lastUpdateBy, address, address2, postalCode, phone, country, city);
                allCustomers.add(customer);
                
            }
            
            
            
            DBConnection.closeConnection();
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("Error: " + ex.getMessage());
        }
        
        //return null;
        return allCustomers;
    }
    
    public static void deleteCustomer(Customer customer){
        
        try
        {
            DBConnection.makeConnection();
            
            String sqlStatement = "delete from customer where customerId = '" + customer.getCustomerId() + "';";
            Query.makeQuery(sqlStatement);
            
            DBConnection.closeConnection();
            
        } 
        catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("Error: " + ex.getMessage());
        }
        
        allCustomers.remove(customer);
    }
    
    
    
}
