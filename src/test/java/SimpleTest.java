import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import java.net.InetAddress;
import java.net.InetSocketAddress;

public class SimpleTest {

    @Test
    public void hexTest(){
        System.out.println(Long.toHexString(0x100000000L+0xcafebabe));
    }

    @Test
    public void prepareContactPoints() throws Exception{
        String contactPoints="10.31.22.22,125.11.22.33,11.23.12.12";
        String[] contactPointArr = StringUtils.split(contactPoints,",");
        //校验长度
       // Validate.notThan();
        InetSocketAddress[] inetSocketAddress =new InetSocketAddress[contactPointArr.length];
        InetAddress inetAddresses = InetAddress.getByName(contactPointArr[0]);
        System.out.println(inetAddresses.toString());
        for(String contactPoint : contactPointArr){
           // InetAddress[] inetAddresses = InetAddress.getAllByName(contactPointArr);

        }
    }
}
