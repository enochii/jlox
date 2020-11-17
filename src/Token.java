/**
 * @author : SCH001
 * @description :
 */
public class Token {
    final TokenType tokenType;
    final String lexeme;
    final Object literal; // literal value of number or string
    final int line;

    public Token(TokenType tokenType, String lexeme, Object literal, int line) {
        this.tokenType = tokenType;
        this.lexeme = lexeme;
        this.literal = literal;
        this.line = line;
    }
}
