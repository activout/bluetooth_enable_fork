package com.hui.bluetooth_enable;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;

import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.embedding.engine.plugins.activity.ActivityAware;
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.PluginRegistry;
import io.flutter.plugin.common.PluginRegistry.ActivityResultListener;

public class BluetoothEnablePlugin implements FlutterPlugin, ActivityAware, MethodCallHandler, ActivityResultListener, PluginRegistry.RequestPermissionsResultListener {
    private static final String TAG = "BluetoothEnablePlugin";
    private Activity activity;
    private MethodChannel channel;
    private Result pendingResult;

    private static final int REQUEST_ENABLE_BLUETOOTH = 0xeb;


    @Override
    public void onMethodCall(@NonNull MethodCall call, @NonNull Result result) {
        BluetoothManager bluetoothManager = (BluetoothManager) this.activity.getSystemService(Context.BLUETOOTH_SERVICE);
        BluetoothAdapter bluetoothAdapter = bluetoothManager.getAdapter();

        if(bluetoothAdapter == null && !"isAvailable".equals(call.method)) {
            result.error("bluetooth_unavailable", "the device does not have bluetooth", null);
            return;
        }

        switch (call.method) {
            case "enableBluetooth":
            {  
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                activity.startActivityForResult(enableBtIntent, REQUEST_ENABLE_BLUETOOTH);
                Log.d(TAG, "rdddesult: " + result);
                pendingResult = result;
                break;
            }

            default:
            {
                result.notImplemented();
                break;
            }
        }
    }

    @Override
    public boolean onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_ENABLE_BLUETOOTH && pendingResult != null){
            try {
                if (resultCode == Activity.RESULT_OK) {
                    Log.d(TAG, "User enabled Bluetooth");
                    pendingResult.success("true");
                } else {
                    Log.d(TAG, "User did NOT enabled Bluetooth");
                    pendingResult.success("false");
                }
                pendingResult = null;
                return true;
            }
            catch(IllegalStateException|NullPointerException e)
            {
                Log.d(TAG, "onActivityResult REQUEST_ENABLE_BLUETOOTH", e);
            }
        }
        return false;
    }

    @Override
    public boolean onRequestPermissionsResult(
        int requestCode, String[] permissions, int[] grantResults) {
        Log.d(TAG, "onRequestPermissionsResult, TWO");

        return false;
    }


    /* FlutterPlugin implementation */

    @Override
    public void onAttachedToEngine(FlutterPluginBinding binding) {
        this.channel = new MethodChannel(binding.getBinaryMessenger(), "bluetooth_enable");
    }

    @Override
    public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {
        this.activity = null;
        this.channel = null;
    }


    /* ActivityAware implementation */

    @Override
    public void onAttachedToActivity(@NonNull ActivityPluginBinding activityPluginBinding) {
        this.initPluginFromPluginBinding(activityPluginBinding);
    }
    @Override
    public void onReattachedToActivityForConfigChanges(@NonNull ActivityPluginBinding activityPluginBinding) {
        this.initPluginFromPluginBinding(activityPluginBinding);
    }
    private void initPluginFromPluginBinding (ActivityPluginBinding activityPluginBinding) {
        this.activity = activityPluginBinding.getActivity();

        activityPluginBinding.addActivityResultListener(this);
        activityPluginBinding.addRequestPermissionsResultListener(this);

        this.channel.setMethodCallHandler(this);
    }

    @Override
    public void onDetachedFromActivityForConfigChanges() {
        this.releaseResources();
    }
    @Override
    public void onDetachedFromActivity() {
        this.releaseResources();
    }
    private void releaseResources() {
        this.activity = null;
    }
}