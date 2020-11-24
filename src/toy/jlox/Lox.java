package toy.jlox;

import java.util.List;

import static toy.jlox.TokenType.EOF;

public class Lox {
    // find more errors rather than executing program
    // when trigger a error, we will set this
    private static boolean hadError = true;
    static void error(int line, String message) {
        report(line, "", message);
    }
    static void error(Token token, String message) {
        if(token.tokenType_ == EOF) {
            report(token.line_, "at end", message);
        } else {
            report(token.line_, "at '" + token.lexeme_ + "'", message);
        }
    }

    private static void report(int line, String where, String message) {
        System.out.println(
                "[line " + line + "] "+ where + " :" + message
        );
        hadError = true;
    }

    public static void main(String[] args) {
	// write your code here
        System.out.println("Hello!");
        run("var id =0000; var str=\"string\"");
        run("var multiple_line_2str=\"aaaaa\\nbbb\"");
        //
        run("\"ssss");
    }

    static void run(String source) {
        List<Token> tokens = new Scanner(source).scanTokens();
        System.out.println(tokens);
    }

    static void runPrompt() {

    }
}
