package nl.rikdicht.potato.service;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import nl.rikdicht.potato.domain.Fragment;
import nl.rikdicht.potato.domain.FragmentMap;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
public class FragmentProvider {

    private static final List<String> mapping = Arrays.asList("a", "b", "c", "d", "e");

    private static final List<String> ordering = Arrays.asList("a", "b", "c", "d", "b", "c", "d", "b", "c", "e", "c");

    private FragmentMap fragmentMap;

    List<Fragment> getFragments(){
        List<Fragment> orderedMovies = new ArrayList<>();
        Map<String, List<Fragment>> mappedFiles = getFragmentMap();
        for(String order : ordering){
            List<Fragment> moviesForOrder = mappedFiles.get(order);
            Collections.shuffle(moviesForOrder);
            if(!moviesForOrder.isEmpty())
                orderedMovies.add(moviesForOrder.remove(0));
        }
        log.info("Creating movie with sequence " + orderedMovies.stream()
                        .map(Fragment::getName)
                        .map(string -> string.substring(string.indexOf("-") + 1, string.indexOf(".")))
                .collect(Collectors.joining()));
        return orderedMovies;
    }

    private FragmentMap getFragmentMap(){
        if(fragmentMap == null) {
            FragmentMap map = new FragmentMap();
            for (String key : mapping) {
                List<Resource> files = getResourceFolderFiles("-" + key + "*");
                List<Fragment> movies = files.stream()
                        .map(Fragment::new)
                        .collect(Collectors.toList());
                map.put(key, movies);
            }
            fragmentMap = map;
        }
        return fragmentMap.copy();
    }

    @SneakyThrows
    private List<Resource> getResourceFolderFiles(String pattern) {
        ClassLoader cl = this.getClass().getClassLoader();
        ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver(cl);
        Resource[] resources = resolver.getResources("classpath*:/*" + pattern + ".mp4");
        return Arrays.stream(resources).collect(Collectors.toList());
    }
}
