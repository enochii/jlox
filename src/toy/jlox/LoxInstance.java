package toy.jlox;

/**
 * @author : SCH001
 * @description :
 */
public class LoxInstance {
    private final LoxClass klass;
    LoxInstance(LoxClass loxClass) {
        this.klass = loxClass;
    }

    @Override
    public String toString() {
        return "<Instance of Class"  + klass.name + ">";
    }
}
