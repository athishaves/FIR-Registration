package com.athishWorks.policeapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Environment;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.io.File;
import java.io.FileOutputStream;

public class PaintView extends View {

    private Bitmap mBitmap;
    private Canvas mCanvas;
    private Paint mPaint;
    private Path mPath;

    public PaintView(Context context) {
        super(context, null);
    }

    public PaintView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        mPath = new Path();
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(Color.BLACK);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeWidth(5f);
    }

    public void initialise (DisplayMetrics displayMetrics) {
        int height = 15*displayMetrics.heightPixels/100;
        int width = 98*displayMetrics.widthPixels/100;
        mBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        mCanvas = new Canvas(mBitmap);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.save();
        mCanvas.drawColor(Color.WHITE);
        mCanvas.drawPath(mPath, mPaint);
        canvas.drawBitmap(mBitmap, 0, 0, mPaint);
        canvas.restore();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float xPos = event.getX();
        float yPos = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mPath.moveTo(xPos, yPos);
                return true;
            case MotionEvent.ACTION_MOVE:
                mPath.lineTo(xPos, yPos);
                break;
            case MotionEvent.ACTION_UP:
                break;
            default:
                return false;
        }
        invalidate();
        return true;
    }

    public void saveImage(String complaintNumber) {
        String filePath = Environment.getExternalStorageDirectory().getPath() + "/FIR";
        File file = new File(filePath);
        if (!file.exists() && !file.mkdir()) {
            callAToast("FIR Folder couldn't be created");
        }

        filePath += "/Signatures";
        file = new File(filePath);
        if (!file.exists() && !file.mkdir()) {
            callAToast("Signature Folder couldn't be created");
        }

        if (file.exists()) {
            filePath += "/" + complaintNumber + ".png";
            Log.i("Picture", "FilePath --> " +  filePath);
            file = new File(filePath);
            FileOutputStream fileOutputStream;

            try {
                fileOutputStream = new FileOutputStream(file);
                mBitmap.compress(Bitmap.CompressFormat.PNG, 50, fileOutputStream);
                fileOutputStream.flush();
                fileOutputStream.close();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void callAToast(String a) {
        Toast.makeText(getContext(), a, Toast.LENGTH_SHORT).show();
    }

    public void clearCanvas() {
        mPath.reset();
        invalidate();
    }

}
