package com.bd2.seniorhomeapp.report.pdf;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;

public class SimplePdfDocument {
    private final List<String> lines = new ArrayList<>();

    public void addTitle(String value) {
        addBlankLine();
        lines.add("TITLE:" + normalize(value));
    }

    public void addHeading(String value) {
        addBlankLine();
        lines.add("HEADING:" + normalize(value));
    }

    public void addLine(String value) {
        splitLine(normalize(value), 96).forEach(lines::add);
    }

    public void addBullet(String value) {
        splitLine("- " + normalize(value), 96).forEach(lines::add);
    }

    public void addBlankLine() {
        if (!lines.isEmpty() && !lines.get(lines.size() - 1).isBlank()) {
            lines.add("");
        }
    }

    public byte[] toByteArray() {
        List<List<String>> pages = paginate(52);
        List<byte[]> objects = new ArrayList<>();
        objects.add(null);
        objects.add(null);
        objects.add("<< /Type /Font /Subtype /Type1 /BaseFont /Helvetica >>".getBytes(StandardCharsets.ISO_8859_1));

        StringBuilder kids = new StringBuilder("[");
        for (int i = 0; i < pages.size(); i++) {
            int pageObjectNumber = 4 + i * 2;
            int contentObjectNumber = pageObjectNumber + 1;
            kids.append(pageObjectNumber).append(" 0 R ");
            byte[] pageContent = buildPageContent(pages.get(i), i + 1, pages.size());
            objects.add(("<< /Type /Page /Parent 2 0 R /MediaBox [0 0 595 842] /Resources << /Font << /F1 3 0 R >> >> /Contents "
                    + contentObjectNumber + " 0 R >>").getBytes(StandardCharsets.ISO_8859_1));
            objects.add(("<< /Length " + pageContent.length + " >>\nstream\n"
                    + new String(pageContent, StandardCharsets.ISO_8859_1)
                    + "endstream").getBytes(StandardCharsets.ISO_8859_1));
        }
        kids.append("]");
        objects.set(0, "<< /Type /Catalog /Pages 2 0 R >>".getBytes(StandardCharsets.ISO_8859_1));
        objects.set(1, ("<< /Type /Pages /Kids " + kids + " /Count " + pages.size() + " >>")
                .getBytes(StandardCharsets.ISO_8859_1));

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        List<Integer> offsets = new ArrayList<>();
        write(out, "%PDF-1.4\n");

        for (int i = 0; i < objects.size(); i++) {
            offsets.add(out.size());
            write(out, (i + 1) + " 0 obj\n");
            out.writeBytes(objects.get(i));
            write(out, "\nendobj\n");
        }

        int xrefOffset = out.size();
        write(out, "xref\n");
        write(out, "0 " + (objects.size() + 1) + "\n");
        write(out, "0000000000 65535 f \n");
        for (Integer offset : offsets) {
            write(out, String.format("%010d 00000 n \n", offset));
        }
        write(out, "trailer\n");
        write(out, "<< /Size " + (objects.size() + 1) + " /Root 1 0 R >>\n");
        write(out, "startxref\n");
        write(out, xrefOffset + "\n");
        write(out, "%%EOF");

        return out.toByteArray();
    }

    private byte[] buildPageContent(List<String> pageLines, int pageNumber, int pageCount) {
        StringBuilder content = new StringBuilder();
        content.append("BT\n");
        content.append("/F1 10 Tf\n");
        content.append("50 790 Td\n");

        boolean firstLine = true;
        for (String line : pageLines) {
            if (!firstLine) {
                content.append("0 -14 Td\n");
            }
            firstLine = false;

            if (line.startsWith("TITLE:")) {
                content.append("/F1 16 Tf\n");
                content.append("(").append(escape(line.substring(6))).append(") Tj\n");
                content.append("/F1 10 Tf\n");
            } else if (line.startsWith("HEADING:")) {
                content.append("/F1 12 Tf\n");
                content.append("(").append(escape(line.substring(8))).append(") Tj\n");
                content.append("/F1 10 Tf\n");
            } else if (!line.isBlank()) {
                content.append("(").append(escape(line)).append(") Tj\n");
            }
        }
        content.append("0 -28 Td\n");
        content.append("/F1 9 Tf\n");
        content.append("(Strona ").append(pageNumber).append(" z ").append(pageCount).append(") Tj\n");
        content.append("ET\n");

        return content.toString().getBytes(StandardCharsets.ISO_8859_1);
    }

    private List<List<String>> paginate(int maxLinesPerPage) {
        List<List<String>> pages = new ArrayList<>();
        List<String> current = new ArrayList<>();
        for (String line : lines) {
            if (current.size() >= maxLinesPerPage) {
                pages.add(current);
                current = new ArrayList<>();
            }
            current.add(line);
        }
        if (pages.isEmpty() && current.isEmpty()) {
            current.add("");
        }
        if (!current.isEmpty()) {
            pages.add(current);
        }
        return pages;
    }

    private static List<String> splitLine(String value, int maxLength) {
        List<String> result = new ArrayList<>();
        String remaining = value;
        while (remaining.length() > maxLength) {
            int splitAt = remaining.lastIndexOf(' ', maxLength);
            if (splitAt < 1) {
                splitAt = maxLength;
            }
            result.add(remaining.substring(0, splitAt).trim());
            remaining = remaining.substring(splitAt).trim();
        }
        result.add(remaining);
        return result;
    }

    private static String normalize(String value) {
        if (value == null) {
            return "";
        }
        return Normalizer.normalize(value, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .replace("ł", "l")
                .replace("Ł", "L");
    }

    private static String escape(String value) {
        return value.replace("\\", "\\\\")
                .replace("(", "\\(")
                .replace(")", "\\)");
    }

    private static void write(ByteArrayOutputStream out, String value) {
        out.writeBytes(value.getBytes(StandardCharsets.ISO_8859_1));
    }
}
