package toy.tool;

import java.io.*;

public interface FileUtils {
    static String readFile(String filename) {
        BufferedReader in = null;
        StringBuilder stringBuilder = null;
        try {
            in = new BufferedReader(new FileReader(filename));

            stringBuilder = new StringBuilder();
            String line = null;
            while ((line = in.readLine()) != null) {
                stringBuilder.append(line).append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stringBuilder.toString();
//        return "print 1* 2+3*-3; \n " +
//                "print 1;";
    }
}
