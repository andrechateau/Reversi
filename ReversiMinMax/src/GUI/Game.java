/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package GUI;

import java.awt.Point;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import org.lwjgl.util.ReadableColor;
import org.newdawn.slick.BasicGame;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.gui.AbstractComponent;
import org.newdawn.slick.gui.ComponentListener;
import org.newdawn.slick.gui.MouseOverArea;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;
import reversiminmax.Board;
import static reversiminmax.Board.WHITE;

/**
 *
 * @author Andre Chateaubriand
 */
public class Game extends BasicGame {

    Board b;
    private final int gridSize = 64;
    private final int offsetX = 32;
    private final int offsetY = 32;
    private final int distance = 4;
    private byte round = Board.WHITE;
    private List<MouseOverArea> validMovements;
    private Image blackPiece;
    private Image whitePiece;
    private Image background;
    public static boolean IA = true;
    private long lastTime = System.currentTimeMillis();

    public Game(String title) {
        super(title);
    }

    @Override
    public void init(GameContainer container) throws SlickException {
        round = Board.WHITE;
        validMovements = new LinkedList<>();
        b = new Board();
        List<Point> l = b.getValidPlays(WHITE);
        for (Point point : l) {
            // b.board[point.x][point.y] = 5;
        }
//        byte[][] k = {
//            {0, 0, 0, 0, 0, 0, 0, 0},
//            {0, 0, 0, 0, 0, 2, 1, 0},
//            {0, 0, 0, 0, 2, 2, 1, 0},
//            {0, 0, 0, 0, 2, 2, 1, 0},
//            {0, 0, 0, 1, 1, 1, 1, 0},
//            {0, 0, 0, 0, 1, 1, 1, 0},
//            {0, 0, 0, 0, 0, 0, 0, 0},
//            {0, 0, 0, 0, 0, 0, 0, 0}
//        };
//        b.board = k;
        //b.play(WHITE, 1, 4);
        blackPiece = new Image("res/black.png");
        blackPiece = blackPiece.getScaledCopy(gridSize - (2 * distance), gridSize - (2 * distance));
        whitePiece = new Image("res/white.png");
        whitePiece = whitePiece.getScaledCopy(gridSize - (2 * distance), gridSize - (2 * distance));
        background = new Image("res/back.jpg");
        background = background.getScaledCopy(container.getWidth(), container.getHeight());
        updateValid(container);
    }

    private void updateValid(GameContainer container) {
        validMovements = new LinkedList<>();
        List<Point> l = b.getValidPlays(round);
        for (Point point : l) {
            MouseOverArea a = new MouseOverArea(container, round == Board.WHITE ? whitePiece : blackPiece, point.y * gridSize + distance + offsetX, point.x * gridSize + distance + offsetY);
            a.setNormalColor(new Color(100, 100, 100, 50));
            if (!IA || round == Board.WHITE) {
                //a.setMouseOverColor(new Color(100, 100, 100));
                a.setMouseOverColor(new Color(1f, 1f, 1f, 0.7f));
                a.setMouseOverImage(round == Board.WHITE ? whitePiece : blackPiece);
                a.addListener(new ComponentListener() {
                    @Override
                    public void componentActivated(AbstractComponent source) {
                        click(container, point.x, point.y);
                    }
                });
            } else {
                lastTime = System.currentTimeMillis();
                a.setMouseOverColor(new Color(100, 100, 100, 50));
                a.setMouseDownColor(new Color(100, 100, 100, 50));

            }
            validMovements.add(a);

        }

    }

    private void click(GameContainer container, int i, int j) {
        b.play(round, i, j);
        round = round == Board.WHITE ? Board.BLACK : Board.WHITE;
        updateValid(container);
        boolean has = false;
        for (byte[] bs : b.board) {
            for (byte c : bs) {
                if (c == Board.FREE) {
                    has = true;
                    break;
                }
            }
        }

        if (has) {
            if (b.getValidPlays(round).isEmpty()) {
                JOptionPane.showMessageDialog(null, "The " + (round == Board.WHITE ? "white" : "black") + " player has no possible movements.");
                round = round == Board.WHITE ? Board.BLACK : Board.WHITE;
                if (b.getValidPlays(round).isEmpty()) {
                    JOptionPane.showMessageDialog(null, "There's no possible movements.");
                    callEndGame(container);
                } else {
                    updateValid(container);
                }
            }
        } else {
            callEndGame(container);
        }
    }

