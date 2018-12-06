package sample;

import com.jfoenix.controls.*;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.Loader;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import java.io.IOException;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.Period;
import java.util.ResourceBundle;
import javafx.event.*;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.scene.control.Label;

import javax.jws.soap.SOAPBinding;
import javax.swing.*;

public class signUpFXMLController implements Initializable {

    private user User = new user();
    private String Pass;
    dbConnect connect = new dbConnect();
    private boolean checkUpdate = false; //check whether we load exiting data

    @FXML private JFXButton btnSignUp, btnBack;
    @FXML private JFXTextField firstName,lastName,address,email,mobile,nic;
    @FXML private JFXPasswordField pw, rpw;
    @FXML private JFXDatePicker dob;
    @FXML private JFXComboBox<String> gender, bloodType;
    @FXML private Label lblPwCheck, lblValueCheck;

    @FXML private void setValues(){
        gender.getItems().addAll("Male","Female");
        bloodType.getItems().addAll("A+","A-","B+","B-","AB+","AB-","O+","O-");
    }
    @FXML private void handlePickGender(ActionEvent pickGenderEvent){
        User.gender = gender.getValue();
    }
    @FXML private void handlePickBloodType(ActionEvent pickBloodTypeEvent){
        User.bloodType = bloodType.getValue();
    }
    @FXML private void handlePickDate(ActionEvent pickDateEvent){
        User.dob = dob.getValue();
    }
    @FXML private void handleCloseButton(ActionEvent closeEvent){
        System.exit(0);
    }
    @FXML private void handleSignUpButton(ActionEvent signUpEvent){
        try{
            boolean x = validate();
            if(x == true){
                User.fname = firstName.getText();
                User.lname = lastName.getText();
                User.address = address.getText();
                User.email = email.getText();
                User.mobile = mobile.getText();
                User.nic = nic.getText();
                User.pw = pw.getText();

                if(checkUpdate == true){
                    User.gender = gender.getValue();
                    User.dob = dob.getValue();
                    User.bloodType = bloodType.getValue();
                }

                //submit to values to database
                submit2DB();

                //goto profile
                FXMLLoader loader = new FXMLLoader(getClass().getResource("profileFXML.fxml"));
                Parent homePageParent = loader.load();
                profileFXMLController profile = loader.getController();
                profile.setUID(nic.getText());
                Scene homePageScene = new Scene(homePageParent);
                Stage appStage = (Stage)((Node)signUpEvent.getSource()).getScene().getWindow();
                appStage.hide();
                appStage.setScene(homePageScene);
                appStage.show();
            }else{
                System.out.println("error");
            }}catch (Exception ex){
            ex.printStackTrace();
        }
    }
    @FXML private void handleBackButton(ActionEvent backEvent) throws IOException{
        Parent homePageParent = FXMLLoader.load(getClass().getResource("loginFXML.fxml"));
        Scene homePageScene = new Scene(homePageParent);
        Stage appStage = (Stage)((Node)backEvent.getSource()).getScene().getWindow();
        appStage.hide();
        appStage.setScene(homePageScene);
        appStage.show();
    }
    private boolean validate(){
        if(firstName.getText().isEmpty() || lastName.getText().isEmpty() || address.getText().isEmpty() || email.getText().isEmpty() || mobile.getText().isEmpty() || nic.getText().isEmpty() || pw.getText().isEmpty() || rpw.getText().isEmpty() || bloodType.getValue().isEmpty() || dob.getValue().toString().isEmpty() || gender.getValue().isEmpty()){
            lblValueCheck.setText("Empty fields available");
            return false;
        } else if(!pw.getText().equals(rpw.getText())){
            lblValueCheck.setText(null);
            lblPwCheck.setText("Password does not match");
            return false;
        } else if (User.calculateAge().getYears() < 18){
            lblValueCheck.setText("You must be at least 18 years old");
            return false;
        } else {
            lblPwCheck.setText(null);
            return true;
        }
    }
    private void submit2DB(){
        String query;
        String requestQuery = "INSERT INTO PATIENT_REQUEST(NIC,Kidney,Heart,Intestines,Pancreas,Liver,Lungs,Veins,Corneas,Bone,Skin,Cartilage,Ligaments,Heart_Valves,Tendons,Middle_Ear,Blood) values('"+User.nic+"','0','0','0','0','0','0','0','0','0','0','0','0','0','0','0','0')";
        if(checkUpdate == true && pw.getText().equals(User.pw) && rpw.getText().equals(User.pw)){
            query = "UPDATE PATIENT SET NIC='"+User.nic+"', Fname='"+User.fname+"', Lname='"+User.lname+"', Address='"+User.address+"',Email='"+User.email+"',Mobile='"+User.mobile+"',Gender='"+User.gender+"',Blood_type='"+User.bloodType+"',DOB='"+User.dob+"'";
        } else {
            query = "INSERT INTO PATIENT(NIC,Fname,Lname,Address,Email,Mobile,Gender,Blood_type,DOB,Password) values('"+User.nic+"','"+User.fname+"','"+User.lname+"','"+User.address+"','"+User.email+"','"+User.mobile+"','"+User.gender+"','"+User.bloodType+"','"+User.dob+"','"+User.pw+"')";
        }
        try{
            Statement statement = connect.connect2DB().createStatement();
            statement.execute(query);
            if(checkUpdate == true)
                JOptionPane.showMessageDialog(null,"Profile update successful");
            else {
                JOptionPane.showMessageDialog(null,"Sign up Successful");
            }
        }catch (SQLException e){
            e.printStackTrace();
        }

        if(checkUpdate != true){
            try {
                Statement requestStatement = connect.connect2DB().createStatement();
                requestStatement.execute(requestQuery);
            } catch (SQLException e){
                e.printStackTrace();
            }
        }
    }
    public void setUID(String inUID){
        setExitingValues(inUID);
    }
    private void setExitingValues(String inUID){
        String query = "SELECT * FROM PATIENT WHERE NIC = '"+inUID+"'";
        checkUpdate = true;
        try{
            Statement statement = connect.connect2DB().createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            btnSignUp.setText("Update");
            lblPwCheck.setText("Enter the exciting password to\ncontinue");
            btnBack.setVisible(false);

            while(resultSet.next()){
                nic.setText(resultSet.getString("NIC"));
                firstName.setText(resultSet.getString("Fname"));
                lastName.setText(resultSet.getString("Lname"));
                dob.setValue(resultSet.getDate("DOB").toLocalDate());
                mobile.setText(resultSet.getString("Mobile"));
                email.setText(resultSet.getString("Email"));
                address.setText(resultSet.getString("Address"));
                bloodType.setValue(resultSet.getString("Blood_type"));
                gender.setValue(resultSet.getString("Gender"));
                Pass = resultSet.getString("Password");
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setValues();
        connect.connect2DB();
    }


}
