import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;

import org.junit.Test;

public class AssemblerTests {

    @Test
    public void LexerTest() throws Exception {
        /* YOU WILL HAVE TO CHANGE FILE PATH!!!!! */
        List<String> lines = Files.readAllLines(Paths.get("C:/Users/milli/Dropbox/ICSI 404/404/src/assembly"),
                StandardCharsets.UTF_8);

        Lexer lex = new Lexer();
        for (int i = 0; i < lines.size(); i++) {
            lex.lex(lines.get(i), i + 1, lines.size());
        }
        // prints tokens
        for (Token token : lex.getTokens()) {
            token.print();
        }
        List<Token> tokenList = lex.getTokens();
        assertEquals(Token.tokenType.MATH, tokenList.get(0).getType());
        assertEquals(Token.tokenType.ADD, tokenList.get(1).getType());
        assertEquals(Token.tokenType.REGISTER, tokenList.get(2).getType());
        assertEquals(1, tokenList.get(2).getValue());
        assertEquals(Token.tokenType.REGISTER, tokenList.get(3).getType());
        assertEquals(2, tokenList.get(3).getValue());
        assertEquals(Token.tokenType.REGISTER, tokenList.get(4).getType());
        assertEquals(3, tokenList.get(4).getValue());
        assertEquals(Token.tokenType.NEWLINE, tokenList.get(5).getType());
        assertEquals(Token.tokenType.LOAD, tokenList.get(6).getType());
        assertEquals(Token.tokenType.NUMBER, tokenList.get(7).getType());
        assertEquals(100, tokenList.get(7).getValue());
        assertEquals(Token.tokenType.REGISTER, tokenList.get(8).getType());
        assertEquals(1, tokenList.get(8).getValue());
        assertEquals(Token.tokenType.NEWLINE, tokenList.get(9).getType());
        assertEquals(Token.tokenType.ENDOFFILE, tokenList.get(10).getType());
    }

    @Test
    public void ParserTest() throws Exception {
        /* YOU WILL HAVE TO CHANGE FILE PATH!!!!! */
        List<String> lines = Files.readAllLines(Paths.get("/Users/joshr/Dropbox/ICSI 404/404/src/assembly"),
                StandardCharsets.UTF_8);

        Lexer lex = new Lexer();
        for (int i = 0; i < lines.size(); i++) {
            lex.lex(lines.get(i), i + 1, lines.size());
        }
        // prints tokens
        for (Token token : lex.getTokens()) {
            token.print();
        }
        LinkedList<Token> tokenList = lex.getTokens();
        Parser parser = new Parser(tokenList);
        parser.parse();
        for (Word word : parser.getProgram()) {
            System.out.println(word);
        }
        /*MATH ADD R1 R2 R3
         * = 00000000 00001 00010 1110 00011 00010
         *   LOAD R1 100
         * = 000000000001100100 0000 00001 10001
         * 
         */
        assertEquals(571490, parser.getProgram().get(0).getSigned());
        assertEquals(1638449, parser.getProgram().get(1).getSigned());
    }

}
