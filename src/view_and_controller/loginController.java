/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view_and_controller;

import data.DBConnection;
import data.Query;
import static data.TimeZone_utility.tzAdjust;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.sql.ResultSet;
import java.time.LocalDateTime;
import java.util.Locale;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author markwartman
 */
public class loginController implements Initializable {

    @FXML
    private TextField userName_tf;
    @FXML
    private PasswordField passW_tf;
    
    @FXML
    private Button login_button;
    @FXML
    private Label label;
    
    
    Stage stage;
    Parent scene;
    ResultSet rsMine;
    boolean isValid = false;
    boolean isEnglish = true;
    boolean hasAppt = false;
    String time, custId, type;
    LocalDateTime ldt;
    

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        // test Spanish
        //Locale.setDefault(new Locale("es"));
        Locale thisLocale = Locale.getDefault();
        
        if( !thisLocale.getLanguage().contains("en") ){
            isEnglish = false;
        
            // username = nombre de usuario
            // password = contraseña
            // login = Iniciar sesión
            userName_tf.setPromptText("nombre de usuario");
            passW_tf.setPromptText("contraseña");
            login_button.setText("Iniciar sesión");
        }
        
        
        
    }    

    @FXML
    private void login_button_action(ActionEvent event) throws IOException {
        
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Validate Fields: All fields must be filled out");
        alert.setHeaderText(null);
        
        try {
            
            DBConnection.makeConnection();
            
            String select = "SELECT * FROM U05flP.user where userName = '" + userName_tf.getText() +"' "
                    + "and password = '"+ passW_tf.getText() +"';";
            
            Query.makeQuery(select);

            rsMine = Query.getResult();
            
            while(rsMine.next())
            {
                if(rsMine.getString("userName").length() > 0 && rsMine.getString("password").length() > 0)
                {
                    isValid = true;
                }
            }
            
            DBConnection.closeConnection();
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("Error: " + ex.getMessage());
            alert.setContentText("Error: NumberFormatException\n Fill in all fields\n" + ex.getMessage());
            alert.showAndWait();
            
        }
        
        if(isValid){
            
            // navigation use to take place here...
            
            try {
            
            DBConnection.makeConnection();
            
            ldt = LocalDateTime.now();
            
            // file writer obj: Appends to file
            FileWriter fwriter = new FileWriter("loginTimes.txt", true);
            // create open file
            PrintWriter outputFile = new PrintWriter(fwriter);
            // log a timestamp to file
            outputFile.println(ldt.toString());
            // close file
            outputFile.close();
                                                                                                                                                                                            // ldt.toString()
            String select = "SELECT * FROM U05flP.appointment where start between '"+ tzAdjust(ldt.toString().substring(0, 10), ldt.toString().substring(11, 19), "toUTC") +"' and date_add('"+ tzAdjust(ldt.toString().substring(0, 10), ldt.toString().substring(11, 19), "toUTC") +"', INTERVAL 15 MINUTE);";
            
            Query.makeQuery(select);

            rsMine = Query.getResult();
            
            // what if there were mulitiple appointments?
            while(rsMine.next())
            {
                if(rsMine.getString("start").length() > 0)
                {
                    hasAppt = true;
                    time = rsMine.getString("start");
                    type = rsMine.getString("type");
                    custId = rsMine.getString("customerId");
                }
            }
            
            DBConnection.closeConnection();
            
            
            
            } catch (Exception ex) {
                ex.printStackTrace();
                System.out.println("Error: " + ex.getMessage());

            }
            
            // appt in en
            if(hasAppt && isEnglish){
                alert.setTitle("");
                alert.setContentText("Alert:\nAppointment at: " + time + "\nType: " + type + "\nWith a customer Id: " + custId);
                alert.showAndWait();
            }
            
            // appt in es
            if(hasAppt && !isEnglish){
                alert.setTitle("");
                alert.setContentText("Alerta:\nCita a: " + time + "\nTipo: " + type + "\nCon una identificación del cliente: " + custId);
                alert.showAndWait();
            }
            
            stage = (Stage)((Button)event.getSource()).getScene().getWindow();
            scene = FXMLLoader.load(getClass().getResource("main.fxml"));
            stage.setScene(new Scene(scene));
            stage.show();
            
        }
        else{
            
            if(isEnglish){
                alert.setContentText("Error: The username and password did not match.\n");
                alert.showAndWait();
            }
            else
            {
                // if Not english: Spanish
                alert.setContentText("Error: El nombre de usuario y la contraseña no coinciden.\n");
                alert.showAndWait();
            }
            
            
        }
        
    }
    
}
