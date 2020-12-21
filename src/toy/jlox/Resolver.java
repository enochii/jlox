package toy.jlox;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import static toy.jlox.Resolver.FUNCTION_TYPE.*;
import static toy.jlox.Resolver.VARIABLE_STATE.*;

/**
 * @author : SCH001
 * @description :
 */
public class Resolver implements Expr.Visitor<Void>, Stmt.Visitor<Void> {
    final Stack<Map<String, VARIABLE_STATE>> scopes_ = new Stack<>();
    final Interpreter interpreter_;

    private FUNCTION_TYPE curFunctionType = NONE;

    enum VARIABLE_STATE {
        DECLARED,
        INITIALIZED,
        READ
    }

    enum FUNCTION_TYPE {
        NONE,
        FUNCTION,
        METHOD
    }

    Resolver(Interpreter interpreter) {
        this.interpreter_ = interpreter;
    }

    private void enterScope() {
        scopes_.push(new HashMap<>());
    }

    private void exitScope() {
        for(Map.Entry<String, VARIABLE_STATE> entry:
            scopes_.peek().entrySet()) {
            if(entry.getValue() == INITIALIZED) {
                System.out.println("[Warning]: " + entry.getKey() + " is declared but never used.");
            }
        }
        scopes_.pop();
    }

    private void declare(Token name) {
        // top level -> global env
        if(scopes_.empty()) return;

        Map<String, VARIABLE_STATE> cur = scopes_.peek();
        if(cur.containsKey(name.lexeme_)) {
            Lox.error(name, "Redefine "+name.lexeme_);
        }
        // initialization not complete
        cur.put(name.lexeme_, DECLARED);
    }

    private void define(Token name) {
        if(scopes_.empty()) return;

        scopes_.peek().put(name.lexeme_, INITIALIZED);
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

    private void resolveLocal(Expr variable, String name, boolean isRead) {
        for(int i=scopes_.size()-1; i>=0; i--) {
            if(scopes_.get(i).containsKey(name)) {
                interpreter_.resolve(variable,
                        scopes_.size() - i - 1);
                if(isRead) {
                    scopes_.get(i).put(name, READ);
                }
                return;
            }
        }
        // make global as the target address?
        interpreter_.resolve(variable, scopes_.size());
    }

    @Override
    public Void visitAssign(Expr.Assign expr) {
        resolve(expr.newVal);
        resolveLocal(expr, expr.name.lexeme_, false);
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

        if(!scopes_.empty() && scopes_.peek().containsKey(expr.var.lexeme_)
                && scopes_.peek().get(expr.var.lexeme_)==DECLARED) {
            // incomplete definition
            Lox.error(expr.var, "Can not initialize " + expr.var.lexeme_ +
                    " by itself");
        }
        resolveLocal(expr, expr.var.lexeme_, true);
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
    public Void visitGet(Expr.Get expr) {
        resolve(expr.object);
        return null;
    }

    @Override
    public Void visitSet(Expr.Set expr) {
        resolve(expr.val);
        resolve(expr.object);
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

        resolveFuncBody(stmt, FUNCTION);

        return null;
    }

    private void resolveFuncBody(Stmt.FuncDecl stmt, FUNCTION_TYPE type) {
        enterScope();
        for(Token pa:stmt.parameters) {
            declare(pa);
            define(pa);
        }
        FUNCTION_TYPE previous = curFunctionType;
        curFunctionType = type;
        // body and the argument list are in the same scope!
        resolve(stmt.body.stmts);
        curFunctionType = previous;
        exitScope();
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
        resolve(expr.thenBranch);
        if(expr.elseBranch != null) resolve(expr.elseBranch);
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
        if(curFunctionType == NONE) {
            Lox.error(expr.semicolon,
                    "Can not return when not in a method or function.");
        }
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

        if(expr.superCls != null) {
            if(expr.name.lexeme_.equals(expr.superCls.var.lexeme_)) {
                Lox.error(expr.superCls.var, " Can not inherit itself");
            }
            resolve(expr.superCls);
        }

        enterScope();
        scopes_.peek().put("this", READ);
        scopes_.peek().put("super", READ);
//        for(Stmt.FuncDecl funcDecl: expr.methods) {
//            declare(funcDecl.name);
//            define(funcDecl.name);
//        }
        for(Stmt.FuncDecl funcDecl: expr.methods) {
            resolveFuncBody(funcDecl, METHOD);
        }
        exitScope();
        return null;
    }
}
