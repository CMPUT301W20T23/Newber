package com.cmput301w20t23.newber.views;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.cmput301w20t23.newber.R;
import com.cmput301w20t23.newber.controllers.RideController;
import com.cmput301w20t23.newber.controllers.UserController;
import com.cmput301w20t23.newber.models.RideRequest;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class RequestPaymentFragment extends Fragment {
    private RideRequest rideRequest;
    private String role;
    private Double cost;

    private RideController rideController = new RideController();
    private UserController userController = new UserController(this.getContext());

    private Bitmap userBitmap;
    private ImageView qrImage;
    private ImageView qrPicture;
    private TextView driverTextView;
    private Button qrScanButton;

    private IntentIntegrator qrScan;

    public static int WHITE = 0xFFFFFFFF;
    public static int BLACK = 0xFF000000;
    public final static int WIDTH = 500;

    /**
     * Instantiates a new RequestPaymentFragment.
     *
     * @param request the current request
     * @param role the role of the current user
     */
    public RequestPaymentFragment(RideRequest request, String role) {
        this.rideRequest = request;
        this.role = role;
    }

    // https://stackoverflow.com/questions/28232116/android-using-zxing-generate-qr-code
    Bitmap encodeAsBitmap(String str) throws WriterException {
        BitMatrix result;
        try {
            result = new MultiFormatWriter().encode(str,
                    BarcodeFormat.QR_CODE, WIDTH, WIDTH, null);
        } catch (IllegalArgumentException iae) {
            // Unsupported format
            return null;
        }
        int w = result.getWidth();
        int h = result.getHeight();
        int[] pixels = new int[w * h];
        for (int y = 0; y < h; y++) {
            int offset = y * w;
            for (int x = 0; x < w; x++) {
                pixels[offset + x] = result.get(x, y) ? BLACK : WHITE;
            }
        }
        Bitmap bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        bitmap.setPixels(pixels, 0, WIDTH, 0, 0, w, h);
        return bitmap;
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        // View for this fragment
        View view = inflater.inflate(R.layout.request_payment_fragment, container, false);

        qrScan = new IntentIntegrator(getActivity());
        qrImage = (ImageView)  view.findViewById(R.id.qr_image);
        driverTextView = view.findViewById(R.id.qr_driver_text);
        qrScanButton = view.findViewById(R.id.qr_scan_button);

        switch (role) {
            case "Rider":
                handleRiderQR();
                break;
            case "Driver":
                handleDriverQR();
                break;
        }

        return view;
    }

    public void handleRiderQR() {
        qrImage.setVisibility(View.VISIBLE);
        driverTextView.setVisibility(View.VISIBLE);
        System.out.println("handleRiderQR, driverTextView vis: " + driverTextView.getVisibility());

        cost = rideRequest.getCost();
        try {
            userBitmap = encodeAsBitmap(Double.toString(cost));
            qrImage.setImageBitmap(userBitmap);
        } catch (Exception e) {
            System.out.println("Error while creating bitmap: " + e.getMessage());
        }
    }

    public void handleDriverQR() {
        qrScanButton.setVisibility(View.VISIBLE);
        System.out.println("handleDriverQR, qrScanButton vis: " + qrScanButton.getVisibility());

        qrScanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                qrScan.initiateScan();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            //if qrcode has nothing in it
            if (result.getContents() == null) {
                Toast.makeText(getActivity(), "Result Not Found", Toast.LENGTH_LONG).show();
            } else {
                System.out.println("Scanned Results: " + result.getContents());
            }
        }
    }

}