    private void callEndGame(GameContainer container) {
        int wh = b.getPiecesCount(Board.WHITE);
        int bl = b.getPiecesCount(Board.BLACK);
        String winner = wh > bl ? "White" : "Black";
        winner = wh == bl ? "Nobody" : winner;
        JOptionPane.showMessageDialog(null, "End Game! \n " + winner + " won!");

        try {
            init(container);
        } catch (SlickException ex) {
            Logger.getLogger(Game.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void update(GameContainer container, int delta) throws SlickException {
        if (IA && round == Board.BLACK) {
            if (b.getValidPlays(round).size() > 0) {
                if (System.currentTimeMillis() > lastTime + 1000) {
                    lastTime = System.currentTimeMillis();
                    IA(container);
                }
            }
        }
    }

    public void IA(GameContainer container) {
        List<Point> l = b.getValidPlays(Board.BLACK);
        Point bestpoint = null;
        int bestScore = -1000;
        for (Point point : l) {
            Board k = b.clone();
            k.play(Board.BLACK, point.x, point.y);
            int score = minMax(k, false, 1, 2);
            if (score > bestScore) {
                bestScore = score;
                bestpoint = point;
            }
        }
        if (bestpoint != null) {
            click(container, bestpoint.x, bestpoint.y);
        } else {
            Random rnd = new Random();
            int n = l.size() - 1 > 0 ? rnd.nextInt(l.size() - 1) : 0;
            Point p = l.get(n);
            click(container, p.x, p.y);
        }
    }

    private Point bestMove() {
        return null;
    }

    private int minMax(Board k, boolean max, int currentDepth, int finalDepth) {
        if (currentDepth == finalDepth || k.getValidPlays(Board.BLACK).isEmpty()) {
            return k.getPlayPoint(Board.BLACK);
        } else if (max) {
            int bestScore = -1000;
            for (Point validPlay : k.getValidPlays(Board.BLACK)) {
                Board q = k.clone();
                q.play(Board.BLACK, validPlay.x, validPlay.y);
                int score = minMax(q, false, currentDepth + 1, finalDepth);
                if (score > bestScore) {
                    bestScore = score;
                }
            }
            return bestScore;

        } else {

            int bestScore = +1000;
            for (Point validPlay : k.getValidPlays(Board.WHITE)) {
                Board q = k.clone();
                q.play(Board.WHITE, validPlay.x, validPlay.y);
                int score = minMax(q, true, currentDepth + 1, finalDepth);
                if (score < bestScore) {
                    bestScore = score;
                }
            }
            return bestScore;
        }

    }

    @Override
    public void render(GameContainer container, Graphics g) throws SlickException {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        g.setColor(new Color(0, 200, 0));

        g.fillRect(0, 0, container.getWidth(), container.getHeight());
        g.setColor(new Color(0.8f, 0.8f, 0.8f, 0.5f));
        g.drawImage(background, 0, 0);
        for (int i = offsetX; i <= (8 * gridSize) + offsetX; i += gridSize) {
            g.drawLine(i, offsetX, i, (8 * gridSize) + offsetY);
        }
        for (int i = offsetY; i <= (8 * gridSize) + offsetY; i += gridSize) {
            g.drawLine(offsetY, i, (8 * gridSize) + offsetX, i);
        }
        for (int y = 0; y < b.board.length; y++) {
            for (int x = 0; x < b.board[1].length; x++) {
                if (b.board[x][y] != Board.FREE) {
                    g.setColor(b.board[x][y] == Board.WHITE ? Color.white : Color.black);

                    g.drawImage(b.board[x][y] == Board.WHITE ? whitePiece : blackPiece, y * gridSize + distance + offsetX, x * gridSize + distance + offsetY);
                }
            }
        }
        if (round == Board.WHITE) {
            g.setColor(new Color(0, 255, 255, 50));
        } else {
            g.setColor(new Color(255, 100, 0, 50));
        }
        List<Point> l = b.getValidPlays(round);
        for (Point point : l) {
            g.fillRect(offsetY + (point.y * gridSize) + 1, offsetX + (point.x * gridSize) + 1, gridSize - 1, gridSize - 1);
        }
        for (MouseOverArea validMovement : validMovements) {
            validMovement.render(container, g);
        }
        g.setColor(new Color(255, 255, 255, 200));

        g.drawString("White Pieces: " + b.getPiecesCount(Board.WHITE), offsetX + (gridSize * 9), 100);
        g.drawString("Black Pieces: " + b.getPiecesCount(Board.BLACK), offsetX + (gridSize * 9), 150);

    }

}
