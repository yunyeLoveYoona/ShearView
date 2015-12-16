package com.yun.shearimageview.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ImageView;

import com.yun.imagecheck.ImageCheck;

public class ZoomImageView extends ImageView {
	private float pointerDownX = 0, pointerDownY = 0, downX = 0, downY = 0;
	private float magnifyNum = 1.0f;
	private float reduceNum = 1.0f;
	private Matrix matrix;
	private boolean isZoom = false;
	public boolean isShear = true;
	private Bitmap oldBitmap;
	public boolean isFirst = true;
	public onDrawListenter onDrawListenter;
	public int bitMapWidth = 0;
	public int bitMapHeight = 0;
	private Rect rectTemp;
	private float[] values;
	private float x = 0, y = 0;
	private float right, buttom;
	private boolean isChange = false;
	private boolean isNext = false;
	private boolean isPrv = false;

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
			isChange = false;
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
				isChange = false;
				isPrv = false;
				isNext = false;
				if (Math.abs(event.getX(0) - event.getX(1)) - pointerDownX > 100
						|| Math.abs(event.getY(0) - event.getY(1))
								- pointerDownY > 100) {
					// 放
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
					// 缩
					pointerDownX = Math.abs(event.getX(0) - event.getX(1));
					pointerDownY = Math.abs(event.getY(0) - event.getY(1));
					reduceNum = reduceNum - reduceNum * 0.1f;
					matrix.postScale(reduceNum, reduceNum, event.getX(0)
							+ pointerDownX / 2, event.getY(0) + pointerDownY
							/ 2);
					invalidate();
				}
			} else if (!isZoom) {
				rectTemp = getDrawable().getBounds();
				values = new float[9];
				matrix.getValues(values);
				x = event.getX() - downX;
				y = event.getY() - downY;
				buttom = (values[5] + rectTemp.height() * values[4])
						- getLayoutParams().height;
				right = (values[2] + rectTemp.width() * values[0])
						- getLayoutParams().width;
				if (x > 0) {
					if (values[2] < 0) {
						if (x + values[2] > 0) {
							x = 0 - x;
						} else if (onDrawListenter != null && !isChange
								&& x > 30) {
							isChange = true;
							isPrv = true;
							isNext = false;
						}
					} else {

						if (onDrawListenter != null && !isChange && x > 30) {
							isPrv = true;
							isChange = true;
							isNext = false;
						}
						x = 0;
					}
				} else {
					if (right > 0) {
						if (x + right < 0) {
							x = right;
						} else if (onDrawListenter != null && !isChange
								&& x < -30) {
							isNext = true;
							isChange = true;
							isPrv = false;
						}
					} else {
						if (onDrawListenter != null && !isChange && x < -30) {
							isNext = true;
							isChange = true;
							isPrv = false;
						}
						x = 0;
					}

				}

				if (y > 0) {
					if (buttom < 0) {
						if (buttom + y > 0) {
							y = 0 - buttom;
						}
					} else {
						y = 0;
					}
				} else {
					if (values[5] > 0) {
						if (y + values[5] < 0) {
							y = 0 - values[5];
						}
					} else {
						y = 0;
					}
				}
				if (!isChange) {
					matrix.postTranslate(x, y);
					invalidate();
				}
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
			if (isChange && isPrv && onDrawListenter != null) {
				onDrawListenter.prv();
				isPrv = false;
			}
			if (isChange && isNext && onDrawListenter != null) {
				onDrawListenter.next();
				isPrv = false;
			}
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
		if (bitMapWidth != bitmap.getIntrinsicWidth()) {
			bitMapWidth = bitmap.getIntrinsicWidth();
			bitMapHeight = bitmap.getIntrinsicHeight();
			isShear = true;
		}
		canvas.drawBitmap(bitmap.getBitmap(), matrix, null);
		if (isFirst && (bitmap.getBitmap().getWidth() < 300)) {
			matrix.setTranslate(
					(getRight() - getLeft() - bitmap.getIntrinsicWidth()) / 2,
					(getBottom() - getTop() - bitmap.getIntrinsicHeight()) / 2);
			matrix.postScale(5, 5, getWidth() / 2, getHeight() / 2);
			invalidate();
			isFirst = false;
			isShear = false;
		} else if (isShear) {
			matrix.setTranslate((getRight() - getLeft() - bitMapWidth) / 2,
					(getBottom() - getTop() - bitMapHeight) / 2);
			invalidate();
			isShear = false;
		}
	}

	@Override
	public void setImageResource(int resId) {
		super.setImageResource(resId);
	}

	@Override
	public void setImageDrawable(Drawable drawable) {
		super.setImageDrawable(drawable);
		if (onDrawListenter != null) {
			onDrawListenter.onImageDraw();
		}
	}

	@Override
	public void setImageBitmap(Bitmap bm) {
		super.setImageBitmap(bm);
		if (onDrawListenter != null) {
			onDrawListenter.onImageDraw();
		}
	}

	public void shaer(int x, int y, int width, int height) {
		isShear = true;
		int w = getWidth();
		int h = getHeight();
		Bitmap bitmap = Bitmap.createBitmap(getWidth(), getHeight(),
				Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(bitmap);
		draw(canvas);
		ImageCheck.checkedBitmap = Bitmap.createBitmap(bitmap, x, y, width,
				height);
		bitmap = null;
	}

	public void reset() {
		setImageBitmap(oldBitmap);
		isShear = true;
	}

	public interface onDrawListenter {
		public void onImageDraw();

		public void next();

		public void prv();

	}
}
