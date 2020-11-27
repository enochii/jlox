package toy.jlox;

import java.util.List;

import static toy.jlox.TokenType.EOF;

public class Lox {
    // find more errors rather than executing program
    // when trigger a error, we will set this
    private static boolean hadError = false;
    private static boolean hadRuntimeError = false;
    private static ASTEvaluator interpreter = new ASTEvaluator();

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
        System.out.println("Hello!");
        run("1* 2+3*-3");
        run("1+2");
        run("\n1-false");
    }

    static void run(String source) {
        List<Token> tokens = new Scanner(source).scanTokens();
        Expr expr = new Parser(tokens).expression();
        ASTPrinter astPrinter = new ASTPrinter();
        System.out.println(source + " -> " + expr.accept(astPrinter));

        interpreter.interpret(expr);
    }

    static void runPrompt() {

    }
}
