@Override
public void setValues(double[] y, double[] x) {
    // Hardening: basic null and length checks
    if (x == null || y == null) {
        coef = null;
        last_error_rate = null;
        throw new IllegalArgumentException("x and y must not be null");
    }
    if (x.length != y.length) {
        coef = null;
        last_error_rate = null;
        throw new IllegalArgumentException(String.format("The numbers of y and x values must be equal (%d != %d)", y.length, x.length));
    }
    if (x.length == 0) {
        coef = null;
        last_error_rate = null;
        throw new IllegalArgumentException("x and y must not be empty");
    }

    try {
        // Prepare predictor matrix
        final double[][] xData = new double[x.length][];
        for (int i = 0; i < x.length; i++) {
            xData[i] = xVector(x[i]);
        }

        // Optionally log-transform y
        double[] yData = y;
        if (logY()) {
            yData = java.util.Arrays.copyOf(y, y.length);
            for (int i = 0; i < yData.length; i++) {
                if (yData[i] <= 0) {
                    throw new IllegalArgumentException("y[" + i + "] must be > 0 when logY() is true");
                }
                yData[i] = Math.log(yData[i]);
            }
        }

        // Fit OLS model
        final org.apache.commons.math3.stat.regression.OLSMultipleLinearRegression ols =
                new org.apache.commons.math3.stat.regression.OLSMultipleLinearRegression();
        ols.setNoIntercept(true);
        ols.newSampleData(yData, xData);

        // Store coefficients and diagnostics
        coef = org.apache.commons.math3.linear.MatrixUtils
                .createColumnRealMatrix(ols.estimateRegressionParameters());
        last_error_rate = ols.estimateErrorVariance();

        // Debug diagnostics
        try {
            final double regressandVariance = ols.estimateRegressandVariance();
            final double regressionStdErr = Math.sqrt(last_error_rate);
            android.util.Log.d(TAG,
                    getClass().getSimpleName()
                            + " errVar=" + com.eveningoutpost.dexdrip.JoH.qs(last_error_rate)
                            + " yVar=" + com.eveningoutpost.dexdrip.JoH.qs(regressandVariance)
                            + " stdErr=" + com.eveningoutpost.dexdrip.JoH.qs(regressionStdErr));
        } catch (Exception ignored) {
            // diagnostics are best-effort only
        }
    } catch (RuntimeException e) {
        // Ensure consistent state on failure
        coef = null;
        last_error_rate = null;
        throw new RuntimeException(getClass().getSimpleName() + ": regression failed for n=" + x.length, e);
    }
}