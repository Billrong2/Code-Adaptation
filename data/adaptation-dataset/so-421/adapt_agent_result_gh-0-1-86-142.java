public void process(final org.w3c.dom.Document document) {
        if (document == null) {
            return;
        }
        final org.w3c.dom.Element root = document.getDocumentElement();
        if (root == null) {
            return;
        }

        final java.util.Set<String> namespaces = new java.util.HashSet<String>();

        // First traversal: collect all actually used namespaces
        traverse(root, new ElementVisitor() {
            public void visit(final org.w3c.dom.Element element) {
                if (element == null) {
                    return;
                }
                String namespace = element.getNamespaceURI();
                if (namespace == null) {
                    namespace = "";
                }
                namespaces.add(namespace);

                final org.w3c.dom.NamedNodeMap attributes = element.getAttributes();
                if (attributes == null) {
                    return;
                }
                for (int i = 0; i < attributes.getLength(); i++) {
                    final org.w3c.dom.Node node = attributes.item(i);
                    if (node == null) {
                        continue;
                    }
                    // Skip xmlns declarations themselves
                    if (javax.xml.XMLConstants.XMLNS_ATTRIBUTE_NS_URI.equals(node.getNamespaceURI())) {
                        continue;
                    }
                    final String prefix;
                    // Special handling for xsi:type
                    if (javax.xml.XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI.equals(node.getNamespaceURI())) {
                        if ("type".equals(node.getLocalName())) {
                            final String value = node.getNodeValue();
                            if (value != null && value.contains(":")) {
                                prefix = value.substring(0, value.indexOf(':'));
                            } else {
                                prefix = null;
                            }
                        } else {
                            continue;
                        }
                    } else {
                        prefix = node.getPrefix();
                    }
                    String attrNamespace = element.lookupNamespaceURI(prefix);
                    if (attrNamespace == null) {
                        attrNamespace = "";
                    }
                    namespaces.add(attrNamespace);
                }
            }
        });

        // Second traversal: remove unused xmlns declarations
        traverse(root, new ElementVisitor() {
            public void visit(final org.w3c.dom.Element element) {
                if (element == null) {
                    return;
                }
                final java.util.Set<String> removeLocalNames = new java.util.HashSet<String>();
                final org.w3c.dom.NamedNodeMap attributes = element.getAttributes();
                if (attributes == null) {
                    return;
                }
                for (int i = 0; i < attributes.getLength(); i++) {
                    final org.w3c.dom.Node node = attributes.item(i);
                    if (node == null) {
                        continue;
                    }
                    if (!javax.xml.XMLConstants.XMLNS_ATTRIBUTE_NS_URI.equals(node.getNamespaceURI())) {
                        continue;
                    }
                    if (namespaces.contains(node.getNodeValue())) {
                        continue;
                    }
                    removeLocalNames.add(node.getLocalName());
                }
                for (final String localName : removeLocalNames) {
                    element.removeAttributeNS(javax.xml.XMLConstants.XMLNS_ATTRIBUTE_NS_URI, localName);
                }
            }
        });
    }