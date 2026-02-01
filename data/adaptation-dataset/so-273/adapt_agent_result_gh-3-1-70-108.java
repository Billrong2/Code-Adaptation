  /**
   * Creates a {@link Color} from HSV components.
   * <p>
   * Expected ranges are hue in any real value (normalized to [0,1)), and
   * saturation and value in [0,1]. Hue values outside the range are
   * normalized to avoid invalid switch cases.
   * </p>
   *
   * @param hue        Hue component (any real value; normalized internally)
   * @param saturation Saturation component in [0,1]
   * @param value      Value (brightness) component in [0,1]
   * @return Corresponding {@link Color}
   */
  public static Color fromHSV(final float hue, final float saturation, final float value)
  {
    if (saturation < 0.0f || saturation > 1.0f)
    {
      throw new IllegalArgumentException("Saturation out of range [0,1]: " + saturation);
    }
    if (value < 0.0f || value > 1.0f)
    {
      throw new IllegalArgumentException("Value out of range [0,1]: " + value);
    }

    // Normalize hue into [0, 1)
    float normalizedHue = hue % 1.0f;
    if (normalizedHue < 0.0f)
    {
      normalizedHue += 1.0f;
    }

    final int h = (int) (normalizedHue * 6.0f);
    final float f = normalizedHue * 6.0f - h;
    final float p = value * (1.0f - saturation);
    final float q = value * (1.0f - f * saturation);
    final float t = value * (1.0f - (1.0f - f) * saturation);

    switch (h)
    {
      case 0:
        return fromRGB(value, t, p);
      case 1:
        return fromRGB(q, value, p);
      case 2:
        return fromRGB(p, value, t);
      case 3:
        return fromRGB(p, q, value);
      case 4:
        return fromRGB(t, p, value);
      case 5:
        return fromRGB(value, p, q);
      default:
        // Should be unreachable due to hue normalization; keep as safeguard
        throw new RuntimeException(String.format(
          "Error converting from HSV to RGB. Normalized HSV was h=%f, s=%f, v=%f",
          normalizedHue, saturation, value));
    }
  }