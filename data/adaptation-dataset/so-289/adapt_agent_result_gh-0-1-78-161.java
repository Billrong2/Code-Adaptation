private void load(Context context) {
    if(context == null) return;
    if(mIsLoading) return;
    mIsLoading = true;

    try {
        Resources res = getResources(context);
        if(res == null) return;

        SharedPreferences prefs = android.preference.PreferenceManager.getDefaultSharedPreferences(context);
        boolean useMask = prefs.getBoolean("icon_pack_use_mask", true);

        XmlPullParser xpp = null;
        int appfilterId = res.getIdentifier("appfilter", "xml", packageName);
        if(appfilterId > 0) {
            xpp = res.getXml(appfilterId);
        } else {
            // Fallback to assets/appfilter.xml
            try (InputStream is = res.getAssets().open("appfilter.xml")) {
                XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                factory.setNamespaceAware(true);
                xpp = factory.newPullParser();
                xpp.setInput(is, "utf-8");
            }
        }

        if(xpp == null) return;

        int eventType = xpp.getEventType();
        while(eventType != XmlPullParser.END_DOCUMENT) {
            if(eventType == XmlPullParser.START_TAG) {
                String tag = xpp.getName();

                if(useMask && "iconback".equals(tag)) {
                    for(int i = 0; i < xpp.getAttributeCount(); i++) {
                        String attrName = xpp.getAttributeName(i);
                        if(attrName != null && attrName.startsWith("img")) {
                            String drawableName = xpp.getAttributeValue(i);
                            if(drawableName != null) {
                                Bitmap bmp = loadBitmap(context, drawableName);
                                if(bmp != null) mBackImages.add(bmp);
                            }
                        }
                    }
                } else if(useMask && "iconmask".equals(tag)) {
                    if(xpp.getAttributeCount() > 0 && "img1".equals(xpp.getAttributeName(0))) {
                        String drawableName = xpp.getAttributeValue(0);
                        if(drawableName != null)
                            mMaskImage = loadBitmap(context, drawableName);
                    }
                } else if(useMask && "iconupon".equals(tag)) {
                    if(xpp.getAttributeCount() > 0 && "img1".equals(xpp.getAttributeName(0))) {
                        String drawableName = xpp.getAttributeValue(0);
                        if(drawableName != null)
                            mFrontImage = loadBitmap(context, drawableName);
                    }
                } else if(useMask && "scale".equals(tag)) {
                    if(xpp.getAttributeCount() > 0 && "factor".equals(xpp.getAttributeName(0))) {
                        try {
                            mFactor = Float.valueOf(xpp.getAttributeValue(0));
                        } catch (NumberFormatException ignored) { }
                    }
                } else if("item".equals(tag)) {
                    String componentName = null;
                    String drawableName = null;

                    for(int i = 0; i < xpp.getAttributeCount(); i++) {
                        String attrName = xpp.getAttributeName(i);
                        if("component".equals(attrName)) {
                            componentName = xpp.getAttributeValue(i);
                        } else if("drawable".equals(attrName)) {
                            drawableName = xpp.getAttributeValue(i);
                        }
                    }

                    if(componentName != null && drawableName != null && !mPackagesDrawables.containsKey(componentName)) {
                        mPackagesDrawables.put(componentName, drawableName);
                        totalIcons++;
                    }
                }
            }
            eventType = xpp.next();
        }

        mLoaded = true;
    } catch (XmlPullParserException | IOException ignored) {
        // Graceful failure
    } finally {
        mIsLoading = false;
    }
}