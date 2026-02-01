public HttpCookie decode(String encodedCookie) {
        if (encodedCookie == null) {
            return null;
        }
        final byte[] bytes;
        try {
            bytes = hexStringToByteArray(encodedCookie);
        } catch (Exception e) {
            Log.d(TAG, "Invalid hex string for cookie decoding", e);
            return null;
        }
        try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
             ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream)) {
            final Object deserialized = objectInputStream.readObject();
            if (deserialized instanceof SerializableHttpCookie) {
                SerializableHttpCookie serializableCookie = (SerializableHttpCookie) deserialized;
                return serializableCookie.cookie;
            }
        } catch (IOException e) {
            Log.d(TAG, "IOException while decoding cookie", e);
        } catch (ClassNotFoundException e) {
            Log.d(TAG, "ClassNotFoundException while decoding cookie", e);
        }
        return null;
    }