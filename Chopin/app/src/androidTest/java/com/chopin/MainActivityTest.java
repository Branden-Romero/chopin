package com.chopin;

import junit.framework.TestCase;
import java.util.*;
public class MainActivityTest extends TestCase {

    MainActivity tester = new MainActivity();

    public void testGetDuration() throws Exception {
        int result = tester.getDuration();
        assert(result >= 0);
    }

    public void testGetCurrentPostion() throws Exception {
        int result = tester.getCurrentPosition();
        int dur = tester.getDuration();
        assert(result <= dur);
    }

    public void testGetCurrentPostion_2() throws Exception {
        int result = tester.getCurrentPosition();
        assert(result >= 0);
    }

    public void testIsPlaying_2() throws Exception {
        boolean results = tester.isPlaying();
        assert(results == false);
    }

    public void testCanPause() throws Exception {
        assert(tester.canPause() == true);
    }

    public void testCanSeekBackward() throws Exception {
        assert(tester.canSeekBackward() == false);
    }

    public void testCanSeekForward() throws Exception {
        assert(tester.canSeekForward() == false);
    }



}