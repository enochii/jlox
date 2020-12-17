package toy.jlox;

import java.util.List;

/**
 * @author : SCH001
 * @description :
 */
// user defined function wrapper
public class LoxFunction implements LoxCallable {
    final Stmt.FuncDecl function;
    final Environment enclosing;
    LoxFunction(Stmt.FuncDecl FuncDecl, Environment enclosing) {
        this.function = FuncDecl;
        this.enclosing = enclosing;
    }

    @Override
    public Object call(Interpreter interpreter, List<Object> args) {
        // change .globals_ -> env_! we get a closure!? [X] No
        // instead we get a dynamic scope...
        // if we want a static scope with closure, we need to bind the env
        // at the function-declaration point as the enclosing env here!
        Environment funcEnv = new Environment(enclosing);

        for(int i=0; i<args.size(); i++) {
            funcEnv.define(
                    function.parameters.get(i).lexeme_,
                    args.get(i)
            );
        }
        try {
            interpreter.executeBlock(function.body, funcEnv);
        } catch (Interpreter.ReturnException e) {
            return e.retVal;
        }
        return null;
    }

    @Override
    public int arity() {
        return function.parameters.size();
    }

    @Override
    public String toString() {
        return "<fn " + function.name.lexeme_ + ">";
    }
}
