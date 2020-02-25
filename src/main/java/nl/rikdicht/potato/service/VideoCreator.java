package nl.rikdicht.potato.service;

import lombok.RequiredArgsConstructor;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.mp4parser.Container;
import org.mp4parser.muxer.Movie;
import org.mp4parser.muxer.Track;
import org.mp4parser.muxer.builder.DefaultMp4Builder;
import org.mp4parser.muxer.container.mp4.MovieCreator;
import org.mp4parser.muxer.tracks.AppendTrack;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.FileChannel;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VideoCreator {

    private final FileProvider fileProvider;

    public static final String VIDEO_FOLDER = "videos";

    void createVideo(String filename) throws IOException {
        Movie movie = getMovie();
        FileChannel fc = new FileOutputStream(new File(VIDEO_FOLDER + "/" + filename + ".mp4")).getChannel();
        Container mp4file = new DefaultMp4Builder().build(movie);
        mp4file.writeContainer(fc);
        fc.close();
    }

    Movie getMovie() {
        List<AudioVideoTrack> tracks = fileProvider.getFiles().stream()
                .map(this::getMovieForVideoFile)
                .map(AudioVideoTrack::new)
                .collect(Collectors.toList());
        Movie movie = new Movie();
        movie.addTrack(getAppendTrack(tracks, AudioVideoTrack::getAudioTrack));
        movie.addTrack(getAppendTrack(tracks, AudioVideoTrack::getVideoTrack));
        return movie;
    }

    private Movie getMovieForVideoFile(InputStream file) {
        try {
            return MovieCreator.build(createTempFile(file, UUID.randomUUID().toString()).getPath());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private AppendTrack getAppendTrack(List<AudioVideoTrack> audioVideoTracks, Function<AudioVideoTrack, Track> function) {
        try {
            return new AppendTrack(audioVideoTracks.stream()
                    .map(function).toArray(Track[]::new));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static File createTempFile(InputStream in, String name) throws IOException {
        final File tempFile = File.createTempFile(name, ".mp4");
        tempFile.deleteOnExit();
        try (FileOutputStream out = new FileOutputStream(tempFile)) {
            IOUtils.copy(in, out);
        }
        return tempFile;
    }
}

