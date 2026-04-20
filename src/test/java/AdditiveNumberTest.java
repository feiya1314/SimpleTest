import mytest.algorithm.string.AdditiveNumber;
import org.junit.Test;

public class AdditiveNumberTest {

    private AdditiveNumber solution = new AdditiveNumber();

    @Test
    public void testExample1() {
        String num = "112358";
        boolean result = solution.isAdditiveNumber(num);
        System.out.println("输入: " + num);
        System.out.println("输出: " + result);
        System.out.println("预期: true (1, 1, 2, 3, 5, 8)");
        System.out.println();
        assert result == true : "Test 1 failed!";
    }

    @Test
    public void testExample2() {
        String num = "199100199";
        boolean result = solution.isAdditiveNumber(num);
        System.out.println("输入: " + num);
        System.out.println("输出: " + result);
        System.out.println("预期: true (1, 99, 100, 199)");
        System.out.println();
        assert result == true : "Test 2 failed!";
    }

    @Test
    public void testFalse1() {
        String num = "99100199";
        boolean result = solution.isAdditiveNumber(num);
        System.out.println("输入: " + num);
        System.out.println("输出: " + result);
        System.out.println("预期: false");
        System.out.println();
        assert result == false : "Test false1 failed!";
    }

    @Test
    public void testLeadingZero() {
        String num = "102358";
        boolean result = solution.isAdditiveNumber(num);
        System.out.println("输入: " + num);
        System.out.println("输出: " + result);
        System.out.println("预期: false (03 不符合规则)");
        System.out.println();
        assert result == false : "Test leading zero failed!";
    }

    @Test
    public void testTwoNumbers() {
        String num = "11";
        boolean result = solution.isAdditiveNumber(num);
        System.out.println("输入: " + num);
        System.out.println("输出: " + result);
        System.out.println("预期: false (需要至少3个数)");
        System.out.println();
        assert result == false : "Test two numbers failed!";
    }

    @Test
    public void testSingleZero() {
        String num = "0";
        boolean result = solution.isAdditiveNumber(num);
        System.out.println("输入: " + num);
        System.out.println("输出: " + result);
        System.out.println("预期: false");
        System.out.println();
        assert result == false : "Test single zero failed!";
    }

    @Test
    public void testMultipleZeros() {
        String num = "000";
        boolean result = solution.isAdditiveNumber(num);
        System.out.println("输入: " + num);
        System.out.println("输出: " + result);
        System.out.println("预期: true (0, 0, 0)");
        System.out.println();
        assert result == true : "Test multiple zeros failed!";
    }

    @Test
    public void testLargeNumbers() {
        String num = "12147483647214748364721474836472147483647";
        boolean result = solution.isAdditiveNumber(num);
        System.out.println("输入: " + num);
        System.out.println("输出: " + result);
        System.out.println("预期: true");
        System.out.println();
        assert result == true : "Test large numbers failed!";
    }

    public static void main(String[] args) {
        AdditiveNumberTest test = new AdditiveNumberTest();
        
        System.out.println("========== 测试用例 ==========\n");
        
        test.testExample1();
        test.testExample2();
        test.testFalse1();
        test.testLeadingZero();
        test.testTwoNumbers();
        test.testSingleZero();
        test.testMultipleZeros();
        test.testLargeNumbers();
        
        System.out.println("========== 所有测试通过 ==========");
    }
}
