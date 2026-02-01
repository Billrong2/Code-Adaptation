public static List<com.mendhak.gpslogger.common.SerializableLocation> getPoints(File gpxFile) throws Exception {
    List<com.mendhak.gpslogger.common.SerializableLocation> points = new ArrayList<>();

    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    DocumentBuilder builder = factory.newDocumentBuilder();

    try (FileInputStream fis = new FileInputStream(gpxFile)) {
        Document dom = builder.parse(fis);
        Element root = dom.getDocumentElement();
        NodeList items = root.getElementsByTagName("trkpt");

        for (int j = 0; j < items.getLength(); j++) {
            Node item = items.item(j);
            if (item == null) {
                continue;
            }

            NamedNodeMap attrs = item.getAttributes();
            if (attrs == null) {
                continue;
            }

            Node latNode = attrs.getNamedItem("lat");
            Node lonNode = attrs.getNamedItem("lon");
            if (latNode == null || lonNode == null) {
                continue;
            }

            Location pt = new Location("gpx");
            pt.setLatitude(Double.parseDouble(latNode.getNodeValue()));
            pt.setLongitude(Double.parseDouble(lonNode.getNodeValue()));

            NodeList props = item.getChildNodes();
            if (props != null) {
                // Consolidated pass for multiple GPX tags
                for (int k = 0; k < props.getLength(); k++) {
                    Node child = props.item(k);
                    if (child == null) {
                        continue;
                    }

                    String name = child.getNodeName();
                    Node valueNode = child.getFirstChild();
                    if (valueNode == null) {
                        continue;
                    }

                    String value = valueNode.getNodeValue();
                    if (value == null) {
                        continue;
                    }

                    if (name.equalsIgnoreCase("ele")) {
                        pt.setAltitude(Double.parseDouble(value));
                    } else if (name.equalsIgnoreCase("time")) {
                        pt.setTime(getDateFormatter().parse(value).getTime());
                    } else if (name.equalsIgnoreCase("course")) {
                        pt.setBearing(Float.parseFloat(value));
                    } else if (name.equalsIgnoreCase("speed")) {
                        pt.setSpeed(Float.parseFloat(value));
                    } else if (name.equalsIgnoreCase("hdop")) {
                        pt.setAccuracy(Float.parseFloat(value) * 5f);
                    }
                }

                // Additional redundant pass to set altitude from ele
                for (int y = 0; y < props.getLength(); y++) {
                    Node child = props.item(y);
                    if (child == null || !child.getNodeName().equalsIgnoreCase("ele")) {
                        continue;
                    }
                    Node valueNode = child.getFirstChild();
                    if (valueNode != null && valueNode.getNodeValue() != null) {
                        pt.setAltitude(Double.parseDouble(valueNode.getNodeValue()));
                    }
                }
            }

            points.add(new com.mendhak.gpslogger.common.SerializableLocation(pt));
        }
    }

    return points;
}