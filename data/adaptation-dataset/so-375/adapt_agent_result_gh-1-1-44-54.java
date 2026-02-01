protected void populateViewHolder(final ItemViewHolder viewHolder, final Boolean model, final int position) {
    if (viewHolder == null || viewHolder.itemView == null) {
        return;
    }

    final com.firebase.client.Firebase itemRef = getRef(position);
    if (itemRef == null) {
        return;
    }

    final String key = itemRef.getKey();
    if (key == null || key.length() == 0) {
        return;
    }

    try {
        ref.child("users").child(key).addListenerForSingleValueEvent(new com.firebase.client.ValueEventListener() {
            @Override
            public void onDataChange(com.firebase.client.DataSnapshot dataSnapshot) {
                if (dataSnapshot == null || !dataSnapshot.child("name").exists()) {
                    return;
                }

                final String name;
                try {
                    name = dataSnapshot.child("name").getValue(String.class);
                } catch (RuntimeException e) {
                    android.util.Log.e("Activity36235919", "Failed to read name value", e);
                    return;
                }

                final android.widget.TextView textView = (android.widget.TextView) viewHolder.itemView.findViewById(android.R.id.text1);
                if (textView != null) {
                    textView.setText(name != null ? name : "");
                }
            }

            @Override
            public void onCancelled(com.firebase.client.FirebaseError firebaseError) {
                android.util.Log.e("Activity36235919", "Firebase cancelled: " + (firebaseError != null ? firebaseError.getMessage() : "unknown error"));
            }
        });
    } catch (Exception e) {
        android.util.Log.e("Activity36235919", "Unexpected error in populateViewHolder", e);
    }
}