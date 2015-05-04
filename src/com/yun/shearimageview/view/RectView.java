package com.yun.shearimageview.view;

import android.R;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.ImageView;

public class RectView extends ImageView {
	public RectView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
    
	public RectView(Context context) {
		super(context);
	}
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		Paint paint = new Paint();
		paint.setColor(getResources().getColor(R.color.darker_gray));
		paint.setStrokeWidth(1);
		paint.setStyle(Paint.Style.STROKE);
		canvas.drawRect(10, 10, getMeasuredWidth() - 10,
				getMeasuredHeight() - 10, paint);
	}
}

