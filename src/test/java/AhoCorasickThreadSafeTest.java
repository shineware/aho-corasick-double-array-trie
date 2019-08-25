import kr.co.shineware.ds.trie.trie.doublearray.ahocorasick.AhoCorasickDoubleArrayTrie;
import kr.co.shineware.ds.trie.trie.doublearray.ahocorasick.model.Hit;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.*;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;

@Ignore
public class AhoCorasickThreadSafeTest {
    private static ExecutorService service;
    private static final int THREAD_NUM = 4;
    private static final int DATA_NUM = 100000;

    private static String[] KEYS;
    private static Integer[] VALS;

    private static final AhoCorasickDoubleArrayTrie<Integer> dic = new AhoCorasickDoubleArrayTrie<>();

    @BeforeClass
    public static void setup() {
        KEYS = new String[DATA_NUM];
        VALS = new Integer[DATA_NUM];

        for (int i = 0; i < KEYS.length; i++) {
            KEYS[i] = Integer.toString(i);
            VALS[i] = i;
        }

        for (int i = 0; i < KEYS.length; i++) {
            dic.put(KEYS[i], VALS[i]);
        }
        dic.build();

        service = Executors.newFixedThreadPool(THREAD_NUM);
    }

    @AfterClass
    public static void teardown() {
        if (service != null) {
            service.shutdown();
        }
    }

	//    @Test
	//    public void testSequentialGet() {
	//        for (int i = 0; i < KEYS.length; i++) {
	//            // TODO: 2019-08-23 이 부분이 지금 정상적으로 test가 되어야 기존에 있는 인터페이스를 따라갈 수 있어요.
	//            final Map<String, Integer> result = dic.get(KEYS[i]);
	//            final Set<Integer> expectedResult = expectedResultSet(KEYS[i]);
	//
	//            assertEquals(expectedResult, new HashSet<>(result.values()));
	//        }
	//    }

    @Test
    public void testSequentialGetModifiedByDennis() {
        for (int i = 0; i < KEYS.length; i++) {
            final List<Hit<Integer>> parseResult = dic.parseText(KEYS[i]);
            final Set<Integer> actualResult = parseResult.stream()
                .map(hitResult -> hitResult.value)
                .collect(Collectors.toSet());

            final Set<Integer> expectedResult = new HashSet<>();
            final String targetValue = KEYS[i];
            for (int subStrLen = 1; subStrLen < targetValue.length() + 1; subStrLen++) {
                for (int subStrBeginIdx = 0; subStrBeginIdx + subStrLen <= targetValue.length(); subStrBeginIdx++) {
                    final String subStr = targetValue.substring(subStrBeginIdx, subStrBeginIdx + subStrLen);
                    expectedResult.add(Integer.parseInt(subStr));
                }
            }

            assertEquals(expectedResult, actualResult);
        }
    }

	//    @Test
	//    public void testConcurrentGet() throws ExecutionException, InterruptedException {
	//        final List<Future> futures = new ArrayList<>();
	//        for (int i = 0; i < KEYS.length; i++) {
	//            final int index = i;
	//            futures.add(service.submit(new Callable<Set<Integer>>() {
	//                @Override
	//                public Set<Integer> call() throws Exception {
	//                    return new HashSet<>(dic.get(KEYS[index]).values());
	//                }
	//            }));
	//        }
	//
	//        for (int i = 0; i < futures.size(); i++) {
	//            final Set<Integer> expectedResult = expectedResultSet(KEYS[i]);
	//            assertEquals(expectedResult, futures.get(i).get());
	//        }
	//    }

    @Test
    public void testConcurrentGetModifiedByDennis() throws ExecutionException, InterruptedException {
        final List<Future> futures = new ArrayList<>();
        for (int i = 0; i < KEYS.length; i++) {
            final int index = i;
            futures.add(service.submit(new Callable<Set<Integer>>() {
                @Override
                public Set<Integer> call() throws Exception {
                    return dic.parseText(KEYS[index])
                        .stream()
                        .map(hitResult -> hitResult.value)
                        .collect(Collectors.toSet());
                }
            }));
        }

        for (int i = 0; i < futures.size(); i++) {
            final Set<Integer> expectedResult = new HashSet<>();
            final String targetValue = KEYS[i];
            for (int subStrLen = 1; subStrLen < targetValue.length() + 1; subStrLen++) {
                for (int subStrBeginIdx = 0; subStrBeginIdx + subStrLen <= targetValue.length(); subStrBeginIdx++) {
                    final String subStr = targetValue.substring(subStrBeginIdx, subStrBeginIdx + subStrLen);
                    expectedResult.add(Integer.parseInt(subStr));
                }
            }

            assertEquals(expectedResult, futures.get(i).get());
        }
    }

	//    private static Set<Integer> expectedResultSet(final String str) {
	//        final Set<Integer> resultSet = new HashSet<>();
	//
	//        for (int subStrLen = 1; subStrLen < str.length() + 1; subStrLen++) {
	//            for (int subStrBeginIdx = 0; subStrBeginIdx + subStrLen <= str.length(); subStrBeginIdx++) {
	//                final String subStr = str.substring(subStrBeginIdx, subStrBeginIdx + subStrLen);
	//                resultSet.add(Integer.parseInt(subStr));
	//            }
	//        }
	//
	//        return resultSet;
	//    }
}
