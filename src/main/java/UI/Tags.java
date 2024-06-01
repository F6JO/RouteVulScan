package UI;

import burp.*;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

public class Tags extends AbstractTableModel implements ITab, IMessageEditorController {
    public IBurpExtenderCallbacks callbacks;

    private JSplitPane top;

    public List<TablesData> Udatas = new ArrayList<>();

    public IMessageEditor HRequestTextEditor;

    public IMessageEditor HResponseTextEditor;

    private IHttpRequestResponse currentlyDisplayedItem;

    public URLTable Utable;

    private JScrollPane UscrollPane;

    private JSplitPane HjSplitPane;

    private JTabbedPane Ltable;

    private JTabbedPane Rtable;

    private JSplitPane splitPane;

    private JPopupMenu m_popupMenu;

    public List<String> Get_URL_list() {
        List<String> Urls = new ArrayList<>();
        for (TablesData data : this.Udatas) {
            Urls.add(data.url);
        }
        return Urls;
    }


    public Tags(IBurpExtenderCallbacks callbacks, Config Config_l) {
        this.callbacks = callbacks;

//        this.tagName = name;
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                // 创建最上面的一层
                Tags.this.top = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
                // 创建容器，容器可以加入多个页面
                JTabbedPane tabs = new JTabbedPane();
                // 创建主拆分窗格
                splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);


                // 日志条目表
                URLTable URLTab = new URLTable(Tags.this);
//                URLTable URLTab = new URLTable();
//                JXTable URLTab = new JXTable();
                URLTab.setModel(Tags.this);
//                URLTab.addMouseListener(new Right_click_menu(Tags.this));

                m_popupMenu = new JPopupMenu();
                JMenuItem delMenItem = new JMenuItem();
                delMenItem.setText("Delete item");
                delMenItem.addActionListener(new Remove_action(Tags.this));
                JMenuItem delAllMenItem = new JMenuItem();
                delAllMenItem.setText("Clear all history");
                delAllMenItem.addActionListener(new Remove_All(Tags.this));

                m_popupMenu.add(delMenItem);
                m_popupMenu.add(delAllMenItem);
                URLTab.addMouseListener(new java.awt.event.MouseAdapter() {
                    public void mouseClicked(java.awt.event.MouseEvent evt) {
                        jTable1MouseClicked(evt);
                    }
                });



                Tags.this.Utable = URLTab;
                Tags.this.UscrollPane = new JScrollPane(Tags.this.Utable);


                //创建请求和响应的展示窗
                Tags.this.HjSplitPane = new JSplitPane();
                Tags.this.HjSplitPane.setDividerLocation(0.5D);

                // 创建请求/响应的子选项卡
                Tags.this.Ltable = new JTabbedPane();
                Tags.this.Rtable = new JTabbedPane();
                Tags.this.HRequestTextEditor = Tags.this.callbacks.createMessageEditor(Tags.this, false);
                Tags.this.HResponseTextEditor = Tags.this.callbacks.createMessageEditor(Tags.this, false);


                Tags.this.Ltable.addTab("Request", Tags.this.HRequestTextEditor.getComponent());
                Tags.this.Rtable.addTab("Response", Tags.this.HResponseTextEditor.getComponent());

                // 将子选项卡添加进主选项卡
                Tags.this.HjSplitPane.setResizeWeight(0.5D);
                Tags.this.HjSplitPane.setDividerSize(3);
                Tags.this.HjSplitPane.add(Tags.this.Ltable, "left");
                Tags.this.HjSplitPane.add(Tags.this.Rtable, "right");

                // 将日志条目表和展示窗添加到主拆分窗格
                Tags.this.splitPane.add(Tags.this.UscrollPane, "left");
                Tags.this.splitPane.add(Tags.this.HjSplitPane, "right");

                // 将两个页面插入容器
                tabs.addTab("VulDisplay", Tags.this.splitPane);
//                JTabbedPane ConfigView = new JTabbedPane();
//                ConfigView.addTab("Rules",);
                tabs.addTab("Config",Config_l.$$$getRootComponent$$$());

                // 将容器置于顶层
                top.setTopComponent(tabs);

                // 定制我们的UI组件
                Tags.this.callbacks.customizeUiComponent(Tags.this.top);

