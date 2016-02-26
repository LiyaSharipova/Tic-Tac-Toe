package ru.kpfu.itis.sharipova;

import java.io.IOException;
import java.net.ServerSocket;


public class Server {
    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket= new ServerSocket(4242);
        System.out.println("Tic Tac Toe Server is Running");
        try {
            while (true) {
                TTTGame game = new TTTGame();
                TTTGame.TTTPlayer playerX = game.new TTTPlayer(serverSocket.accept(), 'X');
                TTTGame.TTTPlayer playerO = game.new TTTPlayer(serverSocket.accept(), 'O');
                playerX.setOpponent(playerO);
                playerO.setOpponent(playerX);
                game.currentPlayer = playerX;
                playerX.start();
                playerO.start();
            }
        } finally {
            serverSocket.close();
        }

    }
}
