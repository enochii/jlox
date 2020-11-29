package toy.jlox;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static toy.jlox.TokenType.*;

/**
 * @author : SCH001
 * @description :
 */
public class Interpreter implements Expr.Visitor<Object>, Stmt.Visitor<Void> {
    // variables bindings
//    private Map<String, Object> bindings_ = new HashMap<>();
    Environment env_ = new Environment();

    @Override
    public Void visitExprStmt(Stmt.ExprStmt stmt) {
        evaluate(stmt.expr);
        return null;
    }

    @Override
    public Void visitPrintStmt(Stmt.PrintStmt stmt) {
        Object val = evaluate(stmt.expr);
        System.out.println(stringify(val));
        return null;
    }

    @Override
    public Void visitDefinitionStmt(Stmt.DefinitionStmt stmt) {
        Object res = null;
        if(stmt.expr != null) {
            res = evaluate(stmt.expr);
        }
        env_.define(stmt.name, res);
        return null;
    }

    static class RuntimeError extends RuntimeException {
        Token token;
        RuntimeError(Token token, String msg) {
            super(msg);
            this.token = token;
        }
    }

    public void interpret(List<Stmt> stmts) {
        for(Stmt stmt: stmts) {
            execute(stmt);
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

    private void execute(Stmt stmt) {
        stmt.accept(this);
    }

    private Object evaluate(Expr expr) {
        return expr.accept(this);
    }

    // stringify the result
    private String stringify(Object val) {
        if(val == null) return "nil";
        if(val instanceof Double) {
            String str = val.toString();
            if(str.endsWith(".0")) return str.substring(0, str.length()-2);
        }

        return val.toString();
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
    public Object visitAssign(Expr.Assign expr) {
        Token token = expr.name;
        Object res = null;
        // it maybe a recursive assignment
        // and we do a post order visit
        if(expr.newVal != null) {
            res = evaluate(expr.newVal);
        }
        env_.assign(token, res);
        return res;
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

    @Override
    public Object visitVariable(Expr.Variable expr) {
        return env_.get(expr.var);
    }
}