package com.test.cucumber;


import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(features = {"classpath:features"}, plugin = {"pretty", "html:target/cucumber-html-report"})
public class RunCukesTest {



}
