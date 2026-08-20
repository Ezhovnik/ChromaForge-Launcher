package chromaforge.launcher.coders;

public class BasicParser {
    public class ParsingException extends RuntimeException {
        public ParsingException(String message) {
            super(message);
        }

        public ParsingException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    protected final String source;
    protected int pos = 0;
    protected int line = 1;
    protected int lineStart = 0;
    protected boolean hashComment = false;
    protected boolean clikeComment = false;

    protected static boolean isWhitespace(char c) {
        return c == ' ' || c == '\n' || c == '\r' || c == '\t' || c == '\f';
    }

    protected static int hexchar2int(char c) {
        if (c >= '0' && c <= '9') {
            return c - '0';
        }
        if (c >= 'a' && c <= 'f') {
            return 10 + c - 'a';
        }
        if (c >= 'A' && c <= 'F') {
            return 10 + c - 'A';
        }

        return -1;
    }

    protected static int detectBase(int c) {
        switch (c) {
            case 'B':
            case 'b':
                return 2;
            case 'O':
            case 'o':
                return 8;
            case 'X':
            case 'x':
                return 16;
        }
        return 10;
    }

    protected static boolean isDigit(char c) {
        return (c >= '0' && c <= '9');
    }

    protected static double power(double base, long power) {
        if (power == 0L) {
            return 1.0;
        }
        long exp;
        if (power < 0) {
            base = 1.0 / base;
            exp = ((-(power + 1L)) + 1L);
        } else {
            exp = power;
        }
        double result = 1.0;
        while (exp > 0) {
            if ((exp & 1L) != 0) {
                result *= base;
            }
            base *= base;
            exp >>= 1;
        }
        return result;
    }

