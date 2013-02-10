package org.mdl4ui.fields.sample.i18n;

import org.ez18n.Message;
import org.ez18n.MessageBundle;
import org.mdl4ui.fields.sample.InjectSampleLabel;
import org.mdl4ui.fields.sample.OnSample;
import org.mdl4ui.ui.sample.EFieldSample;

@MessageBundle
public interface SampleMessages {

    @InjectSampleLabel(@OnSample(fields = EFieldSample.TEXTBOX))
    @Message("Please enter some text")
    String texbox();
}