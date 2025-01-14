package org.thoughtcrime.securesms.components;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewTreeObserver;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.thoughtcrime.securesms.util.ViewUtil;

public class MaskView extends View {

  private View  target;
  private int[] targetLocation = new int[2];
  private int   statusBarHeight;
  private Paint maskPaint;

  private final ViewTreeObserver.OnDrawListener onDrawListener = this::invalidate;

  public MaskView(@NonNull Context context) {
    super(context);
  }

  public MaskView(@NonNull Context context, @Nullable AttributeSet attributeSet) {
    super(context, attributeSet);
  }

  @Override
  protected void onFinishInflate() {
    super.onFinishInflate();

    maskPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    setLayerType(LAYER_TYPE_HARDWARE, maskPaint);

    maskPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));

    statusBarHeight = ViewUtil.getStatusBarHeight(this);
  }

  public void setTarget(@Nullable View target) {
    if (this.target != null) {
      this.target.getViewTreeObserver().removeOnDrawListener(onDrawListener);
    }

    this.target = target;

    if (this.target != null) {
      this.target.getViewTreeObserver().addOnDrawListener(onDrawListener);
    }

    invalidate();
  }

  @Override
  protected void onDraw(@NonNull Canvas canvas) {
    super.onDraw(canvas);

    if (target == null) {
      return;
    }

    target.getLocationInWindow(targetLocation);

    Bitmap mask       = Bitmap.createBitmap(target.getWidth(), target.getHeight(), Bitmap.Config.ARGB_8888);
    Canvas maskCanvas = new Canvas(mask);

    target.draw(maskCanvas);

    canvas.drawBitmap(mask, 0, targetLocation[1] - statusBarHeight, maskPaint);

    mask.recycle();
  }
}
