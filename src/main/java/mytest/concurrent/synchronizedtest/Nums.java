package mytest.concurrent.synchronizedtest;

public class Nums {
    private int requestNum=0;
    private int requestNumWithSync=0;
    private int requestNumWithStaticSync=0;
    private int requestNumWithClassSync=0;

    public void requestNumInc() {
        requestNum ++;
    }

    public void requestNumWithSyncInc() {
        requestNumWithSync ++;
    }

    public void requestNumWithStaticSyncInc() {
        requestNumWithStaticSync ++;
    }

    public void requestNumWithClassSyncInc() {
        requestNumWithClassSync ++;
    }

    public void printNum(){
        System.out.println("requestNum : "+requestNum);
        System.out.println("requestNumWithSync : "+requestNumWithSync);
        System.out.println("requestNumWithStaticSync : "+requestNumWithStaticSync);
        System.out.println("requestNumWithClassSync : "+ requestNumWithClassSync);
    }
}
