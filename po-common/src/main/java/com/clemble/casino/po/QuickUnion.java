package com.clemble.casino.po;

import java.io.Serializable;

/**
 * Created by mavarazy on 16/03/14.
 */
public class QuickUnion implements Serializable {
    private int[] id;

    /**
     * Initializes an empty union-find data structure with N isolated components 0 through N-1.
     * @throws java.lang.IllegalArgumentException if N < 0
     * @param N the number of objects
     */
    public QuickUnion(int N) {
        id = new int[N];
        for (int i = 0; i < N; i++)
            id[i] = i;
    }


    /**
     * Merges the component containing site<tt>p</tt> with the component
     * containing site <tt>q</tt>.
     * @param p the integer representing one site
     * @param q the integer representing the other site
     * @throws java.lang.IndexOutOfBoundsException unless both 0 <= p < N and 0 <= q < N
     */
    public int union(int p, int q) {
        int size = 0;
        int tid = id[p];
        int mid = id[q];
        for(int i = 0; i < id.length; i++) {
            if (id[i] == mid || id[i] == tid) {
                id[i] = tid;
                size++;
            }
        }
        return size;
    }

    public int combine(int p) {
        int size = 0;
        int tid = id[p];
        for(int i = 0; i < id.length; i++) {
            if (id[i] == tid) {
                id[i] = i;
                size++;
            }
        }
        return size;
    }

}
