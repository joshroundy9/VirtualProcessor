import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.Test;

public class ALUTests {
   /* @Test
    public void testAdd() {
        ALU alu = new ALU();
        alu.op1.set(-24);
        alu.op2.set(-46);
        alu.add();
        assertEquals(-70, alu.result.getSigned());
        alu.op1.set(24);
        alu.op2.set(46);
        alu.add();
        assertEquals(70, alu.result.getSigned());
        alu.op1.set(423200);
        alu.op2.set(8654100);
        alu.add();
        assertEquals(9077300, alu.result.getSigned());
        alu.op1.set(24);
        alu.op2.set(-46);
        alu.add();
        assertEquals(-22, alu.result.getSigned());
        alu.op1.set(4325712);
        alu.op2.set(-232123);
        alu.add();
        assertEquals(4093589, alu.result.getSigned());
        alu.op2.set(232123);
        alu.op1.set(-4325712);
        alu.add();
        assertEquals(-4093589, alu.result.getSigned());
        alu.op1.set(432571223);
        alu.op2.set(-232123135);
        alu.add();
        assertEquals(200448088, alu.result.getSigned());
        alu.op1.set(-432571223);
        alu.op2.set(232123135);
        alu.add();
        assertEquals(-200448088, alu.result.getSigned());
        alu.op1.set(0);
        alu.op2.set(0);
        alu.add();
        assertEquals(0, alu.result.getSigned());
    }

    @Test
    public void testPositiveIsLarger() {
        ALU alu = new ALU();
        Word word = new Word(), word2 = new Word();
        word.set(35);
        word2.set(-30);
        assertEquals(true, alu.isPositiveLarger(word, word2));
        word.set(35);
        word2.set(36);
        assertEquals(false, alu.isPositiveLarger(word, word2));
    }

    @Test
    public void testGetTwosComplement() {
        Word word = new Word();
        word.set(35);
        Word twosComplement = ALU.getTwosCompliment(word);
        assertEquals(-35, twosComplement.getSigned());
        word.set(456325);
        twosComplement = ALU.getTwosCompliment(word);
        assertEquals(-456325, twosComplement.getSigned());
        word.set(56352325);
        twosComplement = ALU.getTwosCompliment(word);
        assertEquals(-56352325, twosComplement.getSigned());
        word.set(-56352325);
        twosComplement = ALU.getTwosCompliment(word);
        assertEquals(56352325, twosComplement.getSigned());

    }*/

