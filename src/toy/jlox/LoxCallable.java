package toy.jlox;

import java.util.List;

/**
 * @author : SCH001
 * @description :
 */

// predefined callable object can be added into env
// as lib functions
public interface LoxCallable {
    // interpreter will provide the environment needed by
    // evaluating the body
    Object call(Interpreter interpreter, List<Object> args);
    int arity();
}
