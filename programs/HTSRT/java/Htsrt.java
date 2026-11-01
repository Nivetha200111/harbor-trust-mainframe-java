public class Htsrt {
    static String wsInStatus = "  ";
    static String wsOutStatus = "  ";
    static boolean endOfInput = false;
    static boolean inputError = false;
    static long wsRecordCount = 0;
    static long wsAcceptCount = 0;
    static long wsRejectCount = 0;
    static int wsArithError = 0;
    static String wsEdgeCode = spaces(24);
    static String wsKey = spaces(12);
    static String wsPrevKey = spaces(12);
    static String wsBranch = spaces(3);
    static String wsPrevBranch = spaces(3);
    static String wsStatus = spaces(8);
    static java.math.BigDecimal wsAmount = java.math.BigDecimal.ZERO;
    static java.math.BigDecimal wsBalance = java.math.BigDecimal.ZERO;
    static java.math.BigDecimal wsPrincipal = java.math.BigDecimal.ZERO;
    static java.math.BigDecimal wsRate = java.math.BigDecimal.ZERO;
    static java.math.BigDecimal wsFxRate = java.math.BigDecimal.ZERO;
    static java.math.BigDecimal wsInterest = java.math.BigDecimal.ZERO;
    static java.math.BigDecimal wsFee = java.math.BigDecimal.ZERO;
    static String wsFeeWaived = "N";
    static java.math.BigDecimal wsCheckAmt = java.math.BigDecimal.ZERO;
    static String wsReturnReason = spaces(3);
    static long wsReturnCount = 0;
    static java.math.BigDecimal wsEntryAmt = java.math.BigDecimal.ZERO;
    static java.math.BigDecimal wsCycleTotal = java.math.BigDecimal.ZERO;
    static java.math.BigDecimal wsDebits = java.math.BigDecimal.ZERO;
    static java.math.BigDecimal wsCredits = java.math.BigDecimal.ZERO;
    static java.math.BigDecimal wsDelta = java.math.BigDecimal.ZERO;
    static long wsOutOfBalance = 0;
    static int wsDaysInactive = 0;
    static int wsDayBasis = 365;
    static String wsLeapFlag = "N";
    static String wsAmountText = spaces(8);
    static String wsShortAmount = spaces(6);
    static String wsRawBase = spaces(120);
    static int wsItemCount = 0;
    static String[] wsItems = new String[12];
    static java.math.BigDecimal wsPacked = java.math.BigDecimal.ZERO;
    static java.math.BigDecimal wsZoned = java.math.BigDecimal.ZERO;
    static String wsOldDate = "991231";
    static String wsCentury = "20";
    static String wsNewDate = "00000000";
    static char[] wsLine = makeSpaces(200);
    static java.math.BigDecimal wsReportTotal = java.math.BigDecimal.ZERO;
    static long wsReadTime = 0;
    static long wsWriteTime = 0;
    static long wsAbendCode = 0;
    static String inRecord = spaces(160);
    static String outRecord = spaces(200);
    static int returnCode = 0;
    static boolean ebcdicMode = false;
    static java.util.List<String> inLines = new java.util.ArrayList<>();
    static int inIndex = 0;
    static java.io.OutputStream outStream = null;
    static boolean outOpenOk = false;
    static boolean inOpenOk = false;
    static int[] ebcdicTable = null;
    public static void main(String[] args) throws Exception {
        String c1 = System.getenv("DIOCLE_COLLATION");
        String c2 = System.getenv("DIOCLE_CLOCK");
        if (c1 != null && c1.equals("EBCDIC")) ebcdicMode = true;
        if (c2 != null && c2.length() == 0) ebcdicMode = ebcdicMode;
        for (int i = 0; i < 12; i++) wsItems[i] = spaces(4);
        p0000Main();
        if (returnCode != 0) System.exit(returnCode);
    }
    static String spaces(int n) {
        StringBuilder sb = new StringBuilder(n);
        for (int i = 0; i < n; i++) sb.append(' ');
        return sb.toString();
    }
    static char[] makeSpaces(int n) {
        char[] c = new char[n];
        java.util.Arrays.fill(c, ' ');
        return c;
    }
    static String padRight(String s, int n) {
        if (s.length() >= n) return s.substring(0, n);
        StringBuilder sb = new StringBuilder(n);
        sb.append(s);
        for (int i = s.length(); i < n; i++) sb.append(' ');
        return sb.toString();
    }
    static String sub(String s, int start1, int len) {
        return s.substring(start1 - 1, start1 - 1 + len);
    }
    static String setSub(String base, int start1, int len, String val) {
        char[] c = base.toCharArray();
        for (int i = 0; i < len; i++) {
            char ch = ' ';
            if (i < val.length()) ch = val.charAt(i);
            c[start1 - 1 + i] = ch;
        }
        return new String(c);
    }
    static void moveToLine(int start1, int len, String val) {
        for (int i = 0; i < len; i++) {
            char ch = ' ';
            if (i < val.length()) ch = val.charAt(i);
            wsLine[start1 - 1 + i] = ch;
        }
    }
    static String lineString() { return new String(wsLine); }
    static void initEbcdic() {
        if (ebcdicTable != null) return;
        ebcdicTable = new int[256];
        java.nio.charset.Charset cs = java.nio.charset.Charset.forName("IBM037");
        for (int i = 0; i < 256; i++) {
            String s = String.valueOf((char) i);
            byte[] b = s.getBytes(cs);
            ebcdicTable[i] = b[0] & 0xFF;
        }
    }
    static int compareAlpha(String a, String b) {
        int n = Math.max(a.length(), b.length());
        if (!ebcdicMode) {
            for (int i = 0; i < n; i++) {
                char ca = i < a.length() ? a.charAt(i) : ' ';
                char cb = i < b.length() ? b.charAt(i) : ' ';
                if (ca != cb) return ca < cb ? -1 : 1;
            }
            return 0;
        } else {
            initEbcdic();
            for (int i = 0; i < n; i++) {
                char ca = i < a.length() ? a.charAt(i) : ' ';
                char cb = i < b.length() ? b.charAt(i) : ' ';
                int ea = ebcdicTable[ca & 0xFF];
                int eb = ebcdicTable[cb & 0xFF];
                if (ea != eb) return ea < eb ? -1 : 1;
            }
            return 0;
        }
    }
    static boolean isNumericX(String s) {
        if (s.length() == 0) return false;
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c < '0' || c > '9') return false;
        }
        return true;
    }
    static boolean isAlphabetic(String s) {
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c == ' ') continue;
            if (c >= 'A' && c <= 'Z') continue;
            if (c >= 'a' && c <= 'z') continue;
            return false;
        }
        return true;
    }
    static boolean eqPadded(String a, String lit) {
        int n = Math.max(a.length(), lit.length());
        for (int i = 0; i < n; i++) {
            char ca = i < a.length() ? a.charAt(i) : ' ';
            char cb = i < lit.length() ? lit.charAt(i) : ' ';
            if (ca != cb) return false;
        }
        return true;
    }
    static String displayCount9(long v) {
        long m = v & 0xFFFFFFFFL;
        String s = Long.toString(m);
        StringBuilder sb = new StringBuilder();
        for (int i = s.length(); i < 10; i++) sb.append('0');
        sb.append(s);
        return sb.toString();
    }
    static String displayPacked(java.math.BigDecimal v, int intDigits, int fracDigits) {
        boolean neg = v.signum() < 0;
        java.math.BigDecimal a = v.abs();
        a = a.setScale(fracDigits, java.math.RoundingMode.UNNECESSARY);
        java.math.BigDecimal scaled = a.movePointRight(fracDigits);
        java.math.BigInteger bi = scaled.toBigInteger();
        java.math.BigInteger mod = java.math.BigInteger.TEN.pow(intDigits + fracDigits);
        bi = bi.remainder(mod);
        String digits = bi.toString();
        int total = intDigits + fracDigits;
        StringBuilder sb = new StringBuilder();
        for (int i = digits.length(); i < total; i++) sb.append('0');
        sb.append(digits);
        String d = sb.toString();
        if (d.length() > total) d = d.substring(d.length() - total);
        String intPart = d.substring(0, intDigits);
        String fracPart = d.substring(intDigits);
        if (neg) return "-" + intPart + "." + fracPart;
        return "+" + intPart + "." + fracPart;
    }
    static void displayLine(String s) throws Exception {
        byte[] b = (s + "\n").getBytes(java.nio.charset.StandardCharsets.ISO_8859_1);
        System.out.write(b);
        System.out.flush();
    }
    static String trimTrailing(String s) {
        int e = s.length();
        while (e > 0 && s.charAt(e - 1) == ' ') e--;
        return s.substring(0, e);
    }
    static void writeOutRecord(String rec) throws Exception {
        String t = trimTrailing(rec);
        byte[] b = (t + "\n").getBytes(java.nio.charset.StandardCharsets.ISO_8859_1);
        outStream.write(b);
        outStream.flush();
        wsOutStatus = "00";
    }
    static java.math.BigDecimal parseAmountText(String s) {
        long iv = 0;
        for (int i = 0; i < s.length(); i++) iv = iv * 10 + (s.charAt(i) - '0');
        return java.math.BigDecimal.valueOf(iv).setScale(2);
    }
    private static void p0000Main() throws Exception {
        p1000Initialize();
        while (!endOfInput) {
            p2000ReadInput();
            if (!endOfInput) {
                p3000ValidateRecord();
                if (!inputError) {
                    p4000ProcessRecord();
                    p5000WriteResult();
                } else {
                    p6000RejectRecord();
                }
            }
        }
        p7000Finalize();
    }
    private static void p1000Initialize() throws Exception {
        String inPath = System.getenv("DD_INPUT-FILE");
        String outPath = System.getenv("DD_OUTPUT-FILE");
        if (inPath == null || inPath.length() == 0) {
            wsInStatus = "35";
            displayLine("OPEN INPUT FAILED " + wsInStatus);
            returnCode = 12;
            endOfInput = true;
        } else {
            try {
                byte[] all = java.nio.file.Files.readAllBytes(java.nio.file.Paths.get(inPath));
                String content = new String(all, java.nio.charset.StandardCharsets.ISO_8859_1);
                inLines.clear();
                int p = 0;
                while (true) {
                    int q = content.indexOf('\n', p);
                    String line;
                    if (q < 0) { line = content.substring(p); p = content.length(); }
                    else { line = content.substring(p, q); p = q + 1; if (line.endsWith("\r")) line = line.substring(0, line.length() - 1); }
                    if (p > content.length()) break;
                    if (q < 0) { if (line.length() == 0 && content.length() == 0) break; if (!(p >= content.length() && line.length() == 0 && content.endsWith("\n"))) inLines.add(line); break; }
                    inLines.add(line);
                    if (p >= content.length()) break;
                }
                inIndex = 0;
                wsInStatus = "00";
                inOpenOk = true;
            } catch (Exception e) {
                wsInStatus = "35";
                displayLine("OPEN INPUT FAILED " + wsInStatus);
                returnCode = 12;
                endOfInput = true;
            }
        }
        if (outPath == null || outPath.length() == 0) {
            wsOutStatus = "35";
            displayLine("OPEN OUTPUT FAILED " + wsOutStatus);
            returnCode = 12;
            endOfInput = true;
        } else {
            try {
                outStream = new java.io.FileOutputStream(outPath, false);
                wsOutStatus = "00";
                outOpenOk = true;
            } catch (Exception e) {
                wsOutStatus = "35";
                displayLine("OPEN OUTPUT FAILED " + wsOutStatus);
                returnCode = 12;
                endOfInput = true;
            }
        }
        wsRecordCount = 0; wsAcceptCount = 0; wsRejectCount = 0;
        wsEdgeCode = spaces(24);
        p1100CheckRunControl();
        p1200InitializeTotals();
    }
    private static void p1100CheckRunControl() {
        String part = sub(wsOldDate, 1, 2);
        int v = Integer.parseInt(part);
        if (v > 80) wsCentury = "19";
        else wsCentury = "20";
        wsNewDate = setSub(wsNewDate, 1, 2, wsCentury);
        wsNewDate = setSub(wsNewDate, 3, 6, wsOldDate);
    }
    private static void p1200InitializeTotals() {
        wsReportTotal = java.math.BigDecimal.ZERO.setScale(2);
        wsCycleTotal = java.math.BigDecimal.ZERO.setScale(2);
        wsDebits = java.math.BigDecimal.ZERO.setScale(2);
        wsCredits = java.math.BigDecimal.ZERO.setScale(2);
        wsDelta = java.math.BigDecimal.ZERO.setScale(2);
        wsPrevKey = spaces(12);
        wsPrevBranch = spaces(3);
    }
    private static void p2000ReadInput() throws Exception {
        if (!inOpenOk) { endOfInput = true; return; }
        if (inIndex >= inLines.size()) {
            wsInStatus = "10";
            endOfInput = true;
            return;
        }
        String line = inLines.get(inIndex++);
        wsInStatus = "00";
        if (line.length() > 160) line = line.substring(0, 160);
        if (line.length() < 160) line = padRight(line, 160);
        inRecord = line;
        wsRecordCount = (wsRecordCount + 1) % 1000000000L;
        wsRawBase = sub(inRecord, 1, 120);
        wsKey = sub(inRecord, 1, 12);
        wsBranch = sub(inRecord, 13, 3);
        wsAmountText = sub(inRecord, 16, 8);
        String s8 = sub(inRecord, 24, 8);
        if (s8.length() >= 6) wsShortAmount = s8.substring(0, 6);
        else wsShortAmount = padRight(s8, 6);
        wsStatus = sub(inRecord, 24, 8);
        if (!wsInStatus.equals("00") && !wsInStatus.equals("10")) {
            displayLine("READ FAILURE " + wsInStatus);
            inputError = true;
            endOfInput = true;
        }
    }
    private static void p3000ValidateRecord() {
        inputError = false;
        if (wsKey.equals(spaces(12))) p3100BadKey();
        if (!isNumericX(wsAmountText)) p3200BadAmount();
        if (wsStatus.equals(spaces(8))) p3300BadStatus();
        if (!isAlphabetic(wsBranch)) p3400BadBranch();
        if (wsRecordCount > 1 && compareAlpha(wsKey, wsPrevKey) < 0) wsEdgeCode = padRight("SORT-ORDER", 24);
        wsPrevKey = wsKey;
        p3500CheckMultiRecordType();
        p3600CheckDateBoundary();
    }
    private static void p3100BadKey() {
        inputError = true;
        wsEdgeCode = padRight("MISSING-KEY", 24);
    }
    private static void p3200BadAmount() {
        inputError = true;
        wsEdgeCode = padRight("INVALID-AMOUNT", 24);
    }
    private static void p3300BadStatus() {
        inputError = true;
        wsEdgeCode = padRight("INVALID-STATUS", 24);
    }
    private static void p3400BadBranch() {
        inputError = true;
        wsEdgeCode = padRight("INVALID-BRANCH", 24);
    }
    private static void p3500CheckMultiRecordType() {
        String c1 = sub(inRecord, 1, 1);
        if (!c1.equals("A") && !c1.equals("T")) wsEdgeCode = padRight("UNKNOWN-RECORD-TYPE", 24);
        String c2 = sub(inRecord, 2, 1);
        if (c2.equals("D")) wsItemCount = 2;
        else wsItemCount = 1;
    }
    private static void p3600CheckDateBoundary() {
        String d = sub(inRecord, 40, 8);
        if (d.equals("20261231")) wsEdgeCode = padRight("YEAR-END-ROLLOVER", 24);
        if (d.equals("20260228")) wsEdgeCode = padRight("NON-LEAP-FEB-END", 24);
    }
    private static void p4000ProcessRecord() {
        wsArithError = 0;
        wsAmount = parseAmountText(wsAmountText);
        wsBalance = wsAmount;
        wsPrincipal = wsAmount;
        java.math.BigDecimal sum = wsReportTotal.add(wsAmount);
        if (sum.abs().compareTo(new java.math.BigDecimal("99999999999.99")) > 0) wsArithError = 1;
        else wsReportTotal = sum.setScale(2);
        wsRate = new java.math.BigDecimal("0.0525");
        wsFxRate = new java.math.BigDecimal("1.0825");
        wsCheckAmt = wsAmount;
        wsEntryAmt = wsAmount;
        wsFee = java.math.BigDecimal.ZERO.setScale(2);
        wsFeeWaived = sub(inRecord, 120, 1);
        wsInterest = java.math.BigDecimal.ZERO.setScale(2);
        if (compareAlpha(wsKey, wsPrevKey) < 0) wsEdgeCode = padRight("SORT-ORDER", 24);
        wsPrevKey = wsKey;
        moveToLine(1, 12, wsKey);
        moveToLine(13, 3, wsBranch);
        moveToLine(16, 24, wsEdgeCode);
        moveToLine(40, 12, wsAmountText);
        moveToLine(52, 8, wsStatus);
        wsAcceptCount = (wsAcceptCount + 1) % 1000000000L;
    }
    private static void p5000WriteResult() throws Exception {
        outRecord = lineString();
        try {
            writeOutRecord(outRecord);
            wsWriteTime = wsWriteTime + 1;
        } catch (Exception e) {
            displayLine("WRITE FAILURE " + wsOutStatus);
            returnCode = 12;
            endOfInput = true;
            return;
        }
        if (!wsOutStatus.equals("00")) {
            displayLine("WRITE FAILURE " + wsOutStatus);
            returnCode = 12;
            endOfInput = true;
        } else {
        }
    }
    private static void p6000RejectRecord() throws Exception {
        wsRejectCount = (wsRejectCount + 1) % 1000000000L;
        displayLine("REJECT " + displayCount9(wsRecordCount) + " " + wsEdgeCode);
        wsLine = makeSpaces(200);
        moveToLine(1, 6, "REJECT");
        moveToLine(8, 12, wsKey);
        moveToLine(24, 24, wsEdgeCode);
        outRecord = lineString();
        writeOutRecord(outRecord);
    }
    private static void p7000Finalize() throws Exception {
        p7100CheckControlTotals();
        p7200ReportRunCounts();
        try { if (inOpenOk) {} } catch (Exception e) {}
        try { if (outStream != null) outStream.close(); } catch (Exception e) {}
        wsInStatus = "00";
        wsOutStatus = "00";
        if (!wsInStatus.equals("00")) displayLine("CLOSE IN " + wsInStatus);
        if (!wsOutStatus.equals("00")) displayLine("CLOSE OUT " + wsOutStatus);
        if (wsRejectCount > 0) returnCode = 4;
    }
    private static void p7100CheckControlTotals() throws Exception {
        long s = (wsAcceptCount + wsRejectCount) % 1000000000L;
        if (s != (wsRecordCount % 1000000000L)) {
            displayLine("CONTROL COUNT MISMATCH");
            returnCode = 12;
        }
        if (wsArithError != 0) {
            displayLine("ARITHMETIC SIZE ERROR");
            returnCode = 8;
        }
    }
    private static void p7200ReportRunCounts() throws Exception {
        displayLine("PROGRAM HTSRT COMPLETE");
        displayLine("READ=" + displayCount9(wsRecordCount));
        displayLine("ACCEPT=" + displayCount9(wsAcceptCount));
        displayLine("REJECT=" + displayCount9(wsRejectCount));
        displayLine("TOTAL=" + displayPacked(wsReportTotal, 11, 2));
        displayLine("EDGE=" + wsEdgeCode);
    }
    private static void p8000RejectAllZeroAmounts() {
        if (wsAmount.compareTo(java.math.BigDecimal.ZERO) == 0 && eqPadded(wsStatus, "ACTIVE")) wsEdgeCode = padRight("ZERO-ACTIVE-AMOUNT", 24);
    }
    private static void p8010CheckNegativeBalance() {
        if (wsBalance.compareTo(java.math.BigDecimal.ZERO) < 0) wsEdgeCode = padRight("NEGATIVE-BALANCE", 24);
    }
    private static void p8020CheckLongKey() throws Exception {
        if (sub(wsKey, 12, 1).equals(" ")) {} else displayLine("FULL-WIDTH-KEY " + wsKey);
    }
    private static void p8030CheckRoundedAmount() {
        wsPacked = wsAmount.setScale(2, java.math.RoundingMode.HALF_UP);
        if (wsPacked.compareTo(java.math.BigDecimal.ZERO) == 0 && wsAmount.compareTo(java.math.BigDecimal.ZERO) != 0) wsEdgeCode = padRight("TRUNCATION-RISK", 24);
    }
    private static void p8040CheckShortMove() {
        String v = wsAmountText;
        if (v.length() >= 6) wsShortAmount = v.substring(0, 6);
        else wsShortAmount = padRight(v, 6);
        if (!sub(wsAmountText, 7, 2).equals("00")) wsEdgeCode = padRight("SHORT-PIC-TRUNCATION", 24);
    }
    private static void p8050CheckFeeOverlay() {
        String ov = sub(wsRawBase, 120, 1);
        if (ov.equals("Y")) wsFeeWaived = "Y";
        else wsFeeWaived = "N";
    }
    private static void p8060CheckSignedZoned() {
        wsZoned = wsBalance;
        if (wsZoned.compareTo(java.math.BigDecimal.ZERO) < 0 && wsBalance.compareTo(java.math.BigDecimal.ZERO) >= 0) wsEdgeCode = padRight("SIGN-CONVERSION", 24);
    }
    private static void p8070CheckDateWindow() {
        if (sub(wsOldDate, 1, 2).equals("00")) wsEdgeCode = padRight("Y2K-WINDOW-REVIEW", 24);
        if (wsOldDate.equals("991231")) wsEdgeCode = padRight("CENTURY-BOUNDARY", 24);
    }
    private static void p8080CheckRatePrecision() {
        if (wsFxRate.compareTo(new java.math.BigDecimal("1.0825")) != 0) wsEdgeCode = padRight("RATE-IS-FOUR-DECIMAL", 24);
    }
    private static void p8090CheckRecordType() {
        String c = sub(inRecord, 1, 1);
        if (c.equals("A")) {} else if (c.equals("T")) {} else wsEdgeCode = padRight("BAD-RECORD-TYPE", 24);
    }
    private static void p8100CheckReturnCode() {
        if (!wsReturnReason.equals(spaces(3))) wsReturnCount = wsReturnCount + 1;
        if (wsReturnCount > wsRecordCount) returnCode = 12;
    }
    private static void p8110CheckAccumulator() {
        java.math.BigDecimal sum = wsReportTotal.add(wsAmount);
        if (sum.abs().compareTo(new java.math.BigDecimal("99999999999.99")) > 0) wsArithError = 1;
        else wsReportTotal = sum.setScale(2);
        if (wsReportTotal.compareTo(java.math.BigDecimal.ZERO) < 0) wsEdgeCode = padRight("NEGATIVE-CONTROL-TOTAL", 24);
    }
    private static void p8120CheckBranchBreak() throws Exception {
        if (!wsBranch.equals(wsPrevBranch)) {
            p8130WriteBranchTotal();
            wsPrevBranch = wsBranch;
        }
    }
    private static void p8130WriteBranchTotal() throws Exception {
        if (!wsPrevBranch.equals(spaces(3))) {
            displayLine("BRANCH BREAK " + wsPrevBranch);
            displayLine("BRANCH TOTAL " + displayPacked(wsReportTotal, 11, 2));
        }
        wsReportTotal = java.math.BigDecimal.ZERO.setScale(2);
    }
    private static void p8140CheckInputShape() throws Exception {
        if (inRecord.equals(spaces(160))) { inputError = true; wsEdgeCode = padRight("EMPTY-RECORD", 24); }
        if (!sub(inRecord, 160, 1).equals(" ")) displayLine("TRAILING-CONTENT PRESENT");
    }
    private static void p8150CheckMonthRollover() {
        if (sub(inRecord, 40, 8).equals("20261231")) wsNewDate = "20270101";
        if (sub(inRecord, 40, 8).equals("20260131")) wsNewDate = "20260201";
    }
    private static void p8160CheckLeapYear() {
        if (sub(inRecord, 40, 4).equals("2024")) { wsLeapFlag = "Y"; wsDayBasis = 366; }
        else { wsLeapFlag = "N"; wsDayBasis = 365; }
    }
    private static void p8170CheckOddCents() {
        if (sub(wsAmountText, 7, 2).equals("50")) wsEdgeCode = padRight("HALF-CENT-ROUNDING-CASE", 24);
        if (sub(wsAmountText, 7, 2).equals("99")) wsEdgeCode = padRight("ROUND-UP-BOUNDARY", 24);
    }
    private static void p8180CheckStatusTransition() {
        if (eqPadded(wsStatus, "ACTIVE")) {} else if (eqPadded(wsStatus, "DORMANT")) {} else if (eqPadded(wsStatus, "BLOCKED")) {} else wsEdgeCode = padRight("UNKNOWN-STATE", 24);
    }
    private static void p8190CheckBalanceLimit() {
        if (wsBalance.compareTo(new java.math.BigDecimal("999999999.99")) > 0) wsEdgeCode = padRight("BALANCE-OVERFLOW", 24);
        if (wsBalance.compareTo(new java.math.BigDecimal("-999999999.99")) < 0) wsEdgeCode = padRight("BALANCE-UNDERFLOW", 24);
    }
    private static void p8200CheckReconciliation() {
        java.math.BigDecimal d = wsDebits.subtract(wsCredits);
        if (d.abs().compareTo(new java.math.BigDecimal("99999999999.99")) > 0) wsArithError = 1;
        else wsDelta = d.setScale(2);
        if (wsDelta.compareTo(java.math.BigDecimal.ZERO) == 0) {}
    }
    private static void p8210CheckCopybookCount() {
        if (wsItemCount < 1 || wsItemCount > 12) wsEdgeCode = padRight("OCCURS-COUNT-INVALID", 24);
        wsItemCount = 1;
        while (!(wsItemCount > 1)) { wsItemCount = wsItemCount + 1; }
    }
    private static void p8220CheckCallInterface() {
        moveToLine(1, 12, wsKey);
        moveToLine(13, 24, wsEdgeCode);
        if (sub(lineString(), 1, 1).equals(" ")) wsEdgeCode = padRight("CALL-KEY-MISSING", 24);
    }
    private static void p8230CheckWriteStatus() throws Exception {
        if (wsOutStatus.equals("00")) {}
        else { displayLine("DATASET STATUS " + wsOutStatus); returnCode = 12; }
    }
    private static void p8240CheckReadStatus() throws Exception {
        if (wsInStatus.equals("10")) endOfInput = true;
        else { if (!wsInStatus.equals("00")) displayLine("INPUT STATUS " + wsInStatus); }
    }
    private static void p8250CheckCurrencyScale() {
        java.math.BigDecimal prod = wsAmount.multiply(new java.math.BigDecimal("1.0000"));
        prod = prod.setScale(2, java.math.RoundingMode.HALF_UP);
        if (prod.abs().compareTo(new java.math.BigDecimal("999999999.99")) > 0) wsArithError = 1;
        else wsAmount = prod;
        if (wsFxRate.compareTo(java.math.BigDecimal.ZERO) < 0) wsEdgeCode = padRight("NEGATIVE-FX-RATE", 24);
    }
    private static void p8260CheckNestedCallResult() throws Exception {
        wsAbendCode = 0;
        if (wsAbendCode != 0) { displayLine("CALLED ROUTINE RETURNED ERROR"); returnCode = 8; }
    }
    private static void p8270CheckAuditBalance() {
        java.math.BigDecimal sum = wsCycleTotal.add(wsAmount);
        if (sum.abs().compareTo(new java.math.BigDecimal("99999999999.99")) > 0) wsArithError = 1;
        else wsCycleTotal = sum.setScale(2);
        if (wsCycleTotal.compareTo(java.math.BigDecimal.ZERO) == 0) {}
    }
    private static void p8280CheckKeyUniqueness() {
        if (wsKey.equals(wsPrevKey) && !wsKey.equals(spaces(12))) wsEdgeCode = padRight("DUPLICATE-KEY", 24);
        wsPrevKey = wsKey;
    }
    private static void p8290CheckRunId() throws Exception {
        if (wsNewDate.equals("00000000")) { displayLine("RUN DATE NOT INITIALIZED"); returnCode = 12; }
        if (wsRecordCount == 0) displayLine("EMPTY INPUT");
    }
    private static void p9900OperationalChecks() throws Exception {
        p8000RejectAllZeroAmounts();
        p8010CheckNegativeBalance();
        p8020CheckLongKey();
        p8030CheckRoundedAmount();
        p8040CheckShortMove();
        p8050CheckFeeOverlay();
        p8060CheckSignedZoned();
        p8070CheckDateWindow();
        p8080CheckRatePrecision();
        p8090CheckRecordType();
        p8100CheckReturnCode();
        p8110CheckAccumulator();
        p8120CheckBranchBreak();
        p8130WriteBranchTotal();
        p8140CheckInputShape();
        p8150CheckMonthRollover();
        p8160CheckLeapYear();
        p8170CheckOddCents();
        p8180CheckStatusTransition();
        p8190CheckBalanceLimit();
        p8200CheckReconciliation();
        p8210CheckCopybookCount();
        p8220CheckCallInterface();
        p8230CheckWriteStatus();
        p8240CheckReadStatus();
        p8250CheckCurrencyScale();
        p8260CheckNestedCallResult();
        p8270CheckAuditBalance();
        p8280CheckKeyUniqueness();
        p8290CheckRunId();
    }
}