package com.example.bitmap;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class BitmapActivity extends AppCompatActivity {
    Button btnFormAssets;
    Button btFormDrawable;
    Button btnQualityCompress;
    Button btnInSampleCompress;
    Button btnScaleCompress;
    Button btRGB565Compress;
    ImageView ivPic;
    Bitmap image = null;
    TextView tvInfo;
    String bitmapInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bitmap);
        btnFormAssets = findViewById(R.id.btnFormAssets);
        btFormDrawable = findViewById(R.id.btnFromDrawable);
        btnQualityCompress = findViewById(R.id.btnQualityCompress);
        btnInSampleCompress = findViewById(R.id.btnInSampleCompress);
        btnScaleCompress = findViewById(R.id.btnScaleCompress);
        btRGB565Compress = findViewById(R.id.btnRGB565Compress);
        ivPic = findViewById(R.id.ivPic);
        tvInfo = findViewById(R.id.tvInfo);
        savePicToDisk(50);
        //显示assets图片
        btnFormAssets.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BitmapInit("pic.jpg");
                ivPic.setImageBitmap(image);
                showInfo(image);
                tvInfo.setText(bitmapInfo);
            }
        });
//显示download下面图片
        btFormDrawable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                image = BitmapFactory.decodeResource(getResources(), R.drawable.pic);
                ivPic.setImageBitmap(image);
                showInfo(image);
                tvInfo.setText(bitmapInfo);
            }
        });
//显示质量压缩图片
        btnQualityCompress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BitmapInit("pic.jpg");
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                image.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
                int options = 100;
                while (byteArrayOutputStream.toByteArray().length / 1024 > 100) {
                    byteArrayOutputStream.reset();
                    image.compress(Bitmap.CompressFormat.JPEG, options, byteArrayOutputStream);
                    options -= 10;
                    ByteArrayInputStream isBm = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
                    Bitmap bitmap = BitmapFactory.decodeStream(isBm);
                    ivPic.setImageBitmap(bitmap);
                    showInfo(bitmap);
                    tvInfo.setText(bitmapInfo);
                }
            }
        });
//采样率压缩显示
        btnInSampleCompress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                image = BitmapFactory.decodeResource(getResources(), R.drawable.pic);
                ivPic.setImageBitmap(image);
                showInfo(image);
                tvInfo.setText(bitmapInfo);
                Log.d("zmz-----", "开始进行压缩");
                //开始进行压缩
                BitmapFactory.decodeResource(getResources(), R.drawable.pic, options);
                options.inJustDecodeBounds = false;
                options.inSampleSize = caculateSampleSize(options, 500, 500);
                image = BitmapFactory.decodeResource(getResources(), R.drawable.pic, options);
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        ivPic.setImageBitmap(image);
                        showInfo(image);
                        tvInfo.setText(bitmapInfo);
                        Log.d("zmz-----", "压缩完成");
                    }
                }, 10000);

            }
        });
//缩放压缩
        btnScaleCompress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BitmapInit("pic.jpg");
                //缩放比例
                int radio = 8;
                Bitmap result = Bitmap.createBitmap(image.getWidth() / radio, image.getHeight() / radio,
                        Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(result);
                RectF rectF = new RectF(0, 0, image.getWidth() / radio, image.getHeight() / radio);
                //将原画缩放到矩形上面
                canvas.drawBitmap(image, null, rectF, null);
                ivPic.setImageBitmap(result);
                showInfo(result);
                tvInfo.setText(bitmapInfo);
            }
        });

        //转格式

        btRGB565Compress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inPreferredConfig = Bitmap.Config.RGB_565;
                image = BitmapFactory.decodeResource(getResources(), R.drawable.pic, options);
                ivPic.setImageBitmap(image);
                showInfo(image);
                tvInfo.setText(bitmapInfo);
            }
        });
    }

    /**
     * 计算出所需要压缩的大小
     *
     * @param options
     * @param reqWidth  我们期望的图片的宽，单位px
     * @param reqHeight 我们期望的图片的高，单位px
     * @return
     */
    private int caculateSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        int samplesize = 1;
        int picWitch = options.outWidth;
        int picHeight = options.outHeight;
        if (picHeight > reqHeight || picWitch > reqWidth) {
            int halfPicWidth = picWitch / 2;
            int halfPicHeight = picHeight / 2;
            while (halfPicHeight / samplesize > reqHeight || halfPicWidth / samplesize > reqWidth) {
                samplesize *= 2;
            }
        }
        return samplesize;
    }

    //将图片转换文件存储到download下面
    private void savePicToDisk(int i) {
        BitmapInit("pic.jpg");
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, i, byteArrayOutputStream);
        File file = new File(getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), "pic.jpg");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(file);
            fileOutputStream.write(byteArrayOutputStream.toByteArray());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    //从assets里面读取图片
    private void BitmapInit(String name) {
        AssetManager am = getResources().getAssets();
        InputStream is = null;
        try {
            is = am.open(name);
            image = BitmapFactory.decodeStream(is);

            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //显示图片信息
    private void showInfo(Bitmap image) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        bitmapInfo = "图像宽高:" + image.getWidth() + "*" + image.getHeight() + "\n" +
                "图片格式:" + image.getConfig().name() + "\n" +
                "占用内存大小:" + image.getByteCount() / 1024 + "kb \n" +
                "屏幕的density:" + getResources().getDisplayMetrics().density + "\n" +
                "bitmap.density:" + image.getDensity() + "\n" +
                "Bitmap转换成文件大小:" + byteArrayOutputStream.toByteArray().length / 1024 + "kb";
        try {
            byteArrayOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
