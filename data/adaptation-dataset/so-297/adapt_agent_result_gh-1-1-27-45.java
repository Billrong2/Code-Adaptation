public RecyclerItemClickListener(android.content.Context context, OnItemClickListener listener) {
    mListener = listener;
    mGestureDetector = new android.view.GestureDetector(context, new android.view.GestureDetector.SimpleOnGestureListener() {
        @Override
        public boolean onSingleTapUp(android.view.MotionEvent e) {
            // Preserve single-tap behavior
            return true;
        }

        @Override
        public void onLongPress(android.view.MotionEvent e) {
            if (mRecyclerView == null || mListener == null) {
                return;
            }

            android.view.View childView = mRecyclerView.findChildViewUnder(e.getX(), e.getY());
            if (childView instanceof com.dexafree.materialList.card.CardLayout) {
                com.dexafree.materialList.card.CardLayout cardLayout = (com.dexafree.materialList.card.CardLayout) childView;
                com.dexafree.materialList.card.Card card = cardLayout.getCard();
                if (card != null) {
                    int position = mRecyclerView.getChildPosition(cardLayout);
                    mListener.onItemLongClick(card, position);
                }
            }
        }
    });
}