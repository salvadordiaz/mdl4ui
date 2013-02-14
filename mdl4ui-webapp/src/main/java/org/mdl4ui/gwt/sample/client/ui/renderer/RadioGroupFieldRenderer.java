package org.mdl4ui.gwt.sample.client.ui.renderer;

import org.mdl4ui.base.model.FieldID;
import org.mdl4ui.fields.model.component.RadioGroupField;
import org.mdl4ui.gwt.sample.client.ui.RadioGroup;

public class RadioGroupFieldRenderer extends BaseFieldRenderer<RadioGroupField> {

    @Override
    protected RadioGroupField createFieldComponent(FieldID fieldID) {
        return new RadioGroup(fieldID.toString());
    }
}
