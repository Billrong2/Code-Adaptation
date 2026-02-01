@Override
public void onSensorChanged(SensorEvent event) {
    try {
        if (event == null || event.values == null) {
            m_OrientationOK = false;
            return;
        }
        if (parentActivity == null) {
            m_OrientationOK = false;
            return;
        }

        final int type = event.sensor.getType();
        if (type == Sensor.TYPE_GRAVITY) {
            if (m_NormGravityVector == null) m_NormGravityVector = new float[3];
            float gx = event.values[0];
            float gy = event.values[1];
            float gz = event.values[2];
            m_Norm_Gravity = (float) Math.sqrt(gx * gx + gy * gy + gz * gz);
            if (m_Norm_Gravity > 1e-6f) {
                m_NormGravityVector[0] = gx / m_Norm_Gravity;
                m_NormGravityVector[1] = gy / m_Norm_Gravity;
                m_NormGravityVector[2] = gz / m_Norm_Gravity;
            }
        } else if (type == Sensor.TYPE_MAGNETIC_FIELD) {
            if (m_NormMagFieldValues == null) m_NormMagFieldValues = new float[3];
            float mx = event.values[0];
            float my = event.values[1];
            float mz = event.values[2];
            m_Norm_MagField = (float) Math.sqrt(mx * mx + my * my + mz * mz);
            if (m_Norm_MagField > 1e-6f) {
                m_NormMagFieldValues[0] = mx / m_Norm_MagField;
                m_NormMagFieldValues[1] = my / m_Norm_MagField;
                m_NormMagFieldValues[2] = mz / m_Norm_MagField;
            }
        } else {
            return;
        }

        // proceed only if both vectors are available
        if (m_NormGravityVector == null || m_NormMagFieldValues == null) {
            m_OrientationOK = false;
            return;
        }

        // validate magnitudes (free-fall or magnetic anomaly)
        if (m_Norm_Gravity < 1e-3f || m_Norm_MagField < 1e-3f) {
            m_OrientationOK = false;
            return;
        }

        // East = magnetic x gravity
        float ex = m_NormMagFieldValues[1] * m_NormGravityVector[2] - m_NormMagFieldValues[2] * m_NormGravityVector[1];
        float ey = m_NormMagFieldValues[2] * m_NormGravityVector[0] - m_NormMagFieldValues[0] * m_NormGravityVector[2];
        float ez = m_NormMagFieldValues[0] * m_NormGravityVector[1] - m_NormMagFieldValues[1] * m_NormGravityVector[0];
        float eNorm = (float) Math.sqrt(ex * ex + ey * ey + ez * ez);
        if (eNorm < 1e-6f) {
            m_OrientationOK = false;
            return;
        }
        m_NormEastVector[0] = ex / eNorm;
        m_NormEastVector[1] = ey / eNorm;
        m_NormEastVector[2] = ez / eNorm;

        // North = magnetic with gravity component removed
        float dotMG = m_NormMagFieldValues[0] * m_NormGravityVector[0]
                + m_NormMagFieldValues[1] * m_NormGravityVector[1]
                + m_NormMagFieldValues[2] * m_NormGravityVector[2];
        float nx = m_NormMagFieldValues[0] - dotMG * m_NormGravityVector[0];
        float ny = m_NormMagFieldValues[1] - dotMG * m_NormGravityVector[1];
        float nz = m_NormMagFieldValues[2] - dotMG * m_NormGravityVector[2];
        float nNorm = (float) Math.sqrt(nx * nx + ny * ny + nz * nz);
        if (nNorm < 1e-6f) {
            m_OrientationOK = false;
            return;
        }
        m_NormNorthVector[0] = nx / nNorm;
        m_NormNorthVector[1] = ny / nNorm;
        m_NormNorthVector[2] = nz / nNorm;

        // compute angles
        float azimuth = (float) Math.atan2(m_NormEastVector[1], m_NormNorthVector[1]);
        float pitch = (float) Math.acos(Math.max(-1f, Math.min(1f, m_NormGravityVector[2])));
        float pitchAxis = (float) Math.atan2(m_NormGravityVector[0], m_NormGravityVector[1]);

        // compensate for screen rotation
        WindowManager wm = parentActivity.getWindowManager();
        Display display = wm.getDefaultDisplay();
        int rotation = display.getRotation();
        switch (rotation) {
            case Surface.ROTATION_90:
                azimuth -= Math.PI / 2f;
                break;
            case Surface.ROTATION_180:
                azimuth -= Math.PI;
                break;
            case Surface.ROTATION_270:
                azimuth += Math.PI / 2f;
                break;
            case Surface.ROTATION_0:
            default:
                break;
        }

        m_azimuth_radians = azimuth;
        m_pitch_radians = pitch;
        m_pitch_axis_radians = pitchAxis;
        m_OrientationOK = true;

        if (m_parent != null) m_parent.onSensorChanged(event);
    } catch (Exception e) {
        m_OrientationOK = false;
        GPLog.error(this, "Error computing orientation from sensor event", e);
    }
}