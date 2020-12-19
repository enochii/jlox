package toy.jlox;

import java.util.List;
import java.util.Map;

/**
 * @author : SCH001
 * @description :
 */
public class LoxClass implements LoxCallable {
    final String name;
    final Map<String, LoxFunction> methods;

    LoxClass(String name, Map<String, LoxFunction> methods) {
        this.name = name;
        this.methods = methods;
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
