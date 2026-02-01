public void loadIconsPack(String packageName) {
    // store package name
    this.iconsPackPackageName = packageName;

    // hardening: basic null checks
    if (ctx == null || pm == null) {
        Log.w(TAG, "Context or PackageManager is null, aborting icon pack load");
        return;
    }

    // initialize and clear previous state
    packagesDrawables.clear();
    backImages.clear();
    maskImage = null;
    frontImage = null;
    factor = 1.0f;
    cacheClear();

    // early return for default icon pack
    if (packageName == null || "default".equalsIgnoreCase(packageName)) {
        return;
    }

    try {
        // load resources for the icon pack
        iconPackres = pm.getResourcesForApplication(packageName);
        if (iconPackres == null) {
            return;
        }

        int appfilterId = iconPackres.getIdentifier("appfilter", "xml", packageName);
        if (appfilterId <= 0) {
            // no appfilter.xml in res/xml, nothing to parse
            return;
        }

        XmlPullParser xpp = iconPackres.getXml(appfilterId);
        if (xpp == null) {
            return;
        }

        int eventType = xpp.getEventType();
        while (eventType != XmlPullParser.END_DOCUMENT) {
            if (eventType == XmlPullParser.START_TAG) {
                String tagName = xpp.getName();

                if ("iconback".equals(tagName)) {
                    for (int i = 0; i < xpp.getAttributeCount(); i++) {
                        String attrName = xpp.getAttributeName(i);
                        if (attrName != null && attrName.startsWith("img")) {
                            String drawableName = xpp.getAttributeValue(i);
                            if (drawableName != null && drawableName.length() > 0) {
                                Bitmap iconBack = loadBitmap(drawableName);
                                if (iconBack != null) {
                                    backImages.add(iconBack);
                                }
                            }
                        }
                    }
                } else if ("iconmask".equals(tagName)) {
                    if (xpp.getAttributeCount() > 0 && "img1".equals(xpp.getAttributeName(0))) {
                        String drawableName = xpp.getAttributeValue(0);
                        if (drawableName != null && drawableName.length() > 0) {
                            maskImage = loadBitmap(drawableName);
                        }
                    }
                } else if ("iconupon".equals(tagName)) {
                    if (xpp.getAttributeCount() > 0 && "img1".equals(xpp.getAttributeName(0))) {
                        String drawableName = xpp.getAttributeValue(0);
                        if (drawableName != null && drawableName.length() > 0) {
                            frontImage = loadBitmap(drawableName);
                        }
                    }
                } else if ("scale".equals(tagName)) {
                    if (xpp.getAttributeCount() > 0 && "factor".equals(xpp.getAttributeName(0))) {
                        String value = xpp.getAttributeValue(0);
                        if (value != null) {
                            try {
                                factor = Float.valueOf(value);
                            } catch (NumberFormatException ignore) {
                                factor = 1.0f;
                            }
                        }
                    }
                } else if ("item".equals(tagName)) {
                    String componentName = null;
                    String drawableName = null;

                    for (int i = 0; i < xpp.getAttributeCount(); i++) {
                        String attrName = xpp.getAttributeName(i);
                        if ("component".equals(attrName)) {
                            componentName = xpp.getAttributeValue(i);
                        } else if ("drawable".equals(attrName)) {
                            drawableName = xpp.getAttributeValue(i);
                        }
                    }

                    if (componentName != null && componentName.length() > 0
                            && drawableName != null && drawableName.length() > 0) {
                        // keep uniqueness, no counting
                        if (!packagesDrawables.containsKey(componentName)) {
                            packagesDrawables.put(componentName, drawableName);
                        }
                    }
                }
            }
            eventType = xpp.next();
        }
    } catch (Exception e) {
        // simplified error handling
        Log.e(TAG, "Unable to load icon pack: " + packageName, e);
    }
}