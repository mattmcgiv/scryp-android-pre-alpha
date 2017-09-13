package io.scryp.scryp;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import org.web3j.protocol.Web3j;
import org.web3j.protocol.Web3jFactory;
import org.web3j.protocol.core.methods.response.Web3ClientVersion;
import org.web3j.protocol.infura.InfuraHttpService;

public class Web3JDemo extends AppCompatActivity {

    static final String TAG = "Scryp";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v(TAG, "Setting content view");
        setContentView(R.layout.activity_web3_jdemo);
        Log.v(TAG, "Init web3j object");
        new Web3JAsyncTask().execute("Yasssss");
    }

    class Web3JAsyncTask extends AsyncTask<String, Void, String > {
        final String TAG = "Scryp";
        TextView tv = null;

        protected void onPreExecute() {
            tv = (TextView) Web3JDemo.this.findViewById(R.id.web3tv0);
        }
        protected String doInBackground(String... stuff) {
            Log.v(TAG, "In doInBackground method of Web3JAsyncTask");
            try {
                Log.v(TAG, "Get client version");
                Web3j web3 = Web3jFactory.build(new InfuraHttpService("https://rinkeby.infura.io/G62Hvutd5I3GgmRSql0E"));
                Web3ClientVersion web3ClientVersion = web3.web3ClientVersion().sendAsync().get();
                String clientVersion = web3ClientVersion.getWeb3ClientVersion();
                Log.v(TAG, "Setting text view text");
                return clientVersion;
            } catch (Exception e) {
                Log.v(TAG, e.getMessage());
                return "Something went wrong";
            }
        }
        protected void onPostExecute(String result) {
            tv.append(result);
            Log.v(TAG, result);
        }

    }
}