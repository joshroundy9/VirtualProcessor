package main;
import java.util.LinkedList;

public class Word {

    private Bit word[];

    public Word() {
        word = new Bit[32];
        for (int i = 0; i < 32; i++)
            word[i] = new Bit(false);
    }
    public Word(Integer value)
    {
        word = new Bit[32];
        for (int i = 0; i < 32; i++)
            word[i] = new Bit(false);
        set(value);
    }

    /*
     * Returns the value of the bit in position i.
     */
    public Bit getBit(int i) {
        return word[i];
    }

    /*
     * Sets a bit in position i to the given value.
     */
    public void setBit(int i, Bit value) {
        word[i] = value;
    }

    /*
     * Runs the AND operation on each position in the two words.
     * Returns a new word with the result.
     */
    public Word and(Word other) {
        Word newWord = new Word();
        for (int i = 0; i < 32; i++)
            newWord.setBit(i, word[i].and(other.getBit(i)));
        return newWord;
    }

    /*
     * Runs the OR operation on each position in the two words.
     * Returns a new word with the result.
     */
    public Word or(Word other) {
        Word newWord = new Word();
        for (int i = 0; i < 32; i++)
            newWord.setBit(i, word[i].or(other.getBit(i)));
        return newWord;
    }

    /*
     * Runs the XOR operation on each position in the two
     * words. Returns a new word with the result.
     */
    public Word xor(Word other) {
        Word newWord = new Word();
        for (int i = 0; i < 32; i++)
            newWord.setBit(i, word[i].xor(other.getBit(i)));
        return newWord;
    }

    /*
     * Switches all bits in this word.
     */
    public Word not() {
        Word newWord = new Word();
        for (int i = 0; i < 32; i++)
            newWord.setBit(i, word[i].not());
        return newWord;
    }

    /*
     * Shifts all bits right in the word.
     */
    public Word rightShift(int amount) {
        Word newWord = new Word();
        for (int i = amount; i < 32; i++)
            newWord.setBit(i, word[i - amount]);
        for (int i = 0; i < amount; i++)
            newWord.setBit(i, new Bit(false));
        return newWord;
    }

    /*
     * Shifts all bits in the word left.
     */
    public Word leftShift(int amount) {
        Word newWord = new Word();
        for (int i = 0; i < 31 - amount + 1; i++)
            newWord.setBit(i, word[i + amount]);
        for (int i = 31; i > 31 - amount; i--)
            newWord.setBit(i, new Bit(false));
        return newWord;
    }

    /*
     * Converts word to unsigned long
     * this is because longs are 64 bits in length
     * but the word is only 32 bits long so the result
     * cannot be negative.
     */
    public long getUnsigned() {
        long baseTen = 0;
        int multiplier = 1;
        for (int i = 31; i >= 0; i--) {
            if (word[i].getValue())
                baseTen += multiplier;
            multiplier *= 2;
        }
        return baseTen;
    }

    /*
     * converts bits to int
     */
    public int getSigned() {
        int baseTen = 0;
        int multiplier = 1;
        boolean isNegative = word[0].getValue();
        for (int i = 31; i > 0; i--) {
            if (isNegative ? !word[i].getValue() : word[i].getValue())
                baseTen += multiplier;
            multiplier *= 2;
        }
        if (isNegative) {
            baseTen *= -1;
            baseTen--;
        }
        return baseTen;
    }

    /*
     * Copies the values from another word into this word.
     */
    public void copy(Word other) {
        for (int i = 0; i < 32; i++) { 
            if(other.getBit(i).getValue())
            word[i].set();
            else word[i].clear();
        }
    }

    /*
     * Sets this word equal to an integer value
     */
    public void set(int value) {
        var isNegative = false;
        if (value < 0) {
            isNegative = true;
            value *= -1;
            value--;
        }
        var quotient = value;
        var list = new LinkedList<Boolean>();
        for (int i = 1; i < 32; i++) {
            list.add(quotient % 2 == 1);
            quotient /= 2;
        }

        // sets the word to equal the boolean values in the
        for (int i = 31; i >= 1; i--)
            word[i] = new Bit(isNegative ? !list.removeFirst() : list.removeFirst());
        word[0] = new Bit(isNegative);

    }

    public String toString() {
        var s = "";
        for (Bit b : word)
            s += b.getValue() ? "t" : "f";
        return s;
    }
    public boolean equals(Word other)
    {
        for(int i =0;i<32;i++)
            if(word[i].getValue() != other.getBit(i).getValue())
                return false;
        
        return true;
    }
    public void increment()
    {
        /* DOES NOT WORK FOR 0 */
        int i;
        for(i = 31;i>=0;i--)
        {
            if(word[i].xor(new Bit(true)).getValue())
            {
                word[i].set();
                return;
            } else if(word[i].and(new Bit(true)).getValue())
            {    
                word[i].clear();
            }
            if(i ==0)
            set(0);
        }
    }
    public void decrement()
    {
        /* DOES NOT WORK FOR 0 */
        int i;
        for(i = 31;i>=0;i--)
        {
            if(word[i].getValue())
            {
                word[i].clear();
                return;
            } else{    
                word[i].set();
            }
        }
        if(i==0)
        set(1);
    }
}
