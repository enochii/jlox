package toy.jlox;

import com.sun.corba.se.impl.oa.toa.TOA;

import java.util.HashMap;
import java.util.Map;

/**
 * @author : SCH001
 * @description :
 */
public class LoxInstance {
    // pointer to class
    private final LoxClass klass;
    final Map<String, Object> fields;
    private Environment instEnv = null;

    LoxInstance(LoxClass loxClass) {
        this.klass = loxClass;
        this.fields = new HashMap<>();
    }

    void setInstEnv(Environment env) {
        this.instEnv = env;
    }

    Object lookupMethod(Token name) {
        // maybe you can through this method to find THIS !
        return instEnv.getAt(0, name);
    }

    Object get(Token field) {
        if(fields.containsKey(field.lexeme_)) {
            return fields.get(field.lexeme_);
        } else {
            Object method = lookupMethod(field);
            if(method != null) return method;
        }

        throw new Interpreter.RuntimeError(field,
                "No such field or method" + field.lexeme_);
    }

    void set(Token field, Object val) {
        fields.put(field.lexeme_, val);
    }

    @Override
    public String toString() {
        return "<Instance of Class"  + klass.name + ">";
    }
}
