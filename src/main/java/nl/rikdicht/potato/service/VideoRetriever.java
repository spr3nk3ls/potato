package nl.rikdicht.potato.service;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

@Service
@RequiredArgsConstructor
public class VideoRetriever {

    private final VideoCreator videoCreator;

    private List<String> filenames = new ArrayList<>();

    private static final long MAX_AGE = 20*1000;

    public InputStream getRandomFile() throws IOException {
        List<String> tempFilenames = new ArrayList<>(filenames);
        if(tempFilenames.isEmpty()){
            createNewVideo();
        }
        Collections.shuffle(tempFilenames);
        return new FileInputStream(new File("target/" + tempFilenames.get(0) + ".mp4"));
    }

    @Scheduled(fixedRate = 10000)
    void createNewVideo() throws IOException {
        String filename = UUID.randomUUID().toString();
        videoCreator.createVideo(filename);
        filenames.add(filename);
    }

    @Scheduled(fixedRate = 10000)
    void cleanup() throws Exception {
        long nowInMillis = Calendar.getInstance().getTimeInMillis();
        Iterator<String> filenameIterator = filenames.iterator();
        while(filenameIterator.hasNext()){
            String filename = filenameIterator.next();
            File file = new File("target/" + filename + ".mp4");
            if (nowInMillis - file.lastModified() > MAX_AGE) {
                filenameIterator.remove();
                final boolean delete = file.delete();
                if(!delete){
                    throw new Exception("Unable to delete file");
                }
            }
        }
    }
}
