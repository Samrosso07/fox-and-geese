package gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;

public class VictoryDialog extends JDialog {

    private final Color FOX_COLOR_1 = new Color(60, 0, 0);   // Rojo oscuro
    private final Color FOX_COLOR_2 = new Color(30, 0, 0);   // Casi negro
    private final Color GOOSE_COLOR_1 = new Color(20, 30, 40); // Azul oscuro
    private final Color GOOSE_COLOR_2 = new Color(10, 15, 20); // Gris oscuro

    private final Color GOLD = new Color(218, 165, 32);
    private final Color SILVER = new Color(192, 192, 192);

    public VictoryDialog(JFrame parent, String winnerTitle, String message, boolean foxWon) {
        super(parent, "Fin de la Partida", true);
        
        setUndecorated(true);
        
        // 2. Definir tamaño
        setSize(450, 550);
        setLocationRelativeTo(parent);

        // 3. Dar forma redondeada a la ventana
        setShape(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 30, 30));

        // 4. Panel Principal con Degradado y Borde
        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Degradado de fondo
                Color c1 = foxWon ? FOX_COLOR_1 : GOOSE_COLOR_1;
                Color c2 = foxWon ? FOX_COLOR_2 : GOOSE_COLOR_2;
                GradientPaint gp = new GradientPaint(0, 0, c1, 0, getHeight(), c2);
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);

                // Borde (Dorado para Zorro, Plateado para Gansos)
                g2.setColor(foxWon ? GOLD : SILVER);
                g2.setStroke(new BasicStroke(4));
                g2.drawRoundRect(2, 2, getWidth() - 4, getHeight() - 4, 30, 30);
            }
        };
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20)); // Margen interno
        add(mainPanel);

        // --- CONTENIDO ---

        // Título
        JLabel titleLabel = new JLabel(winnerTitle.toUpperCase(), SwingConstants.CENTER);
        titleLabel.setFont(new Font("Serif", Font.BOLD, 28));
        titleLabel.setForeground(foxWon ? GOLD : SILVER);
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        // Imagen Central
        JLabel imageLabel = new JLabel();
        imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        String imagePath = foxWon ? "assets/fox_win.png" : "assets/goose_win.png";
        ImageIcon icon = new ImageIcon(imagePath);
        if (icon.getImageLoadStatus() == MediaTracker.COMPLETE) {
            Image img = icon.getImage();
            // Escalar imagen manteniendo proporción
            Image newImg = img.getScaledInstance(250, 250, java.awt.Image.SCALE_SMOOTH);
            imageLabel.setIcon(new ImageIcon(newImg));
            // Sombra o borde a la imagen (opcional)
            imageLabel.setBorder(BorderFactory.createEmptyBorder(20,0,20,0));
        } else {
            imageLabel.setText("<html><center><h1>🏆</h1></center></html>");
            imageLabel.setForeground(Color.WHITE);
        }
        mainPanel.add(imageLabel, BorderLayout.CENTER);

        // Panel Inferior (Mensaje + Botón)
        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.Y_AXIS));
        bottomPanel.setOpaque(false); // Transparente

        // Mensaje descriptivo
        JLabel msgLabel = new JLabel("<html><center>" + message + "</center></html>", SwingConstants.CENTER);
        msgLabel.setForeground(new Color(220, 220, 220));
        msgLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        msgLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        bottomPanel.add(msgLabel);

        bottomPanel.add(Box.createRigidArea(new Dimension(0, 25))); // Espacio

        // Botón Personalizado
        JButton closeButton = createStyledButton("VOLVER AL MENÚ", foxWon);
        closeButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        closeButton.addActionListener(e -> {
            dispose();       // Cierra diálogo
            parent.dispose();// Cierra ventana de juego
            new MainMenu();  // Abre menú
        });
        bottomPanel.add(closeButton);

        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        setVisible(true);
    }

    private JButton createStyledButton(String text, boolean foxWon) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("SansSerif", Font.BOLD, 14));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setOpaque(true);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        Color baseColor = foxWon ? GOLD : SILVER;
        Color textColor = new Color(40, 40, 40);

        btn.setBackground(baseColor);
        btn.setForeground(textColor);
        
        // Tamaño fijo para el botón
        btn.setMaximumSize(new Dimension(200, 40));
        btn.setPreferredSize(new Dimension(200, 40));

        // Efecto Hover simple
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent evt) {
                btn.setBackground(Color.WHITE);
            }
            public void mouseExited(MouseEvent evt) {
                btn.setBackground(baseColor);
            }
        });
        return btn;
    }
}