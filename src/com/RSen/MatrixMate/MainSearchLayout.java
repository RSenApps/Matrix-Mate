package com.RSen.MatrixMate;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.CheckBox;
import android.widget.LinearLayout;

public class MainSearchLayout extends LinearLayout {
	public MainSearchLayout(Context context, AttributeSet attributeSet) {

		super(context, attributeSet);
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.edit, this);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

		final int proposedheight = MeasureSpec.getSize(heightMeasureSpec);
		final int actualHeight = getHeight();

		if (actualHeight > proposedheight) {
			((CheckBox) findViewById(R.id.showExtraControls)).setChecked(false);

		} else {
			// Keyboard is hidden
			((CheckBox) findViewById(R.id.showExtraControls)).setChecked(true);
			findViewById(R.id.manipulate).requestFocus();
		}
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}
}