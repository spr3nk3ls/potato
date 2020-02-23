package nl.rikdicht.potato.service;

import lombok.Data;
import org.mp4parser.muxer.Movie;
import org.mp4parser.muxer.Track;

import java.util.List;

@Data
class AudioVideoTrack {

    AudioVideoTrack(Movie movie){
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

    private Track audioTrack;
    private Track videoTrack;
}
