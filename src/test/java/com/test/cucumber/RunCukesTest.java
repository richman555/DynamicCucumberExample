package com.test.cucumber;


import cucumber.api.CucumberOptions;
import org.junit.runner.RunWith;

@RunWith(MyCucumberRunner.class)
@CucumberOptions(features = {"classpath:features"}, plugin = {"pretty", "html:target/cucumber-html-report"})
public class RunCukesTest {


}
