package engine;

import javax.swing.JFrame;

public class GameEngine {

    public static void main(String[] args) {
        JFrame window = new JFrame();
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setResizable(false);
        window.setTitle("CampusFlex - 2D Engine");

        GamePanel gamePanel = new GamePanel();
        window.add(gamePanel);
        
        // Causes this Window to be sized to fit the preferred size and layouts of its subcomponents
        window.pack();
        
        window.setLocationRelativeTo(null); // Center on screen
        window.setVisible(true);
        
        // Start the game thread (the game loop)
        gamePanel.startGameThread();
    }
}
