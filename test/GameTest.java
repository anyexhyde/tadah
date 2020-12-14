import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Should explain what specific part of the game you are testing should be main,
 * core game state I really tried thinking of edge cases, only came up with 8
 */

public class GameTest {

    // test in Block.java
    /*
     * tests to see if every block in the array list can be retrieved at random
     * index
     */
    @Test
    public void testGetRandomBlock() {
        Block b = new Block();
        boolean outofbounds = false;
        int i = b.randomNumber();

        // i cannot be negative, so check if value ever exceeds 6
        for (int j = 0; j < 100; j++) {
            if (i > 6) {
                outofbounds = true;
            }
        }

        assertFalse(outofbounds);

    }

    // test in Block.java and Board.java
    /*
     * tests whether block can be rotated at the edge of gameboard such that the
     * rotated block is out of bounds (You shouldn't be able to)
     */
    @Test
    public void testRotateOutOfBounds() {
        Board bd = new Board();
        Block b = new Block();
        b.setShape(Block.Shapes.Line);
        boolean move = bd.canMove(b.rotateCW(), 10, 19);
        assertFalse(move);
    }

    // tests Block.java and Board.java
    // tests that block cannot move past the gameboard
    @Test
    public void testTryMovePastGrid() {
        Block bl = new Block();
        Board b = new Board();
        boolean move = b.canMove(bl, 11, 19);
        assertFalse(move);
    }

    // test in Board.java
    // attempt to clear row when the row isn't full
    @Test
    public void testDropNotClearLine() {
        Board b = new Board();
        Block.Shapes[] arr = b.getGameArray();
        b.addShape(arr);
        b.removeFullLines();
        assertFalse(arr[1] == Block.Shapes.Empty);
    }

    // test in Board.java
    // attempt to clear 2 rows at once
    @Test
    public void testClearedMultipleLines() {
        Board b = new Board();
        Block.Shapes[] arr = b.getGameArray();
        b.clearMultipleRows(arr);
        b.removeFullLines();
        assertEquals(arr[1], Block.Shapes.Empty);
    }

    // test in Board.java
    // clear 2 rows at once, check if score updates correctly
    @Test
    public void testClearLineScore() {
        Board b = new Board();
        Block.Shapes[] arr = b.getGameArray();
        b.clearMultipleRows(arr);
        b.removeFullLines();
        assertEquals(2, b.getFilledLines());
    }

    // test in Board.java
    // fill the gameboard = gameover, check if new block is still generated
    // when canMove is false, no block can be generated and game ends
    @Test
    public void testFillBlocksToTop() {
        Board b = new Board();
        Block bl = new Block();
        Block.Shapes[] arr = b.getGameArray();
        arr[(19 * 10) + 5] = Block.Shapes.Square;
        boolean move = b.canMove(bl, 5, 19);
        assertFalse(move);
    }

    // test in Board.java
    // checks if reset clears the gameboard
    @Test
    public void testReset() {
        Board b = new Board();
        int j = 1;
        b.reset();
        Block.Shapes[] arr = b.getGameArray();
        for (int i = 0; i < arr.length; i++) {
            if (arr[i] != Block.Shapes.Empty) {
                j = -1;
            }
        }
        assertEquals(1, j);
    }

}