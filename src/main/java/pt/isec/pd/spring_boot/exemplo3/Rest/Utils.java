package pt.isec.pd.spring_boot.exemplo3.Rest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import pt.isec.pd.spring_boot.exemplo3.Rest.models.consultPresenModel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Utils {
    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    public static Date StrToDate(String strDate) {
        try {
            return sdf.parse(strDate);
        } catch (ParseException e) {
            return null;
        }
    }

    public static String DateToStr(Date date) {
        return sdf.format(date);
    }

    public static boolean isAdmin(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null && authentication.isAuthenticated() && authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("SCOPE_ADMIN"));
    }
}