    @Test
    public void testDoOperation() {
        // mult tests
        ALU alu = new ALU();
        alu.op1.set(29);
        alu.op2.set(9);
        alu.doOperation(new Bit[] { new Bit(false), new Bit(true), new Bit(true), new Bit(true) });
        assertEquals(261, alu.result.getSigned());
        alu.op1.set(33);
        alu.op2.set(123);
        alu.doOperation(new Bit[] { new Bit(false), new Bit(true), new Bit(true), new Bit(true) });
        assertEquals(4059, alu.result.getSigned());
        alu.op1.set(0);
        alu.op2.set(123);
        alu.doOperation(new Bit[] { new Bit(false), new Bit(true), new Bit(true), new Bit(true) });
        assertEquals(0, alu.result.getSigned());
        alu.op1.set(0);
        alu.op2.set(-123);
        alu.doOperation(new Bit[] { new Bit(false), new Bit(true), new Bit(true), new Bit(true) });
        assertEquals(0, alu.result.getSigned());
        alu.op1.set(0);
        alu.op2.set(0);
        alu.doOperation(new Bit[] { new Bit(false), new Bit(true), new Bit(true), new Bit(true) });
        assertEquals(0, alu.result.getSigned());
        alu.op1.set(534);
        alu.op2.set(-213);
        alu.doOperation(new Bit[] { new Bit(false), new Bit(true), new Bit(true), new Bit(true) });
        assertEquals(-113742, alu.result.getSigned());
        alu.op1.set(5434);
        alu.op2.set(-21321);
        alu.doOperation(new Bit[] { new Bit(false), new Bit(true), new Bit(true), new Bit(true) });
        assertEquals(-115858314, alu.result.getSigned());
        // subtract tests
        alu.op1.set(-24);
        alu.op2.set(46);
        alu.doOperation(new Bit[] { new Bit(true), new Bit(true), new Bit(true), new Bit(true) });
        assertEquals(-70, alu.result.getSigned());
        alu.op1.set(0);
        alu.op2.set(0);
        alu.doOperation(new Bit[] { new Bit(true), new Bit(true), new Bit(true), new Bit(true) });

        assertEquals(0, alu.result.getSigned());
        alu.op1.set(2);
        alu.op2.set(2);
        alu.doOperation(new Bit[] { new Bit(true), new Bit(true), new Bit(true), new Bit(true) });
        assertEquals(0, alu.result.getSigned());
        alu.op1.set(1);
        alu.op2.set(2);
        alu.doOperation(new Bit[] { new Bit(true), new Bit(true), new Bit(true), new Bit(true) });
        assertEquals(-1, alu.result.getSigned());
        alu.op1.set(500);
        alu.op2.set(2);
        alu.doOperation(new Bit[] { new Bit(true), new Bit(true), new Bit(true), new Bit(true) });
        assertEquals(498, alu.result.getSigned());
        alu.op1.set(500);
        alu.op2.set(323);
        alu.doOperation(new Bit[] { new Bit(true), new Bit(true), new Bit(true), new Bit(true) });
        assertEquals(177, alu.result.getSigned());
        alu.op1.set(500000);
        alu.op2.set(32321);
        alu.doOperation(new Bit[] { new Bit(true), new Bit(true), new Bit(true), new Bit(true) });
        assertEquals(467679, alu.result.getSigned());
        alu.op1.set(90000099);
        alu.op2.set(2312354);
        alu.doOperation(new Bit[] { new Bit(true), new Bit(true), new Bit(true), new Bit(true) });
        assertEquals(87687745, alu.result.getSigned());
    }
/* 
    @Test
    public void testMutliply() {
        ALU alu = new ALU();
        alu.op1.set(29);
        alu.op2.set(9);
        alu.mutliply();
        System.out.println(alu.op1 + " " + alu.op2);
        assertEquals(261, alu.result.getSigned());
        alu.op1.set(33);
        alu.op2.set(123);
        alu.mutliply();
        assertEquals(4059, alu.result.getSigned());
        alu.op1.set(0);
        alu.op2.set(123);
        alu.mutliply();
        assertEquals(0, alu.result.getSigned());
        alu.op1.set(0);
        alu.op2.set(-123);
        alu.mutliply();
        assertEquals(0, alu.result.getSigned());
        alu.op1.set(0);
        alu.op2.set(0);
        alu.mutliply();
        assertEquals(0, alu.result.getSigned());
        alu.op1.set(534);
        alu.op2.set(-213);
        alu.mutliply();
        assertEquals(-113742, alu.result.getSigned());
        alu.op1.set(5434);
        alu.op2.set(-21321);
        alu.mutliply();
        assertEquals(-115858314, alu.result.getSigned());
    }

    @Test
    public void testSubtract() {
        ALU alu = new ALU();
        alu.op1.set(-24);
        alu.op2.set(46);
        alu.subtract();
        assertEquals(-70, alu.result.getSigned());
        alu.op1.set(0);
        alu.op2.set(0);
        alu.subtract();

        assertEquals(0, alu.result.getSigned());
        alu.op1.set(2);
        alu.op2.set(2);
        alu.subtract();
        assertEquals(0, alu.result.getSigned());
        alu.op1.set(500);
        alu.op2.set(2);
        alu.subtract();
        assertEquals(498, alu.result.getSigned());
        alu.op1.set(500);
        alu.op2.set(323);
        alu.subtract();
        assertEquals(177, alu.result.getSigned());
        alu.op1.set(500000);
        alu.op2.set(32321);
        alu.subtract();
        assertEquals(467679, alu.result.getSigned());
        alu.op1.set(90000099);
        alu.op2.set(2312354);
        alu.subtract();
        assertEquals(87687745, alu.result.getSigned());
    }*/
}
