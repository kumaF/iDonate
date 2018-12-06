package sample;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.time.LocalDate;
import java.time.Period;

public class user {
    String fname, lname, address, email, nic, mobile,gender, pw, bloodType;
    LocalDate dob;
    disease Disease;
    transplant Donate = new transplant();
    transplant Requests = new transplant();
    transplant Recieved = new transplant();

    user(){

    }

    protected boolean verifyUname(String inUname){
        if(inUname.equals(this.nic))
            return true;
        else return false;
    }

    protected boolean auth(String inUname, String inPassword){
        if(inUname.equals(this.nic) && inPassword.equals(this.pw))
            return true;
        else return false;
    }

    protected Period calculateAge(){
        LocalDate date = LocalDate.now();
        Period age = Period.between(this.dob,date);
        return age;
    }
}
