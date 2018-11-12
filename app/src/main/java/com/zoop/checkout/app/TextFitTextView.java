package com.zoop.checkout.app;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.TextView;
 
import android.content.Context;
import android.os.Handler;
import android.text.Layout;
import android.text.TextPaint;
import android.text.TextUtils.TruncateAt;
import android.util.AttributeSet;
import android.util.TypedValue;

public class TextFitTextView extends TextView {

	private Handler measureHandler = new Handler();
	private Runnable requestLayout = new Runnable() {
	    @Override
	    public void run() {
	        requestLayout();
	    }
	};
	
	public TextFitTextView(Context context, AttributeSet attrs, int defStyle) {
	    super(context, attrs, defStyle);
	}
	
	public TextFitTextView(Context context, AttributeSet attrs) {
	    super(context, attrs);
	}
	
	public TextFitTextView(Context context) {
	    super(context);
	}
	
	@Override
	protected void onMeasure(final int widthMeasureSpec,
	                         final int heightMeasureSpec) {
	    super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	
	    final float maxWidth = getWidth();
	    final float maxHeight = getHeight();
	    if (maxWidth < 1.0f || maxHeight < 1.0f) {
	        return;
	    }
	
	    int index = 0;
	    int lineCount = 0;
	    CharSequence text = getText();
	    final TextPaint paint = getPaint();
	    while (index < text.length()) {
	        index += paint.breakText(text, index, text.length(), true, maxWidth, null);
	        lineCount++;
	    }
	    final float height = lineCount * getLineHeight() + (lineCount > 0 ?
	            (lineCount - 1) * paint.getFontSpacing() : 0);
	    if (height > maxHeight) {
	        final float textSize = getTextSize();
	        setTextSize(TypedValue.COMPLEX_UNIT_PX, (textSize - 1));
	        measureHandler.post(requestLayout);
	    }
	}
	
	public static int getHeightOfMultiLineText(String text, int textSize, int maxWidth) {
	    TextPaint paint = new TextPaint();
	    paint.setTextSize(textSize);
	    int index = 0;
	    int lineCount = 0;
	    while (index < text.length()) {
	        index += paint.breakText(text, index, text.length(), true, maxWidth, null);
	        lineCount++;
	    }

	    Rect bounds = new Rect();
	    paint.getTextBounds("Yy", 0, 2, bounds);
	    // obtain space between lines
	    double lineSpacing = Math.max(0, ((lineCount - 1) * bounds.height() * 0.25));

	    return (int) Math.floor(lineSpacing + lineCount * bounds.height());
	}	
}
