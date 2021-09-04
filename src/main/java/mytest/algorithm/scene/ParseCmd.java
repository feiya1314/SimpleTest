package mytest.algorithm.scene;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * @author : feiya
 * @date : 2021/9/4
 * @description :
 */
public class ParseCmd {

    public static void main(String[] args) throws Exception {
        out();
    }

    //1 i first|1 a sec|2 r rep
    //first
    //rep
    public static void out() throws Exception {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        String cmd = br.readLine();
        br.close();

        String[] lines = cmd.split("\\|");
        int count = lines.length;
        String[] content = new String[count];
        //List<String> content = new ArrayList<>();
        int[] opIndex = new int[count];
        String[] op = new String[count];

        for (int i = 0; i < count; i++) {
            String[] parse = lines[i].split(" ");
            opIndex[i] = Integer.parseInt(parse[0]);
            op[i] = parse[1];
            int opLen = parse[1].length() + 3;
            if (parse.length > 2) {
                content[i] = lines[i].substring(opLen);
            }
            //content.add(lines[i].substring(opLen));
        }
        if (opIndex[0] != 1 || !op[0].equals("i")) {
            System.out.println("error");
            return;
        }

        for (int i = 1; i < count; i++) {
            int index = opIndex[i];
            if (index > i + 1) {
                System.out.println("error");
                return;
            }

            if (op[i].equals("i")) {
                content[index - 1] = content[i];
                if (index - 1 != i) {
                    content[i] = null;
                }
            }
            if (op[i].equals("a")) {
                content[index] = content[i];
                if (index != i) {
                    content[i] = null;
                }
            }
            if (op[i].equals("r")) {
                content[index - 1] = content[i];
                if (index - 1 != i) {
                    content[i] = null;
                }
            }
            if (op[i].equals("d")) {
                content[index - 1] = null;
            }
        }
        for (int i = 0; i < count; i++) {
            if (content[i] != null) {
                System.out.println(content[i]);
            }
        }
    }
}
