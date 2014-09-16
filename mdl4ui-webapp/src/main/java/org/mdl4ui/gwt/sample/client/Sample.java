package org.mdl4ui.gwt.sample.client;

import org.mdl4ui.base.model.ScenarioID;
import org.mdl4ui.fields.model.Wizard;
import org.mdl4ui.fields.model.WizardView;
import org.mdl4ui.fields.model.event.FieldEvent;
import org.mdl4ui.fields.model.event.FieldEventListener;
import org.mdl4ui.fields.model.inject.WizardGinjector;
import org.mdl4ui.gwt.sample.client.factory.SampleWizardGinjector;
import org.mdl4ui.ui.sample.EScenarioSample;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.IsWidget;

public class Sample implements EntryPoint {

    @Override
    public void onModuleLoad() {
        WizardGinjector injector = GWT.create(SampleWizardGinjector.class);
        Wizard wizard = injector.getWizard();
        wizard.addFieldListener(new FieldEventListener() {
            @Override
            public void onEvent(FieldEvent event) {
                GWT.log(event.toString());
            }
        });
        ;
        injector.getWizardContainer().add(injector.getView());
    }
}
