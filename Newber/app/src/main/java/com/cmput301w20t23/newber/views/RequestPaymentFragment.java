package com.cmput301w20t23.newber.views;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.cmput301w20t23.newber.R;
import com.cmput301w20t23.newber.controllers.RideController;
import com.cmput301w20t23.newber.controllers.UserController;
import com.cmput301w20t23.newber.models.RideRequest;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.google.zxing.WriterException;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;

import static android.app.Activity.RESULT_OK;

public class RequestPaymentFragment extends Fragment {

    private RideRequest rideRequest;
    private String role;
    private RideController rideController = new RideController();
    private UserController userController = new UserController(this.getContext());
    private Button create, scan;
    private Double cost;
    private Bitmap bitmap;
    private QRGEncoder qrEncoder;
    private ImageView qrImage;
    private ImageView qrPicture;
    private BarcodeDetector detector;
    static final int REQUEST_IMAGE_CAPTURE = 1;


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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // View for this fragment
        View view = inflater.inflate(R.layout.request_payment_fragment, container, false);

        create = (Button) view.findViewById(R.id.qr_create);
        scan = (Button) view.findViewById(R.id.qr_scan);
        qrImage = (ImageView)  view.findViewById(R.id.qr_image);
        qrPicture = (ImageView) view.findViewById(R.id.qr_picture);

        if(role.equals("Rider")){
            scan.setVisibility(View.VISIBLE);
        }
        else if(role.equals("Driver")) {
            create.setVisibility(View.INVISIBLE);
        }



        create.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                cost = rideRequest.getCost();
                qrEncoder = new QRGEncoder(Double.toString(cost), null, QRGContents.Type.TEXT, 1500);
                try {
                    bitmap = qrEncoder.encodeAsBitmap();
                    qrImage.setImageBitmap(bitmap);
                } catch (WriterException e){
                    //TODO: catch the exception
                }
            }
        });
        scan.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                dispatchTakePictureIntent();
                //onActivityResult runs automatically after dispatchTakePictureIntent
                BitmapDrawable drawable = (BitmapDrawable) qrPicture.getDrawable();
                bitmap = drawable.getBitmap();
                if(detector.isOperational() && bitmap != null)
                {
                    Frame frame = new Frame.Builder().setBitmap(bitmap).build();
                    SparseArray<Barcode> barcodes = detector.detect(frame);
                    if(barcodes.size() != 0)
                    {
                        Barcode code = barcodes.valueAt(0);
                        TextView text = (TextView) view.findViewById(R.id.testqr);

                    }
                }
            }
        });
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(getActivity() != null) {
            detector = new BarcodeDetector.Builder(getActivity())
                    .setBarcodeFormats(Barcode.DATA_MATRIX | Barcode.QR_CODE)
                    .build();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            qrPicture.setImageBitmap(imageBitmap);
        }
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

}
