package toy.jlox;

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

    Parser(List<Token> tokens) {
        this.tokens_ = tokens;
        this.current_ = 0;
    }

    public Expr expression() {
        try {
            return equality();
        } catch (ParseError parseError) {
            return null;
        }
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
}
