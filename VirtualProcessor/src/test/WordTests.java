package test;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Random;

import org.junit.Test;

import main.Bit;
import main.Word;

public class WordTests {
    @Test
    public void testAnd() {
        /*
         * Makes two random words and tests each bit for the output.
         */
        var word = new Word();
        var word2 = new Word();
        var random = new Random();
        for (int i = 0; i < 32; i++) {
            word.setBit(i, new Bit(random.nextBoolean()));
            word2.setBit(i, new Bit(random.nextBoolean()));
        }
        var word3 = word.and(word2);
        for (int i = 0; i < 32; i++)
            assertEquals(word.getBit(i).getValue() && word2.getBit(i).getValue(), word3.getBit(i).getValue());

    }

    @Test
    public void testCopy() {
        var word = new Word();

        var random = new Random();
        for (int i = 0; i < 32; i++)
            word.setBit(i, new Bit(random.nextBoolean()));

        var word2 = new Word();
        word2.copy(word);
        for (int i = 0; i < 32; i++)
            assertEquals(word2.getBit(i).getValue(), word.getBit(i).getValue());

    }

    @Test
    public void testGetBit() {
        var word = new Word();
        word.setBit(3, new Bit(true));
        assertEquals(word.getBit(3).getValue(), true);
    }

    @Test
    public void testGetSigned() {
        var word = new Word();
        word.set(-15);
        assertEquals(word.getSigned(), -15);
        word.set(15);
        assertEquals(word.getSigned(), 15);
        word.set(-153523);
        assertEquals(word.getSigned(), -153523);

    }

    @Test
    public void testGetUnsigned() {
        Word word = new Word();
        word.setBit(31, new Bit(true));
        assertEquals(word.getUnsigned(), 1);
        word.setBit(28, new Bit(true));
        assertEquals(word.getUnsigned(), 9);
        word.setBit(29, new Bit(true));
        assertEquals(word.getUnsigned(), 13);
    }

    @Test
    public void testLeftShift() {
        Word word = new Word();
        word.setBit(31, new Bit(true));
        word = word.leftShift(2);
        assertEquals(word.getBit(29).getValue(), true);
        word = word.leftShift(1);
        assertEquals(word.getBit(28).getValue(), true);
    }

    @Test
    public void testNot() {
        Word word = new Word();
        word = word.not();
        assertEquals(word.getBit(5).getValue(), true);
    }

    @Test
    public void testOr() {
        Word word = new Word();
        Word word2 = new Word();
        Random random = new Random();
        for (int i = 0; i < 32; i++) {
            word.setBit(i, new Bit(random.nextBoolean()));
            word2.setBit(i, new Bit(random.nextBoolean()));
        }
        Word word3 = word.or(word2);
        for (int i = 0; i < 32; i++)
            assertEquals(word.getBit(i).getValue() || word2.getBit(i).getValue(), word3.getBit(i).getValue());
    }

    @Test
    public void testRightShift() {
        Word word = new Word();
        word.setBit(0, new Bit(true));
        word = word.rightShift(2);
        assertEquals(word.getBit(2).getValue(), true);
    }

    @Test
    public void testSet() {
        Word word = new Word(), word2 = new Word();
        word.setBit(31, new Bit(true));
        word.setBit(30, new Bit(true));
        word2.set(3);
        for (int i = 0; i < 32; i++)
            assertEquals(word.getBit(i).getValue(),
                    word2.getBit(i).getValue());
        
        //test twos compliment
        for(int i = 0;i<32;i++)
        word.setBit(i, new Bit(true));
        
        word.setBit(28, new Bit(false));
        word.setBit(29, new Bit(false));
        word.setBit(30, new Bit(false));
        
        word2.set(-15);
        for (int i = 0; i < 32; i++)
            assertEquals(word.getBit(i).getValue(),
                    word2.getBit(i).getValue());

    }

    @Test
    public void testSetBit() {
        Word word = new Word();
        assertEquals(word.getBit(3).getValue(), false);
        word.setBit(3, new Bit(true));
        assertEquals(word.getBit(3).getValue(), true);
    }

    @Test
    public void testXor() {
        Word word = new Word();
        Word word2 = new Word();
        Random random = new Random();
        for (int i = 0; i < 32; i++) {
            word.setBit(i, new Bit(random.nextBoolean()));
            word2.setBit(i, new Bit(random.nextBoolean()));
        }
        Word word3 = word.xor(word2);
        for (int i = 0; i < 32; i++)
            assertEquals(word.getBit(i).getValue() != word2.getBit(i).getValue(), word3.getBit(i).getValue());
    }
    @Test
    public void testIncrement()
    {
        Word word = new Word();
        word.set(0);
        word.increment();
        assertEquals(1, word.getSigned());
        word.increment();
        assertEquals(2, word.getSigned());
        word.set(125);
        word.increment();
        word.increment();
        word.increment();
        assertEquals(128, word.getSigned());
        word.set(41234124);
        word.increment();
        assertEquals(41234125, word.getSigned());
        word.set(-1);
        word.increment();
        assertEquals(0, word.getSigned());
        word.set(-1214);
        word.increment();
        assertEquals(-1213, word.getSigned());
        word.set(-12141231);
        word.increment();
        assertEquals(-12141230, word.getSigned());
    }
    @Test
    public void testDecrement()
    {
        Word word = new Word();
        word.set(2);
        word.decrement();
        assertEquals(1, word.getSigned());
        word.decrement();
        assertEquals(0, word.getSigned());
        word.set(125);
        word.decrement();
        word.decrement();
        word.decrement();
        assertEquals(122, word.getSigned());
        word.set(41234124);
        word.decrement();
        assertEquals(41234123, word.getSigned());
        word.set(-1);
        word.decrement();
        assertEquals(-2, word.getSigned());
        word.set(-1214);
        word.decrement();
        assertEquals(-1215, word.getSigned());
        word.set(-12141231);
        word.decrement();
        assertEquals(-12141232, word.getSigned());
    }
}
