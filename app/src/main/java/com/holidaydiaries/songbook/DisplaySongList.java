package com.holidaydiaries.songbook;



import android.content.Intent;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.holidaydiaries.songbook.contentprovider.SongBookContentProvider;
import com.holidaydiaries.songbook.database.SongTable;


public class DisplaySongList extends ActionBarActivity  {

    private SimpleCursorAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_display_song_list);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().
                    add(R.id.container, new PlaceholderFragment())
                    .commit();

        }


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_display_song_list, menu);
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
    public class PlaceholderFragment extends ListFragment implements
            LoaderManager.LoaderCallbacks<Cursor>{

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            // Fields from the database (projection)
            // Must include the _id column for the adapter to work
            String[] from = new String[] { SongTable.COLUMN_SONGNUM, SongTable.COLUMN_TITLE };
            // Fields on the UI to which we map
            int[] to = new int[] { R.id.songNum, R.id.songTitle };

            View rootView = inflater.inflate(R.layout.fragment_display_song_list, container, false);

            getLoaderManager().initLoader(0, null, this);
            adapter = new SimpleCursorAdapter(getApplicationContext(), R.layout.song_row, null, from,
                    to, 0);

            setListAdapter(adapter);


            setRetainInstance(true);
            return rootView;
        }
        // creates a new loader after the initLoader () call
        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            String[] projection = { SongTable.COLUMN_ID, SongTable.COLUMN_SONGNUM, SongTable.COLUMN_TITLE };
            CursorLoader cursorLoader = new CursorLoader(getApplicationContext(),
                    SongBookContentProvider.CONTENT_URI, projection, null, null, null);
            return cursorLoader;
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
            adapter.swapCursor(data);
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {
            // data is not available anymore, delete reference
            adapter.swapCursor(null);
        }

        // Opens the second activity if an entry is clicked
        @Override
        public void onListItemClick(ListView l, View v, int position, long id) {
            super.onListItemClick(l, v, position, id);
            Intent i = new Intent(getApplicationContext(), DisplaySong.class);
            Uri todoUri = Uri.parse(SongBookContentProvider.CONTENT_URI + "/" + id);
            i.putExtra(SongBookContentProvider.CONTENT_ITEM_TYPE, todoUri);

            startActivity(i);
        }
    }
/*    public void DisplaySong(View view){
        Intent intent = new Intent(this, DisplaySong.class);
        TextView textView = (TextView) findViewById( R.id.songTitle);
        intent.putExtra(MainActivity.EXTRA_SONG_TITLE, textView.getText().toString());
        startActivity(intent);
    }
*/
}