                // 将自定义选项卡添加到Burp的UI
                Tags.this.callbacks.addSuiteTab(Tags.this);
            }
        });
    }

    public String getTabCaption() {
        return "RouteVulScan";
    }

    public Component getUiComponent() {
        return this.top;
    }

    public int getRowCount() {
        return this.Udatas.size();
    }

    public int getColumnCount() {
        return 9;
    }

    public String getColumnName(int columnIndex) {
        switch (columnIndex) {
            case 0:
                return "#";
            case 1:
                return "VulName";
            case 2:
                return "Method";
            case 3:
                return "Url";
            case 4:
                return "Status";
            case 5:
                return "Info";
            case 6:
                return "Size";
            case 7:
                return "startTime";
            case 8:
                return "endTime";
        }
        return null;
    }

    public Class<?> getColumnClass(int columnIndex) {
        return String.class;
    }

    public Object getValueAt(int rowIndex, int columnIndex) {
        TablesData datas = this.Udatas.get(rowIndex);
        switch (columnIndex) {
            case 0:
                return Integer.valueOf(datas.id);
            case 1:
                return datas.VulName;
            case 2:
                return datas.Method;
            case 3:
                return datas.url;
            case 4:
                return datas.status;
            case 5:
                return datas.Info;
            case 6:
                return datas.Size;
            case 7:
                return datas.startTime;
            case 8:
                return datas.endTime;
        }
        return null;
    }

    public byte[] getRequest() {
        return this.currentlyDisplayedItem.getRequest();
    }

    public byte[] getResponse() {
        return this.currentlyDisplayedItem.getResponse();
    }

    public IHttpService getHttpService() {
        return this.currentlyDisplayedItem.getHttpService();
    }

    public int add(String VulName, String Method, String url, String status, String Info, String Size, IHttpRequestResponse requestResponse) {
        synchronized (this.Udatas) {
//            this.callbacks.printOutput(url + "    " + Info);
            Date d = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String startTime = sdf.format(d);
            int id = this.Udatas.size();
            this.Udatas.add(
                    new TablesData(
                            id,
                            VulName,
                            Method,
                            url,
                            status,
                            Info,
                            Size,
                            requestResponse,
                            startTime,
                            ""));
            fireTableRowsInserted(id, id);
            return id;
        }
    }


    public class URLTable extends JTable {
        private TableRowSorter<TableModel> sorter;

        public URLTable(TableModel tableModel) {
            super(tableModel);
            sorter = new TableRowSorter<TableModel>(tableModel) {
                @Override
                public Comparator<?> getComparator(int column) {
                    TableColumnModel columnModel = getColumnModel();
                    int numberColumnIndex = -1;
                    for (int i = 0; i < columnModel.getColumnCount(); i++) {
                        if (columnModel.getColumn(i).getHeaderValue().toString().equals("#")) {
                            numberColumnIndex = i;
                            break;
                        }
                    }

                    if (column == numberColumnIndex) {
                        return Comparator.comparingInt((Object o) -> {
                            int modelRow = ((TableRowSorter<TableModel>) this).convertRowIndexToModel(((Integer) o).intValue());
                            return (Integer) getModel().getValueAt(convertRowIndexToView(modelRow), column);
                        });
                    }
                    return super.getComparator(column);
                }
            };
            setRowSorter(sorter);

            // 添加鼠标监听器
            getTableHeader().addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (e.getClickCount() == 2) {
                        int columnIndex = getColumnModel().getColumnIndexAtX(e.getX());
                        toggleSortOrder(columnIndex);
                    }
                }
            });
        }
        public void changeSelection(int row, int col, boolean toggle, boolean extend) {
            TablesData dataEntry = Tags.this.Udatas.get(convertRowIndexToModel(row));
            Tags.this.HRequestTextEditor.setMessage(dataEntry.requestResponse.getRequest(), true);
            Tags.this.HResponseTextEditor.setMessage(dataEntry.requestResponse.getResponse(), false);
            Tags.this.currentlyDisplayedItem = dataEntry.requestResponse;
            super.changeSelection(row, col, toggle, extend);
        }

        public void sortColumn(int columnIndex, SortOrder sortOrder) {
            List<RowSorter.SortKey> sortKeys = new ArrayList<>();
            sortKeys.add(new RowSorter.SortKey(columnIndex, sortOrder));
            try {
                sorter.setSortKeys(sortKeys);
            }catch (Exception a){
                String x = a.toString();
                System.out.println(x);
            }

        }

        public void toggleSortOrder(int columnIndex) {
            List<? extends RowSorter.SortKey> sortKeys = sorter.getSortKeys();
            if (sortKeys.isEmpty()) {
                sortColumn(columnIndex, SortOrder.ASCENDING);
            } else {
                RowSorter.SortKey sortKey = sortKeys.get(0);
                if (sortKey.getColumn() == columnIndex) {
                    sortColumn(columnIndex, sortKey.getSortOrder() == SortOrder.ASCENDING ? SortOrder.DESCENDING : SortOrder.ASCENDING);
                } else {
                    sortColumn(columnIndex, SortOrder.ASCENDING);
                }
            }

            // 根据列名设置比较器
            String columnName = getColumnModel().getColumn(columnIndex).getHeaderValue().toString();
            if (columnName.equals("Size") || columnName.equals("Status") ){
                sorter.setComparator(columnIndex, new Comparator<String>() {
                    @Override
                    public int compare(String o1, String o2) {
                        return Integer.compare(Integer.parseInt(o1), Integer.parseInt(o2));
                    }
                });
            } else if (columnName.equals("#")) {
                TableColumnModel columnModel = getColumnModel();
                int numberColumnIndex = -1;
                for (int i = 0; i < columnModel.getColumnCount(); i++) {
                    if (columnModel.getColumn(i).getHeaderValue().toString().equals("#")) {
                        numberColumnIndex = i;
                        break;
                    }
                }
                final int retNumber = numberColumnIndex;
                sorter.setComparator(numberColumnIndex, Comparator.comparingInt((Object o) -> {
                    int modelRow = ((TableRowSorter<TableModel>) sorter).convertRowIndexToModel(((Integer) o).intValue());
                    return (Integer) getModel().getValueAt(convertRowIndexToView(modelRow), retNumber);
                }));
            }else if (columnName.equals("startTime")) {
                sorter.setComparator(columnIndex, new Comparator<String>() {
                    @Override
                    public int compare(String o1, String o2) {
                        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        try {
                            Date date1 = format.parse(o1);
                            Date date2 = format.parse(o2);
                            return date1.compareTo(date2);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return 0;
                    }
                });
            } else {
                sorter.setComparator(columnIndex, new Comparator<String>() {
                    @Override
                    public int compare(String o1, String o2) {
                        return o1.compareTo(o2);
                    }
                });
            }
        }
    }


    public static class TablesData {
        final int id;

        final String VulName;

        final String Method;

        final String url;

        final String status;

        final String Info;

        final String Size;

        final IHttpRequestResponse requestResponse;

        final String startTime;

        final String endTime;

        public TablesData(int id, String VulName, String Method, String url, String status, String Info, String Size, IHttpRequestResponse requestResponse, String startTime, String endTime) {
            this.id = id;
            this.VulName = VulName;
            this.Method = Method;
            this.url = url;
            this.status = status;
            this.Info = Info;
            this.Size = Size;
            this.requestResponse = requestResponse;
            this.startTime = startTime;
            this.endTime = endTime;
        }
    }


    private void jTable1MouseClicked(java.awt.event.MouseEvent evt) {
        mouseRightButtonClick(evt);
    }


    private void mouseRightButtonClick(java.awt.event.MouseEvent evt) {
        //判断是否为鼠标的BUTTON3按钮，BUTTON3为鼠标右键
        if (evt.getButton() == java.awt.event.MouseEvent.BUTTON3) {
            //经过点击位置找到点击为表格中的行
            int focusedRowIndex = this.Utable.rowAtPoint(evt.getPoint());
            if (focusedRowIndex == -1) {
                return;
            }
            //将表格所选项设为当前右键点击的行
//            this.Utable.setRowSelectionInterval(focusedRowIndex, focusedRowIndex);
            //弹出菜单
            m_popupMenu.show(this.Utable, evt.getX(), evt.getY());
        }

    }


}


class Remove_All implements ActionListener {
    private Tags tag;

    public Remove_All(Tags tag) {
        this.tag = tag;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
//        tag.Udatas.clear();
        while (tag.Udatas.size() != 0){
            tag.Udatas.remove(0);
            tag.fireTableRowsDeleted(0, 0);
        }
        tag.HRequestTextEditor.setMessage(new byte[]{},true);
        tag.HResponseTextEditor.setMessage(new byte[]{},false);
    }

}


class Remove_action implements ActionListener {
    private Tags tag;

    public Remove_action(Tags tag) {
        this.tag = tag;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        int[] RemId = tag.Utable.getSelectedRows();
        for (int i : reversal(RemId)) {
            tag.Udatas.remove(i);
            tag.fireTableRowsDeleted(i, i);
            tag.HRequestTextEditor.setMessage(new byte[]{},true);
            tag.HResponseTextEditor.setMessage(new byte[]{},false);
        }
    }

    public Integer[] reversal(int[] int_array) {
        Integer newScores[] = new Integer[int_array.length];
        for (int i = 0; i < int_array.length; i++) {
            newScores[i] = new Integer(int_array[i]);
        }

        Arrays.sort(newScores, Collections.reverseOrder());
        return newScores;

    }
}

