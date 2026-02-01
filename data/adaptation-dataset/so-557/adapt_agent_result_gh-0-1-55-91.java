public AlertDialog createFileDialog() {
    if (activity == null) return null;
    if (currentPath == null) return null;
    if (fileList == null) fileList = new String[]{};

    final AlertDialog.Builder builder = new AlertDialog.Builder(activity);
    builder.setTitle(currentPath.getPath());

    if (fileList.length > 0) {
        builder.setItems(fileList, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which < 0 || which >= fileList.length) return;
                try {
                    File chosenFile = getChosenFile(fileList[which]);
                    if (chosenFile == null) return;

                    if (chosenFile.isDirectory()) {
                        loadFileList(chosenFile);
                        dialog.dismiss();
                        AlertDialog newDialog = createFileDialog();
                        if (newDialog != null) newDialog.show();
                    } else {
                        fireFileSelectedEvent(chosenFile);
                        dialog.dismiss();
                    }
                } catch (SecurityException se) {
                    Log.e(TAG, "SecurityException accessing file", se);
                } catch (RuntimeException re) {
                    Log.e(TAG, "Unexpected error handling file selection", re);
                }
            }
        });
    }

    if (selectDirectoryOption) {
        builder.setPositiveButton("Select", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    if (currentPath != null) {
                        fireDirectorySelectedEvent(currentPath);
                    }
                } catch (RuntimeException re) {
                    Log.e(TAG, "Error selecting directory", re);
                }
            }
        });
    }

    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            dialog.dismiss();
        }
    });

    return builder.create();
}