package com.github.hcsp.multithread;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import static com.github.hcsp.multithread.MultiThreadWordCount1.countSingleFile;
import static com.github.hcsp.multithread.MultiThreadWordCount1.mergeMap;

public class MultiThreadWordCount3 {
    // 使用threadNum个线程，并发统计文件中各单词的数量

    private static CountDownLatch done;

    //  使用threadNum个线程，并发统计文件中各单词的数量
    public static Map<String, Integer> count(int threadNum, List<File> files) throws InterruptedException {
        done = new CountDownLatch(files.size());
        ExecutorService service = Executors.newFixedThreadPool(threadNum);
        Map<String, Integer> result = new HashMap<>();

        for (File file : files) {
            service.submit(() -> {
                Map<String, Integer> temp = null;
                try {
                    temp = countSingleFile(file);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                synchronized (MultiThreadWordCount2.class) {
                    assert temp != null;
                    mergeMap(result, temp);
                }
                done.countDown();
            });
        }
        done.await();
        service.shutdown();

        return result;
    }
}
