import java.net.*;
import java.util.Scanner;
import java.io.*;
 
public class Client
{
    // initialize socket and input output streams
    private Socket socket            = null;
    private DataInputStream  input   = null;
    private DataOutputStream out     = null;
 
    // constructor to put ip address and port
    @SuppressWarnings("deprecation")
	public void  Connection(String address, int port)
    {
        // establish a connection
        try
        {
            socket = new Socket(address, port);
            System.out.println("Connected");
 
            // takes input from terminal
         //   input  = new DataInputStream(System.in);
 
            // sends output to the socket
         //   out    = new DataOutputStream(socket.getOutputStream());
        }
        catch(UnknownHostException u)
        {
            System.out.println(u);
        }
        catch(IOException i)
        {
            System.out.println(i);
        }
    }
    
    public void filetransfer() throws FileNotFoundException {
    	byte[] mybytearray = new byte[1024];
    	try {
        InputStream is = socket.getInputStream();
        FileOutputStream fos = new FileOutputStream("s.pdf");
        BufferedOutputStream bos = new BufferedOutputStream(fos);
        int bytesRead = is.read(mybytearray, 0, mybytearray.length);
        bos.write(mybytearray, 0, bytesRead);
        bos.close();
        socket.close();
    	}
    	catch(IOException i) {
    		
    	}
    	
    }
    
    public void chatcode() {
    	
    	
    }
        /*// string to read message from input
        String line = "";
 
        // keep reading until "Over" is input
        while (!line.equals("Over"))
        {
            try
            {
                line = input.readLine();
                out.writeUTF(line);
            }
            catch(IOException i)
            {
                System.out.println(i);
            }
        }
 
        // close the connection
        try
        {
            input.close();
            out.close();
            socket.close();
        }
        catch(IOException i)
        {
            System.out.println(i);
        }
    }*/
 
    public static void main(String args[])
    {
    	Scanner scan = new Scanner(System.in);
    		
    	System.out.println("Enter Ip address::");
    		String ip = scan.nextLine();
    		System.out.println("Enter port no.::");
    		int port = scan.nextInt();
        Client client = new Client();
        client.Connection(ip,port);
        try {
			client.filetransfer();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
}
