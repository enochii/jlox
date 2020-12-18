package toy.jlox;

import java.util.List;

/**
 * @author : SCH001
 * @description :
 */
public class LoxClass implements LoxCallable {
    final String name;
    final List<Stmt.FuncDecl> methods;

    LoxClass(String name, List<Stmt.FuncDecl> methods) {
        this.name = name;
        this.methods = methods;
    }

    @Override
    public String toString() {
        return "<Class " + name + ">";
    }

    @Override
    public Object call(Interpreter interpreter, List<Object> args) {
        LoxInstance instance = new LoxInstance(this);
        initInstanceEnv(interpreter, instance);
        return instance;
    }

    private void initInstanceEnv(Interpreter interpreter, LoxInstance instance) {
        Environment instEnv = new Environment(interpreter.env_);
        instEnv.define("this", instance);

        for(Stmt.FuncDecl funcDecl: methods) {
            LoxFunction loxFunction = new LoxFunction(funcDecl, instEnv);
            instEnv.define(funcDecl.name.lexeme_, loxFunction);
        }
        instance.setInstEnv(instEnv);
    }

    @Override
    public int arity() {
        return 0;
    }
}
