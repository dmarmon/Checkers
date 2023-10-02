package com.example.checkers;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.*;

public class SquareLayout extends LinearLayout{
    public SquareLayout(Context context){super(context);}
    public SquareLayout(Context context, AttributeSet attrs){super(context,attrs);}
    public SquareLayout(Context context, AttributeSet attrs,int defStyle){super(context,attrs,defStyle);}
    @Override
    public void onMeasure(int widthMeasureSpec,int heightMeasureSpec){
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int sqSize=Math.min(widthSize,heightSize);
//        Log.v("square",widthSize+" "+heightSize+" "+sqSize);
        int finalSize=MeasureSpec.makeMeasureSpec(sqSize,MeasureSpec.EXACTLY);
        super.onMeasure(finalSize,finalSize);
    }
}
