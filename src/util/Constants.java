/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;

/**
 *
 * @author eroot
 */
public class Constants {
    public static final String LOGIN_SCREEN = "keti_UI.fxml";
    public static final String MAIN_SCREEN = "mainui.fxml";
    public static final String SUCURSAL_VIEW = "agencies.fxml";
    public static final String CLIENT_VIEW = "clients.fxml";
    public static final String CHARGEMENT_VIEW = "chargement.fxml";
    public static final String DEPENSE_VIEW = "depenses.fxml";
    public static final String PERFORMANCE_VIEW = "performance.fxml";
    public static final String RETRAIT_VIEW = "retrait.fxml";
    public static final String VEHICULE_VIEW = "vehicule.fxml";
    public static final String CAISSE_VIEW = "finance.fxml";
    
    public static final String LOGIN = "login-ui";
    public static final String MAIN = "main-ui";
    public static final String CLIENTS = "clients-ui";
    public static final String SUCURSAL = "sucursal-ui";
    public static final String CHARGEMENT = "chargement-ui";
    public static final String DEPENSE = "depenses-ui";
    public static final String PERFORMANCE = "performance-ui";
    public static final String RETRAIT = "retrait-ui";
    public static final String VEHICULES = "vehicules-ui";
    public static final String CAISSES = "finance-ui";
    public static final SimpleDateFormat DateFormateur=new SimpleDateFormat("yyyy-MM-dd");
    public static final Calendar Calendrier=Calendar.getInstance();
    
    //dialogs
    public static final String TRANSPORTER_DLG = "transporter.fxml";
    public static final String MARCHANDISE_DLG = "marchandise.fxml";
    public static final String ADDTOCART_DLG = "panier.fxml";
    public static final String USERCREATOR_DLG = "utilisateurs.fxml";
    //constants
    public static final String VEHICLE_LOADED="Chargé";
    public static final String VEHICLE_DEPARTURE="Départ";
    public static final String VEHICLE_ARRIVAL="Arrivée";
    
    public static Date toUtilDate(LocalDate ldt){
        return Date.from(ldt.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }
    
    public static long dateInMillis(LocalDate ldt){
        return ldt.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }
    
    public static String getMonthName(String numRepres){
        if(numRepres.endsWith("01")){
            return "Janvier";
        }else if(numRepres.endsWith("02")){
            return "Fevrier";
        }else if(numRepres.endsWith("03")){
            return "Mars";
        }else if(numRepres.endsWith("04")){
            return "Avril";
        }else if(numRepres.endsWith("05")){
            return "Mai";
        }else if(numRepres.endsWith("06")){
            return "Juin";
        }else if(numRepres.endsWith("07")){
            return "Juillet";
        }else if(numRepres.endsWith("08")){
            return "Aout";
        }else if(numRepres.endsWith("09")){
            return "Septembre";
        }else if(numRepres.endsWith("10")){
            return "Octobre";
        }else if(numRepres.endsWith("11")){
            return "Novemebre";
        }else if(numRepres.endsWith("12")){
            return "Decembre";
        }
        return "";
    }
    
}
