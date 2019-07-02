/*
 *  Code: FetchEmail.java
 *  Author: Karthikeyan Sundaram
 *  Date Created: Jun-2014
 *  PreRequisites:
 *        1) Please set up the FETCH_PROP_PATH env variable before running this program
 *        2) javamail.jar, htmlparser.jar in the java/lib.  Im them using add external JRE rather than copying.  
 *           Copying will not work.
 *        
 *  Notes:
 *  
 * 
 */

import java.io.*;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.*;
import java.util.*;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.Properties;
import java.util.regex.PatternSyntaxException;

import javax.mail.*;
import javax.mail.search.*;

import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;





//import com.sun.java.util.jar.pack.Attribute.Layout.Element;
import com.sun.xml.internal.txw2.Document;
import com.sun.xml.internal.ws.developer.MemberSubmissionEndpointReference.Elements;
 

public class FetchEmail {
	static GeneralLib g = new GeneralLib();
	static Hashtable pd = new Hashtable() ;

	static DateFormat formatter = new SimpleDateFormat("EEE MMM dd HH:mm:ss Z yyyy");
	static DateFormat formatter1 = new SimpleDateFormat("mm/dd/yyyy HH:mm:ss");
	static String debug="N";
	static String debug_file_location=null;
	static String debug_file_name = null;
    static String output_file_location =null; 
    static String output_file_name="FetchMail_Output_"; 
    static String check_subject_pattern="ALL";    
    static Properties props;
	static Calendar cal;
	static Calendar cal1;
	static Session session;
	static Integer file_status=0;
	static String FileLocation="";
	static String Line_Output="";
	static String line="";
	static String search="";
	static String dateStr = "";
    static String dateStr1 = "";
	static String Sent_Dt_Str = "";
	static String Sent_Dt_Str1 = "";
	static ReceivedDateTerm term;
	static String sjbct = "";
	static Integer Notification_Flag=0;
	static String Shipping_Service = "";
	static String content1 = "";
	static String content2 = "";
    static int content2_int=0;
	static String noHTMLString = "";
	static String content = "";
	static FileReader fr;
	static FileWriter fw;
	static FileWriter fw1;
	static BufferedReader br;
	static int sub_rec_count=0;
	static int con_rec_count=0;
	static int var_rec_count=0;
	static int search_replace_word_count=0;
	static int variable_replace_word_count=0;
	static String new_subject="";
	static Object Mail_Content=null;  
	static int check_subject_pattern_int=0;
	
	// eMail related variables
	static BodyPart plainTextPart;
	static Multipart multipart;
	static Store store;
	static Folder[] folder;
	static String[] Subject_Pattern;
	static String[] Content_Pattern;
	static String[] Variable_Pattern;
	static String[] Variable_Pattern_Backup;
	static String[] Extract_Values_Pattern;
	static String[] Subject_Array=null;
	static String[] Subject_Pattern_Split_Array=null;
	static String[] Subject_Content[]=null;
	static String[] Last_Dt[]=null;
	static String What_2_Check="";
	static String What_2_Check_Src="";
	static String Subject_Pattern_Id="";
	static String[] Subject_Pattern_Id_Array=null;
	static String[] Find_Replace_Text=null;
	static String[] Variable_Replace_Text=null;

	
	static Date Sent_Dt = null;
	static Date Sent_Dt1 = null;
	int sIndex=0, eIndex=0;
	
	
	public FetchEmail() {
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		 // System.out.println("argument=" + args[0]);
		try {
			  if (args.length > 0) {
				  System.out.println("argument > 0 " + args[0]);
				  check_subject_pattern = args[0];
				  output_file_name = output_file_name + args[0] + ".txt";
			  } else {
				  output_file_name = output_file_name  + "_ALL.txt";
				//  System.out.println("argument=" + args[0]);
			  }
			//  System.out.println("after if argument=" + args[0]);
			  Initialize();		
			  System.out.println("after initialize =");
			  Connect_Mail("mail.store.protocol", "imaps");
			  System.out.println("after connect_mail");
			  Find_Subject_Content_Count();
			  System.out.println("after find_subject_content");
			  Read_Subject_Content_Pattern();
			  System.out.println("after read_subject_content");
			  Find_variable_Pattern();
			  System.out.println("after find_variable_pattern");
			  Find_Variable_Replace_Words();
			  System.out.println("after find_variable_replace");
			  Read_Variable_Replace_Words();
			  System.out.println("after read_variable_replace");
			  Read_Variables();
			  System.out.println("after read_variables");
			  Find_Search_Replace_Words();
			  System.out.println("after find_search_replace_words");
			  Read_Search_Replace_Words();
			  System.out.println("after read_search_replace_words");
			  Process_eMail();
			  System.out.println("after process_email");
				store.close();
				Write_track_dt();
				System.out.println("all processing done, successful exit");
			  
			  
			 // file_status = g.LogFileWriter(debug_file_location,debug_file_name,"\n ****End of Processing ****\n",1, debug,0);
			} catch (Exception e) { 
				System.out.println("Threw exception at main expection");
				e.printStackTrace();
			} // exception
	}
	
