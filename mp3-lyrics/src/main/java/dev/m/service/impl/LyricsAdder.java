package dev.m.service.impl;

import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.springframework.stereotype.Component;

import java.io.File;
@Component
public class LyricsAdder {


    public  void addLyrics(String filePath, String lyrics) {
        try {
            AudioFile audioFile = AudioFileIO.read(new File(filePath));
            Tag tag = audioFile.getTagOrCreateAndSetDefault();
            tag.setField(FieldKey.LYRICS, lyrics);
            audioFile.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

