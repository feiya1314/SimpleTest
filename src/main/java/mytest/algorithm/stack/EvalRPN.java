package mytest.algorithm.stack;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * https://leetcode.cn/problems/evaluate-reverse-polish-notation/description/?envType=study-plan-v2&envId=top-interview-150
 */
public class EvalRPN {
    public int evalRPN(String[] tokens) {
        Deque<String> stack = new ArrayDeque<String>();
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
        throw new IllegalStateException();
    }
}
