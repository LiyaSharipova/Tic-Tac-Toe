package ru.kpfu.itis.sharipova.interfaces;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Created by Baths on 03.12.2015.
 */
public abstract class Player extends Thread {
    public char mark;
    public Player opponent;
    public Socket socket;
    public BufferedReader input;
    public PrintWriter output;
    public abstract void setOpponent(Player opponent);
    public abstract void otherPlayerMoved(int Y, int X);


}
