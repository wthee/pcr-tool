package cn.wthee.pcrtool.utils;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.TextView;

public class StrokeTextView extends com.google.android.material.textview.MaterialTextView {

    private final TextView outlineTextView;
    private TextPaint strokePaint;

    public StrokeTextView(Context context) {
        super(context);
        outlineTextView = new TextView(context);
    }

    public StrokeTextView(Context context, AttributeSet attrs) {
        super(context, attrs);

        outlineTextView = new TextView(context, attrs);
    }

    public StrokeTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        outlineTextView = new TextView(context, attrs, defStyle);
    }

    @Override
    public void setLayoutParams(ViewGroup.LayoutParams params) {
        super.setLayoutParams(params);
        outlineTextView.setLayoutParams(params);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        //设置轮廓文字
        CharSequence outlineText = outlineTextView.getText();
        if (outlineText == null || !outlineText.equals(this.getText())) {
            outlineTextView.setText(getText());
            postInvalidate();
        }
        outlineTextView.measure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        outlineTextView.layout(left, top, right, bottom);
    }

    @Override
    protected void onDraw(Canvas canvas) {

        if (strokePaint == null) {
            strokePaint = new TextPaint();
        }
        //复制原来TextViewg画笔中的一些参数
        TextPaint paint = getPaint();
        strokePaint.setTextSize(paint.getTextSize());
        strokePaint.setFlags(paint.getFlags());
        strokePaint.setAlpha(paint.getAlpha());

        //自定义描边效果
        strokePaint.setStyle(Paint.Style.STROKE);
        strokePaint.setColor(Color.parseColor("#ffffff"));
        strokePaint.setStrokeWidth(4);

        String text = getText().toString();

        //在文本底层画出带描边的文本
        canvas.drawText(text, (getWidth() - strokePaint.measureText(text)) / 2,
                getBaseline(), strokePaint);
        super.onDraw(canvas);
    }
}
