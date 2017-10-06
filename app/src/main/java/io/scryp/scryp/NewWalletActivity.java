package io.scryp.scryp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import org.w3c.dom.Text;
import org.web3j.crypto.WalletUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class NewWalletActivity extends AppCompatActivity {
    private static final String TAG = "Scryp";
    private static final String PATH = "/data/user/0/io.scryp.scryp/files/";
    private static Map<String,String> wallets = new HashMap<String, String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //wallets.put("path/to/wallet.json","password");
        wallets.put("/data/user/0/io.scryp.scryp/files/UTC--2017-09-13T10-32-42.056--22b07cfd25cf068a444364e8531be5fac8af7ef1.json",
                    "foo");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_wallet);
        Toolbar toolbar = (Toolbar) findViewById(R.id.newWalletToolbar);
        setSupportActionBar(toolbar);
        TextView instructions = (TextView) findViewById(R.id.new_wallet_text);
        instructions.setText(R.string.new_wallet_text);
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        EditText et = (EditText) findViewById(R.id.walletPasswordInput);
        imm.showSoftInput(et, InputMethodManager.SHOW_IMPLICIT);
        et.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    String password = v.getText().toString();
                    createWalletThenLoadMain(password);
                }
                return handled;
            }
        });
    }

    private void createWalletThenLoadMain(String password) {
        Log.v(TAG, "Creating wallet with password:: " + password);
        String fileName = createWalletGetFileName(password);
        Log.v(TAG, "Wallet created at:: " + fileName);
        setWalletCredentials(PATH + fileName, password);
        launchMainActivity();
    }

    protected String createWalletGetFileName(String password) {
        try {
            String fileName = WalletUtils.generateNewWalletFile(
                    password,
                    new File("/data/user/0/io.scryp.scryp/files/"),
                    true);
            return fileName;
        } catch (Exception e) {
            Log.e(TAG, "Creating wallet file failed: " + e.getMessage());
        }
        return null;
    }

    public void launchMainActivity() {
        startActivity(new Intent(this, MainActivity.class));
    }

    public void setWalletCredentials(String path, String password) {
        SharedPreferences sharedPref = getApplicationContext()
                .getSharedPreferences("walletInfo", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(MainActivity.WALLET_PATH, path);
        editor.putString(MainActivity.WALLET_PASSWORD, password);
        editor.commit();
    }
}
