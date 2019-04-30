package oss.csc415.musicnotes;

import android.graphics.RectF;


/**
 * Created by Anthony on 4/7/2019.
 */

public class MusicNote {

    //MusicNote Object Represents The Keys on the Piano
    public int note;
    public RectF key;

    //Boolean Determines If The Piano Key Is Being Pressed
    public boolean notePressed;


    public MusicNote(RectF key, int note)
    {
        //Each Note Is Made of an Index and a Rect
        this.note = note;
        this.key = key;
    }
}
