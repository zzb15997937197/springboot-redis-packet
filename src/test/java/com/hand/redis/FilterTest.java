package com.hand.redis;


import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnels;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class FilterTest {



    private static int size = 1000000;


    private static BloomFilter<Integer> bloomFilter = BloomFilter.create(Funnels.integerFunnel(), size);



    public static void main(String[] args) {

        for (int i = 0; i < size; i++) {

            bloomFilter.put(i);

        }

        List<Integer> list = new ArrayList<Integer>(1000);



        //故意取10000个不在过滤器里的值，看看有多少个会被认为在过滤器里

        for (int i = size + 10000; i < size + 20000; i++) {

            if (bloomFilter.mightContain(i)) {
                //因为布隆过滤器存在误判，这些不存在的值计算出可能存在，那么就是布隆过滤器的误判

                list.add(i);
            }
        }


        System.out.println("误判的数量：" + list.size());


    }


}
