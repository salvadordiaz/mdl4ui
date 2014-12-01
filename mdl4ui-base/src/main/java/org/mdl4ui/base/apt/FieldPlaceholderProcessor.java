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

import org.mdl4ui.base.injection.InjectPlaceholder;

import com.google.auto.service.AutoService;

@SupportedAnnotationTypes(value = "org.mdl4ui.base.injection.InjectPlaceholder")
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@AutoService(Processor.class)
public class FieldPlaceholderProcessor extends FieldMessageProcessor {

    @Override
    protected Class<? extends Annotation> getAnnotationClass() {
        return InjectPlaceholder.class;
    }

    @Override
    protected String getTemplate() {
        return "FieldPlaceholderFactory.template";
    }

    @Override
    public String simpleClassName(String... prefix) {
        return prefix[0] + "FieldPlaceholderFactory";
    }
}
