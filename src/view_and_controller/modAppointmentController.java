/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view_and_controller;

import static data.Appt_utility.getAllAppts;
import data.DBConnection;
import static data.Except_utility.modApptVal;
import data.Query;
import static data.Query.getStartTimes;
import static data.TimeZone_utility.tzAdjust;
import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
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

/**
 * FXML Controller class
 *
 * @author markwartman
 */
public class modAppointmentController implements Initializable {

    @FXML
    private TableColumn<?, ?> start_col;
    @FXML
    private TableColumn<?, ?> end_col;
    @FXML
    private TableColumn<?, ?> location_col;
    @FXML
    private TableColumn<?, ?> user_col;
    @FXML
    private ComboBox<String> lengthCombo;
    @FXML
    private ComboBox<String> startCombo;
    @FXML
    private DatePicker datePicker;
    @FXML
    private TableView<Appointment> tbl_view_appointment;
    @FXML
    private TextField curr_apt_start;
    @FXML
    private TextField curr_apt_end;
    
    ObservableList<String> start1hrOL = FXCollections.observableArrayList("09:00:00", "10:00:00", "11:00:00", "12:00:00", "13:00:00", "14:00:00", "15:00:00", "16:00:00");
    ObservableList<String> start30MinOL2 = FXCollections.observableArrayList("09:00:00","09:30:00","10:00:00","10:30:00","11:00:00","11:30:00","12:00:00","12:30:00","13:00:00","13:30:00","14:00:00","14:30:00","15:00:00","15:30:00","16:00:00","16:30:00");
    ObservableList<String> lengthApptOL = FXCollections.observableArrayList("1:00 Hour", "30 Minutes");
    boolean bool_length;
    boolean bool_date;
    String length_Min;
    ResultSet rs;
    Stage stage;
    Parent scene;
    String id;
    
    
    
    
    public void sendAppointment(Appointment appointment){

        //System.out.println("this start in modAppt: " + appointment.getStart());
        System.out.println("this is start in modAppt: " + appointment);
        
        curr_apt_start.setText(appointment.getStart());
        curr_apt_end.setText(appointment.getEnd());
        
        id = String.valueOf(appointment.getAppointmentId());
        
    }
    

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        tbl_view_appointment.setItems(getAllAppts("2019-01-01 01:00:00", "9019-01-01 21:00:00"));
        
        start_col.setCellValueFactory(new PropertyValueFactory<>("start"));
        end_col.setCellValueFactory(new PropertyValueFactory<>("end"));
        location_col.setCellValueFactory(new PropertyValueFactory<>("location"));
        user_col.setCellValueFactory(new PropertyValueFactory<>("userId"));
        
        // load start times
        lengthCombo.setItems(lengthApptOL);
    }    

    @FXML
    private void save_btn(ActionEvent event) throws IOException {
        
        if(modApptVal(bool_date, !lengthCombo.getSelectionModel().isEmpty(), !startCombo.getSelectionModel().isEmpty())){
            
            // sql update clause...
            String sqlInsert;

            try {

                // put textfield's into the update query
                // add a
                sqlInsert = "update U05flP.appointment set start = '" + tzAdjust(datePicker.getValue().toString(), startCombo.getValue(), "toUTC") +"', "
                        + "end = date_add('"+ tzAdjust(datePicker.getValue().toString(), startCombo.getValue(), "toUTC") +"', INTERVAL "+length_Min+" MINUTE)"
                        + " where appointmentId = "+ id +";";
                System.out.println("sqlInsert: " + sqlInsert);        

                DBConnection.makeConnection();

                // send update query
                Query.makeQuery(sqlInsert);

                DBConnection.closeConnection();
            } 
            catch (Exception ex) {
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
    private void cancel_btn(ActionEvent event) throws IOException {
        
        stage = (Stage)((Button)event.getSource()).getScene().getWindow();
        scene = FXMLLoader.load(getClass().getResource("main.fxml"));
        stage.setScene(new Scene(scene));
        stage.show();
        
    }

    @FXML
    private void start_combo_action(ActionEvent event) {
        
        // as exception control, make sure date is selected?
        // rather, it is likely not needed at all.
        
    }

    @FXML
    private void datePick_action(ActionEvent event) throws Exception {
        
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Sorry:");
        alert.setHeaderText(null);
        
        startCombo.getItems().clear();
        bool_date = false;
        
        System.out.println("datePicker to-string: " + datePicker.getValue().toString());
        System.out.println("datePicker day of week: " + datePicker.getValue().getDayOfWeek());
        if(datePicker.getValue().getDayOfWeek().toString().contains("SATURDAY") || datePicker.getValue().getDayOfWeek().toString().contains("SUNDAY")){
            
            alert.setContentText("Error: Weekends cannot be scheduled\n\nPlease select Mon - Fri");
            alert.showAndWait();
        }
        else
            bool_date = true;
        
        if(bool_length && bool_date && datePicker.toString().length() > 0)
            startCombo.setItems(getStartTimes(length_Min, datePicker, id));
    }
    
    @FXML
    private void length_combo_action(ActionEvent event) throws Exception {
        
        
        startCombo.getItems().clear();
        
        if(lengthCombo.getValue().contains("1:00 Hour")){
            length_Min = "60";
            bool_length = true;
        }
        else if(lengthCombo.getValue().contains("30 Minutes")){
            length_Min = "30";
            bool_length = true;
        }
        
        
        System.out.println("len_bool: " + bool_length);
        System.out.println("length_Min : " + length_Min);
        
        if(bool_length && bool_date)
            startCombo.setItems(getStartTimes(length_Min, datePicker, id));
        
        
        // the new above
        // the old below
        
//        bool_length = true;
//        
//        if(bool_length && bool_date)
//        {
//            // load start times
//            if(lengthCombo.getValue().contains("1:00 Hour"))
//                length_Min = "60";
//            else if(lengthCombo.getValue().contains("30 Minutes"))
//                length_Min = "30";
//            
//            String sqlTimeQuery;
//            
//            
//            try{
//                DBConnection.makeConnection();
//                
//                Iterator<String> iter = start1hrOL.iterator();
//                if(length_Min.contains("30"))
//                    iter = start30MinOL2.iterator(); 
//                
//                
//                
// 
//                while (iter.hasNext()) {
//                    String s = iter.next();
//
//                    System.out.println("this is s: " + s);
//                    
//                    // tack on the end:
//                    // and != ( global-Var )= appointment.getAppointmentId()
//                    sqlTimeQuery = "SELECT * FROM U05flP.appointment where ( "
//                    + "date_add('"+ tzAdjust(datePicker.getValue().toString(), s, "toUTC")+"', INTERVAL 1 SECOND) between start and end or "
//                    + "date_add(date_sub('"+ tzAdjust(datePicker.getValue().toString(), s, "toUTC")+"', INTERVAL 1 SECOND), INTERVAL "+ length_Min +" MINUTE) between start and end) and appointmentId != "+id+";";
//
//                    //System.out.println(sqlTimeQuery);
//                    Query.makeQuery(sqlTimeQuery);
//                    rs = Query.getResult();
//                    // measure length of arg returned
//                    // if no-length: that obj goes in the offical-startOL
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
//            
//            }catch(Exception ex){
//                ex.printStackTrace();
//                System.out.println("Error: " + ex.getMessage());
//            }
//            
//            // Load start times into ComboBox
//            if(length_Min.contains("30"))
//                startCombo.setItems(start30MinOL2);
//            else
//                startCombo.setItems(start1hrOL);
//            
//        }
    }
    
}
