package com.test.cucumber;

import cucumber.api.PendingException;
import cucumber.api.java8.En;

public class MyStepdefs implements En {
    public MyStepdefs() {


        Given("there are {int} cucumbers", (Integer int1) -> {
            // Write code here that turns the phrase above into concrete actions
            throw new PendingException();
        });

        When("I eat {int} cucumbers", (Integer int1) -> {
            // Write code here that turns the phrase above into concrete actions
            throw new PendingException();
        });

        Then("I should have {int} cucumbers", (Integer int1) -> {
            // Write code here that turns the phrase above into concrete actions
            throw new PendingException();
        });

    }
}

