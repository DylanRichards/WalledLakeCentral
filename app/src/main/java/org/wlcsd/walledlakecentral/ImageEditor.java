package org.wlcsd.walledlakecentral;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PointF;
import android.media.FaceDetector;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

public class ImageEditor extends AppCompatActivity {

    private EditorView editorView;
    private Uri uri;

    private Bitmap mBitmap, mFaceBitmap;

    private int mFaceWidth;
    private int mFaceHeight;
    private static final int MAX_FACES = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_editor);

        displayRawImage();

        detectFace();

    }


    private void displayRawImage() {
        editorView = (EditorView) findViewById(R.id.imageEdit);

        uri = Uri.parse(getIntent().getStringExtra("imgUri"));

        mBitmap = BitmapFactory.decodeFile(uri.getPath());

        if(mBitmap != null){
            mFaceBitmap = mBitmap.copy(Bitmap.Config.RGB_565, true);
            mBitmap.recycle();

            mFaceWidth = mFaceBitmap.getWidth();
            mFaceHeight = mFaceBitmap.getHeight();

        }


    }

    public void detectFace() {
        FaceDetector fd;
        FaceDetector.Face[] faces = new FaceDetector.Face[MAX_FACES];
        PointF midpoint = new PointF();
        int[] fpx, fpy;
        int count;
        String faceData;


        try {
            fd = new FaceDetector(mFaceWidth, mFaceHeight, MAX_FACES);
            count = fd.findFaces(mFaceBitmap, faces);
        } catch (Exception e) {
            return;
        }


// check if we detect any faces
        if (count > 0) {
            fpx = new int[count];
            fpy = new int[count];



            for (int i = 0; i < count; i++) {
                try {
                    faces[i].getMidPoint(midpoint);


                    fpx[i] = (int)midpoint.x;
                    fpy[i] = (int)midpoint.y;
                    faceData = "Count: " + count + '\n' +
                            "Confidence: " + faces[i].confidence() + '\n' +
                            "Eyes Distance: " + faces[i].eyesDistance();

                    Log.d("TAG", faceData);

                    drawViking(fpx[i], fpy[i], faces[i].eyesDistance());
                } catch (Exception e) {
                }
            }
        }

    }

    private void drawViking(int x, int y, float dist) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_image_editor, menu);
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
}
