import java.io.*;
import java.net.*;
import java.util.*;

class Game{
	static int turn = 1, winner = 1, teambid1, teambid2, score1, score2, won1, won2, rounds, sequence = 1, shuffle, dynamic, shuffleTurn;
	static String cardType;
	Player currentPlayer;
	static ArrayList<String> name = new ArrayList<String>();
	static ArrayList<String> cards = new ArrayList<String>();
	static ArrayList<String> turnList = new ArrayList<String>();
	static ArrayList<Integer> compareList = new ArrayList<Integer>();
	static ArrayList<Player> playerOrder = new ArrayList<Player>();
	static ArrayList<Player> players = new ArrayList<Player>();
	static ArrayList<Integer> history = new ArrayList<Integer>();
	
	public void shuffleCards(){
		for(int i=1;i<53;i++){
			String temp = Integer.toString(i%13);
			if(temp.length() == 1){
				temp = "0"+temp;
			}
			if(temp.equals("01")){
				temp = "14";
			}
			if(temp.equals("00")){
				temp = "13";
			}
			cards.add("-"+temp);
		}
		
		for(int i=0;i<52;i++){
			int j = i/13;
			if(j==0){
				String temp = "S"+cards.get(i);
				cards.set(i, temp);
			}
			else if(j==1){
				String temp = "C"+cards.get(i);
				cards.set(i, temp);
			}
			else if(j==2){
				String temp = "D"+cards.get(i);
				cards.set(i, temp);
			}
			else{
				String temp = "H"+cards.get(i);
				cards.set(i, temp);
			}
		}
		
		Collections.shuffle(cards);
	}

class Player extends Thread{
	Socket socket;
	BufferedReader input;
    PrintWriter output;
	String response; 
	Player opponent;
	String names;
	int flag = 0;
	ArrayList<String> cardsList = new ArrayList<String>();
	public Player(Socket socket){
		this.socket = socket;
		players.add(this);
		try{
			input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            output = new PrintWriter(socket.getOutputStream(), true);
			while(true){
				response = input.readLine();
				if(response.startsWith("Name:")){
					if(name.contains(response.substring(5))){
						output.println("name exists");
					}
					else{
						name.add(response.substring(5));
						output.println("Welcome");
						this.names = response.substring(5);
						break;
					}
				}
			} 
		} catch(IOException e){
			System.out.println("connection error");
		}		
	}
	public void shiftTurn(Player p){
		if(turn == 4){
			turn = 1;
		}
		else{
			turn++;
		}
		if(currentPlayer == p){
			currentPlayer = currentPlayer.opponent;
		}
	}
	public void shiftToWinner(Player p){
		playerOrder.clear();
		currentPlayer = p;
		turn = winner;
	}
	public void shiftToSequence(Player p){
		playerOrder.clear();
		currentPlayer = p;
		turn = sequence;
	}
	public void getTurnCards(String x, String y){
		String s = "";
		int i = cardsList.indexOf(y);
		s = cardsList.get(i).substring(0,1);
		turnList.add((players.indexOf(this)+1)+") "+y);
		if(x.equals(s)){
			compareList.add(cardValue(cardsList.get(i).substring(2)));
		}
		else{
			compareList.add(0);
		}
		cardsList.set(i, "used");
	}
	public boolean checkCard(String x, Player p){
		//String s = y.substring(0,1);
		for(int i = 0; i<13; i++){
			if(p.cardsList.get(i).substring(0,1).equals(x)){
				return true;
			}
		}
		return false;
	}
	public int decideTurnWinner(){
		int x = 0;
		int y = compareList.get(0);
		for(int i=1;i<4;i++){
			if(compareList.get(i) > y){
				x = i;
				y = compareList.get(i);
			}
		}
		compareList.clear();
		turnList.clear();
		return x;
	}
	public int cardValue(String s){
		if(s.equals("J")){
			return 11;
		}
		else if(s.equals("Q")){
			return 12;
		}
		else if(s.equals("K")){
			return 13;
		}
		else if(s.equals("A")){
			return 14;
		}
		else{
			return Integer.parseInt(s);
		}
	}
	public ArrayList<String> getCards(){
		ArrayList<String> sendCards = new ArrayList<String>();
		for(int i=0;i<13;i++){
			sendCards.add(cards.get((4*i)+turn-1));
		}
		sortCards(sendCards);
		return sendCards;
	}
	public String initialMove(String sent){
		String s = "";
		int i = cardsList.indexOf(sent);
		turnList.add((players.indexOf(this)+1)+") "+sent);
		s = cardsList.get(i).substring(0,1);
		compareList.add(cardValue(cardsList.get(i).substring(2)));
		cardsList.set(i, "used");
		return s;
	}
	public void setOpponent(Player opponent) {
            this.opponent = opponent;
    }
	public boolean initialBids(int x){
		if(turn ==1 || turn == 3){
			teambid1 += x;
			if(teambid1 > 13 || x < 0){
				teambid1 -=x;
				return false;
			}
		}
		else{
			teambid2 += x;
			if(teambid2 > 13 || x < 0){
				teambid2 -=x;
				return false;
			}
		}
		return true;
	}
	public ArrayList<String> sortCards(ArrayList<String> sendCards){
		Collections.sort(sendCards);
		for(int i=0; i<13; i++){
			if(sendCards.get(i).endsWith("11")){
				String temp = sendCards.get(i).substring(0, 2)+"J";
				sendCards.set(i, temp);
			}
			else if(sendCards.get(i).endsWith("12")){
				String temp = sendCards.get(i).substring(0, 2)+"Q";
				sendCards.set(i, temp);
			}
			else if(sendCards.get(i).endsWith("13")){
				String temp = sendCards.get(i).substring(0, 2)+"K";
				sendCards.set(i, temp);
			}
			else if(sendCards.get(i).endsWith("14")){
				String temp = sendCards.get(i).substring(0, 2)+"A";
				sendCards.set(i, temp);
			}
			else{
				String temp = sendCards.get(i).substring(0, 2)+Integer.toString(Integer.parseInt(sendCards.get(i).substring(2,4)));
				sendCards.set(i, temp);
			}
		}
		return sendCards;
	}
	public void score(){
		if(won1 >= teambid1){
			score1+= (10*teambid1)+(won1-teambid1);
		}
		else{
			score1-= (10*teambid1);
		}
		if(won2 >= teambid2){
			score2+= (10*teambid2)+(won2-teambid2);
		}
		else{
			score2-= (10*teambid2);
		}
		won1 = 0;
		won2 = 0;
		teambid1 = 0;
		teambid2 = 0;
		history.clear();
	}
	public boolean decideWinner(){
		if(score1>=250 || score2>=250 || score1<=-250 || score2<=-250){
			if(score1 > score2){
				winner = 1;
			}
			else if(score1 < score2){
				winner = 2;
			}
			else{
				winner = 0;
			}
			return false;
		}
		return true;
	}
	public void getTurnWinner(Player p){
		String x = p.names;
		winner = name.indexOf(x)+1;
		if(winner==1 || winner==3){
			won1++;
			history.add(1);
		}
		else{
			won2++;
			history.add(2);
		}
	}
	public void run(){
		try{
			output.println("All players connected");
			int score = 0, bid=0, tricks=0;
			while(decideWinner()){
				if(currentPlayer == this){
				output.println("Playing"+"Your chance");
				String team = "";
				for(int i=0;i<4;i++){
					players.get(i).flag=0;
					if(this.equals(players.get(i))){
						if(i==0 || i==2){
							team = players.get(0).names+" & "+players.get(2).names;
							score = score1;
							bid = teambid1;
							tricks = won1;
						}
						else{
							team = players.get(1).names+" & "+players.get(3).names;
							score = score2;
							bid = teambid2;
							tricks = won2;
						}
					}
				}
				if(rounds == 56){
					rounds = 0;
					shuffle = 0;
					shuffleTurn = 0;
					score();
					//System.out.println(score1+" "+score2);
					if(sequence == 4){
						sequence = 1;
					}
					else{
						sequence++;
					}
					shiftToSequence(players.get(sequence-1));
					continue;
				}
				if(shuffle == 0){
					cards.clear();
					shuffleCards();
					shuffle++;
				}
				if(rounds == 0 && shuffleTurn<4){
					output.println("reset");
					cardsList = getCards();
					output.println("List");
					for(int i=0;i<13;i++){
						output.println(cardsList.get(i));
					}
					output.println("score"+score);
					output.println("team"+bid);
					output.println("bidteam"+team);
					output.println("print");
					
					shuffleTurn++;
					shiftTurn(this);
					continue;
				}
				if(rounds!=0 && rounds%4 == 0){
					output.println("card-");
					//output.println("team"+bid);
					output.println("history"+history.size());
					if(players.indexOf(this)==0 || players.indexOf(this)==2){
						for(int i=0;i<history.size();i++){
							if(history.get(i)==1){output.println("W");}
							else{output.println("L");}
						}
					}
					else{
						for(int i=0;i<history.size();i++){
							if(history.get(i)==2){output.println("W");}
							else{output.println("L");}
						}
					}
					output.println("tricks"+tricks);
					output.println("Get card");
					response = input.readLine();
					cardType = initialMove(response.substring(5));
					output.println("Good");
					rounds++;
					playerOrder.add(this);
					shiftTurn(this);
				}
				else if(rounds>3 && rounds < 56){
					output.println("card"+cardType);
					//output.println("team"+bid);
					output.println("tricks"+tricks);
					output.println("RunCards"+turnList.size());
					for(int i=0;i<turnList.size();i++){
						output.println(turnList.get(i));
					}
					output.println("Get card");
					response = input.readLine();
					while(checkCard(cardType,this) && !response.substring(5,6).equals(cardType)){
						output.println("Gets card");
						response = input.readLine();
					}
					output.println("Good");
					getTurnCards(cardType, response.substring(5));
					playerOrder.add(this);
					for(int i=0;i<4;i++){
						players.get(i).flag=0;
					}
					try{
						this.sleep(100);
					}catch(InterruptedException ie){}
					rounds++;
					if(rounds%4!=0){
						shiftTurn(this);
					}
				}
				else if(rounds >= 0 && rounds < 4){
					output.println("Bids"+this.names);
					response = input.readLine();
					if(response.startsWith("Bids:")){
						try{
							boolean temp = initialBids(Integer.parseInt(response.substring(5)));
							if(!temp){
								output.println("Greater");
								continue;
							}
						}
						catch(NumberFormatException ne){
							output.println("Bid:");
							continue;
						}
					}
					rounds++;
					shiftTurn(this);
				}
				if(rounds>4 && rounds%4 == 0){
					output.println("RunCards"+turnList.size());
					for(int i=0;i<turnList.size();i++){
						output.println(turnList.get(i));
					}
					int x = decideTurnWinner();
					getTurnWinner(playerOrder.get(x));
					shiftToWinner(playerOrder.get(x));
				}
				output.flush();
			}
			else if(flag==0){
				output.println("Playing"+currentPlayer.names+" is playing");
				output.println("card"+cardType);
				if(turnList.size()!=0){
				output.println("RunCards"+turnList.size());
					for(int i=0;i<turnList.size();i++){
						output.println(turnList.get(i));
					}
				}
				if(players.indexOf(this)==0 || players.indexOf(this)==2){
					output.println("team"+teambid1);
					output.println("bidteam"+players.get(0).names+" & "+players.get(2).names);
					output.println("tricks"+won1);
				}
				else{
					output.println("team"+teambid2);
					output.println("bidteam"+players.get(1).names+" & "+players.get(3).names);
					output.println("tricks"+won2);
				}
				output.println("history"+history.size());
				if(players.indexOf(this)==0 || players.indexOf(this)==2){
					for(int i=0;i<history.size();i++){
						if(history.get(i)==1){output.println("W");}
						else{output.println("L");}
					}
				}
				else{
					for(int i=0;i<history.size();i++){
						if(history.get(i)==2){output.println("W");}
						else{output.println("L");}
					}
				}
				flag=1;
			}
			}
			if(winner==1){
				output.println("Team 1:"+name.get(0)+" and "+name.get(2)+" won");
				if(players.indexOf(this)==0 || players.indexOf(this)==2){
					output.println("YOU WON!!! :)");
					output.println("score"+score1);
				}
				else{
					output.println("YOU LOST!!! :(");
					output.println("score"+score2);
				}
			}
			else if(winner==2){
				output.println("Team 2:"+name.get(1)+" and "+name.get(3)+" won");
				if(players.indexOf(this)==1 || players.indexOf(this)==3){
					output.println("YOU WON!!! :)");
					output.println("score"+score2);
				}
				else{
					output.println("YOU LOST!!! :(");
					output.println("score"+score1);
				}
			}
			else{
				output.println("TIE!!! :|");
			}
		} catch(IOException e){
			System.err.println("error in "+name.get(turn-1)+" connection"+"\nClosing Server");
			output.println("error in "+name.get(turn-1)+" connection");
			System.exit(-1);
		}
	}
}
}