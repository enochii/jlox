package toy.jlox;

import java.util.ArrayList;
import java.util.Arrays;
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

        declaration -> VarDecl
                    -> funDecl
                    -> clsDecl
                    |  statement

        // distinct the declaration and non-declaring statement
        // to disable things like "if() var x = 1;"
        // some places we allow the latter but not the former
     */
    public List<Stmt> program() {
        while (!isAtEnd()) {
            try {
                Stmt stmt = declaration();
                stmts_.add(stmt);
            } catch (ParseError error) {
                synchronize();
            }
        }
        advance(); //EOF
        return stmts_;
    }


    private Stmt declaration() {
        if(match(CLASS)) return clsDecl();
        if(match(VAR)) return varDecl();
        if(match(FUN)) return funcDecl("function");

        return statement();
    }

    private Stmt printStmt() {
        // the print token has been consumed already
        Token action = previous();
        Expr expr = expression();
        consume(SEMICOLON, "Expect a ';' here to end a print statement");
        return new Stmt.PrintStmt(action.tokenType_==PRINTLN, expr);
    }

    // this should be very helpful for assignment(because it's actually a expression rather than a statement)
    // we need to consume the semicolon!
    private Stmt exprStmt() {
        Expr expr = expression();
        if(Lox.repl && !check(SEMICOLON)) {
            // wrap it so can enable expression in REPL mode!
            return new Stmt.PrintStmt(true, expr);
        }
        consume(SEMICOLON, "Expect a ';' here to end a expression statement");
        return new Stmt.ExprStmt(expr);
    }

    private Stmt.FuncDecl funcDecl(String kind) {
        // "fun" token has been eaten
        if(match(IDENTIFIER)) {
            Token name = previous();
            consume(LEFT_PAREN,
                    "Expect a '(' for " + kind + " definition");
            List<Token> parameters = null;
            if(!check(RIGHT_PAREN)) {
                parameters = paras();
            } else {
                parameters = new ArrayList<>();
            }
            consume(RIGHT_PAREN,
                    "Expect a ')' for " + kind + " definition");
            consume(LEFT_BRACE,
                    "Expect a '{' for " + kind + " definition");
            Stmt.Block body = new Stmt.Block(block());

            return new Stmt.FuncDecl(name, parameters, body);
        }
        throw error(peek(), "Expect a ID for " + kind + " name");
    }

    // collect the parameters for function definition
    private List<Token> paras() {
        List<Token> ps = new ArrayList<>();
        do {
            Token p = peek();
            if(match(IDENTIFIER)) {
                ps.add(p);
            } else {
                throw error(p, "Expect a parameter name");
            }
        } while(match(COMMA));
        return ps;
    }

    private Stmt varDecl() {
        // var token has been consumed
        if(match(IDENTIFIER)) {
            Token var = previous();

            Expr rvalue = null;
            if(match(EQUAL)) {
                rvalue = expression();
            }
            consume(SEMICOLON, "Expect a ';' here");
            return new Stmt.VarDecl(var, rvalue);
        }
        throw error(peek(), "Expect a ID for variable name");
    }

    // class CLASS_NAME { funcDecl* }
    private Stmt clsDecl() {
        Token clsName = consume(IDENTIFIER, "Expect a class name");

        Expr.Variable superClass = null;
        if(match(LESS)) {
            consume(IDENTIFIER, "Expect a super class");
            superClass = new Expr.Variable(previous());
        }
        consume(LEFT_BRACE, "Expect a '{' after class name");

        List<Stmt.FuncDecl> methods = new ArrayList<>();

        while (!check(RIGHT_BRACE)) {
            methods.add(funcDecl("method"));
        }

        consume(RIGHT_BRACE, "Expect a '}");
        return new Stmt.ClassStmt(clsName, methods, superClass);
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

    private Stmt.IfStmt ifStmt() {
        consume(LEFT_PAREN, "Expect a '('");
        Expr condition = expression();
        consume(RIGHT_PAREN, "Expect a ')'");
        Stmt thenBranch = statement();
        Stmt elseBranch = null;
        if(match(ELSE)) {
            elseBranch = statement();
        }
        return new Stmt.IfStmt(condition, thenBranch, elseBranch);
    }

    private Stmt.WhileStmt whileStmt() {
        consume(LEFT_PAREN, "Expect a '('");
        Expr condition = expression();
        consume(RIGHT_PAREN, "Expect a ')'");

        Stmt body = statement();
        return new Stmt.WhileStmt(condition, body);
    }

    // syntax sugar
    // forStmt -> FOR((VarDecl | expressionStmt |;) expression ; expression) statement
    private Stmt forStmt() {
        consume(LEFT_PAREN, "Expect a '('");
        // initializer
        Stmt init = null;
        if(match(VAR)) {
            init = varDecl();
        } else if(!match(SEMICOLON)) {
            init = exprStmt();
        }

        // empty condition means Always TRUE
        Expr condition = new Expr.Literal(true);
        if(!check(SEMICOLON)) {
            condition = expression();
        }
        consume(SEMICOLON, "Expect a ';'");

        Expr increment = null;
        if(!check(RIGHT_PAREN)) {
            increment = expression();
        }
        consume(RIGHT_PAREN, "Expect a ')'");

        Stmt body = statement();

        // sugar!
        // for(var i=0; i<5; i=i+1) stmt;
        /*
            {
                var i = 0;
                while(i<5) {
                    stmt;
                    i=i+1;
                }
            }
         */
        Stmt whileBody = increment==null ?
                body :
                createBlock(body, new Stmt.ExprStmt(increment));

        Stmt whileStmt = new Stmt.WhileStmt(
                condition, whileBody
        );
        if(init != null) {
            whileStmt = createBlock(init, whileStmt);
        }

        return whileStmt;
    }

    private Stmt returnStmt() {
        Expr expr = null;
        if(!check(SEMICOLON)) {
            expr = expression();
        }
        Token semicolon = consume(SEMICOLON, "Expect a ';' after return");
        return new Stmt.ReturnStmt(expr, semicolon);
    }

    private Stmt breakStmt() {
        Token semicolon = consume(SEMICOLON, "Expect a ';' after break");
        return new Stmt.BreakStmt(semicolon);
    }

    private Stmt continueStmt() {
        Token semicolon = consume(SEMICOLON, "Expect a ';' after continue");
        return new Stmt.ContinueStmt(semicolon);
    }

    // statement -> printStmt
    //              exprStmt
    //              block
    //              ifStmt
    //              forStmt
    //              whileStmt
    //              returnStmt
    //              breakStmt
    //              continueStmt

    private Stmt statement() {
        if(match(PRINT, PRINTLN)) {
            return printStmt();
        }
        // the block one
        if(match(LEFT_BRACE)) {
            return new Stmt.Block(block());
        }

        if(match(IF)) {
            return ifStmt();
        }
        if(match(WHILE)) {
            return whileStmt();
        }
        if(match(FOR)) {
            return forStmt();
        }
        if(match(RETURN)) {
            return returnStmt();
        }
        if(match(BREAK)) {
            return breakStmt();
        }
        if(match(CONTINUE)) {
            return continueStmt();
        }

        return exprStmt();
    }


    // Expressions
    private Expr expression() {
        return assignment();
    }

    // expression -> assignment
    // assignment -> (name =) expression
    //              equality
    private Expr assignment() {
        Expr expr = or();
        if(match(EQUAL)) {
            if(expr instanceof Expr.Variable) {
                Expr.Variable name = (Expr.Variable)expr;
                return new Expr.Assign(name.var, assignment());
            } else if(expr instanceof Expr.Get) {
                Expr.Get get = (Expr.Get)expr;
                return new Expr.Set(get.object, get.field, assignment());
            }
            error(previous(), "Expect lhs is a r-value");
        }
        return expr;
    }

    // or -> and (OR and)*
    /*
        if the production is like: or -> and (OR or)*,
        then the operator is right-associate ?
     */
    private Expr or() {
        Expr left = and();
        while (match(OR)) {
            Token op = previous();
            Expr right = and();
            left = new Expr.Logical(left, op, right);
        }
        return left;
    }

    // and -> equality (AND equality)*
    private Expr and() {
        Expr left = equality();
        while (match(AND)) {
            Token op = previous();
            Expr right = equality();
            left = new Expr.Logical(left, op, right);
        }
        return left;
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
        return call();
    }

    private Expr call() {
        Expr expr = primary();
        while(true) {
            if(match(LEFT_PAREN)) {
                expr = finishCall(expr);
            } else if(match(DOT)) {
                Token field = consume(IDENTIFIER, "Expect a field token after '.'");
                expr = new Expr.Get(expr, field);
            } else {
                break;
            }
        }
        return expr;
    }

    private Expr finishCall(Expr callee) {
        List<Expr> args = null;
        if(check(RIGHT_PAREN)) {
            // empty argument list
            args = new ArrayList<>();
        } else {
            args = arguments();
        }
        Token paren = consume(RIGHT_PAREN, "Expect a ')' for a call");
        // I think the parameter-limit check should have just handled in
        // function definition??
        if(args.size() > Const.MAX_ARGUMENT_SIZE) {
            // not throw so no panic mode
            // the parsing dont need sync!
            error(paren, "number of argument should not be larger than "
                    + Const.MAX_ARGUMENT_SIZE);
        }
        return new Expr.Call(callee, args, paren);
    }

    private List<Expr> arguments() {
        List<Expr> args = new ArrayList<>();
        do {
            args.add(expression());
        } while(match(COMMA));
        return args;
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
        if (match(IDENTIFIER) || match(THIS)) // this should be handled
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

    // create block
    Stmt.Block createBlock(Stmt... stmts) {
        List<Stmt> stmtList = new ArrayList<>(Arrays.asList(stmts));
        return new Stmt.Block(stmtList);
    }

    // consume the appointed token type, or else
    // raise an exception
    private Token consume(TokenType type, String msg) {
        if(!match(type)) {
            throw error(peek(), msg);
        }
        return previous();
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

    private boolean check(TokenType... types) {
        for (TokenType type: types) {
            if(check(type)) return true;
        }
        return false;
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

    private void synchronize() {
        for (; current_ < tokens_.size(); current_++) {
            if(match(SEMICOLON)) return;

            if(check(VAR, FOR, LEFT_BRACE, EOF)) return;
        }
        // or we will reach the end
    }
}
