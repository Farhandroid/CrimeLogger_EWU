package tanvir.crimelogger_ewu.MOdelClass;

import java.io.Serializable;

/**
 * Created by USER on 08-Jan-18.
 */

public class UserInfoMC implements Serializable {

    String name , userName , email , phoneNumber  , isPCavailable ;

    public UserInfoMC(String name, String userName, String email, String phoneNumber, String isPCavailable) {
        this.name = name;
        this.userName = userName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.isPCavailable = isPCavailable;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getName() {
        return name;
    }

    public String getUserName() {
        return userName;
    }

    public String getEmail() {
        return email;
    }

    public String getIsPCavailable() {
        return isPCavailable;
    }
}
