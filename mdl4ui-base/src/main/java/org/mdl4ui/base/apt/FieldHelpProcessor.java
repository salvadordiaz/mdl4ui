/*
 * Copyright (C) by Courtanet, All Rights Reserved.
 */
package org.mdl4ui.base.apt;

import static javax.lang.model.SourceVersion.RELEASE_6;

import java.lang.annotation.Annotation;

import javax.annotation.processing.Processor;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;

import org.mdl4ui.base.injection.InjectHelp;

import com.google.auto.service.AutoService;

@SupportedAnnotationTypes(value = "org.mdl4ui.base.injection.InjectHelp")
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@AutoService(Processor.class)
public class FieldHelpProcessor extends FieldMessageProcessor {

    @Override
    protected Class<? extends Annotation> getAnnotationClass() {
        return InjectHelp.class;
    }

    @Override
    protected String getTemplate() {
        return "FieldHelpFactory.template";
    }

    @Override
    public String simpleClassName(String... prefix) {
        return prefix[0] + "FieldHelpFactory";
    }
}
