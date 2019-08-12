package mytest.interrupt;

import com.google.common.util.concurrent.Uninterruptibles;

import java.util.concurrent.TimeUnit;

public class InterruptTest {
    public static void main(String[] args) {

        try {
            System.out.println(new BlockTask().getResource(0));
        }catch (InterruptedException e)
        {
            System.out.println("error interruptexecption");
        }

        System.out.println("-------------------2---------------------");
        try {
            System.out.println(new BlockTask().getResource(1));
        }catch (InterruptedException e)
        {
            System.out.println("error interruptexecption");
        }
    }
}
