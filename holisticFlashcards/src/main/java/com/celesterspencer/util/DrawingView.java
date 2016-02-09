package com.celesterspencer.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.graphics.PorterDuff;

// source: http://code.tutsplus.com/tutorials/android-sdk-create-a-drawing-app-touch-interaction--mobile-19202
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
	private float brushSize;
	
	
	public DrawingView(Context context, AttributeSet attrs){
	    super(context, attrs);
	    setupDrawing();
	}
	
	
	
	private void setupDrawing(){
		drawPath = new Path();
		drawPaint = new Paint(); 
		drawPaint.setColor(paintColor);  
		// initial path properties
		drawPaint.setAntiAlias(true);
		drawPaint.setStrokeWidth(20);
		drawPaint.setStyle(Paint.Style.STROKE);
		drawPaint.setStrokeJoin(Paint.Join.ROUND);
		drawPaint.setStrokeCap(Paint.Cap.ROUND);
		// instantiating the canvas paint object
		canvasPaint = new Paint(Paint.DITHER_FLAG);
		brushSize = 10;
	}
	
	
	
	// view functions as a drawing view
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		// instantiating canvas and bitmap
		canvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
		drawCanvas = new Canvas(canvasBitmap);
	}
	
	
	
	// draw canvas and path
	@Override
	protected void onDraw(Canvas canvas) {
		canvas.drawBitmap(canvasBitmap, 0, 0, canvasPaint);
		canvas.drawPath(drawPath, drawPaint);
	}
	
	
	
	// listen for touch events
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (event.getPointerCount() > 1) {
			clear();
		}else {
			// get finger position
			float touchX = event.getX();
			float touchY = event.getY();
			// check for events
			switch (event.getAction()) {
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
		}
		// invalidate view
		invalidate();
		return true;
	}
	
	
	
	public void setColor(String newColor){
		invalidate();
		paintColor = Color.parseColor(newColor);
		drawPaint.setColor(paintColor);
	}
	
	
	
	public void setBrushSize(float newSize){
		float pixelAmount = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
			    newSize, getResources().getDisplayMetrics());
		brushSize=pixelAmount;
		drawPaint.setStrokeWidth(brushSize);
	}

	public void clear(){
		if (drawCanvas != null) drawCanvas.drawColor(0, PorterDuff.Mode.CLEAR);
	    invalidate();
	}
	
}
