package io.scryp.scryp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.scrypPreAlphaText);
        setSupportActionBar(toolbar);

        Window window = this.getWindow();

        // clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

        // finally change the color
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.colorBlack));

        TextView balanceText = (TextView) findViewById(R.id.balance);
        balanceText.setText(getString(R.string.scrypSymbol) + String.format(java.util.Locale.US,"%.2f", MockScrypAccount.getInstance().getBalance()));

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
                String balance = (String) extras.getString("balance");
                if (balance != null) {

                    balanceText.setText(balance);
                }
            }
        }
    }
}


