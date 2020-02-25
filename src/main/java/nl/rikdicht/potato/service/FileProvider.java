package nl.rikdicht.potato.service;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
public class FileProvider {

    private static final List<String> mapping = Arrays.asList("a", "b", "c", "d", "e");

    private static final List<String> ordering = Arrays.asList("a", "b", "c", "d", "b", "c", "d", "b", "c", "e", "c");

    List<Resource> getResources(){
        List<Resource> orderedFiles = new ArrayList<>();
        Map<String, List<Resource>> mappedFiles = getRandomizedMappedResources();
        for(String order : ordering){
            List<Resource> resource = mappedFiles.get(order);
            if(!resource.isEmpty())
                orderedFiles.add(resource.remove(0));
        }
        log.info("Creating file with sequence " + orderedFiles.stream()
                .map(Resource::getFilename)
                .map(string -> string.substring(string.indexOf("-") + 1, string.indexOf(".")))
                .collect(Collectors.joining()));
        return orderedFiles;
    }

    private Map<String, List<Resource>> getRandomizedMappedResources(){
        Map<String, List<Resource>> map = new HashMap<>();
        for(String key : mapping){
            List<Resource> files = getResourceFolderFiles("-" + key + "*");
            Collections.shuffle(files);
            map.put(key, files);
        }
        return map;
    }

    @SneakyThrows
    private List<Resource> getResourceFolderFiles(String pattern) {
        ClassLoader cl = this.getClass().getClassLoader();
        ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver(cl);
        Resource[] resources = resolver.getResources("classpath*:/*" + pattern + ".mp4");
        return Arrays.stream(resources).collect(Collectors.toList());
    }
}
