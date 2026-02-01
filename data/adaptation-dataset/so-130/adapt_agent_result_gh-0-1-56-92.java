public static String encodeURIComponent(String input)
{
    if (input == null)
    {
        return input;
    }

    final String hex = "0123456789ABCDEF";
    final StringBuilder filtered = new StringBuilder(input.length());

    for (int i = 0; i < input.length(); ++i)
    {
        final char c = input.charAt(i);
        if (dontNeedEncoding.get(c))
        {
            filtered.append(c);
        }
        else
        {
            final byte[] bytes = charToBytesUTF(c);
            for (int j = 0; j < bytes.length; ++j)
            {
                filtered.append('%');
                filtered.append(hex.charAt((bytes[j] >> 4) & 0xF));
                filtered.append(hex.charAt(bytes[j] & 0xF));
            }
        }
    }
    return filtered.toString();
}