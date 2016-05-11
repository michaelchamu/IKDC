package com.example.icarus.ikdc;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by donovan on 5/10/15.
 */
public class DrawingView extends View {

    //drawing path
    private Path drawPath;
    //drawing and canvas paint
    private Paint drawPaint, canvasPaint;
    //initial color
    private int paintColor = 0xFF660000;
    //canvas
    private Canvas drawCanvas;
    //canvas bitmap
    private Bitmap canvasBitmap;

    private boolean erase=false;
    public DrawingView(Context context,AttributeSet attrs){
        super(context, attrs);
        setupDrawing();
    }
    private void setupDrawing(){
        drawPath = new Path();
        drawPaint =  new Paint();

        drawPaint.setColor(paintColor);
        drawPaint.setAntiAlias(true);
        drawPaint.setStrokeWidth(5);
        drawPaint.setStyle(Paint.Style.STROKE);
        drawPaint.setStrokeJoin(Paint.Join.ROUND);
        drawPaint.setStrokeCap(Paint.Cap.ROUND);

        canvasPaint =  new Paint(Paint.DITHER_FLAG);
    }

    @Override
    protected void onSizeChanged(int w,int h, int oldw, int oldh)
    {
        super.onSizeChanged(w,h,oldw,oldh);
        canvasBitmap = Bitmap.createBitmap(w,h,Bitmap.Config.ARGB_8888);
        drawCanvas = new Canvas(canvasBitmap);
    }

    public void setErase(boolean isErase){
        //setErase true or false
        erase = isErase;
        if(erase)
            drawPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        else
            drawPaint.setXfermode(null);
    }

    public void backgroundChange (String loc){

        Drawable image = new BitmapDrawable(getResources(), loc);

        setBackground(image);

    }

    public void backgroundClear(){
        setBackgroundColor(Color.WHITE);
    }

    @Override
    protected void onDraw(Canvas canvas){
        //draw view
        canvas.drawBitmap(canvasBitmap,0,0,canvasPaint);
        canvas.drawPath(drawPath, drawPaint);
    }
    @Override
    public boolean onTouchEvent(MotionEvent event){
        //detect user touch. When user touches ppoint, program
        //moves to that point, when finger is moved a path is drawn
        //along the touch, when finger is raised, the path is reset
        //for next drawing operation
        float touchX = event.getX();
        float touchY = event.getY();

        //capture actions up, down, move
        switch(event.getAction()){
            case MotionEvent.ACTION_DOWN:
                drawPath.moveTo(touchX, touchY);
                break;
            case MotionEvent.ACTION_MOVE:
                drawPath.lineTo(touchX, touchY);
                break;
            case MotionEvent.ACTION_UP:
                drawCanvas.drawPath(drawPath, drawPaint);
                drawPath.reset();
                break;
            default:
                return false;
        }
        invalidate();
        return true;
    }
    public void setColor(String newColor){
        //setColor
        invalidate();
        paintColor = Color.parseColor(newColor);
        drawPaint.setColor(paintColor);
    }
    public void startNew(){
        backgroundClear();
        drawCanvas.drawColor(0,PorterDuff.Mode.CLEAR);
        invalidate();
    }
}
