public class Htcbr {
    private String wsInStatus = "  ";
    private String wsOutStatus = "  ";
    private boolean endOfInput = false;
    private String wsError = "N";
    private long wsRecordCount = 0;
    private long wsAcceptCount = 0;
    private long wsRejectCount = 0;
    private int wsArithError = 0;
    private String wsEdgeCode = spaces(24);
    private String wsKey = spaces(12);
    private String wsPrevKey = spaces(12);
    private String wsBranch = spaces(3);
    private String wsPrevBranch = spaces(3);
    private String wsStatus = spaces(8);
    private java.math.BigDecimal wsAmount = java.math.BigDecimal.ZERO;
    private java.math.BigDecimal wsBalance = java.math.BigDecimal.ZERO;
    private java.math.BigDecimal wsPrincipal = java.math.BigDecimal.ZERO;
    private java.math.BigDecimal wsRate = java.math.BigDecimal.ZERO;
    private java.math.BigDecimal wsFxRate = java.math.BigDecimal.ZERO;
    private java.math.BigDecimal wsInterest = java.math.BigDecimal.ZERO;
    private java.math.BigDecimal wsFee = java.math.BigDecimal.ZERO;
    private String wsFeeWaived = "N";
    private java.math.BigDecimal wsCheckAmt = java.math.BigDecimal.ZERO;
    private String wsReturnReason = spaces(3);
    private long wsReturnCount = 0;
    private java.math.BigDecimal wsEntryAmt = java.math.BigDecimal.ZERO;
    private java.math.BigDecimal wsBranchTotal = java.math.BigDecimal.ZERO;
    private java.math.BigDecimal wsCycleTotal = java.math.BigDecimal.ZERO;
    private java.math.BigDecimal wsDebits = java.math.BigDecimal.ZERO;
    private java.math.BigDecimal wsCredits = java.math.BigDecimal.ZERO;
    private java.math.BigDecimal wsDelta = java.math.BigDecimal.ZERO;
    private long wsOutOfBalance = 0;
    private int wsDaysInactive = 0;
    private int wsDayBasis = 365;
    private String wsLeapFlag = "N";
    private String wsAmountText = spaces(8);
    private String wsShortAmount = spaces(6);
    private String wsRawBase = spaces(120);
    private int wsItemCount = 0;
    private String[] wsItems = new String[12];
    private java.math.BigDecimal wsPacked = java.math.BigDecimal.ZERO;
    private java.math.BigDecimal wsZoned = java.math.BigDecimal.ZERO;
    private String wsOldDate = "991231";
    private int wsCentury = 20;
    private char[] wsNewDate = "00000000".toCharArray();
    private char[] wsLine = spaces(200).toCharArray();
    private java.math.BigDecimal wsReportTotal = java.math.BigDecimal.ZERO;
    private long wsReadTime = 0;
    private long wsWriteTime = 0;
    private int wsAbendCode = 0;
    private String inRecord = spaces(160);
    private String outRecord = spaces(200);
    private int returnCode = 0;
    private boolean ebcdicMode = false;
    private java.util.List<String> inputLines = new java.util.ArrayList<>();
    private int inputIndex = 0;
    private java.io.OutputStream outStream = null;
    public static void main(String[] args) throws Exception {
        Htcbr h = new Htcbr();
        h.run();
    }
    private void run() throws Exception {
        String clock = System.getenv("DIOCLE_CLOCK");
        String coll = System.getenv("DIOCLE_COLLATION");
        ebcdicMode = "EBCDIC".equals(coll);
        if (clock != null && clock.length() == 0) ebcdicMode = ebcdicMode;
        for (int i = 0; i < 12; i++) wsItems[i] = spaces(4);
        p0000Main();
        if (returnCode != 0) System.exit(returnCode);
    }
    private static String spaces(int n) {
        char[] c = new char[n];
        java.util.Arrays.fill(c, ' ');
        return new String(c);
    }
    private static String padOrTruncate(String s, int len) {
        if (s == null) s = "";
        if (s.length() > len) return s.substring(0, len);
        if (s.length() == len) return s;
        StringBuilder b = new StringBuilder(s);
        while (b.length() < len) b.append(' ');
        return b.toString();
    }
    private void moveToLine(int start1, int len, String src) {
        String v = padOrTruncate(src, len);
        for (int i = 0; i < len; i++) wsLine[start1 - 1 + i] = v.charAt(i);
    }
    private String lineString() { return new String(wsLine); }
    private void display(String s) { System.out.print(s + "\n"); }
    private boolean isEbcdic() { return ebcdicMode; }
    private int compareAlpha(String a, String b) {
        if (isEbcdic()) {
            try {
                byte[] ba = a.getBytes("Cp037");
                byte[] bb = b.getBytes("Cp037");
                int n = Math.min(ba.length, bb.length);
                for (int i = 0; i < n; i++) {
                    int x = ba[i] & 0xFF;
                    int y = bb[i] & 0xFF;
                    if (x != y) return x < y ? -1 : 1;
                }
                if (ba.length == bb.length) return 0;
                return ba.length < bb.length ? -1 : 1;
            } catch (Exception e) { }
        }
        int n = Math.min(a.length(), b.length());
        for (int i = 0; i < n; i++) {
            char x = a.charAt(i);
            char y = b.charAt(i);
            if (x != y) return x < y ? -1 : 1;
        }
        if (a.length() == b.length()) return 0;
        return a.length() < b.length() ? -1 : 1;
    }
    private static boolean isAllDigits(String s) {
        if (s.length() == 0) return false;
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c < '0' || c > '9') return false;
        }
        return true;
    }
    private static boolean isAlphabetic(String s) {
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c == ' ') continue;
            if (c >= 'A' && c <= 'Z') continue;
            if (c >= 'a' && c <= 'z') continue;
            return false;
        }
        return true;
    }
    private static String fmtCount(long v, int digits) {
        long m = 1;
        for (int i = 0; i < digits; i++) m *= 10L;
        long t = ((v % m) + m) % m;
        String s = Long.toString(t);
        while (s.length() < digits) s = "0" + s;
        return s;
    }
    private static String fmtPacked(java.math.BigDecimal v, int intD, int fracD) {
        boolean neg = v.signum() < 0;
        if (v.compareTo(java.math.BigDecimal.ZERO) == 0) neg = false;
        java.math.BigDecimal a = v.abs();
        java.math.BigDecimal sc = a.movePointRight(fracD);
        sc = sc.setScale(0, java.math.RoundingMode.DOWN);
        String d = sc.toBigInteger().toString();
        int total = intD + fracD;
        if (d.length() > total) d = d.substring(d.length() - total);
        while (d.length() < total) d = "0" + d;
        String ip = d.substring(0, intD);
        String fp = d.substring(intD);
        String num = ip + "." + fp;
        if (neg) return "-" + num;
        return "+" + num;
    }
    private String subIn(int start1, int len) {
        String r = padOrTruncate(inRecord, 160);
        return r.substring(start1 - 1, start1 - 1 + len);
    }
    private void p0000Main() throws Exception {
        p1000Initialize();
        while (!endOfInput) {
            p2000ReadInput();
            if (!endOfInput) {
                p3000ValidateRecord();
                if (!"Y".equals(wsError)) {
                    p4000ProcessRecord();
                    p5000WriteResult();
                } else {
                    p6000RejectRecord();
                }
            }
        }
        p7000Finalize();
    }
    private void p1000Initialize() throws Exception {
        String inPath = System.getenv("DD_INPUT-FILE");
        String outPath = System.getenv("DD_OUTPUT-FILE");
        if (inPath == null || inPath.isEmpty()) {
            wsInStatus = "35";
        } else {
            try {
                java.nio.file.Path p = java.nio.file.Paths.get(inPath);
                byte[] all = java.nio.file.Files.readAllBytes(p);
                inputLines.clear();
                int s = 0;
                for (int i = 0; i < all.length; i++) {
                    if (all[i] == (byte)0x0A) {
                        int e = i;
                        if (e > s && all[e-1] == (byte)0x0D) e--;
                        byte[] rec = java.util.Arrays.copyOfRange(all, s, e);
                        String line = new String(rec, java.nio.charset.StandardCharsets.ISO_8859_1);
                        inputLines.add(line);
                        s = i + 1;
                    }
                }
                if (s < all.length) {
                    byte[] rec = java.util.Arrays.copyOfRange(all, s, all.length);
                    String line = new String(rec, java.nio.charset.StandardCharsets.ISO_8859_1);
                    inputLines.add(line);
                }
                inputIndex = 0;
                wsInStatus = "00";
            } catch (Exception e) {
                wsInStatus = "35";
            }
        }
        if (!"00".equals(wsInStatus)) {
            display("OPEN INPUT FAILED " + wsInStatus);
            returnCode = 12;
            endOfInput = true;
        }
        if (outPath == null || outPath.isEmpty()) {
            wsOutStatus = "35";
        } else {
            try {
                outStream = new java.io.FileOutputStream(outPath, false);
                wsOutStatus = "00";
            } catch (Exception e) {
                wsOutStatus = "35";
            }
        }
        if (!"00".equals(wsOutStatus)) {
            display("OPEN OUTPUT FAILED " + wsOutStatus);
            returnCode = 12;
            endOfInput = true;
        }
        wsRecordCount = 0; wsAcceptCount = 0; wsRejectCount = 0;
        wsEdgeCode = spaces(24);
        p1100CheckRunControl();
        p1200InitializeTotals();
    }
    private void p1100CheckRunControl() {
        String part = wsOldDate.substring(0, 2);
        int v = Integer.parseInt(part);
        if (v > 80) wsCentury = 19; else wsCentury = 20;
        String cs = String.valueOf(wsCentury);
        while (cs.length() < 2) cs = "0" + cs;
        wsNewDate[0] = cs.charAt(0); wsNewDate[1] = cs.charAt(1);
        for (int i = 0; i < 6; i++) wsNewDate[2+i] = wsOldDate.charAt(i);
    }
    private void p1200InitializeTotals() {
        wsReportTotal = java.math.BigDecimal.ZERO;
        wsCycleTotal = java.math.BigDecimal.ZERO;
        wsDebits = java.math.BigDecimal.ZERO;
        wsCredits = java.math.BigDecimal.ZERO;
        wsDelta = java.math.BigDecimal.ZERO;
        wsPrevKey = spaces(12);
        wsPrevBranch = spaces(3);
    }
    private void p2000ReadInput() {
        if (endOfInput) return;
        if (!"00".equals(wsInStatus) && !"10".equals(wsInStatus) && inputLines.isEmpty() && inputIndex == 0) { }
        if (inputIndex < inputLines.size()) {
            String line = inputLines.get(inputIndex++);
            inRecord = padOrTruncate(line, 160);
            wsInStatus = "00";
            wsRecordCount = (wsRecordCount + 1) % 1000000000L;
            wsRawBase = padOrTruncate(inRecord.substring(0, Math.min(160, inRecord.length())), 120).substring(0,120);
            if (inRecord.length() < 160) inRecord = padOrTruncate(inRecord, 160);
            wsKey = subIn(1, 12);
            wsBranch = subIn(13, 3);
            wsAmountText = subIn(16, 8);
            String tmp8 = subIn(24, 8);
            wsShortAmount = padOrTruncate(tmp8, 6);
            wsStatus = tmp8;
        } else {
            if ("35".equals(wsInStatus)) {
                endOfInput = true;
            } else {
                wsInStatus = "10";
                endOfInput = true;
            }
        }
        if (!"00".equals(wsInStatus) && !"10".equals(wsInStatus)) {
            display("READ FAILURE " + wsInStatus);
            wsError = "Y";
            endOfInput = true;
        }
    }
    private void p3000ValidateRecord() {
        wsError = "N";
        if (wsKey.equals(spaces(12))) p3100BadKey();
        if (!isAllDigits(wsAmountText)) p3200BadAmount();
        if (wsStatus.equals(spaces(8))) p3300BadStatus();
        if (!isAlphabetic(wsBranch)) p3400BadBranch();
        if (wsRecordCount > 1 && compareAlpha(wsKey, wsPrevKey) < 0) {
            wsEdgeCode = padOrTruncate("SORT-ORDER", 24);
        }
        wsPrevKey = wsKey;
        p3500CheckMultiRecordType();
        p3600CheckDateBoundary();
    }
    private void p3100BadKey() { wsError = "Y"; wsEdgeCode = padOrTruncate("MISSING-KEY", 24); }
    private void p3200BadAmount() { wsError = "Y"; wsEdgeCode = padOrTruncate("INVALID-AMOUNT", 24); }
    private void p3300BadStatus() { wsError = "Y"; wsEdgeCode = padOrTruncate("INVALID-STATUS", 24); }
    private void p3400BadBranch() { wsError = "Y"; wsEdgeCode = padOrTruncate("INVALID-BRANCH", 24); }
    private void p3500CheckMultiRecordType() {
        String c1 = subIn(1, 1);
        if (!"A".equals(c1) && !"T".equals(c1)) wsEdgeCode = padOrTruncate("UNKNOWN-RECORD-TYPE", 24);
        String c2 = subIn(2, 1);
        if ("D".equals(c2)) wsItemCount = 2; else wsItemCount = 1;
    }
    private void p3600CheckDateBoundary() {
        String d = subIn(40, 8);
        if ("20261231".equals(d)) wsEdgeCode = padOrTruncate("YEAR-END-ROLLOVER", 24);
        if ("20260228".equals(d)) wsEdgeCode = padOrTruncate("NON-LEAP-FEB-END", 24);
    }
    private void p4000ProcessRecord() {
        wsArithError = 0;
        java.math.BigDecimal amt;
        try { amt = new java.math.BigDecimal(wsAmountText); } catch (Exception e) { amt = java.math.BigDecimal.ZERO; }
        wsAmount = amt;
        wsBalance = wsAmount;
        wsPrincipal = wsAmount;
        java.math.BigDecimal nt = wsReportTotal.add(wsAmount);
        java.math.BigDecimal lim = new java.math.BigDecimal("99999999999.99");
        java.math.BigDecimal nlim = new java.math.BigDecimal("-99999999999.99");
        if (nt.compareTo(lim) > 0 || nt.compareTo(nlim) < 0) wsArithError = 1;
        else wsReportTotal = nt;
        wsRate = new java.math.BigDecimal("0.0525");
        wsFxRate = new java.math.BigDecimal("1.0825");
        wsCheckAmt = wsAmount;
        wsEntryAmt = wsAmount;
        wsFee = java.math.BigDecimal.ZERO;
        String c120 = subIn(120, 1);
        wsFeeWaived = c120;
        wsInterest = java.math.BigDecimal.ZERO;
        if (!wsBranch.equals(wsPrevBranch)) p8130WriteBranchTotal();
        java.math.BigDecimal nb = wsBranchTotal.add(wsEntryAmt);
        if (nb.compareTo(lim) > 0 || nb.compareTo(nlim) < 0) wsArithError = 1;
        else wsBranchTotal = nb;
        moveToLine(1, 12, wsKey);
        moveToLine(13, 3, wsBranch);
        moveToLine(16, 24, wsEdgeCode);
        moveToLine(40, 12, wsAmountText);
        moveToLine(52, 8, wsStatus);
        wsAcceptCount = (wsAcceptCount + 1) % 1000000000L;
    }
    private void p5000WriteResult() throws Exception {
        outRecord = lineString();
        try {
            String rec = outRecord;
            int e = rec.length();
            while (e > 0 && rec.charAt(e-1) == ' ') e--;
            String w = rec.substring(0, e);
            byte[] b = (w + "\n").getBytes(java.nio.charset.StandardCharsets.ISO_8859_1);
            outStream.write(b);
            wsOutStatus = "00";
            wsWriteTime++;
        } catch (Exception e) {
            wsOutStatus = "30";
            display("WRITE FAILURE " + wsOutStatus);
            returnCode = 12;
            endOfInput = true;
        }
    }
    private void p6000RejectRecord() throws Exception {
        wsRejectCount = (wsRejectCount + 1) % 1000000000L;
        display("REJECT " + fmtCount(wsRecordCount, 10) + " " + wsEdgeCode);
        wsLine = spaces(200).toCharArray();
        moveToLine(1, 6, "REJECT");
        moveToLine(8, 12, wsKey);
        moveToLine(24, 24, wsEdgeCode);
        outRecord = lineString();
        try {
            String rec = outRecord;
            int e = rec.length();
            while (e > 0 && rec.charAt(e-1) == ' ') e--;
            String w = rec.substring(0, e);
            byte[] b = (w + "\n").getBytes(java.nio.charset.StandardCharsets.ISO_8859_1);
            outStream.write(b);
            wsOutStatus = "00";
        } catch (Exception ex) { wsOutStatus = "30"; }
    }
    private void p7000Finalize() throws Exception {
        p7100CheckControlTotals();
        p7200ReportRunCounts();
        try { if (outStream != null) outStream.close(); } catch (Exception e) { wsOutStatus = "30"; }
        if (!"35".equals(wsInStatus) && !"10".equals(wsInStatus)) { }
        if (!"00".equals(wsInStatus) && !"10".equals(wsInStatus)) display("CLOSE IN " + wsInStatus);
        else { if ("10".equals(wsInStatus)) wsInStatus = "00"; }
        if (!"00".equals(wsOutStatus)) display("CLOSE OUT " + wsOutStatus);
        if (wsRejectCount > 0) returnCode = 4;
    }
    private void p7100CheckControlTotals() {
        if (wsAcceptCount + wsRejectCount != wsRecordCount) {
            display("CONTROL COUNT MISMATCH");
            returnCode = 12;
        }
        if (wsArithError != 0) {
            display("ARITHMETIC SIZE ERROR");
            returnCode = 8;
        }
    }
    private void p7200ReportRunCounts() {
        display("PROGRAM HTCBR COMPLETE");
        display("READ=" + fmtCount(wsRecordCount, 10));
        display("ACCEPT=" + fmtCount(wsAcceptCount, 10));
        display("REJECT=" + fmtCount(wsRejectCount, 10));
        display("TOTAL=" + fmtPacked(wsReportTotal, 11, 2));
        display("EDGE=" + wsEdgeCode);
    }
    private void p8000RejectAllZeroAmounts() {
        if (wsAmount.compareTo(java.math.BigDecimal.ZERO) == 0 && "ACTIVE  ".equals(wsStatus)) wsEdgeCode = padOrTruncate("ZERO-ACTIVE-AMOUNT", 24);
    }
    private void p8010CheckNegativeBalance() {
        if (wsBalance.compareTo(java.math.BigDecimal.ZERO) < 0) wsEdgeCode = padOrTruncate("NEGATIVE-BALANCE", 24);
    }
    private void p8020CheckLongKey() {
        if (wsKey.length() >= 12 && wsKey.charAt(11) == ' ') { } else display("FULL-WIDTH-KEY " + wsKey);
    }
    private void p8030CheckRoundedAmount() {
        wsPacked = wsAmount.setScale(0, java.math.RoundingMode.HALF_UP);
        if (wsPacked.compareTo(java.math.BigDecimal.ZERO) == 0 && wsAmount.compareTo(java.math.BigDecimal.ZERO) != 0) wsEdgeCode = padOrTruncate("TRUNCATION-RISK", 24);
    }
    private void p8040CheckShortMove() {
        wsShortAmount = padOrTruncate(wsAmountText, 6);
        if (!wsAmountText.substring(6, 8).equals("00")) wsEdgeCode = padOrTruncate("SHORT-PIC-TRUNCATION", 24);
    }
    private void p8050CheckFeeOverlay() {
        char ov = wsRawBase.length() >= 120 ? wsRawBase.charAt(119) : ' ';
        if (ov == 'Y') wsFeeWaived = "Y"; else wsFeeWaived = "N";
    }
    private void p8060CheckSignedZoned() {
        wsZoned = wsBalance;
        if (wsZoned.compareTo(java.math.BigDecimal.ZERO) < 0 && wsBalance.compareTo(java.math.BigDecimal.ZERO) >= 0) wsEdgeCode = padOrTruncate("SIGN-CONVERSION", 24);
    }
    private void p8070CheckDateWindow() {
        if (wsOldDate.substring(0,2).equals("00")) wsEdgeCode = padOrTruncate("Y2K-WINDOW-REVIEW", 24);
        if ("991231".equals(wsOldDate)) wsEdgeCode = padOrTruncate("CENTURY-BOUNDARY", 24);
    }
    private void p8080CheckRatePrecision() {
        if (wsFxRate.compareTo(new java.math.BigDecimal("1.0825")) != 0) wsEdgeCode = padOrTruncate("RATE-IS-FOUR-DECIMAL", 24);
    }
    private void p8090CheckRecordType() {
        String c = subIn(1, 1);
        if ("A".equals(c)) { } else if ("T".equals(c)) { } else wsEdgeCode = padOrTruncate("BAD-RECORD-TYPE", 24);
    }
    private void p8100CheckReturnCode() {
        if (!wsReturnReason.equals(spaces(3))) wsReturnCount++;
        if (wsReturnCount > wsRecordCount) returnCode = 12;
    }
    private void p8110CheckAccumulator() {
        java.math.BigDecimal nt = wsReportTotal.add(wsAmount);
        java.math.BigDecimal lim = new java.math.BigDecimal("99999999999.99");
        java.math.BigDecimal nlim = new java.math.BigDecimal("-99999999999.99");
        if (nt.compareTo(lim) > 0 || nt.compareTo(nlim) < 0) wsArithError = 1; else wsReportTotal = nt;
        if (wsReportTotal.signum() < 0) wsEdgeCode = padOrTruncate("NEGATIVE-CONTROL-TOTAL", 24);
    }
    private void p8120CheckBranchBreak() {
        if (!wsBranch.equals(wsPrevBranch)) { p8130WriteBranchTotal(); wsPrevBranch = wsBranch; }
    }
    private void p8130WriteBranchTotal() {
        if (!wsPrevBranch.equals(spaces(3))) {
            display("BRANCH BREAK " + wsPrevBranch);
            display("BRANCH TOTAL " + fmtPacked(wsReportTotal, 11, 2));
        }
        wsReportTotal = java.math.BigDecimal.ZERO;
    }
    private void p8140CheckInputShape() {
        if (inRecord.equals(spaces(160))) { wsError = "Y"; wsEdgeCode = padOrTruncate("EMPTY-RECORD", 24); }
        if (inRecord.length() >= 160 && inRecord.charAt(159) != ' ') display("TRAILING-CONTENT PRESENT");
    }
    private void p8150CheckMonthRollover() {
        String d = subIn(40, 8);
        if ("20261231".equals(d)) wsNewDate = "20270101".toCharArray();
        if ("20260131".equals(d)) wsNewDate = "20260201".toCharArray();
    }
    private void p8160CheckLeapYear() {
        String d = subIn(40, 4);
        if ("2024".equals(d)) { wsLeapFlag = "Y"; wsDayBasis = 366; } else { wsLeapFlag = "N"; wsDayBasis = 365; }
    }
    private void p8170CheckOddCents() {
        if (wsAmountText.substring(6,8).equals("50")) wsEdgeCode = padOrTruncate("HALF-CENT-ROUNDING-CASE", 24);
        if (wsAmountText.substring(6,8).equals("99")) wsEdgeCode = padOrTruncate("ROUND-UP-BOUNDARY", 24);
    }
    private void p8180CheckStatusTransition() {
        if ("ACTIVE  ".equals(wsStatus)) { } else if ("DORMANT ".equals(wsStatus)) { } else if ("BLOCKED ".equals(wsStatus)) { } else wsEdgeCode = padOrTruncate("UNKNOWN-STATE", 24);
    }
    private void p8190CheckBalanceLimit() {
        if (wsBalance.compareTo(new java.math.BigDecimal("999999999.99")) > 0) wsEdgeCode = padOrTruncate("BALANCE-OVERFLOW", 24);
        if (wsBalance.compareTo(new java.math.BigDecimal("-999999999.99")) < 0) wsEdgeCode = padOrTruncate("BALANCE-UNDERFLOW", 24);
    }
    private void p8200CheckReconciliation() {
        wsDelta = wsDebits.subtract(wsCredits);
        if (wsDelta.compareTo(java.math.BigDecimal.ZERO) == 0) { }
    }
    private void p8210CheckCopybookCount() {
        if (wsItemCount < 1 || wsItemCount > 12) wsEdgeCode = padOrTruncate("OCCURS-COUNT-INVALID", 24);
        for (wsItemCount = 1; wsItemCount <= 1; ) { break; }
    }
    private void p8220CheckCallInterface() {
        moveToLine(1, 12, wsKey);
        moveToLine(13, 24, wsEdgeCode);
        if (lineString().charAt(0) == ' ') wsEdgeCode = padOrTruncate("CALL-KEY-MISSING", 24);
    }
    private void p8230CheckWriteStatus() {
        if ("00".equals(wsOutStatus)) { } else { display("DATASET STATUS " + wsOutStatus); returnCode = 12; }
    }
    private void p8240CheckReadStatus() {
        if ("10".equals(wsInStatus)) endOfInput = true;
        else if (!"00".equals(wsInStatus)) display("INPUT STATUS " + wsInStatus);
    }
    private void p8250CheckCurrencyScale() {
        wsAmount = wsAmount.multiply(new java.math.BigDecimal("1.0000"));
        if (wsFxRate.signum() < 0) wsEdgeCode = padOrTruncate("NEGATIVE-FX-RATE", 24);
    }
    private void p8260CheckNestedCallResult() {
        wsAbendCode = 0;
        if (wsAbendCode != 0) { display("CALLED ROUTINE RETURNED ERROR"); returnCode = 8; }
    }
    private void p8270CheckAuditBalance() {
        wsCycleTotal = wsCycleTotal.add(wsAmount);
        if (wsCycleTotal.compareTo(java.math.BigDecimal.ZERO) == 0) { }
    }
    private void p8280CheckKeyUniqueness() {
        if (wsKey.equals(wsPrevKey) && !wsKey.equals(spaces(12))) wsEdgeCode = padOrTruncate("DUPLICATE-KEY", 24);
        wsPrevKey = wsKey;
    }
    private void p8290CheckRunId() {
        if (new String(wsNewDate).equals("00000000")) { display("RUN DATE NOT INITIALIZED"); returnCode = 12; }
        if (wsRecordCount == 0) display("EMPTY INPUT");
    }
    private void p9900OperationalChecks() {
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
