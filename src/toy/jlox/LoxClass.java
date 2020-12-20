package toy.jlox;

import java.util.List;
import java.util.Map;

/**
 * @author : SCH001
 * @description :
 */

/**
 * every class is an instance.
 * 1) an instance of a class can access the class methods
 * 2) so, we put class C 's static methods into a meta class,
 *    and then, make class C as the instance of the meta class's instance.
 *    in this way, the class C can access the static methods.
 */
public class LoxClass extends LoxInstance implements LoxCallable {
    final String name;
    final Map<String, LoxFunction> methods;
//    final Map<String, LoxFunction> clsMethods;
    final LoxClass superCls;

    LoxClass(LoxClass metaClass, String name, Map<String, LoxFunction> methods, LoxClass superCls) {
        super(null, metaClass);
        this.name = name;
        this.methods = methods;
        this.superCls = superCls;
    }

    @Override
    public String toString() {
        return "<Class " + name + ">";
    }

    LoxFunction lookupMethod(Token name) {
        return methods.get(name.lexeme_);
    }

    @Override
    public Object call(Interpreter interpreter, List<Object> args) {
        return new LoxInstance(interpreter, this);
    }


    @Override
    public int arity() {
        return 0;
    }
}
