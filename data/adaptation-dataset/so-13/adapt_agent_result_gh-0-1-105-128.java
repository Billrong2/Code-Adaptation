public int register(final Activity activity, final int sensorSpeed) {
        // current activity required for call to getWindowManager().getDefaultDisplay().getRotation()
        if (activity == null || sensorManager == null) {
            return 0;
        }
        // guard against repeated registration
        if (parentActivity != null) {
            return 0;
        }
        parentActivity = activity;
        m_NormGravityVector = new float[3];
        m_NormMagFieldValues = new float[3];
        m_OrientationOK = false;
        int count = 0;
        final Sensor sensorGravity = sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);
        if (sensorGravity != null) {
            sensorManager.registerListener(this, sensorGravity, sensorSpeed);
            m_GravityAccuracy = SensorManager.SENSOR_STATUS_ACCURACY_HIGH;
            count++;
        } else {
            m_GravityAccuracy = SENSOR_UNAVAILABLE;
        }
        final Sensor sensorMagField = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        if (sensorMagField != null) {
            sensorManager.registerListener(this, sensorMagField, sensorSpeed);
            m_MagneticFieldAccuracy = SensorManager.SENSOR_STATUS_ACCURACY_HIGH;
            count++;
        } else {
            m_MagneticFieldAccuracy = SENSOR_UNAVAILABLE;
        }
        return count;
    }