package ru.kpfu.itis.sharipova.interfaces;

/**
 * Created by Baths on 03.12.2015.
 */
public abstract class Module {
    public Player currentPlayer;
    protected abstract boolean hasWinner();
    protected abstract boolean legalMove(int X, int Y, Player player );
    protected abstract boolean boardFilledUp();
    public Player[][] board= new Player[][]{{null, null, null}, {null, null, null}, {null, null, null}};

}
