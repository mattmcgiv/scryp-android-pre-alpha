package io.scryp.scryp;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */

public class EthereumService extends IntentService {

    private static final String TAG = "Scryp";

    private static final String INIT_NEW_TRANSACTION = "io.scryp.scryp.action.INIT_NEW_TRANSACTION";
    private static final String SEND_SIGNED_TRANSACTION = "io.scryp.scryp.action.SEND_SIGNED_TRANSACTION";

    public static final String BROADCAST_ACTION = "io.scryp.scryp.action.BROADCAST";
    public static final String RESPONSE_STATUS = "io.scryp.scryp.action.STATUS";

    private static final String INPUT_ADDRESS = "io.scryp.scryp.extra.INPUT_ADDRESS";
    private static final String OUTPUT_ADDRESS = "io.scryp.scryp.extra.OUTPUT_ADDRESS";
    private static final String VALUE = "io.scryp.scryp.extra.VALUE";

    //private static final String BLOCK_CYPHER_TOKEN = new BlockCypher().TOKEN;

    public EthereumService() {
        super("EthereumService");
    }

    /**
     * Starts this service to perform action INIT_NEW_TRANSACTION with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method
    public static void startActionInitNewTransaction(Context context, String inputAddress, String outputAddress, String value) {
        Log.v(TAG, "starting action InitNewTransaction");
        Intent intent = new Intent(context, EthereumService.class);
        intent.setAction(INIT_NEW_TRANSACTION);
        intent.putExtra(INPUT_ADDRESS, inputAddress);
        intent.putExtra(OUTPUT_ADDRESS, outputAddress);
        intent.putExtra(VALUE, value);
        context.startService(intent);
    }

    /**
     * Starts this service to perform action Baz with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    public static void startActionBaz(Context context, String param1, String param2) {
        Intent intent = new Intent(context, EthereumService.class);
        intent.setAction(SEND_SIGNED_TRANSACTION);
        intent.putExtra(INPUT_ADDRESS, param1);
        intent.putExtra(OUTPUT_ADDRESS, param2);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (INIT_NEW_TRANSACTION.equals(action)) {
                Log.v(TAG, "onHandleIntent; INIT_NEW_TRANSACTION");
                final String inputAddress = intent.getStringExtra(INPUT_ADDRESS);
                final String outputAddress = intent.getStringExtra(OUTPUT_ADDRESS);
                final String value = intent.getStringExtra(VALUE);
                handleActionInitNewTransaction(inputAddress, outputAddress, value);
            } else if (SEND_SIGNED_TRANSACTION.equals(action)) {
                final String param1 = intent.getStringExtra(INPUT_ADDRESS);
                final String param2 = intent.getStringExtra(OUTPUT_ADDRESS);
                handleActionBaz(param1, param2);
            }
        }
    }

    /**
     * Handle action InitNewTransaction in the provided background thread with the provided
     * parameters.
     */
    private void handleActionInitNewTransaction(String inputAddress, String outputAddress, String value) {
        new HttpRequestHandler(this);
    }

    //to be called from the HTTPRequestHandler to broadcast an update back to the UI
    protected void handleSuccessfulJsonResponse(String response) {
        Log.v(TAG, "response received...creating localIntent with BROADCAST_ACTION");
        Intent localIntent = new Intent(BROADCAST_ACTION)
                .putExtra(RESPONSE_STATUS, response);
        LocalBroadcastManager.getInstance(this).sendBroadcast(localIntent);
    }

    //to be called from the HTTPRequestHandler to broadcast an error message back to UI
    protected void handleBadJsonResponse(String errorMessage) {
        Log.v(TAG, "creating localIntent with BROADCAST_ACTION");
        Intent localIntent = new Intent(BROADCAST_ACTION)
                .putExtra(RESPONSE_STATUS, "error");
        LocalBroadcastManager.getInstance(this).sendBroadcast(localIntent);
    }


    /**
     * Handle action Baz in the provided background thread with the provided
     * parameters.
     */
    private void handleActionBaz(String param1, String param2) {
        // TODO: Handle action Baz
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
