public ArrayList<Video> getStreamingUrisFromYouTubePage(String ytUrl) throws Exception {
    if (ytUrl == null || ytUrl.length() == 0) {
        return null;
    }

    int andIdx = ytUrl.indexOf('&');
    if (andIdx >= 0) {
        ytUrl = ytUrl.substring(0, andIdx);
    }

    String userAgent = "Mozilla/5.0 (Linux; Android 10; Mobile) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Mobile Safari/537.36";
    String html;

    HttpClient client = new DefaultHttpClient();
    client.getParams().setParameter(CoreProtocolPNames.USER_AGENT, userAgent);
    HttpGet request = new HttpGet(ytUrl);

    try (InputStream in = client.execute(request).getEntity().getContent();
         BufferedReader reader = new BufferedReader(new InputStreamReader(in))) {
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }
        html = sb.toString();
    }

    if (html == null || html.length() == 0) {
        return null;
    }

    if (html.contains("verify-age-thumb") || html.contains("das_captcha")) {
        return null;
    }

    html = html.replace("\\u0026", "&").replace("\\", "");

    Pattern streamMapPattern = Pattern.compile("stream_map\":\"(.*?)\"");
    Matcher streamMapMatcher = streamMapPattern.matcher(html);
    if (!streamMapMatcher.find()) {
        return null;
    }

    String streamMap = streamMapMatcher.group(1);
    if (streamMap == null || streamMap.length() == 0) {
        return null;
    }

    HashMap<String, String> foundArray = new HashMap<String, String>();

    Pattern pairPattern = Pattern.compile("itag=([0-9]+).*?url=([^,}]+)");
    Matcher pairMatcher = pairPattern.matcher(streamMap);
    while (pairMatcher.find()) {
        String itag = pairMatcher.group(1);
        String rawUrl = pairMatcher.group(2);
        if (itag != null && rawUrl != null) {
            String cleanedUrl = rawUrl;
            if (cleanedUrl.endsWith("}")) {
                cleanedUrl = cleanedUrl.substring(0, cleanedUrl.length() - 1);
            }
            cleanedUrl = URLDecoder.decode(cleanedUrl, "UTF-8");
            foundArray.put(itag, cleanedUrl);
        }
    }

    if (foundArray.size() == 0) {
        return null;
    }

    HashMap<String, Meta> typeMap = new HashMap<String, Meta>();
    typeMap.put("13", new Meta("13", "3GP", "Low Quality - 176x144"));
    typeMap.put("17", new Meta("17", "3GP", "Medium Quality - 176x144"));
    typeMap.put("36", new Meta("36", "3GP", "High Quality - 320x240"));
    typeMap.put("5", new Meta("5", "FLV", "Low Quality - 400x226"));
    typeMap.put("6", new Meta("6", "FLV", "Medium Quality - 640x360"));
    typeMap.put("34", new Meta("34", "FLV", "Medium Quality - 640x360"));
    typeMap.put("35", new Meta("35", "FLV", "High Quality - 854x480"));
    typeMap.put("43", new Meta("43", "WEBM", "Low Quality - 640x360"));
    typeMap.put("44", new Meta("44", "WEBM", "Medium Quality - 854x480"));
    typeMap.put("45", new Meta("45", "WEBM", "High Quality - 1280x720"));
    typeMap.put("18", new Meta("18", "MP4", "Medium Quality - 480x360"));
    typeMap.put("22", new Meta("22", "MP4", "High Quality - 1280x720"));
    typeMap.put("37", new Meta("37", "MP4", "High Quality - 1920x1080"));
    typeMap.put("33", new Meta("38", "MP4", "High Quality - 4096x230"));

    ArrayList<Video> videos = new ArrayList<Video>();
    for (String format : typeMap.keySet()) {
        if (foundArray.containsKey(format)) {
            Meta meta = typeMap.get(format);
            videos.add(new Video(meta.ext, meta.type, foundArray.get(format)));
        }
    }

    return videos;
}