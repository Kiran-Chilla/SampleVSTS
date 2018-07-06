import java.io.*;
import java.net.*;
import java.util.*;

import javax.swing.*;

import java.awt.*;
import java.awt.event.*;

public class Client {
	String name, score;
	
	private JFrame frame = new JFrame("Client - "+name);;
    private JLabel messageLabel = new JLabel("");
	private JPanel boardPanel = new JPanel();
	private JTextArea text1 = new JTextArea();
	private JTextField text2 = new JTextField();
	private JTextArea text3 = new JTextArea();
	private JTextArea text4 = new JTextArea();
	private JTextArea text5 = new JTextArea();
	private JTextArea text6 = new JTextArea();
	
	private JButton[] button = new JButton[13];
	private int[] buttonFlag = new int[13];
    private JButton currentButton;
	private int currentFlag;
	
	private static int PORT = 12345;
    private Socket socket;
    private BufferedReader inp;
    private PrintWriter out;
	
	ArrayList<String> cardsList = new ArrayList<String>();
	String bidError="", bid="", tricks="", bids="";
	boolean flag = false;
	
	public Client(String name){
		this.name = name;
		try{
			socket = new Socket("localhost", PORT); 
			inp = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			out = new PrintWriter(socket.getOutputStream(), true);
			Scanner inputKeyboard = new Scanner(System.in);
			out.println("Name:"+name);
			
			frame.setTitle("Client-"+name);
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(400, 400);
            frame.setVisible(true);
            frame.setResizable(true);
			
			messageLabel.setBackground(Color.black);
			frame.getContentPane().add(messageLabel, "South");
			
			JPanel boardPanel = new JPanel();
				boardPanel.setBackground(Color.black);
				boardPanel.setLayout(new GridLayout(6, 3, 2, 2));
				
				text1.setBackground(Color.black);
				text1.setForeground(Color.green);
				
				text2.setBackground(Color.black);
				text2.setForeground(Color.green);
				
				text3.setBackground(Color.black);
				text3.setForeground(Color.red);
				
				text4.setBackground(Color.black);
				text4.setForeground(Color.red);
				
				text5.setBackground(Color.black);
				text5.setForeground(Color.green);
				
				text6.setBackground(Color.black);
				text6.setForeground(Color.green);
				
				text1.setEditable(false);text2.setEditable(false);
				text3.setEditable(false);text4.setEditable(false);
				text5.setEditable(false);text6.setEditable(false);
				
				boardPanel.add(text1);
				boardPanel.add(text2);
				boardPanel.add(text3);
				boardPanel.add(text4);
				frame.getContentPane().add(text1, "North");
				
				for (int i = 0; i < button.length; i++) {
					button[i] = new JButton();
					boardPanel.add(button[i]);
				}
				boardPanel.add(text5);boardPanel.add(text6);
				frame.getContentPane().add(boardPanel, "Center");
				frame.setVisible(true);
			
			while(true){
				String response = inp.readLine();
				if(response.toLowerCase().startsWith("name exists")){
					name = JOptionPane.showInputDialog("User with name "+name+" already exits\nPlease enter new name");
					out.println("Name:"+name);
					frame.setTitle("Client-"+name);
				}
				if(response.toLowerCase().startsWith("list")){
					for(int i=0;i<13;i++){
						cardsList.add(inp.readLine());
					}
				}
				if(response.startsWith("score")){
					score = response.substring(5);
					text2.setText("Your Score: "+score);
				}
				if(response.startsWith("bidteam")){
					text1.setText("Your Team :"+response.substring(7)+"\n\n");
					text1.append("Team Bid  :"+bids);
				}
				if(response.toLowerCase().startsWith("print")){
					text5.setText("Card Values:\n");
					text5.append("[2-10]-[2-10]\nJ-11, Q-12, K-13, A-14");
					for(int i=0;i<button.length;i++){
						button[i].setText(cardsList.get(i));
					}
				}
				if(response.startsWith("reset")){
					for(int i=0;i<button.length;i++){
						button[i].setText("");
						button[i].setEnabled(true);
						buttonFlag[i] = 0;
					}
					cardsList.clear();
				}
				if(response.startsWith("Bids")){
					String bid = JOptionPane.showInputDialog("User: "+response.substring(4)+"\n"+bidError+"\nPlease enter your bid[0-13]: ");
					out.println("Bids:"+bid);
					bidError="";
				}
				if(response.startsWith("Bid:")){
					bidError = "Please enter an integer for bid";
				}
				if(response.startsWith("Greater")){
					bidError = "Your team bid should be in [0-13] ";
				}
				if(response.startsWith("RunCards")){
					text4.setText("Cards Played:\n");
					for(int i=0;i<Integer.parseInt(response.substring(8));i++){
						text4.append(inp.readLine()+"  ");
						if(i==2){text4.append("\n");}
					}
				}
				if(response.startsWith("tricks")){
					text3.append("\nTricks Won:"+response.substring(6));
				}
				if(response.startsWith("Get card")){
					//text3.append("\nTricks Won:"+response.substring(8));
					currentButton = new JButton();
					flag = true;
					for(int i=0;i<button.length;i++){
						int j = i;
						if(!button[i].getText().endsWith("used")){
							button[i].addMouseListener(new MouseAdapter() {
								public void mousePressed(MouseEvent e) {
									if(flag && buttonFlag[j]==0){
									currentButton = button[j];
									currentFlag = j;
									out.println("Card:" + currentButton.getText());
									flag=false;}}});							
						}
					}
				}
				if(response.startsWith("Gets card")){
					messageLabel.setText("Your Chance-Please select valid card");
					currentButton = new JButton();
					flag = true;
					for(int i=0;i<button.length;i++){
						int j = i;
						if(!button[i].getText().endsWith("used")){
							button[i].addMouseListener(new MouseAdapter() {
								public void mousePressed(MouseEvent e) {
									if(flag && buttonFlag[j]==0){
									currentButton = button[j];
									currentFlag = j;
									out.println("Card:" + currentButton.getText());
									flag=false;}}});							
						}
					}
				}
				if(response.startsWith("Good")){
					int temp = cardsList.indexOf(currentButton.getText());
					cardsList.set(temp, cardsList.get(temp)+"-used") ;
					currentButton.setText(currentButton.getText()+"-used");
					buttonFlag[currentFlag] = 1;
					currentButton.setEnabled(false);
				}
				if(response.startsWith("Playing")){
					messageLabel.setText(response.substring(7));
				}
				if(response.startsWith("Team")){
					text1.setText(response);
					text1.append("\n"+inp.readLine());
				}
				if(response.startsWith("team")){
					bids = response.substring(4);
				}
				if(response.startsWith("card")){
					text3.setText("Running   :"+response.substring(4));
				}
				if(response.startsWith("history")){
					text6.setText("Tricks History:\n");
					for(int i=0;i<Integer.parseInt(response.substring(7));i++){
						text6.append(inp.readLine()+" ");
						if(i==6){text6.append("\n");}
					}
				}
				if(response.startsWith("error")){
					text1.setText(response);
					text1.append("\nServer is closed");
				}
			}
		} catch(IOException e){
			text1.append("\nConnection lost-");
			text1.append("Server closed");
		}
	}
	public static void main(String[] args) {
		String name;
		name = JOptionPane.showInputDialog("Please input your name: ");
		new Client(name);
	}
	public boolean release(ArrayList<String> x, String y){
		if(x.contains(y)){
			return true;
		}
		else{
			return false;
		}
	}
}
