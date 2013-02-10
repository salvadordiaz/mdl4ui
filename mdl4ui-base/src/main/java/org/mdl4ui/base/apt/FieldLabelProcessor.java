/*
 * Copyright (C) by Courtanet, All Rights Reserved.
 */
package org.mdl4ui.base.apt;

import static java.text.DateFormat.SHORT;
import static javax.lang.model.SourceVersion.RELEASE_6;
import static javax.lang.model.element.ElementKind.ANNOTATION_TYPE;
import static javax.lang.model.element.ElementKind.METHOD;

import java.io.IOException;
import java.io.Writer;
import java.text.DateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.SimpleAnnotationValueVisitor6;
import javax.tools.Diagnostic.Kind;
import javax.tools.JavaFileObject;

import org.apache.commons.io.IOUtils;
import org.ez18n.apt.macro.MacroProcessor;
import org.ez18n.apt.macro.PropertyParsingException;
import org.mdl4ui.base.injection.InjectLabel;
import org.mdl4ui.base.model.BlockID;
import org.mdl4ui.base.model.ElementID;
import org.mdl4ui.base.model.FieldID;
import org.mdl4ui.base.model.GroupID;
import org.mdl4ui.base.model.ScreenID;

@SupportedAnnotationTypes(value = "org.mdl4ui.base.injection.InjectLabel")
@SupportedSourceVersion(RELEASE_6)
public class FieldLabelProcessor extends FieldProcessor {

