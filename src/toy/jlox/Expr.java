// this file is generated automatically by running src/toy/tool/generate_ast.py

package toy.jlox;


abstract class Expr {
    abstract <R> R accept(Visitor<R> v);
    
    static class Assign extends Expr {
        Assign(Token name, Expr newVal)        {
            this.name = name;
            this.newVal = newVal;
        }
        
        @Override
        <R> R accept(Visitor<R> v) {
            return v.visitAssign(this);
        }
        
        final Token name;
        final Expr newVal;
    }
    
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
    
    static class Variable extends Expr {
        Variable(Token var)        {
            this.var = var;
        }
        
        @Override
        <R> R accept(Visitor<R> v) {
            return v.visitVariable(this);
        }
        
        final Token var;
    }
    
    public interface Visitor<R> {
        R visitAssign(Assign expr);
        R visitBinary(Binary expr);
        R visitUnary(Unary expr);
        R visitGrouping(Grouping expr);
        R visitLiteral(Literal expr);
        R visitVariable(Variable expr);
    }
}
