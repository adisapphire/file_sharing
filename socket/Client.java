package computernetworks;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JFileChooser;



public class Client {
	private Socket socket            = null;
    private DataInputStream  input   = null;
    private DataOutputStream out     = null;
	public void setupconnection(String ip,int port) {
		try
        {
            socket = new Socket(ip, port);
            System.out.println("Connection Established!");
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
	
	public void clientchat() {
		try {
		DataInputStream dis = new DataInputStream(socket.getInputStream());
        DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
		
        Scanner scn = new Scanner(System.in);
        // sendMessage thread
        Thread sendMessage = new Thread(new Runnable() 
        {
            @Override
            public void run() {
				try {
			System.out.println("Enter username");
			String name = scn.nextLine();
			dos.writeUTF(name);
			} catch (IOException e) {
                        e.printStackTrace();
                    }

                while (true) {
 
                    // read the message to deliver.
                    String msg = scn.nextLine();
                     
                    try {
                        // write on the output stream
                        dos.writeUTF(msg);
                    } catch (IOException e) {
                        e.printStackTrace();
                        break;
                    }
                }
            }
        });
         
        // readMessage thread
        Thread readMessage = new Thread(new Runnable() 
        {
            @Override
            public void run() {
 
                while (true) {
                    try {
                        // read the message sent to this client
                        String msg = dis.readUTF();
                        System.out.println(msg);
                    } catch (IOException e) {
 
                        e.printStackTrace();
				break;
                    }
                }
            }
        });
 
        sendMessage.start();
        readMessage.start();
		}catch(IOException e) {}
	}
	
	public void serverfiletransfer() {
		 try {
	            
	            
	            input = new DataInputStream(socket.getInputStream());
	            out = new DataOutputStream(socket.getOutputStream());
	            JFileChooser jfc = new JFileChooser();
	            int dialog_value = jfc.showOpenDialog(null);
	            if (dialog_value == JFileChooser.APPROVE_OPTION) {
	                File target_file = jfc.getSelectedFile();
	                out.write(this.CreateDataPacket("124".getBytes("UTF8"), target_file.getName().getBytes("UTF8")));
	                out.flush();
	                RandomAccessFile rw = new RandomAccessFile(target_file, "r");
	                long current_file_pointer = 0;
	                boolean loop_break = false;
	                while (true) {
	                    if (input.read() == 2) {
	                        byte[] cmd_buff = new byte[3];
	                        input.read(cmd_buff, 0, cmd_buff.length);
	                        byte[] recv_buff = this.ReadStream(input);
	                        switch (Integer.parseInt(new String(cmd_buff))) {
	                            case 125:
	                                current_file_pointer = Long.valueOf(new String(recv_buff));
	                                int buff_len = (int) (rw.length() - current_file_pointer < 20000 ? rw.length() - current_file_pointer : 20000);
	                                byte[] temp_buff = new byte[buff_len];
	                                if (current_file_pointer != rw.length()) {
	                                    rw.seek(current_file_pointer);
	                                    rw.read(temp_buff, 0, temp_buff.length);
	                                    out.write(this.CreateDataPacket("126".getBytes("UTF8"), temp_buff));
	                                    out.flush();
	                                    System.out.println("Upload percentage: " + ((float)current_file_pointer/rw.length())*100+"%");
	                                } else {
	                                    loop_break = true;
	                                }
	                                break;
	                        }
	                    }
	                    if (loop_break == true) {
	                        System.out.println("Stop Server informed");
	                        out.write(this.CreateDataPacket("127".getBytes("UTF8"), "Close".getBytes("UTF8")));
	                        out.flush();
	                        socket.close();
	                        System.out.println("Client Socket Closed");
	                        break;
	                    }
	                }
	            }
	        } catch (UnknownHostException ex) {
	            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
	        } catch (IOException ex) {
	            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
	        }
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
	            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
	        }
	        return packet;
	    }

	    private byte[] ReadStream(DataInputStream din) {
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
	            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
	        }
	        return data_buff;
	    }
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Client client = new Client();
		client.setupconnection("127.0.0.1", 8000);
		//client.clientchat();

	}

}
