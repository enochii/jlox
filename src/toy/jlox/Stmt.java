// this file is generated automatically by running src/toy/tool/generate_ast.py

package toy.jlox;
import java.util.List;


abstract class Stmt {
    abstract <R> R accept(Visitor<R> v);
    
    static class ExprStmt extends Stmt {
        ExprStmt(Expr expr)        {
            this.expr = expr;
        }
        
        @Override
        <R> R accept(Visitor<R> v) {
            return v.visitExprStmt(this);
        }
        
        final Expr expr;
    }
    
    static class PrintStmt extends Stmt {
        PrintStmt(boolean newline, Expr expr)        {
            this.newline = newline;
            this.expr = expr;
        }
        
        @Override
        <R> R accept(Visitor<R> v) {
            return v.visitPrintStmt(this);
        }
        
        final boolean newline;
        final Expr expr;
    }
    
    static class VarDecl extends Stmt {
        VarDecl(Token name, Expr expr)        {
            this.name = name;
            this.expr = expr;
        }
        
        @Override
        <R> R accept(Visitor<R> v) {
            return v.visitVarDecl(this);
        }
        
        final Token name;
        final Expr expr;
    }
    
    static class FuncDecl extends Stmt {
        FuncDecl(Token name, List<Token> parameters, Block body)        {
            this.name = name;
            this.parameters = parameters;
            this.body = body;
        }
        
        @Override
        <R> R accept(Visitor<R> v) {
            return v.visitFuncDecl(this);
        }
        
        final Token name;
        final List<Token> parameters;
        final Block body;
    }
    
    static class Block extends Stmt {
        Block(List<Stmt> stmts)        {
            this.stmts = stmts;
        }
        
        @Override
        <R> R accept(Visitor<R> v) {
            return v.visitBlock(this);
        }
        
        final List<Stmt> stmts;
    }
    
    static class IfStmt extends Stmt {
        IfStmt(Expr cond, Stmt thenBranch, Stmt elseBranch)        {
            this.cond = cond;
            this.thenBranch = thenBranch;
            this.elseBranch = elseBranch;
        }
        
        @Override
        <R> R accept(Visitor<R> v) {
            return v.visitIfStmt(this);
        }
        
        final Expr cond;
        final Stmt thenBranch;
        final Stmt elseBranch;
    }
    
    static class WhileStmt extends Stmt {
        WhileStmt(Expr cond, Stmt body)        {
            this.cond = cond;
            this.body = body;
        }
        
        @Override
        <R> R accept(Visitor<R> v) {
            return v.visitWhileStmt(this);
        }
        
        final Expr cond;
        final Stmt body;
    }
    
    static class BreakStmt extends Stmt {
        BreakStmt(Token semicolon)        {
            this.semicolon = semicolon;
        }
        
        @Override
        <R> R accept(Visitor<R> v) {
            return v.visitBreakStmt(this);
        }
        
        final Token semicolon;
    }
    
    static class ContinueStmt extends Stmt {
        ContinueStmt(Token semicolon)        {
            this.semicolon = semicolon;
        }
        
        @Override
        <R> R accept(Visitor<R> v) {
            return v.visitContinueStmt(this);
        }
        
        final Token semicolon;
    }
    
    static class ReturnStmt extends Stmt {
        ReturnStmt(Expr expr, Token semicolon)        {
            this.expr = expr;
            this.semicolon = semicolon;
        }
        
        @Override
        <R> R accept(Visitor<R> v) {
            return v.visitReturnStmt(this);
        }
        
        final Expr expr;
        final Token semicolon;
    }
    
    public interface Visitor<R> {
        R visitExprStmt(ExprStmt expr);
        R visitPrintStmt(PrintStmt expr);
        R visitVarDecl(VarDecl expr);
        R visitFuncDecl(FuncDecl expr);
        R visitBlock(Block expr);
        R visitIfStmt(IfStmt expr);
        R visitWhileStmt(WhileStmt expr);
        R visitBreakStmt(BreakStmt expr);
        R visitContinueStmt(ContinueStmt expr);
        R visitReturnStmt(ReturnStmt expr);
    }
}
