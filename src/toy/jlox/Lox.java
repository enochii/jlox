package toy.jlox;

import toy.tool.FileUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import static toy.jlox.TokenType.EOF;

public class Lox {
    // find more errors rather than executing program
    // when trigger a error, we will set this
    public static boolean repl = false;
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
        String source = FileUtils.readFile("example/unused.txt");

        run(source);
//        runPrompt();
    }

    static void run(String source) {
        // scan
        List<Token> tokens = new Scanner(source).scanTokens();
        // parse
        List<Stmt> stmts   = new Parser(tokens).program();
        // resolve
        new Resolver(interpreter).resolve(stmts);
        // execute
        if(hadError) return;
        interpreter.interpret(stmts);
    }

    static void runPrompt() {
        repl = true;
        while (true) {
            BufferedReader strin=new BufferedReader(new InputStreamReader(System.in));
            try {
                String source = strin.readLine();
                List<Token> tokens = new Scanner(source).scanTokens();
                // parse
                List<Stmt> stmts   = new Parser(tokens).program();
                // execute
                if(hadError || hadRuntimeError) continue;
                interpreter.interpret(stmts);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }
}
