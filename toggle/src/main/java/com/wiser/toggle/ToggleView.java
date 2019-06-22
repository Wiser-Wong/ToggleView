package com.wiser.toggle;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewParent;

/**
 * @author Wiser
 * 
 *         自定义开关按钮
 */
public class ToggleView extends View {

	private final int			CANVAS_BAR_COLOR		= 10;				// 绘制Bar颜色

	private final int			CANVAS_BAR_DRAWABLE		= 11;				// 绘制BarDrawable

	private int					barCanvasModel			= CANVAS_BAR_COLOR;	// 绘制游标Bar模式

	private Paint				backgroundPaint;							// 底色画笔

	private Paint				framePaint;									// 边框画笔

	private Paint				barPaint;									// 开关画笔

	private int					backgroundCloseColor	= Color.GRAY;		// 背景色

	private int					backgroundOpenColor		= Color.GREEN;		// 背景色

	private int					barColor				= Color.RED;		// 开关背景色

	private int					barShadowColor			= Color.BLACK;		// bar阴影颜色

	private int					frameColor				= Color.GRAY;		// 边框颜色

	private float				frameWidth;									// 边框宽度

	private float				toggleRadius			= 40;				// 圆角度

	private float				barHeight				= toggleRadius * 2;	// 开关高度

	private float				backgroundLength;							// 背景长度

	private float				barPadding				= 5;				// 开关与背景间距

	private float				barShadowPadding		= 20;				// 开关与背景间距

	private Drawable			barDrawable;								// 游标图片

	private RectF				backgroundRectF;							// 背景矩阵

	private RectF				leftArgRectF;								// 左半圆矩阵

	private RectF				rightArgRectF;								// 右半圆矩阵

	private RectF				barRectF;									// 开关矩阵

	private float				downX, downY;								// 按下坐标XY 以及记录移动位置

	private boolean				isToggle;									// 是否开关打开

	private boolean				isToggleFrame;								// 是否有边框

	private boolean				isHasShadow;								// 是否有阴影

	private float				mOffset;									// 偏移量

	private OnToggleListener	onToggleListener;							// 监听

	public ToggleView(Context context) {
		super(context);
		init(context, null);
	}

	public ToggleView(Context context, @Nullable AttributeSet attrs) {
		super(context, attrs);
		init(context, attrs);
	}

	private void init(Context context, AttributeSet attrs) {
		setLayerType(View.LAYER_TYPE_SOFTWARE, null); // 关闭硬件加速
		this.setWillNotDraw(false); // 调用此方法后，才会执行 onDraw(Canvas) 方法

		TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ToggleView);
		backgroundCloseColor = typedArray.getColor(R.styleable.ToggleView_toggleBackgroundCloseColor, getResources().getColor(android.R.color.darker_gray));
		backgroundOpenColor = typedArray.getColor(R.styleable.ToggleView_toggleBackgroundOpenColor, getResources().getColor(android.R.color.holo_green_dark));
		barColor = typedArray.getColor(R.styleable.ToggleView_toggleBarColor, getResources().getColor(android.R.color.white));
		barPadding = typedArray.getDimension(R.styleable.ToggleView_toggleBarPadding, barPadding);
		toggleRadius = typedArray.getDimension(R.styleable.ToggleView_toggleRadius, toggleRadius);
		frameWidth = typedArray.getDimension(R.styleable.ToggleView_toggleFrameWidth, frameWidth);
		frameColor = typedArray.getColor(R.styleable.ToggleView_toggleFrameColor, getResources().getColor(android.R.color.darker_gray));
		isToggleFrame = typedArray.getBoolean(R.styleable.ToggleView_toggleIsFrame, isToggleFrame);
		isHasShadow = typedArray.getBoolean(R.styleable.ToggleView_toggleIsHasShadow, isHasShadow);
		barShadowColor = typedArray.getColor(R.styleable.ToggleView_toggleShadowColor, getResources().getColor(android.R.color.darker_gray));
		barShadowPadding = typedArray.getDimension(R.styleable.ToggleView_toggleShadowPadding, barShadowPadding);
		int barSrcId = typedArray.getResourceId(R.styleable.ToggleView_toggleBarSrc, -1);

		typedArray.recycle();

		initBarCanvasMode(barSrcId);

		initPaint();

