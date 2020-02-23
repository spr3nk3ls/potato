package nl.rikdicht.potato.service;

import lombok.Data;
import org.mp4parser.Container;
import org.mp4parser.muxer.Movie;
import org.mp4parser.muxer.Track;
import org.mp4parser.muxer.builder.DefaultMp4Builder;
import org.mp4parser.muxer.container.mp4.MovieCreator;
import org.mp4parser.muxer.tracks.AppendTrack;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class VideoCreator {

    private static final List<String> mapping = Arrays.asList("a", "b", "c", "d", "e");

    private static final List<String> ordering = Arrays.asList("a", "b", "c", "d", "b", "c", "d", "b", "c", "e", "c");

    void newVideo() throws IOException {
        Movie movie = getMovie();
        FileChannel fc = new FileOutputStream(new File("target/output.mp4")).getChannel();
        Container mp4file = new DefaultMp4Builder().build(movie);
        mp4file.writeContainer(fc);
        fc.close();
    }

    Movie getMovie(){
        List<AudioVideoTrack> tracks = getRandomizedMovies().stream()
                .map(this::splitAudioVideo)
                .collect(Collectors.toList());
        AppendTrack audio = getAppendTrack(tracks.stream().map(AudioVideoTrack::getAudioTrack).collect(Collectors.toList()));
        AppendTrack video = getAppendTrack(tracks.stream().map(AudioVideoTrack::getVideoTrack).collect(Collectors.toList()));
        Movie movie = new Movie();
        movie.addTrack(audio);
        movie.addTrack(video);
        return movie;
    }

    private List<Movie> getRandomizedMovies() {
        return getOrderedFiles().stream().map(this::getMovieForVideoFile).collect(Collectors.toList());
    }

    private Movie getMovieForVideoFile(File file) {
        try {
            return MovieCreator.build(file.getPath());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private AppendTrack getAppendTrack(List<Track> tracks){
        try {
            return new AppendTrack(tracks.toArray(new Track[0]));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private AudioVideoTrack splitAudioVideo(Movie movie){
        AudioVideoTrack audioVideoTrack = new AudioVideoTrack();
        for(Track track : movie.getTracks()){
            if(track.getHandler().equals("soun")){
                audioVideoTrack.setAudioTrack(track);
            }
            if(track.getHandler().equals("vide")){
                audioVideoTrack.setVideoTrack(track);
            }
        }
        return audioVideoTrack;
    }

    List<File> getOrderedFiles(){
        List<File> orderedFiles = new ArrayList<>();
        Map<String, List<File>> mappedFiles = getRandomizedMappedFiles();
        for(String order : ordering){
            List<File> files = mappedFiles.get(order);
            if(!files.isEmpty())
            orderedFiles.add(files.remove(0));
        }
        return orderedFiles;
    }

    private Map<String, List<File>> getRandomizedMappedFiles(){
        Map<String, List<File>> map = new HashMap<>();
        for(String key : mapping){
            List<File> files = getResourceFolderFiles("-" + key + "*");
            Collections.shuffle(files);
            map.put(key, files);
        }
        return map;
    }

    private List<File> getResourceFolderFiles(String pattern) {
        ClassLoader cl = this.getClass().getClassLoader();
        ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver(cl);
        Resource[] resources;
        try {
            resources = resolver.getResources("classpath*:/*" + pattern + ".mp4");
        } catch (IOException e) {
            throw new RuntimeException(e); //FIXME
        }
        return Arrays.stream(resources).map(resource -> {
            try {
                return resource.getFile();
            } catch (IOException e) {
                throw new RuntimeException(e); //FIXME
            }
        }).collect(Collectors.toList());
    }

    @Data
    static class AudioVideoTrack {
        private Track audioTrack;
        private Track videoTrack;
    }
}
