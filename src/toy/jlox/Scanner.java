package toy.jlox;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static toy.jlox.TokenType.*;


/**
 * @author : SCH001
 * @description :
 */
public class Scanner {
    private final List<Token> tokens_ = new ArrayList<>();
    private final String source_;

    // pointers to characters
    int start_ = 0, current_ = 0;
    // current line number
    int line_ = 0;

    public Scanner(String source) {
        this.source_ = source;
    }

    // keyword Map
    private static final Map<String, TokenType> keywords_;
    static {
        keywords_ = new HashMap<>();

        keywords_.put("and", AND);
        keywords_.put("class", CLASS);
        keywords_.put("else", ELSE);
        keywords_.put("false", FALSE);
        keywords_.put("fun", FUN);
        keywords_.put("for", FOR);
        keywords_.put("if", IF);
        keywords_.put("nil", NIL);
        keywords_.put("or", OR);
        keywords_.put("print", PRINT);
        keywords_.put("return", RETURN);
        keywords_.put("super", SUPER);
        keywords_.put("this", THIS);
        keywords_.put("true", TRUE);
        keywords_.put("var", VAR);
        keywords_.put("while", WHILE);
    }

    // helper methods
    private char peek() {
        return source_.charAt(current_);
    }
    private char peekNext() {
        return source_.charAt(current_ + 1);
    }

    private boolean isAtEnd() {
        return current_ >= source_.length();
    }

    private void addToken(TokenType tokenType) {
        // tokens like [ ] <= dont need a literal value
        addToken(tokenType, null);
    }
    private void addToken(TokenType tokenType, Object literal) {
        String text = source_.substring(start_, current_);
        tokens_.add(new Token(tokenType, text, literal, line_));
    }

    /*
        return the current char and move forward the current_ pointer
     */
    private char advance() {
        return source_.charAt(current_++);
    }

    /*
        conditional advance
     */
    private boolean match(char c) {
        if(source_.charAt(current_) == c) {
            ++current_;
            return true;
        }
        return false;
    }

    public List<Token> scanTokens() {
        while (!isAtEnd()) {
            scanToken();
        }
        return tokens_;
    }

    private void scanToken() {
        char c = advance();
        switch (c) {
            case '[':
                addToken(LEFT_BRACE);break;
            case ']':
                addToken(RIGHT_BRACE);break;
            case '(':
                addToken(LEFT_PAREN);break;
            case ')':
                addToken(RIGHT_PAREN);break;
            case ',':
                addToken(COMMA);break;
            case '.':
                addToken(DOT); break;
            case '-':
                // todo: handle negative number
                // seems like its more elegant to do it in parser...
                addToken(MINUS); break;
            case '+':
                addToken(PLUS);break;
            case ';':
                addToken(SEMICOLON);break;
            case '*':
                addToken(STAR);break;
            case ' ':
            case '\t':
                break;
            case '\n':
                line_ ++; break;
            /* below tokens need to lookahead one step
                / //
                ! !=
                = ==
                > >=
                < <=
             */
            case '!':
                addToken(
                        match('=')
                        ? BANG
                        : BANG_EQUAL
                ); break;
            case '=':
                addToken(
                        match('=')
                        ? EQUAL_EQUAL
                        : EQUAL
                ); break;
            case '>':
                addToken(
                        match('=')
                        ? GREATER_EQUAL
                        : GREATER
                ); break;
            case '<':
                addToken(
                        match('=')
                        ? LESS_EQUAL
                        : LESS
                ); break;
            case '/': {
                // / or //
                if(match('/')) {
                    // comment, skip until newline
                    while (!isAtEnd() && source_.charAt(current_) != '\n')
                        current_ ++;
                    // dont handle '\n',
                    // handle it in next loop
                } else {
                    addToken(SLASH);
                }
                break;
            }
            case '"': {
                // string, enable multiple-line string
                while (!isAtEnd() && peek() != '"') {
                    char p = advance();
                    if(p == '\n') ++line_;
                }
                if(isAtEnd()) {
                    // error
                    Lox.error(line_, "No matched \" ");
                } else {
                    // quoted content
                    String text = source_.substring(start_+1, current_);
                    addToken(STRING, text);
                    advance();
                }

                break;
            }
            default:
                if(isDigit(c)) {
                    // number
                    number();
                } else if(isAlpha(c)) {
                    // identifier
                    identifier();
                } else {
                    // error
                    Lox.error(line_, "Invalid char " + c);
                }
                break;
        }
        // move forward start_ pointer
        start_ = current_;
    }
    // number handler
    private void number() {
        while (isDigit(peek())) {
            advance();
        }
        if(match('.')){
            while (isDigit(peek())) {
                advance();
            }
        }
        //
        addToken(NUM,
                Double.parseDouble(source_.substring(start_, current_)));
    }
    // id & keyword handler
    private void identifier() {
        advance(); // already test before
        while (isAlphaOrDigit(peek())) {
            advance();
        }

        String text = source_.substring(start_, current_);
        if(keywords_.containsKey(text)) {
            addToken(keywords_.get(text));
        } else {
            addToken(IDENTIFIER, text);
        }
    }

    // judge character type
    private boolean isDigit(char c) {
        return c >= '0' && c <= '9';
    }
    private boolean isAlpha(char c) {
        return  c  == '_'
                || (c >= 'a' && c <= 'z')
                || (c >= 'A' && c <= 'Z');
    }

    private boolean isAlphaOrDigit(char c) {
        return isAlpha(c) || isDigit(c);
    }
}
