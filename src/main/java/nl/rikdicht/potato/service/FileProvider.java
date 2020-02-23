package nl.rikdicht.potato.service;

import lombok.SneakyThrows;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class FileProvider {

    private static final List<String> mapping = Arrays.asList("a", "b", "c", "d", "e");

    private static final List<String> ordering = Arrays.asList("a", "b", "c", "d", "b", "c", "d", "b", "c", "e", "c");

    List<InputStream> getFiles(){
        List<InputStream> orderedFiles = new ArrayList<>();
        Map<String, List<InputStream>> mappedFiles = getRandomizedMappedFiles();
        for(String order : ordering){
            List<InputStream> files = mappedFiles.get(order);
            if(!files.isEmpty())
                orderedFiles.add(files.remove(0));
        }
        return orderedFiles;
    }

    private Map<String, List<InputStream>> getRandomizedMappedFiles(){
        Map<String, List<InputStream>> map = new HashMap<>();
        for(String key : mapping){
            List<InputStream> files = getResourceFolderFiles("-" + key + "*");
            Collections.shuffle(files);
            map.put(key, files);
        }
        return map;
    }

    @SneakyThrows
    private List<InputStream> getResourceFolderFiles(String pattern) {
        ClassLoader cl = this.getClass().getClassLoader();
        ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver(cl);
        Resource[] resources = resolver.getResources("classpath*:/*" + pattern + ".mp4");
        return Arrays.stream(resources).map(FileProvider::getFileFromResource).collect(Collectors.toList());
    }

    @SneakyThrows
    private static InputStream getFileFromResource(Resource resource){
        return resource.getInputStream();
    }

}
