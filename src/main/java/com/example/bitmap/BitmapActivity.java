package com.example.bitmap;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class BitmapActivity extends AppCompatActivity {
Button btnFormAssets;
ImageView ivPic;
Bitmap image=null;
TextView tvInfo;
    String bitmapInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bitmap);
        btnFormAssets=findViewById(R.id.btnFormAssets);
        ivPic=findViewById(R.id.ivPic);
        tvInfo=findViewById(R.id.tvInfo);
        btnFormAssets.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BitmapInit("pic.jpg");
                ivPic.setImageBitmap(image);
                showInfo(image);
                tvInfo.setText(bitmapInfo);
            }
        });
    }

    private void BitmapInit(String name) {
        AssetManager am = getResources().getAssets();
        InputStream is = null;
        try {
            is = am.open(name);
            image= BitmapFactory.decodeStream(is);

            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showInfo(Bitmap image) {
        ByteArrayOutputStream byteArrayOutputStream=new ByteArrayOutputStream();
image.compress(Bitmap.CompressFormat.JPEG,100,byteArrayOutputStream);
 bitmapInfo="图像宽高:"+image.getWidth()+"*"+image.getHeight()+"\n"+
        "图片格式:"+image.getConfig().name()+"\n"+
        "占用内存大小:"+image.getByteCount()/1024+"kb \n"+
        "屏幕的density:"+getResources().getDisplayMetrics().density+"\n"+
        "bitmap.density:"+image.getDensity()+"\n"+
        "Bitmap转换成文件大小:"+byteArrayOutputStream.toByteArray().length/1024+"kb";
        try {
            byteArrayOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
