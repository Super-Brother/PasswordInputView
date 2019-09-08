package com.wenchao.passwordinputview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.text.InputFilter;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatEditText;

/**
 * @author zhang
 */
public class PasswordInputView extends AppCompatEditText {

  /**
   * 密码长度
   */
  private int passwordLength = 6;
  /**
   * 实心圆的半径
   */
  private int mCircleRadius = dip2px(getContext(), 5);
  /**
   * 圆点的画笔
   */
  private Paint mPointPaint;
  /**
   * 边框画笔
   */
  private Paint mStrokePaint;
  /**
   * 分割线画笔
   */
  private Paint mDivideLinePaint;
  /**
   * 圆的颜色   默认BLACK
   */
  private int mPointColor = Color.BLACK;

  /**
   * 边线的颜色
   */
  private int mStrokeColor = Color.BLACK;

  /**
   * 分割线的颜色
   */
  private int mDivideLineColor = Color.BLACK;
  /**
   * 分割线的宽度
   */
  private int mStrokeWidth = dip2px(getContext(), 0.5f);
  /**
   * 描边的矩形
   */
  private RectF mFrameRectF = new RectF();
  /**
   * 控件宽高
   */
  private int mWidth;
  private int mHeight;
  /**
   * 分割线开始的坐标x
   */
  private int mDivideLineWStartX;
  /**
   * 第一个密码实心圆的圆心坐标
   */
  private float mFirstCircleX;
  private float mFirstCircleY;
  /**
   * 矩形边框的圆角
   */
  private int mRectAngle = dip2px(getContext(), 5f);
  /**
   * 当前输入的长度
   */
  private int mCurInputCount = 0;
  /**
   * 输入完成监听
   */
  private OnPasswordCompleteListener mCompleteListener;

  public PasswordInputView(Context context, AttributeSet attrs) {
    super(context, attrs);
    initAttrs(attrs);
    initPaint();

    this.setCursorVisible(false);
    this.setFilters(new InputFilter[]{new InputFilter.LengthFilter(passwordLength)});
  }

  public void setOnCompleteListener(OnPasswordCompleteListener mListener) {
    this.mCompleteListener = mListener;
  }

  private void initAttrs(AttributeSet attrs) {
    TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.PasswordInputView);
    passwordLength = typedArray.getInteger(R.styleable.PasswordInputView_password_length, passwordLength);
    mStrokeColor = typedArray.getColor(R.styleable.PasswordInputView_stroke_color, mStrokeColor);
    mDivideLineColor = typedArray.getColor(R.styleable.PasswordInputView_divide_line_color, mDivideLineColor);
    mPointColor = typedArray.getColor(R.styleable.PasswordInputView_point_color, mPointColor);
    mCircleRadius = (int) typedArray.getDimension(R.styleable.PasswordInputView_point_radius, mCircleRadius);
    mRectAngle = (int) typedArray.getDimension(R.styleable.PasswordInputView_corner_radius, mRectAngle);
    mStrokeWidth = (int) typedArray.getDimension(R.styleable.PasswordInputView_stroke_width, mStrokeWidth);
    typedArray.recycle();
  }

  private void initPaint() {
    mPointPaint = getPaint(dip2px(getContext(), 5), Paint.Style.FILL, mPointColor);
    mStrokePaint = getPaint(dip2px(getContext(), mStrokeWidth), Paint.Style.STROKE, mStrokeColor);
    mDivideLinePaint = getPaint(dip2px(getContext(), mStrokeWidth), Paint.Style.FILL, mDivideLineColor);
  }

  /**
   * 设置画笔
   *
   * @param strokeWidth 画笔宽度
   * @param style       画笔风格
   * @param color       画笔颜色
   * @return
   */
  private Paint getPaint(int strokeWidth, Paint.Style style, int color) {
    Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    paint.setStrokeWidth(strokeWidth);
    paint.setStyle(style);
    paint.setColor(color);
    paint.setAntiAlias(true);
    return paint;
  }

  @Override
  protected void onSizeChanged(int w, int h, int oldw, int oldh) {
    super.onSizeChanged(w, h, oldw, oldh);
    mWidth = w;
    mHeight = h;

    mDivideLineWStartX = w / passwordLength;

    mFirstCircleX = w / passwordLength / 2;
    mFirstCircleY = h / 2;

    mFrameRectF.set(0, 0, mWidth, mHeight);
  }

  @Override
  protected void onDraw(Canvas canvas) {
    drawBorder(canvas);
    drawPwdPoint(canvas);
  }

  /**
   * 绘制边框
   *
   * @param canvas
   */
  private void drawBorder(Canvas canvas) {
    canvas.drawRoundRect(mFrameRectF, mRectAngle, mRectAngle, mStrokePaint);
    for (int i = 1; i < passwordLength; i++) {
      canvas.drawLine(
              i * mDivideLineWStartX, 0,
              i * mDivideLineWStartX, mHeight,
              mDivideLinePaint);
    }
  }

  /**
   * 绘制密码圆点
   *
   * @param canvas
   */
  private void drawPwdPoint(Canvas canvas) {
    for (int i = 0; i < mCurInputCount; i++) {
      canvas.drawCircle(
              mFirstCircleX + i * 2 * mFirstCircleX, mFirstCircleY,
              mCircleRadius, mPointPaint);
    }
  }

  @Override
  protected void onTextChanged(CharSequence text, int start, int lengthBefore, int lengthAfter) {
    super.onTextChanged(text, start, lengthBefore, lengthAfter);
    mCurInputCount = text.toString().length();
    if (mCurInputCount == passwordLength && mCompleteListener != null) {
      mCompleteListener.onComplete(getPasswordString());
    }
    invalidate();
  }

  @Override
  protected void onSelectionChanged(int selStart, int selEnd) {
    super.onSelectionChanged(selStart, selEnd);
    //保证光标始终在最后
    if (selStart == selEnd) {
      setSelection(getText().length());
    }
  }

  /**
   * 获取输入的密码
   *
   * @return
   */
  public String getPasswordString() {
    return getText().toString().trim();
  }

  /**
   * dp转px  自定义事件注意使用dp为单位
   *
   * @param var0
   * @param var1
   * @return
   */
  public static int dip2px(Context var0, float var1) {
    float var2 = var0.getResources().getDisplayMetrics().density;
    return (int) (var1 * var2 + 0.5F);
  }

  /**
   * 密码输入完成回调
   */
  public interface OnPasswordCompleteListener {
    void onComplete(String password);
  }

}
