/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view_and_controller;

import static data.Appt_utility.getAllAppts;
import static data.Customer_Implementation.getAllCustomers;
import data.DBConnection;
import static data.Except_utility.apptVal;
import data.Query;
import static data.Query.getStartTimes;
import static data.TimeZone_utility.tzAdjust;
import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import model.Appointment;
import model.Customer;
import sun.font.TextLabel;

/**
 * FXML Controller class
 *
 * @author markwartman
 */
public class addAppointmentController implements Initializable {
    
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
    private TextField country_id;
    @FXML
    private TextField city_id;
    
    
    @FXML
    private TableView<Customer> custTbl_id;
    @FXML
    private TableColumn<?, ?> name_cust_col_id;
    @FXML
    private TableColumn<?, ?> address_cust_col;
    @FXML
    private TableColumn<?, ?> city_cust_col_id;
    @FXML
    private TableColumn<?, ?> phone_cust_col_id;
    
    
    @FXML
    private TableView<Appointment> apptTable;
    @FXML
    private TableColumn<?, ?> startTable;
    @FXML
    private TableColumn<?, ?> end;
    @FXML
    private TableColumn<?, ?> locationTable;
    @FXML
    private TableColumn<?, ?> user;
    
    
    @FXML
    private Label customerText;
    @FXML
    private DatePicker datePicker;
    @FXML
    private ComboBox<String> type;
    @FXML
    private ComboBox<String> length;
    @FXML
    private ComboBox<String> locationCombo;
    @FXML
    private ComboBox<String> startCombo;
    
    ObservableList<String> typeOL = FXCollections.observableArrayList("Consultation", "Inquiry", "Repair");
    ObservableList<String> hourObservableList = FXCollections.observableArrayList("09:00:00", "10:00:00", "11:00:00", "12:00:00", "13:00:00", "14:00:00", "15:00:00", "16:00:00");
    ObservableList<String> half_hourObservableList = FXCollections.observableArrayList("09:00:00","09:30:00","10:00:00","10:30:00","11:00:00","11:30:00","12:00:00","12:30:00","13:00:00","13:30:00","14:00:00","14:30:00","15:00:00","15:30:00","16:00:00","16:30:00");
    ObservableList<String> lenOL = FXCollections.observableArrayList("1:00 Hour", "30 Minutes");
    ObservableList<String> locationOL = FXCollections.observableArrayList("Phoenix", "New York", "London");
    boolean dp_bool = false;
    boolean len_bool = false;
    ResultSet rs;
    String length_Min;
    
    

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        
        custTbl_id.setItems(getAllCustomers());
        
        name_cust_col_id.setCellValueFactory(new PropertyValueFactory<>("customerName"));
        address_cust_col.setCellValueFactory(new PropertyValueFactory<>("address"));
        city_cust_col_id.setCellValueFactory(new PropertyValueFactory<>("city"));
        phone_cust_col_id.setCellValueFactory(new PropertyValueFactory<>("phone"));
        
        
        apptTable.setItems(getAllAppts("2019-01-01 01:00:00", "9019-01-01 21:00:00"));
        
        startTable.setCellValueFactory(new PropertyValueFactory<>("start"));
        end.setCellValueFactory(new PropertyValueFactory<>("end"));
        locationTable.setCellValueFactory(new PropertyValueFactory<>("location"));
        user.setCellValueFactory(new PropertyValueFactory<>("userId"));
        
        // Load types of Appointments to ComboBox
        type.setItems(typeOL);
        
        // Load start times into ComboBox
        //startCombo.setItems(hourObservableList);
        
        // Load length of appointment into ComboBox
        length.setItems(lenOL);
        
