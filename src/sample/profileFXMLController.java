package sample;

import com.jfoenix.controls.*;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.StringStack;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.scene.control.Label;

import javax.swing.*;
import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.Period;
import java.util.ResourceBundle;
import java.lang.Boolean;

public class profileFXMLController implements Initializable {

    dbConnect connect = new dbConnect();
    String formMainGroup;
    protected user User = new user();
    private boolean checkUpdate;

    @FXML protected JFXRadioButton rBtnOrgan, rBtnBlood, rBtnTissue;
    @FXML protected JFXTextField txtRequireLocation;
    @FXML protected JFXDatePicker requireDate;
    @FXML protected JFXComboBox<String> subGroup;
    @FXML protected AnchorPane profilePane, donatePane, settingsPane, requestPane, requestFormPane, requestPaneResult;
    @FXML protected JFXButton btnProfile, btnDonate, btnRequest, btnSettings, btnSubmit;
    @FXML Label lblName, lblAge, lblBloodType, lblMobile;
    @FXML protected JFXCheckBox chkKidney, chkHeart, chkIntestines, chkPancreas, chkLiver, chkLungs, chkVeins, chkSkin, chkHeartValves, chkCorneas, chkCartilage, chkTendons, chkBone, chkLigaments, chkMiddleEar, chkBlood;

    //Initialize methods
    @Override public void initialize(URL location, ResourceBundle resources) {
        profilePane.setVisible(true);
        donatePane.setVisible(false);
        requestPane.setVisible(false);
        settingsPane.setVisible(false);
        requestFormPane.setVisible(false);
        connect.connect2DB();
    }
    @FXML private void handlePaneButton(ActionEvent paneEvent){
        if(paneEvent.getSource() == btnProfile){
            profilePane.setVisible(true);
            donatePane.setVisible(false);
            requestPane.setVisible(false);
            settingsPane.setVisible(false);
        } else if(paneEvent.getSource() == btnDonate){
            donatePane.setVisible(true);
            profilePane.setVisible(false);
            requestPane.setVisible(false);
            settingsPane.setVisible(false);
        } else if(paneEvent.getSource() == btnRequest){
            requestPane.setVisible(true);
            requestPaneResult.setVisible(true);
            requestFormPane.setVisible(false);
            profilePane.setVisible(false);
            donatePane.setVisible(false);
            settingsPane.setVisible(false);
        } else if(paneEvent.getSource() == btnSettings){
            settingsPane.setVisible(true);
            profilePane.setVisible(false);
            donatePane.setVisible(false);
            requestPane.setVisible(false);
            getExistingDonateDetails(User.nic);
            setExistingDonateDetails();
        }
    }
    public void setUID(String inUID){
        getProfileDetails(inUID);
    }

