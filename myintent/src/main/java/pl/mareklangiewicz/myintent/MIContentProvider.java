package pl.mareklangiewicz.myintent;

import android.app.SearchManager;
import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.provider.BaseColumns;
import android.support.annotation.NonNull;

public class MIContentProvider extends ContentProvider {


    private MIDBHelper mMIDBHelper;

    static final UriMatcher sUriMatcher = buildUriMatcher();

    static final int MATCH_CMD_RECENT_DIR = 101;
    static final int MATCH_CMD_RECENT_ITEM = 102;
    static final int MATCH_CMD_EXAMPLE_DIR = 201;
    static final int MATCH_CMD_EXAMPLE_ITEM = 202;
    static final int MATCH_CMD_SUGGEST_DIR_ALL = 305;
    static final int MATCH_CMD_SUGGEST_DIR_LIKE = 306;
    static final int MATCH_RULE_USER_DIR = 401;
    static final int MATCH_RULE_USER_ITEM = 402;

    static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        matcher.addURI(MIContract.INSTANCE.getAUTH(), MIContract.CmdRecent.INSTANCE.getPATH(), MATCH_CMD_RECENT_DIR);
        matcher.addURI(MIContract.INSTANCE.getAUTH(), MIContract.CmdRecent.INSTANCE.getPATH() + "/#", MATCH_CMD_RECENT_ITEM);
        matcher.addURI(MIContract.INSTANCE.getAUTH(), MIContract.CmdExample.INSTANCE.getPATH(), MATCH_CMD_EXAMPLE_DIR);
        matcher.addURI(MIContract.INSTANCE.getAUTH(), MIContract.CmdExample.INSTANCE.getPATH() + "/#", MATCH_CMD_EXAMPLE_ITEM);
        matcher.addURI(MIContract.INSTANCE.getAUTH(), MIContract.CmdSuggest.INSTANCE.getPATH(), MATCH_CMD_SUGGEST_DIR_ALL);
        matcher.addURI(MIContract.INSTANCE.getAUTH(), MIContract.CmdSuggest.INSTANCE.getPATH() + "/*", MATCH_CMD_SUGGEST_DIR_LIKE);
        matcher.addURI(MIContract.INSTANCE.getAUTH(), MIContract.RuleUser.INSTANCE.getPATH(), MATCH_RULE_USER_DIR);
        matcher.addURI(MIContract.INSTANCE.getAUTH(), MIContract.RuleUser.INSTANCE.getPATH() + "/#", MATCH_RULE_USER_ITEM);
        return matcher;
    }

    public MIContentProvider() {
    }

    @Override
    public boolean onCreate() {
        mMIDBHelper = new MIDBHelper(getContext());
        return true;
    }

    // No need to call this method. This to assist the testing framework in running smoothly.
    // http://developer.android.com/reference/android/content/ContentProvider.html#shutdown()
    @Override
    public void shutdown() {
        mMIDBHelper.close();
        super.shutdown();
    }


    @Override
    public synchronized String getType(@NonNull Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch(match) {
            case MATCH_CMD_RECENT_DIR:
                return MIContract.CmdRecent.INSTANCE.getTYPE_DIR();
            case MATCH_CMD_RECENT_ITEM:
                return MIContract.CmdRecent.INSTANCE.getTYPE_ITEM();
            case MATCH_CMD_EXAMPLE_DIR:
                return MIContract.CmdExample.INSTANCE.getTYPE_DIR();
            case MATCH_CMD_EXAMPLE_ITEM:
                return MIContract.CmdExample.INSTANCE.getTYPE_ITEM();
            case MATCH_CMD_SUGGEST_DIR_ALL:
            case MATCH_CMD_SUGGEST_DIR_LIKE:
                return MIContract.CmdSuggest.INSTANCE.getTYPE_DIR();
            case MATCH_RULE_USER_DIR:
                return MIContract.RuleUser.INSTANCE.getTYPE_DIR();
            case MATCH_RULE_USER_ITEM:
                return MIContract.RuleUser.INSTANCE.getTYPE_ITEM();
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public synchronized Cursor query(@NonNull Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        SQLiteDatabase db = mMIDBHelper.getReadableDatabase();
        final int match = sUriMatcher.match(uri);

        String limit = uri.getQueryParameter("limit");

        switch(match) {
            case MATCH_CMD_RECENT_ITEM: // WARNING: selection and selectionArgs are ignored in this case!
                selection = " " + BaseColumns._ID + " = ? ";
                selectionArgs = new String[] {uri.getLastPathSegment()};
            case MATCH_CMD_RECENT_DIR:
                return db.query(MIContract.CmdRecent.INSTANCE.getTABLE_NAME(), projection, selection, selectionArgs, null, null, sortOrder, limit);
            case MATCH_CMD_EXAMPLE_ITEM:// WARNING: selection and selectionArgs are ignored in this case!
                selection = " " + BaseColumns._ID + " = ? ";
                selectionArgs = new String[] {uri.getLastPathSegment()};
            case MATCH_CMD_EXAMPLE_DIR:
                return db.query(MIContract.CmdExample.INSTANCE.getTABLE_NAME(), projection, selection, selectionArgs, null, null, sortOrder, limit);
            case MATCH_CMD_SUGGEST_DIR_LIKE:// WARNING: selection and selectionArgs and sortOrder are ignored in this case!
                selection = SearchManager.SUGGEST_COLUMN_TEXT_1 + " LIKE ? ";
                selectionArgs = new String[] {"%" + Uri.decode(uri.getLastPathSegment()) + "%"};
            case MATCH_CMD_SUGGEST_DIR_ALL:// WARNING: sortOrder is ignored in this case!
                sortOrder = " " + MIContract.CmdSuggest.INSTANCE.getCOL_PRIORITY() + " DESC , " + MIContract.CmdSuggest.INSTANCE.getCOL_TIME() + " DESC ";
                return db.query(MIContract.CmdSuggest.INSTANCE.getTABLE_NAME(), projection, selection, selectionArgs, null, null, sortOrder, limit);
            case MATCH_RULE_USER_ITEM:// WARNING: selection and selectionArgs are ignored in this case!
                selection = " " + BaseColumns._ID + " = ? ";
                selectionArgs = new String[] {uri.getLastPathSegment()};
            case MATCH_RULE_USER_DIR:
                return db.query(MIContract.RuleUser.INSTANCE.getTABLE_NAME(), projection, selection, selectionArgs, null, null, sortOrder, limit);
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public synchronized Uri insert(@NonNull Uri uri, ContentValues values) {

        SQLiteDatabase db = mMIDBHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        String table;
        switch(match) {
            case MATCH_CMD_RECENT_DIR:
                table = MIContract.CmdRecent.INSTANCE.getTABLE_NAME();
                break;
            case MATCH_CMD_EXAMPLE_DIR:
                table = MIContract.CmdExample.INSTANCE.getTABLE_NAME();
                break;
            case MATCH_RULE_USER_DIR:
                table = MIContract.RuleUser.INSTANCE.getTABLE_NAME();
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        long id = db.insert(table, null, values);
        if(id > 0)
            return ContentUris.withAppendedId(uri, id);
        else
            throw new android.database.SQLException("Failed to insert row into " + uri);
    }

    @Override
    public synchronized int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {

        SQLiteDatabase db = mMIDBHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);

        switch(match) {
            case MATCH_CMD_RECENT_ITEM:
                selection = " " + BaseColumns._ID + " = ? ";
                selectionArgs = new String[] {uri.getLastPathSegment()};
            case MATCH_CMD_RECENT_DIR:
                return db.delete(MIContract.CmdRecent.INSTANCE.getTABLE_NAME(), selection, selectionArgs);
            case MATCH_CMD_EXAMPLE_ITEM:
                selection = " " + BaseColumns._ID + " = ? ";
                selectionArgs = new String[] {uri.getLastPathSegment()};
            case MATCH_CMD_EXAMPLE_DIR:
                return db.delete(MIContract.CmdExample.INSTANCE.getTABLE_NAME(), selection, selectionArgs);
            case MATCH_RULE_USER_ITEM:
                selection = " " + BaseColumns._ID + " = ? ";
                selectionArgs = new String[] {uri.getLastPathSegment()};
            case MATCH_RULE_USER_DIR:
                return db.delete(MIContract.RuleUser.INSTANCE.getTABLE_NAME(), selection, selectionArgs);
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public synchronized int update(@NonNull Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        SQLiteDatabase db = mMIDBHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);

        switch(match) {
            case MATCH_CMD_RECENT_ITEM:
                selection = " " + BaseColumns._ID + " = ? ";
                selectionArgs = new String[] {uri.getLastPathSegment()};
            case MATCH_CMD_RECENT_DIR:
                return db.update(MIContract.CmdRecent.INSTANCE.getTABLE_NAME(), values, selection, selectionArgs);
            case MATCH_CMD_EXAMPLE_ITEM:
                selection = " " + BaseColumns._ID + " = ? ";
                selectionArgs = new String[] {uri.getLastPathSegment()};
            case MATCH_CMD_EXAMPLE_DIR:
                return db.update(MIContract.CmdExample.INSTANCE.getTABLE_NAME(), values, selection, selectionArgs);
            case MATCH_RULE_USER_ITEM:
                selection = " " + BaseColumns._ID + " = ? ";
                selectionArgs = new String[] {uri.getLastPathSegment()};
            case MATCH_RULE_USER_DIR:
                return db.update(MIContract.RuleUser.INSTANCE.getTABLE_NAME(), values, selection, selectionArgs);
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }
}

