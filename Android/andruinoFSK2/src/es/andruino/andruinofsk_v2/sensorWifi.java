package es.andruino.andruinofsk_v2;

import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;

// Inspirado en Guaran3

class sensorWifi extends BroadcastReceiver {
	
	
	   WifiManager Wifimgr;
	   List<ScanResult> wifiList;
	   StringBuilder sb = new StringBuilder();
    
	
	public sensorWifi(Context contexto){
		Wifimgr = (WifiManager) contexto.getSystemService(Context.WIFI_SERVICE);
	    contexto.registerReceiver(this, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
	    Wifimgr.startScan();
	}


	@Override
	public void onReceive(Context c, Intent intent) {
		 sb = new StringBuilder();
         wifiList = Wifimgr.getScanResults();
         for(int i = 0; i < wifiList.size(); i++){
             sb.append((wifiList.get(i)).BSSID.toString());
             sb.append(";");
             sb.append((wifiList.get(i)).level).toString();
             sb.append(";");
         }
         Wifimgr.startScan();
       
	}
	
	public String getBeacons(){
		return (sb.toString());
	}
	public void desregistrasensorWifi(Context contexto){
		contexto.unregisterReceiver(this);
	}
	
}