package com.example.bitmap.event;

import android.content.Context;
import android.graphics.PointF;
import android.util.Log;
import android.view.MotionEvent;

public class MoveGestureDetector {
    private Context mContext;
    private OnMoveGestureListener mListener;
    private PointF mDeltaPointer = new PointF();
    private boolean isGestureMoving;
    private MotionEvent mPreMotionEvent;
    private MotionEvent mCurrentMotionEvent;
    private PointF mPrePointer;
    private PointF mCurPointer;

    public MoveGestureDetector(Context context, OnMoveGestureListener listener) {
        this.mContext = context;
        this.mListener = listener;
    }

    public float getMoveX() {
        return mDeltaPointer.x;
    }

    public float getMoveY() {
        return mDeltaPointer.y;
    }

    public boolean onTouchEvent(MotionEvent event) {
        Log.d("zmz-----", "onTouchEvent");
        if (!isGestureMoving) {
            handleStartEvent(event);
            Log.d("zmz-----", "handleProgressEvent");
        } else {
            handleProgressEvent(event);
            Log.d("zmz-----", "handleProgressEvent");
        }
        return true;
    }

    private void handleProgressEvent(MotionEvent event) {
        switch (event.getAction()) {

            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                mListener.onMoveEnd(this);
                Log.d("zmz-----", "ACTION_CANCEL");
                resetState();
                break;
            case MotionEvent.ACTION_MOVE:
                Log.d("zmz-----", "ACTION_MOVE");
                updateStateByEvent(event);
                if (mListener.onMove(this)) {
                    mPreMotionEvent.recycle();
                    mPreMotionEvent = MotionEvent.obtain(event);
                }
                break;
        }
    }

    private void handleStartEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                resetState();
                mPreMotionEvent = MotionEvent.obtain(event);
                updateStateByEvent(event);
                break;
            case MotionEvent.ACTION_MOVE:
                isGestureMoving = mListener.onMoveBegin(this);
                break;
        }
    }

    private void updateStateByEvent(MotionEvent event) {

        MotionEvent preEvent = mPreMotionEvent;
        mPrePointer = calculateFocalPointer(preEvent);
        mCurPointer = calculateFocalPointer(event);
        boolean skipThisMoveEvent = preEvent.getPointerCount() != event.getPointerCount();
        mDeltaPointer.x = skipThisMoveEvent ? 0 : mCurPointer.x - mPrePointer.x;
        mDeltaPointer.y = skipThisMoveEvent ? 0 : mCurPointer.y - mPrePointer.y;
        Log.d("zmz-----", "mDeltaPointer.x=" + mDeltaPointer.x);
        Log.d("zmz-----", "mDeltaPointer.y=" + mDeltaPointer.y);

    }

    private PointF calculateFocalPointer(MotionEvent event) {
        int count = event.getPointerCount();
        float x = 0, y = 0;
        for (int i = 0; i < count; i++) {
            x += event.getX(i);
            y += event.getY(i);
        }
        x /= count;
        y /= count;
        return new PointF(x, y);
    }

    private void resetState() {
        if (mPreMotionEvent != null) {
            mPreMotionEvent.recycle();
            mPreMotionEvent = null;
        }
        if (mCurrentMotionEvent != null) {
            mCurrentMotionEvent.recycle();
            mCurrentMotionEvent = null;
        }
        isGestureMoving = false;
    }

    public interface OnMoveGestureListener {
        public boolean onMoveBegin(MoveGestureDetector detector);

        public boolean onMove(MoveGestureDetector detector);

        public void onMoveEnd(MoveGestureDetector detector);
    }

    public static class SimpleMoveGestureDetector implements OnMoveGestureListener {
        @Override
        public boolean onMoveBegin(MoveGestureDetector detector) {
            return true;
        }

        @Override
        public boolean onMove(MoveGestureDetector detector) {
            return false;
        }

        @Override
        public void onMoveEnd(MoveGestureDetector detector) {

        }
    }
}
