package org.wlcsd.walledlakecentral;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import android.support.design.widget.FloatingActionButton;

import org.wlcsd.walledlakecentral.camera.CameraActivity;

import java.io.File;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener, View.OnClickListener {

    private static final int GALLERY_ACTIVITY_REQUEST_CODE = 9391;
    private static int screenHeight;

    public static File mediaStorageFile;
    public static String imageDirectory;

    private GridView mGridView;
    private FloatingActionButton mFloatingActionButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mediaStorageFile = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "WLC_Vikings");

        imageDirectory = mediaStorageFile.getPath();

        setScreenHeight();

        mGridView = (GridView) findViewById(R.id.gridview);
        mGridView.setAdapter(new ImageAdapter(getBaseContext()));
        mGridView.setOnScrollListener(new GVScrollListener(this));
        mGridView.setOnItemClickListener(this);

        mFloatingActionButton = (FloatingActionButton) findViewById(R.id.fab);
        mFloatingActionButton.setOnClickListener(this);

        checkCameraHardware();
    }

    public void setScreenHeight() {
        Display display = getWindowManager().getDefaultDisplay();

        if(android.os.Build.VERSION.SDK_INT >= 13){
            Point size = new Point();
            display.getSize(size);
            screenHeight = size.y;
        }else{
            screenHeight = display.getHeight();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        //TODO Remove once added to update only when there is a change
        updateGridViewAdapter();
    }

    public void updateGridViewAdapter(){
        mGridView.setAdapter(new ImageAdapter(getBaseContext()));
    }

    public static int getScreenHeight() {
        return screenHeight;
    }

    private void checkCameraHardware() {
        if (!getBaseContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)){
            // no camera on this device
            mFloatingActionButton.hide();
        } else {
            // this device has a camera
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_settings:
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (position == 0){
            openGallery();
        }else{
            openPicture(Uri.fromFile(ImageAdapter.images[--position]));
        }
    }

    private void openGallery() {
        Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(gallery, GALLERY_ACTIVITY_REQUEST_CODE);
    }

    private void openPicture(Uri uri) {
        Log.d("TAG", uri.toString());

        Intent viewImage = new Intent(this, ImageViewer.class);
        viewImage.putExtra("imgUri", uri.toString());
        startActivity(viewImage);
    }

    private void editPicture(Uri uri) {
        Log.d("TAG", uri.toString());

        Intent editImage = new Intent(this, ImageEditor.class);
        editImage.putExtra("imgUri", uri.toString());
        startActivity(editImage);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.fab:
                Intent cameraIntent = new Intent(this, CameraActivity.class);
                startActivity(cameraIntent);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == RESULT_OK && data != null){
            switch(requestCode){
                case GALLERY_ACTIVITY_REQUEST_CODE:

                    editPicture(data.getData());
                    break;
            }
        }
    }

}
