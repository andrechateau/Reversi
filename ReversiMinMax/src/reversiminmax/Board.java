/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package reversiminmax;

import java.awt.Point;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author Andre Chateaubriand
 */
public class Board {

    public static final byte FREE = 0;
    public static final byte WHITE = 1;
    public static final byte BLACK = 2;
    private static final int[][] regionValue = {
        {5, -1, 1, 1, 1, 1, -1, 5},
        {-1, -2, 1, 1, 1, 1, -2, -1},
        {1, 1, 2, 2, 2, 2, 1, 1},
        {1, 1, 2, 2, 2, 2, 1, 1},
        {1, 1, 2, 2, 2, 2, 1, 1},
        {1, 1, 2, 2, 2, 2, 1, 1},
        {-1, -2, 1, 1, 1, 1, -2, -1},
        {5, -1, 1, 1, 1, 1, -1, 5}
    };

    public byte[][] board;

    public Board() {
        byte[][] bd = {
            {FREE, FREE, FREE, FREE, FREE, FREE, FREE, FREE
            },
            {FREE, FREE, FREE, FREE, FREE, FREE, FREE, FREE
            },
            {FREE, FREE, FREE, FREE, FREE, FREE, FREE, FREE
            },
            {FREE, FREE, FREE, WHITE, BLACK, FREE, FREE, FREE
            },
            {FREE, FREE, FREE, BLACK, WHITE, FREE, FREE, FREE
            },
            {FREE, FREE, FREE, FREE, FREE, FREE, FREE, FREE
            },
            {FREE, FREE, FREE, FREE, FREE, FREE, FREE, FREE
            },
            {FREE, FREE, FREE, FREE, FREE, FREE, FREE, FREE
            }
        };
        this.board = bd;
    }

    public List<Point> getLineFlipped(byte color, int i, int j, int smX, int smY) {
        byte player = color == WHITE ? WHITE : BLACK;
        byte enemy = color == WHITE ? BLACK : WHITE;
        List<Point> flipped = new LinkedList<>();
        int x = j + smX;
        int y = i + smY;
        while (x < board[1].length && x >= 0 && y < board.length && y > 0) {
            if (board[y][x] == FREE) {
                return new LinkedList<>();
            } else if (board[y][x] == enemy) {
                flipped.add(new Point(y, x));
            } else if (board[y][x] == player) {
                return flipped;
            }
            x += smX;
            y += smY;
        }
        return new LinkedList<>();
    }

    public boolean isValid(byte color, int i, int j, int smX, int smY) {
        byte player = color == WHITE ? WHITE : BLACK;
        byte enemy = color == WHITE ? BLACK : WHITE;
        boolean ok = false;
        boolean black = false;
        int x = j + smX;
        int y = i + smY;
        while (x < board[1].length && x >= 0 && y < board.length && y > 0) {
            if (board[y][x] == FREE) {
                return false;
            } else if (board[y][x] == enemy) {
                black = true;
            } else if (board[y][x] == player) {
                if (black) {
                    return true;
                } else {
                    return false;
                }
            }
            x += smX;
            y += smY;
        }
        return false;
    }

    public List<Point> getFlipped(byte color, int i, int j) {
        List<Point> flipped = new LinkedList<>();
        if (board[i][j] == FREE) {
            //UP
            flipped.addAll(getLineFlipped(color, i, j, 0, -1));
            //DOWN
            flipped.addAll(getLineFlipped(color, i, j, 0, +1));
            //LEFT
            flipped.addAll(getLineFlipped(color, i, j, -1, 0));
            //RIGHT
            flipped.addAll(getLineFlipped(color, i, j, +1, 0));
            //UP
            flipped.addAll(getLineFlipped(color, i, j, +1, -1));
            //DOWN
            flipped.addAll(getLineFlipped(color, i, j, -1, +1));
            //LEFT
            flipped.addAll(getLineFlipped(color, i, j, -1, -1));
            //RIGHT
            flipped.addAll(getLineFlipped(color, i, j, +1, +1));
        }
        return flipped;
    }

    public List<Point> getValidPlays(byte color) {
        List<Point> valid = new LinkedList<>();
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[0].length; j++) {
                if (board[i][j] == FREE) {
                    boolean ok = false;
                    //UP
                    ok = ok || isValid(color, i, j, 0, -1);
                    //DOWN
                    ok = ok || isValid(color, i, j, 0, +1);
                    //LEFT
                    ok = ok || isValid(color, i, j, -1, 0);
                    //RIGHT
                    ok = ok || isValid(color, i, j, +1, 0);
                    //UP
                    ok = ok || isValid(color, i, j, +1, -1);
                    //DOWN
                    ok = ok || isValid(color, i, j, -1, +1);
                    //LEFT
                    ok = ok || isValid(color, i, j, -1, -1);
                    //RIGHT
                    ok = ok || isValid(color, i, j, +1, +1);
                    if (ok) {
                        valid.add(new Point(i, j));
                    }
                }
            }
        }
        return valid;
    }

    public void play(byte color, int i, int j) {
        try {
            List<Point> l = getValidPlays(color);
            boolean ok = false;
            for (Point point : l) {
//            System.out.println(i+" "+j+" \t "+ point.y+" "+point.x);
                if (point.y == j && point.x == i) {
                    ok = true;
                    break;
                }
            }
            if (ok) {
                l = getFlipped(color, i, j);
                for (Point point : l) {
//                System.out.println(point + " \t " + color);
                    board[point.x][point.y] = color;
                }
                board[i][j] = color;
            } else {
                throw new Exception("Jogada Inválida");
            }
        } catch (Exception ex) {
            System.out.println(ex.getMessage());

        }
    }

    public void print() {
        System.out.println(" \t0 1 2 3 4 5 6 7");
        int i = 0;
        for (byte[] bs : board) {
            System.out.print(i + "\t");
            i++;
            for (byte c : bs) {
                System.out.print(c + " ");
            }
            System.out.println();
        }

    }

    public int getPlayPoint(byte color) {

        int score = 0;
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[0].length; j++) {
                if (board[i][j] == color) {
                    score += regionValue[i][j];
                }
            }
        }
        return score;
    }

    public int getPiecesCount(byte color) {
        int bix = 0;
        for (byte[] bs : board) {
            for (byte b : bs) {
                if (b == color) {
                    bix++;
                }
            }
        }
        return bix;
    }

    public static void main(String[] args) {
        Board b = new Board();
        List<Point> l = b.getValidPlays(WHITE);
        for (Point point : l) {
            // b.board[point.x][point.y] = 5;
        }
        byte[][] k = {
            {0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 2, 1, 0},
            {0, 0, 0, 0, 2, 2, 1, 0},
            {0, 0, 0, 0, 2, 2, 1, 0},
            {0, 0, 0, 1, 1, 1, 1, 0},
            {0, 0, 0, 0, 1, 1, 1, 0},
            {0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0}
        };
        b.board = k;
        b.print();
        System.out.println("-------------");
        b.play(WHITE, 1, 4);
        b.print();
    }

    public Board clone() {
        Board k = new Board();
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[0].length; j++) {
                k.board[i][j] = board[i][j];
            }
        }
        return k;
    }
}
