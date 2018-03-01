/*
    ©2017 Aina Wireless Inc., All rights reserved.
    ----------------------------------------------

    This file is part of Aina Wireless Inc's ble connect example.

Example is free software: you can redistribute it and/or
    modify it under the terms of the GNU General Public License as published
    by the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    Pairing example is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with Foobar.  If not, see <http://www.gnu.org/licenses/>.
 */


package com.wireless.aina.connectapttasb;

import android.app.AlertDialog;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.ParcelUuid;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattService;
import android.Manifest;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;



public class MainActivity extends AppCompatActivity {

    private BluetoothLeScanner BLEScanner;
    private BluetoothDevice BLEDevice;
    private BluetoothGatt BLEGatt;
    private BluetoothManager BLEManager;
    private BluetoothAdapter BLEAdapter;

    private ScanCallback mScanCallback;
    private BroadcastReceiver mBondReceiver;

    private final Handler TextUpdateHandler = new Handler();

    private boolean GetSwVersion = false;
    private boolean GetBattLevel = false;
    private boolean GetButtons = false;
    private boolean Bonding = false;

    private boolean Ready = false;
    private String State = "IDLE";


    private int apttasb_batt_level = 0x00;
    private int apttasb_button_mask = 0x00;
    private String apttasb_sw_version = "";
    private String aptt_or_asb = "";
    private String devName = "";
    private String classicName = "";

    private TextView TextView_log_1;
    private TextView TextView_log_2;
    private TextView TextView_sw_version;
    private TextView TextView_button_mask;
    private TextView TextView_buttons;
    private TextView TextView_battlevel;

    private Button Button_red_led;
    private Button Button_green_led;
    private Button Button_amber_led;
    private Button Button_blue_led;
    private Button Button_off_led;

    private final static int REQUEST_ENABLE_BT = 1;

    private static final UUID CLIENT_CHAR_CONFIG = UUID.fromString("00002902-0000-1000-8000-00805F9B34FB");

    private static final UUID AINA_SERV = UUID.fromString("127FACE1-CB21-11E5-93D0-0002A5D5C51B");
    private static final UUID BATT_SERV = UUID.fromString("0000180F-0000-1000-8000-00805F9B34FB");
    private static final UUID SW_VERS = UUID.fromString("127FC0FF-CB21-11E5-93D0-0002A5D5C51B");
    private static final UUID BUTTONS = UUID.fromString("127FBEEF-CB21-11E5-93D0-0002A5D5C51B");
    private static final UUID LEDS = UUID.fromString("127FDEAD-CB21-11E5-93D0-0002A5D5C51B");
    private static final UUID BATT_LEVEL = UUID.fromString("00002A19-0000-1000-8000-00805F9B34FB");
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        TextView_log_1 = (TextView) findViewById(R.id.service1);
        TextView_log_2 = (TextView) findViewById(R.id.service2);

        TextView_sw_version = (TextView) findViewById(R.id.sw_version);
        TextView_button_mask = (TextView) findViewById(R.id.textView_buttonmask);
        TextView_buttons = (TextView) findViewById(R.id.textView_buttons);
        TextView_battlevel = (TextView) findViewById(R.id.textView_battlevel);

        Button_red_led = (Button) findViewById(R.id.red_led_btn);
        Button_green_led = (Button) findViewById(R.id.green_led_btn);
        Button_amber_led = (Button) findViewById(R.id.amber_led_btn);
        Button_blue_led = (Button) findViewById(R.id.blue_led_btn);
        Button_off_led = (Button) findViewById(R.id.off_led_btn);


        Button_red_led.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                byte tmp[] = {0x01};

                if (Ready == true) {

                    BLEGatt.getService(AINA_SERV).getCharacteristic(LEDS).setValue(tmp);
                    BLEGatt.writeCharacteristic(BLEGatt.getService(AINA_SERV).getCharacteristic(LEDS));
                }
            }
        });


        Button_green_led.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                byte tmp[] = {0x02};

                if (Ready == true) {

                    BLEGatt.getService(AINA_SERV).getCharacteristic(LEDS).setValue(tmp);
                    BLEGatt.writeCharacteristic(BLEGatt.getService(AINA_SERV).getCharacteristic(LEDS));
                }
            }
        });


        Button_amber_led.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                byte tmp[] = {0x04};

                if (Ready == true) {

                    BLEGatt.getService(AINA_SERV).getCharacteristic(LEDS).setValue(tmp);
                    BLEGatt.writeCharacteristic(BLEGatt.getService(AINA_SERV).getCharacteristic(LEDS));
                }
            }
        });


        Button_blue_led.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                byte tmp[] = {0x04};

                if (Ready == true) {

                    BLEGatt.getService(AINA_SERV).getCharacteristic(LEDS).setValue(tmp);
                    BLEGatt.writeCharacteristic(BLEGatt.getService(AINA_SERV).getCharacteristic(LEDS));
                }
            }
        });


        Button_off_led.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                byte tmp[] = {0x00};

                if (Ready == true) {

                    BLEGatt.getService(AINA_SERV).getCharacteristic(LEDS).setValue(tmp);
                    BLEGatt.writeCharacteristic(BLEGatt.getService(AINA_SERV).getCharacteristic(LEDS));
                }
            }
        });


        BLEManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        BLEAdapter = BLEManager.getAdapter();
        BLEScanner = BLEAdapter.getBluetoothLeScanner();
