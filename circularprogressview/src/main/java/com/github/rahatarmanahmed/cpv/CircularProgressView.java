package com.github.rahatarmanahmed.cpv;

import ohos.agp.animation.Animator;
import ohos.agp.animation.AnimatorGroup;
import ohos.agp.animation.AnimatorValue;
import ohos.agp.components.AttrSet;
import ohos.agp.components.Component;
import ohos.agp.render.Arc;
import ohos.agp.render.Canvas;
import ohos.agp.render.Paint;
import ohos.agp.utils.Color;
import ohos.agp.utils.RectFloat;
import ohos.app.Context;

import java.util.ArrayList;
import java.util.List;

public class CircularProgressView extends Component
    implements Component.DrawTask, Component.LayoutRefreshedListener, Component.BindStateChangedListener {

    private static final float INDETERMINANT_MIN_SWEEP = 15f;

    private final float INDETERMINATE_SWEEP_SCALE = 3;

    private final float HUNDRED = 100;

    private Paint paint;

    private int size = 0;

    private RectFloat bounds;

    private boolean isIndeterminate, autostartAnimation;

    private float currentProgress, maxProgress, indeterminateSweep, indeterminateRotateOffset;

    private int thickness, color, animDuration, animSwoopDuration, animSyncDuration, animSteps;

    private List<CircularProgressViewListener> listeners;

    // Animation related stuff
    private float startAngle;

    private float actualProgress;

    private AnimatorValue startAngleRotate;

    private AnimatorValue progressAnimator;

    private AnimatorGroup indeterminateAnimator;

    private float initialStartAngle;

    private float indeterminateRotateOffsetScale;

    /**
     * Instantiates a new Circular progress view.
     *
     * @param context the context
     */
    public CircularProgressView(Context context) {
        super(context);
        init(null, 0);
    }

    /**
     * Instantiates a new Circular progress view.
     *
     * @param context the context
     * @param attrs   the attrs
     */
    public CircularProgressView(Context context, AttrSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    /**
     * Instantiates a new Circular progress view.
     *
     * @param context  the context
     * @param attrs    the attrs
     * @param defStyle the def style
     */
    public CircularProgressView(Context context, AttrSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    /**
     * Init.
     *
     * @param attrs    the attrs
     * @param defStyle the def style
     */
    protected void init(AttrSet attrs, int defStyle) {
        setLayoutRefreshedListener(this::onRefreshed);
        addDrawTask(this::onDraw);

        listeners = new ArrayList<>();

        initAttributes(attrs, defStyle);

        paint = new Paint();
        paint.setAntiAlias(true);
        updatePaint();

        bounds = new RectFloat();
    }

    private void initAttributes(AttrSet attrs, int defStyle) {

        // Initialize attributes from styleable attributes
        currentProgress = attrs.getAttr(Utils.cpv_progress).isPresent() ? attrs.getAttr(Utils.
            cpv_progress).get().getFloatValue() : Utils.cpv_default_progress;
        maxProgress = attrs.getAttr(Utils.cpv_maxProgress).isPresent() ? attrs.getAttr(Utils.
            cpv_maxProgress).get().getFloatValue() : Utils.cpv_default_max_progress;
        thickness = (int) (attrs.getAttr(Utils.cpv_thickness).isPresent()
            ? attrs.getAttr(Utils.
            cpv_thickness).get().getFloatValue()
            : Utils.getDimen(getContext(), ResourceTable.Float_cpv_default_thickness));
        isIndeterminate = attrs.getAttr(Utils.cpv_indeterminate).isPresent() ? attrs.getAttr(Utils.
            cpv_indeterminate).get().getBoolValue() : Utils.cpv_default_is_indeterminate;
        autostartAnimation = attrs.getAttr(Utils.cpv_animAutostart).isPresent() ? attrs.getAttr(Utils.
            cpv_animAutostart).get().getBoolValue() : Utils.cpv_default_anim_autostart;
        initialStartAngle = attrs.getAttr(Utils.cpv_startAngle).isPresent() ? attrs.getAttr(Utils.
            cpv_startAngle).get().getFloatValue() : Utils.cpv_default_start_angle;
        startAngle = initialStartAngle;


        color = attrs.getAttr(Utils.cpv_color).isPresent() ? attrs.getAttr(Utils.
            cpv_color).get().getIntegerValue() : Utils.getColor(getContext(), ResourceTable.Color_cpv_default_color);

        animDuration = attrs.getAttr(Utils.cpv_animDuration).isPresent() ? attrs.getAttr(Utils.
            cpv_animDuration).get().getIntegerValue() : Utils.cpv_default_anim_duration;
        animSwoopDuration = attrs.getAttr(Utils.cpv_animSwoopDuration).isPresent() ? attrs.getAttr(Utils.
            cpv_animSwoopDuration).get().getIntegerValue() : Utils.cpv_default_anim_swoop_duration;
        animSyncDuration = attrs.getAttr(Utils.cpv_animSyncDuration).isPresent() ? attrs.getAttr(Utils.
            cpv_animSyncDuration).get().getIntegerValue() : Utils.cpv_default_anim_sync_duration;
        animSteps = attrs.getAttr(Utils.cpv_animSteps).isPresent() ? attrs.getAttr(Utils.
            cpv_animSteps).get().getIntegerValue() : Utils.cpv_default_anim_steps;
        if (autostartAnimation) {
            startAnimation();
        }
    }

    @Override
    public void onRefreshed(Component component) {
        onMeasure(getWidth(), getHeight());
        updateBounds();
    }

    /**
     * On measure.
     *
     * @param widthMeasureSpec  the width measure spec
     * @param heightMeasureSpec the height measure spec
     */
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int xPad = getPaddingLeft() + getPaddingRight();
        int yPad = getPaddingTop() + getPaddingBottom();
        int width = getWidth() - xPad;
        int height = getHeight() - yPad;
        size = (width < height) ? width : height;
    }

    @Override
    public void postLayout() {
        super.postLayout();
        updateBounds();
    }


    private void updateBounds() {
        int paddingLeft = getPaddingLeft();
        int paddingTop = getPaddingTop();
        bounds.fuse(paddingLeft + thickness, paddingTop + thickness, size - paddingLeft - thickness,
            size - paddingTop - thickness);
    }

    private void updatePaint() {
        paint.setColor(new Color(color));
        paint.setStyle(Paint.Style.STROKE_STYLE);
        paint.setStrokeWidth(thickness);
        paint.setStrokeCap(Paint.StrokeCap.BUTT_CAP);
    }

    /**
     * Returns the mode of this view (determinate or indeterminate).
     *
     * @return true if this view is in indeterminate mode.
     */
    public boolean isIndeterminate() {
        return isIndeterminate;
    }

    /**
     * Sets whether this CircularProgressView is indeterminate or not.
     * It will reset the animation if the mode has changed.
     *
     * @param isIndeterminate True if indeterminate.
     */
    public void setIndeterminate(boolean isIndeterminate) {
        boolean old = this.isIndeterminate;
        boolean reset = this.isIndeterminate != isIndeterminate;
        this.isIndeterminate = isIndeterminate;
        if (reset) {
            resetAnimation();
        }
        if (old != isIndeterminate) {
            for (CircularProgressViewListener listener : listeners) {
                listener.onModeChanged(isIndeterminate);
            }
        }
    }

    /**
     * Get the thickness of the progress bar arc.
     *
     * @return the thickness of the progress bar arc
     */
    public int getThickness() {
        return thickness;
    }

    /**
     * Sets the thickness of the progress bar arc.
     *
     * @param thickness the thickness of the progress bar arc
     */
    public void setThickness(int thickness) {
        this.thickness = thickness;
        updatePaint();
        updateBounds();
        invalidate();
    }

    /**
     * Gets color.
     *
     * @return the color of the progress bar
     */
    public int getColor() {
        return color;
    }

    /**
     * Sets the color of the progress bar.
     *
     * @param color the color of the progress bar
     */
    public void setColor(int color) {
        this.color = color;
        updatePaint();
        invalidate();
    }

    /**
     * Gets the progress value considered to be 100% of the progress bar.
     *
     * @return the maximum progress
     */
    public float getMaxProgress() {
        return maxProgress;
    }

    /**
     * Sets the progress value considered to be 100% of the progress bar.
     *
     * @param maxProgress the maximum progress
     */
    public void setMaxProgress(float maxProgress) {
        this.maxProgress = maxProgress;
        invalidate();
    }

    /**
     * Gets progress.
     *
     * @return current progress
     */
    public float getProgress() {
        return currentProgress;
    }

    /**
     * Sets the progress of the progress bar.
     *
     * @param currentProgress the new progress.
     */
    public void setProgress(final float currentProgress) {
        this.currentProgress = currentProgress;
        // Reset the determinate animation to approach the new currentProgress
        if (!isIndeterminate) {
            if (progressAnimator != null && progressAnimator.isRunning()) {
                progressAnimator.cancel();
            }
            progressAnimator = new AnimatorValue();
            progressAnimator.setDuration(animSyncDuration);
            progressAnimator.setCurveType(Animator.CurveType.LINEAR);
            progressAnimator.setValueUpdateListener(new AnimatorValue.ValueUpdateListener() {
                @Override
                public void onUpdate(AnimatorValue animatorValue, float v) {
                    actualProgress = v;
                    invalidate();
                }
            });
            progressAnimator.setStateChangedListener(new Animator.StateChangedListener() {
                @Override
                public void onStart(Animator animator) {

                }

                @Override
                public void onStop(Animator animator) {

                }

                @Override
                public void onCancel(Animator animator) {

                }

                @Override
                public void onEnd(Animator animation) {
                    for (CircularProgressViewListener listener : listeners) {
                        listener.onProgressUpdateEnd(currentProgress);
                    }
                }

                @Override
                public void onPause(Animator animator) {

                }

                @Override
                public void onResume(Animator animator) {

                }
            });

            progressAnimator.start();
        }
        invalidate();
        for (CircularProgressViewListener listener : listeners) {
            listener.onProgressUpdate(currentProgress);
        }
    }

    /**
     * Register a CircularProgressViewListener with this View
     *
     * @param listener The listener to register
     */
    public void addListener(CircularProgressViewListener listener) {
        if (listener != null) {
            listeners.add(listener);
        }
    }

    /**
     * Unregister a CircularProgressViewListener with this View
     *
     * @param listener The listener to unregister
     */
    public void removeListener(CircularProgressViewListener listener) {
        listeners.remove(listener);
    }

    /**
     * Starts the progress bar animation.
     * (This is an alias of resetAnimation() so it does the same thing.)
     */
    public void startAnimation() {
        resetAnimation();
    }

    /**
     * Resets the animation.
     */
    public void resetAnimation() {
        // Cancel all the old animators
        if (startAngleRotate != null && startAngleRotate.isRunning()) {
            startAngleRotate.cancel();
        }
        if (progressAnimator != null && progressAnimator.isRunning()) {
            progressAnimator.cancel();
        }
        if (indeterminateAnimator != null && indeterminateAnimator.isRunning()) {
            indeterminateAnimator.cancel();
        }

        // Determinate animation
        if (!isIndeterminate) {
            // The cool 360 swoop animation at the start of the animation
            startAngle = initialStartAngle;
            startAngleRotate = new AnimatorValue();
            startAngleRotate.setDuration(animSwoopDuration);
            startAngleRotate.setCurveType(Animator.CurveType.DECELERATE);
            startAngleRotate.setValueUpdateListener(new AnimatorValue.ValueUpdateListener() {
                @Override
                public void onUpdate(AnimatorValue animatorValue, float v) {
                    startAngle = v;
                    invalidate();
                }
            });
            startAngleRotate.start();

            // The linear animation shown when progress is updated
            actualProgress = 0f;
            progressAnimator = new AnimatorValue();
            progressAnimator.setDuration(animSyncDuration);
            progressAnimator.setCurveType(Animator.CurveType.LINEAR);
            progressAnimator.setValueUpdateListener(new AnimatorValue.ValueUpdateListener() {
                @Override
                public void onUpdate(AnimatorValue animatorValue, float v) {
                    actualProgress = v;
                    invalidate();
                }
            });
            progressAnimator.start();
        }
        // Indeterminate animation
        else {
            indeterminateSweep = INDETERMINANT_MIN_SWEEP;
            // Build the whole AnimatorGroup
            indeterminateAnimator = new AnimatorGroup();
            AnimatorGroup prevSet = null, nextSet;
            for (int k = 0; k < animSteps; k++) {
                nextSet = createIndeterminateAnimator(k);
                AnimatorGroup.Builder builder = indeterminateAnimator.build();
                if (prevSet != null) {
                    builder.addAnimators(prevSet);
                }
                prevSet = nextSet;
            }

            // Listen to end of animation so we can infinitely loop
            indeterminateAnimator.setStateChangedListener(new Animator.StateChangedListener() {
                boolean wasCancelled = false;

                @Override
                public void onStart(Animator animator) {

                }

                @Override
                public void onStop(Animator animator) {

                }

                @Override
                public void onCancel(Animator animator) {
                    wasCancelled = true;
                }

                @Override
                public void onEnd(Animator animation) {
                    if (!wasCancelled) {
                        resetAnimation();
                    }
                }

                @Override
                public void onPause(Animator animator) {

                }

                @Override
                public void onResume(Animator animator) {

                }
            });

            indeterminateAnimator.start();
            for (CircularProgressViewListener listener : listeners) {
                listener.onAnimationReset();
            }
        }

    }

    /**
     * Stops the animation
     */
    public void stopAnimation() {
        if (startAngleRotate != null) {
            startAngleRotate.cancel();
            startAngleRotate = null;
        }
        if (progressAnimator != null) {
            progressAnimator.cancel();
            progressAnimator = null;
        }
        if (indeterminateAnimator != null) {
            indeterminateAnimator.cancel();
            indeterminateAnimator = null;
        }
    }

    // Creates the animators for one step of the animation
    private AnimatorGroup createIndeterminateAnimator(float step) {
        final float maxSweep = 360f * (animSteps - 1) / animSteps + INDETERMINANT_MIN_SWEEP;
        final float start = -90f + step * (maxSweep - INDETERMINANT_MIN_SWEEP);

        // Extending the front of the arc
        AnimatorValue frontEndExtend = new AnimatorValue();
        frontEndExtend.setDuration(animDuration / animSteps / 2);
        frontEndExtend.setCurveType(Animator.CurveType.DECELERATE);
        frontEndExtend.setValueUpdateListener(new AnimatorValue.ValueUpdateListener() {
            @Override
            public void onUpdate(AnimatorValue animatorValue, float v) {
                indeterminateSweep = v;
                invalidate();
            }
        });

        // Overall rotation
        AnimatorValue rotateAnimator1 = new AnimatorValue();
        rotateAnimator1.setDuration(animDuration / animSteps / 2);
        rotateAnimator1.setCurveType(Animator.CurveType.LINEAR);
        rotateAnimator1.setValueUpdateListener(new AnimatorValue.ValueUpdateListener() {
            @Override
            public void onUpdate(AnimatorValue animatorValue, float v) {
                indeterminateRotateOffset = v;
            }
        });

        // Followed by...

        // Retracting the back end of the arc
        AnimatorValue backEndRetract = new AnimatorValue();
        backEndRetract.setDuration(animDuration / animSteps / 2);
        backEndRetract.setCurveType(Animator.CurveType.DECELERATE);
        backEndRetract.setValueUpdateListener(new AnimatorValue.ValueUpdateListener() {
            @Override
            public void onUpdate(AnimatorValue animatorValue, float v) {
                startAngle = v;
                indeterminateSweep = maxSweep - startAngle + start;
                invalidate();
            }
        });

        // More overall rotation
        AnimatorValue rotateAnimator2 = new AnimatorValue();
        rotateAnimator2.setDuration(animDuration / animSteps / 2);
        rotateAnimator2.setCurveType(Animator.CurveType.LINEAR);
        rotateAnimator2.setValueUpdateListener(new AnimatorValue.ValueUpdateListener() {
            @Override
            public void onUpdate(AnimatorValue animatorValue, float v) {
                indeterminateRotateOffset = v;
            }
        });

        AnimatorGroup set = new AnimatorGroup();
        set.build().addAnimators(frontEndExtend, rotateAnimator1);
        set.build().addAnimators(backEndRetract, rotateAnimator2, rotateAnimator1);
        return set;
    }

    @Override
    public void onComponentBoundToWindow(Component component) {
        //Note:this method is not getting called, hence below check moved to init
        if (autostartAnimation) {
            startAnimation();
        }
    }

    @Override
    public void onComponentUnboundFromWindow(Component var1) {
        //Note:this method is not getting called.
        stopAnimation();
    }

    @Override
    public void setVisibility(int visibility) {
        int currentVisibility = getVisibility();
        super.setVisibility(visibility);
        if (visibility != currentVisibility) {
            if (visibility == Component.VISIBLE) {
                resetAnimation();
            } else if (visibility == Component.HIDE || visibility == Component.INVISIBLE) {
                stopAnimation();
            }
        }
    }

    @Override
    public void onDraw(Component component, Canvas canvas) {

        // Draw the arc
        float sweepAngle = currentProgress / maxProgress * 360;

        if (!isIndeterminate) {
            canvas.drawArc(bounds, new Arc(startAngle, sweepAngle, false), paint);
        } else {
            indeterminateRotateOffsetScale = indeterminateRotateOffset * HUNDRED;
            canvas.drawArc(bounds, new Arc(startAngle + indeterminateRotateOffsetScale,
                indeterminateRotateOffsetScale * INDETERMINATE_SWEEP_SCALE, false), paint);
        }
    }
}
