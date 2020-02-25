package nl.rikdicht.potato.service;

import org.junit.Test;
import org.springframework.core.io.Resource;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class FileProviderTest {

    private FileProvider fileProvider = new FileProvider();

    @Test
    public void getOrdered() {
        List<Resource> files = fileProvider.getResources();
        assertEquals(11, files.size());
    }
}
