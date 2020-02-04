/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view_and_controller;

import data.DBConnection;
import static data.Except_utility.validation;
import data.Query;
import static data.Query.selectCity_inThisCountry;
import static data.Query.selectCountry;
import java.awt.Event;
import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.Customer;

/**
 * FXML Controller class
 *
 * @author markwartman
 */

public class modCustomerController implements Initializable {
    
    Stage stage;
    Parent scene;

    @FXML
    private TextField name_id;
    @FXML
    private TextField address_id;
    @FXML
    private TextField address2_id;
    @FXML
    private TextField zipCode_id;
    @FXML
    private TextField phone_id;
    
    int realCustId = -1;
    int realAddressId = -1;
    String city;
    String country;
    @FXML
    private ComboBox<String> cityCombo;
    @FXML
    private ComboBox<String> countryCombo;
    ResultSet rs;
    //ObservableList<String> countryOL = FXCollections.observableArrayList("US", "UK", "Canada", "Norway");
    //ObservableList<String> cityOL = FXCollections.observableArrayList();
    
    
    public void sendCust(Customer customer) {
        
        //modPart_id.setText(String.valueOf(part.getId()));
        name_id.setText(customer.getCustomerName());
        address_id.setText(customer.getAddress());
        address2_id.setText(customer.getAddress2());
        zipCode_id.setText(customer.getPostalCode());
        phone_id.setText(customer.getPhone());
        
        realCustId = customer.getCustomerId();
        realAddressId = customer.getAddressId();
        city = customer.getCity();                  // get city name
        country = customer.getCountry();
        
        try {
            DBConnection.makeConnection();
            // select country from U05flP.country where countryId = (select countryId from U05flP.city where city = 'Houston');
            Query.makeQuery("select country from U05flP.country where countryId = (select countryId from U05flP.city where city = '"+ city +"');");
            rs = Query.getResult();
            while(rs.next())
                country = rs.getString("country");
            
            DBConnection.makeConnection();
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("Error: " + ex.getMessage());
        }
        
        System.out.println("From Query: country: " + country + "  city: " + city);
        
        // POSSIBLY FIRE AN ACTION EVENT TO PROPERLY TRIGGER LISTENER
        countryCombo.getSelectionModel().select(country);
        
//        try{
//            System.out.println("!! !! Here in countrySelection" + countryCombo.getSelectionModel().getSelectedItem());
//            DBConnection.makeConnection();
//            //SELECT city FROM U05flP.city WHERE countryId = (SELECT countryId FROM U05flP.country WHERE country = 'us');
//            Query.makeQuery("SELECT city FROM U05flP.city WHERE countryId = (SELECT countryId FROM U05flP.country WHERE country = "
//                    + "'"+ countryCombo.getSelectionModel().getSelectedItem() +"')");
//            //cityOL
//            cityOL.clear();
//            rs = Query.getResult();
//            while(rs.next())
//              cityOL.add(rs.getString("city"));
//
//            DBConnection.closeConnection();
//
//            cityCombo.setItems(cityOL);
//        }
//        catch(Exception ex){
//            ex.printStackTrace();
//            System.out.println("Error: " + ex.getMessage());
//        }
        
        //  selectCity_inThisCountry
        cityCombo.setItems(selectCity_inThisCountry(countryCombo.getSelectionModel().getSelectedItem()));
        cityCombo.getSelectionModel().select(city);
    }


    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        countryCombo.setItems(selectCountry());
    }    

    @FXML
    private void addCustSave(ActionEvent event) throws IOException {
        
        String sqlInsert;
        String sqlInsert2;
        
        if(validation(name_id.getText(), address_id.getText(), zipCode_id.getText(), phone_id.getText(), countryCombo.getSelectionModel().isEmpty(), cityCombo.getSelectionModel().isEmpty())){
        
            try {
            
                int cityId = -1;

                DBConnection.makeConnection();

                Query.makeQuery("SELECT cityId FROM U05flP.city WHERE CITY = '"+ cityCombo.getSelectionModel().getSelectedItem() +"';");
                rs = Query.getResult();
                while(rs.next())
                    cityId = rs.getInt("cityId");

                // put textfield's into the update query
                sqlInsert = "update customer set customerName = '" + name_id.getText() +"'"
                        + "where customerId = "+ realCustId +";";

                sqlInsert2 =  "update address set address = '" + address_id.getText() +"',"
                        + "address2 = '" + address_id.getText() +"',"
                        + "postalCode = '" + zipCode_id.getText() +"',"
                        + "phone = '" + phone_id.getText() +"',"
                        + "cityId = '"+ cityId +"'"
                        + "where addressId = "+ realAddressId + ";";



                // send update query
                Query.makeQuery(sqlInsert);
                Query.makeQuery(sqlInsert2);

                DBConnection.closeConnection();
            } catch (Exception ex) {
                ex.printStackTrace();
                System.out.println("Error: " + ex.getMessage());
            }

            stage = (Stage)((Button)event.getSource()).getScene().getWindow();
            scene = FXMLLoader.load(getClass().getResource("main.fxml"));
            stage.setScene(new Scene(scene));
            stage.show();
        }
        
    }

    @FXML
    private void addCustCancel(ActionEvent event) throws IOException {
        
        stage = (Stage)((Button)event.getSource()).getScene().getWindow();
        scene = FXMLLoader.load(getClass().getResource("main.fxml"));
        stage.setScene(new Scene(scene));
        stage.show();
        
    }

    @FXML
    private void citySelection(ActionEvent event) {
    }

    @FXML
    private void countrySelection(ActionEvent event) throws SQLException, Exception {
        
//        System.out.println("!! !! Here in countrySelection" + countryCombo.getSelectionModel().getSelectedItem());
//        DBConnection.makeConnection();
//        //SELECT city FROM U05flP.city WHERE countryId = (SELECT countryId FROM U05flP.country WHERE country = 'us');
//        Query.makeQuery("SELECT city FROM U05flP.city WHERE countryId = (SELECT countryId FROM U05flP.country WHERE country = "
//                + "'"+ countryCombo.getSelectionModel().getSelectedItem() +"')");
//        //cityOL
//        cityOL.clear();
//        rs = Query.getResult();
//        while(rs.next())
//          cityOL.add(rs.getString("city"));
//        
//        DBConnection.closeConnection();
//        
//        cityCombo.setItems(cityOL);

        //  selectCity_inThisCountry
        cityCombo.setItems(selectCity_inThisCountry(countryCombo.getSelectionModel().getSelectedItem()));
        
    }
    
}
