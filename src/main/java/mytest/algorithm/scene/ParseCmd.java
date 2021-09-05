package mytest.algorithm.scene;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * 输入 i n 则 第n行前面插入
 * a n 第n行后面插入
 * r n 替换第n行
 * d n 删除第n行
 *
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
        String[] contentOri = new String[count];
        List<String> content = new ArrayList<>();
        int[] opIndex = new int[count];
        String[] op = new String[count];

        for (int i = 0; i < count; i++) {
            String[] parse = lines[i].split(" ");
            opIndex[i] = Integer.parseInt(parse[0]);
            op[i] = parse[1];
            int opLen = parse[1].length() + 3;
            if (parse.length > 2) {
                String line = lines[i].substring(opLen);
                contentOri[i] = line;
                //content.add(line);
            }
        }
        if (opIndex[0] != 1 || !op[0].equals("i")) {
            System.out.println("error");
            return;
        }

        content.add(contentOri[0]);

        for (int i = 1; i < count; i++) {
            int index = opIndex[i];
            if (index > i + 1 || index > content.size()) {
                System.out.println("error");
                return;
            }

            if (op[i].equals("i")) {
                content.add(index - 1, contentOri[i]);
            }
            if (op[i].equals("a")) {
                content.add(index, contentOri[i]);
            }
            if (op[i].equals("r")) {
                content.set(index - 1, contentOri[i]);
            }
            if (op[i].equals("d")) {
                content.remove(index - 1);
            }
        }

        content.forEach(System.out::println);
    }
}
