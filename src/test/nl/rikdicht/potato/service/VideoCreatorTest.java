package nl.rikdicht.potato.service;

import org.junit.Test;
import org.mp4parser.muxer.Movie;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class VideoCreatorTest {

    private VideoCreator videoCreator = new VideoCreator(new FileProvider());

    @Test
    public void createVideoFile() throws IOException {
        videoCreator.newVideo();
    }

    @Test
    public void newVideo() {
        //When
        Movie movie = videoCreator.getMovie();
        assertEquals(2, movie.getTracks().size());
    }
}
