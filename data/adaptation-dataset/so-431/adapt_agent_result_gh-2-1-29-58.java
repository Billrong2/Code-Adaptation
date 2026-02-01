public void setValues(double[] y, double[] x) {
    if (x == null || y == null) {
        throw new IllegalArgumentException("Input arrays x and y must not be null");
    }
    if (x.length != y.length) {
        throw new IllegalArgumentException(String.format("The numbers of y and x values must be equal (%d != %d)", y.length, x.length));
    }
    if (x.length == 0) {
        throw new IllegalArgumentException("Input arrays x and y must not be empty");
    }

    final double[] localY;
    if (logY()) {
        localY = java.util.Arrays.copyOf(y, y.length);
        for (int i = 0; i < localY.length; i++) {
            if (localY[i] <= 0d) {
                throw new IllegalArgumentException("All y values must be positive when logY() is true");
            }
            localY[i] = Math.log(localY[i]);
        }
    } else {
        localY = y;
    }

    final double[][] designMatrix = new double[x.length][];
    for (int i = 0; i < x.length; i++) {
        designMatrix[i] = xVector(x[i]);
        if (designMatrix[i] == null) {
            throw new IllegalStateException("xVector(x) must not return null");
        }
    }

    final org.apache.commons.math3.stat.regression.OLSMultipleLinearRegression ols = new org.apache.commons.math3.stat.regression.OLSMultipleLinearRegression();
    ols.setNoIntercept(true);
    try {
        ols.newSampleData(localY, designMatrix);
        coef = org.apache.commons.math3.linear.MatrixUtils.createColumnRealMatrix(ols.estimateRegressionParameters());
        last_error_rate = ols.estimateErrorVariance();

        final double regressandVariance = ols.estimateRegressandVariance();
        final double regressionStdError = ols.estimateRegressionStandardError();
        android.util.Log.d(TAG, getClass().getSimpleName()
                + " errorVar=" + last_error_rate
                + " regressandVar=" + regressandVariance
                + " stdError=" + regressionStdError);
    } catch (IllegalArgumentException e) {
        throw new IllegalArgumentException("OLS regression failed for " + getClass().getSimpleName(), e);
    }
}