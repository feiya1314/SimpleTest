import mytest.proxy.SportProxy;
import mytest.sport.Basketball;
import mytest.sport.Soccer;
import mytest.sport.Sport;
import org.junit.Test;

import java.lang.reflect.Proxy;

public class SportTest extends BaseTest {
    @Test
    public void testJdkProxy(){
        System.out.println("start testJdkProxy");
        Sport sport= (Sport) Proxy.newProxyInstance(Sport.class.getClassLoader(),new Class[]{Sport.class},new SportProxy(new Soccer()));
        sport.play();
        System.out.println("end testJdkProxy");
    }

    @Test
    public void testJdkProxyWithBind(){
        System.out.println("start testJdkProxyWithBind");
        SportProxy sportProxy =new SportProxy();
        Sport sport= (Sport)sportProxy.bind(new Basketball());
        sport.play();
        System.out.println("end testJdkProxyWithBind");
    }
}
