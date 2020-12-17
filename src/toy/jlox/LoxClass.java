package toy.jlox;

import java.util.List;

/**
 * @author : SCH001
 * @description :
 */
public class LoxClass implements LoxCallable {
    private final String name;
    LoxClass(String name, List<Stmt> methods) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "<Class " + name + " >";
    }

    @Override
    public Object call(Interpreter interpreter, List<Object> args) {
        System.out.print("ctor!");
        return null;
    }

    @Override
    public int arity() {
        return 0;
    }
}
