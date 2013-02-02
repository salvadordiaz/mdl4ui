package org.mdl4ui.gwt.sample.client.ui.factory;

import org.mdl4ui.base.model.FieldID;
import org.mdl4ui.fields.model.FieldRenderer;
import org.mdl4ui.gwt.sample.client.ui.renderer.CheckBoxFieldRenderer;
import org.mdl4ui.gwt.sample.client.ui.renderer.DateFieldRenderer;
import org.mdl4ui.gwt.sample.client.ui.renderer.ListBoxFieldRenderer;
import org.mdl4ui.gwt.sample.client.ui.renderer.NumericFieldRenderer;
import org.mdl4ui.gwt.sample.client.ui.renderer.PasswordFieldRenderer;
import org.mdl4ui.gwt.sample.client.ui.renderer.RadioBoxFieldRenderer;
import org.mdl4ui.gwt.sample.client.ui.renderer.TextBoxFieldRenderer;

import com.google.gwt.core.shared.GWT;

public class FieldRendererFactory {

    public static final FieldRendererFactory RENDERER = GWT.create(FieldRendererFactory.class);

    private FieldRendererFactory() {
    }

    public FieldRenderer<?> get(FieldID fieldID) {
        switch (fieldID.type()) {
            case TEXTBOX:
                return new TextBoxFieldRenderer();
            case PASSWORD:
                return new PasswordFieldRenderer();
            case NUMERIC:
                return new NumericFieldRenderer();
            case LISTBOX:
                return new ListBoxFieldRenderer();
            case RADIOBOX:
                return new RadioBoxFieldRenderer();
            case CHECKBOX:
                return new CheckBoxFieldRenderer();
            case DATE:
                return new DateFieldRenderer();
            default:
                throw new IllegalArgumentException(fieldID.type().toString());
        }
    }
}
