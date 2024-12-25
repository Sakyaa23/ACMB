package ibm;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
//import java.text.DateFormat;
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

public class ACMBRecon {
  public static void main(String[] args) throws ClassNotFoundException, SQLException {
    String result = null;
    Class.forName("oracle.jdbc.driver.OracleDriver");
    /*
    Date date = Calendar.getInstance().getTime();
    Calendar cal = Calendar.getInstance();
    cal.add(5, -1);
    date = cal.getTime();
    DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
    String strDate = dateFormat.format(date);
    */
    SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
	Date date = new Date();
	String currentDate=dateFormat.format(date);
	System.out.println("CurrentDate : "+currentDate);
	Calendar c = Calendar.getInstance();
	c.setTime(date);
	c.add(Calendar.DATE, -3);
	//c.add(Calendar.MINUTE, -15);
	Date newDate1 = c.getTime();
	String strDate=dateFormat.format(newDate1);
	System.out.println("UpdatedDate(15) : "+strDate);
    //Connection con1 = DriverManager.getConnection("jdbc:oracle:thin:@//emcrpt-p-01.internal.das:1525/emcrptp", "FILENETCE_BO_PROD", "Fil3p_P3od");
    //Connection con2 = DriverManager.getConnection("jdbc:oracle:thin:@//fnetcengn-p-01.internal.das:1525/fnetcep", "SRCDATABASE", "ECMstores1#");
    //Statement stmt1 = con1.createStatement();
    Statement stmt1 = null;
    //stmt1 = con1.createStatement();
    //Statement stmt2 = con2.createStatement();
    Statement stmt2 = null;
    ////stmt2 = con2.createStatement();
   String to = "sakya.samanta@anthem.com";
   //String to = "DL-FileNetLightsOnSupport@anthem.com,dl-ecm_bo_report_dev_support@anthem.com,dl-ecm_contentservices@anthem.com";
    String sender = "DL-FileNetLightsOnSupport@anthem.com";
    String host = "smtp.wellpoint.com";
    Properties properties = System.getProperties();
    properties.setProperty("mail.smtp.host", host);
    Session session = Session.getDefaultInstance(properties);
    try {
    	Connection con1 = DriverManager.getConnection("jdbc:oracle:thin:@//emcrpt-p-01.internal.das:1525/emcrptp", "FILENETCE_BO_PROD", "Fil3p_P3od");
    	stmt1 = con1.createStatement();
      MimeMessage message = new MimeMessage(session);
      message.setFrom((Address)new InternetAddress(sender));
      String[] recipientList = to.split(",");
		int counter =0;
		InternetAddress[] recipientAddress = new InternetAddress[recipientList.length];
		for(String recipient : recipientList) {
			recipientAddress[counter]=new InternetAddress(recipient.trim());
			counter++;
		}
		message.setRecipients(Message.RecipientType.TO, recipientAddress);
      //message.addRecipient(Message.RecipientType.TO, (Address)new InternetAddress(recipient));
     // message.addRecipient(Message.RecipientType.TO, (Address)new InternetAddress(recipient1));
     // message.addRecipient(Message.RecipientType.TO, (Address)new InternetAddress(recipient2));
     // message.addRecipient(Message.RecipientType.TO, (Address)new InternetAddress(recipient3));
      message.setSubject("ACMB (FilenetCE Recon) - Status- " + strDate);
      int i = 0, j = 0, diff = 0;
      StringBuilder builder = new StringBuilder();
      builder.append("Hi Team,");
      builder.append("\n");
      builder.append("<br>");
      builder.append("Please find the details below");
      builder.append("\n");
      builder.append("<br>");
      builder.append("<br>");
      System.out.println("Start");
      builder.append("BO Data");
      builder.append("<br>");
      builder.append("\n");
      builder.append("<html><body><table border=1><tr bgcolor='#b2beb5'><th>sub_category_name</th><th>DOCUMENT_CLASS</th><th>count(*)</th></tr>");
      ResultSet rs1 = stmt1.executeQuery("select sub_category_name,DOCUMENT_CLASS,count(*) from BUSSOBJ.ECM_REPOSITORIES where to_char(record_date,'DD-MON-YY')= trunc(SYSDATE)-3 AND REPOSITORY_NAME='FILENETCE' GROUP BY sub_category_name,DOCUMENT_CLASS ORDER BY sub_category_name");
     System.out.println("after BO1");
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
     // ResultSet rs = stmt1.executeQuery("select count(*) as Total_BO_Count from BUSSOBJ.ecm_repositories where to_char(record_date,'DD-MON-YY')= trunc(SYSDATE)-1 AND REPOSITORY_NAME='FILENETCE' order by sub_category_name");
      ResultSet rs = stmt1.executeQuery("select count(*) as Total_BO_Count from BUSSOBJ.ECM_REPOSITORIES where to_char(record_date,'DD-MON-YY')= trunc(SYSDATE)-3 AND REPOSITORY_NAME='FILENETCE' order by sub_category_name");
      builder.append("<html><body><table border=1><tr bgcolor='#b2beb5'><th>Total_BO_Count</th></tr>");
      System.out.println("after BO2");
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
      con1.close();
      Connection con2 = DriverManager.getConnection("jdbc:oracle:thin:@//fnetcengn-p-01.internal.das:1525/fnetcep", "SRCDATABASE", "ECMstores1#");
      stmt2 = con2.createStatement();
      builder.append("FileNet Data");
      builder.append("\n");
      ResultSet rs2 = stmt2.executeQuery("Select UA5_DOCUMENTCLASS, count(*) from Centralclaims.DOCVERSION where CREATE_DATE >= trunc(SYSDATE)-4 + 5/24 AND CREATE_DATE < trunc(SYSDATE)-3 + 5/24 and Mime_Type != 'application/vnd.filenet.im-cold' GROUP BY UA5_DOCUMENTCLASS");
      builder.append("<html><body><table border=1><tr bgcolor='#b2beb5'><th>CENTRALCLAIMS</th><th>count(*)</th></tr>");
      System.out.println("After CentralClaims Data");
      while (rs2.next()) {
        builder.append("<tr>");
        builder.append("<td>");
        builder.append(rs2.getString(1));
        builder.append("</td>");
        builder.append("<td>");
        builder.append(rs2.getInt(2));
        builder.append("</td>");
        builder.append("</tr>");
        i += rs2.getInt(2);
      } 
      builder.append("</table></body></html>");
      ResultSet rs3 = stmt2.executeQuery("Select U9f_DOCUMENTCLASS, count(*) from Westclaims.DOCVERSION where CREATE_DATE >= trunc(SYSDATE)-4 + 5/24 AND CREATE_DATE < trunc(SYSDATE)-3 + 5/24   and Mime_Type != 'application/vnd.filenet.im-cold' GROUP BY U9f_DOCUMENTCLASS");
      builder.append("<html><body><table border=1><tr bgcolor='#b2beb5'><th>WESTCLAIMS</th><th>count(*)</th></tr>");
     System.out.println("After WestClaims Data");
      while (rs3.next()) {
        builder.append("<tr>");
        builder.append("<td>");
        builder.append(rs3.getString(1));
        builder.append("</td>");
        builder.append("<td>");
        builder.append(rs3.getInt(2));
        builder.append("</td>");
        builder.append("</tr>");
        i += rs3.getInt(2);
      } 
      builder.append("</table></body></html>");
      ResultSet rs4 = stmt2.executeQuery("Select UA3_DOCUMENTCLASS, count(*) from Provider.DOCVERSION where CREATE_DATE >= trunc(SYSDATE)-4 + 5/24 AND CREATE_DATE < trunc(SYSDATE)-3 + 5/24   GROUP BY UA3_DOCUMENTCLASS");
      builder.append("<html><body><table border=1><tr bgcolor='#b2beb5'><th>PROVIDER</th><th>count(*)</th></tr>");
      System.out.println("After Provider Data");
      while (rs4.next()) {
        builder.append("<tr>");
        builder.append("<td>");
        builder.append(rs4.getString(1));
        builder.append("</td>");
        builder.append("<td>");
        builder.append(rs4.getInt(2));
        builder.append("</td>");
        builder.append("</tr>");
        i += rs4.getInt(2);
      } 
      builder.append("</table></body></html>");
      ResultSet rs5 = stmt2.executeQuery("Select UA8_DOCUMENTCLASS, count(*) from Enrollment.DOCVERSION where CREATE_DATE >= trunc(SYSDATE)-4 + 5/24 AND CREATE_DATE < trunc(SYSDATE)-3 + 5/24  and Mime_Type != 'application/vnd.filenet.im-cold' GROUP BY UA8_DOCUMENTCLASS");
      builder.append("<html><body><table border=1><tr bgcolor='#b2beb5'><th>ENROLLMENT</th><th>count(*)</th></tr>");
      System.out.println("After Enrollment Data");
      while (rs5.next()) {
        builder.append("<tr>");
        builder.append("<td>");
        builder.append(rs5.getString(1));
        builder.append("</td>");
        builder.append("<td>");
        builder.append(rs5.getInt(2));
        builder.append("</td>");
        builder.append("</tr>");
        i += rs5.getInt(2);
      } 
      builder.append("</table></body></html>");
      ResultSet rs6 = stmt2.executeQuery("Select U6A_DOCUMENTCLASS, count(*) from HR.DOCVERSION where CREATE_DATE >= trunc(SYSDATE)-4 + 5/24 AND CREATE_DATE < trunc(SYSDATE)-3 + 5/24  and Mime_Type != 'application/vnd.filenet.im-cold' GROUP BY U6A_DOCUMENTCLASS");
      builder.append("<html><body><table border=1><tr bgcolor='#b2beb5'><th>HR</th><th>count(*)</th></tr>");
      System.out.println("After HR Data");
      while (rs6.next()) {
        builder.append("<tr>");
        builder.append("<td>");
        builder.append(rs6.getString(1));
        builder.append("</td>");
        builder.append("<td>");
        builder.append(rs6.getInt(2));
        builder.append("</td>");
        builder.append("</tr>");
        i += rs6.getInt(2);
      } 
      builder.append("</table></body></html>");
      ResultSet rs7 = stmt2.executeQuery("Select U9f_DOCUMENTCLASS, count(*) from Eastclaims.DOCVERSION where CREATE_DATE >= trunc(SYSDATE)-4 + 5/24 AND CREATE_DATE < trunc(SYSDATE)-3 + 5/24 and Mime_Type != 'application/vnd.filenet.im-cold' GROUP BY U9f_DOCUMENTCLASS");
      builder.append("<html><body><table border=1><tr bgcolor='#b2beb5'><th>EASTCLAIMS</th><th>count(*)</th></tr>");
      System.out.println("After EastClaims Data");
      while (rs7.next()) {
        builder.append("<tr>");
        builder.append("<td>");
        builder.append(rs7.getString(1));
        builder.append("</td>");
        builder.append("<td>");
        builder.append(rs7.getInt(2));
        builder.append("</td>");
        builder.append("</tr>");
        i += rs7.getInt(2);
      } 
      builder.append("</table></body></html>");
      ResultSet rs8 = stmt2.executeQuery("Select UC4_DOCUMENTCLASS, count(*) from FEP.DOCVERSION where CREATE_DATE >= trunc(SYSDATE)-4 + 5/24 AND CREATE_DATE < trunc(SYSDATE)-3 + 5/24  and Mime_Type != 'application/vnd.filenet.im-cold' GROUP BY UC4_DOCUMENTCLASS");
      builder.append("<html><body><table border=1><tr bgcolor='#b2beb5'><th>FEP</th><th>count(*)</th></tr>");
      System.out.println("After FEP Data");
      while (rs8.next()) {
        builder.append("<tr>");
        builder.append("<td>");
        builder.append(rs8.getString(1));
        builder.append("</td>");
        builder.append("<td>");
        builder.append(rs8.getInt(2));
        builder.append("</td>");
        builder.append("</tr>");
        i += rs8.getInt(2);
      } 
      builder.append("</table></body></html>");
      ResultSet rs9 = stmt2.executeQuery("Select UD6_DOCUMENTCLASS, count(*) from CFSHOME.DOCVERSION where CREATE_DATE >= trunc(SYSDATE)-4 + 5/24 AND CREATE_DATE < trunc(SYSDATE)-3 + 5/24  and Mime_Type != 'application/vnd.filenet.im-cold' GROUP BY UD6_DOCUMENTCLASS");
      builder.append("<html><body><table border=1><tr bgcolor='#b2beb5'><th>CFSHOME</th><th>count(*)</th></tr>");
      System.out.println("After CFSHOME Data");
      while (rs9.next()) {
        builder.append("<tr>");
        builder.append("<td>");
        builder.append(rs9.getString(1));
        builder.append("</td>");
        builder.append("<td>");
        builder.append(rs9.getInt(2));
        builder.append("</td>");
        builder.append("</tr>");
        i += rs9.getInt(2);
      } 
      builder.append("</table></body></html>");
      ResultSet rs10 = stmt2.executeQuery("Select U99_DOCUMENTCLASS, count(*) from Finance.DOCVERSION where CREATE_DATE >= trunc(SYSDATE)-4 + 5/24 AND CREATE_DATE < trunc(SYSDATE)-3 + 5/24  and Mime_Type != 'application/vnd.filenet.im-cold' GROUP BY U99_DOCUMENTCLASS");
      builder.append("<html><body><table border=1><tr bgcolor='#b2beb5'><th>FINANCE</th><th>count(*)</th></tr>");
      System.out.println("After FINANCE Data");
      while (rs10.next()) {
        builder.append("<tr>");
        builder.append("<td>");
        builder.append(rs10.getString(1));
        builder.append("</td>");
        builder.append("<td>");
        builder.append(rs10.getInt(2));
        builder.append("</td>");
        builder.append("</tr>");
        i += rs10.getInt(2);
      } 
      builder.append("</table></body></html>");
      ResultSet rs11 = stmt2.executeQuery("Select U7D_DOCUMENTCLASS, count(*) from Legal.DOCVERSION where CREATE_DATE >= trunc(SYSDATE)-4 + 5/24 AND CREATE_DATE < trunc(SYSDATE)-3 + 5/24  and Mime_Type != 'application/vnd.filenet.im-cold' GROUP BY U7D_DOCUMENTCLASS");
      builder.append("<html><body><table border=1><tr bgcolor='#b2beb5'><th>LEGAL</th><th>count(*)</th></tr>");
      System.out.println("After Legal Data");
      while (rs11.next()) {
        builder.append("<tr>");
        builder.append("<td>");
        builder.append(rs11.getString(1));
        builder.append("</td>");
        builder.append("<td>");
        builder.append(rs11.getInt(2));
        builder.append("</td>");
        builder.append("</tr>");
        i += rs11.getInt(2);
      } 
      builder.append("</table></body></html>");
      builder.append("FileNet count is : ");
      builder.append(i);
      builder.append("<br>");
      builder.append("<br>");
      if (i == j) {
        builder.append("<html><body><table border=1><tr>Count Matches for BO and FileNet</tr></table></body></html>");
      } else if (i != j) {
        if (i < j) {
          diff = j - i;
        } else {
          diff = i - j;
        } 
        builder.append("<html><body><table border=1><tr>Count Does not Match for BO and FileNet. Please check. </tr></table></body></html>");
      } 
      builder.append("<br>");
      builder.append("delta is:");
      builder.append(diff);
      result = builder.toString();
      message.setContent(result, "text/html");
      Transport.send((Message)message);
      System.out.println("Mail successfully sent");
      System.out.println(i);
      System.out.println(j);
      con2.close();
    } catch (MessagingException mex) {
      mex.printStackTrace();
    } 
    //con1.close();
    //con2.close();
  }
}

