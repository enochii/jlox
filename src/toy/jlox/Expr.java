// this file is generated automatically by running src/toy/tool/generate_ast.py

package toy.jlox;


abstract class Expr {
    abstract <R> R accept(Visitor<R> v);
    
    static class Binary extends Expr {
        Binary(Expr left, Token op, Expr right)        {
            this.left = left;
            this.op = op;
            this.right = right;
        }
        
        @Override
        <R> R accept(Visitor<R> v) {
            return v.visitBinary(this);
        }
        
        final Expr left;
        final Token op;
        final Expr right;
    }
    
    static class Unary extends Expr {
        Unary(Token op, Expr expr)        {
            this.op = op;
            this.expr = expr;
        }
        
        @Override
        <R> R accept(Visitor<R> v) {
            return v.visitUnary(this);
        }
        
        final Token op;
        final Expr expr;
    }
    
    static class Grouping extends Expr {
        Grouping(Expr expr)        {
            this.expr = expr;
        }
        
        @Override
        <R> R accept(Visitor<R> v) {
            return v.visitGrouping(this);
        }
        
        final Expr expr;
    }
    
    static class Literal extends Expr {
        Literal(Object val)        {
            this.val = val;
        }
        
        @Override
        <R> R accept(Visitor<R> v) {
            return v.visitLiteral(this);
        }
        
        final Object val;
    }
    
}