// this file is generated automatically by running src/toy/tool/generate_ast.py

package toy.jlox;
import java.util.List;


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
    
    static class Logical extends Expr {
        Logical(Expr left, Token op, Expr right)        {
            this.left = left;
            this.op = op;
            this.right = right;
        }
        
        @Override
        <R> R accept(Visitor<R> v) {
            return v.visitLogical(this);
        }
        
        final Expr left;
        final Token op;
        final Expr right;
    }
    
    static class Call extends Expr {
        Call(Expr callee, List<Expr> args, Token token)        {
            this.callee = callee;
            this.args = args;
            this.token = token;
        }
        
        @Override
        <R> R accept(Visitor<R> v) {
            return v.visitCall(this);
        }
        
        final Expr callee;
        final List<Expr> args;
        final Token token;
    }
    
    static class Get extends Expr {
        Get(Expr object, Token field)        {
            this.object = object;
            this.field = field;
        }
        
        @Override
        <R> R accept(Visitor<R> v) {
            return v.visitGet(this);
        }
        
        final Expr object;
        final Token field;
    }
    
    static class Set extends Expr {
        Set(Expr object, Token field, Expr val)        {
            this.object = object;
            this.field = field;
            this.val = val;
        }
        
        @Override
        <R> R accept(Visitor<R> v) {
            return v.visitSet(this);
        }
        
        final Expr object;
        final Token field;
        final Expr val;
    }
    
    public interface Visitor<R> {
        R visitAssign(Assign expr);
        R visitBinary(Binary expr);
        R visitUnary(Unary expr);
        R visitGrouping(Grouping expr);
        R visitLiteral(Literal expr);
        R visitVariable(Variable expr);
        R visitLogical(Logical expr);
        R visitCall(Call expr);
        R visitGet(Get expr);
        R visitSet(Set expr);
    }
}
