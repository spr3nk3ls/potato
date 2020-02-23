package nl.rikdicht.potato.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nl.rikdicht.potato.service.VideoRetriever;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/video")
public class PotatoController {

    private final VideoRetriever videoRetriever;

    @GetMapping
    public void getVideo(HttpServletResponse response) throws IOException {
        log.info("GET request");
        InputStream fileContents = videoRetriever.getRandomFile();
        response.setContentType("video/mp4");
        response.setHeader("Content-Disposition", "attachment; filename=" + "video.mp4");
        log.info("downloading...");
        returnResponse(response, fileContents);
    }

    private void returnResponse(HttpServletResponse response, InputStream is) throws IOException {
        Assert.notNull(is, "inputstream is null");
        IOUtils.copy(is, response.getOutputStream());
        response.flushBuffer();
        is.close();
        response.getOutputStream().close();
    }
}
