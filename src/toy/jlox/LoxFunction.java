package toy.jlox;

import java.util.List;

/**
 * @author : SCH001
 * @description :
 */
// user defined function wrapper
public class LoxFunction implements LoxCallable {
    private Stmt.FuncDecl function;
    LoxFunction(Stmt.FuncDecl FuncDecl) {
        this.function = FuncDecl;
    }

    @Override
    public Object call(Interpreter interpreter, List<Object> args) {
        // todo: change .globals_ -> env_! we get a closure!?
        Environment funcEnv = new Environment(interpreter.globals_);

//            if(arity() != args.size()) {
//                throw new RuntimeError()
//            }
        for(int i=0; i<args.size(); i++) {
            funcEnv.define(
                    function.parameters.get(i),
                    args.get(i)
            );
        }
        interpreter.executeBlock(function.body, funcEnv);
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
