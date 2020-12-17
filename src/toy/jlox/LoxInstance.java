package toy.jlox;

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
    LoxInstance(LoxClass loxClass) {
        this.klass = loxClass;
        this.fields = new HashMap<>();
    }

    Object get(Token field) {
        if(!fields.containsKey(field.lexeme_)) {
            throw new Interpreter.RuntimeError(field,
                    "No such field " + field.lexeme_);
        }
        return fields.get(field.lexeme_);
    }

    void set(Token field, Object val) {
        fields.put(field.lexeme_, val);
    }

    @Override
    public String toString() {
        return "<Instance of Class"  + klass.name + ">";
    }
}
