package com.trevorhalvorson.rpiledsample;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = MainActivity.class.getSimpleName();

    // Replace X.X.X.X with the IP address of your Pi
    private static final String ENDPOINT = "http://X.X.X.X:5000/led/api/v1.0/morse/";
    private static final String JSON_OBJECT = "code";

    private URL mURL;
    private EditText mMsgEditText;
    private TextView mCodeText;
    private ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mMsgEditText = (EditText) findViewById(R.id.msg_edit_text);
        mCodeText = (TextView) findViewById(R.id.code_text_view);
        mProgressBar = (ProgressBar) findViewById(R.id.progress_bar);

        Button sendButton = (Button) findViewById(R.id.send_button);
        sendButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        try {
            mURL = new URL(ENDPOINT + mMsgEditText.getText().toString());
            new MessageTask().execute();
        } catch (MalformedURLException e) {
            Log.e(TAG, e.getMessage());
        }
    }

    private class MessageTask extends AsyncTask<Void, Void, String> {

        @Override
        protected void onPreExecute() {
            mProgressBar.setVisibility(View.VISIBLE);
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... params) {
            try {
                HttpURLConnection urlConnection = (HttpURLConnection) mURL.openConnection();
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                StringBuilder builder = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    builder.append(line);
                }
                try {
                    JSONObject jsonObject = new JSONObject(builder.toString());
                    return jsonObject.getString(JSON_OBJECT);
                } catch (JSONException e) {
                    Log.e(TAG, e.getMessage());
                } finally {
                    urlConnection.disconnect();
                }
            } catch (IOException e) {
                Log.e(TAG, e.getMessage());
            }
            return "error";
        }

        @Override
        protected void onPostExecute(String result) {
            mProgressBar.setVisibility(View.GONE);
            mCodeText.setText(result);
            super.onPostExecute(result);
        }
    }
}
