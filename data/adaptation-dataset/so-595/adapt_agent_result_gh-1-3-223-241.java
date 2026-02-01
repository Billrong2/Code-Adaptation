protected Cookie decodeCookie(String encodedCookie)
{
    if (encodedCookie == null || encodedCookie.length() == 0)
        return null;

    final byte[] bytes;
    try
    {
        bytes = hexStringToByteArray(encodedCookie);
    } catch (Exception e)
    {
        Log.d(LOG_TAG, "Failed to decode hex string for cookie", e);
        return null;
    }

    if (bytes == null || bytes.length == 0)
        return null;

    try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
         ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream))
    {
        Object deserialized = objectInputStream.readObject();
        if (deserialized instanceof SerializableHttpCookie)
        {
            SerializableHttpCookie serializableCookie = (SerializableHttpCookie) deserialized;
            return serializableCookie.getCookie();
        }
    } catch (IOException e)
    {
        Log.d(LOG_TAG, "IOException in decodeCookie", e);
    } catch (ClassNotFoundException e)
    {
        Log.d(LOG_TAG, "ClassNotFoundException in decodeCookie", e);
    }

    return null;
}