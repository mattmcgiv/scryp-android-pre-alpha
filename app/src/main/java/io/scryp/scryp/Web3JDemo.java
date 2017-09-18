package io.scryp.scryp;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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

public class Web3JDemo extends AppCompatActivity {

    static final String TAG = "Scryp";
    static final BigInteger GAS_PRICE = BigInteger.valueOf(20_000_000_000L);
    static final BigInteger GAS_LIMIT = BigInteger.valueOf(4_300_000);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v(TAG, "Setting content view");
        setContentView(R.layout.activity_web3_jdemo);
        Log.v(TAG, "Init web3j object");
        new Web3JAsyncTask().execute("Yasssss");
    }

    class Web3JAsyncTask extends AsyncTask<String, Void, String > {
        public final Address merchant = new Address("0xfb70456839B62ca7bA09a1fA9E5a553E5e36D4c4");
        final String TAG = "Scryp";
        TextView tv = null;

        protected void onPreExecute() {
            tv = (TextView) Web3JDemo.this.findViewById(R.id.web3tv0);
        }
        protected String doInBackground(String... stuff) {
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

                //Transfers 1 Scryp from "Mint" to "Merchant"
                if (true) {
                    Address contractAddress = new Address("0x805a3a39681762860460b60d2de7e94841723c3f");
                    Scryp_sol_ScrypTestflight contract = Scryp_sol_ScrypTestflight.load(
                            contractAddress.toString(),
                            web3,
                            credentials,
                            GAS_PRICE,
                            GAS_LIMIT
                    );
                    Address mintAddress = new Address(credentials.getAddress());
                    BigInteger mintBalance = getScrypBalance(contract, mintAddress);
                    Log.v(TAG, "Mint balance: " + mintBalance.toString());
                    BigInteger merchantBalance = getScrypBalance(contract, merchant);
                    Log.v(TAG, "Merchant balance: " + merchantBalance.toString());
                    transferScryp(contract, merchant, new Uint256(1));
                    BigInteger mintBalanceAfter = getScrypBalance(contract, mintAddress);
                    Log.v(TAG, "Mint balance: " + mintBalanceAfter.toString());
                    BigInteger merchantBalanceAfter = getScrypBalance(contract, merchant);
                    Log.v(TAG, "Merchant balance: " + merchantBalanceAfter.toString());
                }
                //Deploys contract to blockchain
                if (false) {
                    Future<Scryp_sol_ScrypTestflight> contract = Scryp_sol_ScrypTestflight.deploy(
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
                    Log.v(TAG, "Contract address is: " + contract.get().getContractAddress());
                }

                //Raw transfer of Ethereum
                if (false) {
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
                return "OK";
            } catch (Exception e) {
                e.printStackTrace();
                Log.v(TAG, "Exception: " + e.getMessage());
                return "Something went wrong";
            }
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