package org.mdl4ui.gwt.sample.client.ui.renderer;

import org.mdl4ui.fields.model.FieldRenderer;
import org.mdl4ui.fields.model.component.NumericField;
import org.mdl4ui.gwt.sample.client.ui.IntegerBox;

public class NumericFieldRenderer implements FieldRenderer<NumericField> {

    @Override
    public NumericField getFieldComponent() {
        return new IntegerBox();
    }
}
