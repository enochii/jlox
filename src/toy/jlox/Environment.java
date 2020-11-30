package toy.jlox;

import java.util.HashMap;
import java.util.Map;

/**
 * @author : SCH001
 * @description :
 */
public class Environment {
    private Map<String, Object> bindings_ = new HashMap<>();
    final Environment enclosing_;

    Environment() {
        this.enclosing_ = null;
    }
    Environment(Environment enclosing) {
        this.enclosing_ = enclosing;
    }

    public void define(String name, Object val) {
        bindings_.put(name, val);
    }

    public Object get(Token name) {
        if(bindings_.containsKey(name.lexeme_)) {
            return bindings_.get(name.lexeme_);
        }
        if(enclosing_ != null) {
            return enclosing_.get(name);
        }
        throw new Interpreter.RuntimeError(name,
                "No such variable "+name);
    }

    public void assign(Token name, Object val) {
        if(bindings_.containsKey(name.lexeme_)) {
            bindings_.put(name.lexeme_, val);
        }
        else if(enclosing_ != null) {
            enclosing_.assign(name, val);
        } else {
            throw new Interpreter.RuntimeError(name,
                    "No such variable "+name);
        }
    }
}
