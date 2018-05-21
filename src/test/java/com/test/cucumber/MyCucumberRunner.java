package com.test.cucumber;

import cucumber.api.event.TestRunFinished;
import cucumber.api.formatter.Formatter;
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
import org.junit.runner.Description;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.ParentRunner;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.Statement;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class MyCucumberRunner extends ParentRunner<FeatureRunner> {

    private final JUnitReporter jUnitReporter;
    private final List<FeatureRunner> children = new ArrayList();
    private final Runtime runtime;
    private final Formatter formatter;

    public MyCucumberRunner(Class clazz) throws InitializationError, IOException {
        super(clazz);
//        System.out.println("Generate Feature File ----- ");
//        createFeatureFile("src/test/resources/featureTemplates/featureTemplate.feature", "src/test/resources/features/dynamicFeatureTest.feature");
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

        while (var2.hasNext()) {
            CucumberFeature cucumberFeature = (CucumberFeature) var2.next();
            FeatureRunner featureRunner = new FeatureRunner(cucumberFeature, this.runtime, this.jUnitReporter);
            if (!featureRunner.isEmpty()) {
                this.children.add(featureRunner);
            }
        }

    }

    private void createFeatureFile(String templateFileLocation, String featureFileLocation) {

        Path path = Paths.get(templateFileLocation);
        Path path2 = Paths.get(featureFileLocation);
        Charset charset = StandardCharsets.UTF_8;

        String content = null;
        try {
            content = new String(Files.readAllBytes(path), charset);
        } catch (IOException e) {
            e.printStackTrace();
        }
        content = content.replaceAll("#EXAMPLES1", "    Examples:\n" +
                "      | start | eat | left |\n" +
                "      |  12   |  5  |  7   |\n" +
                "      |  20   |  5  |  15  |");
        try {
            Files.write(path2, content.getBytes(charset));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public List<FeatureRunner> getChildren() {
        return this.children;
    }

    protected Description describeChild(FeatureRunner child) {
        return child.getDescription();
    }

    protected void runChild(FeatureRunner child, RunNotifier notifier) {
        child.run(notifier);
    }

    protected Statement childrenInvoker(RunNotifier notifier) {
        System.out.println("Generate Feature File ----- ");
        createFeatureFile("src/test/resources/featureTemplates/featureTemplate.feature", "src/test/resources/features/dynamicFeatureTest.feature");
        final Statement features = super.childrenInvoker(notifier);
        return new Statement() {
            public void evaluate() throws Throwable {
                features.evaluate();
                MyCucumberRunner.this.runtime.getEventBus().send(new TestRunFinished(MyCucumberRunner.this.runtime.getEventBus().getTime()));
            }
        };
    }

}
