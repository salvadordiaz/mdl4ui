package org.mdl4ui.fields.model;

import static java.util.Arrays.asList;
import static org.mdl4ui.fields.model.DefaultEditor.valid;
import static org.mdl4ui.fields.model.FieldState.ERROR;
import static org.mdl4ui.fields.model.FieldState.SET;
import static org.mdl4ui.fields.model.event.FieldEvent.newEvent;
import static org.mdl4ui.fields.model.event.FieldEvent.releaseSourceEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.mdl4ui.base.model.BlockID;
import org.mdl4ui.base.model.ElementID;
import org.mdl4ui.base.model.ElementType;
import org.mdl4ui.base.model.FieldID;
import org.mdl4ui.base.model.GroupID;
import org.mdl4ui.base.model.ScenarioID;
import org.mdl4ui.base.model.ScreenID;
import org.mdl4ui.fields.model.event.EventProperty;
import org.mdl4ui.fields.model.event.FieldEvent;
import org.mdl4ui.fields.model.event.FieldEventListener;
import org.mdl4ui.fields.model.validation.FieldValidation;

import com.google.inject.Inject;

public class DefaultWizard implements Wizard {

    private final ClientFactory clientFactory;
    private final Map<ScreenID, Screen> screens;
    private final WizardContext context;
    private final ScenarioID scenario;

    private Screen currentScreen;

    @Inject
    public DefaultWizard(WizardContext context, ClientFactory clientFactory, ScenarioID scenario) {
        this.clientFactory = clientFactory;
        this.scenario = scenario;
        this.context = context;
        this.screens = createScreens(scenario);
    }

    @Override
    public void addFieldListener(FieldEventListener listener) {
        clientFactory.getEditorFactory().addListener(listener);
        clientFactory.getBehaviourFactory().addListener(listener);
        clientFactory.getInitializerFactory().addListener(listener);
    }

    @Override
    public void removeFieldListener(FieldEventListener listener) {
        clientFactory.getEditorFactory().removeListener(listener);
        clientFactory.getBehaviourFactory().removeListener(listener);
        clientFactory.getInitializerFactory().removeListener(listener);
    }

    @Override
    public WizardContext getContext() {
        return context;
    }

    @Override
    public Map<ScreenID, Screen> getScreens() {
        return screens;
    }

    private Map<ScreenID, Screen> createScreens(ScenarioID scenario) {
        final Map<ScreenID, Screen> screens = new HashMap<>();
        for (ScreenID screenID : scenario.screens()) {
            List<Block> blocks = new ArrayList<>();
            for (BlockID blockId : screenID.blocks()) {
                blocks.add(createBlock(screenID, blockId));
            }
            Screen screen = new Screen(screenID, blocks);
            screens.put(screenID, screen);
        }
        return screens;
    }

    private Block createBlock(ScreenID screenID, BlockID blockId) {
        List<Element> fields = new ArrayList<>();
        for (ElementID child : blockId.childs()) {
            switch (child.elementType()) {
                case FIELD:
                    fields.add(createField(screenID, (FieldID) child));
                    break;
                case GROUP:
                    fields.add(createGroup(screenID, (GroupID) child));
                    break;
            }
        }
        Block block = new Block(clientFactory.getLabelFactory().get(blockId), blockId);
        block.add(fields);
        return block;
    }

    private Group createGroup(ScreenID screenID, GroupID groupID){
        ArrayList<Field> fields = new ArrayList<>();
        for (FieldID fieldId : groupID.fields()) {
            fields.add(createField(screenID, fieldId));
        }
        return new Group(groupID, fields);
    }
    
    private Field createField(ScreenID screenId, FieldID fieldId) {
        Field field = new Field(fieldId, clientFactory.getLabelFactory().get(fieldId),//
                        clientFactory.getHelpFactory().get(fieldId),//
                        clientFactory.getPlaceholderFactory().get(fieldId),//
                        clientFactory.getRendererFactory().get(fieldId));
        FieldInitializer initializer = clientFactory.getInitializerFactory().get(fieldId);
        if (initializer != null) {
            final FieldEvent event = newEvent(screenId, EventProperty.SCREEN);
            try {
                initializer.init(field, event);
            } finally {
                releaseSourceEvent();
            }
        }
        return field;
    }

    @Override
    public boolean isVisible(ElementID elementID, FieldEvent event) {
        if (elementID.elementType() == ElementType.FIELD) {
            FieldID fieldId = (FieldID) elementID;
            return clientFactory.getBehaviourFactory().get(fieldId).isVisible(fieldId, getContext(), event);
        } else {
            for (FieldID fieldID : elementID.fields()) {
                if (!isVisible(fieldID, event)) {
                    return false;
                }
            }
            return true;
        }
    }

