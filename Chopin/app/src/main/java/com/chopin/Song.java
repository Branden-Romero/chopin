package com.chopin;

/**
 * Created by branden on 2/27/15.
 */
public class Song {
    private long id;
    private String title;
    private String artist;

    public Song(long songID, String songTitle, String songArtist){
        id = songID;
        title = songTitle;
        artist = songArtist;
    }

    public long getID(){
        return id;
    }
    public String getTitle(){
        return title;
    }
    public String getArtist(){
        return artist;
    }

    public Song nextSong(){  //Uncomment this section when database is implemented
        /*String currID = ""+this.id;

        Cursor c = sqLiteDatabase.query("SELECT * FROM Songs WHERE Songs.Id IN"
                +" (SELECT Influences.Id FROM Influences WHERE Influences.Inf = "
                + currID + ") ORDER BY NEWID();");

        int idx = c.getColumnIndex("max");

        Song s = new Song(c.Id,c.Title,c.Artist);

        return s; */

        return this;
    }
}
