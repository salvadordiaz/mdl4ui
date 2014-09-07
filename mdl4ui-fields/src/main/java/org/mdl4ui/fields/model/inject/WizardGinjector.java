package org.mdl4ui.fields.model.inject;

import org.mdl4ui.fields.model.Wizard;
import org.mdl4ui.fields.model.WizardView;

import com.google.gwt.inject.client.Ginjector;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Panel;

public interface WizardGinjector extends Ginjector {
    Wizard getWizard();

    WizardView getView();

    Panel getWizardContainer();
}
