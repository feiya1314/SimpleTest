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

    //1 i first|1 i sec|2 r rep
    // 1 i first|1 i sec|2 r rep|2 a thr|3 a four|4 d
    //first
    //rep
    public static void out() throws Exception {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        String cmd = br.readLine();
        br.close();

        String[] lines = cmd.split("\\|");
        int count = lines.length;
        // 每一行对应写入的字符串
        String[] contentOri = new String[count];
        // 每个命令对应的操作的行号
        int[] opIndex = new int[count];
        // 每个命令
        String[] op = new String[count];
        // 结果存放
        List<String> content = new ArrayList<>();
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
            // 当前最大有 i+1 行，例如当 i = 2时，前面最多有 2 行数据，index 能操作的行号最大为 2，当大于2时，行数肯定不够
            // 当index 操作的行数大于目前 content的长度时，也是异常的情况
            if (index > i || index > content.size()) {
                System.out.println("error");
                return;
            }

            if (op[i].equals("i")) {
                content.add(index - 1, contentOri[i]);
            } else if (op[i].equals("a")) {
                content.add(index, contentOri[i]);
            } else if (op[i].equals("r")) {
                content.set(index - 1, contentOri[i]);
            } else if (op[i].equals("d")) {
                content.remove(index - 1);
            } else {
                System.out.println("error");
            }
        }

        content.forEach(System.out::println);
    }
}
