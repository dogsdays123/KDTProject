package org.zerock.b01.service;

import com.itextpdf.text.*;

import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import org.springframework.stereotype.Service;
import org.zerock.b01.dto.PurchaseItemDTO;
import org.zerock.b01.dto.PurchaseOrderRequestDTO;

import java.io.ByteArrayOutputStream;

@Service
public class PdfService {
    public byte[] createPdf(PurchaseOrderRequestDTO request) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            Document document = new Document();
            PdfWriter.getInstance(document, out);
            document.open();

            // 한글 폰트
            BaseFont baseFont = BaseFont.createFont(
                    "src/main/resources/fonts/malgun.ttf",
                    BaseFont.IDENTITY_H,
                    BaseFont.EMBEDDED
            );
            Font font = new Font(baseFont, 10);
            Font titleFont = new Font(baseFont, 18, Font.BOLD);
            Font contentTitleFont = new Font(baseFont, 14, Font.NORMAL);
            Paragraph titleParagraph = new Paragraph("구매 발주서", titleFont);
            titleParagraph.setAlignment(Element.ALIGN_CENTER); // 정렬 먼저 설정
            document.add(titleParagraph); // 이후 문서에 추가


            // 공급업체 정보
            Paragraph supplierTitleParagraph = new Paragraph("발주자 정보", contentTitleFont);
            supplierTitleParagraph.setAlignment(Element.ALIGN_CENTER); // 정렬 먼저 설정
            document.add(supplierTitleParagraph); // 이후 문서에 추가

            PdfPTable infoTable = new PdfPTable(2);
            infoTable.setWidthPercentage(100);
            infoTable.setSpacingBefore(10f);
            for (PurchaseItemDTO item : request.getItems()) {
                infoTable.addCell(new PdfPCell(new Phrase("업체명", font)));
                infoTable.addCell(new PdfPCell(new Phrase(item.getSupplier(), font)));
                infoTable.addCell(new PdfPCell(new Phrase("공급업체 이메일", font)));
                infoTable.addCell(new PdfPCell(new Phrase(request.getSupplierEmail(), font)));
            }
            document.add(infoTable);
            document.add(new Paragraph(" "));

            Paragraph contentTitleParagraph = new Paragraph("품목 정보", contentTitleFont);
            contentTitleParagraph.setAlignment(Element.ALIGN_CENTER); // 정렬 먼저 설정
            document.add(contentTitleParagraph); // 이후 문서에 추가

            document.add(new Paragraph(" "));
            // 품목 테이블
            PdfPTable table = new PdfPTable(8);
            table.setWidthPercentage(100);
            String[] headers = {"조달계획코드", "자재코드", "자재명", "규격", "수량", "단가", "공급가액", "공급 납기일"};
            for (String h : headers) {
                PdfPCell header = new PdfPCell(new Phrase(h, font));
                header.setBackgroundColor(BaseColor.LIGHT_GRAY);
                header.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(header);
            }

            for (PurchaseItemDTO item : request.getItems()) {
                String standard = item.getDepth() + "x" + item.getHeight() + "x" + item.getWidth();
                int total = Integer.parseInt(request.getUnitPrice()) * Integer.parseInt(item.getQuantity());

                PdfPCell cell1 = new PdfPCell(new Phrase(item.getProcureCode(), font));
                cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(cell1);

                PdfPCell cell2 = new PdfPCell(new Phrase(item.getMaterialCode(), font));
                cell2.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(cell2);

                PdfPCell cell3 = new PdfPCell(new Phrase(item.getMaterialName(), font));
                cell3.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(cell3);

                PdfPCell cell4 = new PdfPCell(new Phrase(standard, font));
                cell4.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(cell4);

                PdfPCell cell5 = new PdfPCell(new Phrase(item.getQuantity(), font)); // 수량
                cell5.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(cell5);

                PdfPCell cell6 = new PdfPCell(new Phrase(request.getUnitPrice(), font)); // 단가
                cell6.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(cell6);

                PdfPCell cell7 = new PdfPCell(new Phrase(String.valueOf(total), font)); // 공급가액
                cell7.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(cell7);

                PdfPCell cell8 = new PdfPCell(new Phrase(item.getDueDate(), font));
                cell8.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(cell8);
            }

            document.add(table);
            document.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return out.toByteArray();
    }
}