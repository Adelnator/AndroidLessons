package ru.ifmo.md.lesson6;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.net.MalformedURLException;
import java.net.URL;


public class MainActivity extends Activity {

    static URL[] addrs;

    static {
        try {
            addrs = new URL[3];
            addrs[0] = new URL("http://feeds.bbci.co.uk/news/rss.xml");
            addrs[1] = new URL("http://echo.msk.ru/interview/rss-fulltext.xml");
            addrs[2] = new URL("http://bash.im/rss/");
        } catch (MalformedURLException ignore) {
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getLoaderManager().initLoader(0, null, new LoaderManager.LoaderCallbacks<Cursor>() {
            @Override
            public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
                return new CursorLoader(
                        getApplicationContext(),
                        ru.ifmo.md.lesson6.RssContentProvider.URI_FEED_DIR,
                        new String[]{ru.ifmo.md.lesson6.RssDatabase.Structure.FEEDS_COLUMN_NAME, ru.ifmo.md.lesson6.RssDatabase.Structure.FEEDS_COLUMN_DESCRIPTION, ru.ifmo.md.lesson6.RssDatabase.Structure.FEEDS_COLUMN_URL, ru.ifmo.md.lesson6.RssDatabase.Structure.COLUMN_ROWID, ru.ifmo.md.lesson6.RssDatabase.Structure.COLUMN_ROWID_QUERY},
                        null,
                        null,
                        null);
            }

            @Override
            public void onLoadFinished(Loader<Cursor> objectLoader, Cursor o) {
                ((CursorAdapter) ((ListView) findViewById(R.id.listViewFeeds)).getAdapter()).swapCursor(o);
            }

            @Override
            public void onLoaderReset(Loader<Cursor> objectLoader) {

            }
        });

        ((ListView) findViewById(R.id.listViewFeeds)).setAdapter(new FeedListAdapter(this, null, true));
        TextView w = new TextView(this);
        w.setText(getString(R.string.no_feeds));
        ((ListView) findViewById(R.id.listViewFeeds)).setEmptyView(w);

        ru.ifmo.md.lesson6.RssLoaderService.startActionGetAll(getApplicationContext());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_reload_add, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_reload) {
            ru.ifmo.md.lesson6.RssLoaderService.startActionGetAll(getApplicationContext());
        } else if (id == R.id.action_add) {
            AlertDialog.Builder alert = new AlertDialog.Builder(this);
            final EditText input = new EditText(this);
            input.setInputType(InputType.TYPE_TEXT_VARIATION_URI);
            alert.setView(input);
            String out;
            alert.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Editable value = input.getText();
                    String url = value.toString();
                    try {
                        new URL(url);
                    } catch (MalformedURLException e) {
                        Toast.makeText(MainActivity.this, getString(R.string.invalid_url_entered), Toast.LENGTH_SHORT).show();
                        return;
                    }
                    ContentValues values = new ContentValues();
                    values.put(ru.ifmo.md.lesson6.RssDatabase.Structure.FEEDS_COLUMN_NAME, "Loading...");
                    values.put(ru.ifmo.md.lesson6.RssDatabase.Structure.FEEDS_COLUMN_DESCRIPTION, "");
                    values.put(ru.ifmo.md.lesson6.RssDatabase.Structure.FEEDS_COLUMN_URL, url);
                    Uri uri = getContentResolver().insert(ru.ifmo.md.lesson6.RssContentProvider.URI_FEED_DIR, values);
                    String insertedId = uri.getLastPathSegment();
                    getContentResolver().notifyChange(ru.ifmo.md.lesson6.RssContentProvider.URI_FEED_DIR, null);
                    ru.ifmo.md.lesson6.RssLoaderService.startActionGetSingle(getApplicationContext(), (int) Long.parseLong(insertedId));
                }
            });
            alert.setNegativeButton(R.string.cancel, null);
            alert.show();
        }
        return super.onOptionsItemSelected(item);
    }

    class FeedListAdapter extends CursorAdapter {
        public FeedListAdapter(Context context, Cursor c, boolean autoRequery) {
            super(context, c, autoRequery);
        }

        @Override
        public View newView(Context context, final Cursor cursor, ViewGroup viewGroup) {
            View w = LayoutInflater.from(MainActivity.this).inflate(R.layout.layout_news_entry, null);
            ((TextView) w.findViewById(R.id.textHeading)).setText(cursor.getString(cursor.getColumnIndex(ru.ifmo.md.lesson6.RssDatabase.Structure.FEEDS_COLUMN_NAME)));
            ((TextView) w.findViewById(R.id.textDescription)).setText(Html.fromHtml(cursor.getString(cursor.getColumnIndex(ru.ifmo.md.lesson6.RssDatabase.Structure.FEEDS_COLUMN_DESCRIPTION))));
            final int id = cursor.getInt(cursor.getColumnIndex(ru.ifmo.md.lesson6.RssDatabase.Structure.COLUMN_ROWID_AFTER_QUERY));
            w.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent it = new Intent(MainActivity.this, ru.ifmo.md.lesson6.RssActivity.class);
                    it.putExtra("feedId", id);
                    MainActivity.this.startActivity(it);
                }
            });
            return w;
        }

        @Override
        public void bindView(View view, Context context, final Cursor cursor) {
            View w = view;
            ((TextView) w.findViewById(R.id.textHeading)).setText(cursor.getString(cursor.getColumnIndex(ru.ifmo.md.lesson6.RssDatabase.Structure.FEEDS_COLUMN_NAME)));
            ((TextView) w.findViewById(R.id.textDescription)).setText(Html.fromHtml(cursor.getString(cursor.getColumnIndex(ru.ifmo.md.lesson6.RssDatabase.Structure.FEEDS_COLUMN_DESCRIPTION))));
            final int id = cursor.getInt(cursor.getColumnIndex(ru.ifmo.md.lesson6.RssDatabase.Structure.COLUMN_ROWID_AFTER_QUERY));
            w.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent it = new Intent(MainActivity.this, ru.ifmo.md.lesson6.RssActivity.class);
                    it.putExtra("feedId", id);
                    MainActivity.this.startActivity(it);
                }
            });
        }
    }
}
