private void step() {
    if (current == null) {
        return;
    }

    // Depth-first backtracking: advance the top iterator if possible,
    // otherwise pop levels until advancement is possible.
    while (!stack.isEmpty()) {
        Iterator<X> top = stack.peek();
        if (top.hasNext()) {
            // We can advance on this level
            X nextElement = top.next();
            current = new Node(current.next, nextElement);

            // Rebuild deeper levels (if any) from the base set
            while (stack.size() < subSize) {
                Iterator<X> it = baseSet.iterator();
                if (current.next != null) {
                    // Realign iterator to the previous element
                    if (!scrollTo(it, current.next.element)) {
                        throw new ConcurrentModificationException("Base set modified during subset iteration");
                    }
                }
                X el = it.next();
                current = new Node(current, el);
                stack.push(it);
            }
            return;
        } else {
            // This level is exhausted: backtrack
            stack.pop();
            current = current.next;
        }
    }

    // No higher level left: iteration finished
    current = null;
}