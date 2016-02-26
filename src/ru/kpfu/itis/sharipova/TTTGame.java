package ru.kpfu.itis.sharipova;
import ru.kpfu.itis.sharipova.interfaces.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 *  Client -> Server           Server -> Client
 *  ----------------           ----------------
 *  MOVE <n>  (0 <= n <= 8)    WELCOME <char>  (char in {X, O})
 *  QUIT                       VALID_MOVE
 *                             OTHER_PLAYER_MOVED <n>
 *                             VICTORY
 *                             DEFEAT
 *                             TIE
 *                             MESSAGE <text>
 *
 */
class TTTGame extends Module{

    protected boolean hasWinner(){

        return (board[0][0] != null && board[0][0] == board[0][1] && board[0][0] == board[0][2])
                ||(board[1][0] != null && board[1][0] == board[1][1] && board[1][0] == board[1][2])
                ||(board[2][0] != null && board[2][0] == board[2][1] && board[2][0] == board[2][2])
                ||(board[0][0] != null && board[0][0] == board[1][0] && board[0][0] == board[2][0])
                ||(board[0][1] != null && board[0][1] == board[1][1] && board[0][1] == board[2][1])
                ||(board[0][2] != null && board[0][2] == board[1][2] && board[0][2] == board[2][2])
                ||(board[0][0] != null && board[0][0] == board[1][1] && board[0][0] == board[2][2])
                ||(board[0][2] != null && board[0][2] == board[1][1] && board[0][2] == board[2][0]);
    };

    protected synchronized boolean legalMove(int y , int x , Player player){
        if (player == currentPlayer && board[y][x] == null) {
            //current player moves
            board[y][x] = currentPlayer;
            currentPlayer = currentPlayer.opponent;
            //tells the oponent's thread about current move
            currentPlayer.otherPlayerMoved(y, x);
            return true;
        }
        return false;
    };
    //tied?
    protected boolean boardFilledUp(){
        for (Player[] players : board) {
            for (Player player : players) {
                if (player==null) return false;
            }
        }
        return true;
    };

    public class TTTPlayer extends Player {

        public TTTPlayer(Socket socket, char mark) {
            this.socket = socket;
            this.mark = mark;
            try {
                input = new BufferedReader(
                        new InputStreamReader(socket.getInputStream()));
                output = new PrintWriter(socket.getOutputStream(), true);
                output.println("WELCOME " + mark);
                output.println("MESSAGE Waiting for opponent to connect");
            } catch (IOException e) {
                System.out.println("Player died: " + e);
            }
        }

        public void setOpponent(Player opponent){
            this.opponent=opponent;
        }
        public void otherPlayerMoved (int i, int j){
            output.println("OPPONENT_MOVED " + i+" "+ j);
            output.println(
                    hasWinner() ? "DEFEAT" : boardFilledUp() ? "TIE" : "");
        }

        public void run() {
            try {
                // The thread is only started after everyone connects.
                output.println("MESSAGE All players connected");

                // Tell the first player that it is her turn.
                if (mark == 'X') {
                    output.println("MESSAGE Your move");
                }

                // Repeatedly get commands from the client and process them.
                while (true) {
                    String command = input.readLine();
                    if (command.startsWith("MOVE")) {
                        int i = Integer.parseInt(command.substring(5, 6));
                        int j = Integer.parseInt(command.substring(7));
                        if (legalMove(i, j, this)) {
                            output.println("VALID_MOVE");
                            output.println(hasWinner() ? "VICTORY"
                                    : boardFilledUp() ? "TIE"
                                    : "");
                        } else {
                            output.println("MESSAGE ?");
                        }
                    } else if (command.startsWith("QUIT")) {
                        return;
                    }
                }
            } catch (IOException e) {
                System.out.println("Player died: " + e);
            } finally {
                try {socket.close();} catch (IOException e) {}
            }
        }

    }
}
