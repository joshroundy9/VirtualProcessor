import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.Test;

public class BitTests {
    @Test
    public void testAnd() {
        Bit bit = new Bit(false);
        Bit bit2 = new Bit(true);
        assertEquals(bit.and(bit2).getValue(), false);
        bit.set();
        assertEquals(bit.and(bit2).getValue(), true);
    }

    @Test
    public void testClear() {
        Bit bit = new Bit(true);
        bit.clear();
        assertEquals(bit.getValue(), false);
    }

    @Test
    public void testGetValue() {
        Bit bit = new Bit(true);
        assertEquals(bit.getValue(), true);
        bit.clear();
        assertEquals(bit.getValue(), false);
    }

    @Test
    public void testNot() {
        Bit bit = new Bit(true);
        assertEquals(bit.not().getValue(), false);
    }

    @Test
    public void testOr() {
        Bit bit = new Bit(true);
        Bit bit2 = new Bit(true);
        assertEquals(bit.or(bit2).getValue(), true);
        bit.clear();
        assertEquals(bit.or(bit2).getValue(), true);
        bit2.clear();
        assertEquals(bit.or(bit2).getValue(), false);
    }

    @Test
    public void testSet() {
        Bit bit = new Bit(false);
        bit.set();
        assertEquals(bit.getValue(), true);
    }

    @Test
    public void testToString() {
        Bit bit = new Bit(true);
        assertEquals(bit.toString(), "t");
        bit.clear();
        assertEquals(bit.toString(), "f");
    }

    @Test
    public void testToggle() {
        Bit bit = new Bit(true);
        bit.toggle();
        assertEquals(bit.getValue(), false);
        bit.toggle();
        assertEquals(bit.getValue(), true);
    }

    @Test
    public void testXor() {
        Bit bit = new Bit(true);
        Bit bit2 = new Bit(true);
        assertEquals(bit.xor(bit2).getValue(), false);
        bit2.toggle();
        assertEquals(bit.xor(bit2).getValue(), true);
    }
}
