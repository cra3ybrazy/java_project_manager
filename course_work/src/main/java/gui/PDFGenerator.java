package gui;
import course_work.*;

import java.util.List;

import java.io.FileOutputStream;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;


public class PDFGenerator {
	private static final Logger PDFlog = LogManager.getLogger(PDFGenerator.class);
	private String FILE;
    public static final String FONT = "C:\\Users\\komba\\eclipse-workspace\\course_work\\src\\main\\resources\\timesnrcyrmt.ttf";
    private BaseFont bf;
    Font rus_font;
    private Document document;

    public PDFGenerator(String path)
    {
        this.FILE = path;
        try
        {
        	PDFlog.info("Creating PDF file.");
            document = new Document();
            PdfWriter.getInstance(document, new FileOutputStream(FILE));//получения экземляра класса PdfWriter
            document.open();
            bf=BaseFont.createFont(FONT, BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
            PDFlog.info("File has been created.");
        }
        catch(Exception e)
        {
            PDFlog.error("Doc ruined or font hadn't fit.");
            e.printStackTrace();
        }
    }
    
    public void addProjects(List<Client> someC)
    {
        PDFlog.info("Creating page with projects.");
        Font font = new Font(bf, 12, Font.NORMAL);
        Paragraph ph = new Paragraph("Список проектов клиентов", font);
        addEmptyLine(ph, 1);

        PdfPTable table = new PdfPTable(7);
        try{ 
            PDFlog.info("Creating table.");
            table.setTotalWidth(1650);
            table.setWidths(new int[]{320, 320, 340, 420, 400, 340, 350}); 
            PDFlog.info("Table created!");
        }
        catch(Exception e){
        	PDFlog.error("Table isn't created.");
        	e.getStackTrace(); 
        }
        
        PdfPCell pfpc;
        pfpc = new PdfPCell(new Phrase("ID Проекта", font));
        pfpc.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(pfpc);
        pfpc = new PdfPCell(new Phrase("Имя Клиента", font));
        pfpc.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(pfpc);
        pfpc = new PdfPCell(new Phrase("Фамилия Клиента", font));
        pfpc.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(pfpc);
        pfpc = new PdfPCell(new Phrase("Проект", font));
        pfpc.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(pfpc);
        pfpc = new PdfPCell(new Phrase("Задание", font));
        pfpc.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(pfpc);
        pfpc = new PdfPCell(new Phrase("Фамилия сотрудника", font));
        pfpc.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(pfpc);
        pfpc = new PdfPCell(new Phrase("Срок задания", font));
        pfpc.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(pfpc);

        for (Client cl : someC) {
            PDFlog.info("Trying add clients and their projects.");
        	cl.addCells(table, font);
        	PDFlog.info("Data added successfully");
        }
        
        table.setHeaderRows(1);
        ph.add(table);
        try
        {
            document.add(ph);
            document.newPage();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    private static void addEmptyLine(Paragraph paragraph, int number) {
        for (int i = 0; i < number; i++) {
            paragraph.add(new Paragraph(" "));
        }
    }

    public void doClose()
    {
        document.close();
    }
}
