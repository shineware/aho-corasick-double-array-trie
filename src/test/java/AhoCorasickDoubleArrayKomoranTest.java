import kr.co.shineware.ds.trie.trie.doublearray.ahocorasick.AhoCorasickDoubleArrayTrie;
import kr.co.shineware.ds.trie.trie.doublearray.ahocorasick.FindContext;
import kr.co.shineware.ds.trie.trie.doublearray.ahocorasick.model.Hit;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AhoCorasickDoubleArrayKomoranTest {

    @Test
    public void tempTest() {
        // Collect test data set
        Map<String, String> map = new HashMap<>();
        String[] keyArray = new String[]
                {
                        "hers",
                        "his",
                        "she",
                        "he"
                };
        for (String key : keyArray) {
            map.put(key, key+"!!!");
        }
        // Build an AhoCorasickDoubleArrayTrie
        AhoCorasickDoubleArrayTrie<String> acdat = new AhoCorasickDoubleArrayTrie<>();
        acdat.build(map);
        // Test it
        String text = "ushersushers";
        List<Hit<String>> wordList = acdat.parseText(text);

        for (Hit<String> word : wordList) {
            System.out.println("Found : " + word);
        }

        FindContext findContext = new FindContext();

        for (char c : text.toCharArray()) {
            List<Hit<String>> result = acdat.parseText(c, findContext);
            for (Hit<String> stringHit : result) {
                System.out.println("Found with "+c+" : " + stringHit);
            }
            System.out.println();
        }
    }
}
