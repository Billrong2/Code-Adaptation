    // borrowed from lai-xin-chu: http://stackoverflow.com/questions/12576119
    private String getText(final Node node) {
        if (node == null) {
            return "";
        }
        final NodeList children = node.getChildNodes();
        if (children == null) {
            return "";
        }
        final StringBuffer sb = new StringBuffer();
        for (int i = 0; i < children.getLength(); i++) {
            final Node child = children.item(i);
            if (child == null) {
                continue;
            }
            switch (child.getNodeType()) {
                case Node.ELEMENT_NODE:
                    sb.append(getText(child));
                    sb.append(" ");
                    break;
                case Node.TEXT_NODE:
                    sb.append(((Text) child).getData());
                    break;
                default:
                    break;
            }
        }
        return sb.toString();
    }