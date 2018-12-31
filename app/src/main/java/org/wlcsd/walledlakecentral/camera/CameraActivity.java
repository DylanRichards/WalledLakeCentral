package org.wlcsd.walledlakecentral.camera;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Surface;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import org.wlcsd.walledlakecentral.ImageEditor;
import org.wlcsd.walledlakecentral.MainActivity;
import org.wlcsd.walledlakecentral.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CameraActivity extends AppCompatActivity implements View.OnClickListener, ActivityCompat.OnRequestPermissionsResultCallback {

    private static Camera mCamera;
    private CameraPreview mPreview;
    private RelativeLayout cameraControlsLayout;
    private ImageButton imgBtnCapture, imgBtnSwitchCamera;
    private File pictureFile;

    private static final int MEDIA_TYPE_IMAGE = 1;
    public static int cameraId;
    private static int rotation;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        cameraPermission();

        displayCamera();
    }

    private void displayCamera() {
        // Create an instance of Camera
        mCamera = getCameraInstance();
        setCameraDisplayOrientation(this, cameraId, mCamera);

        // Create our Preview view and set it as the content of our activity.
        mPreview = new CameraPreview(this, mCamera);
        FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
        preview.addView(mPreview);

        cameraControlsLayout = (RelativeLayout) findViewById(R.id.camera_controls);
        cameraControlsLayout.bringToFront();

        imgBtnCapture = (ImageButton) findViewById(R.id.btn_capture);
        imgBtnCapture.setOnClickListener(this);

        imgBtnSwitchCamera = (ImageButton) findViewById(R.id.btn_switch_camera);
        imgBtnSwitchCamera.setOnClickListener(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 0: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    displayCamera();
                } else {
                    // permission denied
                }
                return;
            }
        }
    }

    private void cameraPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.CAMERA)) {
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.CAMERA},
                        0);

            }
        } else {
            displayCamera();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_capture:
                mCamera.takePicture(null, null, mPicture);
                break;
            case R.id.btn_switch_camera:
                break;
        }
    }

    public static Camera getCameraInstance(){
        Camera c = null;
        try {
            cameraId = Camera.getNumberOfCameras() - 1;
            c = Camera.open(cameraId); // attempt to get a Camera instance
        }
        catch (Exception e){
            // Camera is not available (in use or does not exist)
            e.printStackTrace();
        }
        return c; // returns null if camera is unavailable
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mCamera != null){
            mCamera.release();
            mPreview.getHolder().removeCallback(mPreview);
            mCamera = null;
        }

    }

    private Camera.PictureCallback mPicture = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
//            new SaveImageTask().execute(data);

            saveImage(data);

            openEditor(Uri.fromFile(pictureFile));
        }
    };

    private void saveImage(byte[] data) {

        pictureFile = getOutputMediaFile(MEDIA_TYPE_IMAGE);
        if (pictureFile == null) {
            Log.d("TAG", "Error creating media file, check storage permissions");
        }

        try {
            FileOutputStream fos = new FileOutputStream(pictureFile);
            Bitmap realImage = BitmapFactory.decodeByteArray(data, 0, data.length);

            switch (rotation){
                case 0:
                    realImage= rotate(realImage, 270);
                    break;
                case 1:
                    realImage= rotate(realImage, 0);
                    break;
                case 2:
                    realImage= rotate(realImage, 90);
                    break;
                case 3:
                    realImage= rotate(realImage, 180);
                    break;
            }

            realImage.compress(Bitmap.CompressFormat.JPEG, 100, fos);

            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            Log.d("TAG", "File not found: " + e.getMessage());
        } catch (IOException e) {
            Log.d("TAG", "Error accessing file: " + e.getMessage());
        }
    }

    public Bitmap rotate(Bitmap bitmap, int degree) {
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();

        Matrix mtx = new Matrix();
        mtx.postRotate(degree);

        return Bitmap.createBitmap(bitmap, 0, 0, w, h, mtx, true);
    }

    private void openEditor(Uri uri) {
        Intent editorIntent = new Intent(this, ImageEditor.class);
        editorIntent.putExtra("imgUri", uri.toString());
        startActivity(editorIntent);
        finish();
    }

    public static void setCameraDisplayOrientation(Activity activity, int cameraId, Camera camera) {
        Camera.CameraInfo info = new Camera.CameraInfo();
        Camera.getCameraInfo(cameraId, info);
        rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
        Log.d("TAG", "Rotation: " + rotation);
        int degrees = 0;
        switch (rotation)
        {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = 90;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;
            case Surface.ROTATION_270:
                degrees = 270;
                break;
        }

        int result;

        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360; // compensate the mirror
        }else { // back-facing
            result = (info.orientation - degrees + 360) % 360;
        }
        camera.setDisplayOrientation(result);
    }

    private static File getOutputMediaFile(int type){

        if (!MainActivity.mediaStorageFile.exists()){
            if (! MainActivity.mediaStorageFile.mkdirs()){
                //failed to create directory
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE){
            mediaFile = new File(MainActivity.imageDirectory + File.separator + "IMG_"+ timeStamp + ".jpg");
        } else {
            return null;
        }

        return mediaFile;
    }


    private class SaveImageTask extends AsyncTask<byte[], Void, Void> {

        @Override
        protected Void doInBackground(byte[]... data) {

            return null;
        }


        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

        }
    }
}
