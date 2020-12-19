package toy.jlox;

import java.util.List;

/**
 * @author : SCH001
 * @description :
 */
// user defined function wrapper
public class LoxFunction implements LoxCallable {
    final Stmt.FuncDecl funcDecl;
    final Environment closure;
    LoxFunction(Stmt.FuncDecl funcDecl, Environment closure) {
        this.funcDecl = funcDecl;
        this.closure = closure;
    }

    @Override
    public Object call(Interpreter interpreter, List<Object> args) {
        // change .globals_ -> env_! we get a closure!? [X] No
        // instead we get a dynamic scope...
        // if we want a static scope with closure, we need to bind the env
        // at the function-declaration point as the enclosing env here!
        Environment funcEnv = new Environment(closure);

        for(int i=0; i<args.size(); i++) {
            funcEnv.define(
                    funcDecl.parameters.get(i).lexeme_,
                    args.get(i)
            );
        }
        try {
            interpreter.executeBlock(funcDecl.body, funcEnv);
        } catch (Interpreter.ReturnException e) {
            return e.retVal;
        }
        return null;
    }

//     bind "this" with an instance to the function
    public LoxFunction bind(LoxInstance instance) {
        Environment bindEnv = new Environment(closure);
        bindEnv.define("this", instance);
        return new LoxFunction(funcDecl, bindEnv);
    }

    public LoxFunction bindEnv(Environment environment) {
        return new LoxFunction(funcDecl, environment);
    }

    @Override
    public int arity() {
        return funcDecl.parameters.size();
    }

    @Override
    public String toString() {
        return "<fn " + funcDecl.name.lexeme_ + ">";
    }
}
