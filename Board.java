import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;
import java.util.TreeMap;
import javax.swing.JPanel;
import javax.swing.Timer;
import javax.swing.JOptionPane;

public class Board extends JPanel implements ActionListener {

    private final int width = 10;
    private final int height = 20;
    // gameboard is an array of tetris blocks
    private Block.Shapes[] gameArray;

    // score is dependent on the removal of lines
    private int clearedLines = 0;
    // the difference between points and score:
    // points relevant only during runtime, then they become score
    private static int points;

    // highscores are stored in a Map with Integer score, String name
    private Map<Integer, String> highscores;
    private int highscore;

    // timer
    private final Timer timer = new Timer(1000, this);

    BufferedReader reader = null;

    // actionevent: if block is still falling, then move one line down
    // if block done falling, generate new block
    public void actionPerformed(ActionEvent e) {
        if (fallingDone) {
            // stop the falling, generate new piece
            fallingDone = false;
            makeNewBlock();
        } else {
            // move block one grid down
            moveOneRowDown();
        }
    }

    // block handling variables
    private Block current;
    private Block next;
    // x and y coordinates of the current falling piece
    private int currentX = 0;
    private int currentY = 0;

    // block falling and line clearing variables
    private boolean fallingDone = false;
    private boolean lineIsFull = true;
    private int filledLines = 0;

    // gameplay variables
    private boolean start = false;
    private boolean pause = false;
    private boolean gameOver = false;

    // board constructor and getters for purposes of testing
    public Board() {
        gameArray = new Block.Shapes[10 * 20];
        reset();

        // initialize new pieces
        current = new Block();
        next = new Block();
        current.setShape(Block.randomBlock());
        next.setShape(Block.randomBlock());
    }

    public Block.Shapes[] getGameArray() {
        return gameArray;
    }

