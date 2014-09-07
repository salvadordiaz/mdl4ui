package org.mdl4ui.gwt.sample.client.factory;

import javax.inject.Singleton;

import org.mdl4ui.fields.model.*;
import org.mdl4ui.fields.sample.context.SampleContext;
import org.mdl4ui.gwt.model.client.factory.GwtClientFactory;
import org.mdl4ui.gwt.model.client.factory.GwtScreenFactory;
import org.mdl4ui.gwt.model.client.ui.DefaultWizardView;

import com.google.gwt.inject.client.AbstractGinModule;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.inject.Provides;

public class SampleWizardModule extends AbstractGinModule {

    @Override
    protected void configure() {
        bind(Wizard.class).to(DefaultWizard.class).in(Singleton.class);
        bind(WizardView.class).to(DefaultWizardView.class).in(Singleton.class);

        bind(WizardContext.class).to(SampleContext.class).in(Singleton.class);
        bind(ClientFactory.class).to(GwtClientFactory.class).in(Singleton.class);
        
        bind(GwtScreenFactory.class).to(SampleScreenFactory.class).in(Singleton.class);
    }

    @Provides
    public Panel getWizardContainer() {
        return RootPanel.get();
    }
}
