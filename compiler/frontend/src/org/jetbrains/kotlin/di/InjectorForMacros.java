/*
 * Copyright 2010-2015 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jetbrains.kotlin.di;

import com.intellij.openapi.project.Project;
import org.jetbrains.kotlin.descriptors.ModuleDescriptor;
import org.jetbrains.kotlin.builtins.KotlinBuiltIns;
import org.jetbrains.kotlin.platform.PlatformToKotlinClassMap;
import org.jetbrains.kotlin.types.expressions.ExpressionTypingServices;
import org.jetbrains.kotlin.types.expressions.ExpressionTypingComponents;
import org.jetbrains.kotlin.resolve.calls.CallResolver;
import org.jetbrains.kotlin.resolve.TypeResolver;
import org.jetbrains.kotlin.context.GlobalContext;
import org.jetbrains.kotlin.storage.StorageManager;
import org.jetbrains.kotlin.resolve.AdditionalCheckerProvider.DefaultProvider;
import org.jetbrains.kotlin.resolve.AnnotationResolver;
import org.jetbrains.kotlin.resolve.calls.CallExpressionResolver;
import org.jetbrains.kotlin.resolve.DescriptorResolver;
import org.jetbrains.kotlin.resolve.DelegatedPropertyResolver;
import org.jetbrains.kotlin.resolve.PartialBodyResolveProvider;
import org.jetbrains.kotlin.types.expressions.ControlStructureTypingUtils;
import org.jetbrains.kotlin.types.DynamicTypesSettings;
import org.jetbrains.kotlin.types.expressions.ExpressionTypingUtils;
import org.jetbrains.kotlin.types.expressions.ForLoopConventionsChecker;
import org.jetbrains.kotlin.types.reflect.ReflectionTypes;
import org.jetbrains.kotlin.resolve.calls.ArgumentTypeResolver;
import org.jetbrains.kotlin.resolve.calls.CallCompleter;
import org.jetbrains.kotlin.resolve.calls.CandidateResolver;
import org.jetbrains.kotlin.resolve.calls.tasks.TaskPrioritizer;
import org.jetbrains.kotlin.resolve.QualifiedExpressionResolver;
import org.jetbrains.kotlin.resolve.TypeResolver.FlexibleTypeCapabilitiesProvider;
import org.jetbrains.kotlin.context.LazinessToken;
import org.jetbrains.annotations.NotNull;
import javax.annotation.PreDestroy;

/* This file is generated by org.jetbrains.kotlin.generators.injectors.InjectorsPackage. DO NOT EDIT! */
@SuppressWarnings("all")
public class InjectorForMacros {

    private final Project project;
    private final ModuleDescriptor moduleDescriptor;
    private final KotlinBuiltIns kotlinBuiltIns;
    private final PlatformToKotlinClassMap platformToKotlinClassMap;
    private final ExpressionTypingServices expressionTypingServices;
    private final ExpressionTypingComponents expressionTypingComponents;
    private final CallResolver callResolver;
    private final TypeResolver typeResolver;
    private final GlobalContext globalContext;
    private final StorageManager storageManager;
    private final DefaultProvider defaultProvider;
    private final AnnotationResolver annotationResolver;
    private final CallExpressionResolver callExpressionResolver;
    private final DescriptorResolver descriptorResolver;
    private final DelegatedPropertyResolver delegatedPropertyResolver;
    private final PartialBodyResolveProvider partialBodyResolveProvider;
    private final ControlStructureTypingUtils controlStructureTypingUtils;
    private final DynamicTypesSettings dynamicTypesSettings;
    private final ExpressionTypingUtils expressionTypingUtils;
    private final ForLoopConventionsChecker forLoopConventionsChecker;
    private final ReflectionTypes reflectionTypes;
    private final ArgumentTypeResolver argumentTypeResolver;
    private final CallCompleter callCompleter;
    private final CandidateResolver candidateResolver;
    private final TaskPrioritizer taskPrioritizer;
    private final QualifiedExpressionResolver qualifiedExpressionResolver;
    private final FlexibleTypeCapabilitiesProvider flexibleTypeCapabilitiesProvider;
    private final LazinessToken lazinessToken;

