package toy.jlox;

/**
 * @author : SCH001
 * @description :
 */
public class ASTPrinter implements Expr.Visitor<String> {
    @Override
    public String visitBinary(Expr.Binary binary) {
        return "(" + binary.op.lexeme_ + " "
                + binary.left.accept(this) + " "
                + binary.right.accept(this) + ")";
    }

    @Override
    public String visitUnary(Expr.Unary unary) {
        return "(" + unary.op.lexeme_ + " "
                + unary.expr.accept(this) + ")";
    }

    @Override
    public String visitGrouping(Expr.Grouping grouping) {
        return "(" + grouping.expr.accept(this) + ")";
    }

    @Override
    public String visitLiteral(Expr.Literal literal) {
        return literal.val.toString();
    }

    @Override
    public String visitVariable(Expr.Variable expr) {
        return expr.var.toString();
    }
}
