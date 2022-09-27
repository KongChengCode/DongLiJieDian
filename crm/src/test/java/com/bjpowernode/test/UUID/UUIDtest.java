package com.bjpowernode.test.UUID;

import org.junit.Test;

import java.util.UUID;


public class UUIDtest {
    //jdk提供的UUID
    @Test
    public void testUUID(){
        /*UUID.randomUUID().toString();
        System.out.println(UUID.randomUUID().getClass());
        System.out.println(UUID.randomUUID().toString().getClass());*/
        System.out.println(UUID.randomUUID().toString().replaceAll("-",""));
    }
}
