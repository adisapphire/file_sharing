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
  
  public static Comparator<Record> NameComparator = new Comparator<Record>()
  {

      @Override
      public int compare(Record e1, Record e2) 
      {
          return e1.filename.compareToIgnoreCase(e2.filename);
      }
  };
  
  public static Comparator<Record> DateModifiedComparator = new Comparator<Record>()
  {
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
	  };
	  
	  public static Comparator<Record> UserComparator = new Comparator<Record>()
	  {
	      @Override
	      public int compare(Record e1, Record e2) 
	      {
	          return e1.User.compareToIgnoreCase(e2.User);
	      }
	  };
	   
 
  
  public static void main(String args[])
  {
   try
   {
     FileInputStream fstream = new FileInputStream("data.txt");
     DataInputStream in = new DataInputStream(fstream);
     BufferedReader br = new BufferedReader(new InputStreamReader(in));
     String strLine;
     Record[] record = new Record[5];
     int i=0;
          
	    while ((strLine = br.readLine()) != null)  
	     {
	       String[] tokens = strLine.split("  ");
	       record[i] = new Record(tokens[0],tokens[1],tokens[2],tokens[3]);//process record , etc
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
	    	Arrays.sort(record, Record.NameComparator);
	    	break;
	    	
	    case 2:
	    	Arrays.sort(record, Record.NameComparator.reversed());
	    	break;
	    	
	    case 3:
	    	Arrays.sort(record, Record.DateModifiedComparator);
	    	break;
	    	
	    case 4:
	    	Arrays.sort(record, Record.DateModifiedComparator.reversed());
	    	break;
	    	
	    case 5:
	    	Arrays.sort(record, Record.UserComparator);
	    	break;
	    	
	    case 6:
	    	Arrays.sort(record, Record.UserComparator.reversed());
	    	break;
	    	
	    default:
	    	Arrays.sort(record, Record.DateModifiedComparator.reversed());
		    break;	
	    }
         
	    System.out.println(" Filename\t    Date_Time\t\tStatus\t  User"); 
	    System.out.println("-----------------------------------------------------------");
       for(int j=0;j<5;j++)
       System.out.println(record[j].toString());
       in.close();
   }
   catch (Exception e)
   {
     System.err.println("Error: " + e.getMessage());
   } 
 }
}