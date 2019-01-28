package com.wall.plz;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.WallpaperManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.PermissionChecker;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;

public class MainActivity extends AppCompatActivity {

    private final static int REQUEST_WRITE_STORAGE_PERMISSION = 1;
    private ImageView defaultWallpaper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initPermission();

        WallpaperManager wallpaperManager = WallpaperManager.getInstance(getApplicationContext());
        Drawable wallpaperDrawable = wallpaperManager.getDrawable();

        defaultWallpaper = findViewById(R.id.imageView);
        defaultWallpaper.setImageDrawable(wallpaperDrawable);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                customSnackbar(view);
            }
        });
    }

    private void wallpaper(Context context, Bitmap image, Boolean save) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        if (save) image.compress(Bitmap.CompressFormat.JPEG, 100, bytes);

        String path = MediaStore.Images.Media.insertImage(context.getContentResolver(),
                image, "-", null);
        Uri.parse(path);

        Intent intent = new Intent(android.content.Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_STREAM, Uri.parse(path));
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.setType("image/png");
        if (save) startActivity(intent);
    }

    private void customSnackbar(final View view) {
        LinearLayout.LayoutParams objLayoutParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        final Snackbar snackbar = Snackbar.make(view, "", Snackbar.LENGTH_LONG);
        Snackbar.SnackbarLayout layout = (Snackbar.SnackbarLayout) snackbar.getView();

        getSystemService(LAYOUT_INFLATER_SERVICE);
        @SuppressLint("InflateParams") View snackView = getLayoutInflater().inflate(
                R.layout.custom_snackbar, null);

        TextView textViewOne = snackView.findViewById(R.id.txtOne);
        textViewOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bitmap bitmap = ((BitmapDrawable) defaultWallpaper.getDrawable()).getBitmap();
                wallpaper(getApplicationContext(), bitmap, true);
                snackbar.dismiss();
            }
        });

        TextView textViewTwo = snackView.findViewById(R.id.txtTwo);
        textViewTwo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bitmap bitmap = ((BitmapDrawable) defaultWallpaper.getDrawable()).getBitmap();
                wallpaper(getApplicationContext(), bitmap, false);
                snackbar.dismiss();
                Snackbar.make(view, R.string.snackbar_saved_wallpaper, Snackbar.LENGTH_SHORT).show();
            }
        });

        layout.addView(snackView, objLayoutParams);
        snackbar.show();
    }

    private void initPermission() {
        if (PermissionChecker
                .checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PermissionChecker.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_WRITE_STORAGE_PERMISSION);
        }
    }
}