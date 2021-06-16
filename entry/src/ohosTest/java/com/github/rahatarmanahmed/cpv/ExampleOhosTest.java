package com.github.rahatarmanahmed.cpv;

import static org.junit.Assert.assertEquals;

import ohos.aafwk.ability.delegation.AbilityDelegatorRegistry;

import org.junit.Test;

public class ExampleOhosTest {
    @Test
    public void testBundleName() {
        final String actualBundleName = AbilityDelegatorRegistry.getArguments().getTestBundleName();
        assertEquals("com.github.rahatarmanahmed.cpv", actualBundleName);
    }
}