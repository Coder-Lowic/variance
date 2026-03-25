package com.lowic.ai.service;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.pdf.PagePdfDocumentReader;
import org.springframework.ai.reader.pdf.config.PdfDocumentReaderConfig;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Base64;
import java.util.List;

@Service
public class DocumentParserService {

    public String parseDocument(MultipartFile file) throws IOException {
        String filename = file.getOriginalFilename();
        if (filename == null) {
            throw new IllegalArgumentException("文件名不能为空");
        }

        String extension = getFileExtension(filename).toLowerCase();

        return switch (extension) {
            case "pdf" -> parsePdf(file);
            case "docx" -> parseDocx(file);
            case "xlsx", "xls" -> parseExcel(file);
            case "txt" -> parseTxt(file);
            case "md" -> parseMarkdown(file);
            case "jpg", "jpeg", "png", "gif", "bmp", "webp" -> parseImage(file);
            default -> throw new IllegalArgumentException("不支持的文件格式: " + extension);
        };
    }

    private String parsePdf(MultipartFile file) throws IOException {
        Resource resource = new InputStreamResource(file.getInputStream());
        PdfDocumentReaderConfig config = PdfDocumentReaderConfig.defaultConfig();
        PagePdfDocumentReader reader = new PagePdfDocumentReader(resource, config);
        List<Document> documents = reader.get();
        
        StringBuilder content = new StringBuilder();
        for (Document doc : documents) {
            content.append(doc.getContent()).append("\n\n");
        }
        return content.toString().trim();
    }

    private String parseDocx(MultipartFile file) throws IOException {
        try (InputStream inputStream = file.getInputStream();
             XWPFDocument document = new XWPFDocument(inputStream)) {
            StringBuilder content = new StringBuilder();
            for (XWPFParagraph paragraph : document.getParagraphs()) {
                String text = paragraph.getText();
                if (text != null && !text.trim().isEmpty()) {
                    content.append(text).append("\n");
                }
            }
            return content.toString().trim();
        }
    }

    private String parseExcel(MultipartFile file) throws IOException {
        try (InputStream inputStream = file.getInputStream();
             Workbook workbook = WorkbookFactory.create(inputStream)) {
            StringBuilder content = new StringBuilder();
            
            for (int sheetIndex = 0; sheetIndex < workbook.getNumberOfSheets(); sheetIndex++) {
                Sheet sheet = workbook.getSheetAt(sheetIndex);
                content.append("=== Sheet: ").append(sheet.getSheetName()).append(" ===\n");
                
                for (Row row : sheet) {
                    StringBuilder rowContent = new StringBuilder();
                    for (Cell cell : row) {
                        String cellValue = getCellValue(cell);
                        if (cellValue != null && !cellValue.trim().isEmpty()) {
                            rowContent.append(cellValue).append("\t");
                        }
                    }
                    if (rowContent.length() > 0) {
                        content.append(rowContent.toString().trim()).append("\n");
                    }
                }
                content.append("\n");
            }
            return content.toString().trim();
        }
    }

    private String getCellValue(Cell cell) {
        if (cell == null) {
            return "";
        }
        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue();
            case NUMERIC -> {
                if (DateUtil.isCellDateFormatted(cell)) {
                    yield cell.getDateCellValue().toString();
                } else {
                    yield String.valueOf(cell.getNumericCellValue());
                }
            }
            case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
            case FORMULA -> cell.getCellFormula();
            default -> "";
        };
    }

    private String parseTxt(MultipartFile file) throws IOException {
        return new String(file.getBytes());
    }

    private String parseMarkdown(MultipartFile file) throws IOException {
        return new String(file.getBytes());
    }

    private String parseImage(MultipartFile file) throws IOException {
        String extension = getFileExtension(file.getOriginalFilename()).toLowerCase();
        String mimeType = getImageMimeType(extension);
        byte[] imageBytes = file.getBytes();
        String base64Image = Base64.getEncoder().encodeToString(imageBytes);
        
        return String.format("data:%s;base64,%s", mimeType, base64Image);
    }

    private String getImageMimeType(String extension) {
        return switch (extension) {
            case "jpg", "jpeg" -> "image/jpeg";
            case "png" -> "image/png";
            case "gif" -> "image/gif";
            case "bmp" -> "image/bmp";
            case "webp" -> "image/webp";
            default -> "image/jpeg";
        };
    }

    private String getFileExtension(String filename) {
        int lastDotIndex = filename.lastIndexOf('.');
        if (lastDotIndex == -1) {
            return "";
        }
        return filename.substring(lastDotIndex + 1);
    }

    public boolean isImageFile(MultipartFile file) {
        String filename = file.getOriginalFilename();
        if (filename == null) {
            return false;
        }
        String extension = getFileExtension(filename).toLowerCase();
        return List.of("jpg", "jpeg", "png", "gif", "bmp", "webp").contains(extension);
    }

    public List<String> getSupportedFormats() {
        return List.of("pdf", "docx", "xlsx", "xls", "txt", "md", "jpg", "jpeg", "png", "gif", "bmp", "webp");
    }
}
