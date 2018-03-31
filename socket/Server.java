package computernetworks;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

class ClientHandler implements Runnable 
		{
		    Scanner scn = new Scanner(System.in);
		    private String name;
		    final DataInputStream dis;
		    final DataOutputStream dos;
		    Socket s;
		    boolean isloggedin;
		     
		    // constructor
		    public ClientHandler(Socket s, String name,
		                            DataInputStream dis, DataOutputStream dos) {
		        this.dis = dis;
		        this.dos = dos;
		        this.name = name;
		        this.s = s;
		        this.isloggedin=true;
		    }
		 
		    @Override
		    public void run() {
		 
		        String received;
				try{
				for (ClientHandler mc : Server.ar) 
		                {
		                    // if the recipient is found, write on its
		                    // output stream
		                    if (!mc.name.equals(this.name) && mc.isloggedin==true) 
		                    {
		                        mc.dos.writeUTF(this.name + "Joined");
		                        
		                    }
		                }
		            } catch (IOException e) {
		                 
		                e.printStackTrace();
		            }
		        while (true) 
		        {
		            try
		            {
		                // receive the string
		                received = dis.readUTF();
		                 
		                System.out.println(received);
		                 
		                if(received.equals("logout")){
		                    this.isloggedin=false;
		                    this.s.close();
		                    break;
		                }
		                 
		                // break the string into message and recipient part
		             
		                // search for the recipient in the connected devices list.
		                // ar is the vector storing client of active users
		                for (ClientHandler mc : Server.ar) 
		                {
		                    // if the recipient is found, write on its
		                    // output stream
		                    if (mc.isloggedin==true) 
		                    {
		                        mc.dos.writeUTF(this.name+" : "+received);
		                        
		                    }
		                }
		            } catch (IOException e) {
		                 
		                e.printStackTrace();
				break;
		            }
		             
		        }
		        try
		        {
		            // closing resources
		            this.dis.close();
		            this.dos.close();
		             
		        }catch(IOException e){
		            e.printStackTrace();
				
		        }
		    }
		}
class ClientWorker implements Runnable {

    private Socket target_socket;
    private DataInputStream din;
    private DataOutputStream dout;

    public ClientWorker(Socket recv_socket) {
        try {
            target_socket = recv_socket;
            din = new DataInputStream(target_socket.getInputStream());
            dout = new DataOutputStream(target_socket.getOutputStream());
        } catch (IOException ex) {
            Logger.getLogger(ClientWorker.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void run() {

        RandomAccessFile rw = null;
        long current_file_pointer = 0;
        boolean loop_break = false;
        while (true) {
            byte[] initilize = new byte[1];
            try {
                din.read(initilize, 0, initilize.length);
                if (initilize[0] == 2) {
                    byte[] cmd_buff = new byte[3];
                    din.read(cmd_buff, 0, cmd_buff.length);
                    byte[] recv_data = ReadStream();
                    switch (Integer.parseInt(new String(cmd_buff))) {
                        case 124:
                            rw = new RandomAccessFile("/home/rockstar/Downloads/" + new String(recv_data), "rw");
                            dout.write(CreateDataPacket("125".getBytes("UTF8"), String.valueOf(current_file_pointer).getBytes("UTF8")));
                            dout.flush();
                            break;
                        case 126:
                            rw.seek(current_file_pointer);
                            rw.write(recv_data);                            
                            current_file_pointer = rw.getFilePointer();
                            System.out.println("Download percentage: " + ((float)current_file_pointer/rw.length())*100+"%");
                            dout.write(CreateDataPacket("125".getBytes("UTF8"), String.valueOf(current_file_pointer).getBytes("UTF8")));
                            dout.flush();
                            break;
                        case 127:
                            if ("Close".equals(new String(recv_data))) {
                                loop_break = true;
                            }
                            break;
                    }
                }
                if (loop_break == true) {
                    target_socket.close();
                }
            } catch (IOException ex) {
                Logger.getLogger(ClientWorker.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private byte[] ReadStream() {
        byte[] data_buff = null;
        try {
            int b = 0;
            String buff_length = "";
            while ((b = din.read()) != 4) {
                buff_length += (char) b;
            }
            int data_length = Integer.parseInt(buff_length);
            data_buff = new byte[Integer.parseInt(buff_length)];
            int byte_read = 0;
            int byte_offset = 0;
            while (byte_offset < data_length) {
                byte_read = din.read(data_buff, byte_offset, data_length - byte_offset);
                byte_offset += byte_read;
            }
        } catch (IOException ex) {
            Logger.getLogger(ClientWorker.class.getName()).log(Level.SEVERE, null, ex);
        }
        return data_buff;
    }

    private byte[] CreateDataPacket(byte[] cmd, byte[] data) {
        byte[] packet = null;
        try {
            byte[] initialize = new byte[1];
            initialize[0] = 2;
            byte[] separator = new byte[1];
            separator[0] = 4;
            byte[] data_length = String.valueOf(data.length).getBytes("UTF8");
            packet = new byte[initialize.length + cmd.length + separator.length + data_length.length + data.length];

            System.arraycopy(initialize, 0, packet, 0, initialize.length);
            System.arraycopy(cmd, 0, packet, initialize.length, cmd.length);
            System.arraycopy(data_length, 0, packet, initialize.length + cmd.length, data_length.length);
            System.arraycopy(separator, 0, packet, initialize.length + cmd.length + data_length.length, separator.length);
            System.arraycopy(data, 0, packet, initialize.length + cmd.length + data_length.length + separator.length, data.length);

        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(ClientWorker.class.getName()).log(Level.SEVERE, null, ex);
        }
        return packet;
    }
}
public class Server {
			
	private Socket          socket   = null;
    private ServerSocket    server   = null;
    private DataInputStream in       =  null;
    static Vector<ClientHandler> ar = new Vector<>();
    static int i = 0;
		public void setupconnection(int port) {
			try
	        {
	            server = new ServerSocket(port);
	            System.out.println("Server started");
	 
	            System.out.println("Waiting for a client ...");
	 
	            
	        }
	        catch(IOException i)
	        {
	            System.out.println(i);
	        }
		}
		
		public void serverchat() {
			while (true) 
	        {
	            // Accept the incoming request
				try {
	         socket = server.accept();
	            System.out.println("Client accepted");
	 
	            System.out.println("New client request received : " + socket);
	             
	            // obtain input and output streams
	            DataInputStream dis = new DataInputStream(socket.getInputStream());
	            DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
	             
	            System.out.println("Creating a new handler for this client...");
	 			String name = dis.readUTF();
	            // Create a new handler object for handling this request.
	            ClientHandler mtch = new ClientHandler(socket,name, dis, dos);
	 
	            // Create a new Thread with this object.
	            Thread t = new Thread(mtch);
	             
	            System.out.println("Adding this client to active client list");
	 
	            // add this client to active clients list
	            ar.add(mtch);
	 			
	            // start the thread.
	            t.start();
				}catch(IOException e) {}
	            // increment i for new client.
	            // i is used for naming only, and can be replaced
	            // by any naming scheme
	            i++;
	 
	        }
		}
		
		public void serverfiletransfer() {
			 while (true) {
	                try {
						new Thread(new ClientWorker(server.accept())).start();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
	            }
		}
		
	public static void main(String[] args) {
		// TODO Auto-generated method stub
			Server server = new Server();
			server.setupconnection(8000);
			//server.serverchat();
			server.serverfiletransfer();
	}

}
