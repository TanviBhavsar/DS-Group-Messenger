package edu.buffalo.cse.cse486586.groupmessenger1;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.telephony.TelephonyManager;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * GroupMessengerActivity is the main Activity for the assignment.
 * 
 * @author stevko
 *
 */
public class GroupMessengerActivity extends Activity implements View.OnClickListener {

    static final String REMOTE_PORT0 = "11108";
    static final String REMOTE_PORT1 = "11112";
    static final String REMOTE_PORT2 = "11116";
    static final String REMOTE_PORT3 = "11120";
    static final String REMOTE_PORT4 = "11124";

    static final int SERVER_PORT = 10000;
    //static int seq_no=0;
    static final String TAG = GroupMessengerActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_messenger);

        /*
         * TODO: Use the TextView to display your messages. Though there is no grading component
         * on how you display the messages, if you implement it, it'll make your debugging easier.
         */
        TextView tv = (TextView) findViewById(R.id.textView1);
        //  if(tv!=null)
        tv.setMovementMethod(new ScrollingMovementMethod());
        
        /*
         * Registers OnPTestClickListener for "button1" in the layout, which is the "PTest" button.
         * OnPTestClickListener demonstrates how to access a ContentProvider.
         */
        findViewById(R.id.button1).setOnClickListener(
                new OnPTestClickListener(tv, getContentResolver()));
        
        /*
         * TODO: You need to register and implement an OnClickListener for the "Send" button.
         * In your implementation you need to get the message from the input box (EditText)
         * and send it to other AVDs.
         */

        /*  Used from PA1
         * Calculate the port number that this AVD listens on.
         * It is just a hack that I came up with to get around the networking limitations of AVDs.
         * The explanation is provided in the PA1 spec.
         */
        TelephonyManager tel = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
        String portStr = tel.getLine1Number().substring(tel.getLine1Number().length() - 4);
        final String myPort = String.valueOf((Integer.parseInt(portStr) * 2));


        try {
            /*
             * Create a server socket as well as a thread (AsyncTask) that listens on the server
             * port.
             *
             * AsyncTask is a simplified thread construct that Android provides. Please make sure
             * you know how it works by reading
             * http://developer.android.com/reference/android/os/AsyncTask.html
             */
            ServerSocket serverSocket = new ServerSocket(SERVER_PORT);
            new ServerTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, serverSocket);
        } catch (IOException e) {
            /*
             * Log is a good way to debug your code. LogCat prints out all the messages that
             * Log class writes.
             *
   -          * Please read http://developer.android.com/tools/debugging/debugging-projects.html
             * and http://developer.android.com/tools/debugging/debugging-log.html
             * for more information on debugging.
             */
            Log.e(TAG, "Can't create a ServerSocket");
            return;
        }




        /*
         * Register an OnKeyListener for the input box. OnKeyListener is an event handler that
         * processes each key event. The purpose of the following code is to detect an enter key
         * press event, and create a client thread so that the client thread can send the string
         * in the input box over the network.
         */
        //used http://stackoverflow.com/questions/17540013/declaring-that-a-class-implements-onclicklistener-vs-declaring-it-yourself

        Button b = (Button) findViewById(R.id.button4);
        b.setOnClickListener(this);

    }

    public void onClick(View arg0) {
        // TODO Auto-generated method stub
        final EditText editText = (EditText) findViewById(R.id.editText1);
        String msg = editText.getText().toString() + "\n";
        editText.setText(""); // This is one way to reset the input box.
        TextView localTextView = (TextView) findViewById(R.id.textView1);
        localTextView.append("\t" + msg); // This is one way to display a string.


                    /*
                    Used from PA1
                     * Note that the following AsyncTask uses AsyncTask.SERIAL_EXECUTOR, not
                     * AsyncTask.THREAD_POOL_EXECUTOR as the above ServerTask does. To understand
                     * the difference, please take a look at
                     * http://developer.android.com/reference/android/os/AsyncTask.html
                     */
        TelephonyManager tel = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
        String portStr = tel.getLine1Number().substring(tel.getLine1Number().length() - 4);
        final String myPort = String.valueOf((Integer.parseInt(portStr) * 2));
        new ClientTask().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, msg, myPort);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_group_messenger, menu);
        return true;
    }

    /**
     * ServerTask is an AsyncTask that should handle incoming messages. It is created by
     * ServerTask.executeOnExecutor() call in SimpleMessengerActivity.
     * <p/>
     * Please make sure you understand how AsyncTask works by reading
     * http://developer.android.com/reference/android/os/AsyncTask.html
     *
     * @author stevko
     */
    private class ServerTask extends AsyncTask<ServerSocket, String, Void> {

        int seq_no=0;
        @Override

        protected Void doInBackground(ServerSocket... sockets) {
            ServerSocket serverSocket = sockets[0];

            /*
             * TODO: Fill in your server code that receives messages and passes them
             * to onProgressUpdate().
             */
            Socket socket_read = null;
            /*used http://docs.oracle.com/javase/tutorial/networking/sockets/readingWriting.html
            http://docs.oracle.com/javase/tutorial/networking/sockets/clientServer.html*/
            while (true) {
                try {
                    socket_read = serverSocket.accept();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                BufferedReader in_socket = null;
                if (socket_read != null) try {
                    in_socket = new BufferedReader(new InputStreamReader(socket_read.getInputStream()));
                } catch (IOException e) {
                    e.printStackTrace();
                }

                try {

                    try {
                        String input_string = in_socket.readLine();
                        //store message in content provider
                        ContentResolver mContentResolver = getContentResolver();
                        ContentValues mContentValues=new ContentValues();
                        //Uri mUri;


                        publishProgress(input_string);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                } finally {
                    try {
                        in_socket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            }
            // return null;
        }

        protected void onProgressUpdate(String... strings) {
            /*
             * The following code displays what is received in doInBackground().
             */
            String strReceived = strings[0].trim();
            TextView remoteTextView = (TextView) findViewById(R.id.textView1);
            remoteTextView.append(strReceived + "\t\n");
        /*TextView localTextView = (TextView) findViewById(R.id.local_text_display);
        localTextView.append("\n");*/

            /*
             * The following code creates a file in the AVD's internal storage and stores a file.
             *
             * For more information on file I/O on Android, please take a look at
             * http://developer.android.com/training/basics/data-storage/files.html
             */


            Uri mUri;
            ContentResolver mContentResolver = getContentResolver();
            ContentValues mContentValues=new ContentValues();
            Uri.Builder uriBuilder = new Uri.Builder();
            uriBuilder.authority("edu.buffalo.cse.cse486586.groupmessenger1.provider");
            uriBuilder.scheme("content");
            mUri= uriBuilder.build();
            mContentValues.put("key", Integer.toString(seq_no));

            mContentValues.put("value", strReceived);
            mContentResolver.insert(mUri, mContentValues);
            seq_no++;

            return;
        }
    }

    /**
     * ClientTask is an AsyncTask that should send a string over the network.
     * It is created by ClientTask.executeOnExecutor() call whenever OnKeyListener.onKey() detects
     * an enter key press event.
     *
     * @author stevko
     */
    private class ClientTask extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... msgs) {



            try {
                String sremotePort, remotePort;
                int irp = 11108;
                for (int i = 0; i < 5; i++) {
                    sremotePort = Integer.toString(irp);
                    irp=irp+4;
               //     if (!msgs[1].equals(sremotePort)) {
                        remotePort = sremotePort;

                        Socket socket = new Socket(InetAddress.getByAddress(new byte[]{10, 0, 2, 2}),
                                Integer.parseInt(remotePort));

                        String msgToSend = msgs[0];
                /*
                 * TODO: Fill in your client code that sends out a message.
                 */

                        //used http://docs.oracle.com/javase/tutorial/networking/sockets/readingWriting.html
                        PrintWriter out =
                                new PrintWriter(socket.getOutputStream(), true);
                        out.println(msgToSend);

                        socket.close();

                    //}

                }
            } catch (UnknownHostException e) {
                Log.e(TAG, "ClientTask UnknownHostException");
            } catch (IOException e) {
                Log.e(TAG, "ClientTask socket IOException");
            }


            return null;

        }
    }
}
