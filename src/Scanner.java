import java.util.ArrayList;
import java.util.List;

/**
 * @author : SCH001
 * @description :
 */
public class Scanner {
    private final List<Token> tokens = new ArrayList<>();
    private final String source;

    // pointers to characters
    int start = 0, current = 0;
    //

    public Scanner(String source) {
        this.source = source;
    }

    void test() {
        tokens.add(null);
    }
}
