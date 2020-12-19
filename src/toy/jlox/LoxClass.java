package toy.jlox;

import javafx.util.Pair;

import java.util.List;
import java.util.Map;

/**
 * @author : SCH001
 * @description :
 */
public class LoxClass implements LoxCallable {
    final String name;
    final Map<String, LoxFunction> methods;
    final Environment clsEnv;

    LoxClass(String name, Map<String, LoxFunction> methods, Environment outside) {
        this.name = name;
        this.methods = methods;
        clsEnv = new Environment(outside);
        for(Map.Entry<String, LoxFunction> entry: methods.entrySet()) {
            clsEnv.define(entry.getKey(), entry.getValue());
        }
    }

    @Override
    public String toString() {
        return "<Class " + name + ">";
    }

    LoxFunction lookupMethod(Token name) {
        // maybe you can through this method to find THIS !
        return methods.get(name.lexeme_);
    }

    @Override
    public Object call(Interpreter interpreter, List<Object> args) {
        return new LoxInstance(this);
    }


    @Override
    public int arity() {
        return 0;
    }
}
