import java.io.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Scanner;
 public class Record
{
   String filename;
   String Date_Time;
   String Status;
   String User;

  public Record(String filename, String Date_Time, String Status, String User)
  {
       this.filename = filename;
       this.Date_Time = Date_Time;
       this.Status = Status;
       this.User = User;
  }
  
  @Override
  public String toString() 
  {
       return (filename+
                   "\t  "+ Date_Time +
                   "\t"+ Status +
                   "\t  " + User);
  }
 	  
	   
  public static void main(String args[])
  {
   try
   {
     FileInputStream fstream = new FileInputStream("data.txt");
     DataInputStream in = new DataInputStream(fstream);
     BufferedReader br = new BufferedReader(new InputStreamReader(in));
     String strLine;
     // Record[] record = new Record[5];
     ArrayList<Object> record = new ArrayList<Object>();
     int i=0;
          
	    while ((strLine = br.readLine()) != null)  
	     {
	       String[] tokens = strLine.split("  ");
	       record.add(new Record(tokens[0],tokens[1],tokens[2],tokens[3]));//process record , etc
	       i++;
	     }
	    
	    System.out.println("1. Filename in Ascending order");
	    System.out.println("2. Filename in Descending order");
	    System.out.println("3. Date-Time in Ascending order");
	    System.out.println("4. Date-Time in Descending order");
	    System.out.println("5. User in Ascending order");
	    System.out.println("6. User in Descending order");
	    
	    // Here default sort by date in descending order
	    
	    Scanner scan = new Scanner(System.in);
	    System.out.println("\nChoose any one from above\n");
	    int c = scan.nextInt();
	    
	    switch(c)
	    {
	    case 1:
	    	// Arrays.sort(record, Record.NameComparator);
	    	Collections.sort(record,new NameComparator()); 
	    	break;
	    	
	    case 2:
	    	// Arrays.sort(record, Record.NameComparator.reversed());
	    	Collections.sort(record,new NameComparator());
	    	Collections.reverse(record);
	    	break;
	    	
	    case 3:
	    	// Arrays.sort(record, Record.DateModifiedComparator);
	    	Collections.sort(record,new DateModifiedComparator()); 
	    	break;
	    	
	    case 4:
	    	// Arrays.sort(record, Record.DateModifiedComparator.reversed());
		    Collections.sort(record,new  DateModifiedComparator()); 
		    Collections.reverse(record);
	    	break;
	    	
	    case 5:
	    	// Arrays.sort(record, Record.UserComparator);
	    	Collections.sort(record,new userNameComparator()); 
	    	break;
	    	
	    case 6:
	    	// Arrays.sort(record, Record.UserComparator.reversed());
		    Collections.sort(record,new userNameComparator()); 
		    Collections.reverse(record);
	    	break;
	    	
	    default:
	    	// Arrays.sort(record, Record.DateModifiedComparator.reversed());
		    Collections.sort(record,new NameComparator()); 
		    break;	
	    }
         
	    System.out.println(" Filename\t    Date_Time\t\tStatus\t  User"); 
	    System.out.println("-----------------------------------------------------------");
       for(Record st: record){  
			System.out.println(st.filename+"\t    "+st.Date_Time+"\t   "+st.Status);  
		}  
       in.close();
   }
   catch (Exception e)
   {
     System.err.println("Error: " + e.getMessage());
   } 
 }
}


class NameComparator implements Comparator<Record>{  

	public int compare(Record e1, Record e2){
          return e1.filename.compareToIgnoreCase(e2.filename);
     }
}  

class DateModifiedComparator implements Comparator<Record>{  

		  DateFormat f = new SimpleDateFormat("MM/dd/yyyy hh:mm a");
	@Override
	public int compare(Record o1, Record o2) {
		// TODO Auto-generated method stub
		try {
            return f.parse(o1.Date_Time).compareTo(f.parse(o2.Date_Time));
        } catch (ParseException e) {
            throw new IllegalArgumentException(e);
        }
		
	}
}  

class  userNameComparator implements Comparator<Record>{  

	 public int compare(Record e1, Record e2) 
	      {
	          return e1.User.compareToIgnoreCase(e2.User);
	      }
}  
