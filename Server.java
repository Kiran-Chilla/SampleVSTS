import java.io.*;
import java.net.*;
import java.util.*;

public class Server {
	static int connection = 0;

    public static void main(String[] args) throws Exception {
        ServerSocket listener = new ServerSocket(12345);
        System.out.println("Server is Running");
        try {
            while (connection!=4) {
                Game game = new Game();
				
                Game.Player player1 = game.new Player(listener.accept());
				connection++;
                Game.Player player2 = game.new Player(listener.accept());
				connection++;
				Game.Player player3 = game.new Player(listener.accept());
				connection++;
                Game.Player player4 = game.new Player(listener.accept());
				connection++;
				
                player1.setOpponent(player2);
                player2.setOpponent(player3);
				player3.setOpponent(player4);
                player4.setOpponent(player1);
				
                game.currentPlayer = player1;
				
                player1.start();
                player2.start();
				player3.start();
				player4.start();
            }
        } catch(IOException e){
			System.err.println("Port 12345 already in use");
			System.exit(-1);
		}
		finally {
            listener.close();
        }
    }
}