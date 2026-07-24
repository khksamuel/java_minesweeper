import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class CellTest {
    @Test
    void revealReturnsSafeCodeWhenCellIsNotBomb() {
        Cell cell = new Cell();

        assertEquals(Grid.SAFE, cell.reveal());
    }

    @Test
    void revealReturnsBombCodeWhenCellIsBomb() {
        Cell cell = new Cell();
        cell.setBomb(true);

        assertEquals(Grid.BOMB, cell.reveal());
    }

    @Test
    void flagTogglesFlaggedState() {
        Cell cell = new Cell();

        assertFalse(cell.isFlagged);
        cell.flag();
        assertTrue(cell.isFlagged);
        cell.flag();
        assertFalse(cell.isFlagged);
    }

    @Test
    void settersUpdateCellState() {
        Cell cell = new Cell();

        cell.setBomb(true);
        cell.setNeighbourBombs(3);

        assertTrue(cell.isBomb());
        assertEquals(3, cell.neighbourBombs);
    }
}
