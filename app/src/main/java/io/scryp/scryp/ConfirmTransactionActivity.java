package io.scryp.scryp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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
        toolbar.setTitle(R.string.confirmPaymentText);
        setSupportActionBar(toolbar);

        String qrContent;
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                qrContent = null;
                //TODO dev only remove this
                qrContent = "{\n" +
                        "   \"deal\": {\n" +
                        "       \"id\": \"123xyz\",\n" +
                        "       \"total\": \"4.50\",\n" +
                        "       \"usd_amount\": \"2.50\",\n" +
                        "       \"scryp_amount\": \"2.00\",\n" +
                        "       \"items\": {\n" +
                        "           \"item_0\": {\n" +
                        "               \"name\": \"16oz. Latte\"\n" +
                        "           }\n" +
                        "       }\n" +
                        "   },\n" +
                        "   \"recipient\": {\n" +
                        "       \"id\": \"456zyx\",\n" +
                        "       \"name\": \"Local Coffee Co.\"\n" +
                        "   }\n" +
                        "}";
                updateView(qrContent);
            } else {
                qrContent = extras.getString("qrContent");
                updateView(qrContent);
            }
        } else {
            qrContent= (String) savedInstanceState.getSerializable("qrContent");
            Toast.makeText(this, "Scanned: " + qrContent, Toast.LENGTH_LONG).show();
        }

        Button payButton = (Button) findViewById(R.id.payButton);
        Button canxButton = (Button) findViewById(R.id.cancelButton);

        payButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), TransactionCompleteActivity.class);
                v.getContext().startActivity(intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            }
        });

        canxButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                CharSequence cs = getString(R.string.dealScanCanceled);
                Intent intent = new Intent(v.getContext(), MainActivity.class);
                intent.putExtra("message", cs);
                v.getContext().startActivity(intent);
            }
        });
    }

    private JSONObject getJSONObj (String json)throws JSONException {
        return (JSONObject) new JSONTokener(json).nextValue();
    }

    private void updateView(String qrContent) {
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

}
