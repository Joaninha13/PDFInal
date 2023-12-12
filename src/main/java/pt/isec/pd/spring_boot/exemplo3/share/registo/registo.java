package pt.isec.pd.spring_boot.exemplo3.share.registo;

import java.io.Serializable;

public class registo implements Serializable {
    private static final long serialVersionUID = 1L;

    private String name;
    private int identificationNumber;
    private String email;
    private String password;
    private String msg;
    private boolean isRegistered,isValid,isAdmin;

    public registo(String name, String email, String password) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.isRegistered = false;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getIdentificationNumber() {
        return identificationNumber;
    }

    public void setIdentificationNumber(int identificationNumber) {this.identificationNumber = identificationNumber;}

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public boolean isRegistered() {
        return isRegistered;
    }

    public void setRegistered(boolean registered) {
        isRegistered = registered;
    }

    public boolean isValid() {
        return isValid;
    }

    public void setValid(boolean valid) {
        isValid = valid;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }

    @Override
    public String toString() {
        return "name='" + name + '\'' +
                ", id='" + identificationNumber + '\'' +
                ", email='" + email + '\'';
    }
}
