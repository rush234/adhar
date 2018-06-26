package digilocker.com.qr;

import android.media.FaceDetector;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import java.io.IOException;


/**
 * Created by Shubham on 21-06-2018.
 */

public class scan_activity  extends AppCompatActivity{
    SurfaceView camView;
    public static CameraSource cs;
    public TextView detection;
    public static BarcodeDetector detector;

    @Override
    public void onCreate(Bundle savedState){

        super.onCreate(savedState);
        setContentView(R.layout.scanner);

        detection = (TextView) findViewById(R.id.detection);

        detector = new BarcodeDetector.Builder(this)
                .setBarcodeFormats(Barcode.QR_CODE)
                .build();
        if(!detector.isOperational()){
            Toast.makeText(this,"Could not set up the detector!",Toast.LENGTH_SHORT);
            return;
        }

        // attach detections processor/listener
        detector.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {

            }

            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {
                SparseArray barcodes = detections.getDetectedItems();
                if(barcodes.size()>0) {
                    final Barcode barcode = (Barcode) barcodes.valueAt(0);
                    detection.post(new Runnable() {
                        @Override
                        public void run() {

                            Toast.makeText(scan_activity.this, barcode.rawValue, Toast.LENGTH_SHORT).show();

                            detection.setText(barcode.displayValue);
                        }
                    });
                }
            }
        });

        camView = (SurfaceView) findViewById(R.id.surface);

        // attach  detector with camera
        cs = new CameraSource.Builder(this,detector).build();
        camView.getHolder().addCallback(new SurfaceHolder.Callback(){

            @Override
            public void surfaceCreated(SurfaceHolder surfaceHolder) {
                try {
                    cs.start(camView.getHolder());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) { }

            @Override
            public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
                if (cs != null) {
                    cs.stop();
                    cs = null;
                }
            }
        });
    }

    protected void onPause(){
        super.onPause();
        if (cs != null) {
            cs.stop();
            cs = null;
        }

    }

}
