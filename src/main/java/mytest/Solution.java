package mytest;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * @author : feiya
 * @date : 2021/3/20
 * @description :
 */
class Solution {

    public int evalRPN(String[] tokens) {
        Deque<String> stack = new ArrayDeque<>();
        for (String token : tokens) {
            if ("+".equals(token) || "-".equals(token) || "*".equals(token) || "/".equals(token)) {
                String result = doCalc(stack, token);
                stack.push(result);
                continue;
            }
            stack.push(token);
        }

        return Integer.parseInt(stack.poll());
    }

    private String doCalc(Deque<String> stack, String token) {
        String right = stack.poll();
        String left = stack.poll();
        if ("+".equals(token)) {
            long c1 = Long.parseLong(left);
            long c2 = Long.parseLong(right);
            long result = c1 + c2;
            return String.valueOf(result);
        }
        if ("-".equals(token)) {
            long c1 = Long.parseLong(left);
            long c2 = Long.parseLong(right);
            long result = c1 - c2;
            return String.valueOf(result);
        }
        if ("*".equals(token)) {
            long c1 = Long.parseLong(left);
            long c2 = Long.parseLong(right);
            long result = c1 * c2;
            return String.valueOf(result);
        }
        if ("/".equals(token)) {
            long c1 = Long.parseLong(left);
            long c2 = Long.parseLong(right);
            long result = c1 / c2;
            return String.valueOf(result);
        }
        throw new IllegalArgumentException();
    }
}
