package ru.kpfu.itis.sharipova;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.IOException;
import java.net.ServerSocket;


public class Server {
    public static void main(String[] args) throws IOException {
        ApplicationContext ac = new  ClassPathXmlApplicationContext("ru/kpfu/itis/sharipova/config.xml");
//        ServerSocket serverSocket= new ServerSocket(4242);
        ServerSocket serverSocket= (ServerSocket) ac.getBean("server");
        System.out.println("Tic Tac Toe Server is Running");
        try {
            while (true) {
                TTTGame game = (TTTGame) ac.getBean("game");
                TTTGame.TTTPlayer playerO = game.new TTTPlayer(serverSocket.accept(), 'O');
                TTTGame.TTTPlayer playerX = game.new TTTPlayer(serverSocket.accept(), 'X');

//                TTTGame.TTTPlayer playerX = (TTTGame.TTTPlayer) ac.getBean("x");
//                TTTGame.TTTPlayer playerO = (TTTGame.TTTPlayer)ac.getBean("o");
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
