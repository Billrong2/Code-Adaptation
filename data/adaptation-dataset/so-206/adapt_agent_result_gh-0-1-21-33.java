    @Override
    public void finish(int resultCode, Bundle results) {
        try {
            Class<?> rt = Class.forName("org.jacoco.agent.rt.RT");
            final Method getAgent = rt.getMethod("getAgent");
            final Method dump = getAgent.getReturnType().getMethod("dump", boolean.class);
            Object agent = getAgent.invoke(null);
            dump.invoke(agent, false);
        } catch (Throwable e) {
            Log.d("JACOCO", e.getMessage());
        }
        super.finish(resultCode, results);
    }