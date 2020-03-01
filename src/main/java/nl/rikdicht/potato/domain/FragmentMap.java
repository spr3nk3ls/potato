package nl.rikdicht.potato.domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FragmentMap extends HashMap<String, List<Fragment>> {
    public FragmentMap copy(){
        FragmentMap newMap = new FragmentMap();
        for(String key : this.keySet()){
            newMap.put(key, new ArrayList<>(this.get(key)));
        }
        return newMap;
    }
}
