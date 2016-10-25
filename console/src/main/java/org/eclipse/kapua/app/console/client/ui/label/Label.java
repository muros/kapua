package org.eclipse.kapua.app.console.client.ui.label;

import org.eclipse.kapua.app.console.client.resources.icons.KapuaIcon;

public class Label extends com.extjs.gxt.ui.client.widget.Label {

    private String originalText;
    private KapuaIcon icon;

    public Label(String text, KapuaIcon icon) {
        super();
        setText(text);
        setIcon(icon);
    }

    @Override
    public void setText(String text) {
        super.setText((icon != null ? icon.getInlineHTML() + "&nbsp;&nbsp;" : "") + text);
        this.originalText = text;
    }

    public void setIcon(KapuaIcon icon) {
        super.setText((icon != null ? icon.getInlineHTML() + "&nbsp;&nbsp;" : "") + originalText);
        this.icon = icon;
    }
}
