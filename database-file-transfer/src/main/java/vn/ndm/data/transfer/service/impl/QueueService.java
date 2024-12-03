package vn.ndm.data.transfer.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import vn.ndm.data.transfer.obj.FileInfo;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Stream;

@Slf4j
@Service
public class QueueService {

    private final BlockingQueue<String> blockingQueueRead;
    private List<String> list = new ArrayList<>();
    private final ConcurrentMap<String, FileInfo> mapFile;
    private final AtomicBoolean running = new AtomicBoolean(true);
    private final ThreadPoolTaskExecutor executor;

    @Autowired
    public QueueService(@Qualifier("queue-read") BlockingQueue<String> blockingQueueRead, @Qualifier("map-file") ConcurrentMap<String, FileInfo> mapFile, ThreadPoolTaskExecutor executor) {
        this.blockingQueueRead = blockingQueueRead;
        this.mapFile = mapFile;
        this.executor = executor;
    }

    @Scheduled(fixedDelayString = "60000000")
    public void run() {
        try {
            while (running.get()) {
                for (Map.Entry<String, FileInfo> entry : mapFile.entrySet()) {
                    System.out.println(entry.getKey() + "/" + entry.getValue());
                    FileInfo fileInfo = entry.getValue();
                    readToQueue(fileInfo.getFileChildren());
                   log.info("#Delete file: {}",mapFile.remove(entry.getKey()));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void readToQueue(List<File> paths) {
        for (File path : paths)
            executor.execute(() -> {
                readFileJava8(path.toPath());
            });
    }

    public void readFileJava8(Path filePath) {
        Instant start = Instant.now();
        try (Stream<String> lines = Files.lines(filePath, StandardCharsets.UTF_8)) {
            lines.forEach(list::add);
            Files.deleteIfExists(filePath);
            Instant end = Instant.now();
            long durationInMilliseconds = Duration.between(start, end).toMillis();
            log.info("#Reead file succ file: {}, queue size: {}, {} ms", filePath.getFileName(), list.size(), durationInMilliseconds);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
