@Override
public void setValues(double[] y, double[] x) {
    if (x == null || y == null) {
        throw new IllegalArgumentException("x and y must not be null");
    }
    if (x.length != y.length) {
        throw new IllegalArgumentException(String.format("The numbers of y and x values must be equal (%d != %d)", y.length, x.length));
    }

    final double[][] xData = new double[x.length][];
    for (int i = 0; i < x.length; i++) {
        xData[i] = xVector(x[i]);
    }

    if (logY()) {
        y = java.util.Arrays.copyOf(y, y.length);
        for (int i = 0; i < y.length; i++) {
            y[i] = Math.log(y[i]);
        }
    }

    final org.apache.commons.math3.stat.regression.OLSMultipleLinearRegression ols = new org.apache.commons.math3.stat.regression.OLSMultipleLinearRegression();
    ols.setNoIntercept(true);
    ols.newSampleData(y, xData);
    coef = org.apache.commons.math3.linear.MatrixUtils.createColumnRealMatrix(ols.estimateRegressionParameters());

    // capture error variance for later access and emit debug information
    final double errorVariance = ols.estimateErrorVariance();
    if (!Double.isNaN(errorVariance) && !Double.isInfinite(errorVariance)) {
        last_error_rate = errorVariance;
    }

    final double regressandVariance = ols.estimateRegressandVariance();
    final double regressionStdError = ols.estimateRegressionStandardError();

    if (!Double.isNaN(errorVariance) && !Double.isInfinite(errorVariance)
            && !Double.isNaN(regressandVariance) && !Double.isInfinite(regressandVariance)
            && !Double.isNaN(regressionStdError) && !Double.isInfinite(regressionStdError)) {
        final java.text.DecimalFormat df = new java.text.DecimalFormat("#.####");
        android.util.Log.d(TAG,
                getClass().getSimpleName() + " Forecast Error rate"
                        + " errorVariance=" + df.format(errorVariance)
                        + " regressandVariance=" + df.format(regressandVariance)
                        + " regressionStdError=" + df.format(regressionStdError));
    }
}