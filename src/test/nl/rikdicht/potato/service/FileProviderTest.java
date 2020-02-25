package nl.rikdicht.potato.service;

import org.junit.Test;

import java.io.InputStream;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class FileProviderTest {

    private FileProvider fileProvider = new FileProvider();

    @Test
    public void getOrdered() {
        List<InputStream> files = fileProvider.getFiles();
        assertEquals(11, files.size());
    }
}
