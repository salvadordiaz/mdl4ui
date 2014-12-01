/*
 * Copyright (C) by Courtanet, All Rights Reserved.
 */
package org.ez18n.apt.processor;

import static javax.lang.model.SourceVersion.RELEASE_6;

import javax.annotation.processing.Processor;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;

import com.google.auto.service.AutoService;

@SupportedAnnotationTypes(value = "org.ez18n.MessageBundle")
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@AutoService(Processor.class)
public class TestDesktopBundleProcessor extends TestBundleProcessor {

    @Override
    protected String getBundleClassName(TypeElement typeElement, boolean fqcn) {
        final String simpleName = typeElement.getSimpleName().toString();
        final int resourceIndex = simpleName.indexOf("Resources");
        final String shortName = resourceIndex > 0 ? simpleName.substring(0, resourceIndex) : simpleName;
        return (fqcn ? typeElement.getEnclosingElement().toString() + "." : "") + shortName + "DesktopBundle";
    }

    @Override
    protected String getBundleType() {
        return "Desktop";
    }
}
