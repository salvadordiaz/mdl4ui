package org.mdl4ui.gwt.model.client.ui;

import static org.mdl4ui.fields.model.event.FieldEvent.newEvent;
import static org.mdl4ui.fields.model.event.FieldEvent.releaseSourceEvent;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mdl4ui.base.model.BlockID;
import org.mdl4ui.base.model.GroupID;
import org.mdl4ui.base.model.ScenarioID;
import org.mdl4ui.base.model.ScreenID;
import org.mdl4ui.fields.model.*;
import org.mdl4ui.fields.model.event.EventProperty;
import org.mdl4ui.fields.model.event.FieldEvent;
import org.mdl4ui.gwt.model.client.factory.GwtScreenFactory;

import com.github.gwtbootstrap.client.ui.Container;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.AttachEvent;
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.user.client.ui.Widget;

public class DefaultWizardView implements WizardView {

    private final Container container = new Container();
    private final Map<ScreenID, ScreenView> screenViews = new HashMap<>();
    private final ScenarioID scenario;

    @Inject
    public DefaultWizardView(final Wizard wizard, GwtScreenFactory screenFactory, final ScenarioID scenario) {
        this.scenario = scenario;
        Map<ScreenID, Screen> screens = wizard.getScreens();
        for (final ScreenID screenID : screens.keySet()) {
            final ScreenView screenView = screenFactory.getView(screens.get(screenID));

            for (final Field field : screenView.getScreen().fields()) {
                @SuppressWarnings({ "rawtypes", "unchecked" })
                HasValueChangeHandlers<Object> hasChangeHandler = (HasValueChangeHandlers) field.getComponent();
                hasChangeHandler.addValueChangeHandler(new ValueChangeHandler<Object>() {
                    @Override
                    public void onValueChange(ValueChangeEvent<Object> event) {
                        updateField(wizard, screenView, field);
                    }
                });
            }

            for (final BlockView blockView : screenView.blocks()) {
                blockView.getSubmit().addClickHandler(new ClickHandler() {
                    @Override
                    public void onClick(ClickEvent event) {
                        submitBlock(wizard, screenID, screenView, blockView);
                    }
                });

                blockView.getModify().addClickHandler(new ClickHandler() {
                    @Override
                    public void onClick(ClickEvent event) {
                        modifyBlock(screenView, blockView);
                    }
                });
            }

            this.screenViews.put(screenID, screenView);
        }

        container.addAttachHandler(new AttachEvent.Handler() {
            @Override
            public void onAttachOrDetach(AttachEvent attachEvent) {
                displayScreen(wizard, scenario.screens().get(0));
            }
        });
    }

    @Override
    public Widget asWidget() {
        return container;
    }

    private void updateField(final Wizard wizard, final ScreenView screenView, final Field field) {
        final FieldEvent event = newEvent(field.getFieldID(), EventProperty.FIELD);
        try {
            // udpate field
            wizard.updateField(field, event);

            // update owning group

            for (FieldView fieldView : screenView.fields()) {
                fieldView.updateField();
            }
            for (GroupView groupView : screenView.groups()) {
                GroupID groupID = groupView.getGroup().getGroupID();
                groupView.asWidget().setVisible(wizard.isVisible(groupID, event));
            }
        } finally {
            releaseSourceEvent();
        }

    }

    private void submitBlock(final Wizard wizard, final ScreenID screenID, final ScreenView screenView,
                    final BlockView blockView) {
        Block block = blockView.getBlock();
        wizard.submit(block);
        for (FieldView fieldView : screenView.fields()) {
            fieldView.updateField();
        }
        if (block.isValid()) {
            blockView.collapse();
            blockView.getModify().setVisible(true);
            BlockID nextBlock = screenID.nextBlock(block.getBlockID());
            if (nextBlock != null) {
                for (BlockView screenBlock : screenView.blocks()) {
                    if (screenBlock.getBlock().getBlockID() == nextBlock) {
                        screenBlock.expand();
                    }
                }
            } else {
                submitScreen(wizard, screenID);
            }
        }
    }

    private void submitScreen(final Wizard wizard, final ScreenID screenID) {
        ScreenID nextScreen = scenario.nextScreen(screenID);
        if (nextScreen != null) {
            displayScreen(wizard, nextScreen);
        }
    }

    private void modifyBlock(ScreenView screenView, BlockView blockView) {
        final List<BlockView> blocks = screenView.blocks();
        boolean canBeModified = true;
        for (BlockView otherBlock : blocks) {
            if (blockView == otherBlock) {
                otherBlock.expand();
                canBeModified = false;
            } else {
                otherBlock.collapse();
                otherBlock.getModify().setVisible(canBeModified);
            }
        }
    }

    public void displayScreen(Wizard wizard, ScreenID screenID) {
        container.clear();
        ScreenView screenView = screenViews.get(screenID);
        if (screenView == null) {
            throw new IllegalArgumentException("unknow screen id : " + screenID);
        }

        screenView.onDisplay(wizard.getContext());
        container.add(screenView);

        wizard.displayScreen(screenView.getScreen());
        for (FieldView fieldView : screenView.fields()) {
            fieldView.updateField();
        }

        List<BlockView> blocks = screenView.blocks();
        if (!blocks.isEmpty()) {
            blocks.get(0).expand();
            blocks.remove(0);
            for (BlockView blockView : blocks) {
                blockView.collapse();
                blockView.getModify().setVisible(false);
            }
        }
    }

}