    //Profile Panel
    @FXML private void handleCloseButton(ActionEvent closeEvent){
        System.exit(0);
    }
    @FXML private void  handleLogOutButton(ActionEvent logOutEvent) throws IOException {
        Parent homePageParent = FXMLLoader.load(getClass().getResource("loginFXML.fxml"));
        Scene scene = new Scene(homePageParent);
        Stage appStage = (Stage)((Node)logOutEvent.getSource()).getScene().getWindow();
        appStage.hide();
        appStage.setScene(scene);
        appStage.show();
        System.out.println("Logout successfully from "+User.fname+" "+User.lname+"'s account");
    }
    @FXML private void handleEditProfileButton(ActionEvent editProfileEvent) throws IOException{
        FXMLLoader loader = new FXMLLoader(getClass().getResource("signUpFXML.fxml"));
        Parent homePageParent = loader.load();
        signUpFXMLController profile = loader.getController();
        profile.setUID(User.nic);
        Scene homePageScene = new Scene(homePageParent);
        Stage appStage = (Stage)((Node)editProfileEvent.getSource()).getScene().getWindow();
        appStage.hide();
        appStage.setScene(homePageScene);
        appStage.show();
    }
    private void getProfileDetails(String inUID){
        String query = "SELECT * FROM PATIENT WHERE NIC = '"+inUID+"'";
        try{
            Statement statement = connect.connect2DB().createStatement();
            ResultSet resultSet = statement.executeQuery(query);

            while(resultSet.next()){
                User.nic = resultSet.getString("NIC");
                User.fname = resultSet.getString("Fname");
                User.lname = resultSet.getString("Lname");
                User.dob = resultSet.getDate("DOB").toLocalDate();
                User.mobile = resultSet.getString("Mobile");
                User.email = resultSet.getString("Email");
                User.address = resultSet.getString("Address");
                User.bloodType = resultSet.getString("Blood_type");

                if(resultSet.getString("Gender")=="M")
                    User.gender = "Male";
                else User.gender = "Female";

                //set values to the profile
                setProfileDetails();
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
    }
    private void setProfileDetails(){
        lblName.setText(User.fname+" "+User.lname);
        lblBloodType.setText("Blood Group: "+User.bloodType);
        lblAge.setText("Age: "+User.calculateAge().getYears());
        lblMobile.setText("Mobile: "+User.mobile);
    }

    //Settings Panel
    //Donation Panel
    @FXML private void handleSubmitButton(ActionEvent submitEvent){
        try{
            pickDonateDetails();
            submitDonateValues();
            settingsPane.setVisible(false);
            profilePane.setVisible(true);
            System.out.println("Success");
        } catch (Exception e){
            e.printStackTrace();
        }
    }
    @FXML private void submitDonateValues(){
        String query;
        if(checkUpdate == true){
            query = "UPDATE PATIENT_DONATE SET NIC='"+User.nic+"', Kidney='"+User.Donate.Organs.kidney+"', Heart='"+User.Donate.Organs.heart+"', Intestines='"+User.Donate.Organs.intestines+"',Pancreas='"+User.Donate.Organs.pancreas+"',Liver='"+User.Donate.Organs.liver+"',Lungs='"+User.Donate.Organs.lungs+"',Veins='"+User.Donate.Tissues.veins+"',Corneas='"+User.Donate.Tissues.corneas+"',Bone='"+User.Donate.Tissues.bone+"',Skin='"+User.Donate.Tissues.skin+"',Cartilage='"+User.Donate.Tissues.cartilage+"',Ligaments='"+User.Donate.Tissues.ligaments+"',Heart_Valves='"+User.Donate.Tissues.heartValves+"',Tendons='"+User.Donate.Tissues.tendons+"',Middle_Ear='"+User.Donate.Tissues.middleEar+"',Blood='"+User.Donate.Blood.blood+"'";
        } else {
            query = "INSERT INTO PATIENT_DONATE(NIC,Kidney,Heart,Intestines,Pancreas,Liver,Lungs,Veins,Corneas,Bone,Skin,Cartilage,Ligaments,Heart_Valves,Tendons,Middle_Ear,Blood) values('"+User.nic+"','"+User.Donate.Organs.kidney+"','"+User.Donate.Organs.heart+"','"+User.Donate.Organs.intestines+"','"+User.Donate.Organs.pancreas+"','"+User.Donate.Organs.liver+"','"+User.Donate.Organs.lungs+"','"+User.Donate.Tissues.veins+"','"+User.Donate.Tissues.corneas+"','"+User.Donate.Tissues.bone+"','"+User.Donate.Tissues.skin+"','"+User.Donate.Tissues.cartilage+"','"+User.Donate.Tissues.ligaments+"','"+User.Donate.Tissues.heartValves+"','"+User.Donate.Tissues.tendons+"','"+User.Donate.Tissues.middleEar+"','"+User.Donate.Blood.blood+"')";
        }
        try{
            Statement statement = connect.connect2DB().createStatement();
            statement.execute(query);
            System.out.println("Success");
        }catch (SQLException e){
            e.printStackTrace();
        }
    }
    private void pickDonateDetails(){
        //Organs
        User.Donate.Organs.kidney = boolean2int(chkKidney.isSelected());
        User.Donate.Organs.heart = boolean2int(chkHeart.isSelected());
        User.Donate.Organs.intestines = boolean2int(chkIntestines.isSelected());
        User.Donate.Organs.liver = boolean2int(chkLiver.isSelected());
        User.Donate.Organs.lungs = boolean2int(chkLungs.isSelected());
        User.Donate.Organs.pancreas = boolean2int(chkPancreas.isSelected());
        //Tissues
        User.Donate.Tissues.bone = boolean2int(chkBone.isSelected());
        User.Donate.Tissues.cartilage = boolean2int(chkCartilage.isSelected());
        User.Donate.Tissues.corneas = boolean2int(chkCorneas.isSelected());
        User.Donate.Tissues.heartValves = boolean2int(chkHeartValves.isSelected());
        User.Donate.Tissues.ligaments = boolean2int(chkLigaments.isSelected());
        User.Donate.Tissues.middleEar = boolean2int(chkMiddleEar.isSelected());
        User.Donate.Tissues.skin = boolean2int(chkSkin.isSelected());
        User.Donate.Tissues.tendons = boolean2int(chkTendons.isSelected());
        User.Donate.Tissues.veins = boolean2int(chkVeins.isSelected());
        //Blood
        User.Donate.Blood.blood = boolean2int(chkBlood.isSelected());
        System.out.println("Picked Values");
    }
    private static int boolean2int(Boolean inValue){
        return inValue?1:0;
    }
    private static boolean int2boolean(int inValue){
        return inValue!=0?true:false;
    }
    private void getExistingDonateDetails(String inUID){
        String query = "SELECT * FROM PATIENT_DONATE WHERE NIC = '"+inUID+"'";
        try{
            Statement statement = connect.connect2DB().createStatement();
            ResultSet resultSet = statement.executeQuery(query);

            while(resultSet.next()){
                checkUpdate = true;
                User.Donate.Organs.kidney = resultSet.getInt("Kidney");
                User.Donate.Organs.heart = resultSet.getInt("Heart");
                User.Donate.Organs.intestines = resultSet.getInt("Intestines");
                User.Donate.Organs.pancreas = resultSet.getInt("Pancreas");
                User.Donate.Organs.liver = resultSet.getInt("Liver");
                User.Donate.Organs.lungs = resultSet.getInt("Lungs");
                User.Donate.Tissues.veins = resultSet.getInt("Veins");
                User.Donate.Tissues.corneas = resultSet.getInt("Corneas");
                User.Donate.Tissues.bone = resultSet.getInt("Bone");
                User.Donate.Tissues.skin = resultSet.getInt("Skin");
                User.Donate.Tissues.cartilage = resultSet.getInt("Cartilage");
                User.Donate.Tissues.ligaments = resultSet.getInt("Ligaments");
                User.Donate.Tissues.heartValves = resultSet.getInt("Heart_Valves");
                User.Donate.Tissues.tendons = resultSet.getInt("Tendons");
                User.Donate.Tissues.middleEar = resultSet.getInt("Middle_Ear");
                User.Donate.Blood.blood = resultSet.getInt("Blood");
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
    }
    @FXML private void setExistingDonateDetails(){
        chkKidney.setSelected(int2boolean(User.Donate.Organs.kidney));
        chkHeart.setSelected(int2boolean(User.Donate.Organs.heart));
        chkIntestines.setSelected(int2boolean(User.Donate.Organs.intestines));
        chkPancreas.setSelected(int2boolean(User.Donate.Organs.pancreas));
        chkLiver.setSelected(int2boolean(User.Donate.Organs.liver));
        chkLungs.setSelected(int2boolean(User.Donate.Organs.lungs));
        chkVeins.setSelected(int2boolean(User.Donate.Tissues.veins));
        chkCorneas.setSelected(int2boolean(User.Donate.Tissues.corneas));
        chkBone.setSelected(int2boolean(User.Donate.Tissues.bone));
        chkSkin.setSelected(int2boolean(User.Donate.Tissues.skin));
        chkCartilage.setSelected(int2boolean(User.Donate.Tissues.cartilage));
        chkLigaments.setSelected(int2boolean(User.Donate.Tissues.ligaments));
        chkHeartValves.setSelected(int2boolean(User.Donate.Tissues.heartValves));
        chkTendons.setSelected(int2boolean(User.Donate.Tissues.tendons));
        chkMiddleEar.setSelected(int2boolean(User.Donate.Tissues.middleEar));
        chkBlood.setSelected(int2boolean(User.Donate.Blood.blood));
    }

    //Request Panel
    //Request Form
    @FXML private void handleTissueButton(ActionEvent tissueEvent){
        if(rBtnTissue.isSelected()){
            rBtnOrgan.setSelected(false);
            rBtnBlood.setSelected(false);
            formMainGroup = "Tissue";
            subGroup.getItems().removeAll(subGroup.getItems());
            subGroup.getItems().addAll("Veins","Corneas","Bone","Skin","Cartilage","Ligaments","Heart Valves","Tendons","Middle Ear");
        }
    }
    @FXML private void handleBloodButton(ActionEvent bloodEvent){
        if(rBtnBlood.isSelected()){
            rBtnOrgan.setSelected(false);
            rBtnTissue.setSelected(false);
            formMainGroup = "Blood";
            subGroup.getItems().removeAll(subGroup.getItems());
            subGroup.getItems().addAll("Blood");
        }
    }
    @FXML private void handleOrganButton(ActionEvent organEvent){
        if(rBtnOrgan.isSelected()){
            rBtnTissue.setSelected(false);
            rBtnBlood.setSelected(false);
            formMainGroup = "Organ";
            subGroup.getItems().removeAll(subGroup.getItems());
            subGroup.getItems().addAll("Kidney","Heart","Intestines","Pancreas","Liver","Lungs");
        }
    }
    @FXML private void handleNewRequest(ActionEvent newRequestEvent){
        try {
            requestFormPane.setVisible(true);
            rBtnBlood.setSelected(false);
            rBtnOrgan.setSelected(false);
            rBtnTissue.setSelected(false);
            requireDate.setValue(null);
            txtRequireLocation.setText(null);
            requestPaneResult.setVisible(false);
        } catch (Exception e){
            e.printStackTrace();
        }
    }
    @FXML private void handleSubmitForm(ActionEvent submitFormEvent){
        String query = "INSERT INTO REQUESTS(NIC,Main_Group,Sub_Group,Required_Date,Required_Location) values('"+User.nic+"','"+formMainGroup+"','"+subGroup.getValue()+"','"+requireDate.getValue()+"','"+txtRequireLocation.getText()+"')";
        try {
            Statement statement = connect.connect2DB().createStatement();
            statement.execute(query);
        } catch (SQLException e){
            e.printStackTrace();
        }

        String query2 = "UPDATE PATIENT_REQUEST SET "+subGroup.getValue()+" = '1' WHERE NIC='"+User.nic+"'";
        try {
            Statement statement = connect.connect2DB().createStatement();
            statement.execute(query2);
            requestPaneResult.setVisible(true);
            requestFormPane.setVisible(false);
        } catch (SQLException e){
            e.printStackTrace();
        }
    }

    //Request Result Pane


}
