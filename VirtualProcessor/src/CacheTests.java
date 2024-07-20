import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;

import org.junit.Test;

public class CacheTests {
    /*MUST RUN FULL CLASS TEST OR MAINMEMORY DOES NOT RESET FROM PREVIOUS RUNS */
    @Test
    public void main() throws Exception
    {
        MainMemory.refreshMemory();
        System.out.println("START TEST");
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
        LinkedList<Token> tokenList = lex.getTokens();
        Parser parser = new Parser(tokenList);
        parser.parse();
        Processor processor = new Processor();
        LinkedList<Word> program = parser.getProgram();
        for(int i = 0;i<program.size();i++)
        {
            System.out.println(program.get(i));
            MainMemory.write(new Word(i), program.get(i));
        }
        processor.run();
        System.out.println("CLOCK CYCLES REQUIRED: "+processor.getCurrentClockCycle());
        //checks if the additions were successful
        assertEquals(200, MainMemory.read(new Word(205)).getSigned());
    }
    @Test
    public void main1() throws Exception
    {
        MainMemory.refreshMemory();
        System.out.println("START TEST");
        List<String> lines = Files.readAllLines(Paths.get("C:/Users/milli/Dropbox/ICSI 404/404/src/assembly2"),
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
        Processor processor = new Processor();
        LinkedList<Word> program = parser.getProgram();
        for(int i = 0;i<program.size();i++)
        {
            MainMemory.write(new Word(i), program.get(i));
        }
        processor.run();
        System.out.println("CLOCK CYCLES REQUIRED: "+processor.getCurrentClockCycle());
    }
    @Test
    public void main2() throws Exception
    {
        MainMemory.refreshMemory();
        System.out.println("START TEST");
        List<String> lines = Files.readAllLines(Paths.get("C:/Users/milli/Dropbox/ICSI 404/404/src/assembly3"),
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
        Processor processor = new Processor();
        LinkedList<Word> program = parser.getProgram();
        for(int i = 0;i<program.size();i++)
        {
            MainMemory.write(new Word(i), program.get(i));
        }
        processor.run();
        System.out.println("CLOCK CYCLES REQUIRED: "+processor.getCurrentClockCycle());
        assertEquals(200, MainMemory.read(new Word(205)).getSigned());
    }
    
}
