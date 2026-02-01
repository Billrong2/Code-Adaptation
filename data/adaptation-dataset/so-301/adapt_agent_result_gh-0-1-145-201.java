private void parseLink(byte[] link) throws IOException {
        if (link == null)
            throw new IOException("Link data is null");
        if (link.length < MINIMUM_LENGTH)
            throw new IOException("Link data too short: " + link.length);
        if (!isMagicPresent(link))
            throw new IOException("Missing Windows shortcut magic header");

        try {
            // flags
            final int flagsOffset = 0x14;
            if (link.length <= flagsOffset)
                throw new IOException("Invalid flags offset");
            final byte flags = link[flagsOffset];

            // file attributes
            final int fileAttributesOffset = 0x18;
            if (link.length <= fileAttributesOffset)
                throw new IOException("Invalid file attributes offset");
            final byte fileAttributes = link[fileAttributesOffset];
            final byte isDirectoryMask = (byte) 0x10;
            directory = (fileAttributes & isDirectoryMask) != 0;

            // shell settings
            final int shellOffset = 0x4c;
            final byte hasShellMask = (byte) 0x01;
            int shellLength = 0;
            if ((flags & hasShellMask) != 0) {
                if (link.length < shellOffset + 2)
                    throw new IOException("Invalid shell length offset");
                shellLength = bytesToWord(link, shellOffset) + 2;
            }

            // file location info
            final int fileStart = shellOffset + shellLength;
            final int fileLocationInfoFlagOffset = 0x08;
            if (link.length <= fileStart + fileLocationInfoFlagOffset)
                throw new IOException("Invalid file location info offset");
            final int fileLocationInfoFlag = link[fileStart + fileLocationInfoFlagOffset];
            local = (fileLocationInfoFlag & 2) == 0;

            final int baseNameOffsetOffset = 0x10;
            final int networkVolumeTableOffsetOffset = 0x14;
            final int finalNameOffsetOffset = 0x18;

            if (link.length <= fileStart + finalNameOffsetOffset)
                throw new IOException("Invalid final name offset");
            final int finalNameOffset = link[fileStart + finalNameOffsetOffset] + fileStart;
            final String finalName = getNullDelimitedString(link, finalNameOffset);

            if (local) {
                if (link.length <= fileStart + baseNameOffsetOffset)
                    throw new IOException("Invalid base name offset");
                final int baseNameOffset = link[fileStart + baseNameOffsetOffset] + fileStart;
                final String baseName = getNullDelimitedString(link, baseNameOffset);
                realFileName = baseName + finalName;
            } else {
                if (link.length <= fileStart + networkVolumeTableOffsetOffset)
                    throw new IOException("Invalid network volume table offset");
                final int networkVolumeTableOffset = link[fileStart + networkVolumeTableOffsetOffset] + fileStart;
                final int shareNameOffsetOffset = 0x08;
                if (link.length <= networkVolumeTableOffset + shareNameOffsetOffset)
                    throw new IOException("Invalid share name offset");
                final int shareNameOffset = link[networkVolumeTableOffset + shareNameOffsetOffset] + networkVolumeTableOffset;
                final String shareName = getNullDelimitedString(link, shareNameOffset);
                realFileName = shareName + "\\" + finalName;
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new IOException("Failed to parse Windows shortcut structure", e);
        }
    }