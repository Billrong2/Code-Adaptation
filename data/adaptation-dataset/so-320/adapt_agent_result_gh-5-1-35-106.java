public static Document readXML(final InputStream is) throws IOException, SAXException {
    if (is == null) {
        throw new IllegalArgumentException("InputStream must not be null");
    }

    final Document doc;
    final SAXParser parser;
    try {
        final SAXParserFactory factory = SAXParserFactory.newInstance();
        try {
            factory.setFeature("http://xml.org/sax/features/external-general-entities", false);
            factory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
            factory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
        } catch (Exception ignore) {
            // Feature not supported by all parsers; ignore safely
        }
        parser = factory.newSAXParser();

        final DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
        try {
            docBuilderFactory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
        } catch (Exception ignore) {
            // Feature not supported by all builders; ignore safely
        }
        final DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
        doc = docBuilder.newDocument();
    } catch (final ParserConfigurationException e) {
        throw new RuntimeException("Can't create SAX parser / DOM builder.", e);
    }

    final Stack<Element> elementStack = new Stack<Element>();
    final StringBuilder textBuffer = new StringBuilder();

    final DefaultHandler handler = new DefaultHandler() {
        private Locator locator;

        @Override
        public void setDocumentLocator(final Locator locator) {
            this.locator = locator;
        }

        @Override
        public void startElement(final String uri, final String localName, final String qName, final Attributes attributes)
                throws SAXException {
            addTextIfNeeded();

            final Element el = doc.createElement(qName);
            for (int i = 0; i < attributes.getLength(); i++) {
                el.setAttribute(attributes.getQName(i), attributes.getValue(i));
            }

            if (this.locator != null) {
                el.setUserData(LINE_NUMBER_KEY_NAME, Integer.valueOf(this.locator.getLineNumber()), null);
                el.setUserData(COLUMN_NUMBER_KEY_NAME, Integer.valueOf(this.locator.getColumnNumber()), null);
            }

            // Assign stable per-tag element ID
            synchronized (ELEMENT_NAMES_COUNTER) {
                final Integer current = ELEMENT_NAMES_COUNTER.get(qName);
                final int next = (current == null) ? 1 : current.intValue() + 1;
                ELEMENT_NAMES_COUNTER.put(qName, Integer.valueOf(next));
                el.setUserData(ELEMENT_ID, qName + "_" + next, null);
            }

            elementStack.push(el);
        }

        @Override
        public void endElement(final String uri, final String localName, final String qName) throws SAXException {
            addTextIfNeeded();
            if (elementStack.isEmpty()) {
                return;
            }
            final Element closedEl = elementStack.pop();

            if (this.locator != null) {
                closedEl.setUserData(LINE_NUMBER_LAST_KEY_NAME, Integer.valueOf(this.locator.getLineNumber()), null);
                closedEl.setUserData(COLUMN_NUMBER_LAST_KEY_NAME, Integer.valueOf(this.locator.getColumnNumber()), null);
            }

            if (elementStack.isEmpty()) {
                doc.appendChild(closedEl);
            } else {
                final Element parentEl = elementStack.peek();
                parentEl.appendChild(closedEl);
            }
        }

        @Override
        public void characters(final char[] ch, final int start, final int length) throws SAXException {
            textBuffer.append(ch, start, length);
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