/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view_and_controller;

import static data.Appt_utility.deleteAppointment;
import static data.Appt_utility.getAllAppts;
import static data.Customer_Implementation.deleteCustomer;
import static data.Customer_Implementation.getAllCustomers;
import data.DBConnection;
import data.Query;
import data._Interface;
import data._Interface2;
import java.awt.Desktop;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.File;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
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
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import model.Appointment;
import model.Customer;

/**
 * FXML Controller class
 *
 * @author markwartman
 */
public class mainController implements Initializable {
    
    ObservableList<Customer> Customers = FXCollections.observableArrayList();
    
    Stage stage;
    Parent scene;
    //String fileName = "";
    File file;
    PrintWriter printWriter;
    ResultSet rs;
    String fn, q;  // fn: fileName, q: queryString *** for global use
    
    // Justification: cut's down on repetitious code
    _Interface lambda = (fileName,queryString) -> {
        try{
            file = new File(fileName);
            if(!file.exists())
                file.createNewFile();
            if(!file.exists()){
                System.out.println("File not found! \nProgram shut down \nCheck file path & try again");
                System.exit(0);
            }

            printWriter = new PrintWriter(file);
            DBConnection.makeConnection();
            Query.makeQuery(queryString);
        }
        catch(Exception ex){
        }
    };
    
    // Justification: cut's down on repetitious code
    _Interface2 nav = (thisEvent, location) -> {
        try{
            stage = (Stage)((Button)thisEvent.getSource()).getScene().getWindow();
            scene = FXMLLoader.load(getClass().getResource(location));
            stage.setScene(new Scene(scene));
            stage.show(); 

        }
        catch(Exception ex){
        }
    };

    @FXML
    private TableView<Customer> custTableView;
    @FXML
    private TableColumn<Customer, String> cust_name_id;
    @FXML
    private TableColumn<?, ?> cust_address_col;
    @FXML
    private TableColumn<?, ?> cust_city_id;
    @FXML
    private TableColumn<Customer, String> cust_phone_id;
    
    
    @FXML
    private TableView<Appointment> apptTableView;
    @FXML
    private TableColumn<Appointment, String> start;
    @FXML
    private TableColumn<Appointment, String> end;
    @FXML
    private TableColumn<Appointment, String> location;
    @FXML
    private TableColumn<Appointment, String> user;
    
    @FXML
    private ComboBox<String> monthCombo;
    @FXML
    private ComboBox<String> weekCombo;
    ObservableList<String> month = FXCollections.observableArrayList();
    ObservableList<String> week = FXCollections.observableArrayList();
    List<String> minMonth = new ArrayList<>();
    List<String> maxMonth = new ArrayList<>();
    List<String> minWeek = new ArrayList<>();
    List<String> maxWeek = new ArrayList<>();
    LocalDateTime ldt;
    

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        // populate customer table
        custTableView.setItems(getAllCustomers());
        
        cust_name_id.setCellValueFactory(new PropertyValueFactory<>("customerName"));
        cust_address_col.setCellValueFactory(new PropertyValueFactory<>("address"));
        cust_city_id.setCellValueFactory(new PropertyValueFactory<>("city"));
        cust_phone_id.setCellValueFactory(new PropertyValueFactory<>("phone"));
        
        // populate appointment table
        apptTableView.setItems(getAllAppts("2019-01-01 01:00:00", "9019-01-01 21:00:00"));
        
        start.setCellValueFactory(new PropertyValueFactory<>("start"));
        end.setCellValueFactory(new PropertyValueFactory<>("end"));
        location.setCellValueFactory(new PropertyValueFactory<>("location"));
        user.setCellValueFactory(new PropertyValueFactory<>("userId")); 
        
