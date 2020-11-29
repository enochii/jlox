package toy.jlox;

import toy.tool.FileUtils;

import java.util.List;

import static toy.jlox.TokenType.EOF;

public class Lox {
    // find more errors rather than executing program
    // when trigger a error, we will set this
    private static boolean hadError = false;
    private static boolean hadRuntimeError = false;
    private static Interpreter interpreter = new Interpreter();

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

    static void runtimeError(Token token, String msg) {
        System.out.println(
                "Runtime Error: " + msg + " at line " + token.line_
        );
        hadRuntimeError = true;
    }

    private static void report(int line, String where, String message) {
        System.out.println(
                "[line " + line + "] "+ where + " :" + message
        );
        hadError = true;
    }

    public static void main(String[] args) {
	// write your code here
        String source = FileUtils.readFile("example/test.txt");

        run(source);
    }

    static void run(String source) {
        // scan
        List<Token> tokens = new Scanner(source).scanTokens();
        // parse
        List<Stmt> stmts   = new Parser(tokens).program();
        // execute
        interpreter.interpret(stmts);
    }

    static void runPrompt() {

    }
}
