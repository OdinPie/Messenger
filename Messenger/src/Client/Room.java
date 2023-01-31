package Client;

import Client.Controller.*;
import animatefx.animation.FadeIn;
import animatefx.animation.FadeOut;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.NodeOrientation;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.commons.lang3.StringUtils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.Socket;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ResourceBundle;

import static Client.Controller.loggedInUser;
import static Client.Controller.users;

public class Room extends Thread implements Initializable {
    @FXML
    public Label clientName;
    @FXML
    public Button chatBtn;
    @FXML
    public Pane chat;
    @FXML
    public TextField msgField;
    @FXML
    public TextArea msgRoom;
    @FXML
    public Label online;
    @FXML
    public Label fullName;
    @FXML
    public Label email;
    @FXML
    public Label phoneNo;
    @FXML
    public Label gender;
    @FXML
    public Pane profile;
    @FXML
    public Button profileBtn;
    @FXML
    public TextField fileChoosePath;
    @FXML
    public ImageView proImage;
    @FXML
    public Circle showProPic;
    @FXML
    private ComboBox activeusers;
    @FXML
    private Button groupchatbtn;
    private FileChooser fileChooser;
    private File filePath;
    public boolean toggleChat = false, toggleProfile = false;
    Connection con;
    Statement st;
    ResultSet rs;
    PreparedStatement pst;
    BufferedReader reader;
    PrintWriter writer;
    Socket socket;
   Boolean ind = false;
   Boolean pvt=false;
   private static String rcvr =null;



    public void connectSocket() {
        try {
            socket = new Socket("localhost", 8889);
            System.out.println("Socket is connected with server!");
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            writer = new PrintWriter(socket.getOutputStream(), true);
            this.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
    	
        try {
            while (true) {
                String msg = reader.readLine();
                String[] tokens = msg.split(" ");
                String cmd = tokens[0];
                System.out.println(cmd);
                StringBuilder fulmsg = new StringBuilder();
                for(int i = 1; i < tokens.length; i++) {
                    fulmsg.append(tokens[i]);
                }
                
                System.out.println(fulmsg);
              //  System.out.println(rcvr);
                if (cmd.equalsIgnoreCase(Controller.username + ":")) {
                    continue;
                } else if(fulmsg.toString().equalsIgnoreCase("bye")) {
                    break;
                }
                if(ind==false) {
                msgRoom.appendText(msg + "\n");
                }
                else if (ind==true && cmd.equalsIgnoreCase(rcvr + ":")) {
                	con = mysqlConnect.getConnector();
                    
                    try {
                        Statement st = con.createStatement();
                        st.executeUpdate("INSERT INTO privatechat (username, chat)\r\n" + "VALUES (\'"+Controller.username+"\',\'"+msg+"\');"  );
                      
                        System.out.println("Inserted");
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                	msgRoom.appendText("<Private> "+msg + "\n");
                }  
            }
            reader.close();
            writer.close();
            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        
    	
    	
    }

    public void handleProfileBtn(ActionEvent event) throws SQLException {
        if (event.getSource().equals(profileBtn) && !toggleProfile) {
            new FadeIn(profile).play();
            profile.toFront();
            chat.toBack();
            toggleProfile = true;
            toggleChat = false;
            profileBtn.setText("Back");
            setProfile();
        } else if (event.getSource().equals(profileBtn) && toggleProfile) {
            new FadeIn(chat).play();
            chat.toFront();
            toggleProfile = false;
            toggleChat = false;
            profileBtn.setText("Profile");
        }
    }

  /*  public void setProfile() throws SQLException {
    	 con = mysqlConnect.getConnector();
    	 st= con.createStatement();
    	 //String bing= Controller.username;
    	 
    	 
       
        	    rs = st.executeQuery("select* from info where username = \' "+Controller.username+"\'");
        	    while(rs.next()){
        	    System.out.println("loop choltese");
        	    System.out.println(rs.getString(1));
        	    fullName.setText(rs.getString(1).toString());
                fullName.setOpacity(1);
                email.setText(rs.getString(4));
                email.setOpacity(1);
                phoneNo.setText(rs.getString(5));
                gender.setText(rs.getString(3));
               
        	    }
    }*/
        	    
          
        
    
    
  public void setProfile() {
       for (User user : users) {
    	   if (Controller.username.equalsIgnoreCase(user.name)) {
                fullName.setText(user.name);
                fullName.setOpacity(1);
                email.setText(user.email);
                email.setOpacity(1);
                phoneNo.setText(user.phoneNo);
                gender.setText(user.gender);
            }
        }
    }

    public void handleSendEvent(MouseEvent event) throws SQLException {
        send();
        for(User user : users) {
            System.out.println(user.name);
        }
    }


    public void send() throws SQLException {
        String msg = msgField.getText();
       
      
        writer.println(Controller.username + ": " + msg);
        msgRoom.setNodeOrientation(NodeOrientation.LEFT_TO_RIGHT);
         
        
       
        msgRoom.appendText("Me: " + msg + "\n");
        msgField.setText("");
        con = mysqlConnect.getConnector();
        
        try {
            Statement st = con.createStatement();
            st.executeUpdate("INSERT INTO groupchat (username, Chat)\r\n" + "VALUES (\'"+Controller.username+"\',\'"+msg+"\');"  );
          
        } catch (SQLException e) {
            e.printStackTrace();
        }
   	   
       
    }

    // Changing profile pic

    public boolean saveControl = false;

    public void chooseImageButton(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        fileChooser = new FileChooser();
        fileChooser.setTitle("Open Image");
        this.filePath = fileChooser.showOpenDialog(stage);
        fileChoosePath.setText(filePath.getPath());
        saveControl = true;
    }

    public void sendMessageByKey(KeyEvent event) throws SQLException {
        if (event.getCode().toString().equals("ENTER")) {
            send();
        }
    }

    public void saveImage() {
        if (saveControl) {
            try {
                BufferedImage bufferedImage = ImageIO.read(filePath);
                Image image = SwingFXUtils.toFXImage(bufferedImage, null);
                proImage.setImage(image);
                showProPic.setFill(new ImagePattern(image));
                saveControl = false;
                fileChoosePath.setText("");
            } catch (IOException e) {
                System.err.println(e.getMessage());
            }
        }
    }
    
    @FXML
    void showActiveUsers(ActionEvent event) {
    	 
        ind = true;
       String s = activeusers.getSelectionModel().getSelectedItem().toString();
        rcvr = s;
    }
    @FXML
    void groupChat(ActionEvent event) {
    	ind=false;
    	activeusers.setPromptText("Private Chat");
    	//Controller cw = new Controller();
    	
       // cw.changeWindow();
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        showProPic.setStroke(Color.valueOf("#90a4ae"));
        Image image;
        if(Controller.gender.equalsIgnoreCase("Male")) {
            image = new Image("icons/user.png", false);
        } else {
            image = new Image("icons/female.png", false);
            proImage.setImage(image);
        }
        showProPic.setFill(new ImagePattern(image));
        clientName.setText(Controller.username);
        connectSocket();
       
        con = mysqlConnect.getConnector();
        
       try {
        	st = con.createStatement();
			rs= st.executeQuery("select* from info");
			  ObservableList <String> list = FXCollections.observableArrayList();
			 while(rs.next()) {
				 String names = rs.getString(1);
				//System.out.println(rs.getString(1));
			     
			      // for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
			    	  
	                   list.add(rs.getString(1));
	                   
	               }
			 activeusers.setItems(list); 
	              
			      
			    
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
}
