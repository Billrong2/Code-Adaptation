@Override
public StringBuffer format(long number, StringBuffer toAppendTo,
        FieldPosition pos) {
    // ignore toAppendTo and pos, return a new buffer as per existing behavior
    if (this.formatter == null) {
        // defensive fallback to avoid NPE; use a default two-digit formatter
        this.formatter = new java.text.DecimalFormat("00");
    }
    StringBuffer sb = new StringBuffer();
    long hours = number / 3600L;
    sb.append(this.formatter.format(hours)).append(":");
    long remaining = number - (hours * 3600L);
    long minutes = remaining / 60L;
    sb.append(this.formatter.format(minutes)).append(":");
    long seconds = remaining - (minutes * 60L);
    sb.append(this.formatter.format(seconds));
    return sb;
}