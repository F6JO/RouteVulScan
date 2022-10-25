package burp;

import yaml.YamlUtil;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
//        JTabbedPane tabs = new JTabbedPane();

        // 创建主拆分窗格
        splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);

        // 日志条目表
        Table logTable = new Table(this);
        logTable.addMouseListener(new MouseAdapter() {
            //不要用click点击监听，如果速度过快就会识别为双击，有bug
            @Override
            public void mousePressed(MouseEvent e) {
                // 点击复选框的操作
                if (e.getClickCount() == 1) {
                    int row = logTable.getSelectedRow();
                    int column = logTable.getSelectedColumn();
                    //复选框在哪列填多少，限制鼠标点击的位置
                    if (column == 0) {
                        LogEntry logEntry = log.get(row);
                        Map<String, Object> add_map = new HashMap<String, Object>();
                        add_map.put("id", Integer.parseInt(logEntry.id));
                        add_map.put("type", logEntry.type);
                        add_map.put("loaded", !logEntry.loaded);
                        add_map.put("name", logEntry.name);
                        add_map.put("method", logEntry.method);
                        add_map.put("url", logEntry.url);
                        add_map.put("re", logEntry.re);
                        add_map.put("info", logEntry.info);
                        add_map.put("state", logEntry.state);
                        YamlUtil.updateYaml(add_map, BurpExtender.Yaml_Path);
                        logEntry.loaded = !logEntry.loaded;


                    }

                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(logTable);



        // 将容器置于顶层
        top.setTopComponent(scrollPane);

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
        return 7;
    }

    // 设置每个列的名称
    @Override
    public String getColumnName(int columnIndex) {
        switch (columnIndex) {
            case 0:
                return "Loaded";
            case 1:
                return "Name";
            case 2:
                return "Method";
            case 3:
                return "Url";
            case 4:
                return "Re";
            case 5:
                return "Info";
            case 6:
                return "State";
            default:
                return "";
        }
    }


    @Override
    public Class<?> getColumnClass(int columnIndex) {
        if (columnIndex == 0) {
            return Boolean.class;
        } else {
            return String.class;
        }
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return column == 0;
    }


    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        LogEntry logEntry = log.get(rowIndex);
        // 设置每个条目的每一列的值
        switch (columnIndex) {
            case 0:
                return logEntry.loaded;
            case 1:
                return logEntry.name;
            case 2:
                return logEntry.method;
            case 3:
                return logEntry.url;
            case 4:
                return logEntry.re;
            case 5:
                return logEntry.info;
            case 6:
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
        String type;
        Boolean loaded;
        final String method;
        final String name;
        final String url;
        final String re;
        final String info;
        final String state;

        LogEntry(String id, String type,Boolean loaded, String name, String method, String url, String re, String info, String state) {
            this.id = id;
            this.type = type;
            this.loaded = loaded;
            this.name = name;
            this.method = method;
            this.url = url;
            this.re = re;
            this.info = info;
            this.state = state;

        }
    }
}