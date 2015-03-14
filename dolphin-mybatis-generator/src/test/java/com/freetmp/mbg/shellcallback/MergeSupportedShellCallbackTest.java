package com.freetmp.mbg.shellcallback;

import org.junit.BeforeClass;

/*
 * Created by pin on 2015/2/7.
 */
public class MergeSupportedShellCallbackTest {

    static MergeSupportedShellCallback callback;

    @BeforeClass
    public static void init(){
        callback = new MergeSupportedShellCallback(true);
    }

}
