import java.util.LinkedList;

public class ALU {

    public Word op1, op2, result;

    public ALU() {
        op1 = new Word();
        op2 = new Word();
        result = new Word();
    }

    /**
     * Takes an input array of four bits to determine the operation to complete.
     * 1000 – and
     * 1001 – or
     * 1010 – xor
     * 1011 – not (not “op1”; ignore op2)
     * 1100 – left shift (“op1” is the value to shift, “op2” is the amount to shift;
     * ignore all but the lowest 5 bits)
     * 1101 – right shift (“op1” is the value to shift, “op2” is the amount to
     * shift; ignore all but the lowest 5 bits)
     * 1110 – add
     * 1111 – subtract
     * 0111 - multiply
     * 
     * @param operation Set of bits that determine the operation.
     */
    public void doOperation(Bit[] operation) {
        // filters through bits to find operation
        if (operation[0].getValue()) {
            if (operation[1].getValue()) {// possible values and, or, xor, not
                if (operation[2].getValue()) {// xor, not
                    if (operation[3].getValue())

                        subtract();
                    else
                        add();
                } else { // and or
                    if (operation[3].getValue())
                        setResult(op1.or(op2));
                    else
                        setResult(op1.and(op2));
                }
            } else {// possible values left,right,add,subtract
                if (operation[2].getValue()) {// could be add or subtract
                    if (operation[3].getValue())
                        setResult(op1.not());
                    else
                        setResult(op1.xor(op2));
                } else { // could be left or right shift
                    if (operation[3].getValue())
                        setResult(op1.rightShift(op2.getSigned()));
                    else
                        setResult(op1.leftShift(op2.getSigned()));
                }
            }
        } else
            mutliply();

    }

    /*
     * Adds op1 and op2 together and sets the output to result.
     */
    private void add() {
        setResult(add2(op1, op2));
    }

    public Word add4(Word word1, Word word2, Word word3, Word word4) {
        Word[] inputs = new Word[] { word1, word2, word3, word4 };
        Word output = new Word();
        for (int i = 31; i >= 1; i--) {
            int trueCounter = 0;
            if (word1.getBit(i).and(word2.getBit(i).and(word3.getBit(i).and(word4.getBit(i)))).getValue())
                trueCounter = 4;
            else if (word1.getBit(i).xor(word2.getBit(i)).and(null).getValue())
                switch (trueCounter) {
                    case 1:
                        break;
                }
        }
        return output;
    }

    /*
     * Sets a bit at a position in a word to true, if it has to carry left it does
     * so.
     */
    private void carryLeft(int position, Word output) {
        if (output.getBit(position).getValue()) {
            while (output.getBit(position).getValue()) {

            }
            for (int i = position; i >= 1; i--) {
                if (!output.getBit(i).getValue()) {
                    output.getBit(i).set();
                    break;
                } else {
                    output.getBit(i).clear();
                }
            }
        } else {
            output.getBit(position).set();
        }
    }

    /**
     * @param word1 First word to be added.
     * @param word2 Second word to be added.
     * @return Word Result of adding the two words.
     */
    private Word add2(Word word1, Word word2) {
        Word result = new Word();
        // if both numbers are positive or negative
        if (!word1.getBit(0).xor(word2.getBit(0)).getValue()) {
            boolean bothPositive = !word1.getBit(0).getValue();
            if (!bothPositive)// if both are negative
            {
                word1 = getTwosCompliment(word1);
                word2 = getTwosCompliment(word2);
            } // if both are positive do not modify and directly input
            boolean carry = false;
            for (int i = 31; i >= 1; i--) {
                carry = add2Bits(word1.getBit(i), word2.getBit(i), result.getBit(i), carry);
            }
            /*
             * now if both are negative return the number
             * back to its twos complement form
             */
            if (!bothPositive) {
                result = getTwosCompliment(result);
                result.getBit(0).set();
            } else
                result.getBit(0).clear();
        } else {// if one number is positive and the other is negative
            /*
             * boolean word1Positive = word1.getBit(0).getValue();
             * boolean positiveLarger =
             * !isLeftLarger(word1Positive ? word1 : word2, word1Positive ? word2 : word1);
             */
            Word positive = word1.getBit(0).getValue() ? word2 : word1,
                    negative = word1.getBit(0).getValue() ? word1 : word2;
            boolean positiveLarger = isPositiveLarger(positive, negative);
            // is the positive number larger?
            if (!positiveLarger) {
                word1 = getTwosCompliment(word1);
                word2 = getTwosCompliment(word2);
            }
            boolean carry = false;
            for (int i = 31; i >= 1; i--) {
                carry = add2Bits(word1.getBit(i), word2.getBit(i), result.getBit(i), carry);
            }
            if (!positiveLarger) {
                result = getTwosCompliment(result);
            }
            result.setBit(0, new Bit(!positiveLarger));

        }
        return result;
    }

