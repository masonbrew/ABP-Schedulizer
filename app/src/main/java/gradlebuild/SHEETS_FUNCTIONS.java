package gradlebuild;


import com.google.api.services.sheets.v4.model.*;

import java.util.ArrayList;
import java.util.List;


public class SHEETS_FUNCTIONS {
    public static Request createFillRangeRequest(int sheetId, int startRow, int startColumn, List<String> values,
                                                 boolean bold, int borderThickness) {
        List<CellData> cellDataList = new ArrayList<>();
        int rowIndex = startRow;
        int columnIndex = startColumn;
                                
        for (String value : values) {
            CellData cellData = new CellData()
                    .setUserEnteredValue(new ExtendedValue().setStringValue(value));

            if (bold) {
                TextFormat textFormat = new TextFormat().setBold(true);
                cellData.setUserEnteredFormat(new CellFormat().setTextFormat(textFormat));
            }

            if (borderThickness >= 1 && borderThickness <= 4) {
                String borderStyle = "SOLID";
                switch (borderThickness){
                    case 1:
                        borderStyle = "SOLID";
                        break;
                    case 2:
                        borderStyle = "SOLID_MEDIUM";
                        break;
                    case 3:
                        borderStyle = "SOLID_THICK";
                        break;
                    default:
                }


                Borders borders = new Borders().setTop(new Border().setStyle(borderStyle))
                        .setBottom(new Border().setStyle(borderStyle))
                        .setLeft(new Border().setStyle(borderStyle))
                        .setRight(new Border().setStyle(borderStyle));
                cellData.setUserEnteredFormat(new CellFormat().setBorders(borders));
            }

            GridCoordinate gridCoordinate = new GridCoordinate()
                    .setSheetId(sheetId)
                    .setRowIndex(rowIndex)
                    .setColumnIndex(columnIndex);

            cellDataList.add(cellData);
            columnIndex++;

            if (columnIndex >= values.size()) {
                columnIndex = startColumn;
                rowIndex++;
            }
        }

        List<RowData> setRowsList = new ArrayList<>();
        setRowsList.add(0, new RowData().setValues(cellDataList));
        return new Request()
                .setUpdateCells(new UpdateCellsRequest()
                        .setStart(new GridCoordinate()
                                .setSheetId(sheetId)
                                .setRowIndex(startRow)
                                .setColumnIndex(startColumn))
                        .setRows(setRowsList)
                        .setFields("userEnteredValue,userEnteredFormat"));
    }

    public static Request createFillRangeRequestHorizontal(int sheetId, int startRow, int startColumn, List<String> values,
                                                 boolean bold, int borderThickness) {
        
        List<RowData> setRowsList = new ArrayList<>();
        int rowIndex = startRow;
        int columnIndex = startColumn;
                                
        for (String value : values) {
            CellData cellData = new CellData()
                    .setUserEnteredValue(new ExtendedValue().setStringValue(value));

            if (bold) {
                TextFormat textFormat = new TextFormat().setBold(true);
                cellData.setUserEnteredFormat(new CellFormat().setTextFormat(textFormat));
            }

            if (borderThickness >= 1 && borderThickness <= 4) {
                String borderStyle = "SOLID";
                switch (borderThickness){
                    case 1:
                        borderStyle = "SOLID";
                        break;
                    case 2:
                        borderStyle = "SOLID_MEDIUM";
                        break;
                    case 3:
                        borderStyle = "SOLID_THICK";
                        break;
                    default:
                }


                Borders borders = new Borders().setTop(new Border().setStyle(borderStyle))
                        .setBottom(new Border().setStyle(borderStyle))
                        .setLeft(new Border().setStyle(borderStyle))
                        .setRight(new Border().setStyle(borderStyle));
                cellData.setUserEnteredFormat(new CellFormat().setBorders(borders));
            }

           
            List<CellData> cellDataList = new ArrayList<>();
            cellDataList.add(cellData);
            setRowsList.add(0, new RowData().setValues(cellDataList));
            columnIndex++;

            if (columnIndex >= values.size()) {
                columnIndex = startColumn;
                rowIndex++;
            }
        }
        return new Request()
                .setUpdateCells(new UpdateCellsRequest()
                        .setStart(new GridCoordinate()
                                .setSheetId(sheetId)
                                .setRowIndex(startRow)
                                .setColumnIndex(startColumn))
                        .setRows(setRowsList)
                        .setFields("userEnteredValue,userEnteredFormat"));
    }


