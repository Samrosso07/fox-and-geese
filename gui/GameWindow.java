package gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import logic.*;

public class GameWindow extends JFrame {

    private Game game;
    private BoardPanel boardPanel;
    private JLabel statusLabel;
    private JTextArea logArea;
    private JButton undoButton;
    private JButton soundButton; // Referencia para cambiar el texto 🔇/🔊

    public GameWindow() {
        this.game = new Game();

        setTitle("Fox & Geese - Saga Nórdica");
        
        // Cargar tu ícono personalizado
        setIconImage(new ImageIcon("assets/logo.jpg").getImage()); 
        
        setSize(850, 650); // Un poco más ancho para que quepa todo bien
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // --- 1. PANEL SUPERIOR (ESTADO + AUDIO + RESET) ---
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(new Color(40, 40, 40));
        topPanel.setBorder(new EmptyBorder(10, 20, 10, 20));

        // Etiqueta de Turno (Izquierda)
        statusLabel = new JLabel("Turno: GANSOS");
        statusLabel.setForeground(Color.WHITE);
        statusLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        topPanel.add(statusLabel, BorderLayout.WEST);

        // Panel de Botones Superiores (Derecha)
        JPanel topButtonsPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        topButtonsPanel.setOpaque(false);

        // A. Botón de Audio
        soundButton = new JButton(SoundManager.isMuted() ? "🔇" : "🔊");
        styleDarkButton(soundButton);
        soundButton.setToolTipText("Activar/Desactivar Música");
        soundButton.addActionListener(e -> {
            SoundManager.toggleMute();
            // Actualizar icono visualmente
            soundButton.setText(SoundManager.isMuted() ? "🔇" : "🔊");
            soundButton.setFocusable(false);
        });
        topButtonsPanel.add(soundButton);

        // B. Botón Reiniciar
        JButton resetButton = new JButton("Reiniciar");
        styleDarkButton(resetButton);
        resetButton.addActionListener(e -> resetGame());
        topButtonsPanel.add(resetButton);

        topPanel.add(topButtonsPanel, BorderLayout.EAST);
        add(topPanel, BorderLayout.NORTH);


        // --- 2. PANEL CENTRAL (TABLERO) ---
        boardPanel = new BoardPanel(game, this);
        add(boardPanel, BorderLayout.CENTER);


        // --- 3. PANEL LATERAL (HISTORIAL + DESHACER) ---
        JPanel sidePanel = new JPanel();
        sidePanel.setLayout(new BorderLayout());
        sidePanel.setPreferredSize(new Dimension(220, 0));
        sidePanel.setBackground(new Color(230, 230, 230));
        sidePanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Área de Texto (Log)
        logArea = new JTextArea();
        logArea.setEditable(false);
        logArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane scrollPane = new JScrollPane(logArea);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Crónicas de Batalla"));
        sidePanel.add(scrollPane, BorderLayout.CENTER);

        // Botón Deshacer (Abajo del lateral)
        JPanel sideBottomPanel = new JPanel(new FlowLayout());
        sideBottomPanel.setOpaque(false);
        
        undoButton = new JButton("Deshacer");
        styleLightButton(undoButton); // Estilo claro para el panel lateral
        undoButton.setEnabled(false);
        undoButton.addActionListener(e -> undoMove());
        
        sideBottomPanel.add(undoButton);
        sidePanel.add(sideBottomPanel, BorderLayout.SOUTH);

        add(sidePanel, BorderLayout.EAST);

        setVisible(true);
        updateUIState(); // Estado inicial correcto
    }

    // --- MÉTODOS DE LÓGICA ---

    private void undoMove() {
        if (game.canUndo()) {
            game.undo();
            boardPanel.resetSelection();
            boardPanel.repaint();
            updateUIState();
            addLog("--- El tiempo retrocede ---");
        }
    }

    private void resetGame() {
        game.reset();
        boardPanel.resetSelection(); 
        logArea.setText(""); 
        updateUIState();
        boardPanel.repaint();
        addLog("--- Nueva Partida ---");
    }
    
    public void addLog(String text) {
        logArea.append(text + "\n");
        logArea.setCaretPosition(logArea.getDocument().getLength());
    }

    public void updateUIState() {
        // 1. Actualizar Turno
        if (game.isFoxTurn()) {
            statusLabel.setText("Turno: ZORRO (Rojo)");
            statusLabel.setForeground(new Color(255, 100, 100)); 
        } else {
            statusLabel.setText("Turno: GANSOS (Blanco)");
            statusLabel.setForeground(Color.WHITE);
        }
        
        // 2. Actualizar Botón Deshacer
        undoButton.setEnabled(game.canUndo());
        
        // 3. Asegurar que el botón de audio tenga el icono correcto
        soundButton.setText(SoundManager.isMuted() ? "🔇" : "🔊");
    }

    // --- ESTILOS VISUALES ---

    // Estilo para botones sobre fondo oscuro (Barra superior)
    private void styleDarkButton(JButton btn) {
        btn.setBackground(new Color(60, 60, 60)); 
        btn.setForeground(Color.WHITE);           
        btn.setFocusPainted(false);               
        btn.setFont(new Font("SansSerif", Font.BOLD, 14));
        btn.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(100, 100, 100)), 
            BorderFactory.createEmptyBorder(5, 15, 5, 15) 
        ));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR)); 
    }

    // Estilo para botones sobre fondo claro (Panel lateral)
    private void styleLightButton(JButton btn) {
        btn.setBackground(new Color(200, 200, 200)); 
        btn.setForeground(Color.BLACK);           
        btn.setFocusPainted(false);               
        btn.setFont(new Font("SansSerif", Font.BOLD, 14));
        btn.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(150, 150, 150)), 
            BorderFactory.createEmptyBorder(8, 20, 8, 20) 
        ));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR)); 
    }
}