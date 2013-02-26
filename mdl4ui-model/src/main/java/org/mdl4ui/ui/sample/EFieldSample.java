package org.mdl4ui.ui.sample;

import static org.mdl4ui.base.model.UIElementImpl.containsRec;

import java.util.List;

import org.mdl4ui.base.model.BlockID;
import org.mdl4ui.base.model.EElementType;
import org.mdl4ui.base.model.EFieldType;
import org.mdl4ui.base.model.ElementID;
import org.mdl4ui.base.model.FieldID;
import org.mdl4ui.base.model.GroupID;
import org.mdl4ui.base.model.UIElementImpl;

public enum EFieldSample implements FieldID {
    FIRST_NAME(EFieldType.TEXTBOX), //
    LAST_NAME(EFieldType.TEXTBOX), //
    EMAIL(EFieldType.TEXTBOX), //
    BIRTHDATE(EFieldType.DATE), //
    LANGUAGE(EFieldType.RADIO_GROUP), //
    TIMEZONE(EFieldType.LISTBOX), //

    EMAIL_ACCEPTED(EFieldType.RADIO_GROUP), //
    EMAILS_PREFERENCES(EFieldType.CHECKBOX_GROUP), //
    MAX_WEEKLY_EMAILS(EFieldType.NUMERIC), //

    LOGIN(EFieldType.TEXTBOX), //
    PASSWORD(EFieldType.PASSWORD), //
    PASSWORD_CONFIRMATION(EFieldType.PASSWORD);
    private EFieldType type;

    private EFieldSample(EFieldType type) {
        this.type = type;
    }

    @Override
    public EElementType elementType() {
        return EElementType.FIELD;
    }

    @Override
    public List<ElementID> childs() {
        return null;
    }

    @Override
    public boolean contains(ElementID child) {
        return containsRec(this, child);
    }

    @Override
    public List<FieldID> fields() {
        return UIElementImpl.<FieldID, ElementID> collectFields(this);
    }

    @Override
    public List<BlockID> blocks() {
        return UIElementImpl.<BlockID, ElementID> collectBlocks(this);
    }

    @Override
    public List<GroupID> groups() {
        return UIElementImpl.<GroupID, ElementID> collectGroups(this);
    }

    @Override
    public EFieldType type() {
        return type;
    }
}