	private static void Initialize() {
		// TODO Auto-generated method stub
		 System.out.println("inside initialize =");
		props = System.getProperties();
		  //  pd = new ylib.getPropertyInfo();
			try {
				System.out.println("at try");
				pd = g.getPropertyInfo();
				debug = (String)pd.get("debug");
				debug_file_location = (String)pd.get("debug_file_location");
				debug_file_name = (String)pd.get("debug_file_name") + "_"+ check_subject_pattern;
			    output_file_location =(String)pd.get("output_file_location");
			    System.out.println("after output location=" + debug_file_location);
			   // output_file_name = (String)pd.get("output_file_name");
			    //check_subject_pattern = (String)pd.get("check_subject_pattern");
			    
			    System.out.println("Debug_file_location in Initialize=" + debug);
				System.out.println("Debug_file_location in Initialize=" + debug_file_location);
				System.out.println("Debug_file_name in Initialize=" + debug_file_name);
				System.out.println("output_file_location in Initialize=" + output_file_location);
				System.out.println("output_file_name in Initialize=" + output_file_name);
				System.out.println("check_subject_pattern=" + check_subject_pattern);
				
				file_status = g.LogFileWriter(debug_file_location,debug_file_name,"\n ****Begin of Processing ****\n",0, debug);
				file_status = g.LogFileWriter(debug_file_location,debug_file_name,"\n Entered into Initialize\n",1, debug);
				
				FileLocation = System.getenv("FETCH_PROP_PATH");
				System.out.println("File location=" + FileLocation);
			//	file_status = g.LogFileWriter(debug_file_location,debug_file_name,"\n Entered into Initialize\n",1, debug);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				System.out.println("Exception at Initialize exception 1\n");
				e1.printStackTrace();
			} 
			
		  
		  try {
			System.out.println("at try catch");
			file_status = g.LogFileWriter(debug_file_location,"tracking.log ","\n Entered into sent_dt_str1\n",1, debug);
				
			Sent_Dt_Str1 = Read_Last_Dt(FileLocation,"track_dt");
			
			file_status = g.LogFileWriter(debug_file_location,debug_file_name,"\n exiting into sent_dt_str1 " + Sent_Dt_Str1 + "\n",1, debug);
			
		} catch (Exception e2) {
			// TODO Auto-generated catch block
			System.out.println("Exception at Initialize exception 2\n");
			e2.printStackTrace();
		}
		  System.out.println("Sent_Dt_str1=" + Sent_Dt_Str1);
		  //Sent_Dt_Str1 = "Mon Dec 02 00:00:00 PDT 2013";
		   cal = Calendar.getInstance();
		   cal1 = Calendar.getInstance();
		  try {
			Sent_Dt1 = (Date)formatter.parse(Sent_Dt_Str1);
			cal1.setTime(Sent_Dt1);
		} catch (ParseException e1) {
			System.out.println("Exception at Initialize exception 3\n");
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}  // catch
		  try {
		fw = new FileWriter(output_file_location + "/" + output_file_name);
		  } catch (Exception e) {
			  System.out.println("Unable to initialize the file " + output_file_location + "/" + output_file_name );
		  }
		
	} // Initialize	
	public static void Connect_Mail(String MailStore, String MailMap) throws Exception {
	

		//file_status = g.LogFileWriter(debug_file_location,debug_file_name,"\n Entering Connect_Mail\n",1, debug);
	
	  props.setProperty("mail.store.protocol", "imaps");
	  try {
	    session = Session.getDefaultInstance(props, null);
	    store = session.getStore("imaps");
	    System.out.println("store=" + store);
	  
	  
	    store.connect((String)pd.get("mailserver"), (String)pd.get("mailuser"), (String)pd.get("mailpasswd"));
	  
	    System.out.println("mailserver=" + (String)pd.get("mailserver") + " mailuser=" + (String)pd.get("mailuser"));
	    
	     folder = store.getDefaultFolder().list(); // get the array folders in server
	  } catch (Exception e) {

	 file_status = g.LogFileWriter(debug_file_location,debug_file_name,"\n Entering Exception of Connect_mail\n",1, debug);

	  }
      //file_status = g.LogFileWriter(debug_file_location,debug_file_name,"\n Exiting connect mail\n",1, debug);

} // END OF CONNECT_MAIL

private static void Find_Subject_Content_Count() throws Exception {
		sub_rec_count=0;
		 con_rec_count=0;	
		 fr = new FileReader(FileLocation + "/Subject_pattern.txt"); 
		 br = new BufferedReader(fr); 
		 String s=""; 
		  try {
			  fr = new FileReader(FileLocation + "/Subject_pattern.txt"); 
			  br = new BufferedReader(fr); 
		  while((s = br.readLine()) != null) { 
		if(s.substring(0,1).compareTo("#") != 0) {
			  if (s.substring(0, 7).compareTo("Subject")==0) {
				  sub_rec_count = sub_rec_count + 1;
			  } else {
				  con_rec_count = con_rec_count + 1;
			  }
		    }
		  } // for (s.substring(0,1).compareTo("#") 
		  fr.close(); 
		  } catch (Exception e) {
			  file_status = g.LogFileWriter(debug_file_location,debug_file_name, "Error at Find_Subject_Content_Count exception \n" ,1,debug);
		  }
} // Find_Subject_Content_Count
private static void Read_Subject_Content_Pattern() throws Exception {
	//file_status = g.LogFileWriter(debug_file_location,debug_file_name, "Entered into Read_Subject_Content_Pattern \n" ,1,debug);
	fr = new FileReader(FileLocation + "/Subject_pattern.txt"); 
	 br = new BufferedReader(fr); 
	 int k=0,l=0;
	 String s="";
	  
	  Subject_Pattern = new String[sub_rec_count];
	  Content_Pattern = new String[con_rec_count];
	  try {
		  
	  
	  while((s = br.readLine()) != null) { 
		 // System.out.println("7 char=" + s.substring(0, 7) + " s=" + s + " sub_rec_count=" + sub_rec_count);
		  if(s.substring(0,1).compareTo("#") != 0) {
			if (s.substring(0, 7).compareTo("Subject")==0) {
				//System.out.println("entered into if\n");
				Subject_Pattern[k]=s;
				System.out.println("subject_pattern[" + k + "]=" + Subject_Pattern[k]);
		//		file_status = g.LogFileWriter(debug_file_location,debug_file_name, "Entered into open file for subject_Pattern[" + k + "]=" + Subject_Pattern[k] + "\n" ,1,debug);
				k++;
			} else {
				//System.out.println("entered into else\n");
				if (s.substring(0, 7).compareTo("Content")==0) {
					Content_Pattern[l]=s;
					//System.out.println("in subject_pattern");
			//		file_status = g.LogFileWriter(debug_file_location,debug_file_name, "Entered into open file for Content_Pattern[" + l + "]=" + Content_Pattern[l] + "\n" ,1,debug);
					l++;
				}
				
			}
		  } // for (s.substring(0,1).compareTo("#") 
			
		//	System.out.println("sub_rec count=" + sub_rec_count);
		//	System.out.println("con_rec count=" + con_rec_count);
		} // while 
		//System.out.println("now s=" +Last_Dt); 
			fr.close(); 
	
} catch (Exception e) {
	 file_status = g.LogFileWriter(debug_file_location,debug_file_name, "Error at Read_Subject_Content_Pattern exception\n" ,1,debug);
	 
}		
	 // file_status = g.LogFileWriter(debug_file_location,debug_file_name, "Exiting Read_Subject_Content_Pattern \n" ,1,debug);
}
private static void Find_Variable_Replace_Words() throws Exception {
	fr = new FileReader(FileLocation + "/Variable_Replace.txt"); 
	 br = new BufferedReader(fr); 
	 String s="";
	 try {
		  
		  System.out.println("entered into find_search_replace_words");
		  while((s = br.readLine()) != null) { 
			  if(s.substring(0,1).compareTo("#") != 0) {
			  variable_replace_word_count ++;
			  } // for (s.substring(0,1).compareTo("#") 
			} // while 
			//System.out.println("now s=" +Last_Dt); 
				fr.close(); 
		
	} catch (Exception e) {
		// file_status = g.LogFileWriter(debug_file_location,debug_file_name, "Error at find_search_replace_words exception\n"  ,1,debug);
		 e.printStackTrace();
		 
	}		
}
private static void Read_Variable_Replace_Words() throws Exception {
	//file_status = g.LogFileWriter(debug_file_location,debug_file_name, "Entered into Find_variable_Pattern \n" ,1,debug);
	fr = new FileReader(FileLocation + "/Variable_Replace.txt"); 
	 br = new BufferedReader(fr); 
	 int k=0;
	 String s="";
	 try {
		  
		 Variable_Replace_Text = new String [variable_replace_word_count];
		 try {
		  while((s = br.readLine()) != null) { 
			  if(s.substring(0,1).compareTo("#") != 0) {
			  Variable_Replace_Text[k] = s;
			  k++;
		  } // for (s.substring(0,1).compareTo("#") 
			} // while
		 } catch (Exception e) {
			 
		 }
		  for(k=0;k< variable_replace_word_count;k++) {
			  System.out.println("Variable_Replace_Text[" + k + "]=" + Variable_Replace_Text[k]);  
		  }
			// 
				fr.close(); 
		
	} catch (Exception e) {
		 file_status = g.LogFileWriter(debug_file_location,debug_file_name, "Error at Variable_Replace_Text exception\n" ,1,debug);
		 e.printStackTrace();
	}		
}
private static void Find_Search_Replace_Words() throws Exception {
	fr = new FileReader(FileLocation + "/Search_Replace_Words.txt"); 
	 br = new BufferedReader(fr); 
	 String s="";
	 try {
		  
		  System.out.println("entered into find_search_replace_words");
		  while((s = br.readLine()) != null) { 
			 if(s.substring(0,1).compareTo("#") != 0) {
			  search_replace_word_count ++;
			  } // for (s.substring(0,1).compareTo("#") 
			} // while 
			//System.out.println("now s=" +Last_Dt); 
				fr.close(); 
		
	} catch (Exception e) {
		// file_status = g.LogFileWriter(debug_file_location,debug_file_name, "Error at find_search_replace_words exception\n"  ,1,debug);
		 e.printStackTrace();
		 
	}		
}
private static void Read_Search_Replace_Words() throws Exception {
	//file_status = g.LogFileWriter(debug_file_location,debug_file_name, "Entered into Find_variable_Pattern \n" ,1,debug);
	fr = new FileReader(FileLocation + "/Search_Replace_Words.txt"); 
	 br = new BufferedReader(fr); 
	 int k=0;
	 String s="";
	 try {
		  
		 Find_Replace_Text = new String [search_replace_word_count];
		 try {
		  while((s = br.readLine()) != null) { 
			if(s.substring(0,1).compareTo("#") != 0) {
			  Find_Replace_Text[k] = s;
			  k++;
			  } // for (s.substring(0,1).compareTo("#") 
			} // while
		 } catch (Exception e) {
			 
		 }
		  for(k=0;k< search_replace_word_count;k++) {
			  System.out.println("Find_Replace_Text[" + k + "]=" + Find_Replace_Text[k]);  
		  }
			// 
				fr.close(); 
		
	} catch (Exception e) {
		 file_status = g.LogFileWriter(debug_file_location,debug_file_name, "Error at Find Replace Text exception\n" ,1,debug);
		 e.printStackTrace();
	}		
}

private static void Find_variable_Pattern() throws Exception {
	//file_status = g.LogFileWriter(debug_file_location,debug_file_name, "Entered into Find_variable_Pattern \n" ,1,debug);
	fr = new FileReader(FileLocation + "/variable_pattern.txt"); 
	 br = new BufferedReader(fr); 
	 String s="";
	 try {
		  
		  
		  while((s = br.readLine()) != null) { 
			  if(s.substring(0,1).compareTo("#") != 0) {
			  var_rec_count ++;
			  } // for (s.substring(0,1).compareTo("#") 
			} // while 
			//System.out.println("now s=" +Last_Dt); 
				fr.close(); 
		
	} catch (Exception e) {
		 file_status = g.LogFileWriter(debug_file_location,debug_file_name, "Error at Find_variable_Pattern exception\n" ,1,debug);
		 
	}		
} //Find_variable_Pattern

private static void Read_Variables() throws Exception {
	//file_status = g.LogFileWriter(debug_file_location,debug_file_name, "Entered into Find_variable_Pattern \n" ,1,debug);
	fr = new FileReader(FileLocation + "/variable_pattern.txt"); 
	 br = new BufferedReader(fr); 
	 int k=0;
	 String[] s_array = null;
	 String s="";
	 try {
		  
		 Variable_Pattern = new String [var_rec_count];
		 Variable_Pattern_Backup = new String [var_rec_count];
		  while((s = br.readLine()) != null) { 
			  if(s.substring(0,1).compareTo("#") != 0) {
			   s_array = s.split("\\|");
			  Variable_Pattern[k] = s_array[1];
			  Variable_Pattern_Backup[k]=s_array[1];
			  k++;
			  } // for (s.substring(0,1).compareTo("#") 
			} // while 
		  for(k=0;k< var_rec_count;k++) {
			  System.out.println("Variable_Pattern[" + k + "]=" + Variable_Pattern[k]);  
		  }
			// 
				fr.close(); 
		
	} catch (Exception e) {
		 file_status = g.LogFileWriter(debug_file_location,debug_file_name, "Error at Find_variable_Pattern exception\n" ,1,debug);
		 
	}		
}
public static void Process_eMail() throws Exception {
	int i, search_replace_check=0;
	int sIndex=0, eIndex=0, sbjct_length=0;
	String[] sr_var=null;
        System.out.println("debug_file_location = " + debug_file_location);
		//file_status = g.LogFileWriter(debug_file_location,debug_file_name,"\n entering Process_eMail\n",1, debug);

	try {
	for(i=0;i < (folder.length)-1;i++) {
		    System.out.println("i=" + i);
		folder[i].open(Folder.READ_ONLY);
		term = new ReceivedDateTerm(ComparisonTerm.GT,Sent_Dt1);
		Message[] message = folder[i].search(term);
		System.out.println("i=" + i + " message.length=" + message.length);
		for (int j = 0; j < message.length; j++) {	
			sbjct_length=0;
			content="";
			content1="";
			//file_status = g.LogFileWriter(debug_file_location,debug_file_name,"\n**** Processing message " + j + " ***** \n",1, debug);
		    //file_status = g.LogFileWriter(debug_file_location,debug_file_name,"\n message count=" + j + "\n",1, debug);

			//System.out.println("message count=" + j);
			
			sbjct_length=0;		
		    sjbct = message[j].getSubject();
		    
		   
		    //System.out.println("Subject=" + sjbct);
		    Sent_Dt = message[j].getSentDate();
		    Sent_Dt_Str = Sent_Dt.toString();
		    System.out.println("j=" + j + "/" + message.length + " mail_date=" + Sent_Dt + " --> Subject=" + sjbct  + "\n");
		   // file_status = g.LogFileWriter(debug_file_location,debug_file_name,"Subject=" + sjbct + "\n",1, debug);
		    try {
				Sent_Dt = (Date)formatter.parse(Sent_Dt_Str);
			} catch (ParseException e) {
				
				e.printStackTrace();
				//System.out.println("Error at sent_dt");
			// file_status = g.LogFileWriter(debug_file_location,debug_file_name,"\n exception at sent_dt = (date)formatter.parse" + j + "\n",1, debug);

			}  // for sent_dt = (Date)
		    	
		    cal.setTime(Sent_Dt);
		    
		    // System.out.println("cal=" + Sent_Dt + " cal1=" + Sent_Dt1);
		     
			if (cal.compareTo(cal1) > 0) {
				// file_status = g.LogFileWriter(debug_file_location,debug_file_name,"\n subject=" + sjbct + "\n",1, debug);
 				//file_status = g.LogFileWriter(debug_file_location,debug_file_name,"\n cal=" + Sent_Dt_Str + " cal1=" + Sent_Dt_Str1 + "\n",1, debug);

               // System.out.println("entered into cal.compareto(cal1) > 0) ");
				try {
					//sbjct_length=sjbct.length();
				if(sjbct.equals(null)) {
					sbjct_length=0;
				} else {
					sbjct_length=sjbct.length();
				}
				
				//file_status = g.LogFileWriter(debug_file_location,debug_file_name,"\n 1 --> subject=" + sjbct + "   sbjct_length=" + sbjct_length + "\n",1, debug);
				if(sbjct_length > 0  ) {
		    	Notification_Flag = Check_Subject(sjbct);
		    	//file_status = g.LogFileWriter(debug_file_location,debug_file_name,"\n 2 --> subject=" + sjbct + "   sbjct_length=" + sbjct_length + "\n",1, debug);
		    	try {
		    	if(check_subject_pattern.compareTo("ALL")==0) {
		    		check_subject_pattern_int = Notification_Flag;
		    	} else {
		    		check_subject_pattern_int = Integer.parseInt(check_subject_pattern);
		    	}
		    	} catch (Exception e) {
		    		check_subject_pattern_int = Notification_Flag;
		    	}
		    	//file_status = g.LogFileWriter(debug_file_location,debug_file_name,"check_subject_pattern_int=" + check_subject_pattern_int + "\n",1,debug);
		    	if(Notification_Flag > 0  && Notification_Flag == check_subject_pattern_int ) {
		    	
		    	file_status = g.LogFileWriter(debug_file_location,debug_file_name,"mail_date=" + Sent_Dt + "\nNotification_flag for subject : " + sjbct + " --> " + Notification_Flag + "\n",1,debug);
		    	content = message[j].getContent().toString();
		    	}
		    	//file_status = g.LogFileWriter(debug_file_location,debug_file_name,"\n 3 --> subject=" + sjbct + "   sbjct_length=" + sbjct_length + "\n",1, debug);
		    	 if(Notification_Flag > 0  && Notification_Flag == check_subject_pattern_int) {
		    	
		    		//Get_Content_Info(Notification_Flag);
		    		//file_status = g.LogFileWriter(debug_file_location,debug_file_name,"content=" + content + "\n",1,debug);
		    		 try {
				    	 // if(message[j].getContent().equals("multipart")) {
		    			 System.out.println("j=" + j + " mail date=" + Sent_Dt);
		    			 Mail_Content = message[j].getContent();
		    			 if (Mail_Content instanceof String)  
		    			 {  
		    			     content1 = "abcd" + (String)Mail_Content;  
		    			 }  
		    			 else if (Mail_Content instanceof Multipart)  
		    			 {  
		    				 multipart = (Multipart)message[j].getContent();
		    			  
				    		  
		    			 
				    		 // System.out.println("Multipart=" + multipart);
				    		  plainTextPart = multipart.getBodyPart(1);
				    		//  System.out.println("Plaintext=" + plainTextPart);
						      content1 = new String(plainTextPart.getContent().toString());
						      content1 = "abcd " + content1;
						    //  file_status = g.LogFileWriter(debug_file_location,debug_file_name,"content1=" + content1 + "\n",1,debug);
						      //noHTMLString = Parse_Text(content1, Notification_Flag);
				    	//  } else {
				    	//	  content1 = content;
				    	//  }
		    			 } // content
				      } catch (Exception e) {
				    	  file_status = g.LogFileWriter(debug_file_location,debug_file_name,"Entered into exception of multipart" + "\n",1,debug);
				    	  try {
				    	  plainTextPart = multipart.getBodyPart(0);
			    		 
					      content1 = new String(plainTextPart.getContent().toString());
					      content1 = "abcd " + content1;
				    	  } catch (Exception e1) {
				    		  file_status = g.LogFileWriter(debug_file_location,debug_file_name,"Entered into plaintext " + e +"\n",1,debug);
				    	  }
					    //  file_status = g.LogFileWriter(debug_file_location,debug_file_name,"content1=" + content1 + "\n",1,debug);
					    //  System.out.println("content1=" + content1);
					      
				    	//  file_status = g.LogFileWriter(debug_file_location,debug_file_name,"\ncontent1=" + content1 + "\n",1, debug);
					     //  System.out.println("ERRROR 2");
					       //e.printStackTrace();
					    }
		    	
		    		 try {
		    				line =  content1;
		    			    search = "<style type=\"text/css\">";
		    			    
		    			    
		    			     sIndex = line.indexOf(search);
		    			     file_status = g.LogFileWriter(debug_file_location,debug_file_name,"sIndex=" + sIndex + "  eIndex=",1,debug);
		    			     if(sIndex >= 0) {
		    			      eIndex = line.indexOf("</style>", sIndex + search.length());
		    			      file_status = g.LogFileWriter(debug_file_location,debug_file_name,  eIndex + "\n",1,debug);
		    			     if(eIndex >= 0) {
		    			   //  System.out.println("css Style=" + line.substring(sIndex + search.length(), eIndex));
		    			     content1 = line.substring(0,sIndex - 1 )  + line.substring((eIndex + 1),line.length());
		    			    // file_status = g.LogFileWriter(debug_file_location,debug_file_name,"content1=" + content1,1,debug);
		    			     }
		    			     }
		    			//     file_status = g.LogFileWriter(debug_file_location,debug_file_name,"temp_html=" + content1,1,debug);
		    				} catch (Exception e) {
		    					file_status = g.LogFileWriter(debug_file_location,debug_file_name,"Entered into parse_text_exception",1,debug);	
		    				}
		    		 
		    		  content1 = content1.replaceAll("<(\\S+)[^>]+?mso-[^>]*>.*?</\\1>","");
		    		  content1 = content1.replaceAll("\t"," ");
				      content1 = content1.replaceAll("\\<.*?>"," ");
				      content1 = content1.replaceAll("(?s)<!--.*?-->", "");
				      content1 = content1.replaceAll("\\|"," ");
				      content1 = content1.replaceAll("\'","");
				      content1 = content1.replaceAll("\\[#","");
				      content1 = content1.replaceAll("\\#]","");
				      content1 = content1.replaceAll("\"","");
				      content1 = content1.replaceAll(""," ");
				      content1 = content1.replaceAll("  "," ");
				      content1 = content1.replaceAll("  "," ");
				      content1 = content1.replaceAll("  "," ");
				      content1 = content1.replaceAll("  "," ");
				      content1 = content1.replaceAll("\\?"," ");
				      content1 = content1.replaceAll("\n"," ");
				      content1 = content1.replaceAll("&nbsp;","");
				      content1 = content1.replaceAll("&amp;","");
				      content1 = content1.replaceAll("body\\s*\\{.*?\\}", "");
				      content1 = content1.replaceAll("(\\r|\\n|\\r\\n)+", "");
				     
				      //content1 = content1.replaceAll("\\t","");
				     // content1 = content1.replaceAll("[^a-zA-Z0-9\\!\\@\\#\\$\\%\\^\\&\\*\\(\\)\\-\\_\\+\\=\\}\\{\\]\\[\\;\\:\\'\\.\\,\\|\\>\\<\\/\\?\\s]", " ");
				     // content1 = content1.replaceAll("  "," ");
				      try {
				      for (search_replace_check=0; search_replace_check < search_replace_word_count ; search_replace_check ++ ) {
				    	sr_var =  Find_Replace_Text[search_replace_check].split("\\|");
				    	if ((Integer.parseInt(sr_var[0])) == Notification_Flag) {
				    		if(sr_var[3].toUpperCase().compareTo("N")==0) {
				    			content1 = content1.replaceAll(sr_var[1],sr_var[2].trim());
				    		} else {
				    		content1 = content1.replaceAll(sr_var[1],"|" + sr_var[2].trim());
				    		}
				    		//file_status = g.LogFileWriter(debug_file_location,debug_file_name,"\n --> sr_var[0]=" + sr_var[0] + " notification_flag=" + Notification_Flag  ,1, debug);
				    	}
				    	
				      }
				      } catch (Exception e) {
				    	  file_status = g.LogFileWriter(debug_file_location,debug_file_name,"Exception in search replace\n",1,debug); 
				      }

				      noHTMLString = Parse_Text(content1, Notification_Flag);
				      }
			} // for sjbct > 0 
				} catch (Exception e) {
					file_status = g.LogFileWriter(debug_file_location,debug_file_name,"Exception for subject > 0" + sjbct + " --> "  + " Error=" + e + "\n",1,debug);
				}
				//else {
				    	  // for amazon platform_flag 
				    	 // noHTMLString = Parse_Text(content, Notification_Flag);
				      //}
				      
				      //Prepare_Data(noHTMLString, Notification_Flag);
				  //if(Notification_Flag == 45 ) {
				    if(Notification_Flag > 0 && Notification_Flag == check_subject_pattern_int) {
				    	try {
							Fix_Variables(Notification_Flag);
							} catch (Exception e) {
								file_status = g.LogFileWriter(debug_file_location,debug_file_name,"Exception for Fix Variables",1,debug);
							}
		    		Print_Variables();	
				   }
		    	}
		    		
		    	//System.out.println("Notification_flag for subject :" + sjbct + " --> " + Notification_Flag);
		    //	System.out.println("entered into cal.compateTo(cal1) > 0 and Notification_flag=" + Notification_Flag);
		   
			
		   
		 //   System.out.println("completed " + j + " message");
		    //file_status = g.LogFileWriter(debug_file_location,debug_file_name,"\n**** End of Processing message " + j + " ***** \n",1, debug);
		    } // for message
		//System.out.println("after message");
		    } // for folder
		   // System.out.println("before folder close");

	//file_status = g.LogFileWriter(debug_file_location,debug_file_name,"\ntrack_dt " + Sent_Dt_Str + " ***** \n",1,debug);
    //File_Flag = Write_Last_Dt(FileLocation,"track_dt",Sent_Dt_Str);
    //file_status = g.LogFileWriter(debug_file_location,debug_file_name,"\n Last line of the Main program.  Everything is success \n",1,debug);
    //System.out.println("Success");
		   try {
		//    folder[i].close(true);
		    } catch (ArrayIndexOutOfBoundsException  e) {
		    	file_status = g.LogFileWriter(debug_file_location,debug_file_name,"\n**** Folder Error " + i + " ***** \n",1,debug);
		    } // catch
		    
		    
	 } catch (NoSuchProviderException e) {
		    e.printStackTrace();

				file_status = g.LogFileWriter(debug_file_location,debug_file_name,"\n NoSuchProviderException\n",1,debug);

		   // System.exit(1);
		   } catch (MessagingException e) {

			file_status = g.LogFileWriter(debug_file_location,debug_file_name,"\n MessagingException\n",1,debug);

		     e.printStackTrace();
		    // System.exit(2);
		   } catch (IOException e) {
			// TODO Auto-generated catch block

			file_status = g.LogFileWriter(debug_file_location,debug_file_name,"\n IOException\n",1,debug);

			e.printStackTrace();
			System.exit(3);
		} // catch
	//System.out.println("exiting process_email");

} // Process_eMail()

