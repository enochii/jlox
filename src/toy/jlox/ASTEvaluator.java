package toy.jlox;

import static toy.jlox.TokenType.*;

/**
 * @author : SCH001
 * @description :
 */
public class ASTEvaluator implements Visitor<Object> {
    private Object evaluate(Expr expr) {
        return expr.accept(this);
    }

    @Override
    public Object visitBinary(Expr.Binary binary) {
        // post order traversal
        Object left = evaluate(binary.left);
        Object right = evaluate(binary.right);
        switch (binary.op.tokenType_) {
            case PLUS:
                if(left instanceof Double && right instanceof Double)
                    return (double)left + (double)right;
                if(left instanceof String && right instanceof String)
                    return left + (String)right;
                break;
            case MINUS:
                return (double)left - (double)right;
            case STAR:
                return (double)left * (double)right;
            case SLASH:
                return (double)left / (double)right;
            case BANG_EQUAL:
                return !isEqual(left, right);
            case EQUAL_EQUAL:
                return isEqual(left, right);
            case LESS:
                return (double)left < (double)right;
            case LESS_EQUAL:
                return (double)left <= (double)right;
            case GREATER:
                return (double)left > (double)right;
            case GREATER_EQUAL:
                return (double)left >= (double)right;
        }

        // unreachable
        return null;
    }

    private boolean isEqual(Object left, Object right) {
        if(left == null) return right == null;
        return left.equals(right);
    }

    @Override
    public Object visitUnary(Expr.Unary unary) {
        Object res = evaluate(unary.expr);
        if(unary.op.tokenType_ == MINUS) {
            return -((double)res);
        } else if(unary.op.tokenType_ == BANG) {
            return !(isTruthy(res));
        } else {
            return null;
        }
    }

    // false, null -> false-y
    // otherwise, truthy
    private boolean isTruthy(Object val) {
        if(val == null) return false;
        return !(val.equals(false));
    }

    @Override
    public Object visitGrouping(Expr.Grouping grouping) {
        return evaluate(grouping.expr);
    }

    @Override
    public Object visitLiteral(Expr.Literal literal) {
        return literal.val;
    }
}
