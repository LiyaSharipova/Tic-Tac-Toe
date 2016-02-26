package ru.kpfu.itis.sharipova;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Client -> Server           Server -> Client
 * ----------------           ----------------
 * MOVE <n>  (0 <= n <= 8)    WELCOME <char>  (char in {X, O})
 * QUIT                       VALID_MOVE
 * OTHER_PLAYER_MOVED <n>
 * VICTORY
 * DEFEAT
 * TIE
 * MESSAGE <text>
 */
public class TTTClient {
    private static final int PORT = 4242;
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;

    private final int size = 3;
    private final int buttomSize = 100;
    ArrayList<JButton> buttons = new ArrayList<>();
    private JFrame frame = new JFrame();
    private JLabel messageLabel = new JLabel();
    private JButton currentButton = new JButton();

    public TTTClient(String serverAddress) throws Exception {
        socket = new Socket(serverAddress, PORT);
        in = new BufferedReader(new InputStreamReader(
                socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream(), true);

        frame.setLayout(new GroupLayout(frame.getContentPane()));
        frame.setBounds(50, 50, buttomSize * size, buttomSize * size + 20);

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                JButton jb = new JButton();
                jb.setBounds(j * buttomSize, i * buttomSize, buttomSize, buttomSize);
                buttons.add(jb);
                jb.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        JButton jb1 = (JButton) e.getSource();
                        int i = jb1.getY() / buttomSize;
                        int j = jb1.getX() / buttomSize;
                        out.println("MOVE " + i + " " + j);
                        currentButton = jb1;
                    }
                });
                frame.add(jb);

            }
        }

        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.setVisible(true);
    }

    private JButton getButtonByPosition(int x, int y) {
        for (JButton button : buttons) {
            if (button.getX() == x && button.getY() == y) {
                return button;
            }
        }
        throw new IllegalStateException();
    }

    public void play() throws Exception {
        String response;
        String mark;
        String opponentMark;
        try {
            response = in.readLine();
            if (response.startsWith("WELCOME")) {
                mark = response.charAt(8) + "";
                opponentMark = (mark.equals("X")) ? "O" : "X";
                frame.setTitle("Tic Tac Toe - Player " + mark);

                while (true) {
                    response = in.readLine();
                    if (response.startsWith("VALID_MOVE")) {
                        messageLabel.setText("Valid move, please wait");
                        currentButton.setText(mark + "");
                        currentButton.setEnabled(false);
                    } else if (response.startsWith("OPPONENT_MOVED")) {
                        int i = Integer.parseInt(response.substring(15, 16));
                        int j = Integer.parseInt(response.substring(17, 18));
                        JButton jButton = getButtonByPosition(j * buttomSize, i * buttomSize);
                        jButton.setText(opponentMark);
                        jButton.setEnabled(false);

                        messageLabel.setText("Opponent moved, your turn");
                    } else if (response.startsWith("VICTORY")) {
                        messageLabel.setText("You win");
                        break;
                    } else if (response.startsWith("DEFEAT")) {
                        messageLabel.setText("You lose");
                        break;
                    } else if (response.startsWith("TIE")) {
                        messageLabel.setText("You tied");
                        break;
                    } else if (response.startsWith("MESSAGE")) {
                        messageLabel.setText(response.substring(8));
                    }
                }
            }
            out.println("QUIT");
        } finally {
            socket.close();
        }
    }

    private boolean wantsToPlayAgain() {
        int response = JOptionPane.showConfirmDialog(frame,
                messageLabel.getText() + "\n Want to play again?",
                "Tic Tac Toe is Fun Fun Fun",
                JOptionPane.YES_NO_OPTION);
        frame.dispose();
        return response == JOptionPane.YES_OPTION;
    }

    public static void main(String[] args) throws Exception {
        System.out.println("Serever IP (if 'localhost' press ENTER)");
        Scanner sc = new Scanner(System.in);
        String s = sc.nextLine();
        String ip = (s.equals("")) ? "localhost" : s;

        while (true) {
            TTTClient client = new TTTClient(ip);
            client.play();
            if (!client.wantsToPlayAgain()) break;
        }
    }
}