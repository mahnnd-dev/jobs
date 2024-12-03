package dev.m.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.springframework.stereotype.Component;

import java.io.File;

@Slf4j
@Component
public class LyricsChecker {
    public boolean hasLyrics(String filePath) {
        try {
            AudioFile audioFile = AudioFileIO.read(new File(filePath));
            Tag tag = audioFile.getTag();
            if (tag != null) {
                System.out.println("Title: " + tag.getFirst(FieldKey.TITLE));
                System.out.println("Artist: " + tag.getFirst(FieldKey.ARTIST));
                System.out.println("Album: " + tag.getFirst(FieldKey.ALBUM));
                System.out.println("Year: " + tag.getFirst(FieldKey.YEAR));
                System.out.println("Genre: " + tag.getFirst(FieldKey.GENRE));
                System.out.println("Lyrics: " + tag.getFirst(FieldKey.LYRICS));
                return tag.getFirst(FieldKey.LYRICS).length() > 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}

