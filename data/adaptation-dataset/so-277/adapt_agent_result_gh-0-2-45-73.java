public boolean MoveNext() {
        // Reset token state on each call
        _number = 0;
        _suffix = "";

        if (_versionString == null) {
            return false;
        }
        if (_position >= _length) {
            return false;
        }

        // Parse contiguous leading digits into _number
        while (_position < _length) {
            char ch = _versionString.charAt(_position);
            if (ch >= '0' && ch <= '9') {
                _number = (_number * 10) + (ch - '0');
                _position++;
            } else {
                break;
            }
        }

        // Capture suffix until '.' or end of string
        int suffixStart = _position;
        while (_position < _length) {
            char ch = _versionString.charAt(_position);
            if (ch == '.') {
                break;
            }
            _position++;
        }
        if (_position > suffixStart) {
            _suffix = _versionString.substring(suffixStart, _position);
        }

        // Advance past '.' separator if present
        if (_position < _length && _versionString.charAt(_position) == '.') {
            _position++;
        }

        return true;
    }