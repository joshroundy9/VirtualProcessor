package main;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import main.Token.tokenType;

public class Parser {

    private LinkedList<Word> program;
    private LinkedList<Token> tokenList;
    private HashSet<Token.tokenType> statementTokens;
    private HashSet<Token.tokenType> MOPTokens;
    private HashSet<Token.tokenType> BOPTokens;

    public Parser(LinkedList<Token> tokenList) {
        program = new LinkedList<>();
        this.tokenList = tokenList;
        statementTokens = new HashSet<>();
        MOPTokens = (HashSet<tokenType>) Set.of(
        Token.tokenType.ADD,
        Token.tokenType.MULTIPLY,
        Token.tokenType.SUBTRACT);
        BOPTokens = (HashSet<tokenType>) Set.of(Token.tokenType.EQUAL,
        Token.tokenType.NOTEQUAL,
        Token.tokenType.LESS,
        Token.tokenType.GREATER,
        Token.tokenType.GREATEROREQUAL,
        Token.tokenType.LESSOREQUAL);
        statementTokens.add(Token.tokenType.MATH);
        statementTokens.add(Token.tokenType.BRANCH);
        statementTokens.add(Token.tokenType.HALT);
        statementTokens.add(Token.tokenType.COPY);
        statementTokens.add(Token.tokenType.JUMP);
        statementTokens.add(Token.tokenType.CALL);
        statementTokens.add(Token.tokenType.PUSH);
        statementTokens.add(Token.tokenType.POP);
        statementTokens.add(Token.tokenType.LOAD);
        statementTokens.add(Token.tokenType.STORE);
        statementTokens.add(Token.tokenType.RETURN);
        statementTokens.add(Token.tokenType.PEEK);
        statementTokens.add(Token.tokenType.INTERRUPT);
    }

    /*
     * Parses the entire program by looping parseStatement() and checking for the
     * endoffile
     */
    public void parse() throws Exception {
        if (tokenList.peek().getType() == Token.tokenType.ENDOFFILE)
            return;
        do {
            matchAndRemove(Token.tokenType.NEWLINE);
            program.add(parseStatement());
            System.out.println(program.getLast());
            if (matchAndRemove(Token.tokenType.NEWLINE) == null)
                throw new Exception("Statements must be followed by a new line. "+tokenList);

        } while (matchAndRemove(Token.tokenType.ENDOFFILE) == null);
    }

    public LinkedList<Word> getProgram() {
        return program;
    }

