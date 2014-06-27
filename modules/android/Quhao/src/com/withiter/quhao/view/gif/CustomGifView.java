package com.withiter.quhao.view.gif;

import com.withiter.quhao.R;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Movie;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.view.View;

public class CustomGifView extends View 
{

	private Movie mMovie;
	
	private long mMovieStart;
	
	public CustomGifView(Context context,AttributeSet attributeSet) 
	{
		super(context, attributeSet);
		mMovie = Movie.decodeStream(getResources().openRawResource(R.drawable.loading));
	}

	@Override
	protected void onDraw(Canvas canvas) 
	{
		long now = SystemClock.uptimeMillis();
		
		if (mMovieStart == 0) 
		{
			mMovieStart = now;
		}
		
		if (mMovie != null) 
		{
			int dur = mMovie.duration();
			if (dur == 0) 
			{
				dur = 1000;
			}
			
			int relTime = (int) ((now - mMovieStart)%dur);
			
			mMovie.setTime(relTime);
			mMovie.draw(canvas, 0, 0);
			invalidate();
			
		}
		
//		super.draw(canvas);
	}
}
