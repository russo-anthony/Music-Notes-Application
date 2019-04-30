package oss.csc415.musicnotes;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.os.Handler;
import android.os.Message;

import java.util.ArrayList;
import java.util.HashMap;


/**
 * Created by Anthony on 4/7/2019.
 */

public class MusicNotesUI extends View {

    //MusicNotesUI Manages Display and Interactions With The UI
    public static final int keyNum = 7;
    private Paint white, black, ltGray, dkGray;
    private HashMap<Integer, MusicNote> whiteKeys = new HashMap<>();
    private HashMap<Integer, MusicNote> blackKeys = new HashMap<>();
    private int keyW, keyH;
    private MusicNotesController notePlayer;
    public Context context;

    public final static String NOTE_C = "1";
    public final static String NOTE_D = "2";
    public final static String NOTE_E = "3";
    public final static String NOTE_F = "4";
    public final static String NOTE_G = "5";
    public final static String NOTE_A = "6";
    public final static String NOTE_B = "7";

    public MusicNotesUI(Context context, AttributeSet attrs)
    {
        super(context, attrs);

        //Initialize Paints Used To Draw On Screen
        black = new Paint();
        dkGray = new Paint();
        white = new Paint();
        ltGray = new Paint();
        black.setColor(Color.BLACK);
        white.setColor(Color.WHITE);
        ltGray.setColor(Color.LTGRAY);
        dkGray.setColor(Color.DKGRAY);
        white.setStyle(Paint.Style.FILL);
        ltGray.setStyle(Paint.Style.FILL);

        //Create The MusicNotesController To Manage Audio
        notePlayer = new MusicNotesController(context);
        this.context = context;
        //context.
    }

    //Handles Change In Screen Size
    //Determines Location And Size Of Each Piano Key
    @Override
    protected void onSizeChanged(int width, int height, int oldWidth, int oldHeight)
    {
        super.onSizeChanged(width, height, oldWidth, oldHeight);

        //Width Of The Screen Divided By Total Number Of Keys
        keyW = width / keyNum;
        keyH = height;

        //Height Divided In Half To Have Room For Music Scale Display
        height = height / 2;

        //Total Number Of Piano Keys
        int keyCount = 8;

        //Rects Are Determined By The Coordinates Of Their 4 Sides
        //Iterate Through Each Key And Create A RectF For Each Key
        for(int x = 0; x < keyNum; x++)
        {
            int leftNoteSide = x * keyW;
            int rightNoteSide = leftNoteSide + keyW;

            //Bounds Condition
            if(x == (keyNum - 1))
            {
                rightNoteSide = width;
            }

            //Create RectF (Rect Float)
            RectF key = new RectF(leftNoteSide, 0, rightNoteSide, height);

            //Offset RectF To Place At Bottom Of Screen
            key.offset(0, height);

            //Create MusicNote From Rect and Index and Add To HashMap
            whiteKeys.put(x + 1, new MusicNote(key, x + 1));

            //Create Black Keys Everywhere Except Index 0, 3, and 7
            if(x != 0 && x != 3 && x != 7)
            {
                //Keys  Are Smaller and Offset In The Middle Of The White Keys
                key = new RectF((float)(x - 1) * keyW + 0.5f * keyW + 0.25f * keyW,
                                0, (float) x * keyW + 0.25f * keyW, 0.67f * height);

                //Offset To Match Height Of White Keys
                key.offset(0, height);

                //Create MusicNote From Rect and Index and Add To HashMap
                blackKeys.put(keyCount, new MusicNote(key, keyCount));
                keyCount++;
            }
        }
    }

    //Draw Method To Draw The Keys To The Canvas
    @Override
    protected void onDraw(Canvas canvas)
    {
        //When A Key Is Pressed In Will Be Redrawn Light Gray To Indicate Use
        for(MusicNote key : whiteKeys.values())
        {
            if(key.notePressed)
            {
                canvas.drawRect(key.key, ltGray);
            }
            else
            {
                canvas.drawRect(key.key, white);
            }
        }

        //Lines Separating The Individual Keys
        for(int i = 0; i < keyNum; i++)
        {
            canvas.drawLine(i * keyW, keyH, i * keyW, keyH / 2, black);
        }

        //When A Key Is Pressed In Will Be Redrawn Dark Gray To Indicate Use
        for(MusicNote key : blackKeys.values())
        {
            if(key.notePressed)
            {
                canvas.drawRect(key.key, dkGray);
            }
            else
            {
                canvas.drawRect(key.key, black);
            }
        }


    }

    //Method Controls Touch Screen Events
    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        int action = event.getAction();

        //ACTION_DOWN Specifies A Screen Touch Event (Tap Finger)
        //ACTION_MOVE Specifies A Screen Touch Event With Moving Coordinates (Drag Finger)
        boolean keyPressed = action == MotionEvent.ACTION_DOWN || action == MotionEvent.ACTION_MOVE;

        //Iterate Through All Event Indexes To Cross Check Event Location With Key Location
        for(int touchNum = 0; touchNum < event.getPointerCount(); touchNum++)
        {
            float x = event.getX(touchNum) * event.getXPrecision();
            float y = event.getY(touchNum) * event.getYPrecision();

            //Method Call To Check If Rect Contains Current Pointer Location
            MusicNote key = noteContains(x, y);

            //If Key Contains The Pointer Location, Set Key Press Boolean True
            if (key != null)
            {
                key.notePressed = keyPressed;
            }
        }

        //List of Key Indexes
        ArrayList<MusicNote> tmp = new ArrayList<>(whiteKeys.values());
        tmp.addAll(blackKeys.values());

        //For Each Key Check Pressed Status
        for (MusicNote key : tmp) {
            if (key.notePressed) {
                //If Key Pressed, Play Note Audio, and Update Display Screen
                if (!notePlayer.isNotePlaying(key.note))
                {
                    notePlayer.playNote(key.note);
                    String broadcastString = Integer.toString(key.note);
                    broadcastUpdate(broadcastString);
                    invalidate();
                } else {
                    releaseKey(key);
                }
            } else {
                notePlayer.stopNote(key.note);
                releaseKey(key);
            }
        }

        return true;
    }

    //Checks Given Location To Contents Of Key Rect
    private MusicNote noteContains(float x, float y)
    {
        for(MusicNote key : blackKeys.values())
        {
            if(key.key.contains(x, y))
            {
                return key;
            }
        }

        for(MusicNote key : whiteKeys.values())
        {
            if(key.key.contains(x, y))
            {
                return key;
            }
        }
        return null;
    }

    //Release Key From Pressed State
    private void releaseKey(final MusicNote key)
    {
        handler.postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                key.notePressed = false;
                handler.sendEmptyMessage(0);
            }
        }, 100);
    }

    private Handler handler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            //Screen Update
            invalidate();
        }
    };

    //BroadCast Updates To The Main Activity
    private void broadcastUpdate(final String action)
    {
        final Intent intent = new Intent(action);
        context.sendBroadcast(intent);
    }
}
