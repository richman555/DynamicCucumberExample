package com.test.cucumber;

import cucumber.api.formatter.Formatter;
import cucumber.api.junit.Cucumber;
import cucumber.runtime.ClassFinder;
import cucumber.runtime.Runtime;
import cucumber.runtime.RuntimeOptions;
import cucumber.runtime.RuntimeOptionsFactory;
import cucumber.runtime.io.MultiLoader;
import cucumber.runtime.io.ResourceLoader;
import cucumber.runtime.io.ResourceLoaderClassFinder;
import cucumber.runtime.junit.Assertions;
import cucumber.runtime.junit.FeatureRunner;
import cucumber.runtime.junit.JUnitOptions;
import cucumber.runtime.junit.JUnitReporter;
import cucumber.runtime.model.CucumberFeature;
import org.junit.runners.model.InitializationError;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class MyCucumberRunner extends Cucumber {

    private final JUnitReporter jUnitReporter;
    private final List<FeatureRunner> children = new ArrayList();
    private final Runtime runtime;
    private final Formatter formatter;

    public MyCucumberRunner(Class clazz) throws InitializationError, IOException {

        super(clazz);
        System.out.println("Generate Feature File ----- ");
        ClassLoader classLoader = clazz.getClassLoader();
        Assertions.assertNoCucumberAnnotatedMethods(clazz);
        RuntimeOptionsFactory runtimeOptionsFactory = new RuntimeOptionsFactory(clazz);
        RuntimeOptions runtimeOptions = runtimeOptionsFactory.create();
        ResourceLoader resourceLoader = new MultiLoader(classLoader);
        ClassFinder classFinder = new ResourceLoaderClassFinder(resourceLoader, classLoader);
        this.runtime = new Runtime(resourceLoader, classFinder, classLoader, runtimeOptions);
        this.formatter = runtimeOptions.formatter(classLoader);
        JUnitOptions junitOptions = new JUnitOptions(runtimeOptions.getJunitOptions());
        List<CucumberFeature> cucumberFeatures = runtimeOptions.cucumberFeatures(resourceLoader, this.runtime.getEventBus());
        this.jUnitReporter = new JUnitReporter(this.runtime.getEventBus(), runtimeOptions.isStrict(), junitOptions);
        this.addChildren(cucumberFeatures);
    }

    private void addChildren(List<CucumberFeature> cucumberFeatures) throws InitializationError {
        Iterator var2 = cucumberFeatures.iterator();

        while(var2.hasNext()) {
            CucumberFeature cucumberFeature = (CucumberFeature)var2.next();
            FeatureRunner featureRunner = new FeatureRunner(cucumberFeature, this.runtime, this.jUnitReporter);
            if (!featureRunner.isEmpty()) {
                this.children.add(featureRunner);
            }
        }

    }

}