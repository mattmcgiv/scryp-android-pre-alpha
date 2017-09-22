package io.scryp.scryp;

import android.app.Activity;
import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.TextView;

import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Utf8String;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.abi.datatypes.generated.Uint8;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.TransactionEncoder;
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.Web3jFactory;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.request.RawTransaction;
import org.web3j.protocol.core.methods.response.EthGetTransactionCount;
import org.web3j.protocol.core.methods.response.EthSendTransaction;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.infura.InfuraHttpService;
import org.web3j.utils.Convert;
import org.web3j.utils.Numeric;

import java.io.File;
import java.math.BigInteger;
import java.util.concurrent.Future;

import static io.scryp.scryp.MainActivity.formatScrypBalance;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */

public class EthereumService {

    private static final String TAG = "Scryp";

    public static final String GET_BALANCE = "io.scryp.scryp.action.GET_BALANCE";

    public static final String BROADCAST_ACTION = "io.scryp.scryp.action.BROADCAST";
    public static final String RESPONSE_STATUS = "io.scryp.scryp.action.STATUS";

    private static final String INPUT_ADDRESS = "io.scryp.scryp.extra.INPUT_ADDRESS";
    private static final String OUTPUT_ADDRESS = "io.scryp.scryp.extra.OUTPUT_ADDRESS";
    private static final String VALUE = "io.scryp.scryp.extra.VALUE";
    public static final String ETH_METHOD = "io.scryp.scryp.extra.ETH_METHOD" ;

    //private static final String BLOCK_CYPHER_TOKEN = new BlockCypher().TOKEN;

    public static void startActionGetBalance(Activity a) {
        new Web3JAsyncTask(a).execute(GET_BALANCE);
    }
}

class Web3JAsyncTask extends AsyncTask<String, Void, BigInteger > {
    public final Address merchant = new Address("0xfb70456839B62ca7bA09a1fA9E5a553E5e36D4c4");
    final String TAG = "Scryp";
    private Activity a;
    static final BigInteger GAS_PRICE = BigInteger.valueOf(20_000_000_000L);
    static final BigInteger GAS_LIMIT = BigInteger.valueOf(4_300_000);

    public Web3JAsyncTask(Activity a) {
        this.a = a;
    }

    protected BigInteger doInBackground(String... stuff) {
        Log.v(TAG, "In doInBackground method of Web3JAsyncTask");
        Web3j web3 = Web3jFactory.build(new InfuraHttpService("https://kovan.infura.io/G62Hvutd5I3GgmRSql0E"));
        try {
            //Load our wallet
            Log.v(TAG, "Loading wallet");
            Credentials credentials = WalletUtils.loadCredentials("foo", "/data/user/0/io.scryp.scryp/files/UTC--2017-09-13T10-32-42.056--22b07cfd25cf068a444364e8531be5fac8af7ef1.json");
            Log.v(TAG, "Credential address: " + credentials.getAddress());

            //A nonce is required for transactions on the blockchain
            BigInteger nonce = getNonce(web3, credentials);

            //convert ether to wei for transaction
            BigInteger value = Convert.toWei(".15", Convert.Unit.ETHER).toBigInteger();

            Address contractAddress = new Address("0x805a3a39681762860460b60d2de7e94841723c3f");
            Scryp_sol_ScrypTestflight contract = Scryp_sol_ScrypTestflight.load(
                    contractAddress.toString(),
                    web3,
                    credentials,
                    GAS_PRICE,
                    GAS_LIMIT
            );
            Address mintAddress = new Address(credentials.getAddress());

            //Transfers 1 Scryp from "Mint" to "Merchant"
            if (stuff[0].equals("transfer")) {

                BigInteger merchantBalance = getScrypBalance(contract, merchant);
                Log.v(TAG, "Merchant balance: " + merchantBalance.toString());
                transferScryp(contract, merchant, new Uint256(1));
                BigInteger mintBalanceAfter = getScrypBalance(contract, mintAddress);
                Log.v(TAG, "Mint balance: " + mintBalanceAfter.toString());
                BigInteger merchantBalanceAfter = getScrypBalance(contract, merchant);
                Log.v(TAG, "Merchant balance: " + merchantBalanceAfter.toString());
                return BigInteger.valueOf(0);
            }
            else if (stuff[0].equals(EthereumService.GET_BALANCE)) {
                Log.v(TAG, "Getting balance");
                BigInteger mintBalance = getScrypBalance(contract, mintAddress);
                Log.v(TAG, "Mint balance: " + mintBalance.toString());
                return mintBalance;
            }
            //Deploys contract to blockchain
            else if (false) {
                Future<Scryp_sol_ScrypTestflight> deployableContract = Scryp_sol_ScrypTestflight.deploy(
                        web3,
                        credentials,
                        GAS_PRICE,
                        GAS_LIMIT,
                        BigInteger.valueOf(0),
                        new Uint256(1000),
                        new Utf8String("ScrypTestflight01"),
                        new Uint8(2),
                        new Utf8String("ScrT"),
                        new Address(credentials.getAddress())
                );
                Log.v(TAG, "Contract address is: " + deployableContract.get().getContractAddress());
            }
            //Raw transfer of Ethereum
            else if (false) {
                //create transaction
                Log.v(TAG, "Create transaction");
                RawTransaction rawTransaction  = RawTransaction.createEtherTransaction(
                        nonce, GAS_PRICE, GAS_LIMIT, merchant.toString(), value);

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
            }

        } catch (Exception e) {
            e.printStackTrace();
            Log.v(TAG, "Exception: " + e.getMessage());
        }
        return null;
    }

    private BigInteger getNonce(Web3j web3, Credentials credentials) {
        //get next available nonce
        Log.v(TAG, "Get nonce");
        BigInteger nonce = BigInteger.valueOf(0);
        try {
            EthGetTransactionCount ethGetTransactionCount = web3.ethGetTransactionCount(
                    credentials.getAddress(), DefaultBlockParameterName.PENDING).sendAsync().get();
            nonce = ethGetTransactionCount.getTransactionCount();
        } catch (Exception e) {
            Log.e(TAG, "Problem getting nonce.");
        }
        Log.v(TAG, "Nonce: " + nonce);
        return nonce;
    }

    private BigInteger getScrypBalance(Scryp_sol_ScrypTestflight contract, Address address) {
        BigInteger bal = BigInteger.valueOf(0);
        Future<Uint256> balFuture = contract.balanceOf(address);

        try {
            bal = balFuture.get().getValue();
        } catch (Exception e) {
            Log.e(TAG, "Problem getting address Scryp balance.");
        } finally {
            return bal;
        }
    }

    private void transferScryp(Scryp_sol_ScrypTestflight contract, Address _to, Uint256 _value) {
        Future<TransactionReceipt> trFuture = contract.transfer(_to, _value);
        try {
            TransactionReceipt tr = trFuture.get();
        } catch (Exception e) {
            Log.e(TAG, "Problem transferring Scryp.");
        }
    }

    protected void onPostExecute(BigInteger result) {
        if (result != null) {
            Log.v(TAG, "Result...balance is: " + result.toString());
            TextView balance = (TextView) a.findViewById(R.id.balance);
            balance.setText(formatScrypBalance(result.toString()));
        }
        else {
            Log.v(TAG, "Result is null");
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