/*
        if (this.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            final AlertDialog.Builder builder = new AlertDialog.Builder(this);

            builder.setTitle("LOCATION SERVICES");
            builder.setMessage("This application needs access to location services to discover devices!");
            builder.setPositiveButton(android.R.string.ok, null);

            builder.setOnDismissListener(new DialogInterface.OnDismissListener() {

                @Override
                public void onDismiss(DialogInterface dialog) {

                    requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                }

            });

            builder.show();
        }
*/

        if (!BLEAdapter.isEnabled()) {

            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);

            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
        } else {
            IntentFilter intent = new IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
            mBondReceiver = new BondReceiver();
            registerReceiver(mBondReceiver, intent);

            startLE();
        }
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if ((requestCode == REQUEST_ENABLE_BT) && (resultCode == -1)) {

            startLE();
        }
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {

        switch (requestCode) {

            case 1: {

                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    System.out.println("Coarse location: PERMISSION_GRANTED");

                } else {

                    final AlertDialog.Builder builder = new AlertDialog.Builder(this);

                    builder.setTitle("LOCATION SERVICES");
                    builder.setMessage("This application needs access to location services to discover devices!");
                    builder.setPositiveButton(android.R.string.ok, null);

                    builder.setOnDismissListener(new DialogInterface.OnDismissListener() {

                        @Override
                        public void onDismiss(DialogInterface dialog) {
                        }

                    });

                    builder.show();
                }
            }
        }
    }


    private void startLE() {

        boolean found = false;
        classicName = "";

        Set<BluetoothDevice> devices = BLEAdapter.getBondedDevices();

        for (BluetoothDevice device : devices) {

            if (device.getType() == BluetoothDevice.DEVICE_TYPE_LE) {

                if ((device.getName().contains("ASB")) || (device.getName().contains("APTT"))) {

                    found = true;

                    BLEDevice = device;

                    if (device.getName().contains("ASB"))
                        aptt_or_asb = "Smart Button";
                    else
                        aptt_or_asb = "Aina PTT";

                    TextView_log_1.setText("Device " + device.getName() + " already paired.");

                    devName = device.getName();

                    TextView_log_2.setText("Trying to connect...");

                    TextUpdateHandler.post(updateText);

                    if (BLEGatt != null) {

                        BLEGatt.disconnect();

                        BLEGatt.close();
                    }
                    new Timer().schedule(new TimerTask() {
                        @Override
                        public void run() {
                            State = "Connecting";

                            BLEGatt = BLEDevice.connectGatt(getApplicationContext(), true, GattCallback);
                        }
                    }, 500);

                }
            }
        }

        if(found == false)
        {
            for (BluetoothDevice device : devices) {

                if (device.getName().contains("APTT")) {
                    classicName = device.getName();
                }
            }
        }

        if (found == false) {

            System.out.println("start scanning ble devices...");

            ScanSettings settings = new ScanSettings.Builder().setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY).build();
            ScanFilter filter = new ScanFilter.Builder().setServiceUuid(ParcelUuid.fromString(AINA_SERV.toString())).build();
            List<ScanFilter> filter_list = new ArrayList<ScanFilter>(1);
            filter_list.add(filter);

            TextView_log_1.setText("Devices not paired.");
            TextView_log_2.setText("Scanning for APTT and ASB devices...");

            TextUpdateHandler.post(updateText);

            mScanCallback = new BLEScanCallback();

            BLEScanner.startScan(filter_list, settings, mScanCallback);
        }
    }



    private class BLEScanCallback extends ScanCallback {

        @Override
        public void onScanResult(int callbackType, ScanResult result) {

            int RSSI;

            BLEDevice = result.getDevice();

            String temp = BLEDevice.getName();

            RSSI = result.getRssi();

            if((!classicName.isEmpty()) && (classicName.contentEquals(temp)) && (State.contains(("IDLE")))) {
                State = "Connecting";

                aptt_or_asb = "Aina PTT";

                //IntentFilter intent = new IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
                //registerReceiver(BondReceiver, intent);

                BLEScanner.flushPendingScanResults(mScanCallback);
                BLEScanner.stopScan(mScanCallback);

                TextView_log_1.setText("Device " + temp + " found.");
                devName = temp;

                TextView_log_2.setText("Trying to connect...");

                TextUpdateHandler.post(updateText);

                if (BLEGatt != null) {

                    BLEGatt.disconnect();

                    BLEGatt.close();
                }

                new Timer().schedule(new TimerTask() {
                    @Override
                    public void run() {

                        TextUpdateHandler.post(updateText);

                        System.out.println(">>>>>connectGatt (OnScanResult))");
                        BLEGatt = BLEDevice.connectGatt(getApplicationContext(), true, GattCallback);

                        Bonding = true;

                    }
                }, 500);
            }

            else if (((temp.contains("ASB")) || (temp.contains("APTT"))) && (State.contains("IDLE")) && (classicName.isEmpty())) {     // If rssi based pairing will be used, check rssi level
//          if (((temp.contains("ASB")) || (temp.contains("APTT"))) && (RSSI > -32) && (State.contains("IDLE"))) {     // If rssi based pairing will be used, check rssi level
//            if (temp.contains("ASB")) {    //Connect to first found aptt or asb
                State = "Connecting";

                if (temp.contains("ASB"))
                    aptt_or_asb = "Smart Button";
                else
                    aptt_or_asb = "Aina PTT";

                //IntentFilter intent = new IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
                //registerReceiver(BondReceiver, intent);

                BLEScanner.flushPendingScanResults(mScanCallback);
                BLEScanner.stopScan(mScanCallback);

                TextView_log_1.setText("Device " + temp + " found.");
                devName = temp;

                TextView_log_2.setText("Trying to connect...");

                TextUpdateHandler.post(updateText);

                if (BLEGatt != null) {

                    BLEGatt.disconnect();

                    BLEGatt.close();
                }

                new Timer().schedule(new TimerTask() {
                    @Override
                    public void run() {

                        TextUpdateHandler.post(updateText);

                        System.out.println(">>>>>connectGatt (OnScanResult))");
                        BLEGatt = BLEDevice.connectGatt(getApplicationContext(), true, GattCallback);

                        Bonding = true;

                    }
                }, 500);
            }
        }
    };

    private class BondReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            String action = intent.getAction();

            if ((BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(action)) && (!State.contains("Connected"))) {

                System.out.println(">>>>>ACTION_BOND_STATE_CHANGED (State != Connected)");

                final int state = intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE, BluetoothDevice.ERROR);
                final int prevState = intent.getIntExtra(BluetoothDevice.EXTRA_PREVIOUS_BOND_STATE, BluetoothDevice.ERROR);

                if (state == BluetoothDevice.BOND_BONDED && prevState == BluetoothDevice.BOND_BONDING) {

                    if (BLEDevice.getBondState() == BluetoothDevice.BOND_BONDED) {

                        System.out.println(">>>>>Start discover services (State = BOND_BONDED)");
                        BLEGatt.discoverServices();

                        State = "Discovering";
                        Bonding = false;

                        new Timer().schedule(new TimerTask() {
                            @Override
                            public void run() {
                                System.out.println(">>>>>DiscoverServices timer");

                                if(State.contains("Discovering")) {
                                    System.out.println(">>>>>Services not found --> restart");
                                    BLEGatt.discoverServices();
                                }
                            }
                        }, 5000);

                    } else {
                        System.out.println(">>>>>State != BOND_BONDED");
                        Ready = false;
                    }

                }

            }

        }
    };




    private final BluetoothGattCallback GattCallback = new BluetoothGattCallback() {

        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {

            if ((newState == BluetoothProfile.STATE_CONNECTED) && (!State.contains("Discovering"))) {

                if (State.contains("Link Loss")) {
                    System.out.println(">>>>>State change LL -> LL - Reconnect");
                    State = "LL - Reconnect";
                }

                if((State.contains("Connecting") || State.contains("LL - Reconnect")) && Bonding == false)
                {
                    System.out.println(">>>>>Start discovering (state = connecting or LL - Reconnect)");

                    gatt.discoverServices();

                    State = "Discovering";
                }
                else {
                    System.out.println(">>>>>onConeectionStateChane  State = " + State);

                    if(State.contains("Connecting")) {
                        gatt.discoverServices();

                        State = "Discovering";

                        Bonding = false;
                    }

                }

                apttasb_button_mask = 0x00;

                TextUpdateHandler.post(updateText);
            } else {
                Ready = false;

                System.out.println(">>>>>LINK LOSS!");

                if (State.contains("Connected") || State.contains("Discovering")) State = "Link Loss";

                TextUpdateHandler.post(updateText);
            }

        }


        @Override
        public void onServicesDiscovered(final BluetoothGatt gatt, int status) {

            if (status == BluetoothGatt.GATT_SUCCESS) {
                System.out.println(">>>>>GATT_SUCCESS on Services discovered");

                if (!State.contains("Connected") && Bonding == false) {
                    State = "Connected";

                    new Timer().schedule(new TimerTask() {
                        @Override
                        public void run() {
                                System.out.println(">>>>>StopScan");
                                BLEScanner.stopScan(mScanCallback);

                                GetSwVersion = true;

                                System.out.println(">>>>>GetSevice(AINA_SERV)");

                                BluetoothGattService Service = BLEGatt.getService(AINA_SERV);

                                if(Service != null)
                                {
                                    System.out.println(">>>>>Got service, now read SW_VERSION char");

                                    BLEGatt.readCharacteristic(Service.getCharacteristic(SW_VERS));
                                    new Timer().schedule(new TimerTask() {
                                        @Override
                                        public void run() {
                                            System.out.println(">>>>>ReadSWversion timer");
                                            if((GetSwVersion == true) && (State.contains("Connected"))) {
                                                State = "Connecting";
                                                BLEGatt = BLEDevice.connectGatt(getApplicationContext(), true, GattCallback);
                                            }
                                        }
                                    }, 2000);
                                }
                                else
                                {
                                    System.out.println(">>>>>Didn't get services, restart from connect");
                                    State = "Connecting";
                                    BLEGatt = BLEDevice.connectGatt(getApplicationContext(), true, GattCallback);
                                }

                            }
                    }, 1600);
                }
                else
                {
                    System.out.println(">>>>>on Services discovered (Bonding or connected!)");
                }
            }
            else
            {
                System.out.println(">>>>>GATT_ERROR on Services discovered!");
            }
        }


        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {

            if (characteristic.getUuid().equals(BUTTONS)) {

                apttasb_button_mask = ((int) characteristic.getValue()[0]);
            }

            if (characteristic.getUuid().equals(BATT_LEVEL)) {

                apttasb_batt_level = ((int) characteristic.getValue()[0]);
            }

            TextUpdateHandler.post(updateText);
        }


        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            System.out.println(">>>>>onCharacteristicsRead");

            if (status == BluetoothGatt.GATT_SUCCESS) {

                if (GetSwVersion) {
                    System.out.println(">>>>>Lets check SWVerion...");

                    if(characteristic.getValue().length == 6) {
                        Ready = true;

                        apttasb_sw_version = aptt_or_asb + " Version: ";
                        apttasb_sw_version += Integer.toHexString((characteristic.getValue()[3] & 0xff));
                        apttasb_sw_version += Integer.toHexString((characteristic.getValue()[4] & 0xff));
                        apttasb_sw_version += Integer.toHexString((characteristic.getValue()[5] & 0xff)).toUpperCase();

                        if (apttasb_sw_version.substring(apttasb_sw_version.length() - 1, apttasb_sw_version.length()).equals("0")) {
                            apttasb_sw_version = apttasb_sw_version.substring(0, apttasb_sw_version.length() - 1);
                        }

                        if (apttasb_sw_version.toUpperCase().contains("BE7A"))
                            apttasb_sw_version = "ASB Version: Beta release";

                        TextUpdateHandler.post(updateText);

                        GetBattLevel = true;

                        BLEGatt.readCharacteristic(BLEGatt.getService(BATT_SERV).getCharacteristic(BATT_LEVEL));
                    }
                    else
                    {
                        System.out.println(">>>>>Not SWVersion, try again...");
                        BLEGatt.readCharacteristic(BLEGatt.getService(BATT_SERV).getCharacteristic(SW_VERS));

                    }

                } else if (GetBattLevel) {

                    apttasb_batt_level = ((int) characteristic.getValue()[0]);

                    BLEGatt.setCharacteristicNotification(BLEGatt.getService(AINA_SERV).getCharacteristic(BUTTONS), true);
                    BluetoothGattDescriptor descriptor = BLEGatt.getService(AINA_SERV).getCharacteristic(BUTTONS).getDescriptor(CLIENT_CHAR_CONFIG);
                    descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                    BLEGatt.writeDescriptor(descriptor);

                    BLEGatt.setCharacteristicNotification(BLEGatt.getService(BATT_SERV).getCharacteristic(BATT_LEVEL), true);
                    descriptor = BLEGatt.getService(BATT_SERV).getCharacteristic(BATT_LEVEL).getDescriptor(CLIENT_CHAR_CONFIG);
                    descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                    BLEGatt.writeDescriptor(descriptor);

                    TextUpdateHandler.post(updateText);

                }
            } else {

                System.out.println(">>>>>GATT_ERROR (onCharRead)!");

                State = "Connecting";
                BLEGatt = BLEDevice.connectGatt(getApplicationContext(), true, GattCallback);

            }
        }
    };


    private final Runnable updateText = new Runnable() {

        public void run() {
            if(aptt_or_asb.contains("Smart")) {
                Button_amber_led.setVisibility(View.VISIBLE);
                Button_blue_led.setVisibility(View.INVISIBLE);
            }
            else
            {
                Button_amber_led.setVisibility(View.INVISIBLE);
                Button_blue_led.setVisibility(View.VISIBLE);
            }

            if (GetSwVersion) {
                TextView_log_2.setText("Connected!");

                GetSwVersion = false;
                GetButtons = true;

                TextView_sw_version.setText(apttasb_sw_version);

                TextView_button_mask.setText("0x00");

            }

            if (GetButtons) {
                TextView_log_1.setText("Device " + devName + " connected.");

                TextView_log_2.setText("Connected!");

                if ((apttasb_button_mask & 0xff) < 16)
                    TextView_button_mask.setText("0x0" + Integer.toHexString(apttasb_button_mask & 0xff).toUpperCase());
                else
                    TextView_button_mask.setText("0x" + Integer.toHexString(apttasb_button_mask & 0xff).toUpperCase());

                TextView_buttons.setText("");

                if ((apttasb_button_mask & 1) == 1) TextView_buttons.append("(PTT1 - 0x01) ");
                if ((apttasb_button_mask & 2) == 2) TextView_buttons.append("(EMERG - 0x02) ");
                if ((apttasb_button_mask & 4) == 4) TextView_buttons.append("(PTT2 - 0x04) ");

                if (((apttasb_button_mask & 8) == 8) && (aptt_or_asb.contains("Smart")))
                    TextView_buttons.append("(DOWN - 0x08) ");
                else if (((apttasb_button_mask & 8) == 8) && (aptt_or_asb.contains("Aina")))
                    TextView_buttons.append("(SOFT1 - 0x08) ");

                if (((apttasb_button_mask & 16) == 16) && (aptt_or_asb.contains("Smart")))
                    TextView_buttons.append("(UP - 0x10) ");
                else if (((apttasb_button_mask & 16) == 16) && (aptt_or_asb.contains("Aina")))
                    TextView_buttons.append("(SOFT2 - 0x10) ");

                if (((apttasb_button_mask & 32) == 32) && (aptt_or_asb.contains("Smart")))
                    TextView_buttons.append("(LEFT - 0x20) ");
                else if (((apttasb_button_mask & 32) == 32) && (aptt_or_asb.contains("Aina")))
                    TextView_buttons.append("(MULTI - 0x20) ");

                if ((apttasb_button_mask & 64) == 64) TextView_buttons.append("(RIGHT - 0x40) ");

                if ((apttasb_button_mask & 128) == 128)
                    TextView_buttons.append("(heartbeat - 0x80)");
            }

            if (GetBattLevel) {
                TextView_battlevel.setText("Battery level: " + apttasb_batt_level + "%");
            }

            if (State.contains("Link Loss")) {
                TextView_sw_version.setText("");
                TextView_button_mask.setText("");
                TextView_buttons.setText("");
                TextView_battlevel.setText("");

                TextView_log_1.setText("Link loss..");
                TextView_log_2.setText("Trying to reconnect..");
            }
            if (State.contains("LL - Reconnect")) {
                TextView_sw_version.setText("");
                TextView_button_mask.setText("");
                TextView_buttons.setText("");
                TextView_battlevel.setText("");

                TextView_log_1.setText("Device found again!");
            }

        }
    };

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("Main Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onRestart() {
        super.onRestart();

        if(mBondReceiver == null) {
            System.out.println(">>>>>onRestart --> New BondReceiver");

            IntentFilter intent = new IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
            registerReceiver(mBondReceiver, intent);
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        if(mBondReceiver == null) {
            System.out.println(">>>>>onResume --> New BondReceiver");
            IntentFilter intent = new IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
            registerReceiver(mBondReceiver, intent);
        }
    }
}


