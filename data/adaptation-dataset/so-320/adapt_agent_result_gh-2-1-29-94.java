public static Document readXML(final InputStream is) throws IOException, SAXException, ParserConfigurationException {
    if (is == null) {
        throw new IllegalArgumentException("InputStream must not be null");
    }

    final Document doc;
    final SAXParser parser;

    final SAXParserFactory factory = SAXParserFactory.newInstance();
    // Secure XML processing to mitigate XXE
    factory.setFeature(javax.xml.XMLConstants.FEATURE_SECURE_PROCESSING, true);
    try {
        factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
    } catch (SAXException e) {
        // Feature may not be supported by all parsers; rethrow to keep behavior explicit
        throw e;
    }
    parser = factory.newSAXParser();

    final DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
    docBuilderFactory.setFeature(javax.xml.XMLConstants.FEATURE_SECURE_PROCESSING, true);
    final DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
    doc = docBuilder.newDocument();

    final Stack<Element> elementStack = new Stack<Element>();
    final StringBuilder textBuffer = new StringBuilder();

    final DefaultHandler handler = new DefaultHandler() {
        private Locator myLocator;

        @Override
        public void setDocumentLocator(final Locator locator) {
            this.myLocator = locator; // Save locator for line number tracking
        }

        @Override
        public void startElement(final String uri, final String localName, final String qName, final Attributes attributes)
                throws SAXException {
            addTextIfNeeded();
            final Element el = doc.createElement(qName);
            for (int i = 0; i < attributes.getLength(); i++) {
                el.setAttribute(attributes.getQName(i), attributes.getValue(i));
            }
            if (this.myLocator != null) {
                el.setUserData(LINE_NUMBER_KEY_NAME, String.valueOf(this.myLocator.getLineNumber()), null);
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
                final Element parentEl = elementStack.peek();
                parentEl.appendChild(closedEl);
            }
        }

        @Override
        public void characters(final char[] ch, final int start, final int length) throws SAXException {
            textBuffer.append(ch, start, length);
        }

        // Outputs text accumulated under the current node
        private void addTextIfNeeded() {
            if (textBuffer.length() > 0 && !elementStack.isEmpty()) {
                final Element el = elementStack.peek();
                final Node textNode = doc.createTextNode(textBuffer.toString());
                el.appendChild(textNode);
                textBuffer.setLength(0);
            }
        }
    };

    // Parsing does not take ownership of the InputStream; caller is responsible for closing it
    parser.parse(is, handler);

    return doc;
}