    /**
     * @param bit1   First bit addition input.
     * @param bit2   Second bit addition input.
     * @param result Bit addition output that could be effected by carry.
     * @param carry  If the previous operation resulted in a carry.
     * @return boolean Set to true if this operation resulted in a carry.
     */
    private boolean add2Bits(Bit bit1, Bit bit2, Bit result, boolean carry) {
        if (bit1.and(bit2).getValue()) {
            if (carry)
                result.set();
            else
                result.clear();
            return true;
        } else if (bit1.xor(bit2).getValue()) {
            if (carry) {
                result.clear();
                return true;
            } else
                result.set();
        } else {
            if (carry)
                result.set();
            else
                result.clear();
        }
        return false;
    }

    /**
     * @param positive The positive word input.
     * @param negative The negative word input.
     * @return boolean Is the positive input larger?
     */
    private boolean isPositiveLarger(Word positive, Word negative) {
        /*
         * loops down each number and finds if both corresponding
         * bits are zeros (negative is larger) or ones (positive is larger)
         */
        Word twosComplement = getTwosCompliment(negative);
        for (int i = 1; i < 32; i++) {
            if (positive.getBit(i).getValue() && !twosComplement.getBit(i).getValue())
                return true;
            if ((!positive.getBit(i).getValue() && twosComplement.getBit(i).getValue()))
                return false;
        }
        return true;
    }

    /**
     * Outputs the twos compliment of the input word.
     * 
     * @param word Word input.
     * @return Word Twos compliment of the word input.
     */
    private Word getTwosCompliment(Word word) {
        Word returnValue = new Word();
        int i;
        boolean allBitsTheSame = true;
        Bit previousBit = word.getBit(1);
        // inverts all bits
        for (i = 1; i < 32; i++) {
            Bit currentBit = word.getBit(i);
            returnValue.setBit(i, new Bit(!word.getBit(i).getValue()));
            // covers finding twos compliment of 0
            if (allBitsTheSame) {
                if (currentBit.getValue() == previousBit.getValue())
                    previousBit = currentBit;
                else {
                    allBitsTheSame = false;
                }
            }
        }
        if (!allBitsTheSame)
            returnValue.setBit(0, new Bit(!word.getBit(0).getValue()));
        else
            returnValue.setBit(0, new Bit(false));
        // adds one
        for (i = 31; i >= 1; i--) {
            if (!returnValue.getBit(i).getValue()) {
                returnValue.setBit(i, new Bit(true));
                break;
            } else
                returnValue.setBit(i, new Bit(false));
        }
        return returnValue;
    }

    /*
     * Subtracts the op2 from op1.
     */
    private void subtract() {
        // if the numbers being subtracted are equal, set result to 0
        if (op1.equals(op2))
            setResult(new Word());
        else {
            op2 = getTwosCompliment(op2);
            add();
        }
    }

    /*
     * Multiplies op1 with op2
     */
    private void mutliply() {
        Bit negative = op1.getBit(0).xor(op2.getBit(0));
        // if either are negative, flip to positve and switch result sign later
        if (op1.getBit(0).getValue())
            op1 = getTwosCompliment(op1);
        if (op2.getBit(0).getValue())
            op2 = getTwosCompliment(op2);

        LinkedList<Word> intermediateProducts = new LinkedList<>();
        int placeholderNumber = 0;
        /*
         * Take each bit in op2 and multiply it with all of op1,
         * then take results and add them together.
         */
        for (int i = 31; i >= 1; i--) {
            Word word = new Word();
            for (int j = 31; j >= 1; j--)
                word.setBit(j, op2.getBit(i).and(op1.getBit(j)));
            word = word.leftShift(placeholderNumber);
            intermediateProducts.add(word);
            placeholderNumber++;
        }
        Word output = new Word();
        for (Word iterator : intermediateProducts) {
            output = add2(output, iterator);
        }
        if (negative.getValue())
            output = getTwosCompliment(output);
        setResult(output);
    }

    /**
     * Takes an input word and sets the result's bits to be the same as
     * 
     * @param input The word to set the result equal to.
     */
    private void setResult(Word input) {
        for (int i = 0; i < 32; i++) {
            if (input.getBit(i).getValue())
                result.getBit(i).set();
            else
                result.getBit(i).clear();
        }
    }

    public void setOp1(Word word) {
        op1.copy(word);
    }

    public void setOp2(Word word) {
        op2.copy(word);
    }

    public Word getResult() {
        Word word = new Word();
        word.copy(result);
        return word;
    }
}
