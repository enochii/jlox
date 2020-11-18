package java.com.jlox;

/**
 * @author : SCH001
 * @description :
 */
public class Token {
    final TokenType tokenType_;
    final String lexeme_; // raw text in source code
    final Object literal_; // literal value of number or string
    final int line_;

    public Token(TokenType tokenType, String lexeme, Object literal, int line) {
        this.tokenType_ = tokenType;
        this.lexeme_ = lexeme;
        this.literal_ = literal;
        this.line_ = line;
    }
}
