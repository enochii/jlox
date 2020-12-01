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
        PrintStmt(Expr expr)        {
            this.expr = expr;
        }
        
        @Override
        <R> R accept(Visitor<R> v) {
            return v.visitPrintStmt(this);
        }
        
        final Expr expr;
    }
    
    static class DefinitionStmt extends Stmt {
        DefinitionStmt(String name, Expr expr)        {
            this.name = name;
            this.expr = expr;
        }
        
        @Override
        <R> R accept(Visitor<R> v) {
            return v.visitDefinitionStmt(this);
        }
        
        final String name;
        final Expr expr;
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
    
    public interface Visitor<R> {
        R visitExprStmt(ExprStmt expr);
        R visitPrintStmt(PrintStmt expr);
        R visitDefinitionStmt(DefinitionStmt expr);
        R visitBlock(Block expr);
        R visitIfStmt(IfStmt expr);
        R visitWhileStmt(WhileStmt expr);
    }
}
