package org.wlcsd.walledlakecentral;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.io.File;
import java.io.FilenameFilter;

public class ImageAdapter extends BaseAdapter {
    public static File[] images;

    private static int width = ViewGroup.LayoutParams.MATCH_PARENT;
    private static int height;
    private Context mContext;


    public ImageAdapter(Context c) {
        this.mContext = c;

        this.height = MainActivity.getScreenHeight() / mContext.getResources().getInteger(R.integer.grid_rows);

        images = listValidFiles(new File(MainActivity.imageDirectory));
    }

    private File[] listValidFiles(File file) {
        return file.listFiles(new FilenameFilter() {

            @Override
            public boolean accept(File dir, String filename) {
                File file2 = new File(dir, filename);
                return (filename.contains(".png") || filename.contains(".jpg"))
                        && !file2.isHidden()
                        && !filename.startsWith(".");

            }
        });
    }


    public int getCount() {
        if (images == null) {
            return 1;
        } else {
            return images.length + 1;
        }
    }

    public Object getItem(int position) {
        return images[--position];
    }

    public long getItemId(int position) {
        return position;
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View view, ViewGroup parent) {
        ImageView imageView;

        if (view == null) {
            imageView = new ImageView(mContext);
            imageView.setLayoutParams(new GridView.LayoutParams(width, height));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        } else {
            imageView = (ImageView) view;
        }

        if (position == 0) {
            loadGallery(imageView);
        } else {
            loadPicture(position, imageView);
        }

        return imageView;
    }

    private void loadGallery(ImageView imageView) {
        Glide.with(mContext)
                .load(R.mipmap.ic_photo_library_black_48dp)
                .placeholder(R.drawable.placeholder)
                .into(imageView);
    }

    private void loadPicture(int position, ImageView imageView) {
        Glide.with(mContext)
                .load(images[--position])
                .placeholder(R.drawable.placeholder)
                .into(imageView);
    }

}
