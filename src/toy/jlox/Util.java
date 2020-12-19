package toy.jlox;

/**
 * @author : SCH001
 * @description :
 */
public interface Util {
    static String mangle(String name, int num) {
        return name + "_" + num;
    }
}
