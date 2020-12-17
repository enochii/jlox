package toy.jlox;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import static toy.jlox.TokenType.*;

/**
 * @author : SCH001
 * @description :
 */
public class Interpreter implements Expr.Visitor<Object>, Stmt.Visitor<Void> {
    private final Map<Expr, Integer> lexicalAddr = new HashMap<>();
    // variables bindings
    final Environment globals_ = new Environment();
    Environment env_ = globals_;

    // for debug
    private final ASTPrinter astPrinter = new ASTPrinter();

    Interpreter() {
        // native function
        globals_.define("clock", new LoxCallable() {
            @Override
            public Object call(Interpreter interpreter, List<Object> args) {
                return System.currentTimeMillis() / 1000;
            }

            @Override
            public int arity() {
                return 0;
            }
        });
    }

    // lexical address related methods
    void resolve(Expr expr, Integer dist) {
        lexicalAddr.put(expr, dist);
    }

    private Object lookupVariable(Expr.Variable var) {
        int dist = lexicalAddr.get(var);
        return env_.getAt(dist, var.var);
    }
    // end

    @Override
    public Void visitExprStmt(Stmt.ExprStmt stmt) {
        evaluate(stmt.expr);
        return null;
    }

    @Override
    public Void visitPrintStmt(Stmt.PrintStmt stmt) {
        Object val = evaluate(stmt.expr);
        String str = stringify(val);
        if(stmt.newline) {
            System.out.println(str);
        } else {
            System.out.print(str);
        }
        return null;
    }

    @Override
    public Void visitVarDecl(Stmt.VarDecl stmt) {
        Object res = null;
        if(stmt.expr != null) {
            res = evaluate(stmt.expr);
        }
        env_.define(stmt.name.lexeme_, res);
        return null;
    }

    @Override
    public Void visitFuncDecl(Stmt.FuncDecl stmt) {
        LoxFunction loxFunction = new LoxFunction(stmt, env_);

        env_.define(stmt.name.lexeme_, loxFunction);
        return null;
    }

    void executeBlock(Stmt.Block block, Environment curEnv) {
        Environment previous = env_;

        try {
            env_ = curEnv;
            for(Stmt stmt: block.stmts) {
                execute(stmt);
            }
        } finally {
            env_ = previous;
        }
    }

    @Override
    public Void visitBlock(Stmt.Block block) {
        // todo
        executeBlock(block, new Environment(env_));
        return null;
    }

    @Override
    public Void visitIfStmt(Stmt.IfStmt expr) {
        boolean res = isTruthy(evaluate(expr.cond));
        if(res) {
            execute(expr.thenBranch);
        } else if(expr.elseBranch != null){
            execute(expr.elseBranch);
        }
        return null;
    }

    @Override
    public Void visitWhileStmt(Stmt.WhileStmt expr) {
        while (isTruthy(evaluate(expr.cond))) {
            try {
                execute(expr.body);
            } catch (BreakExceptoin e) {
                debug("catch a break exception");
                break;
            } catch (ContinueException e) {
                debug("catch a continue exception");
                // and then do nothing
            }
        }
        return null;
    }

    @Override
    public Void visitBreakStmt(Stmt.BreakStmt expr) {
        throw new BreakExceptoin();
    }

    @Override
    public Void visitContinueStmt(Stmt.ContinueStmt expr) {
        throw new ContinueException();
    }

    @Override
    public Void visitReturnStmt(Stmt.ReturnStmt stmt) {
        Object callRes_ = null;
        if(stmt.expr != null) {
            callRes_ = evaluate(stmt.expr);
        }
        throw new ReturnException(callRes_);
    }

    @Override
    public Void visitClassStmt(Stmt.ClassStmt stmt) {
        // todo
        String clsName = stmt.name.lexeme_;
        LoxClass loxClass = new LoxClass(clsName, stmt.methods);
        env_.define(clsName, loxClass);
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
        int dist = lexicalAddr.get(expr);
        env_.assignAt(dist, token, res);
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
        return lookupVariable(expr);
    }

    @Override
    public Object visitLogical(Expr.Logical logical) {
        Object left = evaluate(logical.left);
        if(logical.op.tokenType_ == OR) {
            if(!isTruthy(left))
                return evaluate(logical.right);
        } else if(logical.op.tokenType_ == AND) {
            if(isTruthy(left))
                return evaluate(logical.right);
        }
        return left;
    }

    @Override
    public Object visitCall(Expr.Call call) {
        Object callee = evaluate(call.callee);
        if(!(callee instanceof LoxCallable)) {
            throw new RuntimeError(call.token, call.callee
                    + " is not a callable object!");
        }

        LoxCallable func = (LoxCallable) callee;

        if(func.arity() != call.args.size()) {
            throw new RuntimeError(call.token,
                    "the number of parameter and arguments not matched");
        }

        List<Object> args = new ArrayList<>();
        for(int i=0; i<call.args.size(); i++) {
            args.add(evaluate(call.args.get(i)));
        }

        return func.call(this, args);
    }

    @Override
    public Object visitGet(Expr.Get expr) {
        Object object = evaluate(expr.object);
        if(object instanceof LoxInstance) {
            LoxInstance instance = (LoxInstance)object;
            return instance.get(expr.field);
        }
        throw new RuntimeError(expr.field,
                "object is not a Lox instance");
    }

    @Override
    public Object visitSet(Expr.Set expr) {
        Object val = evaluate(expr.val);
        Object object = evaluate(expr.object);
        if(object instanceof LoxInstance) {
            LoxInstance instance = (LoxInstance)object;
            instance.set(expr.field, val);
            return val;
        }
        throw new RuntimeError(expr.field,
                "object is not a Lox instance");
    }

    static class BreakExceptoin extends RuntimeException {
    }

    static class ReturnException extends RuntimeException {
        final Object retVal;
        ReturnException(Object retVal) {
            this.retVal = retVal;
        }
    }

    static class ContinueException extends RuntimeException {
    }

    void debug(String msg) {
        msg = "[DEBUG]: "+msg;
        System.out.println(msg);
    }
}
