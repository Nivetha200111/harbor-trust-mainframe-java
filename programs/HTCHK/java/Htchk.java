public class Htchk {
    static String wsInStatus = "  ";
    static String wsOutStatus = "  ";
    static boolean endOfInput = false;
    static String wsError = "N";
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
    static char[] wsLineChars = spaces(200).toCharArray();
    static java.math.BigDecimal wsReportTotal = java.math.BigDecimal.ZERO;
    static long wsReadTime = 0;
    static long wsWriteTime = 0;
    static long wsAbendCode = 0;
    static String inRecord = spaces(160);
    static String outRecord = spaces(200);
    static int returnCode = 0;
    static java.util.List<String> inLines = new java.util.ArrayList<>();
    static int inIndex = 0;
    static java.io.BufferedReader inReader = null;
    static java.io.OutputStream outStream = null;
    static boolean isEbcdic = false;

    public static void main(String[] args) throws Exception {
        String clk = System.getenv("DIOCLE_CLOCK");
        if (clk != null && clk.length() == 0) System.out.print("");
        String coll = System.getenv("DIOCLE_COLLATION");
        isEbcdic = "EBCDIC".equals(coll);
        p0000Main();
        if (returnCode != 0) System.exit(returnCode);
    }

    static String spaces(int n) {
        StringBuilder sb = new StringBuilder(n);
        for (int i = 0; i < n; i++) sb.append(' ');
        return sb.toString();
    }
    static String padRight(String s, int n) {
        if (s == null) s = "";
        if (s.length() > n) return s.substring(0, n);
        if (s.length() == n) return s;
        StringBuilder sb = new StringBuilder(n);
        sb.append(s);
        for (int i = s.length(); i < n; i++) sb.append(' ');
        return sb.toString();
    }
    static void setSlice(char[] arr, int start1, int len, String val) {
        String v = padRight(val, len);
        for (int i = 0; i < len; i++) arr[start1 - 1 + i] = v.charAt(i);
    }
    static String slice(String s, int start1, int len) {
        String p = padRight(s, start1 - 1 + len);
        if (p.length() < start1 - 1 + len) p = padRight(p, start1 - 1 + len);
        return p.substring(start1 - 1, start1 - 1 + len);
    }
    static void display(String s) {
        System.out.print(s + "\n");
    }
    static String formatCount9(long v) {
        long m = v % 4294967296L;
        if (m < 0) m += 4294967296L;
        String s = Long.toString(m);
        while (s.length() < 10) s = "0" + s;
        if (s.length() > 10) s = s.substring(s.length() - 10);
        return s;
    }
    static String formatCount7(long v) {
        long m = v % 10000000L;
        if (m < 0) m += 10000000L;
        String s = Long.toString(m);
        while (s.length() < 7) s = "0" + s;
        return s;
    }
    static String formatTotal(java.math.BigDecimal v) {
        boolean neg = v.signum() < 0;
        java.math.BigDecimal a = v.abs().setScale(2, java.math.RoundingMode.DOWN);
        java.math.BigDecimal unscaled = a.movePointRight(2);
        String digits;
        try { digits = unscaled.toBigIntegerExact().toString(); } catch (Exception e) { digits = unscaled.toBigInteger().toString(); }
        while (digits.length() < 13) digits = "0" + digits;
        if (digits.length() > 13) digits = digits.substring(digits.length() - 13);
        String intPart = digits.substring(0, 11);
        String fracPart = digits.substring(11, 13);
        return (neg ? "-" : "+") + intPart + "." + fracPart;
    }
    static boolean isNumericX(String s) {
        if (s.length() == 0) return false;
        for (int i = 0; i < s.length(); i++) { char c = s.charAt(i); if (c < '0' || c > '9') return false; }
        return true;
    }
    static boolean isAlphabeticX(String s) {
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c == ' ') continue;
            if ((c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z')) continue;
            return false;
        }
        return true;
    }
    static int ebcdicByte(char c) {
        try {
            byte[] b = String.valueOf(c).getBytes(java.nio.charset.Charset.forName("Cp037"));
            if (b.length == 0) return (int) c;
            return b[0] & 0xFF;
        } catch (Exception e) { return (int) c; }
    }
    static int compareAlpha(String a, String b) {
        if (isEbcdic) {
            int n = Math.min(a.length(), b.length());
            for (int i = 0; i < n; i++) {
                int ea = ebcdicByte(a.charAt(i));
                int eb = ebcdicByte(b.charAt(i));
                if (ea != eb) return ea - eb;
            }
            return a.length() - b.length();
        } else {
            return a.compareTo(b);
        }
    }
    static long addCount9(long cur, long delta) { long m = (cur + delta) % 4294967296L; if (m < 0) m += 4294967296L; return m; }
    static long addCount7(long cur, long delta) { long m = (cur + delta) % 4294967296L; if (m < 0) m += 4294967296L; return m; }
    static boolean sizeError112(java.math.BigDecimal v) {
        java.math.BigDecimal lim = new java.math.BigDecimal("99999999999.99");
        return v.abs().compareTo(lim) > 0;
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
        try {
            if (inPath == null) throw new java.io.FileNotFoundException();
            inReader = new java.io.BufferedReader(new java.io.InputStreamReader(new java.io.FileInputStream(inPath), java.nio.charset.Charset.forName("ISO-8859-1")));
            String line;
            while ((line = inReader.readLine()) != null) inLines.add(line);
            try { inReader.close(); } catch (Exception e) {}
            inReader = null;
            inIndex = 0;
            wsInStatus = "00";
        } catch (Exception e) {
            wsInStatus = "35";
        }
        if (!wsInStatus.equals("00")) {
            display("OPEN INPUT FAILED " + wsInStatus);
            returnCode = 12;
            endOfInput = true;
        }
        try {
            if (outPath == null) throw new java.io.FileNotFoundException();
            outStream = new java.io.FileOutputStream(outPath, false);
            wsOutStatus = "00";
        } catch (Exception e) {
            wsOutStatus = "35";
        }
        if (!wsOutStatus.equals("00")) {
            display("OPEN OUTPUT FAILED " + wsOutStatus);
            returnCode = 12;
            endOfInput = true;
        }
        wsRecordCount = 0; wsAcceptCount = 0; wsRejectCount = 0;
        wsEdgeCode = spaces(24);
        p1100CheckRunControl();
        p1200InitializeTotals();
    }
    private static void p1100CheckRunControl() {
        int yy = 0;
        try { yy = Integer.parseInt(wsOldDate.substring(0, 2)); } catch (Exception e) {}
        if (yy > 80) wsCentury = "19"; else wsCentury = "20";
        wsCentury = padRight(wsCentury, 2).substring(0, 2);
        String nd = wsCentury + wsOldDate;
        wsNewDate = padRight(nd, 8).substring(0, 8);
    }
    private static void p1200InitializeTotals() {
        wsReportTotal = java.math.BigDecimal.ZERO;
        wsCycleTotal = java.math.BigDecimal.ZERO;
        wsDebits = java.math.BigDecimal.ZERO;
        wsCredits = java.math.BigDecimal.ZERO;
        wsDelta = java.math.BigDecimal.ZERO;
        wsPrevKey = spaces(12);
        wsPrevBranch = spaces(3);
    }
    private static void p2000ReadInput() {
        if (inIndex < inLines.size()) {
            String line = inLines.get(inIndex++);
            wsRecordCount = addCount9(wsRecordCount, 1);
            String rec = padRight(line, 160);
            if (rec.length() > 160) rec = rec.substring(0, 160);
            inRecord = rec;
            wsRawBase = inRecord.substring(0, 120);
            wsKey = inRecord.substring(0, 12);
            wsBranch = inRecord.substring(12, 15);
            wsAmountText = inRecord.substring(15, 23);
            wsShortAmount = padRight(inRecord.substring(23, 31), 6).substring(0, 6);
            wsStatus = inRecord.substring(23, 31);
            wsInStatus = "00";
        } else {
            endOfInput = true;
            wsInStatus = "10";
        }
        if (!wsInStatus.equals("00") && !wsInStatus.equals("10")) {
            display("READ FAILURE " + wsInStatus);
            wsError = "Y"; inputError = true;
            endOfInput = true;
        }
    }
    private static void p3000ValidateRecord() {
        wsError = "N"; inputError = false;
        if (wsKey.equals(spaces(12))) p3100BadKey();
        if (!isNumericX(wsAmountText)) p3200BadAmount();
        if (wsStatus.equals(spaces(8))) p3300BadStatus();
        if (!isAlphabeticX(wsBranch)) p3400BadBranch();
        if (wsRecordCount > 1 && compareAlpha(wsKey, wsPrevKey) < 0) wsEdgeCode = padRight("SORT-ORDER", 24);
        wsPrevKey = wsKey;
        p3500CheckMultiRecordType();
        p3600CheckDateBoundary();
        inputError = wsError.equals("Y");
    }
    private static void p3100BadKey() { wsError = "Y"; inputError = true; wsEdgeCode = padRight("MISSING-KEY", 24); }
    private static void p3200BadAmount() { wsError = "Y"; inputError = true; wsEdgeCode = padRight("INVALID-AMOUNT", 24); }
    private static void p3300BadStatus() { wsError = "Y"; inputError = true; wsEdgeCode = padRight("INVALID-STATUS", 24); }
    private static void p3400BadBranch() { wsError = "Y"; inputError = true; wsEdgeCode = padRight("INVALID-BRANCH", 24); }
    private static void p3500CheckMultiRecordType() {
        char c0 = inRecord.charAt(0);
        if (c0 != 'A' && c0 != 'T') wsEdgeCode = padRight("UNKNOWN-RECORD-TYPE", 24);
        if (inRecord.charAt(1) == 'D') wsItemCount = 2; else wsItemCount = 1;
    }
    private static void p3600CheckDateBoundary() {
        String d = inRecord.substring(39, 47);
        if (d.equals("20261231")) wsEdgeCode = padRight("YEAR-END-ROLLOVER", 24);
        if (d.equals("20260228")) wsEdgeCode = padRight("NON-LEAP-FEB-END", 24);
    }
    private static void p4000ProcessRecord() {
        wsArithError = 0;
        long amtInt = 0;
        try { amtInt = Long.parseLong(wsAmountText); } catch (Exception e) { amtInt = 0; }
        wsAmount = java.math.BigDecimal.valueOf(amtInt);
        wsBalance = wsAmount;
        wsPrincipal = wsAmount;
        java.math.BigDecimal nt = wsReportTotal.add(wsAmount);
        if (sizeError112(nt)) wsArithError = 1; else wsReportTotal = nt;
        wsRate = new java.math.BigDecimal("0.0525");
        wsFxRate = new java.math.BigDecimal("1.0825");
        wsCheckAmt = wsAmount;
        wsEntryAmt = wsAmount;
        wsFee = java.math.BigDecimal.ZERO;
        wsFeeWaived = String.valueOf(inRecord.charAt(119));
        wsInterest = java.math.BigDecimal.ZERO;
        if (wsCheckAmt.compareTo(wsBalance) > 0) wsReturnReason = "R01"; else wsReturnReason = "   ";
        if (!wsReturnReason.equals(spaces(3))) wsReturnCount = addCount7(wsReturnCount, 1);
        setSlice(wsLineChars, 1, 12, wsKey);
        setSlice(wsLineChars, 13, 3, wsBranch);
        setSlice(wsLineChars, 16, 24, wsEdgeCode);
        setSlice(wsLineChars, 40, 12, wsAmountText);
        setSlice(wsLineChars, 52, 8, wsStatus);
        wsAcceptCount = addCount9(wsAcceptCount, 1);
    }
    private static void p5000WriteResult() throws Exception {
        outRecord = new String(wsLineChars);
        String toWrite = rtrim(outRecord);
        try {
            outStream.write(toWrite.getBytes(java.nio.charset.Charset.forName("ISO-8859-1")));
            outStream.write('\n');
            wsOutStatus = "00";
        } catch (Exception e) { wsOutStatus = "34"; }
        if (!wsOutStatus.equals("00")) {
            display("WRITE FAILURE " + wsOutStatus);
            returnCode = 12;
            endOfInput = true;
        } else {
            wsWriteTime = (wsWriteTime + 1) % 100000000L;
        }
    }
    static String rtrim(String s) { int i = s.length(); while (i > 0 && s.charAt(i - 1) == ' ') i--; return s.substring(0, i); }
    private static void p6000RejectRecord() throws Exception {
        wsRejectCount = addCount9(wsRejectCount, 1);
        display("REJECT " + formatCount9(wsRecordCount) + " " + wsEdgeCode);
        for (int i = 0; i < wsLineChars.length; i++) wsLineChars[i] = ' ';
        setSlice(wsLineChars, 1, 6, "REJECT");
        setSlice(wsLineChars, 8, 12, wsKey);
        setSlice(wsLineChars, 24, 24, wsEdgeCode);
        outRecord = new String(wsLineChars);
        String toWrite = rtrim(outRecord);
        try { outStream.write(toWrite.getBytes(java.nio.charset.Charset.forName("ISO-8859-1"))); outStream.write('\n'); wsOutStatus = "00"; } catch (Exception e) { wsOutStatus = "34"; }
    }
    private static void p7000Finalize() throws Exception {
        p7100CheckControlTotals();
        p7200ReportRunCounts();
        try { if (outStream != null) outStream.close(); wsOutStatus = "00"; } catch (Exception e) {}
        wsInStatus = "00";
        try { wsOutStatus = "00"; } catch (Exception e) {}
        if (!wsInStatus.equals("00")) display("CLOSE IN " + wsInStatus);
        if (!wsOutStatus.equals("00")) display("CLOSE OUT " + wsOutStatus);
        if (wsRejectCount > 0) returnCode = 4;
    }
    private static void p7100CheckControlTotals() {
        long sum = (wsAcceptCount + wsRejectCount) % 4294967296L;
        if (sum != (wsRecordCount % 4294967296L)) { display("CONTROL COUNT MISMATCH"); returnCode = 12; }
        if (wsArithError != 0) { display("ARITHMETIC SIZE ERROR"); returnCode = 8; }
    }
    private static void p7200ReportRunCounts() {
        display("PROGRAM HTCHK COMPLETE");
        display("READ=" + formatCount9(wsRecordCount));
        display("ACCEPT=" + formatCount9(wsAcceptCount));
        display("REJECT=" + formatCount9(wsRejectCount));
        display("TOTAL=" + formatTotal(wsReportTotal));
        display("EDGE=" + wsEdgeCode);
    }
    private static void p8000RejectAllZeroAmounts() { if (wsAmount.compareTo(java.math.BigDecimal.ZERO) == 0 && wsStatus.equals("ACTIVE")) wsEdgeCode = padRight("ZERO-ACTIVE-AMOUNT", 24); }
    private static void p8010CheckNegativeBalance() { if (wsBalance.compareTo(java.math.BigDecimal.ZERO) < 0) wsEdgeCode = padRight("NEGATIVE-BALANCE", 24); }
    private static void p8020CheckLongKey() { if (wsKey.charAt(11) == ' ') {} else display("FULL-WIDTH-KEY " + wsKey); }
    private static void p8030CheckRoundedAmount() {
        java.math.BigDecimal r = wsAmount.setScale(0, java.math.RoundingMode.HALF_UP);
        if (sizeError112(r)) wsArithError = 1; else wsPacked = r;
        if (wsPacked.compareTo(java.math.BigDecimal.ZERO) == 0 && wsAmount.compareTo(java.math.BigDecimal.ZERO) != 0) wsEdgeCode = padRight("TRUNCATION-RISK", 24);
    }
    private static void p8040CheckShortMove() { wsShortAmount = padRight(wsAmountText, 6).substring(0, 6); if (!wsAmountText.substring(6, 8).equals("00")) wsEdgeCode = padRight("SHORT-PIC-TRUNCATION", 24); }
    private static void p8050CheckFeeOverlay() { char ov = wsRawBase.charAt(119); if (ov == 'Y') wsFeeWaived = "Y"; else wsFeeWaived = "N"; }
    private static void p8060CheckSignedZoned() { wsZoned = wsBalance; if (wsZoned.compareTo(java.math.BigDecimal.ZERO) < 0 && wsBalance.compareTo(java.math.BigDecimal.ZERO) >= 0) wsEdgeCode = padRight("SIGN-CONVERSION", 24); }
    private static void p8070CheckDateWindow() { if (wsOldDate.substring(0, 2).equals("00")) wsEdgeCode = padRight("Y2K-WINDOW-REVIEW", 24); if (wsOldDate.equals("991231")) wsEdgeCode = padRight("CENTURY-BOUNDARY", 24); }
    private static void p8080CheckRatePrecision() { if (wsFxRate.compareTo(new java.math.BigDecimal("1.0825")) != 0) wsEdgeCode = padRight("RATE-IS-FOUR-DECIMAL", 24); }
    private static void p8090CheckRecordType() { char c = inRecord.charAt(0); if (c == 'A' || c == 'T') {} else wsEdgeCode = padRight("BAD-RECORD-TYPE", 24); }
    private static void p8100CheckReturnCode() { if (!wsReturnReason.equals(spaces(3))) wsReturnCount = addCount7(wsReturnCount, 1); if (wsReturnCount > wsRecordCount) returnCode = 12; }
    private static void p8110CheckAccumulator() { java.math.BigDecimal nt = wsReportTotal.add(wsAmount); if (sizeError112(nt)) wsArithError = 1; else wsReportTotal = nt; if (wsReportTotal.signum() < 0) wsEdgeCode = padRight("NEGATIVE-CONTROL-TOTAL", 24); }
    private static void p8120CheckBranchBreak() { if (!wsBranch.equals(wsPrevBranch)) { p8130WriteBranchTotal(); wsPrevBranch = wsBranch; } }
    private static void p8130WriteBranchTotal() { if (!wsPrevBranch.equals(spaces(3))) { display("BRANCH BREAK " + wsPrevBranch); display("BRANCH TOTAL " + formatTotal(wsReportTotal)); } wsReportTotal = java.math.BigDecimal.ZERO; }
    private static void p8140CheckInputShape() { if (inRecord.equals(spaces(160))) { wsError = "Y"; inputError = true; wsEdgeCode = padRight("EMPTY-RECORD", 24); } if (inRecord.charAt(159) != ' ') display("TRAILING-CONTENT PRESENT"); }
    private static void p8150CheckMonthRollover() { String d = slice(inRecord, 40, 8); if (d.equals("20261231")) wsNewDate = "20270101"; if (d.equals("20260131")) wsNewDate = "20260201"; }
    private static void p8160CheckLeapYear() { String y = slice(inRecord, 40, 4); if (y.equals("2024")) { wsLeapFlag = "Y"; wsDayBasis = 366; } else { wsLeapFlag = "N"; wsDayBasis = 365; } }
    private static void p8170CheckOddCents() { String c = wsAmountText.substring(6, 8); if (c.equals("50")) wsEdgeCode = padRight("HALF-CENT-ROUNDING-CASE", 24); if (c.equals("99")) wsEdgeCode = padRight("ROUND-UP-BOUNDARY", 24); }
    private static void p8180CheckStatusTransition() { if (wsStatus.equals("ACTIVE") || wsStatus.equals("DORMANT") || wsStatus.equals("BLOCKED")) {} else wsEdgeCode = padRight("UNKNOWN-STATE", 24); }
    private static void p8190CheckBalanceLimit() { java.math.BigDecimal lim = new java.math.BigDecimal("999999999.99"); if (wsBalance.compareTo(lim) > 0) wsEdgeCode = padRight("BALANCE-OVERFLOW", 24); if (wsBalance.compareTo(lim.negate()) < 0) wsEdgeCode = padRight("BALANCE-UNDERFLOW", 24); }
    private static void p8200CheckReconciliation() { java.math.BigDecimal d = wsDebits.subtract(wsCredits); if (sizeError112(d)) wsArithError = 1; else wsDelta = d; if (wsDelta.compareTo(java.math.BigDecimal.ZERO) == 0) {} }
    private static void p8210CheckCopybookCount() { if (wsItemCount < 1 || wsItemCount > 12) wsEdgeCode = padRight("OCCURS-COUNT-INVALID", 24); for (wsItemCount = 1; wsItemCount <= 1; ) { if (wsItemCount > 1) break; wsItemCount++; } }
    private static void p8220CheckCallInterface() { setSlice(wsLineChars, 1, 12, wsKey); setSlice(wsLineChars, 13, 24, wsEdgeCode); if (wsLineChars[0] == ' ') wsEdgeCode = padRight("CALL-KEY-MISSING", 24); }
    private static void p8230CheckWriteStatus() { if (wsOutStatus.equals("00")) {} else { display("DATASET STATUS " + wsOutStatus); returnCode = 12; } }
    private static void p8240CheckReadStatus() { if (wsInStatus.equals("10")) endOfInput = true; else if (!wsInStatus.equals("00")) display("INPUT STATUS " + wsInStatus); }
    private static void p8250CheckCurrencyScale() { java.math.BigDecimal r = wsAmount.multiply(new java.math.BigDecimal("1.0000")); r = r.setScale(2, java.math.RoundingMode.HALF_UP); if (sizeError112(r)) wsArithError = 1; else wsAmount = r; if (wsFxRate.signum() < 0) wsEdgeCode = padRight("NEGATIVE-FX-RATE", 24); }
    private static void p8260CheckNestedCallResult() { wsAbendCode = 0; if (wsAbendCode != 0) { display("CALLED ROUTINE RETURNED ERROR"); returnCode = 8; } }
    private static void p8270CheckAuditBalance() { java.math.BigDecimal nt = wsCycleTotal.add(wsAmount); if (sizeError112(nt)) wsArithError = 1; else wsCycleTotal = nt; if (wsCycleTotal.compareTo(java.math.BigDecimal.ZERO) == 0) {} }
    private static void p8280CheckKeyUniqueness() { if (wsKey.equals(wsPrevKey) && !wsKey.equals(spaces(12))) wsEdgeCode = padRight("DUPLICATE-KEY", 24); wsPrevKey = wsKey; }
    private static void p8290CheckRunId() { if (wsNewDate.equals("00000000")) { display("RUN DATE NOT INITIALIZED"); returnCode = 12; } if (wsRecordCount == 0) display("EMPTY INPUT"); }
    private static void p9900OperationalChecks() {
        p8000RejectAllZeroAmounts(); p8010CheckNegativeBalance(); p8020CheckLongKey(); p8030CheckRoundedAmount(); p8040CheckShortMove(); p8050CheckFeeOverlay(); p8060CheckSignedZoned(); p8070CheckDateWindow(); p8080CheckRatePrecision(); p8090CheckRecordType(); p8100CheckReturnCode(); p8110CheckAccumulator(); p8120CheckBranchBreak(); p8130WriteBranchTotal(); p8140CheckInputShape(); p8150CheckMonthRollover(); p8160CheckLeapYear(); p8170CheckOddCents(); p8180CheckStatusTransition(); p8190CheckBalanceLimit(); p8200CheckReconciliation(); p8210CheckCopybookCount(); p8220CheckCallInterface(); p8230CheckWriteStatus(); p8240CheckReadStatus(); p8250CheckCurrencyScale(); p8260CheckNestedCallResult(); p8270CheckAuditBalance(); p8280CheckKeyUniqueness(); p8290CheckRunId();
    }
}
