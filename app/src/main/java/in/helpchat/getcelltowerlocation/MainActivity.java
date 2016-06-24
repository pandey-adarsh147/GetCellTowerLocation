package in.helpchat.getcelltowerlocation;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.telephony.gsm.GsmCellLocation;
import android.text.TextUtils;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private static final int MY_PERMISSIONS_REQUEST = 1;

    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = (TextView) findViewById(R.id.cell);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, MY_PERMISSIONS_REQUEST);
        } else {
            //do your job
            setCellId();
        }
    }

    private void setCellId() {
        final TelephonyManager telephony = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        MyPhoneStateListener psListener = new MyPhoneStateListener();
        telephony.listen(psListener, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);

        String networkOperator = telephony.getNetworkOperator();
        String networkOperatorName = telephony.getNetworkOperatorName();
        String carrierName = telephony.getSimOperatorName();

        System.out.println("============ Network operator: " + networkOperator);
        System.out.println("============ Network operator name: " + networkOperatorName);
        System.out.println("============ Sim operator name: " + carrierName);
        if (!TextUtils.isEmpty(networkOperator)) {
            int mcc = Integer.parseInt(networkOperator.substring(0, 3));
            int mnc = Integer.parseInt(networkOperator.substring(3));

            System.out.println("============ MCC: " + mcc + ", MNC:" + mnc);
        }

        if (telephony.getPhoneType() == TelephonyManager.PHONE_TYPE_GSM) {
            final GsmCellLocation location = (GsmCellLocation) telephony.getCellLocation();
            if (location != null) {
                textView.setText("LAC: " + location.getLac() + " CID: " + location.getCid());

                System.out.println("============ LAC: " + location.getLac() + " CID: " + location.getCid());
            }

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        setCellId();
    }

    public class MyPhoneStateListener extends PhoneStateListener {
        public int signalStrengthValue;

        public void onSignalStrengthsChanged(SignalStrength signalStrength) {
            super.onSignalStrengthsChanged(signalStrength);
            if (signalStrength.isGsm()) {
                if (signalStrength.getGsmSignalStrength() != 99)
                    signalStrengthValue = signalStrength.getGsmSignalStrength() * 2 - 113;
                else
                    signalStrengthValue = signalStrength.getGsmSignalStrength();
            } else {
                signalStrengthValue = signalStrength.getCdmaDbm();
            }

            System.out.println("============ Signal Strength : " + signalStrengthValue);
        }
    }
}
