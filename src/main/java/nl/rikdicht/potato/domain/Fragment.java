package nl.rikdicht.potato.domain;

import lombok.Data;
import org.mp4parser.muxer.InMemRandomAccessSourceImpl;
import org.mp4parser.muxer.Movie;
import org.mp4parser.muxer.RandomAccessSource;
import org.mp4parser.muxer.Track;
import org.mp4parser.muxer.container.mp4.MovieCreator;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.util.List;

@Data
public class Fragment {

    private String name;
    private String key;
    private Track audioTrack;
    private Track videoTrack;

    public Fragment(Resource resource){
        name = resource.getFilename();
        Movie movie = getMovieForVideoFile(resource);
        List<Track> tracks = movie.getTracks();
        if(tracks.size() > 2){
            throw new RuntimeException("Source file movie contains too many tracks.");
        }
        for(Track track : movie.getTracks()){
            if(track.getHandler().equals("soun")){
                this.setAudioTrack(track);
            }
            if(track.getHandler().equals("vide")){
                this.setVideoTrack(track);
            }
        }
    }

    private Movie getMovieForVideoFile(Resource resource) {
        try {
            return MovieCreator.build(
                    Channels.newChannel(resource.getInputStream()),
                    getRandomAccessSource(resource),
                    resource.getFilename());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private RandomAccessSource getRandomAccessSource(Resource resource) throws IOException {
        byte[] resourceBytes = resource.getInputStream().readAllBytes();
        ByteBuffer buffer = ByteBuffer.wrap(resourceBytes);
        return new InMemRandomAccessSourceImpl(buffer);
    }

}
