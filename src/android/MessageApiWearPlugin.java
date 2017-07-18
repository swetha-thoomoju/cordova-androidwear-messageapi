package it.smartcommunitylab.cordova.messageapi;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;

public class MessageApiWearPlugin extends CordovaPlugin {

  private GoogleApiClient _client;

  @Override
  public void initialize(CordovaInterface cordova, CordovaWebView webView) {
    super.initialize(cordova, webView);
    _client = new GoogleApiClient.Builder(cordova.getActivity().getApplicationContext()).addApi(Wearable.API).build();
    _client.connect();
    cordova.getActivity().startService(new Intent(cordova.getActivity(), MessageApiWearService.class));
  }

  @Override
  public boolean execute(String action, JSONArray args, CallbackContext callbackContext) {
    Log.i("WearService", "exec");
    if (action.equals("getMessages")) {
      try { getMessages(args, callbackContext); }
      catch (Exception ex) { callbackContext.error(ex.getMessage()); return false; }
    }
    else if (action.equals("sendMessage")) {
      try { sendMessage(args, callbackContext); }
      catch (Exception ex) { callbackContext.error(ex.getMessage()); return false; }
    }
    else return false;
    return true;
  }

  private void getMessages(JSONArray args, final CallbackContext callbackContext) throws Exception {
    IntentFilter filter = new IntentFilter(Intent.ACTION_SEND);
    BroadcastReceiver receiver = new BroadcastReceiver() {
      @Override
      public void onReceive(Context context, Intent intent) {
        String msg = intent.getStringExtra("WearMessage");
        Log.i("WearService", "Received: " + msg);

        PluginResult resultOK = new PluginResult(PluginResult.Status.OK, msg);
        resultOK.setKeepCallback(true);
        callbackContext.sendPluginResult(resultOK);
      }
    };
    LocalBroadcastManager.getInstance(this.cordova.getActivity().getApplicationContext()).registerReceiver(receiver, filter);
  }

  private void sendMessage(JSONArray args, final CallbackContext callbackContext) throws Exception {
    String msg = args.getString(0);
    NodeApi.GetConnectedNodesResult nodes = Wearable.NodeApi.getConnectedNodes(_client).await();
    for (Node node : nodes.getNodes()) {
      Wearable.MessageApi.sendMessage(_client, node.getId(), msg, msg.getBytes())
          .setResultCallback(new ResultCallback<MessageApi.SendMessageResult>() {
            @Override
            public void onResult(@NonNull MessageApi.SendMessageResult sendMessageResult) {
              if (sendMessageResult.getStatus().isSuccess()) {
                Log.i("WearService", "sent");

                PluginResult resultOK = new PluginResult(PluginResult.Status.OK, "Sent");
                resultOK.setKeepCallback(true);
                callbackContext.sendPluginResult(resultOK);
              }
            }
          });
    }
  }
}
