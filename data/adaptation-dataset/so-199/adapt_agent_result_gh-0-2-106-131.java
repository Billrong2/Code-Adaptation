@Override
public View onCreateView(String name, Context context, AttributeSet attrs) {
    // First defer to the framework implementation
    View view = super.onCreateView(name, context, attrs);
    if (view != null) {
        return view;
    }

    // Only substitute widgets on pre-Lollipop devices to enable tinting
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        return null;
    }

    // Ensure we have a valid context for AppCompat widgets
    if (context == null || name == null) {
        return null;
    }

    // Provide AppCompat equivalents for specific widgets
    switch (name) {
        case "EditText":
            return new AppCompatEditText(context, attrs);
        case "Spinner":
            return new AppCompatSpinner(context, attrs);
        case "CheckBox":
            return new AppCompatCheckBox(context, attrs);
        case "RadioButton":
            return new AppCompatRadioButton(context, attrs);
        case "CheckedTextView":
            return new AppCompatCheckedTextView(context, attrs);
        default:
            // Allow default inflation for all other view types
            return null;
    }
}