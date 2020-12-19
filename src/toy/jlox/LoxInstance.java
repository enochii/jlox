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
        this.fields.put("this", this); // patch
    }

    Object get(Token field) {
        String name = field.lexeme_;
        if(fields.containsKey(name)) {
            return fields.get(name);
        } else {
            LoxFunction method = klass.lookupMethod(field.lexeme_);
            if(method != null) {
                LoxFunction boundMethod = null;
                if(instEnv == null) {
                    boundMethod = method.bind(this);
                    instEnv = boundMethod.closure;
                } else {
                    // make instEnv persistent, so that every method
                    // can share an environment
                    boundMethod = method.bindEnv(instEnv);
                }
                return boundMethod;
            }
        }

        throw new Interpreter.RuntimeError(field,
                "No such field or method " + field.lexeme_);
    }

    void set(Token field, Object val) {
        fields.put(field.lexeme_, val);
    }

    @Override
    public String toString() {
        return "<Instance of Class"  + klass.name + ">";
    }
}
