package toy.jlox;

import java.util.HashMap;
import java.util.Map;

/**
 * @author : SCH001
 * @description :
 */
public class Environment {
    private Map<String, Object> bindings_ = new HashMap<>();

    public void define(String name, Object val) {
        bindings_.put(name, val);
    }

    public Object get(Token name) {
        if(!bindings_.containsKey(name.lexeme_)) {
            throw new Interpreter.RuntimeError(name, "No such variable "+name);
        }
        return bindings_.get(name.lexeme_);
    }

    public void assign(Token name, Object val) {
        if(!bindings_.containsKey(name.lexeme_)) {
            throw new Interpreter.RuntimeError(name, "No such variable "+name);
        }
        bindings_.put(name.lexeme_, val);
    }
}
