package oss.csc415.musicnotes;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

public class MusicNotesActivity extends AppCompatActivity
{

    ImageView scale;
    int musicScale;


    //Main Activity Creation Function
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
         super.onCreate(savedInstanceState);
         setContentView(R.layout.activity_main);

         //ImageView Container To Display Pictures
         scale = (ImageView)findViewById(R.id.scaleContainer);
         musicScale = getResources().getIdentifier("drawable/music_scale", null, this.getPackageName());
         scale.setImageResource(musicScale);
    }


    //On Application Resume Function
    @Override
    protected void onResume()
    {
        super.onResume();

        //Filter Filled With Actions Broadcasted From MusicNotesUI
        final IntentFilter filter = new IntentFilter();
        filter.addAction(MusicNotesUI.NOTE_A);
        filter.addAction(MusicNotesUI.NOTE_B);
        filter.addAction(MusicNotesUI.NOTE_C);
        filter.addAction(MusicNotesUI.NOTE_D);
        filter.addAction(MusicNotesUI.NOTE_E);
        filter.addAction(MusicNotesUI.NOTE_F);
        filter.addAction(MusicNotesUI.NOTE_G);

        //Register Broadcast Receiver In Main Activity
        registerReceiver(imgViewUpdate, filter);
    }

    //On Application Pause
    @Override
    protected void onPause()
    {
        super.onPause();

        //Unregister Broadcast Receiver From Main Activity
        unregisterReceiver(imgViewUpdate);
    }

    //Broadcast Receiver Intercepts and Interprets Actions Sent From MusicNotesUI
    private final BroadcastReceiver imgViewUpdate = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            switch(action)
            {
                //Switch Case Block For Each Note To Update The Music Scale Display
                case MusicNotesUI.NOTE_A:
                    musicScale = getResources().getIdentifier("drawable/music_scale_a", null, getPackageName());
                    scale.setImageResource(musicScale);
                    break;
                case MusicNotesUI.NOTE_B:
                    musicScale = getResources().getIdentifier("drawable/music_scale_b", null, getPackageName());
                    scale.setImageResource(musicScale);
                    break;
                case MusicNotesUI.NOTE_C:
                    musicScale = getResources().getIdentifier("drawable/music_scale_c", null, getPackageName());
                    scale.setImageResource(musicScale);
                    break;
                case MusicNotesUI.NOTE_D:
                    musicScale = getResources().getIdentifier("drawable/music_scale_d", null, getPackageName());
                    scale.setImageResource(musicScale);
                    break;
                case MusicNotesUI.NOTE_E:
                    musicScale = getResources().getIdentifier("drawable/music_scale_e", null, getPackageName());
                    scale.setImageResource(musicScale);
                    break;
                case MusicNotesUI.NOTE_F:
                    musicScale = getResources().getIdentifier("drawable/music_scale_f", null, getPackageName());
                    scale.setImageResource(musicScale);
                    break;
                case MusicNotesUI.NOTE_G:
                    musicScale = getResources().getIdentifier("drawable/music_scale_g", null, getPackageName());
                    scale.setImageResource(musicScale);
                    break;
            }
        }
    };

}
