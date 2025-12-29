package gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Ellipse2D;
import logic.*;

public class BoardPanel extends JPanel {

    private Game game;
    private Board board;
    private GameWindow window;

    private int selectedRow = -1;
    private int selectedCol = -1;

    private final Color BOARD_BG = new Color(210, 180, 140);
    private final Color DARK_BG = new Color(50, 50, 50);
    private final Color GRID_COLOR = new Color(101, 67, 33);
    private final Color HIGHLIGHT_MOVE = new Color(0, 255, 0, 120);

    public BoardPanel(Game game, GameWindow window) {
        this.game = game;
        this.board = game.getBoard();
        this.window = window;

        setBackground(DARK_BG);

        // Listener de Clics
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                handleMouseClick(e.getX(), e.getY());
            }
        });

        // NUEVO: Listener de Movimiento (para el cursor)
        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                handleHover(e.getX(), e.getY());
            }
        });
    }

    // Lógica para cambiar el cursor
    private void handleHover(int x, int y) {
        int[] pos = getRowColAt(x, y);
        int r = pos[0];
        int c = pos[1];

        boolean interactable = false;
        if (r >= 0 && r < 7 && c >= 0 && c < 7) {
            Piece p = board.getPiece(r, c);
            if (p != null) {
                // Si es mi turno y paso sobre mi pieza
                if ((p instanceof Fox && game.isFoxTurn()) || 
                    (p instanceof Goose && !game.isFoxTurn())) {
                    interactable = true;
                }
            } else if (selectedRow != -1) {
                // O si tengo algo seleccionado y paso sobre un destino válido
                 if (board.isLegalMove(selectedRow, selectedCol, r, c, game.isFoxTurn())) {
                     interactable = true;
                 }
            }
        }

        if (interactable) {
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        } else {
            setCursor(Cursor.getDefaultCursor());
        }
    }

    private int[] getRowColAt(int x, int y) {
        int size = Math.min(getWidth(), getHeight());
        int cellSize = size / 7;
        int xOffset = (getWidth() - size) / 2;
        int yOffset = (getHeight() - size) / 2;
        int col = (x - xOffset) / cellSize;
        int row = (y - yOffset) / cellSize;
        return new int[]{row, col};
    }

    public void resetSelection() {
        selectedRow = -1;
        selectedCol = -1;
    }

    private void handleMouseClick(int x, int y) {
        int[] pos = getRowColAt(x, y);
        int row = pos[0];
        int col = pos[1];

        if (col < 0 || col >= 7 || row < 0 || row >= 7) return;
        if (!board.isValidPosition(row, col)) return;

        if (selectedRow == -1) {
            Piece p = board.getPiece(row, col);
            if (p != null) {
                boolean isFox = p instanceof Fox;
                if (isFox == game.isFoxTurn()) {
                    selectedRow = row;
                    selectedCol = col;
                    repaint();
                }
            }
        } else {
            if (row == selectedRow && col == selectedCol) {
                resetSelection();
                repaint();
                return;
            }

            // Guardar log antes del movimiento para la GUI
            String moveText = getMoveDescription(selectedRow, selectedCol, row, col);

            Game.MoveResult result = game.tryMove(selectedRow, selectedCol, row, col);
            
            if (result != Game.MoveResult.ILLEGAL) {
                // Movimiento exitoso
                window.addLog(moveText); // Agregar al historial visual
                processMoveResult(result);
            } else {
                Piece p = board.getPiece(row, col);
                if (p != null) {
                   boolean isFox = p instanceof Fox;
                   if (isFox == game.isFoxTurn()) {
                       selectedRow = row;
                       selectedCol = col;
                       repaint();
                   }
                }
            }
        }
    }
    
    private String getMoveDescription(int fr, int fc, int tr, int tc) {
        String who = game.isFoxTurn() ? "Zorro" : "Ganso";
        return who + ": (" + fr + "," + fc + ") -> (" + tr + "," + tc + ")";
    }

    private void processMoveResult(Game.MoveResult result) {
        switch (result) {
            case MOVED_TURN_CHANGED:
                resetSelection();
                window.updateUIState(); 
                break;
            case MOVED_STAY:
                resetSelection(); 
                window.addLog(">> ¡EL ZORRO PUEDE SALTAR DE NUEVO!");
                window.updateUIState();
                break;
                
            case GAME_OVER: //
                resetSelection();
                repaint();
                
               
                SoundManager.stopMusic(); 
                
            
                SoundManager.playSFX("assets/win.wav");

                String winnerName;
                String message;
                boolean foxWon;

                if (board.isFoxTrapped()) {
                    winnerName = "¡LOS GANSOS HAN GANADO!";
                    message = "El Zorro ha sido acorralado y no tiene escape.";
                    foxWon = false;
                } else {
                    winnerName = "EL ZORRO ES EL VENCEDOR!";
                    message = "Los Gansos han sido diezmados.";
                    foxWon = true;
                }
                
                new VictoryDialog(window, winnerName, message, foxWon);
                break;
                
            default: break;
        }
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int size = Math.min(getWidth(), getHeight());
        int cellSize = size / 7;
        int xOffset = (getWidth() - size) / 2;
        int yOffset = (getHeight() - size) / 2;

        for (int r = 0; r < 7; r++) {
            for (int c = 0; c < 7; c++) {
                if (board.isValidPosition(r, c)) {
                    int x = xOffset + c * cellSize;
                    int y = yOffset + r * cellSize;

                    g2.setColor(BOARD_BG);
                    g2.fillRect(x, y, cellSize, cellSize);
                    g2.setColor(GRID_COLOR);
                    g2.drawRect(x, y, cellSize, cellSize);
                    
                    if (selectedRow != -1) {
                        if (board.isLegalMove(selectedRow, selectedCol, r, c, game.isFoxTurn())) {
                            g2.setColor(HIGHLIGHT_MOVE);
                            g2.fillOval(x + cellSize/3, y + cellSize/3, cellSize/3, cellSize/3);
                        }
                    }
                }
            }
        }

        for (int r = 0; r < 7; r++) {
            for (int c = 0; c < 7; c++) {
                Piece p = board.getPiece(r, c);
                if (p != null) {
                    int x = xOffset + c * cellSize;
                    int y = yOffset + r * cellSize;
                    int padding = cellSize / 6;
                    
                    Color baseColor, lightColor;
                    if (p instanceof Fox) {
                        baseColor = new Color(200, 0, 0); 
                        lightColor = new Color(255, 100, 100);
                    } else {
                        baseColor = new Color(220, 220, 220); 
                        lightColor = Color.WHITE; 
                    }

                    Shape circle = new Ellipse2D.Double(x + padding, y + padding, cellSize - padding*2, cellSize - padding*2);
                    GradientPaint gradient = new GradientPaint(x + padding, y + padding, lightColor, x + cellSize - padding, y + cellSize - padding, baseColor);
                    
                    g2.setPaint(gradient);
                    g2.fill(circle);
                    g2.setColor(Color.BLACK);
                    g2.setStroke(new BasicStroke(2));
                    g2.draw(circle);
                    
                    if (r == selectedRow && c == selectedCol) {
                        g2.setColor(Color.YELLOW);
                        g2.setStroke(new BasicStroke(3));
                        g2.draw(circle);
                    }
                }
            }
        }
    }
}