package dev.m.service.impl;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

@Component
public class LyricsFetcher {

    public String getLyrics(String artist, String title) {
        String apiUrl = String.format("https://api.lyrics.ovh/v1/%s/%s", artist, title);
        System.out.println(apiUrl);
        StringBuilder response = new StringBuilder();

        try {
            URL url = new URL(apiUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();
                // Here, we're assuming the response is in JSON format with a field "lyrics".
                String jsonResponse = response.toString();
                int lyricsStart = jsonResponse.indexOf("\"lyrics\":\"") + 10;
                int lyricsEnd = jsonResponse.indexOf("\",", lyricsStart);
                if (lyricsStart >= 0 && lyricsEnd > lyricsStart) {
                    return jsonResponse.substring(lyricsStart, lyricsEnd).replace("\\n", "\n");
                }
            } else {
                return "Lyrics not found or there was an error with the request.";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "An error occurred.";
        }
        return "Lyrics not found.";
    }

    public String readFileJava8(Path filePath) {
        StringBuilder builder = new StringBuilder();
        try (Stream<String> lines = Files.lines(filePath, StandardCharsets.UTF_8)) {
            lines.forEach(line -> {
                builder.append(line).append("\n");
            });
            return builder.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }
}