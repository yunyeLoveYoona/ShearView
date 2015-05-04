package com.yun.shearimageview.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ImageView;

public class ZoomImageView extends ImageView {
	private float pointerDownX = 0, pointerDownY = 0, downX = 0, downY = 0;
	private float magnifyNum = 1.0f;
	private float reduceNum = 1.0f;
	private Matrix matrix;
	private boolean isZoom = false;
	private boolean isShear = true;
	private Bitmap oldBitmap;

	public ZoomImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		matrix = new Matrix();
	}

	public ZoomImageView(Context context) {
		super(context);
		matrix = new Matrix();
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			if (pointerDownX == 0 && pointerDownY == 0
					&& event.getPointerCount() > 1) {
				pointerDownX = Math.abs(event.getX(0) - event.getX(1));
				pointerDownY = Math.abs(event.getY(0) - event.getY(1));
				isZoom = true;
			} else {
				downX = event.getX();
				downY = event.getY();
			}
			break;
		case MotionEvent.ACTION_MOVE:
			if (event.getPointerCount() > 1) {
				if (Math.abs(event.getX(0) - event.getX(1)) - pointerDownX > 100
						|| Math.abs(event.getY(0) - event.getY(1))
								- pointerDownY > 100) {
					// ·Å
					pointerDownX = Math.abs(event.getX(0) - event.getX(1));
					pointerDownY = Math.abs(event.getY(0) - event.getY(1));
					magnifyNum = magnifyNum + magnifyNum * 0.1f;
					matrix.postScale(magnifyNum, magnifyNum, event.getX(0)
							+ pointerDownX / 2, event.getY(0) + pointerDownY
							/ 2);
					invalidate();
				} else if (pointerDownX
						- Math.abs(event.getX(0) - event.getX(1)) > 100
						|| pointerDownY
								- Math.abs(event.getY(0) - event.getY(1)) > 100) {
					// Ëõ
					pointerDownX = Math.abs(event.getX(0) - event.getX(1));
					pointerDownY = Math.abs(event.getY(0) - event.getY(1));
					reduceNum = reduceNum - reduceNum * 0.1f;
					matrix.postScale(reduceNum, reduceNum, event.getX(0)
							+ pointerDownX / 2, event.getY(0) + pointerDownY
							/ 2);
					invalidate();
				}
			} else if (!isZoom) {
				matrix.postTranslate((event.getX() - downX),
						(event.getY() - downY));
				invalidate();
				downX = event.getX();
				downY = event.getY();
			}
			break;
		case MotionEvent.ACTION_UP:
			downX = 0;
			downY = 0;
			pointerDownX = 0;
			pointerDownY = 0;
			reduceNum = 1.0f;
			magnifyNum = 1.0f;
			isZoom = false;
			break;
		case MotionEvent.ACTION_POINTER_UP:
			isZoom = true;
			break;
		}
		return true;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		canvas.save();
		BitmapDrawable bitmap = (BitmapDrawable) getDrawable();
		if (oldBitmap == null) {
			oldBitmap = bitmap.getBitmap();
		}
		canvas.drawBitmap(bitmap.getBitmap(), matrix, null);
		if (isShear) {
			matrix.setTranslate(
					(getRight() - getLeft() - bitmap.getIntrinsicWidth()) / 2,
					(getBottom() - getTop() - bitmap.getIntrinsicHeight()) / 2);
			invalidate();
			isShear = false;
		}
	}

	public void shaer(int x, int y, int width, int height) {
		isShear = true;
		Bitmap bitmap = Bitmap.createBitmap(getWidth(), getHeight(),
				Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(bitmap);
		draw(canvas);
		setImageBitmap(Bitmap.createBitmap(bitmap, x, y, width, height));
	}

	public void reset() {
		setImageBitmap(oldBitmap);
		isShear = true;
	}
}
