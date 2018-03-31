/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package file_sharing;

import file_sharing.Server;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
import java.util.Vector;
/**
 *
 * @author rockstar
 */

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
                        System.out.println("ok");
		        String received;
				try{
				for (ClientHandler mc : chat_window.ar) 
		                {
		                    // if the recipient is found, write on its
		                    // output stream
		                    if (!mc.name.equals(this.name) && mc.isloggedin==true) 
		                    {
		                        mc.dos.writeUTF("\t"+this.name + " Joined");
		                        
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
		                for (ClientHandler mc : chat_window.ar) 
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
public class chat_window extends javax.swing.JFrame {
static Vector<ClientHandler> ar = new Vector<>();
    static int i = 0;
    private Socket          socket   = null;
    private ServerSocket    server   = null;
 
    public chat_window(ServerSocket    server ,Socket          socket) {
        initComponents();
       
        this.socket = socket;
        this.server = server;
        this.serverchat();
        
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
	             
	            System.out.println("Adding "+name+" to active client list");
	 
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
    /**
     * Creates new form chat_window
     */
    public chat_window() {
        initComponents();
        
        
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        User_name = new javax.swing.JTextArea();
        jLabel2 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabel1.setText("#Chat");
        jLabel1.addAncestorListener(new javax.swing.event.AncestorListener() {
            public void ancestorMoved(javax.swing.event.AncestorEvent evt) {
            }
            public void ancestorAdded(javax.swing.event.AncestorEvent evt) {
                jLabel1AncestorAdded(evt);
            }
            public void ancestorRemoved(javax.swing.event.AncestorEvent evt) {
            }
        });

        User_name.setColumns(20);
        User_name.setRows(5);
        jScrollPane1.setViewportView(User_name);

        jLabel2.setText("Users::");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(151, Short.MAX_VALUE)
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 124, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel2)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 230, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(95, 95, 95)
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(49, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jLabel1AncestorAdded(javax.swing.event.AncestorEvent evt) {//GEN-FIRST:event_jLabel1AncestorAdded
        // TODO add your handling code here:
    }//GEN-LAST:event_jLabel1AncestorAdded

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(chat_window.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(chat_window.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(chat_window.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(chat_window.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        
        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                
                new chat_window().setVisible(true);
                
                
                
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextArea User_name;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JScrollPane jScrollPane1;
    // End of variables declaration//GEN-END:variables
}
