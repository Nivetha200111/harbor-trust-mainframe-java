import java.io.BufferedWriter;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Htiacr {
    private String wsInStatus = "  ";
    private String wsOutStatus = "  ";
    private String wsEof = "N";
    private String wsError = "N";
    private long wsRecordCount = 0;
    private long wsAcceptCount = 0;
    private long wsRejectCount = 0;
    private int wsArithError = 0;
    private String wsEdgeCode = "                        ";
    private String wsKey = "            ";
    private String wsPrevKey = "            ";
    private String wsBranch = "   ";
    private String wsPrevBranch = "   ";
    private String wsStatus = "        ";
    private BigDecimal wsAmount = BigDecimal.ZERO.setScale(2);
    private BigDecimal wsBalance = BigDecimal.ZERO.setScale(2);
    private BigDecimal wsPrincipal = BigDecimal.ZERO.setScale(2);
    private BigDecimal wsRate = BigDecimal.ZERO.setScale(4);
    private BigDecimal wsFxRate = BigDecimal.ZERO.setScale(4);
    private BigDecimal wsInterest = BigDecimal.ZERO.setScale(2);
    private BigDecimal wsFee = BigDecimal.ZERO.setScale(2);
    private String wsFeeWaived = "N";
    private BigDecimal wsCheckAmt = BigDecimal.ZERO.setScale(2);
    private String wsReturnReason = "   ";
    private long wsReturnCount = 0;
    private BigDecimal wsEntryAmt = BigDecimal.ZERO.setScale(2);
    private BigDecimal wsCycleTotal = BigDecimal.ZERO.setScale(2);
    private BigDecimal wsDebits = BigDecimal.ZERO.setScale(2);
    private BigDecimal wsCredits = BigDecimal.ZERO.setScale(2);
    private BigDecimal wsDelta = BigDecimal.ZERO.setScale(2);
    private long wsOutOfBalance = 0;
    private int wsDaysInactive = 0;
    private int wsDayBasis = 365;
    private String wsLeapFlag = "N";
    private String wsAmountText = "        ";
    private String wsShortAmount = "      ";
    private String wsRawBase = padRight("", 120);
    private int wsItemCount = 0;
    private String[] wsItems = new String[12];
    private BigDecimal wsPacked = BigDecimal.ZERO.setScale(2);
    private String wsZoned = "+0000000.00";
    private String wsOldDate = "991231";
    private int wsCentury = 20;
    private String wsNewDate = "00000000";
    private String wsLine = padRight("", 200);
    private BigDecimal wsReportTotal = BigDecimal.ZERO.setScale(2);
    private long wsReadTime = 0;
    private long wsWriteTime = 0;
    private long wsAbendCode = 0;
    private int wsServiceError = 0;
    private BigDecimal wsServiceValue = BigDecimal.ZERO.setScale(2);
    private String wsServiceText = padRight("", 80);
    private String inRecord = padRight("", 160);
    private String outRecord = padRight("", 200);
    private int returnCode = 0;
    private boolean isEbcdic = false;
    private List<String> inLines = new ArrayList<>();
    private int inIdx = 0;
    private BufferedWriter outWriter = null;
    private boolean inOpen = false;
    private boolean outOpen = false;
    private String htdateWsCallName = "        ";
    private String htdateWsDateRaw = "000000";
    private String htdateWsDateFull = "00000000";
    private int htdateWsCentury = 20;
    private BigDecimal htdateWsTemp = BigDecimal.ZERO.setScale(2);
    private BigDecimal htdateWsRound = BigDecimal.ZERO.setScale(2);
    private long htdateWsCounter = 0;

    public static void main(String[] args) {
        Htiacr p = new Htiacr();
        try {
            p.run();
        } catch (Throwable t) {
            try { if (p.returnCode != 0) System.exit(p.returnCode); else System.exit(12); } catch (Throwable t2) { System.exit(12); }
        }
        if (p.returnCode != 0) System.exit(p.returnCode);
    }

    private void run() throws Exception {
        String clockEnv = System.getenv("DIOCLE_CLOCK");
        String coll = System.getenv("DIOCLE_COLLATION");
        isEbcdic = "EBCDIC".equals(coll);
        if (clockEnv != null && clockEnv.length() == 0) isEbcdic = isEbcdic;
        p0000Main();
    }

    private static String padRight(String s, int len) {
        if (s == null) s = "";
        if (s.length() > len) return s.substring(0, len);
        if (s.length() == len) return s;
        StringBuilder b = new StringBuilder(s);
        while (b.length() < len) b.append(' ');
        return b.toString();
    }

    private static String overlay(String base, int start1, int len, String src) {
        String s = src == null ? "" : src;
        String padded;
        if (s.length() > len) padded = s.substring(0, len);
        else if (s.length() < len) padded = s + " ".repeat(len - s.length());
        else padded = s;
        StringBuilder b = new StringBuilder(base);
        while (b.length() < start1 - 1 + len) b.append(' ');
        if (b.length() > 200 && base.length() == 200) b.setLength(200);
        for (int i = 0; i < len; i++) b.setCharAt(start1 - 1 + i, padded.charAt(i));
        return b.toString();
    }

    private boolean isEndOfInput() { return "Y".equals(wsEof); }
    private void setEndOfInput() { wsEof = "Y"; }
    private boolean isInputError() { return "Y".equals(wsError); }
    private void setInputError() { wsError = "Y"; }
    private void setEdge(String v) { wsEdgeCode = padRight(v, 24); }
    private String spaces(int n) { return " ".repeat(n); }

    private boolean isNumericText(String s) {
        if (s == null || s.length() == 0) return false;
        for (int i = 0; i < s.length(); i++) { char c = s.charAt(i); if (c < '0' || c > '9') return false; }
        return true;
    }

    private boolean isAlphabeticText(String s) {
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c == ' ') continue;
            if ((c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z')) continue;
            return false;
        }
        return true;
    }

    private int compareAlpha(String a, String b) {
        if (isEbcdic) {
            try {
                Charset cs = Charset.forName("Cp037");
                byte[] ba = a.getBytes(cs);
                byte[] bb = b.getBytes(cs);
                int n = Math.min(ba.length, bb.length);
                for (int i = 0; i < n; i++) {
                    int x = ba[i] & 0xFF; int y = bb[i] & 0xFF;
                    if (x != y) return x - y;
                }
                return ba.length - bb.length;
            } catch (Exception e) { }
        }
        int n = Math.min(a.length(), b.length());
        for (int i = 0; i < n; i++) { char x = a.charAt(i); char y = b.charAt(i); if (x != y) return x - y; }
        return a.length() - b.length();
    }

    private String fmtCount(long v, int digits) {
        long mod = 1;
        for (int i = 0; i < digits; i++) mod *= 10L;
        long t = ((v % mod) + mod) % mod;
        String s = Long.toString(t);
        while (s.length() < digits) s = "0" + s;
        return s;
    }

    private String fmtTotal(BigDecimal v) {
        boolean neg = v.signum() < 0;
        BigDecimal a = v.abs().setScale(2, RoundingMode.HALF_UP);
        BigDecimal intPart = a.setScale(0, RoundingMode.DOWN);
        int frac = a.remainder(BigDecimal.ONE).movePointRight(2).intValue();
        String si = intPart.toPlainString();
        while (si.length() < 11) si = "0" + si;
        if (si.length() > 11) si = si.substring(si.length() - 11);
        String sf = frac < 10 ? "0" + frac : Integer.toString(frac);
        return (neg ? "-" : "+") + si + "." + sf;
    }

    private void display(String s) { System.out.println(s); }

    private void abend(int code) {
        try { System.out.flush(); } catch (Throwable t) { }
        try { if (outWriter != null) { try { outWriter.flush(); outWriter.close(); } catch (Throwable t) { } } } catch (Throwable t) { }
        inOpen = false; outOpen = false;
        try { System.err.println("CEE3ABD abend code " + code); } catch (Throwable t) { }
        System.exit(16);
    }

    private void p0000Main() throws Exception {
        p1000Initialize();
        while (!isEndOfInput()) {
            p2000ReadInput();
            if (!isEndOfInput()) {
                p3000ValidateRecord();
                if (!isInputError()) {
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
        try {
            if (inPath == null) throw new java.io.IOException("no input");
            Path p = Paths.get(inPath);
            List<String> lines = Files.readAllLines(p, StandardCharsets.ISO_8859_1);
            inLines = lines;
            inIdx = 0;
            inOpen = true;
            wsInStatus = "00";
        } catch (Exception e) {
            wsInStatus = "35";
            inOpen = false;
            inLines = new ArrayList<>();
        }
        if (!"00".equals(wsInStatus)) {
            display("OPEN INPUT FAILED " + wsInStatus);
            returnCode = 12;
            setEndOfInput();
        }
        try {
            if (outPath == null) throw new java.io.IOException("no output");
            Path p = Paths.get(outPath);
            if (p.getParent() != null) try { Files.createDirectories(p.getParent()); } catch (Exception e) { }
            outWriter = Files.newBufferedWriter(p, StandardCharsets.ISO_8859_1,
                java.nio.file.StandardOpenOption.CREATE, java.nio.file.StandardOpenOption.TRUNCATE_EXISTING, java.nio.file.StandardOpenOption.WRITE);
            outOpen = true;
            wsOutStatus = "00";
        } catch (Exception e) {
            wsOutStatus = "35";
            outOpen = false;
        }
        if (!"00".equals(wsOutStatus)) {
            display("OPEN OUTPUT FAILED " + wsOutStatus);
            returnCode = 12;
            setEndOfInput();
        }
        wsRecordCount = 0; wsAcceptCount = 0; wsRejectCount = 0;
        wsEdgeCode = spaces(24);
        p1100CheckRunControl();
        p1200InitializeTotals();
    }

    private void p1100CheckRunControl() {
        int yy = 0;
        try { yy = Integer.parseInt(wsOldDate.substring(0, 2)); } catch (Exception e) { yy = 0; }
        if (yy > 80) wsCentury = 19; else wsCentury = 20;
        String cs = String.format("%02d", wsCentury);
        wsNewDate = overlay(wsNewDate, 1, 2, cs);
        wsNewDate = overlay(wsNewDate, 3, 6, wsOldDate);
    }

    private void p1200InitializeTotals() {
        wsReportTotal = BigDecimal.ZERO.setScale(2);
        wsCycleTotal = BigDecimal.ZERO.setScale(2);
        wsDebits = BigDecimal.ZERO.setScale(2);
        wsCredits = BigDecimal.ZERO.setScale(2);
        wsDelta = BigDecimal.ZERO.setScale(2);
        wsPrevKey = spaces(12);
        wsPrevBranch = spaces(3);
    }

    private void p2000ReadInput() {
        if (!inOpen) { setEndOfInput(); wsInStatus = "10"; return; }
        if (inIdx >= inLines.size()) {
            wsInStatus = "10";
            setEndOfInput();
            return;
        }
        String line = inLines.get(inIdx++);
        wsInStatus = "00";
        inRecord = padRight(line, 160);
        wsRecordCount = (wsRecordCount + 1) % 1000000000L;
        wsRawBase = inRecord.substring(0, 120);
        wsKey = inRecord.substring(0, 12);
        wsBranch = inRecord.substring(12, 15);
        wsAmountText = inRecord.substring(15, 23);
        String s8 = inRecord.substring(23, 31);
        if (s8.length() >= 6) wsShortAmount = s8.substring(0, 6); else wsShortAmount = padRight(s8, 6);
        wsStatus = s8;
        if (!"00".equals(wsInStatus) && !"10".equals(wsInStatus)) {
            display("READ FAILURE " + wsInStatus);
            setInputError();
            setEndOfInput();
        }
    }

    private void p3000ValidateRecord() {
        wsError = "N";
        if (wsKey.equals(spaces(12))) p3100BadKey();
        if (!isNumericText(wsAmountText)) p3200BadAmount();
        if (wsStatus.equals(spaces(8))) p3300BadStatus();
        if (!isAlphabeticText(wsBranch)) p3400BadBranch();
        if (wsRecordCount > 1 && compareAlpha(wsKey, wsPrevKey) < 0) setEdge("SORT-ORDER");
        wsPrevKey = wsKey;
        p3500CheckMultiRecordType();
        p3600CheckDateBoundary();
    }

    private void p3100BadKey() { setInputError(); setEdge("MISSING-KEY"); }
    private void p3200BadAmount() { setInputError(); setEdge("INVALID-AMOUNT"); }
    private void p3300BadStatus() { setInputError(); setEdge("INVALID-STATUS"); }
    private void p3400BadBranch() { setInputError(); setEdge("INVALID-BRANCH"); }

    private void p3500CheckMultiRecordType() {
        char c0 = inRecord.charAt(0);
        if (c0 != 'A' && c0 != 'T') setEdge("UNKNOWN-RECORD-TYPE");
        if (inRecord.charAt(1) == 'D') wsItemCount = 2; else wsItemCount = 1;
    }

    private void p3600CheckDateBoundary() {
        String d = inRecord.substring(39, 47);
        if ("20261231".equals(d)) setEdge("YEAR-END-ROLLOVER");
        if ("20260228".equals(d)) setEdge("NON-LEAP-FEB-END");
    }

    private void p4000ProcessRecord() {
        wsArithError = 0;
        long iv = 0;
        try { iv = Long.parseLong(wsAmountText); } catch (Exception e) { iv = 0; }
        wsAmount = BigDecimal.valueOf(iv).setScale(2);
        htdateCall();
        wsBalance = wsAmount;
        wsPrincipal = wsAmount;
        BigDecimal maxR = new BigDecimal("99999999999.99");
        BigDecimal minR = maxR.negate();
        BigDecimal sum = wsReportTotal.add(wsAmount);
        if (sum.compareTo(maxR) > 0 || sum.compareTo(minR) < 0) wsArithError = 1;
        else wsReportTotal = sum.setScale(2);
        wsRate = new BigDecimal("0.0525");
        wsFxRate = new BigDecimal("1.0825");
        wsCheckAmt = wsAmount;
        wsEntryAmt = wsAmount;
        wsFee = BigDecimal.ZERO.setScale(2);
        wsFeeWaived = inRecord.substring(119, 120);
        wsInterest = BigDecimal.ZERO.setScale(2);
        try {
            BigDecimal raw = wsPrincipal.multiply(wsRate).divide(new BigDecimal("36500"), 20, RoundingMode.HALF_UP);
            BigDecimal rnd = raw.setScale(2, RoundingMode.HALF_UP);
            BigDecimal maxI = new BigDecimal("999999999.99");
            BigDecimal minI = maxI.negate();
            if (rnd.compareTo(maxI) > 0 || rnd.compareTo(minI) < 0) wsArithError = 1;
            else wsInterest = rnd;
        } catch (Exception e) { wsArithError = 1; }
        if (wsInterest.compareTo(new BigDecimal("0.005")) == 0) setEdge("HALF-CENT");
        wsLine = overlay(wsLine, 1, 12, wsKey);
        wsLine = overlay(wsLine, 13, 3, wsBranch);
        wsLine = overlay(wsLine, 16, 24, wsEdgeCode);
        wsLine = overlay(wsLine, 40, 12, wsAmountText);
        wsLine = overlay(wsLine, 52, 8, wsStatus);
        wsAcceptCount = (wsAcceptCount + 1) % 1000000000L;
    }

    private void htdateCall() {
        String lkKey = wsKey;
        BigDecimal lkAmount = wsAmount;
        htdate0000Main(lkKey, lkAmount);
    }

    private void htdate0000Main(String lkKey, BigDecimal lkAmount) {
        wsServiceError = 0;
        wsServiceValue = BigDecimal.ZERO.setScale(2);
        wsServiceText = spaces(80);
        htdate1000ValidateInterface(lkKey, lkAmount);
        if (wsServiceError == 0) htdate2000ProcessRequest(lkKey, lkAmount);
        htdate3900ServiceControls(lkKey, lkAmount);
        htdate5000ServiceValidations(lkKey, lkAmount);
    }

    private void htdate1000ValidateInterface(String lkKey, BigDecimal lkAmount) {
        if (lkKey.equals(spaces(12))) wsServiceError = 4;
        if (lkAmount.signum() < 0) wsServiceError = 8;
    }

    private void htdate2000ProcessRequest(String lkKey, BigDecimal lkAmount) {
        htdateWsDateRaw = "991231";
        htdate3000DateWindow();
        try {
            long v = Long.parseLong(htdateWsDateFull);
            wsServiceValue = BigDecimal.valueOf(v).setScale(2);
        } catch (Exception e) { wsServiceValue = BigDecimal.ZERO.setScale(2); }
    }

    private void htdate3000DateWindow() {
        int yy = Integer.parseInt(htdateWsDateRaw.substring(0, 2));
        if (yy > 80) htdateWsCentury = 19; else htdateWsCentury = 20;
        String cs = String.format("%02d", htdateWsCentury);
        htdateWsDateFull = overlay(htdateWsDateFull, 3, 6, htdateWsDateRaw);
        htdateWsDateFull = overlay(htdateWsDateFull, 1, 2, cs);
        wsServiceText = overlay(wsServiceText, 1, 8, htdateWsDateFull);
    }

    private void htdate3900ServiceControls(String lkKey, BigDecimal lkAmount) {
        htdate4100CheckKeyLowValues(lkKey, lkAmount);
        htdate4110CheckAmountCap(lkKey, lkAmount);
        htdate4120CheckAmountFloor(lkKey, lkAmount);
        htdate4130CheckResultState(lkKey, lkAmount);
        htdate4140CheckServiceName(lkKey, lkAmount);
        htdate4150CheckDateCalendar(lkKey, lkAmount);
        htdate4160CheckDateDay(lkKey, lkAmount);
        htdate4170CheckDateRollover(lkKey, lkAmount);
        htdate4180CheckWindowLow(lkKey, lkAmount);
        htdate4190CheckWindowHigh(lkKey, lkAmount);
        htdate4200CheckRoundBoundary(lkKey, lkAmount);
        htdate4210CheckPackedDecimal(lkKey, lkAmount);
        htdate4220CheckResultConsistency(lkKey, lkAmount);
        htdate4230CheckDynamicName(lkKey, lkAmount);
        htdate4240CheckKeyCharacters(lkKey, lkAmount);
        htdate4250CheckCallCounter(lkKey, lkAmount);
        htdate4260CheckAuditLength(lkKey, lkAmount);
        htdate4270CheckCurrencyScale(lkKey, lkAmount);
        htdate4280CheckWaiverState(lkKey, lkAmount);
        htdate4290CheckRateValid(lkKey, lkAmount);
        htdate4300CheckReturnText(lkKey, lkAmount);
        htdate4310CheckReferenceKey(lkKey, lkAmount);
        htdate4320CheckContentIsolation(lkKey, lkAmount);
        htdate4330CheckResultInitialized(lkKey, lkAmount);
        htdate4340CheckDecimalRounding(lkKey, lkAmount);
        htdate4350CheckCenturyPolicy(lkKey, lkAmount);
        htdate4360CheckReturnCode(lkKey, lkAmount);
    }

    private void htdate4100CheckKeyLowValues(String k, BigDecimal a) {
        boolean low = true;
        for (int i = 0; i < k.length(); i++) if (k.charAt(i) != 0) { low = false; break; }
        if (low) wsServiceError = 4;
    }
    private void htdate4110CheckAmountCap(String k, BigDecimal a) { if (a.compareTo(new BigDecimal("999999999.99")) > 0) wsServiceError = 8; }
    private void htdate4120CheckAmountFloor(String k, BigDecimal a) { if (a.compareTo(new BigDecimal("-999999999.99")) < 0) wsServiceError = 8; }
    private void htdate4130CheckResultState(String k, BigDecimal a) { if (wsServiceError == 0) wsServiceText = overlay(wsServiceText, 1, 2, "OK"); }
    private void htdate4140CheckServiceName(String k, BigDecimal a) { if (htdateWsCallName.equals(spaces(8))) wsServiceText = padRight("SERVICE NAME UNSET", 80); }
    private void htdate4150CheckDateCalendar(String k, BigDecimal a) { try { if (Integer.parseInt(htdateWsDateFull.substring(4, 6)) > 12) wsServiceError = 8; } catch (Exception e) { } }
    private void htdate4160CheckDateDay(String k, BigDecimal a) { try { if (Integer.parseInt(htdateWsDateFull.substring(6, 8)) > 31) wsServiceError = 8; } catch (Exception e) { } }
    private void htdate4170CheckDateRollover(String k, BigDecimal a) { if ("123199".equals(htdateWsDateRaw)) wsServiceText = padRight("YEAR END", 80); }
    private void htdate4180CheckWindowLow(String k, BigDecimal a) { if (htdateWsDateRaw.startsWith("00")) htdateWsCentury = 20; }
    private void htdate4190CheckWindowHigh(String k, BigDecimal a) { try { if (Integer.parseInt(htdateWsDateRaw.substring(0, 2)) > 80) htdateWsCentury = 19; } catch (Exception e) { } }
    private void htdate4200CheckRoundBoundary(String k, BigDecimal a) {
        try { htdateWsRound = a.add(new BigDecimal("0.005")).setScale(2, RoundingMode.HALF_UP); }
        catch (Exception e) { wsServiceError = 8; }
    }
    private void htdate4210CheckPackedDecimal(String k, BigDecimal a) {
        try { htdateWsTemp = a.setScale(2, RoundingMode.HALF_UP); } catch (Exception e) { wsServiceError = 8; }
    }
    private void htdate4220CheckResultConsistency(String k, BigDecimal a) { if (wsServiceError != 0) wsServiceValue = BigDecimal.ZERO.setScale(2); }
    private void htdate4230CheckDynamicName(String k, BigDecimal a) { if (!"HTAUDT  ".equals(htdateWsCallName) && !spaces(8).equals(htdateWsCallName)) { } if (!"HTAUDT".equals(htdateWsCallName.trim()) && !htdateWsCallName.equals(spaces(8))) wsServiceError = 8; }
    private void htdate4240CheckKeyCharacters(String k, BigDecimal a) { if (k.length() < 3 || !k.substring(0, 3).equals("SYN")) wsServiceError = 4; }
    private void htdate4250CheckCallCounter(String k, BigDecimal a) { htdateWsCounter = (htdateWsCounter + 1) % 1000000000L; }
    private void htdate4260CheckAuditLength(String k, BigDecimal a) { if (wsServiceText.equals(spaces(80))) wsServiceText = padRight("NO AUDIT DETAIL", 80); }
    private void htdate4270CheckCurrencyScale(String k, BigDecimal a) { if (htdateWsTemp.signum() < 0) wsServiceError = 8; }
    private void htdate4280CheckWaiverState(String k, BigDecimal a) { if (k.length() >= 12 && k.charAt(11) == 'Y') wsServiceValue = BigDecimal.ZERO.setScale(2); }
    private void htdate4290CheckRateValid(String k, BigDecimal a) { if (htdateWsTemp.compareTo(new BigDecimal("999.9999")) > 0) wsServiceError = 8; }
    private void htdate4300CheckReturnText(String k, BigDecimal a) { if (wsServiceError != 0) wsServiceText = padRight("CALL FAILED", 80); }
    private void htdate4310CheckReferenceKey(String k, BigDecimal a) { }
    private void htdate4320CheckContentIsolation(String k, BigDecimal a) { htdateWsTemp = a.setScale(2); }
    private void htdate4330CheckResultInitialized(String k, BigDecimal a) { }
    private void htdate4340CheckDecimalRounding(String k, BigDecimal a) { if (a.compareTo(new BigDecimal("0.005")) == 0) wsServiceText = padRight("HALF CENT", 80); }
    private void htdate4350CheckCenturyPolicy(String k, BigDecimal a) { if (htdateWsCentury < 19 || htdateWsCentury > 20) wsServiceError = 8; }
    private void htdate4360CheckReturnCode(String k, BigDecimal a) { if (wsServiceError > 8) wsServiceError = 8; }

    private void htdate5000ServiceValidations(String lkKey, BigDecimal lkAmount) {
        htdate5100CheckContentKey(lkKey, lkAmount);
        htdate5110CheckLowValues(lkKey, lkAmount);
        htdate5120CheckDecimalUpper(lkKey, lkAmount);
        htdate5130CheckDecimalLower(lkKey, lkAmount);
        htdate5140CheckHalfCent(lkKey, lkAmount);
        htdate5150CheckRounding(lkKey, lkAmount);
        htdate5160CheckResultReset(lkKey, lkAmount);
        htdate5170CheckResultMessage(lkKey, lkAmount);
        htdate5180CheckWindowBegin(lkKey, lkAmount);
        htdate5190CheckWindowEnd(lkKey, lkAmount);
        htdate5200CheckCalendarMonth(lkKey, lkAmount);
        htdate5210CheckCalendarDay(lkKey, lkAmount);
        htdate5220CheckRateFourPlaces(lkKey, lkAmount);
        htdate5230CheckRateNonzero(lkKey, lkAmount);
        htdate5240CheckFeeWaiverState(lkKey, lkAmount);
        htdate5250CheckAuditKey(lkKey, lkAmount);
        htdate5260CheckCallCounter(lkKey, lkAmount);
        htdate5270CheckValueSign(lkKey, lkAmount);
        htdate5280CheckDateRollover(lkKey, lkAmount);
        htdate5290CheckLeapDay(lkKey, lkAmount);
        htdate5300CheckContentCopy(lkKey, lkAmount);
        htdate5310CheckReferenceReturn(lkKey, lkAmount);
        htdate5320CheckDynamicCallName(lkKey, lkAmount);
        htdate5330CheckReturnText(lkKey, lkAmount);
        for (int i = 0; i < 17; i++) htdate54xxCheckServicePostcondition(lkKey, lkAmount);
    }
    private void htdate5100CheckContentKey(String k, BigDecimal a) { if (!k.substring(0,3).equals("SYN")) wsServiceError = 4; }
    private void htdate5110CheckLowValues(String k, BigDecimal a) { boolean low=true; for(int i=0;i<k.length();i++) if(k.charAt(i)!=0){low=false;break;} if(low) wsServiceError=4; }
    private void htdate5120CheckDecimalUpper(String k, BigDecimal a) { if(a.compareTo(new BigDecimal("999999999.99"))>0) wsServiceError=8; }
    private void htdate5130CheckDecimalLower(String k, BigDecimal a) { if(a.compareTo(new BigDecimal("-999999999.99"))<0) wsServiceError=8; }
    private void htdate5140CheckHalfCent(String k, BigDecimal a) { if(a.compareTo(new BigDecimal("0.005"))==0) wsServiceText=padRight("HALF CENT ROUNDING",80); }
    private void htdate5150CheckRounding(String k, BigDecimal a) { try{htdateWsRound=a.setScale(2,RoundingMode.HALF_UP);}catch(Exception e){wsServiceError=8;} }
    private void htdate5160CheckResultReset(String k, BigDecimal a) { if(wsServiceError!=0) wsServiceValue=BigDecimal.ZERO.setScale(2); }
    private void htdate5170CheckResultMessage(String k, BigDecimal a) { if(wsServiceError!=0) wsServiceText=padRight("SERVICE ERROR",80); }
    private void htdate5180CheckWindowBegin(String k, BigDecimal a) { try{if(Integer.parseInt(htdateWsDateRaw.substring(0,2))<=80) htdateWsCentury=20;}catch(Exception e){} }
    private void htdate5190CheckWindowEnd(String k, BigDecimal a) { try{if(Integer.parseInt(htdateWsDateRaw.substring(0,2))>80) htdateWsCentury=19;}catch(Exception e){} }
    private void htdate5200CheckCalendarMonth(String k, BigDecimal a) { try{if(Integer.parseInt(htdateWsDateFull.substring(4,6))>12) wsServiceError=8;}catch(Exception e){} }
    private void htdate5210CheckCalendarDay(String k, BigDecimal a) { try{if(Integer.parseInt(htdateWsDateFull.substring(6,8))>31) wsServiceError=8;}catch(Exception e){} }
    private void htdate5220CheckRateFourPlaces(String k, BigDecimal a) { htdateWsTemp=new BigDecimal("1.0825"); if(htdateWsTemp.compareTo(new BigDecimal("1.0825"))!=0) wsServiceError=8; }
    private void htdate5230CheckRateNonzero(String k, BigDecimal a) { if(htdateWsTemp.signum()==0) wsServiceError=8; }
    private void htdate5240CheckFeeWaiverState(String k, BigDecimal a) { if(k.charAt(11)=='Y') wsServiceValue=BigDecimal.ZERO.setScale(2); }
    private void htdate5250CheckAuditKey(String k, BigDecimal a) { if(k.equals(spaces(12))) wsServiceText=padRight("AUDIT KEY MISSING",80); }
    private void htdate5260CheckCallCounter(String k, BigDecimal a) { htdateWsCounter=(htdateWsCounter+1)%1000000000L; }
    private void htdate5270CheckValueSign(String k, BigDecimal a) { if(wsServiceValue.signum()<0) wsServiceText=padRight("NEGATIVE RESULT REVIEW",80); }
    private void htdate5280CheckDateRollover(String k, BigDecimal a) { if("991231".equals(htdateWsDateRaw)) wsServiceText=padRight("YEAR END ROLLOVER",80); }
    private void htdate5290CheckLeapDay(String k, BigDecimal a) { if("20240229".equals(htdateWsDateFull)) wsServiceText=padRight("LEAP DAY",80); }
    private void htdate5300CheckContentCopy(String k, BigDecimal a) { htdateWsTemp=a.setScale(2); }
    private void htdate5310CheckReferenceReturn(String k, BigDecimal a) { if(wsServiceError>8) wsServiceError=8; }
    private void htdate5320CheckDynamicCallName(String k, BigDecimal a) { if(!htdateWsCallName.equals(spaces(8)) && !htdateWsCallName.trim().equals("HTAUDT")) wsServiceError=8; }
    private void htdate5330CheckReturnText(String k, BigDecimal a) { if(wsServiceText.equals(spaces(80)) && wsServiceError==0) wsServiceText=overlay(wsServiceText,1,2,"OK"); }
    private void htdate54xxCheckServicePostcondition(String k, BigDecimal a) { if(wsServiceError==0){ if(k.substring(0,3).equals("SYN")){} else wsServiceError=4; } }

    private void p5000WriteResult() throws Exception {
        outRecord = wsLine;
        try {
            String rec = outRecord;
            int end = rec.length();
            while (end > 0 && rec.charAt(end - 1) == ' ') end--;
            String toWrite = rec.substring(0, end);
            outWriter.write(toWrite + "\n");
            wsOutStatus = "00";
        } catch (Exception e) { wsOutStatus = "30"; }
        if (!"00".equals(wsOutStatus)) {
            display("WRITE FAILURE " + wsOutStatus);
            returnCode = 12;
            setEndOfInput();
        } else {
            wsWriteTime = (wsWriteTime + 1) % 100000000L;
        }
    }

    private void p6000RejectRecord() throws Exception {
        wsRejectCount = (wsRejectCount + 1) % 1000000000L;
        display("REJECT " + fmtCount(wsRecordCount, 10) + " " + wsEdgeCode);
        wsLine = spaces(200);
        wsLine = overlay(wsLine, 1, 6, "REJECT");
        wsLine = overlay(wsLine, 8, 12, wsKey);
        wsLine = overlay(wsLine, 24, 24, wsEdgeCode);
        outRecord = wsLine;
        try {
            String rec = outRecord;
            int end = rec.length();
            while (end > 0 && rec.charAt(end - 1) == ' ') end--;
            outWriter.write(rec.substring(0, end) + "\n");
            wsOutStatus = "00";
        } catch (Exception e) { wsOutStatus = "30"; }
    }

    private void p7000Finalize() throws Exception {
        p7100CheckControlTotals();
        p7200ReportRunCounts();
        try { if (inOpen) { inOpen = false; wsInStatus = "00"; } } catch (Throwable t) { wsInStatus = "30"; }
        try { if (outWriter != null && outOpen) { outWriter.flush(); outWriter.close(); outOpen = false; wsOutStatus = "00"; } } catch (Throwable t) { wsOutStatus = "30"; }
        if (!"00".equals(wsInStatus)) display("CLOSE IN " + wsInStatus);
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
        display("PROGRAM HTIACR COMPLETE");
        display("READ=" + fmtCount(wsRecordCount, 10));
        display("ACCEPT=" + fmtCount(wsAcceptCount, 10));
        display("REJECT=" + fmtCount(wsRejectCount, 10));
        display("TOTAL=" + fmtTotal(wsReportTotal));
        display("EDGE=" + wsEdgeCode);
    }

    private void p8000RejectAllZeroAmounts() { if (wsAmount.signum() == 0 && wsStatus.equals(padRight("ACTIVE", 8))) setEdge("ZERO-ACTIVE-AMOUNT"); }
    private void p8010CheckNegativeBalance() { if (wsBalance.signum() < 0) setEdge("NEGATIVE-BALANCE"); }
    private void p8020CheckLongKey() { if (wsKey.charAt(11) == ' ') { } else display("FULL-WIDTH-KEY " + wsKey); }
    private void p8030CheckRoundedAmount() {
        try {
            BigDecimal r = wsAmount.setScale(2, RoundingMode.HALF_UP);
            wsPacked = r;
            if (wsPacked.signum() == 0 && wsAmount.signum() != 0) setEdge("TRUNCATION-RISK");
        } catch (Exception e) { wsArithError = 1; }
    }
    private void p8040CheckShortMove() {
        String t = padRight(wsAmountText, 6).substring(0, 6);
        wsShortAmount = t;
        if (!wsAmountText.substring(6, 8).equals("00")) setEdge("SHORT-PIC-TRUNCATION");
    }
    private void p8050CheckFeeOverlay() {
        char ov = wsRawBase.length() >= 120 ? wsRawBase.charAt(119) : ' ';
        if (ov == 'Y') wsFeeWaived = "Y"; else wsFeeWaived = "N";
    }
    private void p8060CheckSignedZoned() { }
    private void p8070CheckDateWindow() {
        if (wsOldDate.startsWith("00")) setEdge("Y2K-WINDOW-REVIEW");
        if ("991231".equals(wsOldDate)) setEdge("CENTURY-BOUNDARY");
    }
    private void p8080CheckRatePrecision() { if (wsFxRate.compareTo(new BigDecimal("1.0825")) != 0) setEdge("RATE-IS-FOUR-DECIMAL"); }
    private void p8090CheckRecordType() {
        char c = inRecord.charAt(0);
        if (c == 'A' || c == 'T') { } else setEdge("BAD-RECORD-TYPE");
    }
    private void p8100CheckReturnCode() {
        if (!wsReturnReason.equals(spaces(3))) wsReturnCount++;
        if (wsReturnCount > wsRecordCount) returnCode = 12;
    }
    private void p8110CheckAccumulator() {
        BigDecimal max = new BigDecimal("99999999999.99");
        BigDecimal sum = wsReportTotal.add(wsAmount);
        if (sum.compareTo(max) > 0 || sum.compareTo(max.negate()) < 0) wsArithError = 1;
        else wsReportTotal = sum.setScale(2);
        if (wsReportTotal.signum() < 0) setEdge("NEGATIVE-CONTROL-TOTAL");
    }
    private void p8120CheckBranchBreak() {
        if (!wsBranch.equals(wsPrevBranch)) { p8130WriteBranchTotal(); wsPrevBranch = wsBranch; }
    }
    private void p8130WriteBranchTotal() {
        if (!wsPrevBranch.equals(spaces(3))) {
            display("BRANCH BREAK " + wsPrevBranch);
            display("BRANCH TOTAL " + fmtTotal(wsReportTotal));
        }
        wsReportTotal = BigDecimal.ZERO.setScale(2);
    }
    private void p8140CheckInputShape() {
        if (inRecord.equals(spaces(160))) { setInputError(); setEdge("EMPTY-RECORD"); }
        if (inRecord.charAt(159) != ' ') display("TRAILING-CONTENT PRESENT");
    }
    private void p8150CheckMonthRollover() {
        String d = inRecord.length() >= 47 ? inRecord.substring(39, 47) : "";
        if ("20261231".equals(d)) wsNewDate = "20270101";
        if ("20260131".equals(d)) wsNewDate = "20260201";
    }
    private void p8160CheckLeapYear() {
        String y = inRecord.length() >= 43 ? inRecord.substring(39, 43) : "";
        if ("2024".equals(y)) { wsLeapFlag = "Y"; wsDayBasis = 366; } else { wsLeapFlag = "N"; wsDayBasis = 365; }
    }
    private void p8170CheckOddCents() {
        if (wsAmountText.substring(6, 8).equals("50")) setEdge("HALF-CENT-ROUNDING-CASE");
        if (wsAmountText.substring(6, 8).equals("99")) setEdge("ROUND-UP-BOUNDARY");
    }
    private void p8180CheckStatusTransition() {
        if ("ACTIVE  ".equals(wsStatus) || "DORMANT ".equals(wsStatus) || "BLOCKED ".equals(wsStatus)) { }
        else setEdge("UNKNOWN-STATE");
    }
    private void p8190CheckBalanceLimit() {
        if (wsBalance.compareTo(new BigDecimal("999999999.99")) > 0) setEdge("BALANCE-OVERFLOW");
        if (wsBalance.compareTo(new BigDecimal("-999999999.99")) < 0) setEdge("BALANCE-UNDERFLOW");
    }
    private void p8200CheckReconciliation() {
        try { wsDelta = wsDebits.subtract(wsCredits).setScale(2); } catch (Exception e) { wsArithError = 1; }
    }
    private void p8210CheckCopybookCount() {
        if (wsItemCount < 1 || wsItemCount > 12) setEdge("OCCURS-COUNT-INVALID");
        for (int i = 1; i <= 1; i++) { }
    }
    private void p8220CheckCallInterface() {
        wsLine = overlay(wsLine, 1, 12, wsKey);
        wsLine = overlay(wsLine, 13, 24, wsEdgeCode);
        if (wsLine.charAt(0) == ' ') setEdge("CALL-KEY-MISSING");
    }
    private void p8230CheckWriteStatus() {
        if ("00".equals(wsOutStatus)) { } else { display("DATASET STATUS " + wsOutStatus); returnCode = 12; }
    }
    private void p8240CheckReadStatus() {
        if ("10".equals(wsInStatus)) setEndOfInput();
        else if (!"00".equals(wsInStatus)) display("INPUT STATUS " + wsInStatus);
    }
    private void p8250CheckCurrencyScale() {
        try { wsAmount = wsAmount.multiply(new BigDecimal("1.0000")).setScale(2, RoundingMode.HALF_UP); } catch (Exception e) { wsArithError = 1; }
        if (wsFxRate.signum() < 0) setEdge("NEGATIVE-FX-RATE");
    }
    private void p8260CheckNestedCallResult() {
        wsAbendCode = 0;
        if (wsAbendCode != 0) { display("CALLED ROUTINE RETURNED ERROR"); returnCode = 8; }
    }
    private void p8270CheckAuditBalance() {
        BigDecimal max = new BigDecimal("99999999999.99");
        BigDecimal sum = wsCycleTotal.add(wsAmount);
        if (sum.compareTo(max) > 0 || sum.compareTo(max.negate()) < 0) wsArithError = 1;
        else wsCycleTotal = sum.setScale(2);
    }
    private void p8280CheckKeyUniqueness() {
        if (wsKey.equals(wsPrevKey) && !wsKey.equals(spaces(12))) setEdge("DUPLICATE-KEY");
        wsPrevKey = wsKey;
    }
    private void p8290CheckRunId() {
        if ("00000000".equals(wsNewDate)) { display("RUN DATE NOT INITIALIZED"); returnCode = 12; }
        if (wsRecordCount == 0) display("EMPTY INPUT");
    }
    private void p9900OperationalChecks() {
        p8000RejectAllZeroAmounts(); p8010CheckNegativeBalance(); p8020CheckLongKey();
        p8030CheckRoundedAmount(); p8040CheckShortMove(); p8050CheckFeeOverlay();
        p8060CheckSignedZoned(); p8070CheckDateWindow(); p8080CheckRatePrecision();
        p8090CheckRecordType(); p8100CheckReturnCode(); p8110CheckAccumulator();
        p8120CheckBranchBreak(); p8130WriteBranchTotal(); p8140CheckInputShape();
        p8150CheckMonthRollover(); p8160CheckLeapYear(); p8170CheckOddCents();
        p8180CheckStatusTransition(); p8190CheckBalanceLimit(); p8200CheckReconciliation();
        p8210CheckCopybookCount(); p8220CheckCallInterface(); p8230CheckWriteStatus();
        p8240CheckReadStatus(); p8250CheckCurrencyScale(); p8260CheckNestedCallResult();
        p8270CheckAuditBalance(); p8280CheckKeyUniqueness(); p8290CheckRunId();
    }
}