    @Override
    public void updateField(Field field, FieldEvent event) {
        // validate & update context if visible
        if (isVisible(field.getFieldID(), event)) {
            updateContext(field, event);
            validate(field, event);
        }

        // update field dependencies
        final List<FieldID> fieldDeps = asList(clientFactory.getDependencyFactory().get(field.getFieldID()));
        final Set<FieldID> deps = new HashSet<FieldID>(fieldDeps);
        if (!deps.isEmpty()) {
            event.setSourceProperty(EventProperty.DEPENDENCIES);
            for (Screen screen : screens.values()) {
                for (Field otherField : screen.fields()) {
                    if (deps.contains(otherField.getFieldID())) {
                        updateBehaviour(otherField, event);
                    }
                }
            }
        }

        // validate field dependencies already set
        for (FieldID dep : deps) {
            Field depField = currentScreen.getField(dep);
            if (depField != null && (depField.getState() == SET || depField.getState() == ERROR)) {
                validate(depField, event);
            }
        }
    }

    @Override
    public void displayScreen(Screen screen) {
        currentScreen = screen;
        final FieldEvent event = newEvent(screen.getScreenID(), EventProperty.SCREEN);
        try {
            for (FieldID fieldId : screen.getScreenID().fields()) {
                if (isVisible(fieldId, event)) {
                    final Field field = getField(screen.getScreenID(), fieldId);
                    if (field != null) {
                        updateFromContext(field, event);
                    }
                }
            }
            for (Field field : screen.fields()) {
                updateBehaviour(field, event);
            }
        } finally {
            releaseSourceEvent();
        }
    }

    private void updateBehaviour(Field field, FieldEvent event) {
        clientFactory.getBehaviourFactory().get(field.getFieldID()).updateValue(field, context, event);
        boolean visibleBeforeUpdate = field.getState() != FieldState.HIDDEN;
        boolean visibleAfterUpdate = isVisible(field.getFieldID(), event);
        if (!visibleBeforeUpdate && visibleAfterUpdate) {
            // update field using context, if field became visible
            updateFromContext(field, event);
            field.setState(FieldState.DEFAULT, null);
        } else if (!visibleAfterUpdate) {
            final FieldEditor editor = clientFactory.getEditorFactory().get(field.getFieldID());
            if (editor != null) {
                // reset field if not visible anymore
                editor.reset(field, context, event);
            }
            field.setState(FieldState.HIDDEN, null);
        }
    }

    @Override
    public final void updateFromContext(Field field, FieldEvent event) {
        final FieldEditor editor = clientFactory.getEditorFactory().get(field.getFieldID());
        if (editor == null)
            return;
        editor.updateFromContext(field, context, event);
    }

    @Override
    public final void updateContext(Field field, FieldEvent event) {
        final FieldEditor editor = clientFactory.getEditorFactory().get(field.getFieldID());
        if (editor == null)
            return;
        editor.updateContext(field, getContext(), event);
    }

    @Override
    public final FieldValidation validate(Field field, FieldEvent event) {
        final FieldEditor editor = clientFactory.getEditorFactory().get(field.getFieldID());

        // skip field if not visible or no visible field associated
        if (!isVisible(field.getFieldID(), event) || editor == null) {
            return valid(field);
        }

        // validate field
        final FieldValidation validation = editor.validate(field, getContext(), event);

        // update field state according to validation result
        final Field validated = getField(getScreen(field).getScreenID(), validation.getFieldID());
        validated.setState(!validation.isValid() ? ERROR : SET, validation); // update field state
        return validation;
    }

    private Field getField(ScreenID screenID, FieldID fieldID) {
        for (Screen screen : screens.values()) {
            if (screen.getScreenID() == screenID) {
                for (Field field : screen.fields()) {
                    if (field.getFieldID() == fieldID) {
                        return field;
                    }
                }
            }
        }
        return null;
    }

    private Screen getScreen(Field field) {
        for (Screen screen : screens.values()) {
            if (screen.fields().contains(field)) {
                return screen;
            }
        }
        return null;
    }

    @Override
    public void submit(Block block) {
        final FieldEvent event = newEvent(block.getBlockID(), EventProperty.BLOCK);
        try {
            for (Field field : block.fields())
                updateField(field, event);
        } finally {
            releaseSourceEvent();
        }
    }

}
