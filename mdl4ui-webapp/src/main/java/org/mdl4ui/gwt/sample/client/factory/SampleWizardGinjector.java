package org.mdl4ui.gwt.sample.client.factory;

import org.mdl4ui.fields.model.inject.WizardGinjector;
import org.mdl4ui.gwt.model.client.ui.DefaultWizardView;

import com.google.gwt.inject.client.GinModules;

@GinModules(SampleWizardModule.class)
public interface SampleWizardGinjector extends WizardGinjector {
}
