import java.io.OutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

public class Htstm {
    private String wsInStatus = "  ";
    private String wsOutStatus = "  ";
    private boolean endOfInput = false;
    private boolean inputError = false;
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
    private BigDecimal wsAmount = BigDecimal.ZERO;
    private BigDecimal wsBalance = BigDecimal.ZERO;
    private BigDecimal wsPrincipal = BigDecimal.ZERO;
    private BigDecimal wsRate = BigDecimal.ZERO;
    private BigDecimal wsFxRate = BigDecimal.ZERO;
    private BigDecimal wsInterest = BigDecimal.ZERO;
    private BigDecimal wsFee = BigDecimal.ZERO;
    private String wsFeeWaived = "N";
    private BigDecimal wsCheckAmt = BigDecimal.ZERO;
    private String wsReturnReason = spaces(3);
    private long wsReturnCount = 0;
    private BigDecimal wsEntryAmt = BigDecimal.ZERO;
    private String wsPostDateStr = "00000000";
    private int wsCycleMonth = 12;
    private BigDecimal wsCycleTotal = BigDecimal.ZERO;
    private BigDecimal wsDebits = BigDecimal.ZERO;
    private BigDecimal wsCredits = BigDecimal.ZERO;
    private BigDecimal wsDelta = BigDecimal.ZERO;
    private long wsOutOfBalance = 0;
    private long wsDaysInactive = 0;
    private int wsDayBasis = 365;
    private String wsLeapFlag = "N";
    private String wsAmountText = spaces(8);
    private String wsShortAmount = spaces(6);
    private String wsRawBase = spaces(120);
    private int wsItemCount = 0;
    private String[] wsItems = new String[12];
    private BigDecimal wsPacked = BigDecimal.ZERO;
    private BigDecimal wsZonedDec = BigDecimal.ZERO;
    private String wsOldDate = "991231";
    private int wsCentury = 20;
    private String wsNewDate = spaces(8);
    private String wsLine = spaces(200);
    private BigDecimal wsReportTotal = BigDecimal.ZERO;
    private long wsReadTime = 0;
    private long wsWriteTime = 0;
    private long wsAbendCode = 0;
    private String inRecord = spaces(160);
    private String outRecord = spaces(200);
    private int returnCode = 0;
    private boolean ebcdicMode = false;
    private List<String> inputLines = new ArrayList<>();
    private int inputIdx = 0;
    private OutputStream outStream = null;
    private boolean inOpened = false;
    private boolean outOpened = false;

    public static void main(String[] args) throws Exception {
        Htstm p = new Htstm();
        p.run();
        System.out.flush();
        if (p.returnCode != 0) System.exit(p.returnCode);
    }

    private void run() throws Exception {
        String cl = System.getenv("DIOCLE_COLLATION");
        ebcdicMode = "EBCDIC".equals(cl);
        String ck = System.getenv("DIOCLE_CLOCK");
        if (ck != null && ck.length() == 0) ebcdicMode = ebcdicMode;
        p0000Main();
    }

    private static String spaces(int n) {
        StringBuilder sb = new StringBuilder(n);
        for (int i = 0; i < n; i++) sb.append(' ');
        return sb.toString();
    }

    private static String padRight(String s, int n) {
        if (s == null) s = "";
        if (s.length() > n) return s.substring(0, n);
        if (s.length() == n) return s;
        StringBuilder sb = new StringBuilder(n);
        sb.append(s);
        for (int i = s.length(); i < n; i++) sb.append(' ');
        return sb.toString();
    }

    private static String rtrim(String s) {
        int e = s.length();
        while (e > 0 && s.charAt(e - 1) == ' ') e--;
        return s.substring(0, e);
    }

    private static String slice(String s, int start1, int len) {
        return s.substring(start1 - 1, start1 - 1 + len);
    }

    private static String replaceSlice(String base, int start1, int len, String val) {
        String v = padRight(val, len);
        return base.substring(0, start1 - 1) + v + base.substring(start1 - 1 + len);
    }

