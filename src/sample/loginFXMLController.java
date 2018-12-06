package sample;

import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.event.*;
import javafx.scene.control.Label;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javax.swing.*;
import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ResourceBundle;

public class loginFXMLController implements Initializable {

    private user User = new user();
    dbConnect connect = new dbConnect();

    @FXML private JFXTextField uName;
    @FXML private JFXPasswordField pw;
    @FXML private Label lblUnameCheck, lblPwCheck;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        connect.connect2DB();
    }

    @FXML
    private void  handleSignUpButton(ActionEvent signUpEvent) throws IOException{
        Parent homePageParent = FXMLLoader.load(getClass().getResource("signUpFXML.fxml"));
        Scene homePageScene = new Scene(homePageParent);
        Stage appStage = (Stage)((Node)signUpEvent.getSource()).getScene().getWindow();
        appStage.hide();
        appStage.setScene(homePageScene);
        appStage.show();
    }

    @FXML
    private void handleLoginButton(ActionEvent loginEvent){
        try {
            if(validate() == true){
                FXMLLoader loader = new FXMLLoader(getClass().getResource("profileFXML.fxml"));
                Parent parent = loader.load();
                profileFXMLController profile = loader.getController();
                profile.setUID(uName.getText());
                Scene scene = new Scene(parent);
                Stage appStage = (Stage)((Node)loginEvent.getSource()).getScene().getWindow();
                appStage.hide();
                appStage.setScene(scene);
                appStage.show();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private boolean validate(){
        getResult(uName.getText());
        if(uName.getText().isEmpty()){
            lblUnameCheck.setText("User ID field is empty");
            lblPwCheck.setText(null);
            return false;
        } else if(!User.verifyUname(uName.getText())){
            lblUnameCheck.setText("Incorrect User ID");
            lblPwCheck.setText(null);
            return false;
        } else if (pw.getText().isEmpty()){
            lblPwCheck.setText("Password field is empty");
            lblUnameCheck.setText(null);
            return false;
        } else if(!User.auth(uName.getText(),pw.getText())){
            lblPwCheck.setText("Incorrect password");
            lblUnameCheck.setText(null);
            return false;
        } else return true;
    }

    private void getResult(String inUname){
        String query = "SELECT * FROM PATIENT WHERE NIC = '"+inUname+"'";
        try{
            Statement statement = connect.connect2DB().createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            while (resultSet.next()){
                User.nic = resultSet.getString("NIC");
                User.pw = resultSet.getString("Password");
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    @FXML
    private void handleCloseButton(ActionEvent closeEvent){
        System.exit(0);
    }



}
