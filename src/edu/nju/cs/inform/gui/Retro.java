package edu.nju.cs.inform.gui;

import edu.nju.cs.inform.core.diff.CodeElementsComparer;
import edu.nju.cs.inform.core.type.ArtifactsCollection;
import edu.nju.cs.inform.core.type.CodeElementChange;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.*;
import java.util.Set;
/**
 * Created by Xufy on 2016/3/20.
 */
public class Retro {

    static Display display;
    static Shell entrance;
    static Shell retro;
    static Font entranceBtnFont;
    static Font boldFont;
    static Font normalFont;
    static String callProjectName;
    static String callRequirementLocation;
    static String callOldCodeLocation;
    static String callNewCodeLocation;
    static Table codeElementsTable;
    static TableItem[] CodeElementChanges;
    static Table requirementElementsTable;
    static Text codeText;
    static Text requirementText;
    static MouseListener codeElementsTableMouseListener;

    static String prevPath = "";
    static int newCount;
    static CodeElementsComparer comparer;

    static {
        display = new Display();
        retro = new Shell(display);
        entrance = new Shell(retro, SWT.CLOSE);
        entranceBtnFont = new Font(display, "Arial", 12, SWT.NORMAL);
        boldFont = new Font(display, "Arial", 14, SWT.BOLD);
        normalFont = new Font(display, "Arial", 10, SWT.NORMAL);
    }

    public Retro() {}

    public static void main(String args[]) {
        initRetro();
        initEntrance();
        entrance.open();
        while (!retro.isDisposed()) {
            if (!display.readAndDispatch()) {
                display.sleep();
            }
        }
        display.dispose();
    }

    private static void initEntrance() {
        entrance.setText("START A NEW PROJECT");
        //entrance.setImage(new Image(entrance.getDisplay(), new ImageData(
         //       LayoutConstants.iconLocation)));

        int entranceWidth = LayoutConstants.screenWidth / 2;
        int entranceHeight = LayoutConstants.screenHeight / 2;
        entrance.setBounds(entranceWidth / 2, entranceHeight / 2,
                entranceWidth, entranceHeight);
        entrance.addShellListener(new ShellAdapter() {
            @Override
            public void shellClosed(ShellEvent e) {
                super.shellClosed(e);
                retro.dispose();
            }
        });
        setEntranceUI(entranceWidth, entranceHeight);
    }

    private static void setEntranceUI(int width, int height) {
        GridLayout layout = new GridLayout(1, false);
        layout.marginTop = height / 10;
        layout.verticalSpacing = height / 24;
        entrance.setLayout(layout);

        addCompositeWithoutButton(width, height, boldFont);
        addCompositeWithSingleButton(width, height, boldFont);
        addCompositeWithDoubleButton(width, height);
    }

    private static void addCompositeWithoutButton(int width, int height,
                                                  Font font) {
        Composite composite = new Composite(entrance, SWT.NONE);
        GridLayout layout = new GridLayout(3, false);
        composite.setLayout(layout);
        Label label = new Label(composite, SWT.VERTICAL | SWT.BEGINNING);
        label.setText("  Project name:");
        label.setFont(font);
        GridData labelStyle = new GridData(GridData.VERTICAL_ALIGN_CENTER);
        labelStyle.heightHint = height / 12;
        labelStyle.widthHint = width * 13 / 32;
        label.setLayoutData(labelStyle);

        final Text txtProjectName = new Text(composite, SWT.SINGLE
                | SWT.VERTICAL | SWT.BORDER);
        txtProjectName.setFont(entranceBtnFont);
        GridData textStyle = new GridData(GridData.VERTICAL_ALIGN_BEGINNING);
        textStyle.heightHint = height / 18;
        textStyle.widthHint = width * 2 / 5;
        txtProjectName.setLayoutData(textStyle);
    }

