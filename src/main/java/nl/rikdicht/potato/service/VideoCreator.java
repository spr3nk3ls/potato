package nl.rikdicht.potato.service;

import lombok.RequiredArgsConstructor;
import nl.rikdicht.potato.domain.Fragment;
import org.mp4parser.Container;
import org.mp4parser.muxer.Movie;
import org.mp4parser.muxer.Track;
import org.mp4parser.muxer.builder.DefaultMp4Builder;
import org.mp4parser.muxer.tracks.AppendTrack;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.List;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class VideoCreator {

    private final FragmentProvider fragmentProvider;

    public static final String VIDEO_FOLDER = "videos";

    void createVideo(String filename) throws IOException {
        Movie movie = getMovie();
        FileChannel fc = new FileOutputStream(new File(VIDEO_FOLDER + "/" + filename)).getChannel();
        Container mp4file = new DefaultMp4Builder().build(movie);
        mp4file.writeContainer(fc);
        fc.close();
    }

    Movie getMovie() {
        List<Fragment> tracks = fragmentProvider.getFragments();
        Movie movie = new Movie();
        movie.addTrack(getAppendTrack(tracks, Fragment::getAudioTrack));
        movie.addTrack(getAppendTrack(tracks, Fragment::getVideoTrack));
        return movie;
    }

    private AppendTrack getAppendTrack(List<Fragment> fragments, Function<Fragment, Track> function) {
        try {
            return new AppendTrack(fragments.stream()
                    .map(function).toArray(Track[]::new));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

