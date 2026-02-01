public CheckableRelativeLayout(final Context context, final AttributeSet attrs) {
    super(context, attrs);

    TypedArray typedArray = null;
    Drawable checkDrawable = null;
    Drawable radioDrawable = null;

    // Cache the check box drawable (multiple choice)
    try {
        typedArray = context.getTheme().obtainStyledAttributes(new int[] { android.R.attr.listChoiceIndicatorMultiple });
        if (typedArray != null && typedArray.length() > 0) {
            checkDrawable = typedArray.getDrawable(0);
        }
    } finally {
        if (typedArray != null) {
            typedArray.recycle();
            typedArray = null;
        }
    }

    // Cache the radio button drawable (single choice)
    try {
        typedArray = context.getTheme().obtainStyledAttributes(new int[] { android.R.attr.listChoiceIndicatorSingle });
        if (typedArray != null && typedArray.length() > 0) {
            radioDrawable = typedArray.getDrawable(0);
        }
    } finally {
        if (typedArray != null) {
            typedArray.recycle();
        }
    }

    mCheckDrawable = checkDrawable;
    mRadioDrawable = radioDrawable;
    mIsChecked = false;
}