		initRectF();
	}

	// 初始化Bar绘制模式
	private void initBarCanvasMode(int barSrcId) {
		if (barSrcId > 0) {
			barDrawable = getResources().getDrawable(barSrcId);
			// 游标drawable下资源Drawable
			if (barDrawable == null) {
				barCanvasModel = CANVAS_BAR_COLOR;
			} else {
				barCanvasModel = CANVAS_BAR_DRAWABLE;
				toggleRadius = (float) barDrawable.getIntrinsicHeight() / 2;
			}
		} else {
			barCanvasModel = CANVAS_BAR_COLOR;
		}

		barHeight = 2 * toggleRadius;

		if (!isToggleFrame) frameWidth = 0;

		backgroundLength = barHeight * 2f + 2f * barPadding + 2f * frameWidth;

		if (barShadowPadding > barPadding) barShadowPadding = barPadding;
	}

	private void initPaint() {
		backgroundPaint = new Paint();
		backgroundPaint.setStyle(Paint.Style.FILL);
		backgroundPaint.setAntiAlias(true);
		backgroundPaint.setColor(backgroundCloseColor);
		backgroundPaint.setTextAlign(Paint.Align.CENTER);

		barPaint = new Paint();
		barPaint.setStyle(Paint.Style.FILL);
		barPaint.setAntiAlias(true);
		barPaint.setColor(barColor);
		barPaint.setTextAlign(Paint.Align.CENTER);

		framePaint = new Paint();
		framePaint.setStyle(Paint.Style.STROKE);
		framePaint.setStrokeWidth(frameWidth);
		framePaint.setAntiAlias(true);
		framePaint.setColor(frameColor);
		framePaint.setTextAlign(Paint.Align.CENTER);
	}

	private void initRectF() {
		backgroundRectF = new RectF();
		barRectF = new RectF();
		leftArgRectF = new RectF();
		rightArgRectF = new RectF();
	}

	@Override protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
		super.onLayout(changed, left, top, right, bottom);
		int width = right - left;
		// 左半圆矩阵
		leftArgRectF.set(getPaddingLeft() + frameWidth, getPaddingTop() + frameWidth, getPaddingLeft() + frameWidth + (toggleRadius + barPadding) * 2f,
				getPaddingTop() + frameWidth + (toggleRadius + barPadding) * 2f);
		// 右半圆矩阵
		rightArgRectF.set(width - getPaddingRight() - (toggleRadius + barPadding) * 2f - frameWidth, getPaddingTop() + frameWidth, width - getPaddingRight() - frameWidth,
				getPaddingTop() + frameWidth + (toggleRadius + barPadding) * 2f);
		// 背景矩阵
		backgroundRectF.set(leftArgRectF.right - toggleRadius - barPadding - 2, leftArgRectF.top, rightArgRectF.right - toggleRadius - barPadding + 2,
				getPaddingTop() + frameWidth + (toggleRadius + barPadding) * 2f);
		// 开关矩阵
		barRectF.set(leftArgRectF.left + barPadding, leftArgRectF.top + barPadding, leftArgRectF.left + 2f * (toggleRadius + barPadding), leftArgRectF.bottom - barPadding);
	}

	@Override protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if (isToggle) {
			backgroundPaint.setColor(backgroundOpenColor);
		} else {
			backgroundPaint.setColor(backgroundCloseColor);
		}
		setRect();
		// 左半圆矩阵
		canvas.drawArc(leftArgRectF, 90, 180, false, backgroundPaint);
		// 画背景
		canvas.drawRect(backgroundRectF, backgroundPaint);
		// 右半圆矩阵
		canvas.drawArc(rightArgRectF, -90, 180, false, backgroundPaint);
		if (isToggleFrame && frameWidth > 0) {
			// 左半圆矩阵
			canvas.drawArc(leftArgRectF, 90, 180, false, framePaint);
			// 画上线
			canvas.drawLine(leftArgRectF.left + barPadding + toggleRadius, leftArgRectF.top, rightArgRectF.left + barPadding + toggleRadius, leftArgRectF.top, framePaint);
			// 画下线
			canvas.drawLine(leftArgRectF.left + barPadding + toggleRadius, leftArgRectF.bottom, rightArgRectF.left + barPadding + toggleRadius, leftArgRectF.bottom, framePaint);
			// 右半圆矩阵
			canvas.drawArc(rightArgRectF, -90, 180, false, framePaint);
		}
		// 画阴影
		if (isHasShadow) {
			barPaint.setShadowLayer(barShadowPadding, 0, 0, barShadowColor);
			if (barDrawable != null) canvas.drawCircle(barRectF.left + toggleRadius, barRectF.top + toggleRadius, toggleRadius, barPaint);
		}
		// 画开关
		switch (barCanvasModel) {
			case CANVAS_BAR_DRAWABLE:// 绘制Drawable
				if (barDrawable == null) canvas.drawCircle(barRectF.left + toggleRadius, barRectF.top + toggleRadius, toggleRadius, barPaint);
				else {
					barDrawable.setBounds((int) barRectF.left, (int) barRectF.top, (int) barRectF.right + 10, (int) barRectF.bottom);
					barDrawable.draw(canvas);
				}
				break;
			case CANVAS_BAR_COLOR:// 绘制颜色
				canvas.drawCircle(barRectF.left + toggleRadius, barRectF.top + toggleRadius, toggleRadius, barPaint);
				break;
		}
		if (isHasShadow) {
			barPaint.clearShadowLayer();
		}
	}

	private void setRect() {
		barRectF.left = mOffset + leftArgRectF.left + barPadding;
		barRectF.right = mOffset + barHeight + leftArgRectF.left;
	}

	// 自动滚动到结束位置
	private void scrollToEnd() {
		ValueAnimator animator;
		if (isToggle) {
			animator = ValueAnimator.ofFloat(0, backgroundLength - 2f * (barPadding + toggleRadius) - 2f * frameWidth);
		} else {
			animator = ValueAnimator.ofFloat(backgroundLength - 2f * (barPadding + toggleRadius) - 2f * frameWidth, 0);
		}
		animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

			@Override public void onAnimationUpdate(ValueAnimator animation) {
				mOffset = (float) animation.getAnimatedValue();
				postInvalidate();
			}
		});
		animator.setDuration(250);
		animator.start();
	}

	public boolean isToggle() {
		return isToggle;
	}

	public void openToggle() {
		if (isToggle) return;
		isToggle = true;
		scrollToEnd();
		if (onToggleListener != null) onToggleListener.toggle(isToggle);
	}

	public void closeToggle() {
		if (!isToggle) return;
		isToggle = false;
		scrollToEnd();
		if (onToggleListener != null) onToggleListener.toggle(isToggle);
	}

	public void setOnToggleListener(OnToggleListener onToggleListener) {
		this.onToggleListener = onToggleListener;
	}

	@Override protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int widthMode = MeasureSpec.getMode(widthMeasureSpec);
		int widthSize = MeasureSpec.getSize(widthMeasureSpec);
		int heightMode = MeasureSpec.getMode(heightMeasureSpec);
		int heightSize = MeasureSpec.getSize(heightMeasureSpec);
		int height = (int) (heightMode == MeasureSpec.UNSPECIFIED ? (barHeight + barPadding * 2 + 2 * frameWidth + getPaddingTop() + getPaddingBottom())
				: heightMode == MeasureSpec.EXACTLY ? heightSize : Math.min(barHeight + barPadding * 2 + 2 * frameWidth + getPaddingTop() + getPaddingBottom(), heightSize));
		int width = (int) (widthMode == MeasureSpec.UNSPECIFIED ? (backgroundLength + getPaddingLeft() + getPaddingRight())
				: widthMode == MeasureSpec.EXACTLY ? widthSize : Math.min(backgroundLength + getPaddingLeft() + getPaddingRight(), widthSize));
		setMeasuredDimension(width, height);
	}

	@SuppressLint("ClickableViewAccessibility") @Override public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				downTouch();
				downX = event.getX();
				downY = event.getY();
				break;
			case MotionEvent.ACTION_MOVE:
				break;
			case MotionEvent.ACTION_CANCEL:
			case MotionEvent.ACTION_UP:
				upTouch();
				isToggle = !isToggle;
				scrollToEnd();
				if (onToggleListener != null) onToggleListener.toggle(isToggle);
				break;
		}
		return true;
	}

	// 按下控件处理拦截事件
	private void downTouch() {
		ViewParent parent = getParent();
		if (parent != null) {
			parent.requestDisallowInterceptTouchEvent(true);
		}
	}

	// 抬起控件处理拦截事件
	private void upTouch() {
		ViewParent parent = getParent();
		if (parent != null) {
			parent.requestDisallowInterceptTouchEvent(false);
		}
	}

	@Override protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		detach();
	}

	private void detach() {
		backgroundPaint = null;
		barPaint = null;
		backgroundRectF = null;
		barRectF = null;
	}
}
