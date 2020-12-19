package toy.jlox;

import java.util.HashMap;
import java.util.Map;

/**
 * @author : SCH001
 * @description :
 */
public class Environment {
    final Map<String, Object> bindings_ = new HashMap<>();
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

    public Object getAt(int dist, Token token) {
        return ancestor(dist).bindings_.get(token.lexeme_);
    }

    public void assignAt(int dist, Token token, Object val) {
        Environment env = ancestor(dist);
        String name = token.lexeme_;
        if(!env.bindings_.containsKey(name)) {
            throw new Interpreter.RuntimeError(token,
                    "No such variable "+name);
        }
        env.bindings_.put(token.lexeme_, val);
    }

    Environment ancestor(int dist) {
        Environment environment = this;
        for(int i=0; i<dist; i++) {
            environment = environment.enclosing_;
        }
        return environment;
    }
}
