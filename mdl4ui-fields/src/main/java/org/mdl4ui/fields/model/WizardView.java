package org.mdl4ui.fields.model;

import org.mdl4ui.base.model.ScreenID;

import com.google.gwt.user.client.ui.IsWidget;

public interface WizardView extends IsWidget {
    void displayScreen(Wizard wizard, ScreenID screenID);
}
