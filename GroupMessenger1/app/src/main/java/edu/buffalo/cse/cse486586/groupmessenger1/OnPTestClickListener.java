package edu.buffalo.cse.cse486586.groupmessenger1;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

/**
 * OnPTestClickListener demonstrates how to access a ContentProvider. First, please read 
 * 
 * http://developer.android.com/guide/topics/providers/content-providers.html
 * http://developer.android.com/reference/android/content/ContentProvider.html
 * 
 * before you start. Please note that our use of a ContentProvider is a bit different from the
 * standard way of using it as described in the PA2 spec. The bottom line is that our
 * ContentProvider does not have full support for SQL. It is just a key-value table, like a hash
 * table. It just needs to be able to insert (key, value) pairs, store them, and return them when
 * queried.
 * 
 * A ContentProvider has a unique URI that other apps use to access it. ContentResolver is
 * the class to use when accessing a ContentProvider.
 * 
 * @author stevko
 *
 */
public class OnPTestClickListener implements OnClickListener {

    private static final String TAG = OnPTestClickListener.class.getName();
    private static final int TEST_CNT = 50;
    private static final String KEY_FIELD = "key";
    private static final String VALUE_FIELD = "value";

    private final TextView mTextView;
    private final ContentResolver mContentResolver;
    private final Uri mUri;
    private final ContentValues[] mContentValues;

    public OnPTestClickListener(TextView _tv, ContentResolver _cr) {
        mTextView = _tv;
        mContentResolver = _cr;
        mUri = buildUri("content", "edu.buffalo.cse.cse486586.groupmessenger1.provider");
        mContentValues = initTestValues();
    }

    /**
     * buildUri() demonstrates how to build a URI for a ContentProvider.
     * 
     * @param scheme
     * @param authority
     * @return the URI
     */
    private Uri buildUri(String scheme, String authority) {
        Uri.Builder uriBuilder = new Uri.Builder();
        uriBuilder.authority(authority);
        uriBuilder.scheme(scheme);
        return uriBuilder.build();
    }

    private ContentValues[] initTestValues() {
        ContentValues[] cv = new ContentValues[TEST_CNT];
        for (int i = 0; i < TEST_CNT; i++) {
            cv[i] = new ContentValues();
            cv[i].put(KEY_FIELD, "key" + Integer.toString(i));
            cv[i].put(VALUE_FIELD, "val" + Integer.toString(i));
        }

        return cv;
    }

    @Override
    public void onClick(View v) {
        //remove later
        //mTextView.append("In onclick\n");
        if (testInsert()) {
            mTextView.append("Insert success\n");
        } else {
            mTextView.append("Insert fail\n");
            return;
        }

        if (testQuery()) {
            mTextView.append("Query success\n");
        } else {
            mTextView.append("Query fail\n");
        }
    }

    /**
     * testInsert() uses ContentResolver.insert() to insert values into your ContentProvider.
     * 
     * @return true if the insertions were successful. Otherwise, false.
     */
    private boolean testInsert() {
        try {
            for (int i = 0; i < TEST_CNT; i++) {
                mContentResolver.insert(mUri, mContentValues[i]);
            }
        } catch (Exception e) {
            Log.e(TAG, e.toString());
            return false;
        }

        return true;
    }
    private boolean mytestInsert() {
        try {
            ContentValues mc=new ContentValues();

            mc.put(KEY_FIELD,"iyzUkRQHBgskTxejzTdzLteiyilRT9oY");
            mc.put(VALUE_FIELD, "310DvwJtFOJ1aApf1z0edg5HHCFvD6ei");
            mContentResolver.insert(mUri, mc);



            mc.put(KEY_FIELD,"1PGlamEKhcBEt3R3dR9bmPlReMEwaqOL");
            mc.put(VALUE_FIELD, "04gFVUvRAYWshCAF0ZhqSD4fGemH7XVg");
            mContentResolver.insert(mUri, mc);

            mc.put(KEY_FIELD,"TufHkgeRCgjj6FYaPFDUNgto948WBTr3");
            mc.put(VALUE_FIELD, "VC0g7HEUVGvkqbIrUh3bJNfGBbJHUEwx");
            mContentResolver.insert(mUri, mc);

            mc.put(KEY_FIELD,"U4ovTmKeNAxeta9xWyVN1SIYMF19W4iu");
            mc.put(VALUE_FIELD, "b5hTX7T6DiSsiTm2hbUy53AeNCpIFxcQ");
            mContentResolver.insert(mUri, mc);




        } catch (Exception e) {
            Log.e(TAG, e.toString());
            return false;
        }

        return true;
    }

