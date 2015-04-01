package com.chopin;

import junit.framework.TestCase;
import android.media.MediaPlayer;

public class MusicServiceTest extends TestCase {
    MusicService tester = new MusicService();

    public void testOnError() throws Exception {
           MediaPlayer mp = new MediaPlayer();
            int y = 0;
            int x = 0;
      assert(tester.onError(mp,y,x) == false);
    }
}