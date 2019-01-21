package edu.nju.cs.inform.gui;

import edu.nju.cs.inform.core.ir.IRModelConst;
import edu.nju.cs.inform.core.ir.Retrieval;
import edu.nju.cs.inform.core.recommend.MethodRecommendation;
import edu.nju.cs.inform.core.type.ArtifactsCollection;
import edu.nju.cs.inform.core.type.SimilarityMatrix;
import edu.nju.cs.inform.io.ArtifactsReader;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;
import java.util.List;

import java.util.Map;

/**
 * Created by Xufy on 2016/3/20.
 */
public class ProgressDialog extends Dialog{
    private Button finish;
    static ProgressBar progress;

    public ProgressDialog(Shell parentShell) {
        super(parentShell);
    }

    @Override
    protected Control createDialogArea(Composite parent) {

        final Composite area = new Composite(parent, SWT.NULL);
        area.setBounds(LayoutConstants.screenWidth / 2 - LayoutConstants.screenWidth / 30,LayoutConstants.screenHeight / 2 - LayoutConstants.screenHeight / 60,
                LayoutConstants.screenWidth / 15,LayoutConstants.screenHeight / 30);
        addProgressBar(area);
        addButton(area);
        updateProgress();

        return area;

    }

    private void addProgressBar(Composite parent){
        progress = new ProgressBar(parent,SWT.NULL);
        progress.setMinimum(0);
        progress.setMaximum(100);
        final GridLayout layout = new GridLayout();
        layout.marginHeight = LayoutConstants.screenHeight / 50;
        layout.marginWidth = LayoutConstants.screenWidth / 50;
        parent.setLayout(layout);
        GridData progressStyle = new GridData();
        progressStyle.widthHint = LayoutConstants.screenWidth / 10;
        progressStyle.heightHint = LayoutConstants.screenHeight / 30;
        progress.setLayoutData(progressStyle);
    }

    private void addButton(Composite parent) {
        finish = new Button(parent,SWT.PUSH);
        finish.setText("Finish");
        finish.setEnabled(false);
        finish.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent selectionEvent) {
                close();
                ArtifactsCollection changeDescriptionCollection = Retro.comparer.getChangeDescriptionCollection();
                Retro.callRequirementLocation = "data/sample/AquaLush_Requirement";
                final ArtifactsCollection requirementCollection = ArtifactsReader.getCollections(Retro.callRequirementLocation, ".txt");

                Retrieval retrieval = new Retrieval(changeDescriptionCollection, requirementCollection, IRModelConst.VSM);
                retrieval.tracing();

                final SimilarityMatrix similarityMatrix = retrieval.getSimilarityMatrix();
                final MethodRecommendation methodRecommendation = new MethodRecommendation(Retro.comparer, requirementCollection, similarityMatrix);
                final Map<String, java.util.List<String>> recommendMethodsForRequirements = methodRecommendation.getRecommendMethodsForRequirements();

                Map<String, Double> candidatedOutdatedRequirementsRank = retrieval.getCandidateOutdatedRequirementsRank();
                int requirementItemIndex = 0;
                for(Map.Entry<String,Double> map:candidatedOutdatedRequirementsRank.entrySet()){
                    TableItem item = new TableItem(Retro.requirementElementsTable,SWT.NONE);
                    item.setText(new String[]{String.valueOf( ++requirementItemIndex),String.valueOf(map.getValue()),map.getKey(),"default"});
                }

                Retro.requirementElementsTable.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseDown(MouseEvent mouseEvent) {

                        if(mouseEvent.button == 1){
                            int codeItemIndex = Retro.requirementElementsTable.getSelectionIndex();
                            TableItem[] itemList = Retro.requirementElementsTable.getItems();
                            String id = itemList[codeItemIndex].getText(2);
                            Retro.requirementText.setText(requirementCollection.get(id).text);

                        }

                        if(mouseEvent.button == 3){
                            //Point point = new Point(mouseEvent.x,mouseEvent.y);
                            final TableItem item = Retro.requirementElementsTable.getItem(Retro.requirementElementsTable.getSelectionIndex());
                            if(item == null){
                                return;
                            }
                            //鼠标右键显示remark as outdated和recommend methods选项菜单
                            Menu menu = new Menu(Retro.requirementElementsTable);
                            MenuItem remarkAsOutdated = new MenuItem(menu,SWT.PUSH);
                            remarkAsOutdated.setText("Remark as outdated");
                            remarkAsOutdated.addListener(SWT.Selection, new Listener() {
                                @Override
                                public void handleEvent(Event event) {
                                    TableItem[] itemList = Retro.requirementElementsTable.getItems();
                                    item.setText(3,"OutDated");
                                }
                            });
                            MenuItem recommendMethods = new MenuItem(menu,SWT.PUSH);
                            recommendMethods.setText("Recommend methods");
                            recommendMethods.addListener(SWT.Selection, new Listener() {
                                @Override
                                public void handleEvent(Event event) {

                                    //删除原来codeElementsTable中的所有单元格removeAll()
                                    //拉大第二字个段宽度
                                    //将第三第四个字段宽度设置为0、0
                                    //取消监听事件
                                    int everyWidth = LayoutConstants.everyWidth;
                                    Table table = Retro.codeElementsTable;
                                    int size = table.getItemCount();
                                    for(int i = size - 1;i >= 0;i--){
                                        table.remove(i);
                                    }
                                    Retro.setTableLayout(table,new int[]{everyWidth / 15, everyWidth * 14 / 15,0,0},0,0);
                                    table.getColumn(2).setResizable(false);
                                    table.getColumn(3).setResizable(false);
                                    table.removeMouseListener(Retro.codeElementsTableMouseListener);

                                    //往表格中添加行
                                    //获取到requirementElementsTable中被点击的一行的id--eg:SRS358
                                    String req = item.getText(2);
                                    List<String> recommendList = recommendMethodsForRequirements.get(req);
                                    int i = 0;
                                    for (String method : recommendList) {
                                        TableItem item = new TableItem(table,0);
                                        String[] codeInfo = {"" + ++i,method};
                                        item.setText(codeInfo);
                                   }
                                }
                            });
                            Retro.requirementElementsTable.setMenu(menu);
                        }
                    }
                });
            }
        });
        GridData buttonStyle = new GridData();
        buttonStyle.widthHint = LayoutConstants.screenWidth / 20;
        buttonStyle.heightHint = LayoutConstants.screenHeight / 30;
        buttonStyle.verticalIndent = LayoutConstants.screenHeight / 60;
        buttonStyle.horizontalIndent = LayoutConstants.screenWidth / 40;
        finish.setLayoutData(buttonStyle);
    }

    /**
     * 使用Thread更新线程
     */
    protected void updateProgress(){
        new Thread(new Runnable() {
            private int start = 0;
            private int end = 100;
            private static final int INCREMENT = 10;


            @Override
            public void run() {
                while(!progress.isDisposed()){
                    Display.getDefault().asyncExec(new Runnable() {
                        @Override
                        public void run() {
                            if(progress.getSelection() != end) {
                                progress.setSelection(start += INCREMENT);
                            }else{
                                finish.setEnabled(true);
                            }
                        }
                    });

                    try
                    {
                        Thread.sleep(100);
                    }
                    catch (InterruptedException e)
                    {
                        e.printStackTrace();
                    }
                }
            }

        }).start();
    }

    @Override
    protected void createButtonsForButtonBar(Composite parent) {
        //不重写此方法就会默认创建两个按钮
    }

}
