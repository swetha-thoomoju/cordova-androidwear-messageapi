package it.smartcommunitylab.cordova.messageapi;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.os.SystemClock;
import android.util.Log;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;
import com.google.android.gms.wearable.WearableListenerService;

public class MessageApiWearService extends WearableListenerService {

  private GoogleApiClient _client;

  @Override
  public void onCreate() {
    super.onCreate();
    if (_client == null) {
      _client = new GoogleApiClient.Builder(this.getApplicationContext()).addApi(Wearable.API).build();
    }
    if (!_client.isConnected()) {
      _client.connect();
    }
  }
  
  @Override
  public void onMessageReceived(MessageEvent messageEvent) {
    super.onMessageReceived(messageEvent);
    byte[] b = messageEvent.getData();
    String s = new String(b);
    Log.i("WearService", "ReceivedService: " + s);

    //sendData(); //DEBUG

    Intent intent = new Intent();
    intent.setAction(Intent.ACTION_SEND);
    intent.putExtra("WearMessage", s);
    LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
  }

  ////DEBUG
  // private void sendData(){
  //   String msg = "sendBackService";
  //   NodeApi.GetConnectedNodesResult nodes = Wearable.NodeApi.getConnectedNodes(_client).await();
  //   for (Node node : nodes.getNodes()) {
  //     Wearable.MessageApi.sendMessage(_client, node.getId(), msg, msg.getBytes());
  //   }
  // }

  //Keeps the service active in background
  @Override
  public int onStartCommand(Intent intent, int flags, int startId) {
    return START_STICKY;
  }

  //Keeps the service active in background
  @Override
  public void onTaskRemoved(Intent rootIntent) {
    Intent restartService = new Intent(getApplicationContext(), this.getClass());
    restartService.setPackage(getPackageName());
    PendingIntent restartServicePI = PendingIntent.getService(getApplicationContext(), 1, restartService,
        PendingIntent.FLAG_ONE_SHOT);

    AlarmManager alarmService = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
    alarmService.set(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime() + 100, restartServicePI);
  }
}
