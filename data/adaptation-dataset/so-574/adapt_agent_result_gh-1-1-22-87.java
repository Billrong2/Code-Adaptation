@Override
public void onAccessibilityEvent(AccessibilityEvent event) {
    if (event == null || event.getEventType() != AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED) {
        return;
    }

    Parcelable parcelable = event.getParcelableData();
    if (!(parcelable instanceof Notification)) {
        return;
    }

    Notification notification = (Notification) parcelable;
    RemoteViews views = notification.contentView;
    if (views == null) {
        return;
    }

    final int TEXT_VIEW_ID = 16908358; // android.R.id.text1
    String notificationText = null;

    try {
        Class<?> remoteViewsClass = views.getClass();
        Field[] outerFields = remoteViewsClass.getDeclaredFields();
        for (Field outerField : outerFields) {
            if (!"mActions".equals(outerField.getName())) {
                continue;
            }

            outerField.setAccessible(true);
            Object actionsObj = outerField.get(views);
            if (!(actionsObj instanceof ArrayList)) {
                continue;
            }

            @SuppressWarnings("unchecked")
            ArrayList<Object> actionsList = (ArrayList<Object>) actionsObj;
            for (Object action : actionsList) {
                if (action == null) {
                    continue;
                }

                Field[] innerFields = action.getClass().getDeclaredFields();
                Object value = null;
                Integer type = null;
                Integer viewId = null;

                for (Field field : innerFields) {
                    field.setAccessible(true);
                    String fieldName = field.getName();
                    if ("value".equals(fieldName)) {
                        value = field.get(action);
                    } else if ("type".equals(fieldName)) {
                        type = field.getInt(action);
                    } else if ("viewId".equals(fieldName)) {
                        viewId = field.getInt(action);
                    }
                }

                if (type != null && viewId != null && value != null && viewId == TEXT_VIEW_ID && (type == 9 || type == 10)) {
                    notificationText = value.toString();
                    break;
                }
            }
        }
    } catch (IllegalAccessException e) {
        Log.e(TAG, "IllegalAccessException while extracting notification text", e);
    } catch (NoSuchFieldException e) {
        Log.e(TAG, "NoSuchFieldException while extracting notification text", e);
    } catch (RuntimeException e) {
        Log.e(TAG, "Unexpected error while extracting notification text", e);
    }

    if (notificationText != null) {
        Log.i(TAG, "Notification text: " + notificationText);

        if (notificationText.contains("Connecting")) {
            Intent intent = new Intent(this, net.npike.android.pebbleunlock.receiver.PebbleUnlockReceiver.class);
            intent.putExtra("lost_connection", true);
            sendBroadcast(intent);
        }
    }
}