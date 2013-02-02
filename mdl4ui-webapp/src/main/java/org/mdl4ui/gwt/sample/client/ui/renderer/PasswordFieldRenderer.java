package org.mdl4ui.gwt.sample.client.ui.renderer;

import org.mdl4ui.fields.model.FieldRenderer;
import org.mdl4ui.fields.model.component.PasswordField;
import org.mdl4ui.gwt.sample.client.ui.PasswordTextBox;

public class PasswordFieldRenderer implements FieldRenderer<PasswordField> {

    @Override
    public PasswordField getFieldComponent() {
        return new PasswordTextBox();
    }
}