    public InjectorForMacros(
        @NotNull Project project,
        @NotNull ModuleDescriptor moduleDescriptor
    ) {
        this.project = project;
        this.moduleDescriptor = moduleDescriptor;
        this.kotlinBuiltIns = moduleDescriptor.getBuiltIns();
        this.platformToKotlinClassMap = moduleDescriptor.getPlatformToKotlinClassMap();
        this.expressionTypingComponents = new ExpressionTypingComponents();
        this.expressionTypingServices = new ExpressionTypingServices(getExpressionTypingComponents());
        this.callResolver = new CallResolver();
        this.annotationResolver = new AnnotationResolver();
        this.qualifiedExpressionResolver = new QualifiedExpressionResolver();
        this.flexibleTypeCapabilitiesProvider = new FlexibleTypeCapabilitiesProvider();
        this.globalContext = org.jetbrains.kotlin.context.ContextPackage.GlobalContext();
        this.storageManager = globalContext.getStorageManager();
        this.lazinessToken = new LazinessToken();
        this.dynamicTypesSettings = new DynamicTypesSettings();
        this.typeResolver = new TypeResolver(annotationResolver, qualifiedExpressionResolver, moduleDescriptor, flexibleTypeCapabilitiesProvider, storageManager, lazinessToken, dynamicTypesSettings);
        this.defaultProvider = DefaultProvider.INSTANCE$;
        this.callExpressionResolver = new CallExpressionResolver();
        this.descriptorResolver = new DescriptorResolver();
        this.delegatedPropertyResolver = new DelegatedPropertyResolver();
        this.partialBodyResolveProvider = new PartialBodyResolveProvider();
        this.controlStructureTypingUtils = new ControlStructureTypingUtils(getExpressionTypingServices());
        this.expressionTypingUtils = new ExpressionTypingUtils(getExpressionTypingServices(), getCallResolver(), kotlinBuiltIns);
        this.forLoopConventionsChecker = new ForLoopConventionsChecker();
        this.reflectionTypes = new ReflectionTypes(moduleDescriptor);
        this.argumentTypeResolver = new ArgumentTypeResolver();
        this.candidateResolver = new CandidateResolver();
        this.callCompleter = new CallCompleter(argumentTypeResolver, candidateResolver);
        this.taskPrioritizer = new TaskPrioritizer(storageManager);

        this.expressionTypingServices.setAnnotationResolver(annotationResolver);
        this.expressionTypingServices.setBuiltIns(kotlinBuiltIns);
        this.expressionTypingServices.setCallExpressionResolver(callExpressionResolver);
        this.expressionTypingServices.setCallResolver(callResolver);
        this.expressionTypingServices.setDescriptorResolver(descriptorResolver);
        this.expressionTypingServices.setPartialBodyResolveProvider(partialBodyResolveProvider);
        this.expressionTypingServices.setProject(project);
        this.expressionTypingServices.setTypeResolver(typeResolver);

        this.expressionTypingComponents.setAdditionalCheckerProvider(defaultProvider);
        this.expressionTypingComponents.setBuiltIns(kotlinBuiltIns);
        this.expressionTypingComponents.setCallResolver(callResolver);
        this.expressionTypingComponents.setControlStructureTypingUtils(controlStructureTypingUtils);
        this.expressionTypingComponents.setDynamicTypesSettings(dynamicTypesSettings);
        this.expressionTypingComponents.setExpressionTypingServices(expressionTypingServices);
        this.expressionTypingComponents.setExpressionTypingUtils(expressionTypingUtils);
        this.expressionTypingComponents.setForLoopConventionsChecker(forLoopConventionsChecker);
        this.expressionTypingComponents.setGlobalContext(globalContext);
        this.expressionTypingComponents.setPlatformToKotlinClassMap(platformToKotlinClassMap);
        this.expressionTypingComponents.setReflectionTypes(reflectionTypes);

        this.callResolver.setArgumentTypeResolver(argumentTypeResolver);
        this.callResolver.setCallCompleter(callCompleter);
        this.callResolver.setCandidateResolver(candidateResolver);
        this.callResolver.setExpressionTypingServices(expressionTypingServices);
        this.callResolver.setTaskPrioritizer(taskPrioritizer);
        this.callResolver.setTypeResolver(typeResolver);

        annotationResolver.setCallResolver(callResolver);
        annotationResolver.setStorageManager(storageManager);
        annotationResolver.setTypeResolver(typeResolver);

        callExpressionResolver.setExpressionTypingServices(expressionTypingServices);

        descriptorResolver.setAnnotationResolver(annotationResolver);
        descriptorResolver.setBuiltIns(kotlinBuiltIns);
        descriptorResolver.setDelegatedPropertyResolver(delegatedPropertyResolver);
        descriptorResolver.setExpressionTypingServices(expressionTypingServices);
        descriptorResolver.setStorageManager(storageManager);
        descriptorResolver.setTypeResolver(typeResolver);

        delegatedPropertyResolver.setBuiltIns(kotlinBuiltIns);
        delegatedPropertyResolver.setCallResolver(callResolver);
        delegatedPropertyResolver.setExpressionTypingServices(expressionTypingServices);

        forLoopConventionsChecker.setBuiltIns(kotlinBuiltIns);
        forLoopConventionsChecker.setExpressionTypingServices(expressionTypingServices);
        forLoopConventionsChecker.setExpressionTypingUtils(expressionTypingUtils);
        forLoopConventionsChecker.setProject(project);

        argumentTypeResolver.setBuiltIns(kotlinBuiltIns);
        argumentTypeResolver.setExpressionTypingServices(expressionTypingServices);
        argumentTypeResolver.setTypeResolver(typeResolver);

        candidateResolver.setArgumentTypeResolver(argumentTypeResolver);

    }

    @PreDestroy
    public void destroy() {
    }

    public ExpressionTypingServices getExpressionTypingServices() {
        return this.expressionTypingServices;
    }

    public ExpressionTypingComponents getExpressionTypingComponents() {
        return this.expressionTypingComponents;
    }

    public CallResolver getCallResolver() {
        return this.callResolver;
    }

    public TypeResolver getTypeResolver() {
        return this.typeResolver;
    }

}
