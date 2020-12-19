package toy.jlox;

import java.util.List;
import java.util.Map;

/**
 * @author : SCH001
 * @description :
 */
public class LoxClass implements LoxCallable {
    static int argNum = -1;
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

    LoxFunction lookupMethod(String name) {
        // maybe you can through this method to find THIS !
        if(argNum < 0) {
            throw new RuntimeException("invalid mangling");
        }
        name = Util.mangle(name, argNum);
        argNum = -1;
        return methods.get(name);
    }

    @Override
    public Object call(Interpreter interpreter, List<Object> args) {
        // allocate object
        LoxInstance instance = new LoxInstance(this);

//        LoxFunction ctor = lookupMethod("init");
//        if(ctor != null) {
//            // initialize
//            ctor.bind(instance).call(interpreter, args);
//        }
        return instance;
    }


    @Override
    public int arity() {
        return 0;
    }
}
