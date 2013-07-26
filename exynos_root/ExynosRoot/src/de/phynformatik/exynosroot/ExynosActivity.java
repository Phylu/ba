package de.phynformatik.exynosroot;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class ExynosActivity extends Activity {
	
	private native int getRoot();
	private native int getUid();
	private TextView textData, textUid;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exynos);

        Button buttonExynos = (Button) findViewById(R.id.buttonExynos);
        buttonExynos.setOnClickListener(buttonExynosOnClickListener);
        
        Button buttonData = (Button) findViewById(R.id.buttonData);
        buttonData.setOnClickListener(buttonDataOnClickListener);
        
        Button buttonWlanPass = (Button) findViewById(R.id.buttonWlanPass);
        buttonWlanPass.setOnClickListener(buttonWlanPassOnClickListener);
        
        textData = (TextView) findViewById(R.id.text_data);
        textUid = (TextView) findViewById(R.id.text_uid);

        updateUid();
    }
    
    private OnClickListener buttonExynosOnClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			
			Log.e("UID before:", String.valueOf(getUid()));
			
			if (getRoot() == 0) {
				Toast.makeText(getApplicationContext(),
						getResources().getText(R.string.text_root),
						Toast.LENGTH_LONG).show();
				
				Log.e("UID after:", String.valueOf(getUid()));
				
				updateUid();
			} else {
				Toast.makeText(getApplicationContext(),
						getResources().getText(R.string.text_no_root),
						Toast.LENGTH_LONG).show();
			}
			
		}
	};
	
	private OnClickListener buttonDataOnClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			showData();
		}
	};
	
	private OnClickListener buttonWlanPassOnClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			readWlanPass();
		}
	};
	
	private void updateUid() {
				textUid.setText(String.format("%s %d", getResources().getText(R.string.text_uid), getUid()));
	}
	
	private void showData() {
		String lsData = getResources().getString(R.string.text_no_root);
		
		Process proc;
		String command = "ls /data";
		Runtime r = Runtime.getRuntime();
		
		try {
			proc = r.exec(command);
			proc.waitFor();
			if (proc.exitValue() == 0) {
				lsData = getResources().getString(R.string.text_data);
				InputStream inStream = proc.getInputStream();
				BufferedReader buff = new BufferedReader(new InputStreamReader(inStream));
				String str;
				while ((str = buff.readLine()) != null) {
					lsData += str + "\n";
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		textData.setText(lsData);
	}

	
	private void readWlanPass() {
		String wlanData = getResources().getString(R.string.text_no_root);
		
		Process proc;
		String command = "cat /data/misc/wifi/wpa_supplicant.conf";
		Runtime r = Runtime.getRuntime();
		
		try {
			proc = r.exec(command);
			proc.waitFor();
			if (proc.exitValue() == 0) {
				//wlanData = getResources().getString(R.string.text_wlan);
				wlanData = "";
				InputStream inStream = proc.getInputStream();
				BufferedReader buff = new BufferedReader(new InputStreamReader(inStream));
				String str;
				while ((str = buff.readLine()) != null) {
					wlanData += str + "\n";
				}
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		textData.setText(wlanData);
	}
	
	
	/**
	 * Somehow this does not work.
	 * The app has superuser rights but cannot open protected files natively
	 */
	/*
	private void readWlanPass() {
		File wpaSupplicant = new File("/data/misc/wifi/wpa_supplicant.conf");
		try {
			BufferedReader buff = new BufferedReader(new FileReader(wpaSupplicant));
			String thisLine;
			String wlanData = "";
			while((thisLine = buff.readLine()) != null) {
				wlanData += thisLine + "\n";
			}
			buff.close();
			textData.setText(wlanData);
		} catch (IOException e) {
			e.printStackTrace();
			textData.setText(getResources().getString(R.string.text_no_root));
		}
	}
	*/

    
    
    static {
        System.loadLibrary("exynos-abuse");
    }
}