    public static Request createCellFormatRequest(int sheetID, int rowIndex, int columnIndex, String text, String textColor, String bgColor, boolean bold) {
        Request request = new Request();

        // Set text color
        request.setRepeatCell(createUpdateCellRequest(sheetID, rowIndex, columnIndex, textColor, bgColor, bold, text));

        return request;
    }

    private static RepeatCellRequest createUpdateCellRequest(int sheetID, int rowIndex, int columnIndex, String textColor, String backgroundColor, boolean bold, String text) {
        RepeatCellRequest repeatCellRequest = new RepeatCellRequest();

        CellFormat cellFormat = new CellFormat();
        Color bgColor = getColorFromString(backgroundColor);
        Color fgColor = getColorFromString(textColor);
        
        cellFormat.setBackgroundColor(bgColor);
        TextFormat textFormat = new TextFormat().setForegroundColor(fgColor);
        if (bold == true){
            textFormat.setBold(true);
        }
        cellFormat.setTextFormat(textFormat);
        
        repeatCellRequest.setCell(new CellData()
                .setUserEnteredValue( new ExtendedValue().setStringValue(text))
                .setUserEnteredFormat(cellFormat))
                .setRange(new GridRange().setSheetId(sheetID).setStartRowIndex(rowIndex).setStartColumnIndex(columnIndex).setEndRowIndex(rowIndex+1).setEndColumnIndex(columnIndex+1))
                .setFields("*");
        return repeatCellRequest;
    }

    
    

    public static String IndexToA1(int rowIndex, int columnIndex) {
        StringBuilder sb = new StringBuilder();
        int tempIndex = columnIndex;

        while (tempIndex >= 0) {
            int remainder = tempIndex % 26;
            sb.insert(0, (char) (remainder + 'A'));
            tempIndex = (tempIndex / 26) - 1;
        }

        return sb.toString() + (rowIndex + 1);
    }

    public static Color getColorFromString(String colorString) {
        switch (colorString.toLowerCase()) {
            case "red":
                return new Color().setRed(0.6f).setGreen(0.15f).setBlue(0.15f).setAlpha(0.8f);
            case "green":
                return new Color().setGreen(1f);
            case "blue":
                return new Color().setRed(0.2f).setGreen(0.5f).setBlue(0.95f);
            case "yellow":
                return new Color().setRed(1f).setGreen(0.785f).setBlue(0.23f);
            case "cyan":
                return new Color().setGreen(1f).setBlue(1f);
            case "magenta":
                return new Color().setRed(1f).setBlue(1f);
            case "orange":
                return new Color().setRed(1f).setGreen(0.f);
            case "pink":
                return new Color().setRed(1f).setBlue(0.3f).setGreen(0.3f).setAlpha(0.8f);
            case "gray":
                return new Color().setRed(0.5f).setGreen(0.5f).setBlue(0.5f);
            case "lightgray":
                return new Color().setRed(0.6f).setGreen(0.8f).setBlue(0.8f);
            case "lightblue":
                return new Color().setRed(0.35f).setGreen(0.75f).setBlue(0.96f);
            case "darkgray":
                return new Color().setRed(0.3f).setGreen(0.3f).setBlue(0.3f);
            case "lightestblue":
                return new Color().setRed(0.52f).setGreen(0.89f).setBlue(1f);
            case "white":
                return new Color().setRed(1f).setGreen(1f).setBlue(1f);
            default:
                // Default to black if color is not recognized
                return new Color().setRed(0f).setGreen(0f).setBlue(0f);
        }
    }
}
