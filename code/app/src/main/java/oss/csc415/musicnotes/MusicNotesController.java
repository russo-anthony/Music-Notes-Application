package oss.csc415.musicnotes;


import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.util.SparseArray;

import java.io.InputStream;

/**
 * Created by Anthony on 4/7/2019.
 */

public class MusicNotesController {

    //MusicNotesController Handles Audio of Played Notes

    //Array To Hold All Threads, New Thread For Each Note
    private SparseArray<NoteThread> noteThreads = null;
    private Context context;

    //Array To Contain Each Note
    private static final SparseArray<String> NOTE_MAP = new SparseArray<>();
    private static final int VOLUME = 100;

    //Static Block Maps Notes Index To Its Sound File In The Resources
    static
    {
        NOTE_MAP.put(1, "c");
        NOTE_MAP.put(2, "d");
        NOTE_MAP.put(3, "e");
        NOTE_MAP.put(4, "f");
        NOTE_MAP.put(5, "g");
        NOTE_MAP.put(6, "a");
        NOTE_MAP.put(7, "b");

        NOTE_MAP.put(8, "c_sharp");
        NOTE_MAP.put(9, "e_flat");
        NOTE_MAP.put(10, "f_sharp");
        NOTE_MAP.put(11, "g_sharp");
        NOTE_MAP.put(12, "a");
    }

    public MusicNotesController(Context context)
    {
        this.context = context;
        noteThreads = new SparseArray<>();
    }

    public void playNote(int note)
    {
        if(!isNotePlaying(note))
        {
            //If Note Is Not Already Playing
            //Start A New Thread For That Note And Add It To The Thread Map
            NoteThread note_thread = new NoteThread(note);
            note_thread.start();
            noteThreads.put(note, note_thread);
        }
    }

    public void stopNote(int note)
    {
        NoteThread note_thread = noteThreads.get(note);

        if(note_thread != null)
        {
            //Remove Stopped Note From The Thread Map
            noteThreads.remove(note);
        }
    }

    public boolean isNotePlaying(int note)
    {
        //Checks To Ensure Thread Is Active
        return noteThreads.get(note) != null;
    }

    private class NoteThread extends Thread
    {
        //NoteThread extends Thread Class, Used To Run Audio Sound Files At The Same Time
        int note;
        AudioTrack noteTrack;

        public NoteThread(int note)
        {
            this.note = note;
        }

        @Override
        public void run() {
            try {
                //Sound WAV Files Mapped To Current Note
                String path = NOTE_MAP.get(note) + ".wav";
                AssetManager assetManager = context.getAssets();
                AssetFileDescriptor ad = assetManager.openFd(path);
                long fileSize = ad.getLength();
                int bufferSize = 4096;
                byte[] buffer = new byte[bufferSize];

                //New AudioTrack
                noteTrack = new AudioTrack(AudioManager.STREAM_MUSIC, 44100, AudioFormat.CHANNEL_OUT_MONO,
                        AudioFormat.ENCODING_PCM_16BIT, bufferSize, noteTrack.MODE_STREAM);


                noteTrack.setStereoVolume(VOLUME, VOLUME);

                //Play AudioTrack
                noteTrack.play();

                //InputStream Created To Read From File
                InputStream audioStream = null;
                int headerOffset = 0x2C;
                long bytesWritten = 0;
                int bytesRead = 0;

                //Connect AudioStream To Filepath
                audioStream = assetManager.open(path);
                audioStream.read(buffer, 0, headerOffset);

                //Read From Filepath and Write Data Into The AudioTrack
                while (bytesWritten < fileSize - headerOffset)
                {
                    bytesRead = audioStream.read(buffer, 0, bufferSize);
                    bytesWritten += noteTrack.write(buffer, 0, bytesRead);
                }

                //Stop and Release AudioTrack After All Data Is Read/Written
                noteTrack.stop();
                noteTrack.release();

            } catch (Exception e) {
            } finally {
                if (noteTrack != null) {
                    noteTrack.release();
                }
            }
        }

    }



}
