public static void removeCryptographyRestrictions() {
    // Register BouncyCastle provider if not already present
    if (java.security.Security.getProvider("BC") == null) {
        java.security.Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
    }

    // Early return if cryptography is not restricted
    if (!isRestrictedCryptography()) {
        return;
    }

    try {
        // Reflectively disable JCE cryptography restrictions
        Class<?> jceSecurityClass = Class.forName("javax.crypto.JceSecurity");
        Class<?> cryptoPermissionsClass = Class.forName("javax.crypto.CryptoPermissions");
        Class<?> cryptoAllPermissionClass = Class.forName("javax.crypto.CryptoAllPermission");

        // Set JceSecurity.isRestricted = false
        java.lang.reflect.Field isRestrictedField = jceSecurityClass.getDeclaredField("isRestricted");
        if (isRestrictedField == null) {
            throw new RuntimeException("Failed to access JceSecurity.isRestricted field.");
        }
        isRestrictedField.setAccessible(true);
        isRestrictedField.set(null, false);

        // Obtain and modify default policy permissions
        java.lang.reflect.Field defaultPolicyField = jceSecurityClass.getDeclaredField("defaultPolicy");
        if (defaultPolicyField == null) {
            throw new RuntimeException("Failed to access JceSecurity.defaultPolicy field.");
        }
        defaultPolicyField.setAccessible(true);
        java.security.PermissionCollection defaultPolicy = (java.security.PermissionCollection) defaultPolicyField.get(null);
        if (defaultPolicy == null) {
            throw new RuntimeException("JceSecurity.defaultPolicy is null.");
        }

        java.lang.reflect.Field permsField = cryptoPermissionsClass.getDeclaredField("perms");
        if (permsField == null) {
            throw new RuntimeException("Failed to access CryptoPermissions.perms field.");
        }
        permsField.setAccessible(true);
        Object permsObject = permsField.get(defaultPolicy);
        if (!(permsObject instanceof java.util.Map)) {
            throw new RuntimeException("Unexpected type for CryptoPermissions.perms field.");
        }
        ((java.util.Map<?, ?>) permsObject).clear();

        // Add CryptoAllPermission.INSTANCE
        java.lang.reflect.Field instanceField = cryptoAllPermissionClass.getDeclaredField("INSTANCE");
        if (instanceField == null) {
            throw new RuntimeException("Failed to access CryptoAllPermission.INSTANCE field.");
        }
        instanceField.setAccessible(true);
        defaultPolicy.add((java.security.Permission) instanceField.get(null));

    } catch (Exception e) {
        throw new RuntimeException(
            "Failed to remove Java cryptography restrictions. " +
            "Ensure that you are running on a compatible Java runtime and that reflective access is permitted. " +
            "If this fails on newer Java versions, consider installing the JCE Unlimited Strength policy or upgrading your JVM.",
            e
        );
    }
}