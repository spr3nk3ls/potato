package nl.rikdicht.potato.service;

import nl.rikdicht.potato.domain.Fragment;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class FragmentProviderTest {

    private FragmentProvider fragmentProvider = new FragmentProvider();

    @Test
    public void getOrdered() {
        List<Fragment> files = fragmentProvider.getFragments();
        assertEquals(11, files.size());
    }
}
