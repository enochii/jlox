package toy.jlox;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

/**
 * @author : SCH001
 * @description :
 */
public class Resolver implements Expr.Visitor<Void>, Stmt.Visitor<Void> {
    final Stack<Map<String, Boolean>> scopes_ = new Stack<>();
    final Interpreter interpreter_;

    Resolver(Interpreter interpreter) {
        this.interpreter_ = interpreter;
    }

    private void enterScope() {
        scopes_.push(new HashMap<>());
    }

    private void exitScope() {
        scopes_.pop();
    }

    private void declare(Token name) {
        // top level -> global env
        if(scopes_.empty()) return;

        Map<String, Boolean> cur = scopes_.peek();
        if(cur.containsKey(name.lexeme_)) {
            Lox.error(name, "Redefine "+name.lexeme_);
        }
        // initialization not complete
        cur.put(name.lexeme_, false);
    }

    private void define(Token name) {
        if(scopes_.empty()) return;

        scopes_.peek().put(name.lexeme_, true);
    }

    public void resolve(List<Stmt> stmts) {
        for(Stmt stmt:stmts) {
            resolve(stmt);
        }
    }
    private void resolve(Expr expr) {
        expr.accept(this);
    }
    private void resolve(Stmt stmt) {
        stmt.accept(this);
    }

    private void resolveLocal(Expr variable, String name) {
        for(int i=scopes_.size()-1; i>=0; i--) {
            if(scopes_.get(i).containsKey(name)) {
                interpreter_.resolve(variable,
                        scopes_.size() - i - 1);
                return;
            }
        }
        // make global as the target address?
        interpreter_.resolve(variable, scopes_.size());
    }

    @Override
    public Void visitAssign(Expr.Assign expr) {
        resolve(expr.newVal);
        resolveLocal(expr, expr.name.lexeme_);
        return null;
    }

    @Override
    public Void visitBinary(Expr.Binary expr) {
        resolve(expr.left);
        resolve(expr.right);
        return null;
    }

    @Override
    public Void visitUnary(Expr.Unary expr) {
        resolve(expr.expr);
        return null;
    }

    @Override
    public Void visitGrouping(Expr.Grouping expr) {
        resolve(expr.expr);
        return null;
    }

    @Override
    public Void visitLiteral(Expr.Literal expr) {
        return null;
    }

    @Override
    public Void visitVariable(Expr.Variable expr) {
        if(scopes_.empty()) return null;

        Map<String, Boolean> cur = scopes_.peek();
        Token name = expr.var;
//        if(!cur.containsKey(name.lexeme_)) {
//            Lox.error(name, "Undefined variable " + name.lexeme_);
//        } else
        if(scopes_.peek().containsKey(name.lexeme_) && !cur.get(name.lexeme_)) {
            // incomplete definition
            Lox.error(name, "Can not initialize " + name.lexeme_ +
                    " by itself");
        }
        resolveLocal(expr, expr.var.lexeme_);
        return null;
    }

    @Override
    public Void visitLogical(Expr.Logical expr) {
        resolve(expr.left);
        resolve(expr.right);
        return null;
    }

    @Override
    public Void visitCall(Expr.Call stmt) {
        resolve(stmt.callee);
        for(Expr arg: stmt.args) {
            resolve(arg);
        }
        return null;
    }

    @Override
    public Void visitExprStmt(Stmt.ExprStmt stmt) {
        resolve(stmt.expr);
        return null;
    }

    @Override
    public Void visitPrintStmt(Stmt.PrintStmt stmt) {
        resolve(stmt.expr);
        return null;
    }

    @Override
    public Void visitVarDecl(Stmt.VarDecl stmt) {
        declare(stmt.name);
        if(stmt.expr != null)
            resolve(stmt.expr);
        define(stmt.name);
        return null;
    }

    @Override
    public Void visitFuncDecl(Stmt.FuncDecl stmt) {
        declare(stmt.name);
        define(stmt.name); // eagerly define

        enterScope();
        for(Token pa:stmt.parameters) {
            declare(pa);
            define(pa);
        }
        // body and the argument list are in the same scope!
        resolve(stmt.body.stmts);
        exitScope();

        return null;
    }

    @Override
    public Void visitBlock(Stmt.Block stmt) {
        enterScope();
        resolve(stmt.stmts);
        exitScope();
        return null;
    }

    @Override
    public Void visitIfStmt(Stmt.IfStmt expr) {
        resolve(expr.cond);
        resolve(expr.elseBranch);
        if(expr.thenBranch != null) resolve(expr.thenBranch);
        return null;
    }

    @Override
    public Void visitWhileStmt(Stmt.WhileStmt expr) {
        resolve(expr.cond);
        resolve(expr.body);
        return null;
    }

    @Override
    public Void visitBreakStmt(Stmt.BreakStmt expr) {
        return null;
    }

    @Override
    public Void visitContinueStmt(Stmt.ContinueStmt expr) {
        return null;
    }

    @Override
    public Void visitReturnStmt(Stmt.ReturnStmt expr) {
        if(expr.expr != null) {
            resolve(expr.expr);
        }
        return null;
    }

    @Override
    public Void visitClassStmt(Stmt.ClassStmt expr) {
        // todo
        declare(expr.name);
        define(expr.name);
        return null;
    }
}
