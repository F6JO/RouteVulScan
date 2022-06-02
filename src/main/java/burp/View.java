package burp;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;
import java.util.ArrayList;
import java.util.List;

public class View extends AbstractTableModel {
    private JSplitPane splitPane;
    public final List<LogEntry> log = new ArrayList<LogEntry>();

    private JSplitPane top;

    public LogEntry Choice;

    //
    // 实现IBurpExtender
    //

    public View() {

        // 创建最上面的一层
        top = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
//        // 创建容器，容器可以加入多个页面
        JTabbedPane tabs = new JTabbedPane();
        // 创建主拆分窗格
        splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);

        // 日志条目表
        Table logTable = new Table(this);
        JScrollPane scrollPane = new JScrollPane(logTable);


        // 将日志条目表和展示窗添加到主拆分窗格
        splitPane.add(scrollPane, "top");

        // 将两个页面插入容器
        tabs.addTab("Show", splitPane);

        // 将容器置于顶层
        top.setTopComponent(tabs);


    }

    //
    // 返回ITab
    //

    public JSplitPane Get_View() {
        return this.top;
    }

    //
    // 扩展AbstractTableModel
    //

    @Override
    public int getRowCount() {
        return log.size();
    }

    // 设置总共有几列
    @Override
    public int getColumnCount() {
        return 5;
    }

    // 设置每个列的名称
    @Override
    public String getColumnName(int columnIndex) {
        switch (columnIndex) {
            case 0:
                return "Name";
            case 1:
                return "URL";
            case 2:
                return "RE";
            case 3:
                return "INFO";
            case 4:
                return "STATE";
            default:
                return "";
        }
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return String.class;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        LogEntry logEntry = log.get(rowIndex);
        // 设置每个条目的每一列的值
        switch (columnIndex) {
            case 0:
                return logEntry.name;
            case 1:
                return logEntry.url;
            case 2:
                return logEntry.re;
            case 3:
                return logEntry.info;
            case 4:
                return logEntry.state;

            default:
                return "";
        }
    }



    //
    // 扩展JTable以处理单元格选择
    //

    private class Table extends JTable {
        public Table(TableModel tableModel) {
            super(tableModel);

        }

        // 当条目被点击时触发
        @Override
        public void changeSelection(int row, int col, boolean toggle, boolean extend) {

            LogEntry logEntry = log.get(row);
            Choice = logEntry;
            super.changeSelection(row, col, toggle, extend);
        }
    }

    //
    // 类来保存每个日志条目的详细信息
    //

    public static class LogEntry {
        final String id;
        final String name;
        final String url;
        final String re;
        final String info;
        final String state;

        LogEntry(String id, String name, String url, String re, String info, String state) {
            this.id = id;
            this.name = name;
            this.url = url;
            this.re = re;
            this.info = info;
            this.state = state;

        }
    }
}