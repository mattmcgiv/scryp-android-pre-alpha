package io.scryp.scryp;

import io.scryp.scryp.BlockCypher;

import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import org.web3j.crypto.Credentials;
import org.web3j.crypto.TransactionEncoder;
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.Web3jFactory;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.request.RawTransaction;
import org.web3j.protocol.core.methods.response.EthGetTransactionCount;
import org.web3j.protocol.core.methods.response.EthGetTransactionReceipt;
import org.web3j.protocol.core.methods.response.EthSendTransaction;
import org.web3j.protocol.core.methods.response.Web3ClientVersion;
import org.web3j.protocol.infura.InfuraHttpService;
import org.web3j.utils.Convert;
import org.web3j.utils.Numeric;

import java.io.File;
import java.math.BigInteger;

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
                Log.v(TAG, "About to load credentials from keystore");
                Log.v(TAG, "About to generate wallet");
                Log.v(TAG, "Getting files dir");

                //Load our wallet
                Log.v(TAG, "Loading wallet");
                Credentials credentials = WalletUtils.loadCredentials("foo", "/data/user/0/io.scryp.scryp/files/UTC--2017-09-13T10-32-42.056--22b07cfd25cf068a444364e8531be5fac8af7ef1.json");

                Log.v(TAG, "Credential address: " + credentials.getAddress());

                //get next available nonce
                Log.v(TAG, "Get nonce");
                EthGetTransactionCount ethGetTransactionCount = web3.ethGetTransactionCount(
                        credentials.getAddress(), DefaultBlockParameterName.LATEST).sendAsync().get();

                BigInteger nonce = ethGetTransactionCount.getTransactionCount();
                Log.v(TAG, "Nonce: " + nonce);


                //convert ether to wei for transaction
                BigInteger value = Convert.toWei(".25", Convert.Unit.ETHER).toBigInteger();

                //create transaction
                Log.v(TAG, "Create transaction");
                RawTransaction rawTransaction  = RawTransaction.createEtherTransaction(
                        nonce, BigInteger.valueOf(500000), BigInteger.valueOf(500000), "0xfb70456839B62ca7bA09a1fA9E5a553E5e36D4c4", value);

                //encode and sign transaction
                Log.v(TAG, "Encode and sign transaction");
                byte[] signedMessage = TransactionEncoder.signMessage(rawTransaction, credentials);
                String hexValue = Numeric.toHexString(signedMessage);

                //send
                Log.v(TAG, "Send transaction");
                EthSendTransaction ethSendTransaction = web3.ethSendRawTransaction(hexValue).sendAsync().get();
                Log.v(TAG, "Get transaction hash");
                String transactionHash = ethSendTransaction.getTransactionHash();
                Log.v(TAG, "" + transactionHash);
                return "OK";
            } catch (Exception e) {
                e.printStackTrace();
                Log.v(TAG, "Exception: " + e.getMessage());
                return "Something went wrong";
            }
        }
        protected void onPostExecute(String result) {
            tv.append(result);
            Log.v(TAG, result);
        }
    }

    protected String createWalletGetFileName() {
        try {
            String fileName = WalletUtils.generateNewWalletFile(
                    "your password",
                    new File("/data/user/0/io.scryp.scryp/files/UTC--2017-09-13T10-32-42.056--22b07cfd25cf068a444364e8531be5fac8af7ef1.json"),
                    true);
            return fileName;
        } catch (Exception e) {
            Log.e(TAG, "Creating wallet file failed: " + e.getMessage());
        }
        return null;
    }
}