private double[][] applyDCT(final double[][] spatial) {
        // Validate input
        if (spatial == null) {
            throw new IllegalArgumentException("Input matrix must not be null");
        }
        if (spatial.length != size) {
            throw new IllegalArgumentException("Input matrix size does not match configured size");
        }
        for (int i = 0; i < size; i++) {
            if (spatial[i] == null || spatial[i].length != size) {
                throw new IllegalArgumentException("Input matrix must be square and match configured size");
            }
        }

        final int N = size;
        final double[][] frequency = new double[N][N];
        final double normalizationDivisor = 4.0;

        for (int u = 0; u < N; u++) {
            for (int v = 0; v < N; v++) {
                double sum = 0.0;
                for (int i = 0; i < N; i++) {
                    final double cosIU = Math.cos(((2 * i + 1) * u * Math.PI) / (2.0 * N));
                    for (int j = 0; j < N; j++) {
                        final double cosJV = Math.cos(((2 * j + 1) * v * Math.PI) / (2.0 * N));
                        sum += spatial[i][j] * cosIU * cosJV;
                    }
                }
                frequency[u][v] = (c[u] * c[v] / normalizationDivisor) * sum;
            }
        }

        return frequency;
    }