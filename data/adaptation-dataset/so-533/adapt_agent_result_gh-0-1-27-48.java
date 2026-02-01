@Override
protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    final RecyclerView view = new RecyclerView(this);
    setContentView(view);

    view.setLayoutManager(new LinearLayoutManager(this));

    Firebase.setAndroidContext(this);
    final Firebase ref = new Firebase("https://stackoverflow.firebaseio.com/36299197")
            .child("subscriptions/obama@gmsil,com");

    final FirebaseRecyclerAdapter<Program, ProgramVH> adapter =
            new FirebaseRecyclerAdapter<Program, ProgramVH>(
                    Program.class,
                    android.R.layout.two_line_list_item,
                    ProgramVH.class,
                    ref
            ) {
                @Override
                protected void populateViewHolder(final ProgramVH programViewHolder, Program program, int position) {
                    System.out.println("populateViewHolder for position " + position + " with program " + program);

                    if (programViewHolder == null) {
                        return;
                    }

                    if (program != null) {
                        if (programViewHolder.title != null) {
                            programViewHolder.title.setText(program.getTitle() != null ? program.getTitle() : "");
                        }
                        if (programViewHolder.level != null) {
                            programViewHolder.level.setText(String.valueOf(program.getLength()));
                        }
                    }
                }
            };

    view.setAdapter(adapter);
}