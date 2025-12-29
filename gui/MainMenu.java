package gui;

import javax.swing.*;
import java.awt.*;
import logic.SoundManager;

public class MainMenu extends JFrame {

    public MainMenu() {
        setTitle("Fox & Geese: Saga Nórdica");
        // Ajustamos el tamaño a 600x600 para que la imagen cuadrada se vea bien
        setIconImage(new ImageIcon("assets/logo.jpg").getImage());
        setSize(600, 600); 
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Iniciar música
        SoundManager.playMusic("assets/music.wav");

        // Panel con la imagen de fondo
        JPanel backgroundPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                ImageIcon bgIcon = new ImageIcon("assets/menu_bg.jpg");
                if (bgIcon.getImageLoadStatus() == MediaTracker.COMPLETE) {
                    g.drawImage(bgIcon.getImage(), 0, 0, getWidth(), getHeight(), this);
                }
            }
        };
     
        backgroundPanel.setLayout(new GridBagLayout());
        add(backgroundPanel);

        // --- CREAR EL BOTÓN INVISIBLE ---
        JButton startButton = new JButton();
        
    
        startButton.setOpaque(false);
        startButton.setContentAreaFilled(false);
        startButton.setBorderPainted(false);
        startButton.setFocusPainted(false);
        

        startButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

 
        startButton.setPreferredSize(new Dimension(100, 100));

        // Acción al hacer clic
        startButton.addActionListener(e -> {
            new GameWindow();
            dispose();
        });

        // --- POSICIONARLO ---
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0; // Empuja hacia abajo
        gbc.anchor = GridBagConstraints.SOUTH; // Anclar al fondo
        gbc.insets = new Insets(0, 0, 30, 0); // Margen inferior (ajusta este número para subir/bajar el botón)

        backgroundPanel.add(startButton, gbc);

        setVisible(true);
    }
}