    protected static boolean isIdentifierStart(char c) {
        return (c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z') || c == '_';
    }

    protected static boolean isIdentifierPart(char c) {
        return isIdentifierStart(c) || isDigit(c) || c == '-';
    }

    private void skipWhitespaceBasic(boolean newline) {
        while (hasNext()) {
            char next = source.charAt(pos);
            if (next == '\n') {
                if (!newline) {
                    break;
                }
                line++;
                lineStart = ++pos;
                continue;
            }
            if (isWhitespace(next)) {
                pos++;
            } else {
                break;
            }
        }
    }

    private void skipWhitespaceHashComment(boolean newline) {
        skipWhitespaceBasic(newline);
        if (hasNext() && source.charAt(pos) == '#') {
            if (!newline) {
                readUntilEOL();
                return;
            }
            skipLine();
            if (hasNext() && (isWhitespace(source.charAt(pos)) || source.charAt(pos) == '#')) {
                skipWhitespaceHashComment(newline);
            }
        }
    }

    private void skipWhitespaceCLikeComment(boolean newline) {
        skipWhitespaceBasic(newline);
        if (hasNext() && source.charAt(pos) == '/' && pos + 1 < source.length()) {
            pos++;
            switch (source.charAt(pos)) {
                case '*':
                    pos++;
                    while (hasNext()) {
                        if (source.charAt(pos) == '/' && source.charAt(pos - 1) == '*') {
                            pos++;
                            skipWhitespace(newline);
                            return;
                        }
                        pos++;
                    }
                    break;
                case '/':
                    if (!newline) {
                        readUntilEOL();
                        return;
                    }
                    skipLine();
                    if (hasNext() && (isWhitespace(source.charAt(pos)) || source.charAt(pos) == '/')) {
                        skipWhitespaceCLikeComment(newline);
                    }
                    break;
                default:
                    pos--;
                    break;
            }
        }
    }

    protected void skipWhitespace(boolean newline) {
        if (hashComment) {
            skipWhitespaceHashComment(newline);
            return;
        } else if (clikeComment) {
            skipWhitespaceCLikeComment(newline);
            return;
        }
        skipWhitespaceBasic(newline);
    }

    protected void skip(int n) {
        n = Integer.min(n, source.length() - pos);

        for (int i = 0; i < n; ++i) {
            char next = source.charAt(pos++);
            if (next == '\n') {
                line++;
                lineStart = pos;
            }
        }
    }

    protected void skipLine() {
        while (hasNext()) {
            if (source.charAt(pos) == '\n') {
                pos++;
                lineStart = pos;
                line++;
                break;
            }
            pos++;
        }
    }

    protected void skipEmptyLines() {
        if (!hasNext()) {
            return;
        }

        int initpos = pos;
        skipWhitespace(true);
        pos = Integer.max(initpos, lineStart);
    }

    protected boolean skipTo(String subString) {
        int idx = source.indexOf(subString, pos);
        if (idx == -1) {
            skip(source.length() - pos);
            return false;
        } else {
            skip(idx - pos);
            return true;
        }
    }

    protected void expect(char expected) {
        char c = peek();
        if (c != expected) {
            throw new ParsingException(expected + " expected");
        }
        pos++;
    }

    protected void expect(String subString) {
        if (subString.isEmpty()) {
            return;
        }
        for (int i = 0; i < subString.length(); ++i) {
            if (source.length() <= pos + i || source.charAt(pos + i) != subString.charAt(i)) {
                throw new ParsingException(subString + " expected");
            }
        }
        pos += subString.length();
    }

    protected boolean isNext(String subString) {
        if (subString.isEmpty()) {
            return false;
        }
        if (source.length() - pos < subString.length()) {
            return false;
        }
        return source.startsWith(subString, pos);
    }

    protected void expectNewLine() {
        while (hasNext()) {
            char next = source.charAt(pos);
            if (next == '\n') {
                line++;
                lineStart = ++pos;
                return;
            }
            if (isWhitespace(next)) {
                pos++;
            } else {
                throw new ParsingException("Line separator expected");
            }
        }
    }

    protected void goBack(int count) {
        if (pos < count) {
            throw new ParsingException("pos < jump");
        }
        if (pos != 0) {
            pos -= count;
        }
    }

    protected void reset() {
        pos = 0;
    }

    protected long parseSimpleInt(int base, int maxLength) {
        if (maxLength == 0) {
            return 0;
        }

        int start = pos;
        char c = peek();
        int index = hexchar2int(c);
        if (index == -1 || index >= base) {
            throw new ParsingException("Invalid number literal");
        }
        long value = (long)index;
        pos++;
        while (hasNext() && pos - start < maxLength) {
            c = source.charAt(pos);
            while (c == '_' && pos + 1 < source.length()) {
                c = source.charAt(++pos);
            }
            index = hexchar2int(c);
            if (index == -1 || index >= base) {
                return value;
            }
            value *= base;
            value += index;
            pos++;
        }
        return value;
    }

    protected long parseSimpleInt(int base) {
        return parseSimpleInt(base, Integer.MAX_VALUE);
    }

    protected double parseNumber(int sign) {
        char c = peek();
        int base = 10;
        if (c == '0' && pos + 1 < source.length()) {
            base = detectBase(source.charAt(pos + 1));
            if (base != 10) {
                pos += 2;
                return parseSimpleInt(base);
            }
        }
        if (c == 'i' && pos + 2 < source.length() && source.charAt(pos + 1) == 'n' && source.charAt(pos + 2) == 'f') {
            pos += 3;
            return Double.POSITIVE_INFINITY * sign;
        }
        if (c == 'n' && pos + 2 < source.length() && source.charAt(pos + 1) == 'a' && source.charAt(pos + 2) == 'n') {
            pos += 3;
            return Double.NaN * sign;
        }

        long value = parseSimpleInt(base);
        if (!hasNext()) {
            return value * sign;
        }
        c = source.charAt(pos);
        if (c == 'e' || c == 'E') {
            pos++;
            int s = 1;
            if (peek() == '-') {
                s = -1;
                pos++;
            } else if (peek() == '+') {
                pos++;
            }
            return sign * value * power(10, s * parseSimpleInt(10));
        }
        if (c == '.') {
            pos++;
            long exp = 1;
            while(hasNext() && source.charAt(pos) == '0') {
                exp *= 10;
                pos++;
            }
            long afterdot = 0;
            if (hasNext() && isDigit(source.charAt(pos))) {
                afterdot = parseSimpleInt(10);
            }
            exp = (long)(exp * power(10, Long.max(0L, (long)(Math.log10(afterdot) + 1.0))));

            double dvalue = (value + (afterdot / (double)(exp)));
            if (hasNext()) {
                c = source.charAt(pos);
                if (c == 'e' || c == 'E') {
                    pos++;
                    int s = 1;
                    if (peek() == '-') {
                        s = -1;
                        pos++;
                    } else if (peek() == '+') {
                        pos++;
                    }
                    return sign * dvalue * power(10, s * parseSimpleInt(10));
                }
            }
            return sign * dvalue;
        }
        return sign * value;
    }

    protected double parseNumber() {
        switch (peek()) {
            case '-':
                skip(1);
                return parseNumber(-1);
            case '+':
                skip(1);
                return parseNumber(1);
            default:
                return parseNumber(1);
        }
    }

    protected String parseString(char quote, boolean closeRequired) {
        StringBuilder sb = new StringBuilder("");
        while (hasNext()) {
            char c = source.charAt(pos);
            if (c == quote) {
                pos++;
                return sb.toString();
            }
            if (c == '\\') {
                pos++;
                c = nextChar();
                if (c >= '0' && c <= '7') {
                    pos--;
                    sb.append((char)(parseSimpleInt(8)));
                    continue;
                }
                if (c == 'u' || c == 'x') {
                    int codepoint = (int)parseSimpleInt(16, c == 'u' ? 4 : 2);
                    sb.append((char) codepoint);
                    continue;
                }
                switch (c) {
                    case 'n':
                        sb.append('\n');
                        break;
                    case 'r':
                        sb.append('\r');
                        break;
                    case 'b':
                        sb.append('\b');
                        break;
                    case 't':
                        sb.append('\t');
                        break;
                    case 'f':
                        sb.append('\f');
                        break;
                    case '\'':
                        sb.append('\'');
                        break;
                    case '"':
                        sb.append('"');
                        break;
                    case '\\':
                        sb.append('\\');
                        break;
                    case '/':
                        sb.append('/');
                        break;
                    case '\n':
                        continue;
                    default:
                        throw new ParsingException("'\\" + c + "' is an illegal escape");
                }
                continue;
            }
            if (c == '\n' && closeRequired) {
                throw new ParsingException("Non-closed string literal");
            }
            sb.append(c);
            pos++;
        }
        if (closeRequired) {
            throw new ParsingException("Unexpected end");
        }
        return sb.toString();
    }

    public String readUntil(char c) {
        int start = pos;
        while (hasNext() && source.charAt(pos) != c) {
            pos++;
        }
        return source.substring(start, pos);
    }

    public String readUntil(String s, boolean nothrow) {
        int start = pos;
        int found = source.indexOf(s, pos);
        if (found == -1) {
            if (nothrow) {
                pos = source.length();
                return source.substring(start);
            }
            throw new ParsingException(s + " expected");
        }
        skip(found - pos);
        return source.substring(start, pos);
    }

    public String readUntilWhitespace() {
        int start = pos;
        while (hasNext() && !isWhitespace(source.charAt(pos))) {
            pos++;
        }
        return source.substring(start, pos);
    }

    public String readUntilEOL() {
        int start = pos;
        while (hasNext() && source.charAt(pos) != '\n') {
            pos++;
        }
        if (pos > start && source.charAt(pos - 1) == '\r') {
            return source.substring(start, pos - 1);
        }
        return source.substring(start, pos);
    }

    public String parseName() {
        char c = peek();
        if (!isIdentifierStart(c)) {
            throw new ParsingException("Identifier expected");
        }
        int start = pos;
        while (hasNext() && isIdentifierPart(source.charAt(pos))) {
            pos++;
        }
        return source.substring(start, pos);
    }

    public boolean hasNext() {
        return pos < source.length();
    }

    public int remain() {
        return source.length() - pos;
    }

    public char peek() {
        skipWhitespace(true);
        if (pos >= source.length()) {
            throw new ParsingException("Unexpected end");
        }
        return source.charAt(pos);
    }

    public char peekInLine() {
        while (hasNext()) {
            char next = source.charAt(pos);
            if (next == '\n') {
                return next;
            }
            if (isWhitespace(next)) {
                pos++;
            } else {
                break;
            }
        }
        if (pos >= source.length()) {
            throw new ParsingException("Unexpected end");
        }
        return source.charAt(pos);
    }

    public char peekNoJump() {
        if (pos >= source.length()) {
            throw new ParsingException("Unexpected end");
        }
        return source.charAt(pos);
    }

    public char nextChar() {
        if (!hasNext()) {
            throw new ParsingException("Unexpected end");
        }
        return source.charAt(pos++);
    }

    public BasicParser(String source) {
        this.source = source.startsWith("\uFEFF") ? source.substring(1) : source;
    }
}
