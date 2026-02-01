    // Adapted from StackOverflow: querying maximum OpenGL ES texture size via EGL configs
    // Source: https://stackoverflow.com/questions/10111073
    private static int getMaxTextureSize() {
        // Safe minimum default size (renamed constant)
        final int GL_MAX_TEXTURE_SIZE = 2048;

        javax.microedition.khronos.egl.EGL10 egl = null;
        javax.microedition.khronos.egl.EGLDisplay display = null;
        int maximumTextureSize = 0;

        try {
            egl = (javax.microedition.khronos.egl.EGL10) javax.microedition.khronos.egl.EGLContext.getEGL();
            display = egl.eglGetDisplay(javax.microedition.khronos.egl.EGL10.EGL_DEFAULT_DISPLAY);

            // Verify display initialization
            if (display == javax.microedition.khronos.egl.EGL10.EGL_NO_DISPLAY) {
                return GL_MAX_TEXTURE_SIZE;
            }

            int[] version = new int[2];
            if (!egl.eglInitialize(display, version)) {
                return GL_MAX_TEXTURE_SIZE;
            }

            // Query total number of configurations
            int[] totalConfigurations = new int[1];
            if (!egl.eglGetConfigs(display, null, 0, totalConfigurations) || totalConfigurations[0] <= 0) {
                return GL_MAX_TEXTURE_SIZE;
            }

            // Query actual list of configurations
            javax.microedition.khronos.egl.EGLConfig[] configurationsList =
                    new javax.microedition.khronos.egl.EGLConfig[totalConfigurations[0]];
            if (!egl.eglGetConfigs(display, configurationsList, totalConfigurations[0], totalConfigurations)) {
                return GL_MAX_TEXTURE_SIZE;
            }

            int[] textureSize = new int[1];

            // Iterate through all configurations to locate the maximum texture size
            for (int i = 0; i < totalConfigurations[0]; i++) {
                // Only need to check width since OpenGL textures are squared
                if (egl.eglGetConfigAttrib(display, configurationsList[i],
                        javax.microedition.khronos.egl.EGL10.EGL_MAX_PBUFFER_WIDTH, textureSize)) {
                    if (maximumTextureSize < textureSize[0]) {
                        maximumTextureSize = textureSize[0];
                    }
                }
            }
        } catch (Throwable t) {
            // Fall back to default size on any EGL/runtime failure
            return GL_MAX_TEXTURE_SIZE;
        } finally {
            if (egl != null && display != null && display != javax.microedition.khronos.egl.EGL10.EGL_NO_DISPLAY) {
                egl.eglTerminate(display);
            }
        }

        // Return largest texture size found, or default
        return Math.max(maximumTextureSize, GL_MAX_TEXTURE_SIZE);
    }