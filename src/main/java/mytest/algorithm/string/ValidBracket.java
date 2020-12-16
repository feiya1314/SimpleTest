package mytest.algorithm.string;

import com.alibaba.druid.sql.visitor.functions.Char;

import java.util.Deque;
import java.util.LinkedList;

/**
 * 给定一个只包括 '('，')'，'{'，'}'，'['，']' 的字符串，判断字符串是否有效。
 *
 * 有效字符串需满足：
 *
 * 左括号必须用相同类型的右括号闭合。
 * 左括号必须以正确的顺序闭合。
 * 注意空字符串可被认为是有效字符串。
 *
 * 示例 1:
 *
 * 输入: "()"
 * 输出: true
 * 示例 2:
 *
 * 输入: "()[]{}"
 * 输出: true
 * 示例 3:
 *
 * 输入: "(]"
 * 输出: false
 * 示例 4:
 *
 * 输入: "([)]"
 * 输出: false
 * 示例 5:
 *
 * 输入: "{[]}"
 * 输出: true
 *
 * 来源：力扣（LeetCode）
 * 链接：https://leetcode-cn.com/problems/valid-parentheses
 * 著作权归领扣网络所有。商业转载请联系官方授权，非商业转载请注明出处。
 */
public class ValidBracket {
    public static void main(String[] args) {

    }

    public boolean isValid(String s) {
        Deque<Character> stack = new LinkedList<>();
        for (Character character : s.toCharArray()){
            if (character== ' '){
                continue;
            }
            if (character=='(' || character=='['|| character == '{'){
                stack.push(character);
                continue;
            }
            if (character == '}' ||character == ')'||character == ']' ){
                // 栈为空的时候说明没有对应的左括号
                if(stack.isEmpty() || !isMatch(stack.pop(),character)){
                  return false;
                }
            }
        }
        // 如果最后还有剩余，说明没有对应的右括号
        return stack.isEmpty();
    }

    private boolean isMatch(Character left,Character right){
        if (left == '{' ){
            return right == '}';
        }

        if (left == '[' ){
            return right == ']';
        }

        if (left == '(' ){
            return right == ')';
        }

        return false;
    }
}