    @Override
    protected boolean isGwtFactory() {
        return true;
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {

        if (roundEnv.processingOver()) {
            return true;
        }
        final Map<ExecutableElement, Set<FieldID>> fields = new HashMap<ExecutableElement, Set<FieldID>>();
        final Map<ExecutableElement, Set<GroupID>> groups = new HashMap<ExecutableElement, Set<GroupID>>();
        final Map<ExecutableElement, Set<BlockID>> blocks = new HashMap<ExecutableElement, Set<BlockID>>();
        final Map<ExecutableElement, Set<ScreenID>> screens = new HashMap<ExecutableElement, Set<ScreenID>>();

        try {
            for (Element element : roundEnv.getElementsAnnotatedWith(InjectLabel.class)) {
                if (element.getKind() != ANNOTATION_TYPE) {
                    continue;
                }
                final TypeElement annotation = (TypeElement) element;

                // retrieve all annotations extending @InjectLabel
                for (Element elementAnnoted : roundEnv.getElementsAnnotatedWith(annotation)) {
                    if (elementAnnoted.getKind() != METHOD) {
                        continue;
                    }
                    final ExecutableElement methodAnnoted = (ExecutableElement) elementAnnoted;

                    // retrieve all annotated elements
                    for (AnnotationMirror injectLabel : elementAnnoted.getAnnotationMirrors()) {
                        Map<? extends ExecutableElement, ? extends AnnotationValue> injectLabelValues = injectLabel
                                        .getElementValues();
                        for (ExecutableElement injectLabelMethod : injectLabelValues.keySet()) {
                            final AnnotationValue injectLabelValue = injectLabelValues.get(injectLabelMethod);

                            final OnElementVisitor visitor = new OnElementVisitor();
                            final OnElementVisitorContext visitorContext = new OnElementVisitorContext();

                            // retrieve @OnElement value
                            // FIXME check if annotation extends @OnElement
                            if (!(injectLabelValue.getValue() instanceof AnnotationMirror)) {
                                continue;
                            }

                            final AnnotationMirror onElement = (AnnotationMirror) injectLabelValue.getValue();
                            final Map<? extends ExecutableElement, ? extends AnnotationValue> onElementValues = onElement
                                            .getElementValues();
                            for (ExecutableElement onElementMethod : onElementValues.keySet()) {
                                final AnnotationValue onElementValue = onElementValues.get(onElementMethod);
                                onElementValue.accept(visitor, visitorContext);
                            }

                            final Set<ScreenID> screensIds = new HashSet<ScreenID>();
                            final Set<BlockID> blockIds = new HashSet<BlockID>();
                            final Set<GroupID> groupIds = new HashSet<GroupID>();
                            final Set<FieldID> fieldIds = new HashSet<FieldID>();
                            for (Element elementFound : visitorContext.elementIds) {
                                final ElementID elementID = getElementID(elementFound);
                                switch (elementID.elementType()) {
                                    case SCREEN:
                                        screensIds.add((ScreenID) elementID);
                                        break;
                                    case BLOCK:
                                        blockIds.add((BlockID) elementID);
                                        break;
                                    case GROUP:
                                        groupIds.add((GroupID) elementID);
                                        break;
                                    case FIELD:
                                        fieldIds.add((FieldID) elementID);
                                        break;
                                }
                            }

                            processingEnv.getMessager().printMessage(Kind.NOTE, injectLabelMethod.toString());
                            insertAndCheckUnicity(this, fields, methodAnnoted, fieldIds, processingEnv);
                            insertAndCheckUnicity(this, groups, methodAnnoted, groupIds, processingEnv);
                            insertAndCheckUnicity(this, blocks, methodAnnoted, blockIds, processingEnv);
                            insertAndCheckUnicity(this, screens, methodAnnoted, screensIds, processingEnv);
                        }
                    }
                }

                final FactoryName[] factories = new FactoryName[] { new GwtFactoryName(), new BundleFactoryName() };
                for (FactoryName factoryName : factories) {
                    // FIXME package name should be computed from ElementID package
                    final String className = qualifiedClassName("org.mdl4ui", factoryName.prefix());
                    processingEnv.getMessager().printMessage(Kind.NOTE, className);
                    try {
                        final JavaFileObject file = processingEnv.getFiler().createSourceFile(className);
                        final Writer writer = file.openWriter();
                        writer.write(getCode(fields, groups, blocks, screens, true, factoryName));
                        writer.close();
                    } catch (IOException e) {
                        processingEnv.getMessager().printMessage(Kind.ERROR, e.getMessage());
                    }
                }
            }
            return false;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String simpleClassName(String... prefix) {
        return prefix[0] + "FieldLabelFactory";
    }

    private ElementID getElementID(Element element) throws Exception {
        final String elementName = element.getSimpleName().toString();
        Class<?> enumClass = Class.forName(((TypeElement) element.getEnclosingElement()).getQualifiedName().toString());
        List<ElementID> values = Arrays.asList((ElementID[]) enumClass.getMethod("values").invoke(null));
        for (ElementID elementID : values) {
            if (elementName.equals(elementID.toString())) {
                return elementID;
            }
        }
        return null;
    }

    private String getCode(Map<ExecutableElement, Set<FieldID>> fields, Map<ExecutableElement, Set<GroupID>> groups,
                    Map<ExecutableElement, Set<BlockID>> blocks, Map<ExecutableElement, Set<ScreenID>> screens,
                    boolean gwt, FactoryName factoryName) throws IOException {
        final String template = IOUtils.toString(getClass().getResourceAsStream("FieldLabelFactory.template"));
        final Set<ExecutableElement> filterElts = new HashSet<ExecutableElement>(fields.keySet());
        filterElts.addAll(groups.keySet());
        filterElts.addAll(blocks.keySet());
        filterElts.addAll(screens.keySet());

        final StringBuilder labelInit = new StringBuilder();
        labelInit.append(getLabelInit(filterElts, fields, "${map.name.elements}", factoryName));
        labelInit.append(getLabelInit(filterElts, groups, "${map.name.elements}", factoryName));
        labelInit.append(getLabelInit(filterElts, blocks, "${map.name.elements}", factoryName));
        labelInit.append(getLabelInit(filterElts, screens, "${map.name.elements}", factoryName));

        final Map<String, String> conf = new HashMap<String, String>();
        conf.put("process.class", getClass().getName());
        conf.put("process.date", DateFormat.getDateTimeInstance(SHORT, SHORT).format(new Date()));
        conf.put("package.name", "org.mdl4ui"); // FIXME hardcoded package
        conf.put("target.class.name", simpleClassName(factoryName.prefix()));
        conf.put("map.name.elements", "elements");
        conf.put("map.name.tags", "tags");
        conf.put("class.init", labelInit.toString());
        try {
            return MacroProcessor.replaceProperties(template, conf, "#no_value#");
        } catch (PropertyParsingException e) {
            throw new RuntimeException(e);
        }
    }

    private static final class OnElementVisitor extends SimpleAnnotationValueVisitor6<Void, OnElementVisitorContext> {

        @Override
        public Void visitEnumConstant(VariableElement c, OnElementVisitorContext p) {
            p.elementIds.add(c);
            return null;
        }

        @Override
        public Void visitArray(List<? extends AnnotationValue> vals, OnElementVisitorContext p) {
            for (AnnotationValue annotationValue : vals) {
                visit(annotationValue, p);
            }
            return null;
        }
    }

    private static final class OnElementVisitorContext {
        private final Set<Element> elementIds = new HashSet<Element>();
    }
}