import java.net.*;
import java.util.Scanner;
import java.io.*;
 
public class Server
{
    //initialize socket and input stream
    private Socket          socket   = null;
    private ServerSocket    server   = null;
    private DataInputStream in       =  null;
 
    // constructor with port
    public void Connection(int port)
    {
        // starts server and waits for a connection
        try
        {
            server = new ServerSocket(port);
            System.out.println("Server started");
 
            System.out.println("Waiting for a client ...");
 
            socket = server.accept();
            System.out.println("Client accepted");
 
            // takes input from the client socket
      /*      in = new DataInputStream(
                new BufferedInputStream(socket.getInputStream()));
 
            String line = "";
 
            // reads message from client until "Over" is sent
            while (!line.equals("Over"))
            {
                try
                {
                    line = in.readUTF();
                    System.out.println(line);
 
                }
                catch(IOException i)
                {
                    System.out.println(i);
                }
            }
            System.out.println("Closing connection");
 
            // close connection
            socket.close();
            in.close();*/
        }
        catch(IOException i)
        {
            System.out.println(i);
        }
    }
 
    public void filetransfer() {
    	 File myFile = new File("s.pdf");
    	
        try {
        	byte[] mybytearray = new byte[(int) myFile.length()];
            BufferedInputStream bis = new BufferedInputStream(new FileInputStream(myFile));
			bis.read(mybytearray, 0, mybytearray.length);
			  OutputStream os = socket.getOutputStream();
		        os.write(mybytearray, 0, mybytearray.length);
		        os.flush();
		        socket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
      
    }
    
    public static void main(String args[])
    {
    	Scanner scan = new Scanner(System.in);
        Server server = new Server();
        System.out.println("Enter port no.::");
        int port = scan.nextInt();
        server.Connection(port);
        
        
    }
}
