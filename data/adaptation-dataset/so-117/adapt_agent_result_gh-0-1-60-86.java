public static void adjustHue(ColorMatrix cm, float value)
{
    if (cm == null) {
        return;
    }

    value = cleanValue(value, 180f) / 180f * (float) Math.PI;
    if (value == 0) {
        return;
    }

    float cosVal = (float) Math.cos(value);
    float sinVal = (float) Math.sin(value);
    float lumR = 0.213f;
    float lumG = 0.715f;
    float lumB = 0.072f;

    // 4x5 ColorMatrix (20 elements): RGB rows + alpha row
    float[] mat = new float[] {
            lumR + cosVal * (1 - lumR) + sinVal * (-lumR),
            lumG + cosVal * (-lumG) + sinVal * (-lumG),
            lumB + cosVal * (-lumB) + sinVal * (1 - lumB),
            0f, 0f,

            lumR + cosVal * (-lumR) + sinVal * (0.143f),
            lumG + cosVal * (1 - lumG) + sinVal * (0.140f),
            lumB + cosVal * (-lumB) + sinVal * (-0.283f),
            0f, 0f,

            lumR + cosVal * (-lumR) + sinVal * (-(1 - lumR)),
            lumG + cosVal * (-lumG) + sinVal * (lumG),
            lumB + cosVal * (1 - lumB) + sinVal * (lumB),
            0f, 0f,

            0f, 0f, 0f, 1f, 0f
    };

    cm.postConcat(new ColorMatrix(mat));
}