	public static String Read_Last_Dt(String FileLocation, String FileName) throws Exception { 
		System.out.println("Read_Last_dt function");
		  FileReader fr = new FileReader(FileLocation + "/" + FileName); 
		  BufferedReader br = new BufferedReader(fr); 
		  String s; 
		  String Last_Dt=null;
		  String Last_Dt_arr[] = null;
			while((s = br.readLine()) != null) { 
				Last_Dt =  s;
				Last_Dt_arr = Last_Dt.split("\\|");
				System.out.println("last_dt_arr[0]=" + Last_Dt_arr[0] + " Last_Dt_arr[1]=" + Last_Dt_arr[1]);
				if (Last_Dt_arr[0].compareTo(check_subject_pattern)==0) {
					Last_Dt = Last_Dt_arr[1];
					file_status = g.LogFileWriter(debug_file_location,debug_file_name,"Last_Dt for " + check_subject_pattern + "=" + Last_Dt + "\n",1,debug);
					break;
				}
				//System.out.println("s=" + s); 
			} // while 
			//System.out.println("now s=" +Last_Dt); 
				fr.close(); 
				 return Last_Dt;
} // Read_Last_Dt 
	public static void Initialize_Variables() throws Exception {
		//file_status = g.LogFileWriter(debug_file_location,debug_file_name,"Entered into  Initialize Variables\n",1,debug);	
		int i=0;
		try {
			for (i=0;i < var_rec_count; i++) {
				 Variable_Pattern[i] =  Variable_Pattern_Backup[i];
			}
		//	file_status = g.LogFileWriter(debug_file_location,debug_file_name,"Exiting  Initialize Variables\n",1,debug);	
		}catch (Exception e) {
			file_status = g.LogFileWriter(debug_file_location,debug_file_name,"Entered into exception Initialize Variables\n",1,debug);	
		}
	}
public static int Check_Subject(String Msg) throws Exception {
	
	int index_value=0;
	int z=0;
	int return_val=-1;
	String in_Msg = Msg;
	String [] sr_var=null;
	int search_replace_check=0;
	
	
	
	 int rec_count=0;
	 int index_count=0;
	  String s; 
	  Initialize_Variables();
	  try {
	      for (search_replace_check=0; search_replace_check < search_replace_word_count ; search_replace_check ++ ) {
	    	sr_var =  Find_Replace_Text[search_replace_check].split("\\|");
	    	if ((Integer.parseInt(sr_var[0])) == 0) {
	    		
	    			in_Msg = in_Msg.replaceAll(sr_var[1],sr_var[2].trim());
	    		   
	    		    	
	    		    
	    		
	    	} else { 
	    		break;
	    	}
	    	
	      }
	     // file_status = g.LogFileWriter(debug_file_location,debug_file_name,"\n --> Msg=" + Msg + " --> in_msg=" + in_Msg   ,1, debug);
	      } catch (Exception e) {
	    	  file_status = g.LogFileWriter(debug_file_location,debug_file_name,"Exception in search replace",1,debug); 
	      }
	try {
		//file_status = g.LogFileWriter(debug_file_location,debug_file_name,"Subject=" + Msg + "\n",1,debug);
		for (rec_count=0; rec_count < sub_rec_count;rec_count ++ ) {
			Subject_Pattern_Split_Array = Subject_Pattern[rec_count].split("\\|");
			Subject_Array = in_Msg.split("\\s+");
			What_2_Check_Src = Subject_Pattern_Split_Array[4];
		//	file_status = g.LogFileWriter(debug_file_location,debug_file_name,"   --> What to Check source=" + What_2_Check_Src + "\n",1,debug);
			//System.out.println("what_2_check_src=" + What_2_Check_Src);
			Subject_Pattern_Id=Subject_Pattern_Split_Array[3];
			if (Subject_Pattern_Id.indexOf("last") > 0) {
				file_status = g.LogFileWriter(debug_file_location,debug_file_name,"   Entered into Subject_patter_id contains last" + Subject_Pattern_Id + "\n",1,debug);
				//Subject_Pattern_Id = Subject_Pattern_Id.replace("last",Subject_Pattern_Split_Array[4].length());
			}
		//	file_status = g.LogFileWriter(debug_file_location,debug_file_name,"   --> Subject Pattern ID=" + Subject_Pattern_Id + "\n",1,debug);
			//System.out.println("Subject_Pattern_Id=" + Subject_Pattern_Id);
			Subject_Pattern_Id_Array = Subject_Pattern_Id.split(",");
			
		//	file_status = g.LogFileWriter(debug_file_location,debug_file_name,"   --> Subject Array Length=" + Subject_Array.length + " Subject_Pattern_Split_Array_Length=" + Subject_Pattern_Split_Array.length + "\n",1,debug);
			try {
			for (index_count=0;index_count < (Subject_Pattern_Id_Array.length) ; index_count ++) {
				index_value = Integer.parseInt(Subject_Pattern_Id_Array[index_count]);
				//System.out.println("Entered into for loop for index_value=" + index_value + "\n");
				if(index_count==0) {
					What_2_Check = Subject_Array[index_value];
				//	System.out.println("What_2_Check=" + What_2_Check );
				} else {
					What_2_Check = What_2_Check + " " + Subject_Array[index_value];
				//	System.out.println("What_2_Check=" + What_2_Check );
				}
				
			}
			} catch (Exception e) {
				//file_status = g.LogFileWriter(debug_file_location,debug_file_name,"Exeption in splitting what_2_check, but its ok\n",1,debug);
			}
		//	System.out.println("now What_2_check=" + What_2_Check);
			if(What_2_Check.equals(What_2_Check_Src)) {
				System.out.println("Matched " + What_2_Check + "=" + What_2_Check_Src);
				return_val = Integer.parseInt(Subject_Pattern_Split_Array[2]);
			//	file_status = g.LogFileWriter(debug_file_location,debug_file_name,"What_2_Check=" + What_2_Check + "=" + What_2_Check_Src + " Matched\n",1,debug);
                   new_subject = Msg.replaceAll(What_2_Check + " ", "");
                  // file_status = g.LogFileWriter(debug_file_location,debug_file_name,"\n  --> new_subject=" + new_subject + " message pattern=" + Subject_Pattern_Split_Array[5] + "\n",1,debug);
		    		Extract_values(new_subject, Subject_Pattern_Split_Array[5]);
		    		
		    	    
		    		//Print_Variables();
		    		// do nothing.
				break;
			} else {
				//System.out.println("Entered into else compareTo");
				//file_status = g.LogFileWriter(debug_file_location,debug_file_name,"What_2_Check=" + What_2_Check + "=" + What_2_Check_Src + " Not Matched\n",1,debug);
			}
			//}
			
		}
		
		return return_val;
 } catch (Exception e) {
	 file_status = g.LogFileWriter(debug_file_location,debug_file_name, "Error at check_subject for msg=" + in_Msg +  e + "\n" ,1,debug);
	 return -1;
 }
 } // end of check_notification

public static void  Extract_values(String Msg, String Msg_Pattern) throws Exception {
	int k=0,l=0,m=0,a=0,n=0;
	String[] Local_Extract_Values;
	String[] Local_Individual_Variable;
	String[] Local_Individual_Variable1;
	String temp_var="";
	String new_msg="";
	String get_output;
	String clean_output;
	get_output="";
	clean_output="";
	try {
  if(Msg_Pattern.length() > 0 ) {
	//  file_status = g.LogFileWriter(debug_file_location,debug_file_name,"   --->Msg_Pattern= " + Msg_Pattern + "\n",1,debug);
	  Extract_Values_Pattern = Msg_Pattern.split(",");
	  for(k=0;k < Extract_Values_Pattern.length; k++) {
		//  file_status = g.LogFileWriter(debug_file_location,debug_file_name,"Entered into K loop " + k + " -->" + Extract_Values_Pattern[k] + "\n",1,debug);
			 
		  Local_Individual_Variable = Extract_Values_Pattern[k].split("=");
		 // file_status = g.LogFileWriter(debug_file_location,debug_file_name,"value for Local_Individual_variable length=" + Local_Individual_Variable.length  + "\n",1,debug);

		  get_output = Fetch_Value_from_Subject(Msg, Local_Individual_Variable[1].trim());
		  Assign_value_To_Variables(get_output,Local_Individual_Variable[0].trim());
		 			
			   
			  
		  }   //for(k=0
		   
		//  file_status = g.LogFileWriter(debug_file_location,debug_file_name,"--- *** ---\n" +
	//  "  Extract_Values_Pattern[" + k + "]=" + Extract_Values_Pattern[k] + "\n",1,debug); 
	  } //Msg > 0
	  
  
	} catch (Exception e) {
		file_status = g.LogFileWriter(debug_file_location,debug_file_name,"Extract Values exception\n",1,debug);
	}
}
public static String Fetch_Value_from_Subject(String Msg, String Fetch_value) throws Exception {
	String Subject_val = Msg;
	String To_Extract;
	String Split_To_Extract;
	String Start_Pointer;
	String Return_Variable="";
	int i1, j1,k1;
	To_Extract=Fetch_value.toLowerCase();
	//file_status = g.LogFileWriter(debug_file_location,debug_file_name,"Fetch_Value_From_Subject Method: To_Extract=" + To_Extract + "\n",1,debug);
	try {
	try {
	if (To_Extract.compareTo("last") == 0) {
	//	file_status = g.LogFileWriter(debug_file_location,debug_file_name,"Entered into last=" + Subject_Array[Subject_Array.length-1] + "\n",1,debug);
		Return_Variable= Subject_Array[(Subject_Array.length-1)];
		Return_Variable = Return_Variable.trim();
		return Return_Variable;
	}
	} catch (Exception e) {
		return "Error at To_Extract.compareTo(last) on method Fetch_Value_from_Subject " + e;
	}
	try {
	if(To_Extract.indexOf(" ") > 0) {
	//	file_status = g.LogFileWriter(debug_file_location,debug_file_name,"To_Extract.indexOf(space) > 0 " + To_Extract.indexOf(" ") +"\n",1,debug);
		Split_To_Extract = To_Extract.substring(1,To_Extract.length()).trim();
	//	file_status = g.LogFileWriter(debug_file_location,debug_file_name,"---> Split_To_Extract=" + Split_To_Extract +"\n",1,debug);
		
		if (Split_To_Extract.compareTo("to last-1") == 0) {
	//	file_status = g.LogFileWriter(debug_file_location,debug_file_name,"Split_To_Extract.indexOf(to last-1) > 0 " + Split_To_Extract.indexOf(" ") +"\n",1,debug);
		Start_Pointer = To_Extract.substring(0,1).trim();
		i1=Integer.parseInt(Start_Pointer);
		j1=Subject_Array.length-1;
//		file_status = g.LogFileWriter(debug_file_location,debug_file_name,"Value of i=" + i + " value of j=" + j +"\n",1,debug);
		for (k1=i1;k1 < j1; k1 ++) {
			if (k1==i1) {
				Return_Variable = Subject_Array[k1].trim();
			}else {
				Return_Variable = Return_Variable + " " + Subject_Array[k1];
			}
		}  // for k
	} else if (Split_To_Extract.compareTo("to last-2") == 0) {
	//	file_status = g.LogFileWriter(debug_file_location,debug_file_name,"Split_To_Extract.indexOf(to last-2) > 0 " + Split_To_Extract.indexOf(" ") +"\n",1,debug);
		Start_Pointer = To_Extract.substring(0,1).trim();
		i1=Integer.parseInt(Start_Pointer);
		j1=Subject_Array.length-2;
//		file_status = g.LogFileWriter(debug_file_location,debug_file_name,"Value of i=" + i + " value of j=" + j +"\n",1,debug);
		for (k1=i1;k1 < j1; k1 ++) {
			if (k1==i1) {
				Return_Variable = Subject_Array[k1].trim();
			}else {
				Return_Variable = Return_Variable + " " + Subject_Array[k1];
			}
		}  // for k
	} else if (Split_To_Extract.compareTo("to last-3") == 0) {
	//	file_status = g.LogFileWriter(debug_file_location,debug_file_name,"Split_To_Extract.indexOf(to last-3) > 0 " + Split_To_Extract.indexOf(" ") +"\n",1,debug);
		Start_Pointer = To_Extract.substring(0,1).trim();
		i1=Integer.parseInt(Start_Pointer);
		j1=Subject_Array.length-3;
//		file_status = g.LogFileWriter(debug_file_location,debug_file_name,"Value of i=" + i + " value of j=" + j +"\n",1,debug);
		for (k1=i1;k1 < j1; k1 ++) {
			if (k1==i1) {
				Return_Variable = Subject_Array[k1].trim();
			}else {
				Return_Variable = Return_Variable + " " + Subject_Array[k1];
			}
		}  // for k
	} 
	if(To_Extract.indexOf(" to ") > 0 &&  To_Extract.indexOf("last") == 0 ){
		file_status = g.LogFileWriter(debug_file_location,debug_file_name,"To_Extract.indexOf( to ) > 0 &&  To_Extract.indexOf(last) == 0" +"\n",1,debug);	
	}
	if (Split_To_Extract.compareTo("to last") == 0) {
			Start_Pointer = To_Extract.substring(0,1).trim();
			i1=Integer.parseInt(Start_Pointer);
			j1=Subject_Array.length;
			for (k1=i1;k1 < j1; k1++) {
				if (k1==i1) {
					Return_Variable = Subject_Array[k1].trim();
				}else {
					Return_Variable = Return_Variable + " " + Subject_Array[k1];
				}
			}  // for k
	}  // check for to last")
	return Return_Variable;
	} else {  // for just variables
		i1=0;
		i1=Integer.parseInt(To_Extract);
		Return_Variable = Subject_Array[i1];
		return Return_Variable;
	} // for to_extract.indexof
	} catch (Exception e) {
		return "Error at To_Extract.index on method Fetch_Value_from_Subject " + e;
	}
	
	} catch (Exception e)  {
		return "unknown exception in Fetch_Value_from_Subject method";
	}
}

public static void Assign_value_To_Variables(String Fetch_value, String Assign_value) throws Exception {
	
	int i2, j2,k2;

	//file_status = g.LogFileWriter(debug_file_location,debug_file_name," --> Assign_Value_To_Variables Fetch_Value=" + Fetch_value + " Assign_Value=" + Assign_value + "\n",1,debug);
	try {
		Fetch_value = Fetch_value.trim();
		Fetch_value = Fetch_value.replace("\t", "");
		for (i2=0;i2 < var_rec_count; i2 ++){
		// if(Variable_Pattern[i].indexOf("=") == 0) {
		  if (Variable_Pattern[i2].compareTo(Assign_value) ==0 && Fetch_value.length() > 0) {
			  Variable_Pattern[i2] = Variable_Pattern[i2].trim() + "=" + Fetch_value;
			  break;
		  }
			  
		}
		//}
	} catch (Exception e)  {
		
	}
}
public static void Fix_Variables(int in_notification_flag) throws Exception {
	
	int i3, j3, k3, from_var1_postn=0, to_var1_postn=0;
	String[] sr_var=null;
	String from_var=null;
	String to_var=null;
	String from_var1=null;
	String to_var1=null;
	String[] sr_var1=null;
	String[] sr_var1_postn=null;
	String[] sr_var2=null;
	int String_Position=0;
	String to_var1_temp=null;
	String target_value=null;
	
			//System.out.println("entered into fix_variables");
	//file_status = g.LogFileWriter(debug_file_location,debug_file_name,"Enter into fix_variables=" + variable_replace_word_count + "\n",1,debug);
	try {
	for(i3=0; i3 < variable_replace_word_count; i3++) {
//		file_status = g.LogFileWriter(debug_file_location,debug_file_name,"var_replace_text=" + Variable_Replace_Text[i3] + "\n",1,debug);
		sr_var=Variable_Replace_Text[i3].split("\\|");
	//	file_status = g.LogFileWriter(debug_file_location,debug_file_name,"sr_var=" + sr_var.length + "\n",1,debug);
		if ((Integer.parseInt(sr_var[0])) == in_notification_flag) {
			if(sr_var[1].compareTo("remove") == 0) {
				//System.out.println("sr_var[2]=" + sr_var[2]);
				if(sr_var[2].compareTo("value")==0) {
				from_var=sr_var[3];
				to_var=sr_var[4];
				try {
		//			file_status = g.LogFileWriter(debug_file_location,debug_file_name,"var_rec_count=" + var_rec_count + "\n", 1,debug);
				for(j3=0;j3 < var_rec_count; j3++ ) {
				//	file_status = g.LogFileWriter(debug_file_location,debug_file_name,"variable_pattern[" + j3 + "]=" + Variable_Pattern[j3] + "--> " + Variable_Pattern[j3].indexOf("=") + "\n",1,debug);					
					if(Variable_Pattern[j3].indexOf("=") > 0) {
					  	sr_var1=Variable_Pattern[j3].split("=");
			//		  	file_status = g.LogFileWriter(debug_file_location,debug_file_name,"sr_var1[0]=" + sr_var1[0]+ " -->" + sr_var1.length + " --> variable_pattern[" + j3 + "]=" + Variable_Pattern[j3] + "\n",1,debug);
					  	
					  	if(from_var.compareTo(sr_var1[0]) == 0) {
				//	  		file_status = g.LogFileWriter(debug_file_location,debug_file_name,"from_var=" + from_var + "sr_var1[0]=" + sr_var1[0]+ " --> " + sr_var1[1] + "\n",1,debug);
					  		from_var1=sr_var1[1];
					  		from_var1_postn=j3;
				//	  		file_status = g.LogFileWriter(debug_file_location,debug_file_name,"from_var1=" + from_var1 + "sr_var1[1]=" + sr_var1[1]+"\n",1,debug);
					  		break;
					  	}
					  	
					}
				}
				} catch (Exception e) {
					file_status = g.LogFileWriter(debug_file_location,debug_file_name,"For j3=0 loop exception\n" + e +"\n",1,debug);
				}
				try {
				//	file_status = g.LogFileWriter(debug_file_location,debug_file_name,"var_rec_count=" + var_rec_count + "\n", 1,debug);
				for(j3=0;j3 < var_rec_count; j3++ ) {
			//		file_status = g.LogFileWriter(debug_file_location,debug_file_name,"variable_pattern[" + j3 + "]=" + Variable_Pattern[j3] + "--> " + Variable_Pattern[j3].indexOf("=") + "\n",1,debug);					
					if(Variable_Pattern[j3].indexOf("=") > 0) {
					  	sr_var1=Variable_Pattern[j3].split("=");
					  	if(to_var.compareTo(sr_var1[0]) == 0) {
					  		to_var1=sr_var1[1];
					  		to_var1_postn=j3;
					  		break;
					  	}
					}
				}
				} catch (Exception e) {
					file_status = g.LogFileWriter(debug_file_location,debug_file_name,"For j3=0 2nd loop exception\n" + e +"\n",1,debug);
				}
				try {
				for(j3=0;j3 < var_rec_count; j3++ ) {
					if(Variable_Pattern[j3].indexOf("=") > 0) {
					  	sr_var1=Variable_Pattern[j3].split("=");
					  //	file_status = g.LogFileWriter(debug_file_location,debug_file_name,"from_var=" + from_var + "--> to_var=" + to_var1 +"\n",1,debug);
					  	if(from_var.compareTo(sr_var1[0]) == 0) {
					  		//to_var1 = to_var1 + " ";
					  		//to_var1 = to_var1.trim();
					  		Variable_Pattern[j3] = Variable_Pattern[j3].replace(to_var1 + " ", "").trim();
					  		Variable_Pattern[j3] = Variable_Pattern[j3].replace(to_var1, "").trim();
					  		break;
					  	}
					  	
					}
				}
				} catch (Exception e) {
					file_status = g.LogFileWriter(debug_file_location,debug_file_name,"For j3=0 3rd time loop exception\n" + e +"\n",1,debug);
				}
			} 
			if (sr_var[2].compareTo("position")==0) {
				from_var=sr_var[3];
				to_var=sr_var[4];
				from_var1_postn=0;
				try {
				//	file_status = g.LogFileWriter(debug_file_location,debug_file_name,"Position, from_var=" + from_var + " to_var=" + to_var + "\n", 1,debug);
							for(j3=0;j3 < var_rec_count; j3++ ) {
					//			file_status = g.LogFileWriter(debug_file_location,debug_file_name,"variable_pattern[" + j3 + "]=" + Variable_Pattern[j3] + "--> " + Variable_Pattern[j3].indexOf("=") + "\n",1,debug);					
								if(Variable_Pattern[j3].indexOf("=") > 0) {
								  	sr_var1=Variable_Pattern[j3].split("=");
						//		  	file_status = g.LogFileWriter(debug_file_location,debug_file_name,"sr_var1[0]=" + sr_var1[0]+ " -->" + sr_var1.length + " --> variable_pattern[" + j3 + "]=" + Variable_Pattern[j3] + "\n",1,debug);
								  	
								  	if(from_var.compareTo(sr_var1[0]) == 0) {
							//	  		file_status = g.LogFileWriter(debug_file_location,debug_file_name,"from_var=" + from_var + "sr_var1[0]=" + sr_var1[0]+ " --> " + sr_var1[1] + "\n",1,debug);
								  		from_var1=sr_var1[1];
								  		from_var1_postn=j3;
							//	  		file_status = g.LogFileWriter(debug_file_location,debug_file_name,"from_var1=" + from_var1 + "sr_var1[1]=" + sr_var1[1]+"\n",1,debug);
								  		break;
								  	}
								  	
								}
							}
					} catch (Exception e) {
								file_status = g.LogFileWriter(debug_file_location,debug_file_name,"Position exception\n" + e +"\n",1,debug);
					}
			//	file_status = g.LogFileWriter(debug_file_location,debug_file_name,"Position, from_var1=" + from_var1 +  " from_var=" + from_var +"\n", 1,debug);
				sr_var1_postn = from_var1.split(" ");
				to_var1_temp=from_var + "=" ;
				for (j3=0; j3 < sr_var1_postn.length; j3 ++) {
					//if(to_var.compareTo("last")==0) {
					//	j3=sr_var1_postn.length-1;
					//}
					if(to_var.compareTo("last")==0) {
						to_var = String.valueOf((sr_var1_postn.length - 1));
					}
					if(to_var.compareTo("last-1")==0) {
						to_var = String.valueOf((sr_var1_postn.length - 2));
					}
					if(to_var.compareTo("last-2")==0) {
						to_var = String.valueOf((sr_var1_postn.length - 3));
					}
					if(Integer.parseInt(to_var) != j3) {
						to_var1 = sr_var1_postn[j3];
						to_var1_temp = to_var1_temp + to_var1.trim() + " ";
					//	file_status = g.LogFileWriter(debug_file_location,debug_file_name,"Position, to_var1_temp=" + to_var1_temp +  "\n", 1,debug);
						//Variable_Pattern[from_var1_postn] = Variable_Pattern[from_var1_postn].replace(to_var1,"").trim();
						
						//break;
					}
				}
				Variable_Pattern[from_var1_postn] = to_var1_temp.trim();
			} 
			if (sr_var[2].compareTo("text")==0) {  // for text
				//System.out.println("entered into text");
				from_var=sr_var[3];
				to_var=sr_var[4];
				try {
				for(j3=0;j3 < var_rec_count; j3++ ) {
					if(Variable_Pattern[j3].indexOf("=") > 0) {
					  	sr_var1=Variable_Pattern[j3].split("=");
					  	if(from_var.compareTo(sr_var1[0]) == 0) {
					  	//	System.out.println("checking for " + from_var + " sr_var1[0]=" + sr_var1[0] + " --> to_var=" + to_var); 
					  		Variable_Pattern[j3] = Variable_Pattern[j3].replace(to_var,"").trim();
					  	//	Variable_Pattern[j3] = Variable_Pattern[j3].replaceAll(to_var,"").trim();
					  		break;
					  		
					  		
					  	}
					  	
					}
				}
				} catch (Exception e) {
					file_status = g.LogFileWriter(debug_file_location,debug_file_name,"For j3=0 on else loop exception\n" + e +"\n",1,debug);
				}
			} // value
			}
		try {
		if(sr_var[1].compareTo("split") == 0) {
		//	file_status = g.LogFileWriter(debug_file_location,debug_file_name,"Enter into split\n",1,debug);
			from_var = sr_var[2];
			to_var = sr_var[3];
			
			//file_status = g.LogFileWriter(debug_file_location,debug_file_name,"other_col=" + from_var + "-->" + to_var + "--> " + String_Position + "\n",1,debug);
			for(j3=0;j3 < var_rec_count; j3++ ) {
				if(Variable_Pattern[j3].indexOf("=") > 0) {
				  	sr_var1=Variable_Pattern[j3].split("=");
				  	if(from_var.compareTo(sr_var1[0]) == 0) {
				  	  sr_var2 = sr_var1[1].split(" ");
				 // 	file_status = g.LogFileWriter(debug_file_location,debug_file_name,"sr_var2.len=" + sr_var2.length + " --> sr_var[4]=" + sr_var[4] + "\n",1,debug);
				  	if (sr_var[4].compareTo("full") == 0) {
				  		target_value = sr_var1[1];
				  	}else if(sr_var[4].compareTo("last") == 0) {
				  	//	file_status = g.LogFileWriter(debug_file_location,debug_file_name,"sr_var[4]=" + sr_var[4] + "\n",1,debug);
						String_Position= sr_var2.length-1;
						target_value = sr_var2[String_Position];
					} else if(sr_var[4].compareTo("last-1") == 0) {
					  	//	file_status = g.LogFileWriter(debug_file_location,debug_file_name,"sr_var[4]=" + sr_var[4] + "\n",1,debug);
							String_Position= sr_var2.length-2;
							target_value = sr_var2[String_Position];
					} else if(sr_var[4].compareTo("last-1") == 0) {
					  	//	file_status = g.LogFileWriter(debug_file_location,debug_file_name,"sr_var[4]=" + sr_var[4] + "\n",1,debug);
							String_Position= sr_var2.length-3;
							target_value = sr_var2[String_Position];
					} else {
					String_Position= Integer.parseInt(sr_var[4]);
					target_value = sr_var2[String_Position];
					}
				//  	file_status = g.LogFileWriter(debug_file_location,debug_file_name,"String_Position=" + String_Position+ "\n",1,debug);
				  	  
				  //	file_status = g.LogFileWriter(debug_file_location,debug_file_name,"variable_patter[" + j3 + "]=" + Variable_Pattern[j3] + " target_value=" + target_value + "\n",1,debug);
				  			  	
				  	  break;
				  	}
				  		
		 }
		} // for j3
		//	file_status = g.LogFileWriter(debug_file_location,debug_file_name,"after break"  + "\n",1,debug);
			for(j3=0;j3 < var_rec_count; j3++ ) {
				if(Variable_Pattern[j3].indexOf("=") > 0) {
				  	sr_var1=Variable_Pattern[j3].split("=");
				  	if(to_var.compareTo(sr_var1[0]) == 0) {
				  	//	file_status = g.LogFileWriter(debug_file_location,debug_file_name,"entered into if"  + "\n",1,debug);
				  		Variable_Pattern[j3] = Variable_Pattern[j3] + "=" + target_value.trim();
				  		Variable_Pattern[j3] = Variable_Pattern[j3].replace("==", "=");
				  //		file_status = g.LogFileWriter(debug_file_location,debug_file_name,"if Variable_Patter[" + j3 + "]=" + Variable_Pattern[j3] + "\n",1,debug);
				  		break;
				  	}
				} else {
					
					if(to_var.compareTo(Variable_Pattern[j3]) == 0) {
					//	file_status = g.LogFileWriter(debug_file_location,debug_file_name,"entered into else for from_var=" + from_var + " --> to_var=" + to_var  + "\n",1,debug);
				  		Variable_Pattern[j3] = Variable_Pattern[j3] + "=" + target_value;
				  		Variable_Pattern[j3] = Variable_Pattern[j3].replace("==", "=");
				  	//	file_status = g.LogFileWriter(debug_file_location,debug_file_name,"else Variable_Patter[" + j3 + "]=" + Variable_Pattern[j3] + "\n",1,debug);
				  		break;
				  	}
				}
			} // for j3
		} // split
		} catch (Exception e) {
			file_status = g.LogFileWriter(debug_file_location,debug_file_name,"split exeption\n" + e +"\n",1,debug);	
		}
		} // notification_flag
	}
	} catch (Exception e) {
		file_status = g.LogFileWriter(debug_file_location,debug_file_name,"Entered into fix_variable execption\n" + e +"\n",1,debug);
	}
}

public static String Parse_Text(String Msg,  Integer notification_flag) throws Exception {
	String temp_html=Msg;
	String line=null;
	int sIndex=0, eIndex=0;
	int i3, j3, k3;
	String Content_Array_Str=null;
	String Content_Array_Str1=null;
	String[] Content_Array = null;
	String[] Content_Array1 = null;
	String[] Content_NV_Pair = null;
	String Cont_Repl_Str=null;
	String Cont_Srch_Str=null;
	String[] Value_NV_Pair=null;

	
		file_status = g.LogFileWriter(debug_file_location,debug_file_name,"Entered into Parse_Text begin \n" + Msg +"\n",1,debug);

	
	  try {
		  Content_Array = Msg.split("\\|");
		  
		for(i3 = 0; i3 < Content_Array.length ; i3 ++) {
		//	file_status = g.LogFileWriter(debug_file_location,debug_file_name,"Content_Array[" + i3 + "]=" + Content_Array[i3].trim() + " -->" + Content_Array[i3].trim().indexOf("=") + "\n" ,1,debug);
			if(Content_Array[i3].trim().indexOf("=") > 0) {
			Content_Array1 = Content_Array[i3].split("=");
			
			for(j3=0; j3 < var_rec_count ; j3++) {	
			if (Variable_Pattern[j3].indexOf("=") > 0) {
			} else {
				Assign_value_To_Variables(Content_Array1[1].trim(), Content_Array1[0].trim());
				
			}
			
			}
		}
		}
	  } catch (Exception e) {
				file_status = g.LogFileWriter(debug_file_location,debug_file_name,"Exception for content_Array1" + e + "\n",1,debug);	
				e.printStackTrace();
			}
		
				
			

		//file_status = g.LogFileWriter(debug_file_location,debug_file_name,"Temp Html=" + temp_html + "\n",1,debug);
	//	file_status = g.LogFileWriter(debug_file_location,debug_file_name," --> Content_Array_Str1=" + Content_Array_Str1 + "\n",1,debug);	
		
     return temp_html;
}

public static void Write_track_dt() throws Exception {
	String s="";
	String Last_Dt_arr[] = null;
	String to_write=null;
	String Last_Dt_arr1[]=null;
	int count=-1;
	int i=0;
	Last_Dt_arr1 = new String[sub_rec_count+1];
	file_status = g.LogFileWriter(debug_file_location,debug_file_name,"Entered into write_track_dt \n" + FileLocation +"=" + sub_rec_count + "\n",1,debug);



  try {
	  FileReader fr = new FileReader(FileLocation + "/" + "track_dt"); 
	  BufferedReader br = new BufferedReader(fr); 
      file_status = g.LogFileWriter(debug_file_location,debug_file_name,"After Buffer Reader \n" + FileLocation + "\n",1,debug);

	  while((s=br.readLine())!=null)
      {
		count ++;
        Last_Dt_arr=s.split("\\|");
		file_status = g.LogFileWriter(debug_file_location,debug_file_name,"Check Subject Pattern " + check_subject_pattern  + "\n",1,debug);   
		//file_status = g.LogFileWriter(debug_file_location,debug_file_name,"Last_Dt for " +  s + "\n",1,debug);
		//file_status = g.LogFileWriter(debug_file_location,debug_file_name,"arr[0]=" + Last_Dt_arr[0] + "-" + Last_Dt_arr[1] + "\n",1,debug);
	    if (Last_Dt_arr[0].compareTo(check_subject_pattern)==0) {
			file_status = g.LogFileWriter(debug_file_location,debug_file_name,"Entered into if\n",1,debug);
	    	
		  Last_Dt_arr1[count] = Last_Dt_arr[0] + "|" + Sent_Dt.toString();
	     } else {
				file_status = g.LogFileWriter(debug_file_location,debug_file_name,"Entered into else\n",1,debug);
	    	 Last_Dt_arr1[count] = s;
	     } // else 
	//	file_status = g.LogFileWriter(debug_file_location,debug_file_name,"after if/else" + "\n",1,debug);

      } // while 
	  fr.close();
	  try {
	  fw1 = new FileWriter(FileLocation + "/" + "track_dt");
	  BufferedWriter bw = new BufferedWriter(fw1);
	  for (i=0;i < sub_rec_count;i++) {
			 fw1.write(Last_Dt_arr1[i]+"\n");
	  }
	  fw1.close();
	  file_status = g.LogFileWriter(debug_file_location,debug_file_name,"After while \n" + FileLocation,1,debug);
	  }catch (Exception e) {
		  file_status = g.LogFileWriter(debug_file_location,debug_file_name," Error at Read/writing track_dt\n",1,debug);
		  fw1.close();

	  }
  }catch (Exception e) {
	  file_status = g.LogFileWriter(debug_file_location,debug_file_name," Error at Reading track_dt\n",1,debug);
	  e.printStackTrace();
  }
  
  


}
public static void Print_Variables() throws Exception {
	int k=0;
	String filename=output_file_location + "/" + output_file_name;
	String source="";
	int append=0;
	String temp_value="";
	String String_val="";
//	FileWriter fw = new FileWriter(filename);
	
	// append flag --> 1 to append, 0 to no append
	

	fw = new FileWriter(filename,true); 


 source = source + Subject_Pattern_Split_Array[1] + "|";
 source = source + Sent_Dt + "|";
 source = source + Notification_Flag + "|";
 source = source + Subject_Pattern_Split_Array[7].replace("txn_type=", "") + "|";

 
    file_status = g.LogFileWriter(debug_file_location,debug_file_name,"file_name=" + filename,1,debug);
	file_status = g.LogFileWriter(debug_file_location,debug_file_name,"\n--> Environment=" + Subject_Pattern_Split_Array[1] + "|\n",1,debug);
	file_status = g.LogFileWriter(debug_file_location,debug_file_name,"--> Mail_Date=" + Sent_Dt + "|\n",1,debug);
	file_status = g.LogFileWriter(debug_file_location,debug_file_name,"--> Subject_index=" + Notification_Flag + "|\n",1,debug);
	file_status = g.LogFileWriter(debug_file_location,debug_file_name,"--> " + Subject_Pattern_Split_Array[7] + "\n",1,debug);
	
	for(k=0; k < var_rec_count ; k++) {
		//System.out.println(Variable_Pattern[k] + "|");
		if(Variable_Pattern[k].indexOf("=") > 0) {
		String_val = Variable_Pattern[k].substring(0,(Variable_Pattern[k].indexOf("=")));
		temp_value = Variable_Pattern[k].substring((Variable_Pattern[k].indexOf("=")+1), Variable_Pattern[k].length());
		temp_value = temp_value.trim();
		temp_value = temp_value.replaceAll("[^a-zA-Z0-9\\!\\@\\#\\$\\%\\^\\&\\*\\(\\)\\-\\_\\+\\=\\}\\{\\]\\[\\;\\:\\'\\.\\,\\|\\>\\<\\/\\?\\s]", "");
		file_status = g.LogFileWriter(debug_file_location,debug_file_name,"--> " + String_val + "=" + temp_value + "|\n",1,debug);
	     // content1 = content1.replaceAll("  "," ");
		source = source + temp_value  + "|";
		} else {
			source = source +  "|";
		}
		
	}
	source = source + "\n";

	file_status = g.LogFileWriter(debug_file_location,debug_file_name,"\n\n",1,debug);
	if(Notification_Flag  > 0 ) {
	fw.write(source.replace("\\n", ""));
	}

	fw.close();
}

}
