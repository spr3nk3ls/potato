package nl.rikdicht.potato.service;

import lombok.RequiredArgsConstructor;
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
import java.nio.channels.FileChannel;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VideoCreator {

    private final FileProvider fileProvider;

    void newVideo() throws IOException {
        Movie movie = getMovie();
        FileChannel fc = new FileOutputStream(new File("target/output.mp4")).getChannel();
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

    private Movie getMovieForVideoFile(File file) {
        try {
            return MovieCreator.build(file.getPath());
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
}

