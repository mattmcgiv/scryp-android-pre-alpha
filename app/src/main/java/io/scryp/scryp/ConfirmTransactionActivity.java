package io.scryp.scryp;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

public class ConfirmTransactionActivity extends AppCompatActivity {
    private static final String TAG = "CnfrmTrnsActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_transaction);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        String qrContent;
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                qrContent = null;
            } else {
                qrContent = extras.getString("qrContent");
                //Log.v(TAG, qrContent);
                try {
                    JSONObject qrJSON = getJSONObj(qrContent);

                    //Set recipient name in UI
                    TextView recipientTxtVw = (TextView) findViewById(R.id.recipient_name);
                    JSONObject recipientJSON = new JSONObject(qrJSON.getString("recipient"));
                    recipientTxtVw.setText(recipientJSON.getString("name"));

                    //Set item name in UI
                    TextView itemTxtVw = (TextView) findViewById(R.id.item_description);
                    JSONObject dealJSON = new JSONObject(qrJSON.getString("deal"));
                    JSONObject itemsJSON = new JSONObject(dealJSON.getString("items"));
                    JSONObject firstItemJSON = new JSONObject(itemsJSON.getString("item_0"));
                    String item_name = firstItemJSON.getString("name");
                    itemTxtVw.setText(item_name);

                    //Set total price in UI
                    TextView totalPriceTxtVw = (TextView) findViewById(R.id.total_price);
                    String total = dealJSON.getString("total");
                    totalPriceTxtVw.setText("$" + total);

                    //Set Scryp deal in UI
                    TextView dealTxtVw = (TextView) findViewById(R.id.scryp_deal_description);
                    String dealText = "$" + dealJSON.getString("usd_amount")
                            + " + "
                            + "$c" + dealJSON.getString("scryp_amount");
                    dealTxtVw.setText(dealText);

                } catch (JSONException je) {
                    Log.v(TAG, "JSONException:: " + je);
                }

            }
        } else {
            qrContent= (String) savedInstanceState.getSerializable("qrContent");
            Toast.makeText(this, "Scanned: " + qrContent, Toast.LENGTH_LONG).show();
        }
    }

    private JSONObject getJSONObj (String json)throws JSONException {
        return (JSONObject) new JSONTokener(json).nextValue();
    }

}
