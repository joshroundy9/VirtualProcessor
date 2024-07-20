package test;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.Test;

import main.Bit;

public class BitTests {
    @Test
    public void testAnd() {
        var bit = new Bit(false);
        var bit2 = new Bit(true);
        assertEquals(bit.and(bit2).getValue(), false);
        bit.set();
        assertEquals(bit.and(bit2).getValue(), true);
    }

    @Test
    public void testClear() {
        var bit = new Bit(true);
        bit.clear();
        assertEquals(bit.getValue(), false);
    }

    @Test
    public void testGetValue() {
        var bit = new Bit(true);
        assertEquals(bit.getValue(), true);
        bit.clear();
        assertEquals(bit.getValue(), false);
    }

    @Test
    public void testNot() {
        var bit = new Bit(true);
        assertEquals(bit.not().getValue(), false);
    }

    @Test
    public void testOr() {
        var bit = new Bit(true);
        var bit2 = new Bit(true);
        assertEquals(bit.or(bit2).getValue(), true);
        bit.clear();
        assertEquals(bit.or(bit2).getValue(), true);
        bit2.clear();
        assertEquals(bit.or(bit2).getValue(), false);
    }

    @Test
    public void testSet() {
        var bit = new Bit(false);
        bit.set();
        assertEquals(bit.getValue(), true);
    }

    @Test
    public void testToString() {
        var bit = new Bit(true);
        assertEquals(bit.toString(), "t");
        bit.clear();
        assertEquals(bit.toString(), "f");
    }

    @Test
    public void testToggle() {
        var bit = new Bit(true);
        bit.toggle();
        assertEquals(bit.getValue(), false);
        bit.toggle();
        assertEquals(bit.getValue(), true);
    }

    @Test
    public void testXor() {
        var bit = new Bit(true);
        var bit2 = new Bit(true);
        assertEquals(bit.xor(bit2).getValue(), false);
        bit2.toggle();
        assertEquals(bit.xor(bit2).getValue(), true);
    }
}
