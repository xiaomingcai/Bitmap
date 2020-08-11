package com.example.bitmap.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapRegionDecoder;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.example.bitmap.event.MoveGestureDetector;

import java.io.IOException;
import java.io.InputStream;

public class LargeImageView extends View {

    private BitmapRegionDecoder mDecoder;
    private static final BitmapFactory.Options mDecodeOption = new BitmapFactory.Options();
    private MoveGestureDetector mMoveGestureDetector;

    static {
        mDecodeOption.inPreferredConfig = Bitmap.Config.RGB_565;
    }

    private Rect mRect = new Rect();
    private int mImageWidth;
    private int mImageHeight;

    public LargeImageView(Context context) {
        super(context);
        //   init();
    }

    /*   public  LargeImageView(Context context, AttributeSet attrs,int def){
           super(context,attrs,def);
           init();
       }*/
    public LargeImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public void getImageInputStream(InputStream is) {
        try {
            mDecoder = BitmapRegionDecoder.newInstance(is, false);
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(is, null, options);
            mImageHeight = options.outHeight;
            mImageWidth = options.outWidth;
            requestLayout();
            invalidate();

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void init() {
        mMoveGestureDetector = new MoveGestureDetector(getContext(),
                new MoveGestureDetector.SimpleMoveGestureDetector() {
                    @Override
                    public boolean onMove(MoveGestureDetector detector) {
                        Log.d("zmz-----", "init()");
                        int movX = (int) detector.getMoveX();
                        int movY = (int) detector.getMoveY();
                        if (mImageWidth > getWidth()) {
                            mRect.offset(-movX, 0);
                            checkWidth();
                            invalidate();
                        }
                        if (mImageHeight > getHeight()) {
                            mRect.offset(0, -movY);
                            checkHeight();
                            invalidate();
                        }
                        return true;
                    }
                });


    }

    private void checkHeight() {
        if (mRect.bottom > mImageHeight) {
            mRect.bottom = mImageHeight;
            mRect.top = mRect.bottom - getHeight();
        }
        if (mRect.top < 0) {
            mRect.top = 0;
            mRect.bottom = mRect.top + getHeight();
        }
    }

    private void checkWidth() {
        if (mRect.right > mImageWidth) {
            mRect.right = mImageWidth;
            mRect.left = mImageWidth - getWidth();
        }
        if (mRect.left < 0) {
            mRect.left = 0;
            mRect.right = mRect.left + getWidth();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mMoveGestureDetector.onTouchEvent(event);
        return true;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = getMeasuredWidth();
        int height = getMeasuredHeight();
        mRect.left = mImageWidth / 2 - width / 2;
        mRect.top = mImageHeight / 2 - height / 2;
        mRect.right = mRect.left + width;
        mRect.bottom = mRect.top + height;
    }

    @Override
    protected void onDraw(Canvas canvas) {

        Bitmap bitmap = mDecoder.decodeRegion(mRect, mDecodeOption);
        canvas.drawBitmap(bitmap, 0, 0, null);
    }
}
