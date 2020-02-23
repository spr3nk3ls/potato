package nl.rikdicht.potato.service;

import lombok.SneakyThrows;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class FileProvider {

    private static final List<String> mapping = Arrays.asList("a", "b", "c", "d", "e");

    private static final List<String> ordering = Arrays.asList("a", "b", "c", "d", "b", "c", "d", "b", "c", "e", "c");

    List<File> getFiles(){
        List<File> orderedFiles = new ArrayList<>();
        Map<String, List<File>> mappedFiles = getRandomizedMappedFiles();
        for(String order : ordering){
            List<File> files = mappedFiles.get(order);
            if(!files.isEmpty())
                orderedFiles.add(files.remove(0));
        }
        return orderedFiles;
    }

    private Map<String, List<File>> getRandomizedMappedFiles(){
        Map<String, List<File>> map = new HashMap<>();
        for(String key : mapping){
            List<File> files = getResourceFolderFiles("-" + key + "*");
            Collections.shuffle(files);
            map.put(key, files);
        }
        return map;
    }

    @SneakyThrows
    private List<File> getResourceFolderFiles(String pattern) {
        ClassLoader cl = this.getClass().getClassLoader();
        ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver(cl);
        Resource[] resources = resolver.getResources("classpath*:/*" + pattern + ".mp4");
        return Arrays.stream(resources).map(FileProvider::getFileFromResource).collect(Collectors.toList());
    }

    @SneakyThrows
    private static File getFileFromResource(Resource resource){
        return resource.getFile();
    }

}
