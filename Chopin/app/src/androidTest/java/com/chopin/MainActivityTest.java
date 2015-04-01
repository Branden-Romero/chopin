package com.chopin;

import junit.framework.TestCase;
public class MainActivityTest extends TestCase {

    MainActivity tester = new MainActivity();

    public void testGetDuration() throws Exception {

    }

    public void testGetCurrentPostion() throws Exception {
        int result = tester.getCurrentPosition();
        int dur = tester.getDuration();
        assert(result <= dur);

    }

    public void testIsPlaying() throws Exception {

    }

    public void testCanPause() throws Exception {

    }

    public void testCanSeekBackward() throws Exception {

    }

    public void testCanSeekForward() throws Exception {

    }

}