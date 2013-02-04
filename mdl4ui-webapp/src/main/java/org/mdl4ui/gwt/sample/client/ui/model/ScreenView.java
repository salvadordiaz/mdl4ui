package org.mdl4ui.gwt.sample.client.ui.model;

import static org.mdl4ui.fields.model.event.SimpleEventBus.EVENT_BUS;

import org.mdl4ui.fields.model.Block;
import org.mdl4ui.fields.model.Screen;
import org.mdl4ui.fields.model.event.ExpandBlockEvent;

import com.github.gwtbootstrap.client.ui.Column;
import com.github.gwtbootstrap.client.ui.Container;
import com.github.gwtbootstrap.client.ui.Row;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

public class ScreenView implements IsWidget {

    private final Container container = new Container();

    public ScreenView(final Screen screen) {
        for (Block block : screen.getBlocks()) {
            Row row = new Row();
            container.add(row);

            Column column = new Column(6, 3);
            row.add(column);

            BlockView blockView = new BlockView(block);
            column.add(blockView);
        }
        Block firstBlock = screen.getBlocks().get(0);
        EVENT_BUS.publish(new ExpandBlockEvent(firstBlock));
    }

    @Override
    public Widget asWidget() {
        return container;
    }
}