import java.util.ArrayList;
import javax.swing.JComponent;
import java.awt.Color;

public class Block extends JComponent {

    private Shapes piece;
    private int[][] coordinates;

    // enum for all seven possible types of Block shapes, with their respective
    // colors
    // Block shapes are stored in 4x2 2D arrays
    public enum Shapes {
        Empty(new int[][] { { 0, 0 }, { 0, 0 }, { 0, 0 }, { 0, 0 } }, Color.black),
        Square(new int[][] { { 0, 0 }, { 1, 0 }, { 0, 1 }, { 1, 1 } }, Color.green),
        Line(new int[][] { { 0, -1 }, { 0, 0 }, { 0, 1 }, { 0, 2 } }, Color.pink),
        flippedChair(new int[][] { { 0, -1 }, { 0, 0 }, { -1, 0 }, { -1, 1 } }, Color.cyan),
        Chair(new int[][] { { 0, -1 }, { 0, 0 }, { 1, 0 }, { 1, 1 } }, Color.red),
        T(new int[][] { { -1, 0 }, { 0, 0 }, { 1, 0 }, { 0, 1 } }, Color.yellow),
        flippedL(new int[][] { { 1, -1 }, { 0, -1 }, { 0, 0 }, { 0, 1 } }, Color.blue),
        L(new int[][] { { -1, -1 }, { 0, -1 }, { 0, 0 }, { 0, 1 } }, Color.orange);

        private int[][] coordinates;
        private Color color;

        // ArrayList storing all block shapes to pick at random
        private static ArrayList<Shapes> blockList = new ArrayList<>();

        // Shapes constructor
        private Shapes(int[][] coordinates, Color c) {
            this.coordinates = coordinates;
            color = c;
        }

        // initialize() adds the seven shapes to the ArrayList blockList
        public static void initialize() {
            blockList.add(Chair);
            blockList.add(flippedChair);
            blockList.add(Square);
            blockList.add(flippedL);
            blockList.add(L);
            blockList.add(T);
            blockList.add(Line);
        }
    }

    // Block constructor
    public Block() {
        // each block is a 4x2 2D array
        coordinates = new int[4][2];
        // convert the shape to array coordinates
        shapeToIntArr(Shapes.Empty);
    }

    // stores the Shape to the class variable piece
    public void setShape(Block.Shapes s) {
        piece = s;
    }

    // retrieves the Shape stored in piece
    public Shapes getShape() {
        return piece;
    }

    // get a random Shape from the 7 possible Shapes
    public static Shapes randomBlock() {
        Shapes.initialize();
        int index = (int) Math.floor(Math.random() * 7);
        return Shapes.blockList.get(index);

    }

    // implemented for testing purposes
    public int randomNumber() {
        int i = (int) Math.floor(Math.random() * 7);
        return i;
    }

    // after getting random Shape, convert Shape to array coordinates
    public void setRandomShape() {
        shapeToIntArr(randomBlock());
    }

    // converts Shape to 2D array coordinates,
    // stores to class variable coordinates
    public int[][] shapeToIntArr(Shapes shape) {
        int[][] arr = new int[4][2];
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 2; ++j) {
                arr[i][j] = shape.coordinates[i][j];
            }
        }

        piece = shape;
        coordinates = arr;
        return coordinates;
    }

    // sets the specified row to a given value x
    private void setX(int i, int x) {
        coordinates[i][0] = x;
    }

    // sets the specified column to a given value y
    private void setY(int i, int y) {
        coordinates[i][1] = y;
    }

    // x coordinate getter (row)
    public int getX(int i) {
        return coordinates[i][0];
    }

    // y coordinate getter (col)
    public int getY(int i) {
        return coordinates[i][1];
    }

    // coordinates getter for testing purposes
    public int[][] getCoordinates() {
        return coordinates;
    }

    // retrieves color of the input Shape
    public static Color getColor(Shapes s) {
        Color color = Color.black;
        if (s == Shapes.Empty) {
            return Color.black;
        }
        if (s == Shapes.Square) {
            color = Color.green;
        }
        if (s == Shapes.Line) {
            color = Color.pink;
        }
        if (s == Shapes.Chair) {
            color = Color.red;
        }
        if (s == Shapes.flippedChair) {
            color = Color.cyan;
        }
        if (s == Shapes.L) {
            color = Color.orange;
        }
        if (s == Shapes.flippedL) {
            color = Color.blue;
        }
        if (s == Shapes.T) {
            color = Color.yellow;
        }

        return color;
    }

    // iterates over piece
    // determines minimum starting X coordinate of the piece
    public int minX() {
        int min = coordinates[0][0];
        for (int x = 0; x < 4; x++) {
            min = Math.min(min, coordinates[x][0]);
        }
        return min;
    }

    // iterates over piece
    // determines minimum starting Y coordinate of the piece
    public int minY() {
        int min = coordinates[0][1];
        for (int y = 0; y < 4; y++) {
            min = Math.min(min, coordinates[y][1]);
        }
        return min;
    }

    // functions handling block CW and CCW rotation
    public Block rotateCCW() {
        // create a new block for the rotated piece
        Block rotated = new Block();
        for (int i = 0; i < 4; ++i) {
            rotated.setX(i, getY(i));
            rotated.setY(i, -getX(i));
        }
        // update the piece to rotated form
        rotated.piece = piece;
        return rotated;
    }

    public Block rotateCW() {
        Block rotated = new Block();
        for (int i = 0; i < 4; ++i) {
            rotated.setX(i, -getY(i));
            rotated.setY(i, getX(i));
        }

        rotated.piece = piece;
        return rotated;
    }
}