package mytest.algorithm.array;

public class IsValidSudoku {
    public boolean isValidSudoku(char[][] board) {
        // 记录每一行的0-9 数字出现次数
        int[][] rows = new int[9][9];
        // 记录每一列的0-9 数字出现次数
        int[][] columns = new int[9][9];
        // 记录每一小九宫格的 0-9 数字出现次数
        int[][][] parts = new int[3][3][9];
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                char value = board[i][j];
                if (value != '.') {
                    // 把值换成数组的坐标，1的坐标是 0 ，9的坐标是 8
                    int index = value - '0' - 1;
                    // 第i行中 某数出现的次数，例如第一行，如果8 出现两次，会index会重复
                    rows[i][index]++;
                    // 第j列中，某数出现的次数
                    columns[j][index]++;
                    // 某个小九宫格中index出现的次数，[0][0]  [0][1] [0][2] [1][0]等，都是属于第一个九宫格，以此类推
                    // [i/3][j/3]时，对应某个九宫格，统计九宫格中某个值出现的次数
                    parts[i / 3][j / 3][index]++;

                    //如果任意一个数字出现大于1次，则false，不合法
                    if (rows[i][index] > 1 || columns[j][index] > 1 || parts[i / 3][j / 3][index] > 1) {
                        return false;
                    }
                }
            }
        }
        return true;
    }
}