        // LOAD MONTH COMBO BOX
        month.clear();
        try{
            DBConnection.makeConnection();
            
            String monQuery = "SELECT concat(year(start), ' - ',  monthname(str_to_date(month(start), '%m'))) as date, weekofyear(start) as weekNum, " +
            "min(start) as min, max(start) as max " +
            "FROM U05flP.appointment " +
            "group by month(start);";
            System.out.println(monQuery);
            Query.makeQuery(monQuery);
            rs = Query.getResult();
            while(rs.next()){
                month.add(rs.getString("date"));
                System.out.println(rs.getString("date"));
                minMonth.add(rs.getString("min"));
                maxMonth.add(rs.getString("max"));
            }
            
            DBConnection.closeConnection();
        }
        catch(Exception ex){
            System.out.println("Error: here in monthCombo "+ ex.getMessage());
            ex.printStackTrace();
        }
        monthCombo.setItems(month);
        
        // LOAD WEEK COMBO BOX
        week.clear();
        try{
            DBConnection.makeConnection();
            
            Query.makeQuery("SELECT concat(year(start), ' - ',  monthname(str_to_date(month(start), '%m'))) as date, weekofyear(start) as weekNum, " +
            "min(start) as min, max(start) as max " +
            "FROM U05flP.appointment " +
            "group by week(start);");
            rs = Query.getResult();
            while(rs.next()){
                week.add(rs.getString("weekNum") + " " + rs.getString("date"));
                minWeek.add(rs.getString("min"));
                maxWeek.add(rs.getString("max"));
            }
            
            DBConnection.closeConnection();
        }
        catch(Exception ex){
            System.out.println("Error: in weekCombo: "+ ex.getMessage());
            ex.printStackTrace();
        }
        weekCombo.setItems(week);
    }    

    @FXML
    private void custAdd(ActionEvent event) throws IOException {
        
        nav.navigate(event, "addCustomer.fxml");
        
//        stage = (Stage)((Button)event.getSource()).getScene().getWindow();
//        scene = FXMLLoader.load(getClass().getResource("addCustomer.fxml"));
//        stage.setScene(new Scene(scene));
//        stage.show();    
    }

    @FXML
    private void custModify(ActionEvent event) throws IOException {
        
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("modCustomer.fxml"));
        loader.load();
        
        modCustomerController mc = loader.getController();
        mc.sendCust(custTableView.getSelectionModel().getSelectedItem());
        
        stage = (Stage)((Button)event.getSource()).getScene().getWindow();
        Parent scene = loader.getRoot();
        stage.setScene(new Scene(scene));
        stage.show();    
    }

    @FXML
    private void custDelete(ActionEvent event) {
        
        deleteCustomer(custTableView.getSelectionModel().getSelectedItem());
    }

    @FXML
    private void apptAdd(ActionEvent event) throws IOException {
        
        nav.navigate(event, "addAppointment.fxml");
        
//        stage = (Stage)((Button)event.getSource()).getScene().getWindow();
//        scene = FXMLLoader.load(getClass().getResource("addAppointment.fxml"));
//        stage.setScene(new Scene(scene));
//        stage.show();    
    }

    @FXML
    private void apptModify(ActionEvent event) throws IOException {
        
        // passing object 'a' instead of 'apptTableView.getSelectionModel().getSelectedItem()'...
        // because of Unresolveable error ???
        Appointment a = apptTableView.getSelectionModel().getSelectedItem();
        
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("modAppointment.fxml"));
        loader.load();
        
        modAppointmentController ma = loader.getController();
        ma.sendAppointment(a);
        
        stage = (Stage)((Button)event.getSource()).getScene().getWindow();
        Parent scene = loader.getRoot();
        stage.setScene(new Scene(scene));
        stage.show();    
    }

    @FXML
    private void apptDelete(ActionEvent event) {
        
        deleteAppointment(apptTableView.getSelectionModel().getSelectedItem());
    }

    @FXML
    private void mainExit(ActionEvent event) {
        
        System.exit(0);   
    }

    @FXML
    private void reportApptType(ActionEvent event) throws IOException, SQLException, Exception {
        
        // strings feed lambda use
        fn = "Appointment_Type.txt";
        q = "SELECT count(distinct type) as types, concat(year(start), ' - ',  monthname(str_to_date(month(start), '%m'))) as date FROM U05flP.appointment group by month(start);";
        lambda.ok(fn, q);

        printWriter.println("TYPES OF APPOINTMENTS PER MONTH\n\n");
        printWriter.printf("%-8s%-18s%n", "COUNT", "DATE");
        printWriter.printf("%-8s%-18s%n%n", "------", "------------");
        rs = Query.getResult();
        while(rs.next()){
            printWriter.printf("%-8s%-18s%n", rs.getString("types"), rs.getString("date"));
        }
        printWriter.close();
        DBConnection.closeConnection();
        Desktop.getDesktop().open(file);
    }

    @FXML
    private void reportApptCount(ActionEvent event) throws IOException, SQLException, Exception {
        
        // strings feed lambda use
        fn = "Appointment_Count.txt";
        q = "SELECT count(appointmentId) as appts, count(distinct type) as types, concat(year(start), ' - ',  monthname(str_to_date(month(start), '%m'))) as date FROM U05flP.appointment group by month(start);";
        lambda.ok(fn, q);

        printWriter.println("NUMBER OF APPOINTMENTS PER MONTH\n    second column is Type count\n\n");
        printWriter.printf("%-18s%-8s%-18s%n", "APPOINTMENTS", "TYPES", "DATE");
        printWriter.printf("%-18s%-8s%-18s%n%n", "------------", "------", "------------");
        rs = Query.getResult();
        while(rs.next()){
            printWriter.printf("%-18s%-8s%-18s%n", rs.getString("appts"), rs.getString("types"), rs.getString("date"));
        }
        printWriter.close();
        DBConnection.closeConnection();
        Desktop.getDesktop().open(file);
    }

    @FXML
    private void reportUserSchedule(ActionEvent event) throws IOException, SQLException, Exception {
        
        // strings feed lambda use
        fn = "user_Schedule.txt";
        q = "SELECT userId, start, end, type, location FROM U05flP.appointment order by userId;";
        lambda.ok(fn, q);

        printWriter.println("\nUSER SCHEDULE\n\n");
        printWriter.printf("%-7s%-24s%-24s%-14s%-14s%n", "Id", "START", "END", "TYPE", "LOCATION");
        printWriter.printf("%-7s%-24s%-24s%-14s%-14s%n%n", "---", "------------", "------------", "------", "------------");
        rs = Query.getResult();
        while(rs.next()){
            printWriter.printf("%-7s%-24s%-24s%-14s%-14s%n", rs.getString("userId"), rs.getString("start"), rs.getString("end"), rs.getString("type"), rs.getString("location"));
        }
        printWriter.close();
        DBConnection.closeConnection();
        Desktop.getDesktop().open(file);
    }

    @FXML
    private void reportLoginTimes(ActionEvent event) throws IOException {
        
        // "loginTimes.txt"
        file = new File("loginTimes.txt");
        if(file.exists())
            Desktop.getDesktop().open(file);
        else
            System.out.println("File is not there to open");
    }

    @FXML
    private void monthAction(ActionEvent event) {
        System.out.println("index in monthAction: " + monthCombo.getSelectionModel().getSelectedIndex());
        getAllAppts(minMonth.get(monthCombo.getSelectionModel().getSelectedIndex()), maxMonth.get(monthCombo.getSelectionModel().getSelectedIndex()));
    }

    @FXML
    private void weekAction(ActionEvent event) {
        
        getAllAppts(minWeek.get(weekCombo.getSelectionModel().getSelectedIndex()), maxWeek.get(weekCombo.getSelectionModel().getSelectedIndex()));
    }

    @FXML
    private void clearAction(ActionEvent event) {
        getAllAppts("2019-01-01 01:00:00", "9019-01-01 21:00:00");
    }
    
}
