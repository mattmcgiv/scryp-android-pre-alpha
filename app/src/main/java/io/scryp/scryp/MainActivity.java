package io.scryp.scryp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.web3j.crypto.WalletUtils;

import java.io.File;

public class MainActivity extends AppCompatActivity {
    public static final String PREFS_NAME = "MyPrefsFile";
    public static final String TAG = "Scryp";
    public static final String WALLET_PATH = "io.scryp.scryp.info.wallet_path";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.scrypPreAlphaText);
        setSupportActionBar(toolbar);

        String walletPath = null;

        //Load the wallet path from SharedStorage
        walletPath = getWalletPath();

//        walletPath = null;
        //If the wallet path couldn't resolve, create a new wallet
        if (walletPath == null) {
            startActivity(new Intent(this, NewWalletActivity.class));
        }

        // Restore preferences
        // Start intentservice to get balance of address from blockchain
        //TODO get wallet path
        EthereumService.startActionGetBalance(this, walletPath);
        // Load wallet
        // Get balance of address
        // Update balance variable
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        float balance = settings.getFloat("scrypBalance", 44);

        ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar1);
        progressBar.setVisibility(View.VISIBLE);

        Window window = this.getWindow();

        // clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

        // finally change the color
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.colorBlack));

        Button scanADealButton = (Button) findViewById(R.id.scanADeal);
        scanADealButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), QrActivity.class);
                v.getContext().startActivity(intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            }
        });

        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras != null) {
                String message = (String) extras.getString("message");
                if (message != null) {
                    CoordinatorLayout cl = (CoordinatorLayout) findViewById(R.id.mainActivityCoordLayout);
                    Snackbar sb = Snackbar.make(cl, message, Snackbar.LENGTH_SHORT);
                    sb.show();
                }
                //balanceTextView.setText(R.string.scrypSymbol + String.valueOf(balance));
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_replenish:
                replenishBalance();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void replenishBalance() {
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putFloat("scrypBalance", 99);
        // Commit the edits!
        editor.commit();
        TextView balanceTextView = (TextView) findViewById(R.id.balance);
        Resources res = getResources();
        String balanceText = res.getString(R.string.scryp_amount_text, 99f);
        balanceTextView.setText(balanceText);
    }

    public static String formatScrypBalance(String balance) {
        return "$c" + balance;
    }

    public String getWalletPath() {
        SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        return sharedPref.getString(WALLET_PATH, null);
    }
}


