package nl.rikdicht.potato.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Stream;

import static nl.rikdicht.potato.service.VideoCreator.VIDEO_FOLDER;

@Slf4j
@Service
@RequiredArgsConstructor
public class VideoRetriever {

    private final VideoCreator videoCreator;

    private List<String> filenames = new ArrayList<>();

    private static final long MAX_AGE = 60*1000;
    private static final long CREATE_RATE = 10*1000;

    public File getRandomFile() throws IOException {
        List<String> tempFilenames = new ArrayList<>(filenames);
        if(tempFilenames.isEmpty()){
            createNewVideo();
        }
        Collections.shuffle(tempFilenames);
        return new File(VIDEO_FOLDER + "/" + tempFilenames.get(0) + ".mp4");
    }

    @Scheduled(fixedRate = CREATE_RATE)
    void createNewVideo() throws IOException {
        String filename = UUID.randomUUID().toString();
        videoCreator.createVideo(filename);
        filenames.add(filename);
    }

    @Scheduled(fixedRate = CREATE_RATE)
    void cleanup() throws Exception {
        long nowInMillis = Calendar.getInstance().getTimeInMillis();
        try (Stream<Path> stream = Files.walk(Paths.get(VIDEO_FOLDER), 2)) {
            stream.map(Path::toFile)
                  .filter(file -> file.getName().endsWith(".mp4"))
                  .filter(file -> nowInMillis - file.lastModified() > MAX_AGE)
                  .forEach(File::delete);
        }
    }
}
