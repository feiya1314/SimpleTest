import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        RainTest.class,
        AopTest.class,
        StormTest.class
    })
public class AllTest {
}
