/********************************************************
    Module  : GeneralLib.java

    Description : T
    
    History
    -------
    12/4/2013: Created
**********************************************************/

import java.io.*;
import java.util.*;
//import java.text.*;

//import javax.mail.*;
//import javax.mail.internet.*;
//import javax.activation.*;

//import java.lang.*;

//import javax.naming.InitialContext;
//import javax.naming.NamingException;
//import javax.sql.DataSource;

public class GeneralLib
{
    
//
// Method LogFileWriter
// This will write the log file

public static int LogFileWriter(String FileLocation,String filename, String Content,int appendFlag, String debug ) throws IOException{
	

    boolean append = true;
   // System.out.println("filename=" + filename);
     if(filename.length() == 0) {
      filename="a.txt";
     }
      
 
    Date curDate = new Date();
    
    String source = null;
    source = Content ;


    try {
    if(debug.compareTo("Y")==0) {
    	FileWriter f0 = new FileWriter(FileLocation +"/" + filename,true);
    	// append flag --> 1 to append, 0 to no append
    if(appendFlag == 0) {
    	f0 = new FileWriter(FileLocation +"/" + filename);
    }else {
    	f0 = new FileWriter(FileLocation +"/" + filename,append); 
    }
    f0.write(source);
    
    f0.close();
    }
    return 0;
  
    }catch(FileNotFoundException f) {
        System.out.println("error");
        return 1;
    }
}


// 
// Method getPropertyInfo
// This will get all the property info and transfer into hashtable
//

public  Hashtable<String, String> getPropertyInfo() throws IOException {
   String filepath = System.getenv("FETCH_PROP_PATH") + "/FetchEmail.properties"; 
   String mailserver = "";
   String debug = "";
   String mailuser = "";
   String mailpasswd = "";
   String Dt_File_Location = "";
   String subject_val = "";
   String variable_val="";
   String debug_file_location="";
   String debug_file_name="";
   String output_file_location="";
  // String output_file_name="";
   //String check_subject_pattern="ALL";

   Properties p = new Properties();
   Hashtable<String, String> details =null;
   //System.out.println("filepath=" + filepath);
   details=new Hashtable<String, String>();
   FileInputStream fi = new FileInputStream(filepath);

      try {
      p.load(fi);
      mailserver      = p.getProperty("mailserver");
      debug           = p.getProperty("debug");
      mailuser          = p.getProperty("mailuser");
      mailpasswd        = p.getProperty("mailpasswd");
      subject_val    = p.getProperty("subject_val");
      variable_val    = p.getProperty("variable_val");
      debug_file_location = p.getProperty("debug_file_location");
      debug_file_name   =   p.getProperty("debug_file_name");
      output_file_location  = p.getProperty("output_file_location");
   //   output_file_name   = p.getProperty("output_file_name");
      //check_subject_pattern = p.getProperty("check_subject_pattern");
     // check_subject_pattern =check_subject_pattern.toUpperCase();

      
      Dt_File_Location = p.getProperty("Dt_File_Location");


      details.put("mailserver",mailserver);
//      System.out.println("details.mailserver = " + details.get("mailserver"));

      details.put("debug",debug);
//      System.out.println("details.debug = " + details.get("debug"));

      details.put("mailuser",mailuser);
//      System.out.println("details.mailuser = " + details.get("mailuser"));

      details.put("mailpasswd",mailpasswd);
      //details.put("check_subject_pattern",check_subject_pattern);
//      System.out.println("details.tcpasswd = " + details.get("tcpasswd"));
  //    details.put("subject_val",subject_val);
 //   System.out.println("details.subject_val = " + details.get("subject_val"));
      
  //    details.put("variable_array",variable_val);
 //   System.out.println("details.variable_array = " + details.get("variable_val"));
    //  details.put("Dt_File_Location",Dt_File_Location);
      details.put("debug_file_location",debug_file_location);
      
      details.put("debug_file_name",debug_file_name);
      details.put("output_file_location",output_file_location);
  //    details.put("output_file_name",output_file_name);
    //  details.put("check_subject_pattern", check_subject_pattern);

      }catch(FileNotFoundException f) {
        System.out.println("error");
      }
//      System.out.println("details.dbusername = " + details.get("dbuser"));
      return details;
}

} // end of YugoalLib class
