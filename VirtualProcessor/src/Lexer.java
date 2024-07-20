import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

public class Lexer {

    private LinkedList<Token> tokenList = new LinkedList<Token>();
    private HashMap<String, Token.tokenType> knownWords = new HashMap<>();

    public Lexer() {
        knownWords.put("math", Token.tokenType.MATH);
        knownWords.put("add", Token.tokenType.ADD);
        knownWords.put("subtract", Token.tokenType.SUBTRACT);
        knownWords.put("multiply", Token.tokenType.MULTIPLY);
        knownWords.put("and", Token.tokenType.AND);
        knownWords.put("or", Token.tokenType.OR);
        knownWords.put("not", Token.tokenType.NOT);
        knownWords.put("xor", Token.tokenType.XOR);
        knownWords.put("copy", Token.tokenType.COPY);
        knownWords.put("halt", Token.tokenType.HALT);
        knownWords.put("branch", Token.tokenType.BRANCH);
        knownWords.put("jump", Token.tokenType.JUMP);
        knownWords.put("call", Token.tokenType.CALL);
        knownWords.put("push", Token.tokenType.PUSH);
        knownWords.put("load", Token.tokenType.LOAD);
        knownWords.put("return", Token.tokenType.RETURN);
        knownWords.put("store", Token.tokenType.STORE);
        knownWords.put("peek", Token.tokenType.PEEK);
        knownWords.put("pop", Token.tokenType.POP);
        knownWords.put("interrupt", Token.tokenType.INTERRUPT);
        knownWords.put("equal", Token.tokenType.EQUAL);
        knownWords.put("unequal", Token.tokenType.UNEQUAL);
        knownWords.put("greater", Token.tokenType.GREATER);
        knownWords.put("less", Token.tokenType.LESS);
        knownWords.put("greaterorequal", Token.tokenType.GREATEROREQUAL);
        knownWords.put("lessorequal", Token.tokenType.LESSOREQUAL);
        knownWords.put("shift", Token.tokenType.SHIFT);
        knownWords.put("left", Token.tokenType.LEFT);
        knownWords.put("right", Token.tokenType.RIGHT);
        knownWords.put("number", Token.tokenType.NUMBER);
        knownWords.put("register", Token.tokenType.REGISTER);
        knownWords.put("newline", Token.tokenType.NEWLINE);
        knownWords.put("endoffile", Token.tokenType.ENDOFFILE);
        knownWords.put("twor", Token.tokenType.TWOR);
        knownWords.put("threer", Token.tokenType.THREER);
        knownWords.put("destonly", Token.tokenType.DEST_ONLY);
        knownWords.put("nor", Token.tokenType.NOR);
    }

    /**
     * Takes a string and converts into tokens consisting of its words and numbers,
     * putting them into the tokenList.
     * 
     * @param line The string used as input.
     */
    public void lex(String lineString,int currentLine, int totalLines) throws Exception {
        
        char[] charArray = lineString.toCharArray();
        String currentWord = "";
        for(int i = 0;i<charArray.length;i++)
        {
            char character = charArray[i];
            //in the case of register, always check for numbers after it.
            if(character == 'R' || character == 'r' && currentWord.equals(""))
            {
                String currentNumber = "";
                i++;
                character = charArray[i];
                //if no number is found after the R, default to a value of 0
                if(!Character.isDigit(character))
                tokenList.add(new Token(0, Token.tokenType.REGISTER));
                else {
                    currentNumber = currentNumber+character;
                    while(++i<charArray.length && Character.isDigit(charArray[i]))
                    {
                        character = charArray[i];
                        currentNumber = currentNumber+character;
                    }
                    Token token = new Token(Integer.parseInt(currentNumber), Token.tokenType.REGISTER);
                    //adds the register token to the list with its assigned number
                    tokenList.add(token);
                }

            } else if(character == ' ')
            {
                createWordIfPossible(currentWord);
                currentWord = "";
            } else {
                currentWord+=character;
            }
            if(i == charArray.length-1)
            {
            createWordIfPossible(currentWord);
            currentWord = "";
            }
        }
        tokenList.add(new Token(null, Token.tokenType.NEWLINE));
        if(currentLine == totalLines)
        tokenList.add(new Token(null, Token.tokenType.ENDOFFILE));
    }

    /**
     * Takes a string and maps it to a known word if possible,
     * if it fails to find a known word it creates an identifier with the string.
     * 
     * @param accString The input string.
     * @throws Exception 
     */
    public void createWordIfPossible(String accString) throws Exception {
        if (!accString.equals("")) {
            Token last;
            boolean isNumber = Character.isDigit(accString.toCharArray()[0]);
            if(isNumber)
                last = new Token(Integer.parseInt(accString), Token.tokenType.NUMBER);
             else {
                if (knownWords.containsKey(accString.toLowerCase())) {
                    last = new Token(null, knownWords.get(accString));

                } else //this lexer does not accept strings, so 
                    throw new Exception("Invalid token input \""+accString+"\".");
                
            }
            tokenList.add(last);
        }
    }
    public void createNumberIfPossible(String accString)
    {
        if (!accString.equals("")) 
        {
            tokenList.add(new Token(Integer.parseInt(accString), Token.tokenType.NUMBER));
        }
    }

    /**
     * Getter for the collection of tokens.
     * 
     * @return List<Token> The list of tokens.
     */
    public LinkedList<Token> getTokens() {
        return tokenList;
    }
}
