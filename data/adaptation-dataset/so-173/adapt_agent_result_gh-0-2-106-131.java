@Override
@android.support.annotation.Nullable
public android.view.View onCreateView(String name, android.content.Context context, android.util.AttributeSet attrs) {
    // Delegate to super first to allow default inflation
    android.view.View superView = super.onCreateView(name, context, attrs);
    if (superView != null) {
        return superView;
    }

    // Hardening: ensure required inputs are present
    if (name == null || context == null) {
        return null;
    }

    // Only intervene on pre-Lollipop devices to provide AppCompat widgets
    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
        return null;
    }

    // Map framework widget names to AppCompat equivalents
    switch (name) {
        case "EditText":
            return new android.support.v7.widget.AppCompatEditText(context, attrs);
        case "Spinner":
            return new android.support.v7.widget.AppCompatSpinner(context, attrs);
        case "CheckBox":
            return new android.support.v7.widget.AppCompatCheckBox(context, attrs);
        case "RadioButton":
            return new android.support.v7.widget.AppCompatRadioButton(context, attrs);
        case "CheckedTextView":
            return new android.support.v7.widget.AppCompatCheckedTextView(context, attrs);
        default:
            // Allow the system/default inflater to handle everything else
            return null;
    }
}