package com.Hamza.RLCheckers.CheckersLogic;

import javax.swing.*;
import java.awt.*;
public class CheckersViewer extends JPanel {
    public stone[][] board;
    public int title_size = 80;

    public CheckersViewer(Board board) {
        this.board = board.getBoard();
        int size = board.getBoard().length * title_size;
        setPreferredSize(new Dimension(size, size));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        int n = board.length;

        for (int row = 0; row < n; row++) {
            for (int col = 0; col < n; col++) {
                // draw checkerboard tile
                boolean light = (row + col) % 2 == 0;
                g.setColor(light ? Color.LIGHT_GRAY : Color.DARK_GRAY);
                g.fillRect(col * title_size, row * title_size, title_size, title_size);

                // draw piece
                stone s = board[row][col];
                if (s != null) {
                    if (s.getTeam() == 'w') {
                        g.setColor(Color.WHITE);
                    } else {
                        g.setColor(Color.BLACK);
                    }
                    g.fillOval(col * title_size + 10, row * title_size + 10, title_size - 20, title_size - 20);

                    // king crown
                    if (s.kingStatus()) {
                        g.setColor(Color.YELLOW);
                        g.drawString("K", col * title_size + title_size / 2 - 5, row * title_size + title_size / 2 + 5);
                    }
                }
            }
        }
    }
}


