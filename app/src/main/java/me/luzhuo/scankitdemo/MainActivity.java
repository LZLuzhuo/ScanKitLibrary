package me.luzhuo.scankitdemo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import me.luzhuo.lib_qrcode.QRCodeCallback;
import me.luzhuo.lib_qrcode.QRCodeManager;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;


public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = findViewById(R.id.imageView2);
    }

    public void onCreateQrCode1(View view) {
        Bitmap bitmap = QRCodeManager.getInstance().createQrCode("这是二维码内容", 700, 700);
        imageView.setImageBitmap(bitmap);
    }

    public void onCreateQrCode2(View view) {
        Bitmap bitmap = QRCodeManager.getInstance().createQrCode("这是二维码内容", 0, 0xFF0000FF, 0xFFFFFFFF, 700, 700);
        imageView.setImageBitmap(bitmap);
    }

    @SuppressLint("MissingPermission")
    public void onScanPlusStyle(View view) {
        QRCodeManager.getInstance().openScanDefaultView(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        String scanDefaultViewData = QRCodeManager.getInstance().parseScanDefaultViewData(requestCode, resultCode, data);
        Log.e(TAG, "" + scanDefaultViewData);
    }

    @SuppressLint("MissingPermission")
    public void onStyleA(View view) {
        QRCodeManager.getInstance().openScan(this, QRCodeManager.QRCodeStyleA, new QRCodeCallback() {
            @Override
            public void onQRResult(@NonNull String result) {
                Log.e(TAG, "" + result);
            }
        });
    }
}