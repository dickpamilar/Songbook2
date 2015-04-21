package com.holidaydiaries.songbook;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;


public class DisplaySong extends ActionBarActivity {

    TextView textView;
    ScaleGestureDetector scaleGestureDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_song);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new SongFragment())
                    .commit();
        }

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // TODO Auto-generated method stub
        if (event.getPointerCount() > 1) {
            scaleGestureDetector.onTouchEvent(event);
            return true;
        }
        return true;
    }

    public class simpleOnScaleGestureListener extends
            ScaleGestureDetector.SimpleOnScaleGestureListener {

        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            // TODO Auto-generated method stub
            float size = textView.getTextSize();
            Log.d("TextSizeStart", String.valueOf(size));

            float factor = detector.getScaleFactor();
            Log.d("Factor", String.valueOf(factor));


            float product = size*factor;
            Log.d("TextSize", String.valueOf(product));
            textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, product);

            size = textView.getTextSize();
            Log.d("TextSizeEnd", String.valueOf(size));
            return true;
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_display_song, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public class SongFragment extends Fragment {

        public SongFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_display_song, container, false);
            Intent intent = getIntent();
            String song_title = intent.getStringExtra(MainActivity.EXTRA_SONG_TITLE);
            textView = (TextView) rootView.findViewById(R.id.textview_song);
            scaleGestureDetector = new ScaleGestureDetector(this.getActivity(), new simpleOnScaleGestureListener());
            String song = "";
            try{
                InputStream songfile = getAssets().open(song_title);
                String line;
                InputStreamReader inputStreamReader = new InputStreamReader(songfile);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                while (( line = bufferedReader.readLine()) != null) {
                    song += line + "\n";
                }


            }
            catch (Exception e) {
                Toast.makeText(getApplicationContext(), e.toString() + e.getMessage(),Toast.LENGTH_LONG).show();
            }
            textView.setText(song);
            textView.setHorizontallyScrolling(true);
            textView.setMovementMethod(new ScrollingMovementMethod());
            textView.setOnTouchListener(new View.OnTouchListener()
            {

                @Override
                public boolean onTouch(View v, MotionEvent event)
                {
                    scaleGestureDetector.onTouchEvent(event);
                    return v.performClick();

                }
            });
            setRetainInstance(true);
            return rootView;
        }
    }
}
