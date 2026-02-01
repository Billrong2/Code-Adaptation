private void loadIconsPack(String packageName) {
    // store selected icon pack
    this.iconsPackPackageName = packageName;

    // reset state and cache
    packagesDrawables.clear();
    backImages.clear();
    maskImage = null;
    frontImage = null;
    factor = 1.0f;
    cacheClear();

    // default pack: nothing else to load
    if (packageName == null || "default".equalsIgnoreCase(packageName)) {
        return;
    }

    try {
        // load appfilter.xml strictly from APK resources
        iconPackres = pm.getResourcesForApplication(packageName);
        int appfilterId = iconPackres.getIdentifier("appfilter", "xml", packageName);
        if (appfilterId <= 0) {
            return;
        }

        org.xmlpull.v1.XmlPullParser xpp = iconPackres.getXml(appfilterId);
        if (xpp == null) {
            return;
        }

        int eventType = xpp.getEventType();
        while (eventType != org.xmlpull.v1.XmlPullParser.END_DOCUMENT) {
            if (eventType == org.xmlpull.v1.XmlPullParser.START_TAG) {
                String tagName = xpp.getName();

                if ("iconback".equals(tagName)) {
                    int attrCount = xpp.getAttributeCount();
                    for (int i = 0; i < attrCount; i++) {
                        String attrName = xpp.getAttributeName(i);
                        if (attrName != null && attrName.startsWith("img")) {
                            String drawableName = xpp.getAttributeValue(i);
                            if (drawableName != null) {
                                Bitmap iconback = loadBitmap(drawableName);
                                if (iconback != null) {
                                    backImages.add(iconback);
                                }
                            }
                        }
                    }
                } else if ("iconmask".equals(tagName)) {
                    if (xpp.getAttributeCount() > 0 && "img1".equals(xpp.getAttributeName(0))) {
                        String drawableName = xpp.getAttributeValue(0);
                        if (drawableName != null) {
                            maskImage = loadBitmap(drawableName);
                        }
                    }
                } else if ("iconupon".equals(tagName)) {
                    if (xpp.getAttributeCount() > 0 && "img1".equals(xpp.getAttributeName(0))) {
                        String drawableName = xpp.getAttributeValue(0);
                        if (drawableName != null) {
                            frontImage = loadBitmap(drawableName);
                        }
                    }
                } else if ("scale".equals(tagName)) {
                    if (xpp.getAttributeCount() > 0 && "factor".equals(xpp.getAttributeName(0))) {
                        String value = xpp.getAttributeValue(0);
                        if (value != null) {
                            try {
                                factor = Float.valueOf(value);
                            } catch (NumberFormatException ignored) {
                                factor = 1.0f;
                            }
                        }
                    }
                } else if ("item".equals(tagName)) {
                    String componentName = null;
                    String drawableName = null;
                    int attrCount = xpp.getAttributeCount();
                    for (int i = 0; i < attrCount; i++) {
                        String attrName = xpp.getAttributeName(i);
                        if ("component".equals(attrName)) {
                            componentName = xpp.getAttributeValue(i);
                        } else if ("drawable".equals(attrName)) {
                            drawableName = xpp.getAttributeValue(i);
                        }
                    }
                    if (componentName != null && drawableName != null && !packagesDrawables.containsKey(componentName)) {
                        packagesDrawables.put(componentName, drawableName);
                    }
                }
            }
            eventType = xpp.next();
        }
    } catch (Exception e) {
        Log.e(TAG, "Failed to load icon pack " + packageName, e);
    }
}