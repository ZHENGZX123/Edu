package cn.kiway.activity.main.teaching;


import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ListView;

public class IListView extends ListView{
	/**
	 * 主要用于上课时候的列表，由于直接调用listview会让滑动的事件冲突
	 * */
	private static final int MAXTouchSize=50;//移动超过此距离，再放开就不算点击是事件
	private int mTouchSize;//记录手在此View上滑动多远的距离
	private GestureDetector mDetector;
	private IGestureListener mGestureListener;
	private int verticalMinDistance = 20;
	private int minVelocity         = 0;
	//private Animation pervanim;
	//private Animation nextanim;
	
	private OnPrevPageListener pervPageListener;
	private OnNextPageListener nextPageListener;
	
	public IListView(Context context) {
		super(context);
		init(context);
	}
	
	public IListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}
	
	public IListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}
	
	@SuppressWarnings("deprecation")
	private void init(Context context){
		mGestureListener=new IGestureListener();
		mDetector=new GestureDetector(mGestureListener);
		//pervanim=AnimationUtils.loadAnimation(IListView.this.getContext(), android.R.anim.fade_in);
		//nextanim=AnimationUtils.loadAnimation(IListView.this.getContext(), android.R.anim.fade_out);
	}
	
	public void setPervPageListener(OnPrevPageListener pervPageListener) {
		this.pervPageListener = pervPageListener;
	}
	
	public void setNextPageListener(OnNextPageListener nextPageListener) {
		this.nextPageListener = nextPageListener;
	}
	
	@Override
	public boolean performItemClick(View view, int position, long id) {
    	int m=mTouchSize;
    	mTouchSize=0;
    	if(m > MAXTouchSize){
    		return true;
    	}else{
    		return super.performItemClick(view, position, id);
    	}
	}
    
	@SuppressLint("ClickableViewAccessibility")
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_MOVE && event.getHistorySize() > mTouchSize) {
				mTouchSize = event.getHistorySize();
		}
		mDetector.onTouchEvent(event);
		return super.onTouchEvent(event);
	}

	
	private class IGestureListener extends SimpleOnGestureListener{
		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,float velocityY) {
	        if (e1.getX() - e2.getX() > verticalMinDistance && Math.abs(velocityX) > minVelocity) {
	        	if(nextPageListener!=null){
	        		nextPageListener.onNextPage();
	        		//IListView.this.startAnimation(nextanim);//滑动动画
	        	}
	        } else if (e2.getX() - e1.getX() > verticalMinDistance && Math.abs(velocityX) > minVelocity) {
	        	if(pervPageListener!=null){
	        		pervPageListener.onPrevPage();
	        		//IListView.this.startAnimation(pervanim);//滑动动画
	        	}
	        }
			return false;
		}
	}
	
	public interface OnPrevPageListener {
        void onPrevPage();
    }
	
	public interface OnNextPageListener {
        void onNextPage();
    }
}
