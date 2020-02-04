/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view_and_controller;


//import static data.Customer_Implementation.getAllCustomers;
import data.DBConnection;
import static data.Except_utility.validation;
//import static data.DBConnection.conn;
import data.Query;
import static data.Query.selectCity_inThisCountry;
import static data.Query.selectCountry;
import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.Scanner;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author markwartman
 */
public class addCustomerController implements Initializable {
    
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
    @FXML
    private ComboBox<String> countrySelection;
    @FXML
    private ComboBox<String> citySelection;
    
    //ObservableList<String> countryOL = FXCollections.observableArrayList("US", "UK", "Canada", "Norway");
    //ObservableList<String> cityOL = FXCollections.observableArrayList();
    ResultSet rsMine;
    boolean isValid;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        countrySelection.setItems(selectCountry());
    }    

    @FXML
    private void addCustSave(ActionEvent event) throws IOException {
        
       
        
        String sqlInsert;
        String sqlInsert2;
        //String sqlCityInsert;
       
        // validation passes arguments to Except_utility.java
        if(validation(name_id.getText(), address_id.getText(), zipCode_id.getText(), phone_id.getText(), countrySelection.getSelectionModel().isEmpty(), citySelection.getSelectionModel().isEmpty())){
            try {
                isValid = true;
                DBConnection.makeConnection();
                // get data to insert a record
                // use this: SELECT max(addressId) FROM U05flP.address;
                // Advisor Wanda's help
                String select = "SELECT max(addressId) as addressId FROM U05flP.address";
                //PreparedStatement ps = conn.prepareStatement(select);
                //ResultSet rs = ps.executeQuery();
                int nextId = 0;

                // my Query implementation
                Query.makeQuery(select);
                //ResultSet rsMine = Query.getResult();
                rsMine = Query.getResult();

                while(rsMine.next())
                {
                    nextId = rsMine.getInt("addressId");
                }
                //int nextId = Query.getResult();
                
                nextId++;
                System.out.println("addressId going into the select statement: " + nextId);

                //customerName = name_id.getText();

                sqlInsert = "insert into customer (customerName,            addressId,      active, createDate, createdBy, lastUpdate, lastUpdateBy)"
                        + "values('" +           name_id.getText() + "', "+ nextId +",          1,      now(),      'test',     now(),      'test');";

                Query.makeQuery("SELECT cityId FROM U05flP.city WHERE CITY = '"+ citySelection.getSelectionModel().getSelectedItem() +"';");
                rsMine = Query.getResult();
                while(rsMine.next())
                    nextId = rsMine.getInt("cityId");
                System.out.println("this is cityId for ???: " + nextId);

                sqlInsert2 =  "insert into address (address,                        address2,                   cityId,         postalCode,                     phone,                  createDate, createdBy, lastUpdate, lastUpdateBy)"
                        + "values('"+               address_id.getText() +"', '" + address2_id.getText() + "', "+ nextId +", '" + zipCode_id.getText() + "', '" + phone_id.getText() + "', now(),      'test',     now(),      'test' " + ");";

                            //INSERT INTO address (address, address2, cityId, postalCode, phone, createDate, createdBy, lastUpdate, lastUpdateBy) VALUES (
                            //'1 New Main','x',1,'11111','555-1212','2019-01-06 16:16:38','test','2019-01-06 16:16:38','test');

                            //1,'123 Main',null,1,'11111','555-1212','2019-01-06 16:16:38','test','2019-01-06 16:16:38','test'
                            //3,'Houston',1,'2019-01-06 16:12:41','test','2019-01-06 16:12:41','test'
                            //                      3,         'Houston',1,         '2019-01-06 16:12:41','test','2019-01-06 16:12:41','test'

                // I DONT THINK I NEED TO INSERT INTO THE CITY OR COUNTRY TABLE; I THINK THEY CAN BE REFERENCED            
                //Query.makeQuery("SELECT countryId FROM U05flP.country WHERE country = "
                // + "'"+ countrySelection.getSelectionModel().getSelectedItem() +"'");
                //rsMine = Query.getResult();
                //while(rsMine.next())
               //     nextId = rsMine.getInt("countryId");
                //System.out.println("this is courntryId for United States: " + nextId);

                //sqlCityInsert = "insert into city (cityId,          city,                                                   countryId, createDate, createdBy, lastUpdate, lastUpdateBy )"
                //        + "values(              "+ nextId +", '"+ citySelection.getSelectionModel().getSelectedItem() +"', "+ nextId +", now(), 'test', now(), 'test' );";



                // insert records
                
                Query.makeQuery(sqlInsert2);
                Query.makeQuery(sqlInsert);
                
                //Query.makeQuery(sqlCityInsert);
            
            
                DBConnection.closeConnection();
            } catch (Exception ex) {
                ex.printStackTrace();
                System.out.println("Error: " + ex.getMessage());
            }

            if(isValid){
                stage = (Stage)((Button)event.getSource()).getScene().getWindow();
                scene = FXMLLoader.load(getClass().getResource("main.fxml"));
                stage.setScene(new Scene(scene));
                stage.show();
            }
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
    private void countryCombo(ActionEvent event) throws SQLException, Exception {
        
        
//        System.out.println(countrySelection.getSelectionModel().getSelectedItem());
//        DBConnection.makeConnection();
//        //SELECT city FROM U05flP.city WHERE countryId = (SELECT countryId FROM U05flP.country WHERE country = 'us');
//        Query.makeQuery("SELECT city FROM U05flP.city WHERE countryId = (SELECT countryId FROM U05flP.country WHERE country = "
//                + "'"+ countrySelection.getSelectionModel().getSelectedItem() +"')");
//        //cityOL
//        cityOL.clear();
//        rsMine = Query.getResult();
//        while(rsMine.next())
//          cityOL.add(rsMine.getString("city"));
//        
//        DBConnection.closeConnection();
//        
//        citySelection.setItems(cityOL);

        //  selectCity_inThisCountry
        citySelection.setItems(selectCity_inThisCountry(countrySelection.getSelectionModel().getSelectedItem()));
        
    }

    @FXML
    private void cityCombo(ActionEvent event) {
        
        //citySelection.getSelectionModel().getSelectedItem();
    }
    
}
