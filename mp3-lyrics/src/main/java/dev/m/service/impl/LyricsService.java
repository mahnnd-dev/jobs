package dev.m.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.nio.file.Paths;

@Service
@RequiredArgsConstructor
public class LyricsService {
    private final LyricsAdder lyricsAdder;
    private final LyricsChecker lyricsChecker;
    private final LyricsFetcher lyricsFetcher;


    @PostConstruct
    public void run() {
        String filePath = "data/Linh Hồn và Thể Xác.mp3";
        String lyric = "data/lhvtx.txt";
        String songTitle = "Linh Hồn và Thể Xác";
        String artist = "Nguyễn Hải Phong";
        if (!lyricsChecker.hasLyrics(filePath)) {
            String lyrics = lyricsFetcher.readFileJava8(Paths.get(lyric));
            System.out.println(lyrics);
            if (lyrics != null) {
                lyricsAdder.addLyrics(filePath, lyrics);
                System.out.println("Lyrics added to the file!");
            } else {
                System.out.println("Could not find lyrics.");
            }
        } else {
            System.out.println("File already has lyrics.");
        }
    }
}
