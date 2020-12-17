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

    @Override
    public String visitLogical(Expr.Logical logical) {
        return "(" + logical.op.lexeme_ + " "
                + print(logical.left) + " "
                + print(logical.right) + ")";
    }

    @Override
    public String visitCall(Expr.Call expr) {
        StringBuilder ret = new StringBuilder(print(expr.callee) + "(");
        if (expr.args != null)
            for(Expr expr1:expr.args) {
                ret.append(print(expr1)+", ");
            }
        ret.append(")");
        return ret.toString();
    }

    @Override
    public String visitGet(Expr.Get expr) {
        return print(expr.object) + "." + expr.field.lexeme_;
    }

    @Override
    public String visitSet(Expr.Set expr) {
        return print(expr.object) + "." + expr.field.lexeme_ + "=" + print(expr.val);
    }
}