    private static void addCompositeWithSingleButton(int width, int height,
                                                     Font font) {

        Composite rComposite = new Composite(entrance, SWT.NONE);
        GridLayout rLayout = new GridLayout(3, false);
        rComposite.setLayout(rLayout);
        Label rLabel = new Label(rComposite, SWT.VERTICAL | SWT.BEGINNING);
        rLabel.setText("  Requirement location:");
        rLabel.setFont(font);
        GridData rLabelStyle = new GridData(GridData.VERTICAL_ALIGN_CENTER);
        rLabelStyle.heightHint = height / 12;
        rLabelStyle.widthHint = width * 13 / 32;
        rLabel.setLayoutData(rLabelStyle);

        final Text requirementText = new Text(rComposite, SWT.SINGLE | SWT.VERTICAL
                | SWT.BORDER);
        requirementText.setFont(entranceBtnFont);
        GridData requirementTextStyle = new GridData(GridData.VERTICAL_ALIGN_BEGINNING);
        requirementTextStyle.verticalIndent = 2;
        requirementTextStyle.heightHint = height / 18;
        requirementTextStyle.widthHint = width * 2 / 5;
        requirementText.setLayoutData(requirementTextStyle);

        Button requirementButton = new Button(rComposite, SWT.BUTTON1);
        requirementButton.setFont(entranceBtnFont);
        requirementButton.setText("Choose");
        GridData requirementButtonStyle = new GridData(GridData.VERTICAL_ALIGN_BEGINNING);
        requirementButtonStyle.verticalIndent = 1;
        requirementButtonStyle.heightHint = height / 14;
        requirementButtonStyle.widthHint = width / 9;
        requirementButton.setLayoutData(requirementButtonStyle);
        requirementButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                super.widgetSelected(e);
                DirectoryDialog fileFolder = new DirectoryDialog(entrance,
                        SWT.SAVE);
                try {
                    fileFolder.setText("Select old version location");
                    fileFolder.setFilterPath(prevPath);
                    callRequirementLocation = fileFolder.open();
                    requirementText.setText(callRequirementLocation);
                    Retro.prevPath = callRequirementLocation;
                } catch (Exception exception) {

                }

            }
        });

        Composite oComposite = new Composite(entrance, SWT.NONE);
        GridLayout oLayout = new GridLayout(3, false);
        oComposite.setLayout(oLayout);
        Label oLabel = new Label(oComposite, SWT.VERTICAL | SWT.BEGINNING);
        oLabel.setText("  Code location(old version):");
        oLabel.setFont(font);
        GridData oLabelStyle = new GridData(GridData.VERTICAL_ALIGN_CENTER);
        oLabelStyle.heightHint = height / 12;
        oLabelStyle.widthHint = width * 13 / 32;
        oLabel.setLayoutData(oLabelStyle);

        final Text oText = new Text(oComposite, SWT.SINGLE | SWT.VERTICAL
                | SWT.BORDER);
        oText.setFont(entranceBtnFont);
        GridData oTextStyle = new GridData(GridData.VERTICAL_ALIGN_BEGINNING);
        oTextStyle.verticalIndent = 2;
        oTextStyle.heightHint = height / 18;
        oTextStyle.widthHint = width * 2 / 5;
        oText.setLayoutData(oTextStyle);

        Button oButton = new Button(oComposite, SWT.BUTTON1);
        oButton.setFont(entranceBtnFont);
        oButton.setText("Choose");
        GridData oButtonStyle = new GridData(GridData.VERTICAL_ALIGN_BEGINNING);
        oButtonStyle.verticalIndent = 1;
        oButtonStyle.heightHint = height / 14;
        oButtonStyle.widthHint = width / 9;
        oButton.setLayoutData(oButtonStyle);
        oButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                super.widgetSelected(e);
                DirectoryDialog fileFolder = new DirectoryDialog(entrance,
                        SWT.SAVE);
                try {
                    fileFolder.setText("Select old version location");
                    fileFolder.setFilterPath(prevPath);
                    callOldCodeLocation = fileFolder.open();
                    oText.setText(callOldCodeLocation);
                    Retro.prevPath = callOldCodeLocation;
                } catch (Exception exception) {

                }

            }
        });

        Composite nComposite = new Composite(entrance, SWT.NONE);
        GridLayout nLayout = new GridLayout(3, false);
        nComposite.setLayout(nLayout);
        Label nLabel = new Label(nComposite, SWT.VERTICAL | SWT.BEGINNING);
        nLabel.setText("  Code location(new version):");
        nLabel.setFont(font);
        GridData nLabelStyle = new GridData(GridData.VERTICAL_ALIGN_CENTER);
        nLabelStyle.heightHint = height / 12;
        nLabelStyle.widthHint = width * 13 / 32;
        nLabel.setLayoutData(nLabelStyle);

        final Text nText = new Text(nComposite, SWT.SINGLE | SWT.VERTICAL
                | SWT.BORDER);
        nText.setFont(entranceBtnFont);
        GridData nTextStyle = new GridData(GridData.VERTICAL_ALIGN_BEGINNING);
        nTextStyle.verticalIndent = 2;
        nTextStyle.heightHint = height / 18;
        nTextStyle.widthHint = width * 2 / 5;
        nText.setLayoutData(nTextStyle);

        Button nButton = new Button(nComposite, SWT.BUTTON1);
        nButton.setFont(entranceBtnFont);
        nButton.setText("Choose");
        GridData nButtonStyle = new GridData(GridData.VERTICAL_ALIGN_BEGINNING);
        nButtonStyle.verticalIndent = 1;
        nButtonStyle.heightHint = height / 14;
        nButtonStyle.widthHint = width / 9;
        nButton.setLayoutData(nButtonStyle);
        nButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                super.widgetSelected(e);
                DirectoryDialog fileFolder = new DirectoryDialog(entrance,
                        SWT.SAVE);
                try {
                    fileFolder.setText("Select old version location");
                    fileFolder.setFilterPath(prevPath);
                    callNewCodeLocation = fileFolder.open();
                    nText.setText(callNewCodeLocation);
                    Retro.prevPath = callNewCodeLocation;
                } catch (Exception exception) {

                }

            }
        });
    }

    private static void addCompositeWithDoubleButton(int width, int height) {
        Composite composite = new Composite(entrance, SWT.NONE);
        RowLayout layout = new RowLayout(SWT.HORIZONTAL);
        layout.marginHeight = height / 30;
        layout.marginWidth = width / 6;
        layout.spacing = width / 3;
        composite.setLayout(layout);

        Button cancelBtn = new Button(composite, SWT.BUTTON1);
        cancelBtn.setFont(entranceBtnFont);
        cancelBtn.setText("Cancel");
        cancelBtn.setLayoutData(new RowData(width / 6, height / 12));
        cancelBtn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseUp(MouseEvent e) {
                super.mouseUp(e);
                newCount = 0;
                retro.close();
            }
        });

        Button finishBtn = new Button(composite, SWT.BUTTON1);
        finishBtn.setFont(entranceBtnFont);
        finishBtn.setText("Finish");
        finishBtn.setLayoutData(new RowData(width / 6, height / 12));
        finishBtn.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                super.widgetSelected(e);
                entrance.setVisible(false);
                retro.open();
                retro.forceFocus();
                //        进入Retro的时候就显示CodeElementsTable中的内容
                //callNewCodeLocation = "data/sample/AquaLush_Change4";
                //callOldCodeLocation = "data/sample/AquaLush_Change3";
                comparer = new CodeElementsComparer(callNewCodeLocation, callOldCodeLocation);
                comparer.diff();
                final Set<CodeElementChange> codeElementChangesList = comparer.getCodeElementChangesList();
                CodeElementChanges = new TableItem[codeElementChangesList.size()];
                int i = 0;
                for(CodeElementChange elementChange : codeElementChangesList){
                    TableItem item = new TableItem(codeElementsTable,0);
                    String[] codeInfo = {"" + ++i,elementChange.getElementName(),"" + elementChange.getElementType(), "" + elementChange.getChangeType()};
                    item.setText(codeInfo);
                    CodeElementChanges[i - 1] = item;
                }
            }
        });
    }

    private static void initRetro() {
        retro = new Shell(display, SWT.BORDER | SWT.MIN | SWT.MAX | SWT.RESIZE);
        //Image icon = new Image(entrance.getDisplay(), new ImageData(
          //      LayoutConstants.iconLocation));
        //retro.setImage(icon);
        retro.setText("Main");
        retro.setMinimumSize(LayoutConstants.retroMinSizeWidth, LayoutConstants.retroMinSizeHeight);

        Menu menuBar = new Menu(retro, SWT.BAR);
        fillInMenuBar(menuBar);

        int everyWidth = LayoutConstants.everyWidth;
        final GridLayout retroLayout = new GridLayout(2, false);
        setRetroLayout(retroLayout,LayoutConstants.screenWidth);

        final Label codeElementsLabel = addLabel(retro,LayoutConstants.screenHeight, everyWidth, "Differing Code Elements", boldFont);
        final Label requirementElementsLabel = addLabel(retro,LayoutConstants.screenHeight, everyWidth, "Requirement Elements", boldFont);

        int tableHeight = LayoutConstants.screenHeight / 4;
        codeElementsTable = fillInElementsTable(new String[]{"No","Id","Type","Changed"},
                new int[]{everyWidth / 15, everyWidth / 2,everyWidth / 10, everyWidth * 2 / 5},
                everyWidth, tableHeight, normalFont, "Left Click on Tableitem to Show Call Paragraph");
        requirementElementsTable = fillInElementsTable(new String[]{"No","Score","Id","Status"},
                new int[]{everyWidth / 15,everyWidth / 2 , everyWidth / 3,everyWidth / 10 },
                everyWidth, tableHeight, normalFont, "Left Click on Status Column to Mark the State of Requirement");

        final Label codeTextLabel = addLabel(retro,LayoutConstants.screenHeight,everyWidth,"Code Text",boldFont);
        final Label requirementTextLabel = addLabel(retro,LayoutConstants.screenHeight,everyWidth,"Requirements Text",boldFont);

        int textHeight = LayoutConstants.screenHeight / 3;
        codeText = addText(everyWidth, textHeight);
        requirementText = addText(everyWidth, textHeight);

        //Retrieve
        final Button retrieve = new Button(retro, SWT.BUTTON1);
        retrieve.setText("Retrieve");
        setRetrieveButtonLayout(retrieve,LayoutConstants.screenWidth,LayoutConstants.screenHeight);

        retrieve.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent selectionEvent) {
                super.widgetSelected(selectionEvent);
                //popup progress dialog
                new ProgressDialog(retro).open();
                retrieve.setEnabled(false);

                //get code difference discription
                ArtifactsCollection changeDescriptionCollection = comparer.getChangeDescriptionCollection();
                codeElementsTableMouseListener = new MouseAdapter(){
                    @Override
                    public void mouseDown(MouseEvent mouseEvent) {
                        if(mouseEvent.button == 1){
                            int codeItemIndex = codeElementsTable.getSelectionIndex();
                            TableItem[] itemList = codeElementsTable.getItems();
                            String id = itemList[codeItemIndex].getText(1);
                            //codeText.setText(changeDescriptionCollection.get(id).text);空指针报错get(id)
                            codeText.setText(id);
                        }

                        if(mouseEvent.button == 3){

                        }
                    }
                };
                codeElementsTable.addMouseListener(codeElementsTableMouseListener);

            }
        });

        retro.setMenuBar(menuBar);
        retro.setLayout(retroLayout);
        retro.addControlListener(new ControlAdapter() {

            @Override
            public void controlResized(ControlEvent controlEvent) {
                Point size = retro.getSize();
                int width = size.x;
                int height = size.y;
                setRetroLayout(retroLayout,width);
                int everyWidth = width / 2 - width / 25;
                setLabelLayout(codeElementsLabel,height,everyWidth);
                setLabelLayout(requirementElementsLabel,height,everyWidth);
                setLabelLayout(codeTextLabel,height,everyWidth);
                setLabelLayout(requirementTextLabel,height,everyWidth);
                int tableHeight = height / 4;
                setTableLayout(codeElementsTable,new int[]{everyWidth / 15, everyWidth / 2,everyWidth / 10, everyWidth * 2 / 5},
                        everyWidth,tableHeight);
                setTableLayout(requirementElementsTable,
                        new int[]{everyWidth / 15, everyWidth / 2, everyWidth / 3, everyWidth / 10},
                        everyWidth,tableHeight);
                int textHeight = height / 3;
                setTextLayout(codeText,everyWidth - width / 30,textHeight);
                setTextLayout(requirementText,everyWidth - width / 30,textHeight);
                setRetrieveButtonLayout(retrieve,width,height);
            }
        });
        retro.addShellListener(new ShellAdapter() {
            @Override
            public void shellClosed(ShellEvent e) {
                retro.dispose();
            }

            @Override
            public void shellActivated(ShellEvent shellEvent) {
            }
        });
    }

    private static void setRetroLayout(final GridLayout layout,int width){
        layout.marginLeft = width / 200;
        layout.horizontalSpacing = width / 50;
    }

    private static Menu addMenu(Menu menuBar,String menuText,String[] menuItemTexts){
        MenuItem menuHeader = new MenuItem(menuBar, SWT.CASCADE);
        menuHeader.setText(menuText);
        Menu menu = new Menu(retro, SWT.DROP_DOWN);
        menuHeader.setMenu(menu);
        if(menuItemTexts != null){
            for (int i = 0;i < menuItemTexts.length;i++){
                if(menuItemTexts[i] != null){
                    MenuItem item = new MenuItem(menu,SWT.PUSH);
                    item.setText(menuItemTexts[i]);
                }else{
                    @SuppressWarnings("unused")
                    MenuItem seperator = new MenuItem(menu, SWT.SEPARATOR);
                }
            }
        }

        return menu;
    }

    private static void fillInMenuBar(Menu menuBar) {

        Menu fileMenu = addMenu(menuBar,"&File",new String[]{"Start New Project","Load Project     ",
                "Load RTM              ","Save.....              ","Close Current Project",null,"Exit"});
        int exitItemIndex = 6;
        fileMenu.getItem(exitItemIndex).addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent selectionEvent) {
                retro.close();
            }
        });

        Menu actionMenu = addMenu(menuBar,"&Action",null);
        Menu optionsMenu = addMenu(menuBar,"&Options",null);

        Menu dataMenu = addMenu(menuBar,"&Data",new String[]{"Import","Export"});
        int importItemIndex = 0,exportItemIndex = 1;
        dataMenu.getItem(importItemIndex).setEnabled(false);
        dataMenu.getItem(exportItemIndex).setEnabled(false);

        Menu helpMenu = addMenu(menuBar,"&Help",new String[]{"Help","Export"});
    }

    private static Label addLabel(final Shell retro,int height, int everyWidth, String s, Font boldFont) {
        Label label = new Label(retro, SWT.VERTICAL | SWT.BEGINNING);
        label.setText(s);
        label.setFont(boldFont);
        setLabelLayout(label,height,everyWidth);
        return label;
    }

    private static void setLabelLayout(Label label,int height,int everyWidth){
        GridData layout = new GridData();
        layout.widthHint = everyWidth;
        layout.verticalIndent = height / 30;
        label.setLayoutData(layout);
    }

    private static Table fillInElementsTable(String[] s, int[] w,int everyWidth, int tableHeight, Font normalFont, String tip) {
        Table table = new Table(retro, SWT.IGNORE);
        table.setLinesVisible(true);
        table.setHeaderVisible(true);
        table.setFont(normalFont);
        table.setToolTipText(tip);
        for(int i = 0;i < s.length;i++){
            TableColumn column = new TableColumn(table, SWT.CASCADE);
            column.setText(s[i]);
            column.setAlignment(SWT.LEFT);
            column.setWidth(w[i]);
        }
        setTableLayout(table,w,everyWidth,tableHeight);

        return table;
    }

    public static void setTableLayout(final Table table,int[] w,int width,int height){
        for(int i = 0;i < w.length;i++){
            //if语句判断当retrieving之后在codeElementsTable中显示与需求匹配的方法
           //if(table.getColumn(i).getWidth() != 0){
           //    table.getColumn(i).setWidth(w[i]);
           //}
            if(table.getColumn(3).getWidth() == 0 && table.getColumn(2).getWidth() == 0) {
                if (i == 0) {
                    table.getColumn(0).setWidth(w[0]);
                } else if (i == 1) {
                    table.getColumn(1).setWidth(width - w[0]);
                }
            }else{
                table.getColumn(i).setWidth(w[i]);
            }
        }
        GridData layout = new GridData();
        if(width != 0 && height != 0){
            layout.widthHint = width;
            layout.heightHint = height;
        }
        table.setLayoutData(layout);
    }

    private static Text addText(int everyWidth, int textHeight) {
        Text text = new Text(retro, SWT.V_SCROLL | SWT.BORDER);
        text.setText("");
        setTextLayout(text,everyWidth - LayoutConstants.screenWidth / 30,textHeight);
        return text;
    }

    private static void setTextLayout(final Text text,int width,int height){
        GridData layout = new GridData();
        layout.heightHint = height;
        layout.widthHint = width;
        text.setLayoutData(layout);
    }
    private static void setRetrieveButtonLayout(final Button retrieve,int width,int height){
        GridData retrieveLayout = new GridData();
        retrieveLayout.verticalIndent = height / 100;
        retrieveLayout.heightHint = height / 25;
        retrieveLayout.widthHint = width / 10;
        retrieve.setLayoutData(retrieveLayout);
    }
}