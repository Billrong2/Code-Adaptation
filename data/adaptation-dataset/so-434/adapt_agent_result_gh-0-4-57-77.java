    @Override
    public void setAdapter(SpinnerAdapter orig) {
        final SpinnerAdapter adapter = newProxy(orig);

        super.setAdapter(adapter);

        try {
            final Method methodNext = AdapterView.class.getDeclaredMethod(
                    "setNextSelectedPositionInt", int.class
            );
            methodNext.setAccessible(true);
            methodNext.invoke(this, -1);

            final Method methodSelected = AdapterView.class.getDeclaredMethod(
                    "setSelectedPositionInt", int.class
            );
            methodSelected.setAccessible(true);
            methodSelected.invoke(this, -1);
        }

        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }