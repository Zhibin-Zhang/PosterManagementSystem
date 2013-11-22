package ordappengine;

import java.io.UnsupportedEncodingException;
import java.util.Properties;  

import javax.mail.*;  
import javax.mail.internet.InternetAddress;  
import javax.mail.internet.MimeMessage;  
  
public class Mailer {  
public static void send(String to,String subject,String msg) throws UnsupportedEncodingException{  
  
//1st step) Get the session object    
Properties props = new Properties();  
Session session = Session.getDefaultInstance(props, null);
  
  
//2nd step)compose message  
try {  
 MimeMessage message = new MimeMessage(session);  
 message.setFrom(new InternetAddress("Cloud5olutionsUCO@uco.com" , "Poster Management System"));  
 message.addRecipient(Message.RecipientType.TO,new InternetAddress(to));  
 message.setSubject(subject);  
 message.setText(msg);  
   
 //3rd step)send message  
 Transport.send(message);  
  
 System.out.println("Done");  
  
 } catch (MessagingException e) {  
    throw new RuntimeException(e);  
 }  
      
}  
}