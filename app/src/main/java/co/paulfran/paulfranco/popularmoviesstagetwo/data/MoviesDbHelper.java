package co.paulfran.paulfranco.popularmoviesstagetwo.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MoviesDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "movies.db";
    private static final int DATABASE_VERSION = 1;

    public MoviesDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String CREATE_TABLE = "CREATE TABLE " + MoviesContract.MoviesEntry.TABLE_NAME + " (" +
                MoviesContract.MoviesEntry._ID + " INTEGER PRIMARY KEY, " +
                MoviesContract.MoviesEntry.COLUMN_ID + " TEXT NOT NULL, " +
                MoviesContract.MoviesEntry.COLUMN_TITLE + " TEXT NOT NULL," +
                MoviesContract.MoviesEntry.COLUMN_OVERVIEW + " TEXT NOT NULL," +
                MoviesContract.MoviesEntry.COLUMN_POSTER_PATH + " TEXT NOT NULL," +
                MoviesContract.MoviesEntry.COLUMN_BACKDROP_PATH + " TEXT NOT NULL," +
                MoviesContract.MoviesEntry.COLUMN_RELEASE_DATE + " TEXT NOT NULL," +
                MoviesContract.MoviesEntry.COLUMN_RUNTIME + " INTEGER NOT NULL," +
                MoviesContract.MoviesEntry.COLUMN_VOTE_AVERAGE + " REAL NOT NULL)" +
                "; ";

        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + MoviesContract.MoviesEntry.TABLE_NAME);
        onCreate(db);
    }

}
