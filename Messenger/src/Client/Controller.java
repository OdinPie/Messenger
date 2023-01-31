package Client;

import Server.ClientHandler;
import Server.Server;
import animatefx.animation.FadeIn;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ComboBox;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.ResourceBundle;


public class Controller {
    @FXML
    public Pane pnSignIn;
    @FXML
    public Pane pnSignUp;
    @FXML
    public Button btnSignUp;
    @FXML
    public Button getStarted;
    @FXML
    public ImageView btnBack;
    @FXML
    public TextField regName;
    @FXML
    public TextField regPass;
    @FXML
    public TextField regEmail;
    @FXML
    public TextField regFirstName;
    @FXML
    public TextField regPhoneNo;
    @FXML
    public RadioButton male;
    @FXML
    public RadioButton female;
    @FXML
    public Label controlRegLabel;
    @FXML
    public Label success;
    @FXML
    public Label goBack;
    @FXML
    public TextField userName;
    @FXML
    public TextField passWord;
    @FXML
    public Label loginNotifier;
    @FXML
    public Label nameExists;
    @FXML
    public Label checkEmail;
    @FXML
    private ComboBox<?> activeusers;
    public static String username, password, gender;
   
    Connection con;
    PreparedStatement pst;
    ResultSet rs;
    Statement st;

    public static ArrayList<User> loggedInUser = new ArrayList<>();
    public static ArrayList<User> users = new ArrayList<User>();

    public void registration(ActionEvent event) {
        if (!regName.getText().equalsIgnoreCase("")
                && !regPass.getText().equalsIgnoreCase("")
                && !regEmail.getText().equalsIgnoreCase("")
                && !regFirstName.getText().equalsIgnoreCase("")
                && !regPhoneNo.getText().equalsIgnoreCase("")
                && (male.isSelected() || female.isSelected())) {
            if(checkUser(regName.getText())) {
                if(checkEmail(regEmail.getText())) {
                    User newUser = new User();
                    newUser.name = regName.getText();
                    newUser.password = regPass.getText();
                    newUser.email = regEmail.getText();
                    newUser.fullName = regFirstName.getText();
                    newUser.phoneNo = regPhoneNo.getText();
                    
                   
                    if (male.isSelected()) {
                        newUser.gender = "Male";
                        gender="Male";
                    } else {
                        newUser.gender = "Female";
                        gender="Female";
                    }
                   
                    users.add(newUser);
                    //mysqlConnect msc=new mysqlConnect();
                    
                    String sql = "INSERT INTO info (username, password, gender, email, phoneNo, fullname) values (?, ?, ?, ?, ?, ?);";
                    try {
						pst = mysqlConnect.getConnector().prepareStatement(sql);
						pst.setString(1, regName.getText());
						pst.setString(2, regPass.getText());
						pst.setString(3, gender);
						pst.setString(4, regEmail.getText());
						pst.setString(5, regPhoneNo.getText());
						pst.setString(6, regFirstName.getText());
						pst.execute();
						
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
                    String sql2 = "INSERT INTO groupchat (username) values (?);";
                    try {
						pst = mysqlConnect.getConnector().prepareStatement(sql2);
						pst.setString(1, regName.getText());
						pst.execute();
						
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
                    goBack.setOpacity(1);
                    success.setOpacity(1);
                    makeDefault();
                    if (controlRegLabel.getOpacity() == 1) {
                        controlRegLabel.setOpacity(0);
                    }
                    if (nameExists.getOpacity() == 1) {
                        nameExists.setOpacity(0);
                    }
                } else {
                    checkEmail.setOpacity(1);
                    setOpacity(nameExists, goBack, controlRegLabel, success);
                }
            } else {
                nameExists.setOpacity(1);
                setOpacity(success, goBack, controlRegLabel, checkEmail);
            }
        } else {
            controlRegLabel.setOpacity(1);
            setOpacity(success, goBack, nameExists, checkEmail);
        }
    }
    
    

    private void setOpacity(Label a, Label b, Label c, Label d) {
        if(a.getOpacity() == 1 || b.getOpacity() == 1 || c.getOpacity() == 1 || d.getOpacity() == 1) {
            a.setOpacity(0);
            b.setOpacity(0);
            c.setOpacity(0);
            d.setOpacity(0);
        }
    }


    private void setOpacity(Label controlRegLabel, Label checkEmail, Label nameExists) {
        controlRegLabel.setOpacity(0);
        checkEmail.setOpacity(0);
        nameExists.setOpacity(0);
    }

    private boolean checkUser(String username) {
        for(User user : users) {
            if(user.name.equalsIgnoreCase(username)) {
                return false;
            }
        }
        return true;
    }

    private boolean checkEmail(String email) {
        for(User user : users) {
            if(user.email.equalsIgnoreCase(email)) {
                return false;
            }
        }
        return true;
    }

    private void makeDefault() {
        regName.setText("");
        regPass.setText("");
        regEmail.setText("");
        regFirstName.setText("");
        regPhoneNo.setText("");
        male.setSelected(true);
        setOpacity(controlRegLabel, checkEmail, nameExists);
    }


    public void login() throws SQLException {
        username = userName.getText();
        password = passWord.getText();
        boolean login = false;
        con = mysqlConnect.getConnector();
      //  String query = " select* from info where username =\'"+username+"\' & password = \'"+password+"\'";
       
    /*   for (User x : users)  {
        	x.name= username;
        	x.password= password;
        	pst=con.prepareStatement("select * from info where username=? and password=?");
        	pst.setString(1, x.name);
        	pst.setString(2, x.password);
             rs = pst.executeQuery();
            if (rs.next()!= false) {
            	login = true;
            	System.out.println("LOGGED IN");
                 loggedInUser.add(x);
                 System.out.println(x.name);
                 gender = x.gender;
            	
            	
               
                break;
            }
        }*/
    
        
        	
        	pst=con.prepareStatement("select * from info where username=? and password=?");
        	pst.setString(1, username);
        	pst.setString(2, password);
            rs = pst.executeQuery();
            if (rs.next()!= false) {
             	login = true;
             	st= con.createStatement();
            	rs= st.executeQuery("select* from info where password = \'"+password+"\'");
            	while(rs.next()) {
            		gender= rs.getString(3);
            		break;
            		
            	}
            	
              
            	
               
               
        }
        
        if (login) {
        	
            changeWindow();
        } else {
            loginNotifier.setOpacity(1);
        }
    }

    public void changeWindow() {
        try {
            Stage stage = (Stage) userName.getScene().getWindow();
            Parent root = FXMLLoader.load(this.getClass().getResource("Room.fxml"));
            stage.setScene(new Scene(root, 330, 560));
            stage.setTitle(username + "");
            stage.setOnCloseRequest(event -> {
                System.exit(0);
            });
            stage.setResizable(false);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleButtonAction(ActionEvent event) {
        if (event.getSource().equals(btnSignUp)) {
            new FadeIn(pnSignUp).play();
            pnSignUp.toFront();
        }
        if (event.getSource().equals(getStarted)) {
            new FadeIn(pnSignIn).play();
            pnSignIn.toFront();
        }
        loginNotifier.setOpacity(0);
        userName.setText("");
        passWord.setText("");
    }

    @FXML
    private void handleMouseEvent(MouseEvent event) {
        if (event.getSource() == btnBack) {
            new FadeIn(pnSignIn).play();
            pnSignIn.toFront();
        }
        regName.setText("");
        regPass.setText("");
        regEmail.setText("");
    }
    public void initialize(URL arg0, ResourceBundle arg1) {
		// TODO Auto-generated method stub
		
	}
}

//repudiation