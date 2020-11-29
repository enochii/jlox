package toy.jlox;

import static toy.jlox.TokenType.*;

/**
 * @author : SCH001
 * @description :
 */
public class ASTEvaluator implements Visitor<Object> {
    private class RuntimeError extends RuntimeException {
        Token token;
        String msg;
        RuntimeError(Token token, String msg) {
            super(msg);
            this.token = token;
        }
    }
    public void interpret(Expr expr) {
        try {
            Object res = evaluate(expr);
            System.out.println("results: " + stringify(res));
        } catch (RuntimeError error) {
            Lox.runtimeError(error.token, error.getMessage());
        }
    }
    private Object evaluate(Expr expr) {
        return expr.accept(this);
    }

    // stringify the result
    private String stringify(Object val) {
        if(val instanceof Boolean ) {
            return val.toString();
        } else if(val instanceof Double) {
            String str = val.toString();
            if(str.endsWith(".0")) return str.substring(0, str.length()-2);
        }

        return null;
    }

    @Override
    public Object visitBinary(Expr.Binary binary) {
        // post order traversal
        Object left = evaluate(binary.left);
        Object right = evaluate(binary.right);
        Token op = binary.op;
        switch (op.tokenType_) {
            case PLUS:
                if(left instanceof Double && right instanceof Double)
                    return (double)left + (double)right;
                if(left instanceof String && right instanceof String)
                    return left + (String)right;

                throw new RuntimeError(binary.op, op.lexeme_
                        + " should has two numbers or two strings");
            case MINUS:
                checkNumberOperands(op, left, right);
                return (double)left - (double)right;
            case STAR:
                checkNumberOperands(op, left, right);
                return (double)left * (double)right;
            case SLASH:
                checkNumberOperands(op, left, right);
                checkDivisionByZero(op, right);
                return (double)left / (double)right;
            case BANG_EQUAL:
                checkNumberOperands(op, left, right);
                return !isEqual(left, right);
            case EQUAL_EQUAL:
                checkNumberOperands(op, left, right);
                return isEqual(left, right);
            case LESS:
                checkNumberOperands(op, left, right);
                return (double)left < (double)right;
            case LESS_EQUAL:
                checkNumberOperands(op, left, right);
                return (double)left <= (double)right;
            case GREATER:
                checkNumberOperands(op, left, right);
                return (double)left > (double)right;
            case GREATER_EQUAL:
                checkNumberOperands(op, left, right);
                return (double)left >= (double)right;
        }

        // unreachable
        return null;
    }

    private boolean isEqual(Object left, Object right) {
        if(left == null) return right == null;
        return left.equals(right);
    }

    private void checkNumberOperand(Token token, Object operand) {
        if(operand instanceof Double) return;

        throw new RuntimeError(token, token.lexeme_
                + " should has number operand");
    }

    private void checkNumberOperands(Token token, Object left,
                                     Object right) {
        if(left instanceof Double && right instanceof Double) return;

        throw new RuntimeError(token, token.lexeme_
                + " should has two number operands");
    }

    private void checkDivisionByZero(Token token, Object divisor) {
        if((double)divisor == 0) {
            throw new RuntimeError(token, "Division By 0");
        }
    }

    @Override
    public Object visitUnary(Expr.Unary unary) {
        Object res = evaluate(unary.expr);
        Token op = unary.op;
        if(op.tokenType_ == MINUS) {
            checkNumberOperand(op, res);
            return -((double)res);
        } else if(op.tokenType_ == BANG) {
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
