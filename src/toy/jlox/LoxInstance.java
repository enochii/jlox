package toy.jlox;

import com.sun.corba.se.impl.oa.toa.TOA;

import java.util.ArrayList;
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
    LoxInstance parent = null;

    LoxInstance(Interpreter interpreter, LoxClass loxClass) {
        if(loxClass.superCls != null) {
            this.parent = (LoxInstance)
                    loxClass.superCls.call(interpreter, new ArrayList<>());
        }
        this.klass = loxClass;
        this.fields = new HashMap<>();
        this.fields.put("this", this); // patch
    }

    Object get(Token field) {
        String name = field.lexeme_;
        if(fields.containsKey(name)) {
            return fields.get(name);
        } else {
            LoxFunction method = klass.lookupMethod(field);
            if(method != null) {
                LoxFunction boundMethod = null;
                if(instEnv == null) {
                    boundMethod = method.bind(this);
                    instEnv = boundMethod.closure;
                    instEnv.define("super", this.parent);
                } else {
                    // make instEnv persistent, so that every method
                    // can share an environment
                    boundMethod = method.bindEnv(instEnv);
                }
                return boundMethod;
            }
        }

        if(parent != null) return parent.get(field);
        throw new Interpreter.RuntimeError(field,
                "No such field or method " + field.lexeme_);
    }

    void set(Token field, Object val) {
        String fieldName = field.lexeme_;
        LoxInstance curIns = this;
        do {
            if(curIns.fields.containsKey(fieldName)) {
                curIns.fields.put(fieldName, val);
                return;
            }
            curIns = this.parent;
        } while (curIns != null);
        fields.put(field.lexeme_, val);
    }

    @Override
    public String toString() {
        return "<Instance of Class"  + klass.name + ">";
    }

    static String prefix = "";
    static private void addTab() {
        prefix += "  ";
    }
    static private void rmTab() {
        prefix = prefix.substring(0, prefix.length()-2);
    }

    public void dumpMe() {
        System.out.println(prefix+"{");
        addTab();
        for (Map.Entry<String, Object> entry:fields.entrySet()) {
            System.out.println(prefix + entry.getKey() +
                    ": " + entry.getValue()
                    );
        }
        if(parent != null) {
            parent.dumpMe();
        }
        rmTab();
        System.out.println(prefix+"}");
    }
}
