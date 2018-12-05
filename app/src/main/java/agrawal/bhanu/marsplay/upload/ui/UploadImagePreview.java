package agrawal.bhanu.marsplay.upload.ui;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;


import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import agrawal.bhanu.marsplay.MainActivity;
import agrawal.bhanu.marsplay.R;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import cn.bluemobi.dylan.photoview.library.PhotoView;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link UploadImagePreview.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link UploadImagePreview#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UploadImagePreview extends Fragment implements View.OnTouchListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String ARG_PARAM3 = "param3";
    private static final String ARG_PARAM4 = "param4";
    private static final int PIC_CROP = 1;

    // TODO: Rename and change types of parameters
    private String username;
    private String filePath;
    Boolean editable;


    @BindView(R.id.capturedImage1)
    PhotoView myImage;

    @BindView(R.id.crop)
    ImageView crop;

    Button uploadButton, cancelButton;

    private Unbinder uibinder;
    @BindView(R.id.upload_footer)
    LinearLayout uploadFooter;




    private OnFragmentInteractionListener mListener;
    private float mScaleFactor = 1.0f;
    private ScaleGestureDetector mScaleGestureDetector;
    private Uri uri;
    private float initialWitdh;
    private float initialHeight;
    private File imgFile;
    private NavController navigationController;

    public UploadImagePreview() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static UploadImagePreview newInstance(Bundle args) {
        UploadImagePreview fragment = new UploadImagePreview();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            username = getArguments().getString(ARG_PARAM1);
            filePath = getArguments().getString(ARG_PARAM2);
            try {
                uri = getArguments().getParcelable(ARG_PARAM3);

            }
            catch (Exception e){
                e.printStackTrace();
            }
            editable = getArguments().getBoolean(ARG_PARAM4);
        }
        mScaleGestureDetector = new ScaleGestureDetector(getContext(), new ScaleListener());
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView =  inflater.inflate(R.layout.fragment_upload_image_preview, container, false);
        uibinder = ButterKnife.bind(this, rootView);


        if(!editable){
            uploadFooter.setVisibility(View.GONE);
            crop.setVisibility(View.GONE);
        }


        loadFile();



        uploadButton = (Button) rootView.findViewById(R.id.confirmUpload);


        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {

                    if(uri!= null){
                        mListener.onCofirmUpload( new File(getPath(uri)));

                    }
                    else{
                        mListener.onCofirmUpload(new File(filePath));
                    }



                } catch (Exception exc) {
                    Toast.makeText(getContext(), exc.getMessage(), Toast.LENGTH_SHORT).show();
                }

            }
        });

        cancelButton = (Button) rootView.findViewById(R.id.cancelUpload);

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onCancelUpload(filePath);
            }
        });


        //crop.setVisibility(View.GONE);



        crop.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                new Handler(Looper.myLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        myImage.invalidate();
                        Bitmap original = myImage.getVisibleRectangleBitmap();
                        createFile(original);
                        loadFile();

/*                        Fragment frg = getActivity().getSupportFragmentManager().findFragmentByTag(MainActivity.UPLOAD_PREVIEW_FRAGMENT);
                        Fragment updatedFrg = UploadImagePreview.newInstance("7055553175", filePath, null,  true);
                        final FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                        ft.detach(frg);
                        ft.attach(updatedFrg);
                        ft.commit();*/
                    }
                });

            }
        });
        
        


        return rootView;
    }

    private void loadFile() {
        final File imgFile = new  File(filePath);


        if(imgFile.exists()){


            //Toast.makeText(Upload.this, filePath, Toast.LENGTH_LONG).show();
            setPic();

        }
        else if(uri != null){
            try {
                RequestOptions requestOptions = new RequestOptions();
                requestOptions.placeholder(R.drawable.ic_thumbnail);
                Glide.with(getContext())
                        .setDefaultRequestOptions(requestOptions)
                        .load(uri).into(myImage);
            }
            catch (Exception e){
                Log.d("error", "path not valid");
            }
        }
        else {
            try {
                RequestOptions requestOptions = new RequestOptions();
                requestOptions.placeholder(R.drawable.ic_thumbnail);
                Glide.with(getContext())
                        .setDefaultRequestOptions(requestOptions)
                        .load(filePath).
                        listener(new RequestListener<Drawable>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                return false;
                            }
                        }).
                        into(myImage);
            }
            catch (Exception e){
                Log.d("error", "path not valid");
            }
        }

    }

    private void openCropActivity(Uri ima) {
        try {
            Intent cropIntent = new Intent("com.android.camera.action.CROP");
            cropIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            // indicate image type and Uri
            cropIntent.setDataAndType(ima, "image/*");
            // set crop properties here
            cropIntent.putExtra("crop", true);
            // indicate aspect of desired crop
            cropIntent.putExtra("aspectX", 1);
            cropIntent.putExtra("aspectY", 1);
            // indicate output X and Y
            cropIntent.putExtra("outputX", 128);
            cropIntent.putExtra("outputY", 128);
            // retrieve data on return
            cropIntent.putExtra("return-data", true);
            // start the activity - we handle returning in onActivityResult
            startActivityForResult(cropIntent, PIC_CROP);
        }
        // respond to users whose devices do not support the crop action
        catch (ActivityNotFoundException anfe) {
            // display an error message
            String errorMessage = "Whoops - your device doesn't support the crop action!";
            Toast toast = Toast.makeText(getContext(), errorMessage, Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnEnterVerificationFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        return false;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other com.example.bhanu.feedme.fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onCofirmUpload(File path);
        void onCancelUpload(String path);
        void onBackPressed();
    }



    private void setPic() {

        final File f = new File(filePath);
/*        Picasso.get()
                .load(R.drawable.ic_thumbnail) // thumbnail url goes here
                .into(myImage, new Callback() {
                    @Override
                    public void onSuccess() {
                        Picasso.get()
                                .load(f) // image url goes here
                                .placeholder(myImage.getDrawable())
                                .into(myImage);
                    }

                    @Override
                    public void onError(Exception e) {

                    }

                });*/

        RequestOptions requestOptions = new RequestOptions();
        requestOptions.placeholder(R.drawable.ic_thumbnail);
        Glide.with(getContext())
                .setDefaultRequestOptions(requestOptions)
                .load(f)
                .apply(RequestOptions.skipMemoryCacheOf(true))
                .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.NONE))
                .into(myImage);

    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector scaleGestureDetector) {
            mScaleFactor *= scaleGestureDetector.getScaleFactor();
            mScaleFactor = Math.max(0.1f,
                    Math.min(mScaleFactor, 10.0f));
            myImage.setScaleX(mScaleFactor);
            myImage.setScaleY(mScaleFactor);
            return true;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        uibinder.unbind();
    }



    public String getPath(Uri uri)
    {
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = getContext().getContentResolver().query(uri, projection, null, null, null);
        if (cursor == null) return null;
        int column_index =             cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String s=cursor.getString(column_index);
        cursor.close();
        return s;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PIC_CROP) {
            if (data != null) {
                // get the returned data
                Bundle extras = data.getExtras();
                // get the cropped bitmap
                Bitmap selectedBitmap = extras.getParcelable("data");
                Uri imageUri = getImageUri(getContext(), selectedBitmap);
                Fragment frg = getActivity().getSupportFragmentManager().findFragmentByTag(MainActivity.UPLOAD_PREVIEW_FRAGMENT);
                final FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                ft.detach(frg);
                ft.commit();


                navigationController = Navigation.findNavController(getActivity(), R.id.my_nav_host_fragment);

                Bundle bundle = new Bundle();
                bundle.putString("param1", "7055553175");
                bundle.putString("param2", imageUri.getPath());
                bundle.putParcelable("param3", imageUri);
                bundle.putBoolean("param4", true);

                navigationController.navigate(R.id.uploadImagePreview, bundle);
            }
        }
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    private void createFile(Bitmap bitmap){
        if(uri!= null){
            imgFile =  new File(getPath(uri));
            Long tsLong = System.currentTimeMillis()/1000;
            String ts = tsLong.toString();
            imgFile = new File(imgFile.getPath() + ts );
            filePath = imgFile.getPath();
            uri = null;
        }
        else{
            imgFile = new  File(filePath);
        }

        FileOutputStream out = null;
        try {
            out = new FileOutputStream(imgFile, false);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80 /*ignored for PNG*/, bos);
        byte[] contents = bos.toByteArray();
        try {
            out.write(contents);
            out.flush();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
