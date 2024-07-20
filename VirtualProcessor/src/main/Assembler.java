package main;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Assembler {
    public static void main(String args[]) throws Exception
    {
        var lines = Files.readAllLines(Paths.get("/Users/joshr/Dropbox/VirtualProcessor/VirtualProcessor/src/test/assembly"),
                StandardCharsets.UTF_8);

        var lexer = new Lexer();
        for (int i = 0; i < lines.size(); i++) {
            lexer.lex(lines.get(i), i + 1, lines.size());
        }
        lexer.getTokens()
        .forEach(token -> token.print());
    
        var parser = new Parser(lexer.getTokens());
        parser.parse();
        parser.getProgram()
        .forEach(word -> System.out.println(word));
    }
}
