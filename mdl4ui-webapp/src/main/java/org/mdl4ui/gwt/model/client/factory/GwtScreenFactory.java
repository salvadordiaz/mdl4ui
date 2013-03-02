package org.mdl4ui.gwt.model.client.factory;

import org.mdl4ui.fields.model.Screen;
import org.mdl4ui.gwt.model.client.ui.ElementView;

public interface GwtScreenFactory {

    ElementView getView(Screen screen);

}