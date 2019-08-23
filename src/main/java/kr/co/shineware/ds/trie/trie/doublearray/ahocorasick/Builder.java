//package kr.co.shineware.ds.trie.trie.doublearray.ahocorasick;
//
//import java.util.*;
//
//public class Builder<V> {
//
//
//    /**
//     * the root state of trie
//     */
//    private State rootState = new State();
//    /**
//     * whether the position has been used
//     */
//    private boolean used[];
//    /**
//     * the allocSize of the dynamic array
//     */
//    private int allocSize;
//    /**
//     * a parameter controls the memory growth speed of the dynamic array
//     */
//    private int progress;
//    /**
//     * the next position to check unused memory
//     */
//    private int nextCheckPos;
//    /**
//     * the size of the key-pair sets
//     */
//    private int keySize;
//
//    protected V[] v;
//
//    /**
//     * the length of every key
//     */
//    protected int[] l;
//
//    /**
//     * the size of base and check array
//     */
//    protected int size;
//
//    /**
//     * Build from a map
//     *
//     * @param map a map containing key-value pairs
//     */
//    @SuppressWarnings("unchecked")
//    public void build(Map<String, V> map) {
//        // 把值保存下来
//        v = (V[]) map.values().toArray();
//        l = new int[v.length];
//        Set<String> keySet = map.keySet();
//        // 构建二分trie树
//        addAllKeyword(keySet);
//        // 在二分trie树的基础上构建双数组trie树
//        buildDoubleArrayTrie(keySet.size());
//        used = null;
//        // 构建failure表并且合并output表
//        constructFailureStates();
//        rootState = null;
//        loseWeight();
//    }
//
//    /**
//     * fetch siblings of a parent node
//     *
//     * @param parent   parent node
//     * @param siblings parent node's child nodes, i . e . the siblings
//     * @return the amount of the siblings
//     */
//    private int fetch(State parent, List<Map.Entry<Integer, State>> siblings) {
//        if (parent.isAcceptable()) {
//            State fakeNode = new State(-(parent.getDepth() + 1));  // 此节点是parent的子节点，同时具备parent的输出
//            fakeNode.addEmit(parent.getLargestValueId());
//            siblings.add(new AbstractMap.SimpleEntry<Integer, State>(0, fakeNode));
//        }
//        for (Map.Entry<Character, State> entry : parent.getSuccess().entrySet()) {
//            siblings.add(new AbstractMap.SimpleEntry<Integer, State>(entry.getKey() + 1, entry.getValue()));
//        }
//        return siblings.size();
//    }
//
//    /**
//     * add a keyword
//     *
//     * @param keyword a keyword
//     * @param index   the index of the keyword
//     */
//    private void addKeyword(String keyword, int index) {
//        State currentState = this.rootState;
//        for (Character character : keyword.toCharArray()) {
//            currentState = currentState.addState(character);
//        }
//        currentState.addEmit(index);
//        l[index] = keyword.length();
//    }
//
//    /**
//     * add a collection of keywords
//     *
//     * @param keywordSet the collection holding keywords
//     */
//    private void addAllKeyword(Collection<String> keywordSet) {
//        int i = 0;
//        for (String keyword : keywordSet) {
//            addKeyword(keyword, i++);
//        }
//    }
//
//    /**
//     * construct failure table
//     */
//    private void constructFailureStates() {
//        fail = new int[size + 1];
//        fail[1] = base[0];
//        output = new int[size + 1][];
//        Queue<State> queue = new ArrayDeque<State>();
//
//        // 第一步，将深度为1的节点的failure设为根节点
//        for (State depthOneState : this.rootState.getStates()) {
//            depthOneState.setFailure(this.rootState, fail);
//            queue.add(depthOneState);
//            constructOutput(depthOneState);
//        }
//
//        // 第二步，为深度 > 1 的节点建立failure表，这是一个bfs
//        while (!queue.isEmpty()) {
//            State currentState = queue.remove();
//
//            for (Character transition : currentState.getTransitions()) {
//                State targetState = currentState.nextState(transition);
//                queue.add(targetState);
//
//                State traceFailureState = currentState.failure();
//                while (traceFailureState.nextState(transition) == null) {
//                    traceFailureState = traceFailureState.failure();
//                }
//                State newFailureState = traceFailureState.nextState(transition);
//                targetState.setFailure(newFailureState, fail);
//                targetState.addEmit(newFailureState.emit());
//                constructOutput(targetState);
//            }
//        }
//    }
//
//    /**
//     * construct output table
//     */
//    private void constructOutput(State targetState) {
//        Collection<Integer> emit = targetState.emit();
//        if (emit == null || emit.size() == 0) return;
//        int output[] = new int[emit.size()];
//        Iterator<Integer> it = emit.iterator();
//        for (int i = 0; i < output.length; ++i) {
//            output[i] = it.next();
//        }
//        AhoCorasickDoubleArrayTrie.this.output[targetState.getIndex()] = output;
//    }
//
//    private void buildDoubleArrayTrie(int keySize) {
//        progress = 0;
//        this.keySize = keySize;
//        resize(65536 * 32); // 32个双字节
//
//        base[0] = 1;
//        nextCheckPos = 0;
//
//        State root_node = this.rootState;
//
//        List<Map.Entry<Integer, State>> siblings = new ArrayList<Map.Entry<Integer, State>>(root_node.getSuccess().entrySet().size());
//        fetch(root_node, siblings);
//        insert(siblings);
//    }
//
//    /**
//     * allocate the memory of the dynamic array
//     *
//     * @param newSize
//     * @return
//     */
//    private int resize(int newSize) {
//        int[] base2 = new int[newSize];
//        int[] check2 = new int[newSize];
//        boolean used2[] = new boolean[newSize];
//        if (allocSize > 0) {
//            System.arraycopy(base, 0, base2, 0, allocSize);
//            System.arraycopy(check, 0, check2, 0, allocSize);
//            System.arraycopy(used, 0, used2, 0, allocSize);
//        }
//
//        base = base2;
//        check = check2;
//        used = used2;
//
//        return allocSize = newSize;
//    }
//
//    /**
//     * insert the siblings to double array trie
//     *
//     * @param siblings the siblings being inserted
//     * @return the position to insert them
//     */
//    private int insert(List<Map.Entry<Integer, State>> siblings) {
//        int begin = 0;
//        int pos = Math.max(siblings.get(0).getKey() + 1, nextCheckPos) - 1;
//        int nonzero_num = 0;
//        int first = 0;
//
//        if (allocSize <= pos)
//            resize(pos + 1);
//
//        outer:
//        // 此循环体的目标是找出满足base[begin + a1...an]  == 0的n个空闲空间,a1...an是siblings中的n个节点
//        while (true) {
//            pos++;
//
//            if (allocSize <= pos)
//                resize(pos + 1);
//
//            if (check[pos] != 0) {
//                nonzero_num++;
//                continue;
//            } else if (first == 0) {
//                nextCheckPos = pos;
//                first = 1;
//            }
//
//            begin = pos - siblings.get(0).getKey(); // 当前位置离第一个兄弟节点的距离
//            if (allocSize <= (begin + siblings.get(siblings.size() - 1).getKey())) {
//                // progress can be zero // 防止progress产生除零错误
//                double l = (1.05 > 1.0 * keySize / (progress + 1)) ? 1.05 : 1.0 * keySize / (progress + 1);
//                resize((int) (allocSize * l));
//            }
//
//            if (used[begin])
//                continue;
//
//            for (int i = 1; i < siblings.size(); i++)
//                if (check[begin + siblings.get(i).getKey()] != 0)
//                    continue outer;
//
//            break;
//        }
//
//        // -- Simple heuristics --
//        // if the percentage of non-empty contents in check between the
//        // index
//        // 'next_check_pos' and 'check' is greater than some constant value
//        // (e.g. 0.9),
//        // new 'next_check_pos' index is written by 'check'.
//        if (1.0 * nonzero_num / (pos - nextCheckPos + 1) >= 0.95)
//            nextCheckPos = pos; // 从位置 next_check_pos 开始到 pos 间，如果已占用的空间在95%以上，下次插入节点时，直接从 pos 位置处开始查找
//        used[begin] = true;
//
//        size = (size > begin + siblings.get(siblings.size() - 1).getKey() + 1) ? size : begin + siblings.get(siblings.size() - 1).getKey() + 1;
//
//        for (Map.Entry<Integer, State> sibling : siblings) {
//            check[begin + sibling.getKey()] = begin;
//        }
//
//        for (Map.Entry<Integer, State> sibling : siblings) {
//            List<Map.Entry<Integer, State>> new_siblings = new ArrayList<Map.Entry<Integer, State>>(sibling.getValue().getSuccess().entrySet().size() + 1);
//
//            if (fetch(sibling.getValue(), new_siblings) == 0)  // 一个词的终止且不为其他词的前缀，其实就是叶子节点
//            {
//                base[begin + sibling.getKey()] = (-sibling.getValue().getLargestValueId() - 1);
//                progress++;
//            } else {
//                int h = insert(new_siblings);   // dfs
//                base[begin + sibling.getKey()] = h;
//            }
//            sibling.getValue().setIndex(begin + sibling.getKey());
//        }
//        return begin;
//    }
//
//    /**
//     * free the unnecessary memory
//     */
//    private void loseWeight() {
//        int nbase[] = new int[size + 65535];
//        System.arraycopy(base, 0, nbase, 0, size);
//        base = nbase;
//
//        int ncheck[] = new int[size + 65535];
//        System.arraycopy(check, 0, ncheck, 0, size);
//        check = ncheck;
//    }
//}
