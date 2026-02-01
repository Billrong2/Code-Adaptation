private void load(Context context) {
        if (mIsLoading) return;
        mIsLoading = true;
        try {
            if (context == null) return;

            Resources res = getResources(context);
            if (res == null) return;

            boolean useMask = false;
            try {
                SharedPreferences prefs = android.preference.PreferenceManager.getDefaultSharedPreferences(context);
                useMask = prefs.getBoolean("icon_pack_use_mask", false);
            } catch (Throwable t) {
                useMask = false;
            }

            XmlPullParser xpp = null;
            InputStream assetStream = null;

            int appfilterId = res.getIdentifier("appfilter", "xml", packageName);
            if (appfilterId > 0) {
                xpp = res.getXml(appfilterId);
            } else {
                try {
                    assetStream = res.getAssets().open("appfilter.xml");
                    XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                    factory.setNamespaceAware(true);
                    xpp = factory.newPullParser();
                    xpp.setInput(assetStream, "utf-8");
                } catch (IOException ignored) {
                    xpp = null;
                }
            }

            if (xpp != null) {
                int eventType = xpp.getEventType();
                while (eventType != XmlPullParser.END_DOCUMENT) {
                    if (eventType == XmlPullParser.START_TAG) {
                        String tag = xpp.getName();

                        if (useMask && "iconback".equals(tag)) {
                            for (int i = 0; i < xpp.getAttributeCount(); i++) {
                                if (xpp.getAttributeName(i).startsWith("img")) {
                                    String drawableName = xpp.getAttributeValue(i);
                                    Bitmap iconBack = loadBitmap(context, drawableName);
                                    if (iconBack != null) mBackImages.add(iconBack);
                                }
                            }
                        } else if (useMask && "iconmask".equals(tag)) {
                            if (xpp.getAttributeCount() > 0 && "img1".equals(xpp.getAttributeName(0))) {
                                mMaskImage = loadBitmap(context, xpp.getAttributeValue(0));
                            }
                        } else if (useMask && "iconupon".equals(tag)) {
                            if (xpp.getAttributeCount() > 0 && "img1".equals(xpp.getAttributeName(0))) {
                                mFrontImage = loadBitmap(context, xpp.getAttributeValue(0));
                            }
                        } else if (useMask && "scale".equals(tag)) {
                            if (xpp.getAttributeCount() > 0 && "factor".equals(xpp.getAttributeName(0))) {
                                try {
                                    mFactor = Float.valueOf(xpp.getAttributeValue(0));
                                } catch (NumberFormatException ignored) {}
                            }
                        } else if ("item".equals(tag)) {
                            String componentName = null;
                            String drawableName = null;

                            for (int i = 0; i < xpp.getAttributeCount(); i++) {
                                String attr = xpp.getAttributeName(i);
                                if ("component".equals(attr)) {
                                    componentName = xpp.getAttributeValue(i);
                                } else if ("drawable".equals(attr)) {
                                    drawableName = xpp.getAttributeValue(i);
                                }
                            }

                            if (componentName != null && drawableName != null
                                    && !mPackagesDrawables.containsKey(componentName)) {
                                mPackagesDrawables.put(componentName, drawableName);
                                totalIcons++;
                            }
                        }
                    }
                    eventType = xpp.next();
                }
            }

            mLoaded = true;
        } catch (XmlPullParserException | IOException ignored) {
            // fail silently
        } finally {
            mIsLoading = false;
        }
    }