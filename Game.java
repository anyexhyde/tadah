import java.awt.BorderLayout;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class Game extends JFrame implements Runnable {
    
    private static final long serialVersionUID = 1L;
    String hscore = Board.drawHighscores();
    final JLabel highscore = new JLabel("HIGHSCORES: " + Board.drawHighscores());

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Game());

    }

    @Override
    public void run() {
        Game game = new Game();
        game.setLocationRelativeTo(null);
        game.setVisible(true);
        // creates top level window
        JFrame frame = new JFrame("Tetris");
        JOptionPane.showMessageDialog(frame,
                "INSTRUCTIONS: Game starts immediately. "
                        + "To move left/right, press on left/right keys. "
                        + "To rotate, press up/down keys. "
                        + "To drop, press Space. " + 
                        "Game ends when there is no space for the falling block.",
                "Instructions", JOptionPane.PLAIN_MESSAGE);

        // add scoreboard
        frame.add(highscore, BorderLayout.WEST);
        JPanel panel = new JPanel();

        frame.add(panel);

        JButton b1 = new JButton("Pause/Resume");
        frame.add(b1, BorderLayout.EAST);

        Board board = new Board(this);
        panel.add(board);
        setSize(500, 700);

        b1.addActionListener((e) -> {
            board.pause();
        });

        // auto-sets frame size
        frame.pack();

        // tell frame to display itself
        frame.setVisible(true);
        board.start();
        // end program when window is closed
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    }

}