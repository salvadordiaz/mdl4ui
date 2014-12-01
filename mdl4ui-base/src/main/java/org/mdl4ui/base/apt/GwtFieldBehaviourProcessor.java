/*
 * Copyright (C) by Courtanet, All Rights Reserved.
 */
package org.mdl4ui.base.apt;

import static javax.lang.model.SourceVersion.RELEASE_6;

import javax.annotation.processing.Processor;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;

import com.google.auto.service.AutoService;

@SupportedAnnotationTypes(value = "org.mdl4ui.base.injection.InjectBehaviour")
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@AutoService(Processor.class)
public class GwtFieldBehaviourProcessor extends FieldBehaviourProcessor {
    @Override
    protected boolean isGwtFactory() {
        return true;
    }

    @Override
    public String simpleClassName(String... prefix) {
        return "GwtFieldBehaviourFactory";
    }
}
