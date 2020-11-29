package toy.jlox;

/**
 * @author : SCH001
 * @description :
 */
public class ASTPrinter implements Expr.Visitor<String> {
    public String print(Expr expr) {
        return expr.accept(this);
    }
    @Override
    public String visitAssign(Expr.Assign expr) {
        return expr.name.lexeme_ + " = "
        + print(expr.newVal);
    }

    @Override
    public String visitBinary(Expr.Binary binary) {
        return "(" + binary.op.lexeme_ + " "
                + print(binary.left) + " "
                + print(binary.right) + ")";
    }

    @Override
    public String visitUnary(Expr.Unary unary) {
        return "(" + unary.op.lexeme_ + " "
                + print(unary.expr) + ")";
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
