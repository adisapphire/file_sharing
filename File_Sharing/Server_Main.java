package file_sharing;

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


public class Server_Main extends javax.swing.JFrame {

    public Socket          socket   = null;
    public ServerSocket    server   = null;
    public DataInputStream in       =  null;
    
		public void setupconnection(int port) {
                    System.out.println(port);
			try
	        {
	            this.server = new ServerSocket(port);
	            System.out.println("Server started");
	 
	            System.out.println("Waiting for a client ...");
                       
	            
	        }
	        catch(IOException i)
	        {
	            System.out.println(i);
	        }
		}
                
                
    
    public Server_Main() {
        initComponents();
    }

    @SuppressWarnings("unchecked")
    private void initComponents() {

        portno = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        start_server = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        portno.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                portnoActionPerformed(evt);
            }
        });

        jLabel1.setText("Port Number");

        start_server.setText("Start");
        start_server.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                start_serverActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(106, 106, 106)
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(portno, javax.swing.GroupLayout.PREFERRED_SIZE, 78, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(145, 145, 145)
                        .addComponent(start_server)))
                .addContainerGap(132, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(106, 106, 106)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(portno, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1))
                .addGap(29, 29, 29)
                .addComponent(start_server)
                .addContainerGap(118, Short.MAX_VALUE))
        );

        pack();
    }
    private void portnoActionPerformed(java.awt.event.ActionEvent evt) {

    }

    private void start_serverActionPerformed(java.awt.event.ActionEvent evt) {
        pn = Integer.parseInt(this.portno.getText());
        
              this.setupconnection(pn);
             
              if(this.server!=null){
                  
                  this.hide();
                  
              chat_window obj_chat = new chat_window(this.server,this.socket);
              obj_chat.setVisible(true);
              Thread t1 = new Thread( new Runnable(){
                       public  void run(){
                            obj_chat.serverchat();
                        }
              });
              t1.start();
              
              
              
              
              }
              
              
              
    }
    public static void main(String args[]) {
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Server_Main.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Server_Main.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Server_Main.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Server_Main.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Server_Main().setVisible(true);
            }
        });
    }
  private int pn;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JTextField portno;
    private javax.swing.JButton start_server;
}
