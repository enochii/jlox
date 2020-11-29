// this file is generated automatically by running src/toy/tool/generate_ast.py

package toy.jlox;


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
    
    public interface Visitor<R> {
        R visitExprStmt(ExprStmt expr);
        R visitPrintStmt(PrintStmt expr);
        R visitDefinitionStmt(DefinitionStmt expr);
    }
}
