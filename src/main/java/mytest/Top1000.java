package mytest;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.math.BigDecimal;
import java.util.Comparator;
import java.util.PriorityQueue;

/**
 * @author : feiya
 * @date : 2021/8/5
 * @description :
 */
public class Top1000 {

    public static void main(String[] args) throws Exception {
        PriorityQueue<Asset> result = new Top1000().execute("G:\\SimpleTest\\src\\main\\resources\\asset", 5);
        System.out.println("result ----------------------------");
        for (Asset a : result) {
            System.out.println(a.getUid() + "  "+a.getAsset().toString());
        }
    }

    private PriorityQueue<Asset> execute(String filePath, int k) throws Exception {
        PriorityQueue<Asset> priorityQueue = new PriorityQueue<>(k, Comparator.comparing(Asset::getAsset));
        File file = new File(filePath);
        BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            parseLine(line, priorityQueue, k);
        }

        return priorityQueue;
    }

    private void parseLine(String line, PriorityQueue<Asset> priorityQueue, int size) {
        String[] strings = line.split(" ");
        String uid = strings[0];
        BigDecimal marketValue = new BigDecimal(strings[1]);
        BigDecimal cash = new BigDecimal(strings[2]);
        BigDecimal asset = marketValue.add(cash);
        System.out.println("asset " + asset.toString());
        Asset assetBean = new Asset(uid, asset);
        priorityQueue.offer(assetBean);
        if (priorityQueue.size() > size) {
            priorityQueue.poll();
        }
    }

    private class Asset {
        private String uid;
        private BigDecimal asset;

        public Asset(String uid, BigDecimal asset) {
            this.uid = uid;
            this.asset = asset;
        }

        public String getUid() {
            return uid;
        }

        public void setUid(String uid) {
            this.uid = uid;
        }

        public BigDecimal getAsset() {
            return asset;
        }

        public void setAsset(BigDecimal asset) {
            this.asset = asset;
        }
    }
}
