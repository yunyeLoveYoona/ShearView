package com.yun.shearimageview.view;

import android.R;
import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.RelativeLayout;

public class ShearView extends RelativeLayout {
	public ZoomImageView imageView;
	private RectView rectView;

	public ShearView(Context context, AttributeSet attrs) {
		super(context, attrs);
		imageView = new ZoomImageView(context);
		LayoutParams prams = new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT);
		prams.addRule(CENTER_IN_PARENT, 1);
		imageView.setLayoutParams(prams);
		imageView.setScaleType(ScaleType.CENTER);
		imageView.setAdjustViewBounds(true);
		addView(imageView);
		rectView = new RectView(context);
		addView(rectView);
		setBackgroundColor(getResources().getColor(R.color.black));
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		LayoutParams prams1 = new LayoutParams(getWidth(), getHeight() / 2);
		prams1.addRule(CENTER_IN_PARENT, 1);
		rectView.setLayoutParams(prams1);
	}

	public void shear() {
		imageView.shaer(rectView.getLeft(), rectView.getTop(),
				rectView.getRight() - rectView.getLeft(), rectView.getBottom()
						- rectView.getTop());
	}
	public void reset(){
		imageView.reset();
	}
}
