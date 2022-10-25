package burp;

import yaml.YamlUtil;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.*;
import java.util.Hashtable;
import java.util.Map;

class TabTitleEditListener extends MouseAdapter implements ChangeListener, DocumentListener {
    public final JTextField ruleEditTextField = new JTextField();
    public final JTabbedPane ruleEditTabbedPane;
    private BurpExtender burp;
    protected int editingIndex = -1;
    protected int len = -1;
    protected Boolean listen = true;
    protected Dimension dim;
    protected Component tabComponent;
    protected Boolean isRenameOk = false;

    protected final Action startEditing = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {

            editingIndex = ruleEditTabbedPane.getSelectedIndex();
            tabComponent = ruleEditTabbedPane.getTabComponentAt(editingIndex);
            ruleEditTabbedPane.setTabComponentAt(editingIndex, ruleEditTextField);
            isRenameOk = true;
            ruleEditTextField.setVisible(true);
            ruleEditTextField.setText(ruleEditTabbedPane.getTitleAt(editingIndex));
            ruleEditTextField.selectAll();
            ruleEditTextField.requestFocusInWindow();
            len = ruleEditTextField.getText().length();
            dim = ruleEditTextField.getPreferredSize();
            ruleEditTextField.setMinimumSize(dim);

        }
    };

    protected final Action renameTabTitle = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            String title = ruleEditTextField.getText().trim();
            if (editingIndex >= 0 && !title.isEmpty()) {
                String oldName = ruleEditTabbedPane.getTitleAt(editingIndex);
                ruleEditTabbedPane.setTitleAt(editingIndex, title);
                View view = burp.views.get(oldName);
                if (view != null){
                    for (View.LogEntry logEntry : view.log){
                        Map<String,Object> up_map = new Hashtable<String,Object>();
                        up_map.put("id", Integer.parseInt(logEntry.id));
                        up_map.put("type", title);
                        up_map.put("loaded", logEntry.loaded);
                        up_map.put("name", logEntry.name);
                        up_map.put("method", logEntry.method);
                        up_map.put("url", logEntry.url);
                        up_map.put("re", logEntry.re);
                        up_map.put("info", logEntry.info);
                        up_map.put("state", logEntry.state);
                        YamlUtil.updateYaml(up_map,BurpExtender.Yaml_Path);
                    }
//                    burp.views = Bfunc.Get_Views();
                    Bfunc.show_yaml(burp);
                }


            }
            cancelEditing.actionPerformed(null);
        }
    };

    protected final Action cancelEditing = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (editingIndex >= 0) {
                ruleEditTabbedPane.setTabComponentAt(editingIndex, tabComponent);
                ruleEditTextField.setVisible(false);
                editingIndex = -1;
                len = -1;
                tabComponent = null;
                ruleEditTextField.setPreferredSize(null);
                ruleEditTabbedPane.requestFocusInWindow();
            }
            if (ruleEditTabbedPane.getTabCount()-1 == ruleEditTabbedPane.getSelectedIndex()){
                burp.Config_l.newTab();
                renameTabTitle.actionPerformed(null);
            }

        }
    };

    protected TabTitleEditListener(JTabbedPane tabbedPane,BurpExtender burp) {
        super();
        this.burp = burp;
        this.ruleEditTabbedPane = tabbedPane;
        ruleEditTextField.setBorder(BorderFactory.createEmptyBorder());
        ruleEditTextField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                renameTabTitle.actionPerformed(null);
            }
        });
        InputMap im = ruleEditTextField.getInputMap(JComponent.WHEN_FOCUSED);
        ActionMap am = ruleEditTextField.getActionMap();
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "cancel-editing");
        am.put("cancel-editing", cancelEditing);
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "rename-tab-title");
        am.put("rename-tab-title", renameTabTitle);
        ruleEditTextField.getDocument().addDocumentListener(this);
        tabbedPane.getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "start-editing");
        tabbedPane.getActionMap().put("start-editing", startEditing);
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        if (e.getSource() instanceof JTabbedPane && listen) {
            JTabbedPane pane = (JTabbedPane) e.getSource();
            if (!isRenameOk) {
                if (pane.getSelectedIndex() == pane.getComponentCount() - 1) {
                    this.burp.Config_l.newTab();
                }
            } else {
                if (pane.getSelectedIndex() == pane.getComponentCount() - 2) {
                    this.burp.Config_l.newTab();
                }
            }
        }
        renameTabTitle.actionPerformed(null);
    }

    public void setListen(Boolean listen) {
        this.listen = listen;
    }

    @Override
    public void insertUpdate(DocumentEvent e) {
        updateTabSize();
    }

    @Override
    public void removeUpdate(DocumentEvent e) {
        updateTabSize();
    }

    @Override
    public void changedUpdate(DocumentEvent e) {
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        switch (e.getButton()) {
            case 1: {
                Rectangle r = ruleEditTabbedPane.getBoundsAt(ruleEditTabbedPane.getSelectedIndex());
                boolean isDoubleClick = e.getClickCount() >= 2;
                if (isDoubleClick && r.contains(e.getPoint())) {
                    startEditing.actionPerformed(null);

                } else {
                    renameTabTitle.actionPerformed(null);
                }
                break;
            }
            case 2:{

            }
            case 3: {
                if (ruleEditTabbedPane.getTabCount()-1 != ruleEditTabbedPane.getSelectedIndex()) {
                    Config.tabMenu.show(e.getComponent(), e.getX(), e.getY());
                }

                break;
            }
            default:
                break;
        }
    }

    protected void updateTabSize() {
        ruleEditTextField.setPreferredSize(ruleEditTextField.getText().length() > len ? null : dim);
        ruleEditTabbedPane.revalidate();
    }
}