package io.scryp.scryp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class AddressActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address);
        Intent intent = this.getIntent();
        String address = intent.getStringExtra("address");
        TextView tv = (TextView) this.findViewById(R.id.address_tv);
        tv.setText(address);
    }
}
