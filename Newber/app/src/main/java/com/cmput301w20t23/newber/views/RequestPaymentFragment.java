package com.cmput301w20t23.newber.views;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;

import com.cmput301w20t23.newber.R;
import com.cmput301w20t23.newber.controllers.RideController;
import com.cmput301w20t23.newber.controllers.UserController;
import com.cmput301w20t23.newber.models.RideRequest;
import com.google.zxing.WriterException;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;

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

                }
            }
        });

        scan.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                final int REQUEST_IMAGE_CAPTURE = 1;

//                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
//                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
//                }

            }
        });




        return view;
    }
}
