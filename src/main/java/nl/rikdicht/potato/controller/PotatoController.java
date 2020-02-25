package nl.rikdicht.potato.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nl.rikdicht.potato.service.VideoRetriever;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/video.mp4")
public class PotatoController {

    private final VideoRetriever videoRetriever;

    @GetMapping
    public ResponseEntity<?> getVideo() throws IOException {
        log.info("GET request");
        File file = videoRetriever.getRandomFile();
        log.info("Serving file " + file.getName());
        InputStreamResource inputStreamResource = new InputStreamResource(new FileInputStream(file));
        HttpHeaders headers = new HttpHeaders();
        headers.setContentLength(file.length());
        headers.set("Content-Type", "video/mp4");
        return new ResponseEntity<Object>(inputStreamResource, headers, HttpStatus.OK);
    }
}
