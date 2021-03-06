/**
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license agreements. See the NOTICE
 * file distributed with this work for additional information regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package org.ez18n.apt.processor;

import static java.text.DateFormat.SHORT;
import static org.ez18n.apt.macro.MacroProcessor.replaceProperties;
import static org.ez18n.apt.processor.DesktopMessagesProcessor.getDesktopMessagesClassName;

import javax.annotation.processing.Processor;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;
import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ez18n.apt.LabelTemplateMethod;
import org.ez18n.apt.TemplateLoader;
import org.ez18n.apt.macro.PropertyParsingException;

import com.google.auto.service.AutoService;

@SupportedAnnotationTypes(value = "org.ez18n.MessageBundle")
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@AutoService(Processor.class)
public final class MessagesFactoryProcessor extends LabelBundleProcessor {
    private final String template;

    public MessagesFactoryProcessor() {
        try {
            template = TemplateLoader.load("MessagesFactory.template");
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    protected String getTargetSimpleName(TypeElement typeElement) {
        return getMessagesFactoryClassName(typeElement, false);
    }

    static final String getMessagesFactoryClassName(TypeElement typeElement, boolean fqcn) {
        final String simpleName = typeElement.getSimpleName().toString();
        final int resourceIndex = simpleName.indexOf("Resources");
        final String shortName = resourceIndex > 0 ? simpleName.substring(0, resourceIndex) : simpleName;
        final String suffix = shortName.endsWith("Messages") ? "Factory" : "MessagesFactory";
        return (fqcn ? typeElement.getEnclosingElement().toString() + "." : "") + shortName + suffix;
    }

    @Override
    protected String getCode(TypeElement bundleType, List<LabelTemplateMethod> methods) {
        final String code;
        final Map<String, String> conf = new HashMap<>();
        conf.put("process.class", getClass().getName());
        conf.put("process.date", DateFormat.getDateTimeInstance(SHORT, SHORT).format(new Date()));
        conf.put("source.class.name.camel", toCamelCase(bundleType));
        conf.put("target.class.name", getTargetSimpleName(bundleType));
        conf.put("source.class.name", bundleType.getSimpleName().toString());
        conf.put("package.name", bundleType.getEnclosingElement().toString());
        conf.put("desktop.messages.class.name", getDesktopMessagesClassName(bundleType, true));
        try {
            code = replaceProperties(template, conf, NO_VALUE);
        } catch (PropertyParsingException e) {
            throw new RuntimeException(e);
        }
        return code;
    }
}
