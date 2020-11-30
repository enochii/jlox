package toy.jlox;

import java.util.ArrayList;
import java.util.List;

import static toy.jlox.TokenType.*;

/**
 * @author : SCH001
 * @description :
 */
public class Parser {
    private class ParseError extends RuntimeException {}

    private List<Token> tokens_;
    // points to the current token
    private int current_;
    // parsed statements
    private List<Stmt> stmts_;

    Parser(List<Token> tokens) {
        this.tokens_ = tokens;
        this.current_ = 0;
        this.stmts_ = new ArrayList<>();
    }

    /*
        program -> declaration* EOF

        declaration -> definitionStmt
                    |  statement

        // distinct the declaration and non-declaring statement
        // to disable things like "if() var x = 1;"
        // some places we allow the latter but not the former
     */
    public List<Stmt> program() {
        while (peek().tokenType_ != EOF) {
            Stmt stmt = declaration();
            stmts_.add(stmt);
        }
        advance(); //EOF
        return stmts_;
    }


    private Stmt declaration() {
        if(match(VAR)) return definitionStmt();

        return statement();
    }

    private Stmt printStmt() {
        // the print token has been consumed already
        Expr expr = expression();
        consume(SEMICOLON, "Expect a ';' here to end a print statement");
        return new Stmt.PrintStmt(expr);
    }

    // this should be very helpful for assignment(because it's actually a expression rather than a statement)
    // we need to consume the semicolon!
    private Stmt exprStmt() {
        Expr expr = expression();
        consume(SEMICOLON, "Expect a ';' here to end a expression statement");
        return new Stmt.ExprStmt(expr);
    }

    private Stmt definitionStmt() {
        // var token has been consumed
        if(match(IDENTIFIER)) {
            Token var = previous();

            Expr rvalue = null;
            if(match(EQUAL)) {
                rvalue = expression();
            }
            consume(SEMICOLON, "Expect a ';' here");
            return new Stmt.DefinitionStmt(var.lexeme_, rvalue);
        }
        throw error(peek(), "Invalid Definition");
    }

    // collect statements in the block
    // declaration rather than statement means that
    // variable declaration is allowed!
    // block -> { declaration* }
    private List<Stmt> block() {
        List<Stmt> stmts = new ArrayList<>();
        while (!check(RIGHT_BRACE) && !isAtEnd()) {
            stmts.add(declaration());
        }
        consume(RIGHT_BRACE, "Expect a '}' to end block");
        return stmts;
    }

    // statement -> printStmt
    //              exprStmt
    //              block
    private Stmt statement() {
        if(match(PRINT)) {
            return printStmt();
        }
        // the block one
        if(match(LEFT_BRACE)) {
            return new Stmt.Block(block());
        }
        return exprStmt();
    }


    // Expressions
    private Expr expression() {
        try {
            return assignment();
        } catch (ParseError parseError) {
            // todo: error recovery, synchronization
            return null;
        }
    }

    // expression -> assignment
    // assignment -> (name =) expression
    //              equality
    private Expr assignment() {
        if(peek().tokenType_ == IDENTIFIER &&
            next().tokenType_ == EQUAL
        ) {
            Token name = advance();
            advance(); // =
            return new Expr.Assign(name, expression());
        }
        return equality();
    }

    // equality -> equality (!= | ==) comparison
    //          -> comparison
    // to remove recursion, we transform into:
    // equality -> comparison (("!=" | "==") comparison)*
    private Expr equality() {
        Expr expr = comparision();
        while (match(BANG_EQUAL, EQUAL_EQUAL)) {
            Token op = previous();
            Expr right = comparision();
            // left associativity
            expr = new Expr.Binary(expr, op, right);
        }
        return expr;
    }

    // < <= > >=
    // term (("<" | "<=" | ">" | ">=") term)*
    private Expr comparision() {
        Expr expr = term();
        while (match(LESS, LESS_EQUAL, GREATER, GREATER_EQUAL)) {
            Token op = previous();
            Expr right = term();
            expr = new Expr.Binary(expr, op, right);
        }
        return expr;
    }

    // + -
    private Expr term() {
        Expr expr = factor();
        while (match(PLUS, MINUS)) {
            Token op = previous();
            Expr right = factor();
            expr = new Expr.Binary(expr, op, right);
        }
        return expr;
    }

    // * /
    private Expr factor() {
        Expr expr = unary();
        while (match(STAR, SLASH)) {
            Token op = previous();
            Expr right = unary();
            expr = new Expr.Binary(expr, op, right);
        }
        return expr;
    }

    // ! -
    private Expr unary() {
        if (match(MINUS, BANG)) {
            Token op = previous();
            // recursion
            return new Expr.Unary(op, unary());
        }
        return primary();
    }

    // literal
    // "true" | "false" | "nil" | STRING | NUMBER
    // "(" expression ")"
    private Expr primary() {
        if(match(TRUE)) return new Expr.Literal(true);
        if(match(FALSE)) return new Expr.Literal(false);
        if(match(NIL)) return new Expr.Literal(null);

        if(match(STRING, NUM))
            return new Expr.Literal(previous().literal_);
        if (match(IDENTIFIER))
            return new Expr.Variable(previous());
        // parentheses
        if(match(LEFT_PAREN)) {
            Expr grouped = expression();
            consume(RIGHT_PAREN, "Expected token \")\" ");
            return new Expr.Grouping(grouped);
        }

        throw error(peek(), "Unexpected token");
    }

    ParseError error(Token token, String msg) {
        Lox.error(token, msg);
        return new ParseError();
    }

    // consume the appointed token type, or else
    // raise an exception
    private void consume(TokenType type, String msg) {
        if(!match(type)) {
            throw error(peek(), msg);
        }
    }

    // helper methods
    private boolean match(TokenType ...candidates) {
        for(TokenType t : candidates) {
            if(peek().tokenType_ == t) {
                advance();
                return true;
            }
        }
        return false;
    }

    private boolean match(TokenType t) {
        if(peek().tokenType_ == t) {
            advance();
            return true;
        }
        return false;
    }

    private boolean isAtEnd() {
        return peek().tokenType_ == EOF;
    }

    private boolean check(TokenType type) {
        return peek().tokenType_ == type;
    }

    private Token peek() {
        return tokens_.get(current_);
    }

    // get current token and move forward
    private Token advance() {
        return tokens_.get(current_++);
    }

    private Token previous() {
        return tokens_.get(current_ - 1);
    }

    private Token next() {
        return tokens_.get(current_ + 1);
    }
}