    // Gameboard constructor
    public Board(Game game) {
        setFocusable(true);
        requestFocusInWindow();
        // set board array to width and height (10 wide, 20 tall)
        gameArray = new Block.Shapes[10 * 20];
        reset();

        // initialize new pieces
        current = new Block();
        next = new Block();
        current.setShape(Block.randomBlock());
        next.setShape(Block.randomBlock());

        // initiate the timer and variables relevant to scores
        timer.start();
        points = 0;
        highscores = new TreeMap<Integer, String>();
        highscore = 0;

        // KeyListener for arrow keys and spacebar
        addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (pause) {
                    return;
                }

                if (e.getKeyCode() == KeyEvent.VK_LEFT) {
                    canMove(current, currentX - 1, currentY);
                    return;
                } else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
                    canMove(current, currentX + 1, currentY);
                    return;
                } else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
                    canMove(current.rotateCW(), currentX, currentY);
                    return;
                } else if (e.getKeyCode() == KeyEvent.VK_UP) {
                    canMove(current.rotateCCW(), currentX, currentY);
                    return;
                } else if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                    drop();
                    return;
                }
            }
        });
    }

    // size of gameboard
    public Dimension getPreferredSize() {
        return new Dimension(500, 680);
    }

    // dimensions of the blocks
    private final int squareWidth() {
        return (int) 300 / width;
    }

    private final int squareHeight() {
        return (int) 600 / height;
    }

    // function returning the shape at the given x,y in gameArray
    private Block.Shapes shapeAt(int x, int y) {
        return gameArray[(y * width) + x];
    }
    
    public int getFilledLines() {
        return filledLines;
    }

    // clears board and points
    public void reset() {
        for (int i = 0; i < gameArray.length; i++) {
            gameArray[i] = Block.Shapes.Empty;
        }
        clearedLines = 0;
        points = 0;
        fallingDone = false;
    }

    // starts game, clears board, and initializes pieces and timer
    public void start() {
        Block.Shapes.initialize();
        start = true;
        reset();

        makeNewBlock();
        timer.start();
    }

    // stops the timer and dropping of block
    public void pause() {
        if (pause) {
            pause = false;
            timer.stop();
        } else if (!pause) {
            pause = true;
            timer.start();
        }
        repaint();
    }

    // paints
    public void paintComponent(Graphics g) {
        drawBoard(g);
        // paint blocks that have already dropped
        for (int i = 0; i < height; ++i) {
            for (int j = 0; j < width; ++j) {
                Block.Shapes shape = shapeAt(j, height - i - 1);
                if (shape != Block.Shapes.Empty) {
                    drawBlock(g, j * squareWidth(), 
                            (690 - height * squareHeight()) + i * squareHeight(), shape);
                }
            }
        }

        // paint current falling block
        if (current.getShape() != Block.Shapes.Empty) {

            for (int i = 0; i < 4; i++) {
                int x = currentX + current.getX(i);
                int y = currentY - current.getY(i);
                drawBlock(g, x * squareWidth(), 
                        (690 - height * squareHeight()) + (height - y - 1) * squareHeight(),
                        current.getShape());
            }
        }

        drawCurrentScore(g, points);
    }

    // draws gameboard
    public void drawBoard(Graphics g) {
        int size = 30;
        for (int row = 0; row <= 23; row++) {
            g.drawLine(0, row * size + 30, 10 * size, row * size + 30);
        }

        for (int col = 0; col <= 10; col++) {
            g.drawLine(col * size, 90, col * size, 25 * size);
        }

    }

    // draws blocks
    private void drawBlock(Graphics g, int x, int y, Block.Shapes shape) {
        g.setColor(Block.getColor(shape));
        g.fillRect(x, y, squareWidth(), squareHeight());
    }

    // draws and updates current score
    public void drawCurrentScore(Graphics g, int sc) {
        g.setColor(Color.BLACK);
        g.drawString("SCORE: " + String.valueOf(sc), 0, height);
    }

    // drops block
    private void drop() {
        int y = currentY;
        while (y > 0) {
            if (!canMove(current, currentX, y - 1)) {
                break;
            }
            y--;
        }
        addDroppedShape();
    }

    // shifts one row down
    private void moveOneRowDown() {
        if (!canMove(current, currentX, currentY - 1)) {
            addDroppedShape();
        }
    }

    // adds the falling block to the board array
    private void addDroppedShape() {
        for (int i = 0; i < 4; ++i) {
            int x = currentX + current.getX(i);
            int y = currentY - current.getY(i);
            gameArray[(y * width) + x] = current.getShape();
        }

        removeFullLines();
        makeNewBlock();

    }

    // addDroppedShape implemented for testing purposes
    public void addShape(Block.Shapes[] arr) {
        for (int i = 1; i < 10; i++) {
            arr[i] = Block.Shapes.Square;
        }
    }

    // implemented for testing purposes
    public void clearMultipleRows(Block.Shapes[] arr) {
        for (int i = 0; i <= 20; i++) {
            arr[i] = Block.Shapes.Square;
        }
    }

    // handles generating new pieces and ending game
    public void makeNewBlock() {
        current.setRandomShape();
        // center of the board
        currentX = width / 2;
        currentY = height - 1 + current.minY();

        // end game if can't move block at current location
        if (!canMove(current, currentX, currentY)) {
            current.shapeToIntArr(Block.Shapes.Empty);
            timer.stop();
            start = false;
            highscore = points;
            enterName();

        }
    }

    // returns false if trying to move past board, or if block touches another block
    public boolean canMove(Block block, int newcurrentX, int newcurrentY) {
        for (int i = 0; i < 4; ++i) {
            int x = newcurrentX + block.getX(i);
            int y = newcurrentY - block.getY(i);
            if (x < 0 || x >= 10 || y < 0 || y >= 20) {
                return false;
            }
            if (shapeAt(x, y) != Block.Shapes.Empty) {
                return false;
            }
        }

        current = block;
        currentX = newcurrentX;
        currentY = newcurrentY;
        repaint();
        return true;
    }

    // removes filled rows and updates score
    public void removeFullLines() {
        for (int i = height - 1; i >= 0; i--) {
            boolean lineIsFull = true;

            for (int j = 0; j < 10; j++) {
                if (shapeAt(j, i) == Block.Shapes.Empty) {
                    lineIsFull = false;
                    break;
                }
            }

            if (lineIsFull) {
                filledLines++;
                for (int k = i; k < 20 - 1; k++) {
                    for (int j = 0; j < 10; j++) {
                        gameArray[(k * 10) + j] = shapeAt(j, k + 1);
                    }
                }
                clearedLines = filledLines;
                points = clearedLines;
                fallingDone = true;
            }
        }
    }

    // joptionpane to enter user name and adds to highscore map
    public void enterName() {
        int i = -1;
        while (i < 0) {
            String name = JOptionPane.showInputDialog("GAME OVER! "
                    + "Enter username to save your highscore: ");
            if (name.length() > 0) {
                i = 1;
                highscores.put(points, name);
            } else if (name.length() == 0) {
                i = 1;
                highscores.put(points, "noname");
            }
        }
        writeHighscore();
        extractHighscores();
    }

    // writes highscore and name to scores.txt file
    public void writeHighscore() {
        FileWriter filewriter = null;
        PrintWriter printwriter = null;

        try {
            filewriter = new FileWriter(new File("Scores.txt"));
            printwriter = new PrintWriter(filewriter);
        } catch (IOException e) {
            e.printStackTrace();
        }

        int counter = 0;
        for (Map.Entry<Integer, String> entry : highscores.entrySet()) {
            if (counter > 5) {
                break;
            } else {
                printwriter.println("Score:" + entry.getKey() + "User:" + entry.getValue());
                counter++;
            }
        }

        printwriter.close();
    }

    // extracts the scores and names in scores.txt to a new TreeMap
    private static Map<Integer, String> output = new TreeMap<>();
    String[] names = null;
    int[] nameScore = null;
    int index = 0;

    public void extractHighscores() {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader("src/Scores.txt"));
            String readLine = reader.readLine();
            while (readLine != null) {
                String[] splitted = readLine.split("[Score:User:]");
                for (int i = 1; i < splitted.length; i += 2) {
                    nameScore[index] = Integer.parseInt(splitted[i - 1]);
                    names[index] = splitted[i];
                    output.put(nameScore[index], names[index]);
                    index++;
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // returns a String with the extracted names and scores from scores.txt
    private static String extractedHigh = null;

    public static String drawHighscores() {
        int c = 0;
        if (c < 5) {
            for (Map.Entry<Integer, String> entry : output.entrySet()) {
                extractedHigh += "Score: " + entry.getKey() + " User: " + entry.getValue();
                c++;
            }
        }
        return extractedHigh;
    }

}