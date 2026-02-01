public void addImageToPage(final org.apache.pdfbox.pdmodel.PDDocument document, final int pdfpage, final int x, final int y, final float scale, final java.awt.image.BufferedImage sourceImage) throws java.io.IOException {
    // Validate inputs
    if (document == null) {
        throw new IllegalArgumentException("PDDocument must not be null");
    }
    if (sourceImage == null) {
        throw new IllegalArgumentException("BufferedImage must not be null");
    }
    if (pdfpage < 0 || pdfpage >= document.getDocumentCatalog().getAllPages().size()) {
        throw new IllegalArgumentException("Page index out of bounds: " + pdfpage);
    }
    if (scale <= 0.0f) {
        throw new IllegalArgumentException("Scale factor must be greater than zero");
    }

    // Convert the image to TYPE_4BYTE_ABGR so PDFBox won't throw exceptions (e.g. for transparent PNGs)
    final java.awt.image.BufferedImage image = new java.awt.image.BufferedImage(
            sourceImage.getWidth(), sourceImage.getHeight(), java.awt.image.BufferedImage.TYPE_4BYTE_ABGR);
    image.createGraphics().drawRenderedImage(sourceImage, null);

    // Create PDFBox image object
    final org.apache.pdfbox.pdmodel.graphics.xobject.PDXObjectImage ximage =
            new org.apache.pdfbox.pdmodel.graphics.xobject.PDPixelMap(document, image);

    // Retrieve page
    final org.apache.pdfbox.pdmodel.PDPage page =
            (org.apache.pdfbox.pdmodel.PDPage) document.getDocumentCatalog().getAllPages().get(pdfpage);

    // Draw image onto page, ensuring the content stream is always closed
    try (org.apache.pdfbox.pdmodel.edit.PDPageContentStream contentStream =
                 new org.apache.pdfbox.pdmodel.edit.PDPageContentStream(document, page, true, true)) {
        contentStream.drawXObject(
                ximage,
                x,
                y,
                ximage.getWidth() * scale,
                ximage.getHeight() * scale);
    }
}