    /**
     * testQuery() uses ContentResolver.query() to retrieves values from your ContentProvider.
     * It simply queries one key at a time and verifies whether it matches any (key, value) pair
     * previously inserted by testInsert().
     * 
     * Please pay extra attention to the Cursor object you return from your ContentProvider.
     * It should have two columns; the first column (KEY_FIELD) is for keys 
     * and the second column (VALUE_FIELD) is values. In addition, it should include exactly
     * one row that contains a key and a value.
     * 
     * @return
     */
    private boolean testQuery() {
        try {
            for (int i = 0; i < TEST_CNT; i++) {
                String key = (String) mContentValues[i].get(KEY_FIELD);
                String val = (String) mContentValues[i].get(VALUE_FIELD);

                Cursor resultCursor = mContentResolver.query(mUri, null, key, null, null);
                if (resultCursor == null) {
                    Log.e(TAG, "Result null");
                    throw new Exception();
                }

                int keyIndex = resultCursor.getColumnIndex(KEY_FIELD);
                int valueIndex = resultCursor.getColumnIndex(VALUE_FIELD);
                if (keyIndex == -1 || valueIndex == -1) {
                    Log.e(TAG, "Wrong columns");
                    resultCursor.close();
                    throw new Exception();
                }

                resultCursor.moveToFirst();

                if (!(resultCursor.isFirst() && resultCursor.isLast())) {
                    Log.e(TAG, "Wrong number of rows");
                    resultCursor.close();
                    throw new Exception();
                }

                String returnKey = resultCursor.getString(keyIndex);
                String returnValue = resultCursor.getString(valueIndex);
                if (!(returnKey.equals(key) && returnValue.equals(val))) {
                    Log.e(TAG, "(key, value) pairs don't match\n");
                    resultCursor.close();
                    throw new Exception();
                }

                resultCursor.close();
            }
        } catch (Exception e) {
            return false;
        }

        return true;
    }
    ;


    private boolean mytestQuery() {
        try {
            String[] key = {"iyzUkRQHBgskTxejzTdzLteiyilRT9oY","1PGlamEKhcBEt3R3dR9bmPlReMEwaqOL","TufHkgeRCgjj6FYaPFDUNgto948WBTr3"};
         //   key[0] = "iayzUkRQHBgskTxejzTdzLteiyilRT9oY";
            String[] val = { "310DvwJtFOJ1aApf1z0edg5HHCFvD6ei","04gFVUvRAYWshCAF0ZhqSD4fGemH7XVg","VC0g7HEUVGvkqbIrUh3bJNfGBbJHUEwx"};


            for (int i = 0; i < 3; i++) {
                Cursor resultCursor = mContentResolver.query(mUri, null, key[i], null, null);
                if (resultCursor == null) {
                    Log.e(TAG, "Result null");
                    throw new Exception();
                }

                int keyIndex = resultCursor.getColumnIndex(KEY_FIELD);
                int valueIndex = resultCursor.getColumnIndex(VALUE_FIELD);
                if (keyIndex == -1 || valueIndex == -1) {
                    Log.e(TAG, "Wrong columns");
                    resultCursor.close();
                    throw new Exception();
                }

                resultCursor.moveToFirst();

                if (!(resultCursor.isFirst() && resultCursor.isLast())) {
                    Log.e(TAG, "Wrong number of rows");
                    resultCursor.close();
                    throw new Exception();
                }

                String returnKey = resultCursor.getString(keyIndex);
                String returnValue = resultCursor.getString(valueIndex);
                if (!(returnKey.equals(key[i]) && returnValue.equals(val[i]))) {
                    Log.e(TAG, "(key, value) pairs don't match\n");
                    resultCursor.close();
                    throw new Exception();
                }

                resultCursor.close();
            }
            }
        catch (Exception e) {
            return false;
        }

        return true;
    }
}
