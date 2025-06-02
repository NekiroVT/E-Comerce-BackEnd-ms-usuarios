
package com.msusuarios.util;

import jakarta.mail.Session;
import jakarta.mail.Transport;
import java.util.Properties;

public class EmailValidatorUtil {

    public static boolean verificarCorreoSMTP(String email) {
        try {
            String dominio = email.substring(email.indexOf("@") + 1);

            Properties props = new Properties();
            props.put("mail.smtp.host", "smtp." + dominio);
            props.put("mail.smtp.port", "25");
            props.put("mail.smtp.timeout", "5000");
            props.put("mail.smtp.connectiontimeout", "5000");

            Session session = Session.getInstance(props);
            Transport transport = session.getTransport("smtp");

            transport.connect();
            transport.close();

            return true;
        } catch (Exception e) {
            System.out.println("❌ Error validando correo SMTP: " + e.getMessage());
            return false;
        }
    }
}