    /*
     * Parses a statement from the list.
     */
    private Word parseStatement() throws Exception {
        if (!statementTokens.contains(tokenList.peek().getType()))
            throw new Exception("Each line must begin with a statement type.");
        var statement = tokenList.remove();
        var word = new Word();
        switch (statement.getType()) {
            case MATH:
                /*
                 * FORMAT:
                 * 2R: MATH FUNCTION RS RD
                 * 3R: MATH FUNCTION RS1 RS2 RD
                 */
                Token function = tokenList.remove();
                if (!MOPTokens.contains(function.getType()))
                    throw new Exception(
                            "Math instructions must be followed by math keywords. " + tokenList.peek().getType());
                // find if the math statement is 2r or 3r
                if (tokenList.size() < 3)
                    throw new Exception("Math operation must be followed by two registers.");
                Token t1 = tokenList.remove(),
                        t2 = tokenList.remove(), t3 = tokenList.peek();
                if (t3.getType() == Token.tokenType.REGISTER)
                    tokenList.remove();
                Word addRegister = new Word(t1.getValue());
                word = word.or(addRegister);
                if (t3.getType() == Token.tokenType.REGISTER) {
                    word = word.leftShift(5);
                    addRegister.set(t2.getValue());
                    word = word.or(addRegister);
                }
                word = word.leftShift(4);
                addRegister.set(getMOPValue(function.getType()));
                word = word.or(addRegister);
                word = word.leftShift(5);
                if (t3.getType() == Token.tokenType.REGISTER)
                addRegister.set(t3.getValue());
                else 
                addRegister.set(t2.getValue());
                word = word.or(addRegister);
                word = word.leftShift(5);
                // 000 10 for math 3r 000 11 for math 2r
                if (t3.getType() == Token.tokenType.REGISTER)
                    addRegister.set(2);
                else
                    addRegister.set(3);
                word = word.or(addRegister);
                return word;
            case BRANCH:
                word = new Word();
                function = tokenList.remove();
                if (!BOPTokens.contains(tokenList.peek().getType()))
                    throw new Exception("Branch instructions must be followed by math keywords.");
                /* since 2r and 3r function identically, use same algorithm for both. */
                if (tokenList.size() < 3)
                    throw new Exception("Branch operation must be followed by two registers.");
                t1 = tokenList.peek();
                t2 = tokenList.get(1);
                addRegister = new Word(t1.getValue());
                word = word.or(addRegister);
                word = word.leftShift(4);
                // sets the function value with to the bop
                addRegister.set(getBOPValue(function.getType()));
                word = word.or(addRegister);
                word = word.leftShift(5);
                addRegister.set(t2.getValue());
                word = word.or(addRegister);
                word = word.leftShift(5);
                // 001 11 for branch 2r
                addRegister.set(7);
                word = word.or(addRegister);
                return word;
            case HALT:
                return new Word();
            case COPY:
             
                if (tokenList.peek().getType() != Token.tokenType.NUMBER
                        || tokenList.get(1).getType() != Token.tokenType.REGISTER)
                    throw new Exception("Statement copy must be followed by a number and a register.");
                Token number = matchAndRemove(Token.tokenType.NUMBER),
                        register = matchAndRemove(Token.tokenType.REGISTER);
                word = new Word(number.getValue());
                System.out.println("COPY: NUMBER: "+number+" REGISTER: "+register);
                word = word.leftShift(9);
                word = word.or(new Word(register.getValue()));
                word = word.leftShift(5);
                // sets opcode to 000 01
                word.setBit(31, new Bit(true));
                return word;
            case JUMP:
                // for this one, I decided to explicitly ask the user if its for the nor or dest
                // only version
                var type = tokenList.removeFirst();
                number = matchAndRemove(Token.tokenType.NUMBER);
                switch (type.getType()) {
                    case DEST_ONLY:
                        word = new Word(number.getValue());
                        word = word.leftShift(14);
                        // 010 01 for jump dest only
                        addRegister = new Word(9);
                        word = word.or(addRegister);
                        return word;

                    case NOR:
                        word = new Word(number.getValue());
                        word = word.leftShift(5);
                        // 010 00 for jump nor
                        addRegister = new Word(8);
                        word = word.or(addRegister);
                        return word;

                    default:
                        throw new Exception("Jump must specify instruction type with NoR or DestOnly.");
                }
            case CALL:
                /*
                 * format: 2R: call BOP REGISTER REGISTER NUMBER
                 * 3R: call BOP REGISTER REGISTER REGISTER NUMBER
                 * DEST ONLY: call REGISTER NUMBER
                 * NO R: call NUMBER
                 */
                word = new Word();
                function = tokenList.remove();
                if (function.getType() == Token.tokenType.NUMBER) // NO R case
                {
                    word = word.or(new Word(function.getValue()));
                    word = word.leftShift(5);
                    // opcode 010 00
                    word = word.or(new Word(8));
                    return word;
                } else if (function.getType() == Token.tokenType.REGISTER) {
                    number = matchAndRemove(Token.tokenType.NUMBER);
                    if (number == null)
                        throw new Exception("Call Dest Only must contain a register followed by a number.");
                    word = word.or(new Word(number.getValue()));
                    word = word.leftShift(9);
                    word = word.or(new Word(function.getValue()));
                    word = word.leftShift(5);
                    // opcode 010 01
                    word = word.or(new Word(9));
                    return word;
                }
                if (!BOPTokens.contains(tokenList.peek().getType()))
                    throw new Exception("Call instructions must be followed by BOP keywords.");
                // find if the math statement is 2r or 3r
                if (tokenList.size() < 5)
                    throw new Exception("Call operation must be followed by two or three registers and a number.");
                    var tempTokenStorage = new Token[] {
                        tokenList.removeFirst(), matchAndRemove(Token.tokenType.REGISTER),
                        matchAndRemove(Token.tokenType.REGISTER), tokenList.removeFirst(),
                        tokenList.removeFirst() };
                // find if it is a case of 3r or 2r
                if (tempTokenStorage[3].getType() == Token.tokenType.REGISTER) {// 3r
                    if (tempTokenStorage[4].getType() != Token.tokenType.NUMBER)
                        throw new Exception("The final entry in a call statement must be a number.");
                    word = word.or(new Word(tempTokenStorage[4].getValue()));
                    word = word.leftShift(5);
                    word = word.or(new Word(tempTokenStorage[1].getValue()));
                    word = word.leftShift(5);
                    word = word.or(new Word(tempTokenStorage[2].getValue()));
                    word = word.leftShift(4);
                    word = word.or(new Word(getBOPValue(tempTokenStorage[0].getType())));
                    word = word.leftShift(5);
                    word = word.or(new Word(tempTokenStorage[3].getValue()));
                    word = word.leftShift(5);
                    // opcode 010 10
                    word = word.or(new Word(10));
                    return word;
                } else { // 2r
                    if (tempTokenStorage[3].getType() != Token.tokenType.NUMBER)
                        throw new Exception("The final entry in a call statement must be a number.");
                    word = word.or(new Word(tempTokenStorage[3].getValue()));
                    word = word.leftShift(5);
                    word = word.or(new Word(tempTokenStorage[1].getValue()));
                    word = word.leftShift(4);
                    word = word.or(new Word(getBOPValue(tempTokenStorage[0].getType())));
                    word = word.leftShift(5);
                    word = word.or(new Word(tempTokenStorage[2].getValue()));
                    word = word.leftShift(5);
                    // opcode 010 11
                    word = word.or(new Word(11));
                    return word;
                }
            case PUSH:
                /*
                 * FORMAT:
                 * R3, R2: push MOP REGISTER REGISTER
                 * Dest_Only: push MOP REGISTER NUMBER
                 */
                word = new Word();
                if (tokenList.size() < 3
                        || !MOPTokens.contains(tokenList.peek().getType())
                        || tokenList.get(1).getType() != Token.tokenType.REGISTER)
                    throw new Exception("Invalid input following PUSH statement.");
                // if the last token is a number, the push is dest only
                tempTokenStorage = new Token[] { tokenList.removeFirst(), tokenList.removeFirst(),
                        tokenList.removeFirst() };
                if (tokenList.get(2).getType() == Token.tokenType.NUMBER) {
                    word = word.or(new Word(tempTokenStorage[2].getValue()));
                    word = word.leftShift(4);
                    word = word.or(new Word(getMOPValue(tempTokenStorage[0].getType())));
                    word = word.leftShift(5);
                    word = word.or(new Word(tempTokenStorage[1].getValue()));
                    word = word.leftShift(5);
                    // opcode 011 01
                    word = word.or(new Word(13));
                    return word;
                }
                word = word.or(new Word(tempTokenStorage[1].getValue()));
                word = word.leftShift(4);
                word = word.or(new Word(getMOPValue(tempTokenStorage[0].getType())));
                word = word.leftShift(5);
                word = word.or(new Word(tempTokenStorage[2].getValue()));
                word = word.leftShift(5);
                // opcode 011 11
                word = word.or(new Word(15));
                return word;

            case POP:
                /* Format: pop REGISTER */
                var token = matchAndRemove(Token.tokenType.REGISTER);
                if (token == null)
                    throw new Exception("Statement pop must be followed by a register.");
                word = new Word(token.getValue());
                word = word.leftShift(5);
                // opcode 11001
                word = word.or(new Word(25));
                return word;
            case LOAD:
                /*
                 * FORMAT:
                 * 2R: load REGISTER REGISTER NUMBER
                 * 3R: load REGISTER REGISTER REGISTER
                 * Dest Only: load REGISTER NUMBER
                 */
                if (tokenList.size() < 3 || tokenList.get(0).getType() != Token.tokenType.REGISTER)
                    throw new Exception("Statement load must be entered with the proper format.");
                tempTokenStorage = new Token[] { tokenList.remove(), tokenList.remove(), tokenList.peek() };
                if (tempTokenStorage[2].getType() != Token.tokenType.NEWLINE)
                    tokenList.remove();

                System.out.println("Temp token storage: " + Arrays.asList(tempTokenStorage));

                if (tempTokenStorage[1].getType() == Token.tokenType.NUMBER) { // Dest only case
                    word = new Word(tempTokenStorage[1].getValue());
                    word = word.leftShift(9);
                    word = word.or(new Word(tempTokenStorage[0].getValue()));
                    word = word.leftShift(5);
                    // opcode 100 01
                    word = word.or(new Word(17));
                    return word;
                }
                if (tempTokenStorage[2].getType() == Token.tokenType.NUMBER) {// 2r case
                    word = new Word(tempTokenStorage[2].getValue());
                    word = word.leftShift(5);
                    word = word.or(new Word(tempTokenStorage[0].getValue()));
                    word = word.leftShift(9);
                    word = word.or(new Word(tempTokenStorage[1].getValue()));
                    word = word.leftShift(5);
                    // opcode 10011
                    word = word.or(new Word(19));
                    return word;
                } else { // 3R case
                    word = new Word(tempTokenStorage[0].getValue());
                    word = word.leftShift(5);
                    word = word.or(new Word(tempTokenStorage[1].getValue()));
                    word = word.leftShift(9);
                    word = word.or(new Word(tempTokenStorage[2].getValue()));
                    word = word.leftShift(5);
                    // opcode 10010
                    word = word.or(new Word(19));
                    return word;
                }
            case STORE:
                /*
                 * FORMAT:
                 * 2R: store REGISTER REGISTER NUMBER
                 * 3R: store REGISTER REGISTER REGISTER
                 * Dest Only: store REGISTER NUMBER
                 */
                
                if (tokenList.size() < 3 || tokenList.get(0).getType() != Token.tokenType.REGISTER)
                    throw new Exception("Statement store must be entered with the proper format.");
                tempTokenStorage = new Token[] { tokenList.remove(), tokenList.remove(), tokenList.peek()};
                if(tempTokenStorage[2].getType() != Token.tokenType.NEWLINE)
                tokenList.remove();
                System.out.println("STORE: "+tempTokenStorage[0] + " "+tempTokenStorage[1] + " "+tempTokenStorage[2]);
                if (tempTokenStorage[1].getType() == Token.tokenType.NUMBER) { // Dest only case
                    word = new Word(tempTokenStorage[1].getValue());
                    word = word.leftShift(9);
                    word = word.or(new Word(tempTokenStorage[0].getValue()));
                    word = word.leftShift(5);
                    // opcode 101 01
                    word = word.or(new Word(21));
                    return word;
                }
                if (tempTokenStorage[2].getType() == Token.tokenType.NUMBER) {// 2r case
                    word = new Word(tempTokenStorage[2].getValue());
                    word = word.leftShift(5);
                    word = word.or(new Word(tempTokenStorage[0].getValue()));
                    word = word.leftShift(9);
                    word = word.or(new Word(tempTokenStorage[1].getValue()));
                    word = word.leftShift(5);
                    // opcode 10111
                    word = word.or(new Word(23));
                    return word;
                } else { // 3R case
                    word = new Word(tempTokenStorage[0].getValue());
                    word = word.leftShift(5);
                    word = word.or(new Word(tempTokenStorage[1].getValue()));
                    word = word.leftShift(9);
                    word = word.or(new Word(tempTokenStorage[2].getValue()));
                    word = word.leftShift(5);
                    // opcode 10110
                    word = word.or(new Word(22));
                    return word;
                }
            case RETURN:
                // opcode: 100 00
                return new Word(16);
            case PEEK:
                /*
                 * FORMAT:
                 * 2R: peek REGISTER REGISTER NUMBER
                 * 3R: peek REGISTER REGISTER REGISTER
                 */
                if (tokenList.size() < 3 || tokenList.get(0).getType() != Token.tokenType.REGISTER)
                    throw new Exception("Statement peek must be entered with the proper format.");
                tempTokenStorage = new Token[] { tokenList.remove(), tokenList.remove(), tokenList.remove() };
                if (tempTokenStorage[2].getType() == Token.tokenType.NUMBER) {// 2r case
                    word = new Word(tempTokenStorage[2].getValue());
                    word = word.leftShift(5);
                    word = word.or(new Word(tempTokenStorage[0].getValue()));
                    word = word.leftShift(9);
                    word = word.or(new Word(tempTokenStorage[1].getValue()));
                    word = word.leftShift(5);
                    // opcode 11011
                    word = word.or(new Word(27));
                    return word;
                } else { // 3R case
                    word = new Word(tempTokenStorage[0].getValue());
                    word = word.leftShift(5);
                    word = word.or(new Word(tempTokenStorage[1].getValue()));
                    word = word.leftShift(9);
                    word = word.or(new Word(tempTokenStorage[2].getValue()));
                    word = word.leftShift(5);
                    // opcode 11010
                    word = word.or(new Word(26));
                    return word;
                }
            case INTERRUPT:
                token = tokenList.remove();
                if (token.getType() != Token.tokenType.NUMBER)
                    throw new Exception("Interrupt must be followed by a number.");
                word = new Word(tokenList.remove().getValue());
                word = word.leftShift(5);
                // opcode 110 00
                word = word.or(new Word(24));
                return word;

            default:
                return null;
        }

    }

    private Integer getBOPValue(Token.tokenType type) {
        switch (type) {
            case EQUAL:
                return 0;
            case NOTEQUAL:
                return 1;
            case LESS:
                return 2;
            case GREATEROREQUAL:
                return 3;
            case GREATER:
                return 4;
            case LESSOREQUAL:
                return 5;
            default:
                return null;
        }
    }

    private Integer getMOPValue(Token.tokenType type) {
        switch (type) {
            case ADD:
                return 14;
            case SUBTRACT:
                return 15;
            case MULTIPLY:
                return 7;
            default:
                return null;
        }
    }

    private Token matchAndRemove(Token.tokenType type) {
        if (tokenList.peek().getType() == type)
            return tokenList.removeFirst();
        else
            return null;
    }

}
