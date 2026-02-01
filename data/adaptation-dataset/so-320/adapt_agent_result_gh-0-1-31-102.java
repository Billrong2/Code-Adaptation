public static Document readXML(final InputStream is) throws IOException, SAXException {
    // Caller retains ownership of InputStream lifecycle
    final Document doc;
    final SAXParser parser;
    try {
        final SAXParserFactory factory = SAXParserFactory.newInstance();
        factory.setNamespaceAware(true);
        parser = factory.newSAXParser();

        final DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
        docBuilderFactory.setNamespaceAware(true);
        final DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
        doc = docBuilder.newDocument();
    } catch (final ParserConfigurationException e) {
        throw new RuntimeException("Can't create namespace-aware SAX parser / DOM builder.", e);
    }

    final java.util.Deque<Element> elementStack = new java.util.ArrayDeque<Element>();
    final StringBuilder textBuffer = new StringBuilder();

    final DefaultHandler2 handler = new DefaultHandler2() {
        private Locator locator;

        @Override
        public void setDocumentLocator(final Locator locator) {
            this.locator = locator; // may be null depending on parser
        }

        @Override
        public void startElement(final String uri, final String localName, final String qName, final Attributes attributes)
                throws SAXException {
            addTextIfNeeded();
            final String effectiveLocalName = (localName != null && localName.length() > 0) ? localName : qName;
            final Element el = (uri != null && uri.length() > 0)
                    ? doc.createElementNS(uri, effectiveLocalName)
                    : doc.createElement(effectiveLocalName);

            for (int i = 0; i < attributes.getLength(); i++) {
                final String attrUri = attributes.getURI(i);
                final String attrLocalName = attributes.getLocalName(i);
                final String attrQName = attributes.getQName(i);
                final String attrName = (attrLocalName != null && attrLocalName.length() > 0) ? attrLocalName : attrQName;
                if (attrUri != null && attrUri.length() > 0) {
                    el.setAttributeNS(attrUri, attrName, attributes.getValue(i));
                } else {
                    el.setAttribute(attrName, attributes.getValue(i));
                }
            }

            if (this.locator != null) {
                el.setUserData(LINE_NUMBER_KEY_NAME, String.valueOf(this.locator.getLineNumber()), null);
            }
            elementStack.push(el);
        }

        @Override
        public void endElement(final String uri, final String localName, final String qName) throws SAXException {
            addTextIfNeeded();
            final Element closedEl = elementStack.pop();
            if (elementStack.isEmpty()) {
                doc.appendChild(closedEl);
            } else {
                elementStack.peek().appendChild(closedEl);
            }
        }

        @Override
        public void characters(final char[] ch, final int start, final int length) throws SAXException {
            textBuffer.append(ch, start, length);
        }

        // Enable lexical handling; no-op to preserve existing behavior
        @Override
        public void comment(final char[] ch, final int start, final int length) throws SAXException {
            super.comment(ch, start, length);
        }

        private void addTextIfNeeded() {
            if (textBuffer.length() > 0 && !elementStack.isEmpty()) {
                final Element el = elementStack.peek();
                final Node textNode = doc.createTextNode(textBuffer.toString());
                el.appendChild(textNode);
                textBuffer.setLength(0);
            }
        }
    };

    parser.parse(is, handler);
    return doc;
}