    private static boolean isNumericText(String s) {
        if (s.length() == 0) return false;
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c < '0' || c > '9') return false;
        }
        return true;
    }

    private static boolean isAlphabeticText(String s) {
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c == ' ') continue;
            if ((c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z')) continue;
            return false;
        }
        return true;
    }

    private int compareAlpha(String a, String b) {
        if (ebcdicMode) {
            try {
                byte[] ba = a.getBytes("Cp037");
                byte[] bb = b.getBytes("Cp037");
                int n = Math.min(ba.length, bb.length);
                for (int i = 0; i < n; i++) {
                    int x = ba[i] & 0xFF;
                    int y = bb[i] & 0xFF;
                    if (x != y) return x - y;
                }
                return ba.length - bb.length;
            } catch (Exception e) {
            }
        }
        int n = Math.min(a.length(), b.length());
        for (int i = 0; i < n; i++) {
            int x = a.charAt(i) & 0xFF;
            int y = b.charAt(i) & 0xFF;
            if (x != y) return x - y;
        }
        return a.length() - b.length();
    }

    private static String fmtUnsigned(long v, int digits) {
        long t = v & 0xFFFFFFFFL;
        int w = 10;
        String s = Long.toString(t);
        while (s.length() < w) s = "0" + s;
        if (s.length() > w) s = s.substring(s.length() - w);
        return s;
    }

    private static String fmtSignedDecimal(BigDecimal v, int intDigits, int fracDigits) {
        boolean neg = v.signum() < 0;
        BigDecimal a = v.abs().setScale(fracDigits, RoundingMode.HALF_UP);
        String plain = a.toPlainString();
        int dot = plain.indexOf('.');
        String ip;
        String fp;
        if (dot >= 0) {
            ip = plain.substring(0, dot);
            fp = plain.substring(dot + 1);
        } else {
            ip = plain;
            fp = "";
        }
        while (fp.length() < fracDigits) fp += "0";
        if (fp.length() > fracDigits) fp = fp.substring(0, fracDigits);
        while (ip.length() < intDigits) ip = "0" + ip;
        if (ip.length() > intDigits) ip = ip.substring(ip.length() - intDigits);
        return (neg ? "-" : "+") + ip + "." + fp;
    }

    private static boolean overflows(BigDecimal v, int intDigits, int fracDigits) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < intDigits; i++) sb.append('9');
        sb.append('.');
        for (int i = 0; i < fracDigits; i++) sb.append('9');
        BigDecimal max = new BigDecimal(sb.toString());
        if (v.compareTo(max) > 0) return true;
        if (v.compareTo(max.negate()) < 0) return true;
        return false;
    }

    private void display(String s) {
        try {
            System.out.write((s + "\n").getBytes(StandardCharsets.ISO_8859_1));
        } catch (Exception e) {
        }
    }

    private void p0000Main() {
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

    private void p1000Initialize() {
        String inPath = System.getenv("DD_INPUT-FILE");
        String outPath = System.getenv("DD_OUTPUT-FILE");
        if (inPath == null || inPath.length() == 0) {
            wsInStatus = "35";
            display("OPEN INPUT FAILED " + wsInStatus);
            returnCode = 12;
            endOfInput = true;
        } else {
            try {
                Path p = Paths.get(inPath);
                if (!Files.exists(p)) {
                    wsInStatus = "35";
                    display("OPEN INPUT FAILED " + wsInStatus);
                    returnCode = 12;
                    endOfInput = true;
                } else {
                    byte[] data = Files.readAllBytes(p);
                    String content = new String(data, StandardCharsets.ISO_8859_1);
                    List<String> lines = new ArrayList<>();
                    if (data.length > 0) {
                        StringBuilder cur = new StringBuilder();
                        for (int i = 0; i < content.length(); i++) {
                            char c = content.charAt(i);
                            if (c == '\n') {
                                String ln = cur.toString();
                                if (ln.endsWith("\r")) ln = ln.substring(0, ln.length() - 1);
                                lines.add(ln);
                                cur.setLength(0);
                            } else {
                                cur.append(c);
                            }
                        }
                        if (cur.length() > 0) {
                            String ln = cur.toString();
                            if (ln.endsWith("\r")) ln = ln.substring(0, ln.length() - 1);
                            lines.add(ln);
                        }
                    }
                    inputLines = lines;
                    inputIdx = 0;
                    wsInStatus = "00";
                    inOpened = true;
                }
            } catch (Exception e) {
                wsInStatus = "35";
                display("OPEN INPUT FAILED " + wsInStatus);
                returnCode = 12;
                endOfInput = true;
            }
        }
        if (outPath == null || outPath.length() == 0) {
            wsOutStatus = "34";
            display("OPEN OUTPUT FAILED " + wsOutStatus);
            returnCode = 12;
            endOfInput = true;
        } else {
            try {
                Path p = Paths.get(outPath);
                Path parent = p.getParent();
                if (parent != null) Files.createDirectories(parent);
                outStream = Files.newOutputStream(p, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE);
                wsOutStatus = "00";
                outOpened = true;
            } catch (Exception e) {
                wsOutStatus = "34";
                display("OPEN OUTPUT FAILED " + wsOutStatus);
                returnCode = 12;
                endOfInput = true;
            }
        }
        wsRecordCount = 0;
        wsAcceptCount = 0;
        wsRejectCount = 0;
        wsEdgeCode = spaces(24);
        p1100CheckRunControl();
        p1200InitializeTotals();
    }

    private void p1100CheckRunControl() {
        int v = Integer.parseInt(wsOldDate.substring(0, 2));
        if (v > 80) wsCentury = 19;
        else wsCentury = 20;
        String cs = (wsCentury < 10 ? "0" + wsCentury : Integer.toString(wsCentury));
        wsNewDate = cs + wsOldDate;
    }

    private void p1200InitializeTotals() {
        wsReportTotal = BigDecimal.ZERO;
        wsCycleTotal = BigDecimal.ZERO;
        wsDebits = BigDecimal.ZERO;
        wsCredits = BigDecimal.ZERO;
        wsDelta = BigDecimal.ZERO;
        wsPrevKey = spaces(12);
        wsPrevBranch = spaces(3);
    }

    private void p2000ReadInput() {
        if (!inOpened) return;
        if (endOfInput) return;
        if (inputIdx >= inputLines.size()) {
            endOfInput = true;
            wsInStatus = "10";
            return;
        }
        String ln = inputLines.get(inputIdx++);
        String rec;
        if (ln.length() > 160) rec = ln.substring(0, 160);
        else if (ln.length() < 160) rec = padRight(ln, 160);
        else rec = ln;
        inRecord = rec;
        wsInStatus = "00";
        wsRecordCount = wsRecordCount + 1;
        wsRawBase = inRecord.substring(0, 120);
        wsKey = inRecord.substring(0, 12);
        wsBranch = inRecord.substring(12, 15);
        wsAmountText = inRecord.substring(15, 23);
        String s8 = inRecord.substring(23, 31);
        wsShortAmount = s8.substring(0, 6);
        wsStatus = s8;
        if (!wsInStatus.equals("00") && !wsInStatus.equals("10")) {
            display("READ FAILURE " + wsInStatus);
            inputError = true;
            endOfInput = true;
        }
    }

    private void p3000ValidateRecord() {
        inputError = false;
        if (wsKey.equals(spaces(12))) p3100BadKey();
        if (!isNumericText(wsAmountText)) p3200BadAmount();
        if (wsStatus.equals(spaces(8))) p3300BadStatus();
        if (!isAlphabeticText(wsBranch)) p3400BadBranch();
        if (wsRecordCount > 1 && compareAlpha(wsKey, wsPrevKey) < 0) {
            wsEdgeCode = padRight("SORT-ORDER", 24);
        }
        wsPrevKey = wsKey;
        p3500CheckMultiRecordType();
        p3600CheckDateBoundary();
    }

    private void p3100BadKey() {
        inputError = true;
        wsEdgeCode = padRight("MISSING-KEY", 24);
    }

    private void p3200BadAmount() {
        inputError = true;
        wsEdgeCode = padRight("INVALID-AMOUNT", 24);
    }

    private void p3300BadStatus() {
        inputError = true;
        wsEdgeCode = padRight("INVALID-STATUS", 24);
    }

    private void p3400BadBranch() {
        inputError = true;
        wsEdgeCode = padRight("INVALID-BRANCH", 24);
    }

    private void p3500CheckMultiRecordType() {
        String c1 = inRecord.substring(0, 1);
        if (!c1.equals("A") && !c1.equals("T")) {
            wsEdgeCode = padRight("UNKNOWN-RECORD-TYPE", 24);
        }
        String c2 = inRecord.substring(1, 2);
        if (c2.equals("D")) wsItemCount = 2;
        else wsItemCount = 1;
    }

    private void p3600CheckDateBoundary() {
        String d = inRecord.substring(39, 47);
        if (d.equals("20261231")) wsEdgeCode = padRight("YEAR-END-ROLLOVER", 24);
        if (d.equals("20260228")) wsEdgeCode = padRight("NON-LEAP-FEB-END", 24);
    }

    private void p4000ProcessRecord() {
        wsArithError = 0;
        BigDecimal amt = new BigDecimal(wsAmountText).setScale(2, RoundingMode.UNNECESSARY);
        wsAmount = amt;
        wsBalance = wsAmount;
        wsPrincipal = wsAmount;
        BigDecimal sum = wsReportTotal.add(wsAmount);
        if (overflows(sum, 11, 2)) wsArithError = 1;
        else wsReportTotal = sum;
        wsRate = new BigDecimal("0.0525");
        wsFxRate = new BigDecimal("1.0825");
        wsCheckAmt = wsAmount;
        wsEntryAmt = wsAmount;
        wsFee = BigDecimal.ZERO;
        wsFeeWaived = inRecord.substring(119, 120);
        wsInterest = BigDecimal.ZERO;
        String postPart = wsPostDateStr.substring(4, 6);
        String cyc = (wsCycleMonth < 10 ? "0" + wsCycleMonth : Integer.toString(wsCycleMonth));
        if (!postPart.equals(cyc)) wsEdgeCode = padRight("CYCLE-BOUNDARY", 24);
        BigDecimal sum2 = wsCycleTotal.add(wsEntryAmt);
        if (overflows(sum2, 11, 2)) wsArithError = 1;
        else wsCycleTotal = sum2;
        wsLine = replaceSlice(wsLine, 1, 12, wsKey);
        wsLine = replaceSlice(wsLine, 13, 3, wsBranch);
        wsLine = replaceSlice(wsLine, 16, 24, wsEdgeCode);
        wsLine = replaceSlice(wsLine, 40, 12, wsAmountText);
        wsLine = replaceSlice(wsLine, 52, 8, wsStatus);
        wsAcceptCount = wsAcceptCount + 1;
    }

    private void p5000WriteResult() {
        outRecord = wsLine;
        if (!outOpened) {
            display("WRITE FAILURE " + wsOutStatus);
            returnCode = 12;
            endOfInput = true;
            return;
        }
        try {
            String t = rtrim(outRecord);
            outStream.write(t.getBytes(StandardCharsets.ISO_8859_1));
            outStream.write('\n');
            wsOutStatus = "00";
            wsWriteTime = wsWriteTime + 1;
        } catch (Exception e) {
            display("WRITE FAILURE " + wsOutStatus);
            returnCode = 12;
            endOfInput = true;
        }
    }

    private void p6000RejectRecord() {
        wsRejectCount = wsRejectCount + 1;
        display("REJECT " + fmtUnsigned(wsRecordCount, 9) + " " + wsEdgeCode);
        wsLine = spaces(200);
        wsLine = replaceSlice(wsLine, 1, 6, "REJECT");
        wsLine = replaceSlice(wsLine, 8, 12, wsKey);
        wsLine = replaceSlice(wsLine, 24, 24, wsEdgeCode);
        outRecord = wsLine;
        try {
            if (outOpened) {
                String t = rtrim(outRecord);
                outStream.write(t.getBytes(StandardCharsets.ISO_8859_1));
                outStream.write('\n');
                wsOutStatus = "00";
            }
        } catch (Exception e) {
        }
    }

    private void p7000Finalize() {
        p7100CheckControlTotals();
        p7200ReportRunCounts();
        try {
            if (inOpened) { }
            if (outOpened && outStream != null) outStream.close();
            wsInStatus = inOpened ? "00" : wsInStatus;
            wsOutStatus = outOpened ? "00" : wsOutStatus;
        } catch (Exception e) {
        }
        if (!wsInStatus.equals("00")) display("CLOSE IN " + wsInStatus);
        if (!wsOutStatus.equals("00")) display("CLOSE OUT " + wsOutStatus);
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
        display("PROGRAM HTSTM COMPLETE");
        display("READ=" + fmtUnsigned(wsRecordCount, 9));
        display("ACCEPT=" + fmtUnsigned(wsAcceptCount, 9));
        display("REJECT=" + fmtUnsigned(wsRejectCount, 9));
        display("TOTAL=" + fmtSignedDecimal(wsReportTotal, 11, 2));
        display("EDGE=" + wsEdgeCode);
    }

    private void p8000RejectAllZeroAmounts() {
        if (wsAmount.compareTo(BigDecimal.ZERO) == 0 && wsStatus.equals(padRight("ACTIVE", 8))) {
            wsEdgeCode = padRight("ZERO-ACTIVE-AMOUNT", 24);
        }
    }

    private void p8010CheckNegativeBalance() {
        if (wsBalance.signum() < 0) wsEdgeCode = padRight("NEGATIVE-BALANCE", 24);
    }

    private void p8020CheckLongKey() {
        if (slice(wsKey, 12, 1).equals(" ")) { }
        else display("FULL-WIDTH-KEY " + wsKey);
    }

    private void p8030CheckRoundedAmount() {
        BigDecimal r = wsAmount.setScale(2, RoundingMode.HALF_UP);
        if (overflows(r, 9, 2)) wsArithError = 1;
        else wsPacked = r;
        if (wsPacked.compareTo(BigDecimal.ZERO) == 0 && wsAmount.compareTo(BigDecimal.ZERO) != 0) {
            wsEdgeCode = padRight("TRUNCATION-RISK", 24);
        }
    }

    private void p8040CheckShortMove() {
        wsShortAmount = padRight(wsAmountText, 6).substring(0, 6);
        if (!wsAmountText.substring(6, 8).equals("00")) wsEdgeCode = padRight("SHORT-PIC-TRUNCATION", 24);
    }

    private void p8050CheckFeeOverlay() {
        String ov = wsRawBase.substring(119, 120);
        if (ov.equals("Y")) wsFeeWaived = "Y";
        else wsFeeWaived = "N";
    }

    private void p8060CheckSignedZoned() {
        wsZonedDec = wsBalance;
        if (wsZonedDec.signum() < 0 && wsBalance.signum() >= 0) wsEdgeCode = padRight("SIGN-CONVERSION", 24);
    }

    private void p8070CheckDateWindow() {
        if (wsOldDate.substring(0, 2).equals("00")) wsEdgeCode = padRight("Y2K-WINDOW-REVIEW", 24);
        if (wsOldDate.equals("991231")) wsEdgeCode = padRight("CENTURY-BOUNDARY", 24);
    }

    private void p8080CheckRatePrecision() {
        if (wsFxRate.compareTo(new BigDecimal("1.0825")) != 0) wsEdgeCode = padRight("RATE-IS-FOUR-DECIMAL", 24);
    }

    private void p8090CheckRecordType() {
        String c = inRecord.substring(0, 1);
        if (c.equals("A")) { }
        else if (c.equals("T")) { }
        else wsEdgeCode = padRight("BAD-RECORD-TYPE", 24);
    }

    private void p8100CheckReturnCode() {
        if (!wsReturnReason.equals(spaces(3))) wsReturnCount = wsReturnCount + 1;
        if (wsReturnCount > wsRecordCount) returnCode = 12;
    }

    private void p8110CheckAccumulator() {
        BigDecimal s = wsReportTotal.add(wsAmount);
        if (overflows(s, 11, 2)) wsArithError = 1;
        else wsReportTotal = s;
        if (wsReportTotal.signum() < 0) wsEdgeCode = padRight("NEGATIVE-CONTROL-TOTAL", 24);
    }

    private void p8120CheckBranchBreak() {
        if (!wsBranch.equals(wsPrevBranch)) {
            p8130WriteBranchTotal();
            wsPrevBranch = wsBranch;
        }
    }

    private void p8130WriteBranchTotal() {
        if (!wsPrevBranch.equals(spaces(3))) {
            display("BRANCH BREAK " + wsPrevBranch);
            display("BRANCH TOTAL " + fmtSignedDecimal(wsReportTotal, 11, 2));
        }
        wsReportTotal = BigDecimal.ZERO;
    }

    private void p8140CheckInputShape() {
        if (inRecord.equals(spaces(160))) {
            inputError = true;
            wsEdgeCode = padRight("EMPTY-RECORD", 24);
        }
        if (!inRecord.substring(159, 160).equals(" ")) display("TRAILING-CONTENT PRESENT");
    }

    private void p8150CheckMonthRollover() {
        String d = inRecord.length() >= 47 ? inRecord.substring(39, 47) : spaces(8);
        if (d.equals("20261231")) wsNewDate = "20270101";
        if (d.equals("20260131")) wsNewDate = "20260201";
    }

    private void p8160CheckLeapYear() {
        String y = inRecord.length() >= 4 ? inRecord.substring(39, 43) : "";
        if (y.equals("2024")) {
            wsLeapFlag = "Y";
            wsDayBasis = 366;
        } else {
            wsLeapFlag = "N";
            wsDayBasis = 365;
        }
    }

    private void p8170CheckOddCents() {
        if (wsAmountText.substring(6, 8).equals("50")) wsEdgeCode = padRight("HALF-CENT-ROUNDING-CASE", 24);
        if (wsAmountText.substring(6, 8).equals("99")) wsEdgeCode = padRight("ROUND-UP-BOUNDARY", 24);
    }

    private void p8180CheckStatusTransition() {
        String s = rtrim(wsStatus);
        if (s.equals("ACTIVE")) { }
        else if (s.equals("DORMANT")) { }
        else if (s.equals("BLOCKED")) { }
        else wsEdgeCode = padRight("UNKNOWN-STATE", 24);
    }

    private void p8190CheckBalanceLimit() {
        if (wsBalance.compareTo(new BigDecimal("999999999.99")) > 0) wsEdgeCode = padRight("BALANCE-OVERFLOW", 24);
        if (wsBalance.compareTo(new BigDecimal("-999999999.99")) < 0) wsEdgeCode = padRight("BALANCE-UNDERFLOW", 24);
    }

    private void p8200CheckReconciliation() {
        BigDecimal d = wsDebits.subtract(wsCredits);
        if (overflows(d, 11, 2)) wsArithError = 1;
        else wsDelta = d;
        if (wsDelta.compareTo(BigDecimal.ZERO) == 0) { }
    }

    private void p8210CheckCopybookCount() {
        if (wsItemCount < 1 || wsItemCount > 12) wsEdgeCode = padRight("OCCURS-COUNT-INVALID", 24);
        for (wsItemCount = 1; wsItemCount <= 1; wsItemCount++) { }
    }

    private void p8220CheckCallInterface() {
        wsLine = replaceSlice(wsLine, 1, 12, wsKey);
        wsLine = replaceSlice(wsLine, 13, 24, wsEdgeCode);
        if (slice(wsLine, 1, 1).equals(" ")) wsEdgeCode = padRight("CALL-KEY-MISSING", 24);
    }

    private void p8230CheckWriteStatus() {
        if (wsOutStatus.equals("00")) { }
        else {
            display("DATASET STATUS " + wsOutStatus);
            returnCode = 12;
        }
    }

    private void p8240CheckReadStatus() {
        if (wsInStatus.equals("10")) endOfInput = true;
        else if (!wsInStatus.equals("00")) display("INPUT STATUS " + wsInStatus);
    }

    private void p8250CheckCurrencyScale() {
        BigDecimal r = wsAmount.multiply(new BigDecimal("1.0000"));
        r = r.setScale(2, RoundingMode.HALF_UP);
        if (overflows(r, 9, 2)) wsArithError = 1;
        else wsAmount = r;
        if (wsFxRate.signum() < 0) wsEdgeCode = padRight("NEGATIVE-FX-RATE", 24);
    }

    private void p8260CheckNestedCallResult() {
        wsAbendCode = 0;
        if (wsAbendCode != 0) {
            display("CALLED ROUTINE RETURNED ERROR");
            returnCode = 8;
        }
    }

    private void p8270CheckAuditBalance() {
        BigDecimal s = wsCycleTotal.add(wsAmount);
        if (overflows(s, 11, 2)) wsArithError = 1;
        else wsCycleTotal = s;
        if (wsCycleTotal.compareTo(BigDecimal.ZERO) == 0) { }
    }

    private void p8280CheckKeyUniqueness() {
        if (wsKey.equals(wsPrevKey) && !wsKey.equals(spaces(12))) wsEdgeCode = padRight("DUPLICATE-KEY", 24);
        wsPrevKey = wsKey;
    }

    private void p8290CheckRunId() {
        if (wsNewDate.equals("00000000")) {
            display("RUN DATE NOT INITIALIZED");
            returnCode = 12;
        }
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
