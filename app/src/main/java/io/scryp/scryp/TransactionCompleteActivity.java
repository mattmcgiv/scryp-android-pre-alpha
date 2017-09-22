package io.scryp.scryp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class TransactionCompleteActivity extends AppCompatActivity {

    float transactionAmount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_complete);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.paymentStatus);
        setSupportActionBar(toolbar);
        hideSuccessContent();

        Intent i = getIntent();
        transactionAmount = i.getFloatExtra("scrypPrice", 0);

        Button homeButton = (Button) findViewById(R.id.homeButton);
        homeButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), MainActivity.class);
                v.getContext().startActivity(intent);
            }
        });
    }

    private void hideSuccessContent() {
        View v = findViewById(R.id.content_transaction_complete);
        v.setVisibility(View.GONE);
    }

    private void showSuccessContent() {
        View v = findViewById(R.id.content_transaction_complete);
        TextView debited = (TextView) findViewById(R.id.amountText);
        Resources res = getResources();
        String debitedText = res.getString(R.string.scryp_amount_text, String.valueOf(transactionAmount));
        debited.setText(debitedText);
        v.setVisibility(View.VISIBLE);
    }
}
