package kr.co.shineware.ds.trie.trie.doublearray.ahocorasick.model;

public class Hit<V>
{
    /**
     * the beginning index, inclusive.
     */
    public final int begin;
    /**
     * the ending index, exclusive.
     */
    public final int end;
    /**
     * the value assigned to the keyword
     */
    public final V value;

    public Hit(int begin, int end, V value)
    {
        this.begin = begin;
        this.end = end;
        this.value = value;
    }

    public Hit(V value)
    {
        this.begin = -1;
        this.end = -1;
        this.value = value;
    }

    @Override
    public String toString()
    {
        return String.format("[%d:%d]=%s", begin, end, value);
    }
}
