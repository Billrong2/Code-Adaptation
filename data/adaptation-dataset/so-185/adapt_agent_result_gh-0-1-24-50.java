@Override
protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    // Create the ListView programmatically
    final ListView answersList = new ListView(this);

    // Initialize Firebase context (assumed safe here for this minimal example)
    com.firebase.client.Firebase.setAndroidContext(this);

    // Constants for configuration
    final String firebaseUrl = "https://stackoverflow.firebaseio.com/36160819/answers";
    final String questionId = "-KDi2YLc2nzdvJXvntYC";

    final com.firebase.client.Firebase ref = new com.firebase.client.Firebase(firebaseUrl);
    final com.firebase.client.Query queryRef = ref.orderByChild("questionid").equalTo(questionId);

    final com.firebase.ui.FirebaseListAdapter<com.firebase.client.DataSnapshot> adapter =
            new com.firebase.ui.FirebaseListAdapter<com.firebase.client.DataSnapshot>(
                    this,
                    com.firebase.client.DataSnapshot.class,
                    android.R.layout.two_line_list_item,
                    queryRef
            ) {
                @Override
                protected com.firebase.client.DataSnapshot parseSnapshot(com.firebase.client.DataSnapshot snapshot) {
                    return snapshot;
                }

                @Override
                protected void populateView(View v, com.firebase.client.DataSnapshot answer, int position) {
                    Object authorIdValue = answer.child("authorid").getValue();
                    Object answerValue = answer.child("answer").getValue();

                    TextView text1 = (TextView) v.findViewById(android.R.id.text1);
                    TextView text2 = (TextView) v.findViewById(android.R.id.text2);

                    if (text1 != null) {
                        text1.setText(authorIdValue != null ? authorIdValue.toString() : "");
                    }
                    if (text2 != null) {
                        text2.setText(answerValue != null ? answerValue.toString() : "");
                    }
                }
            };

    answersList.setAdapter(adapter);
    setContentView(answersList);
}