        // Load loacation to ComboBox
        locationCombo.setItems(locationOL);
    }    

    @FXML
    private void addCustSave(ActionEvent event) {
    }

    @FXML
    private void addCustCancel(ActionEvent event) {
    }



    @FXML
    private void addApptCancel(ActionEvent event) throws IOException {
        
        stage = (Stage)((Button)event.getSource()).getScene().getWindow();
        scene = FXMLLoader.load(getClass().getResource("main.fxml"));
        stage.setScene(new Scene(scene));
        stage.show();
        
    }

    @FXML
    private void addApptSave(ActionEvent event) throws IOException {
        
        
        // apptVal passes argument to Except_utility.java
        if(apptVal(custTbl_id.getSelectionModel().getSelectedItem() != null, !type.getSelectionModel().isEmpty(), !locationCombo.getSelectionModel().isEmpty(), dp_bool, !length.getSelectionModel().isEmpty(), !startCombo.getSelectionModel().isEmpty())){
            
            String sqlInsert;
            int nextId = 0;

            try {

                DBConnection.makeConnection();

                //  query max appointmentId: "SELECT max(appointmentId) as appointmentId FROM U05flP.appointment";
                sqlInsert = "SELECT max(appointmentId) as appointmentId FROM U05flP.appointment";
                Query.makeQuery(sqlInsert);
                rs = Query.getResult();
                while(rs.next())
                {
                    nextId = rs.getInt("appointmentId");
                }
                nextId++;

                sqlInsert =  "insert into appointment (appointmentId, customerId,                                                           userId,      title,     description,    location,                                   contact,            type,                           url,            start,                                                                          end,                                                                                                                        createDate, createdBy, lastUpdate, lastUpdateBy)"
                        + "values(                  "+ nextId +",  "+  custTbl_id.getSelectionModel().getSelectedItem().getCustomerId() +",  1, 'not needed',  'not needed',  '"+ locationCombo.getValue().toString() +"',  'not needed',   '"+ type.getValue().toString() +"',  'not needed',   '"+ tzAdjust(datePicker.getValue().toString() , startCombo.getValue().toString(), "toUTC") +"',   DATE_ADD('"+ tzAdjust(datePicker.getValue().toString() , startCombo.getValue().toString(), "toUTC") +"', INTERVAL "+ length_Min +" MINUTE),   now(),      'test',     now(),      'test' " + ");";

                //INSERT INTO appointment ( appointmentId, customerId,      userId,      title,     description, location,      contact,    type,    url, start, end, createDate, createdBy, lastUpdate, lastUpdateBy )
                //appointment VALUES (      1,              3,         'not needed','not needed',   'not needed','not needed',  'not needed','2019-01-10 16:00:00','2019-01-10 17:00:00','2019-01-06 16:23:08','test','2019-01-06 16:27:17','test', null, null);





                // insert record
                Query.makeQuery(sqlInsert);

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
    private void selectCustomer(ActionEvent event) {
        
        // link customer to appointment
        customerText.setText(custTbl_id.getSelectionModel().getSelectedItem().getCustomerName());
        
    }
    
    

    @FXML
    private void dp_action(ActionEvent event) throws SQLException, Exception {
        
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Sorry:");
        alert.setHeaderText(null);
        
        startCombo.getItems().clear();
        dp_bool = false;
        
        System.out.println("datePicker to-string: " + datePicker.getValue().toString());
        System.out.println("datePicker day of week: " + datePicker.getValue().getDayOfWeek());
        if(datePicker.getValue().getDayOfWeek().toString().contains("SATURDAY") || datePicker.getValue().getDayOfWeek().toString().contains("SUNDAY")){
            
            alert.setContentText("Error: Weekends cannot be scheduled\n\nPlease select Mon - Fri");
            alert.showAndWait();
        }
        else
            dp_bool = true;
        
        if(len_bool && dp_bool && datePicker.toString().length() > 0)
            startCombo.setItems(getStartTimes(length_Min, datePicker, ""));
        
        //System.out.println("Location: " + locationCombo.getValue().toString());
    }

    @FXML
    private void len_action(ActionEvent event) throws SQLException, Exception {
        
        startCombo.getItems().clear();
        
        if(length.getValue().contains("1:00 Hour")){
            length_Min = "60";
            len_bool = true;
        }
        else if(length.getValue().contains("30 Minutes")){
            length_Min = "30";
            len_bool = true;
        }
        
        
        System.out.println("len_bool: " + len_bool);
        System.out.println("length_Min : " + length_Min);
        
        if(len_bool && dp_bool)
            startCombo.setItems(getStartTimes(length_Min, datePicker, ""));
        
//        if(len_bool && dp_bool){
//            dp_bool = false;
//            len_bool = false;
//            System.out.println("Accessing while loop!");
//            System.out.println("datePicker to-string: " + datePicker.getValue().toString());
//            String sqlTimeQuery;
//            //sqlTimeQuery = "SELECT * FROM U05flP.appointment where ( date_add('2019-01-07 16:00:00', INTERVAL 1 SECOND) between start and end or date_sub('2019-01-07 17:00:00', INTERVAL 1 SECOND) between start and end);";
//            // and: != itself while modifying to avoid self conflict
//            
//            try{
//                DBConnection.makeConnection();
//            
//                Iterator<String> iter = hourObservableList.iterator();
//                if(length_Min.contains("30"))
//                    iter = half_hourObservableList.iterator(); 
//                
//                
//                
// 
//                while (iter.hasNext()) {
//                    String s = iter.next();
//
//                    System.out.println("this is s: " + s);
//                    
//                    sqlTimeQuery = "SELECT * FROM U05flP.appointment where ( "
//                    + "date_add('"+ tzAdjust(datePicker.getValue().toString(), s, "toUTC")+"', INTERVAL 1 SECOND) between start and end or "
//                    + "date_add(date_sub('"+ tzAdjust(datePicker.getValue().toString(), s, "toUTC")+"', INTERVAL 1 SECOND), INTERVAL "+ length_Min +" MINUTE) between start and end);";
//
//                    //System.out.println(sqlTimeQuery);
//                    Query.makeQuery(sqlTimeQuery);
//                    rs = Query.getResult();
//                    // measure length of arg returned
//                    // if no-length: that obj goes in the offical-hourObservableList
//
//
//
//                    while(rs.next()){
//
//                        System.out.println("start len: " + rs.getDate("start").toString());
//
//                        if( rs.getDate("start").toString().length() != 0 && rs.getDate("end").toString().length() != 0)
//                        {
//                            iter.remove();
//                        }
//                    }
//                   
//                }
//                
//                DBConnection.closeConnection();
//            } catch(Exception ex){
//                ex.printStackTrace();
//                System.out.println("Error: " + ex.getMessage());
//            }
//            
//            // Load start times into ComboBox
//            if(length_Min.contains("30"))
//                startCombo.setItems(half_hourObservableList);
//            else
//                startCombo.setItems(hourObservableList);
//        }
    }

    @FXML
    private void startAction(ActionEvent event) {
        
        //dp_bool = false;
        //len_bool = false;
    }
    
    
    
}
