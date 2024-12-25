package ibm;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Properties;
import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class DBQuery {
  public static void main(String[] args) throws ClassNotFoundException, SQLException {
    String result = null;
    Class.forName("oracle.jdbc.driver.OracleDriver");
    Date date = Calendar.getInstance().getTime();
    Calendar cal = Calendar.getInstance();
    cal.add(5, -1);
    date = cal.getTime();
    DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
    String strDate = dateFormat.format(date);
    Connection con2 = DriverManager.getConnection("jdbc:oracle:thin:@//fnetcengn-p-01.internal.das:1525/fnetcep ", "SRCDATABASE", "ECMstores1#");
    Statement stmt2 = con2.createStatement();
    String recipient = "Himgauri.Khaladkar@anthem.com";
    String sender = "Himgauri.Khaladkar@anthem.com";
    String host = "smtp.wellpoint.com";
    Properties properties = System.getProperties();
    properties.setProperty("mail.smtp.host", host);
    Session session = Session.getDefaultInstance(properties);
    try {
      MimeMessage message = new MimeMessage(session);
      message.setFrom((Address)new InternetAddress(sender));
      message.addRecipient(Message.RecipientType.TO, (Address)new InternetAddress(recipient));
      message.setSubject("ACMB (FilenetCE Recon) - Status- " + strDate);
      ResultSet rs1 = stmt2.executeQuery("select sub_category_name,DOCUMENT_CLASS,count(*) from BUSSOBJ.ecm_repositories where to_char(record_date,'DD-MON-YY')>= trunc(SYSDATE) and document_class !='PIAIDocs' GROUP BY sub_category_name,DOCUMENT_CLASS ORDER BY sub_category_name");
      int i = 0, j = 0;
      StringBuilder builder = new StringBuilder();
      builder.append("Hi Team,");
      builder.append("\n");
      builder.append("<br>");
      builder.append("Please find the details below");
      builder.append("\n");
      builder.append("<br>");
      builder.append("<br>");
      builder.append("BO Data");
      builder.append("<br>");
      builder.append("\n");
      builder.append("<html><body><table border=1><tr bgcolor='#b2beb5'><th>sub_category_name</th><th>DOCUMENT_CLASS</th><th>count(*)</th></tr>");
      while (rs1.next()) {
        builder.append("<tr>");
        builder.append("<td>");
        builder.append(rs1.getString(1));
        builder.append("</td>");
        builder.append("<td>");
        builder.append(rs1.getString(2));
        builder.append("</td>");
        builder.append("<td>");
        builder.append(rs1.getInt(3));
        builder.append("</td>");
        builder.append("</tr>");
      } 
      builder.append("</table></body></html>");
      builder.append("<tr>");
      builder.append("</tr>");
      ResultSet rs = stmt2.executeQuery("");
      builder.append("<html><body><table border=1><tr bgcolor='#b2beb5'><th>count(*)</th></tr>");
      while (rs.next()) {
        builder.append("<tr>");
        builder.append("<td>");
        builder.append(rs.getInt(1));
        builder.append("</td>");
        builder.append("</tr>");
        j = rs.getInt(1);
      } 
      builder.append("</table></body></html>");
      builder.append("<tr>");
      builder.append("</tr>");
      builder.append("\n");
      builder.append("<br>");
      builder.append("<br>");
      builder.append("FileNet Data");
      builder.append("\n");
      builder.append("<html><body><table border=1><tr bgcolor='#b2beb5'><th>DCNs</th><th>count(*)</th></tr>");
      builder.append("</table></body></html>");
      result = builder.toString();
      message.setContent(result, "text/html");
      Transport.send((Message)message);
      System.out.println("Mail successfully sent");
      System.out.println(i);
      System.out.println(j);
    } catch (MessagingException mex) {
      mex.printStackTrace();
    } 
    con2.close();
  }
}

