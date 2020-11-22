package toy.jlox;


abstract class Expr {
    static class Binary extends Expr {
        Binary( Expr left, Token op, Expr right)        {
            this.left = left;
            this.op = op;
            this.right = right;
        }
        
        final Expr left;
        final Token op;
        final Expr right;
    }
    
    static class Unary extends Expr {
        Unary( Expr expr, Token op)        {
            this.expr = expr;
            this.op = op;
        }
        
        final Expr expr;
        final Token op;
    }
    
    static class Grouping extends Expr {
        Grouping( Expr expr)        {
            this.expr = expr;
        }
        
        final Expr expr;
    }
    
    static class Literal extends Expr {
        Literal( Object val)        {
            this.val = val;
        }
        
        final Object val;
    }
    
}
