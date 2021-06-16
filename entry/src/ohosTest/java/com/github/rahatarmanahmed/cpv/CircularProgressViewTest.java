package com.github.rahatarmanahmed.cpv;

import static junit.framework.TestCase.assertFalse;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import ohos.aafwk.ability.delegation.AbilityDelegatorRegistry;
import ohos.agp.components.Attr;
import ohos.agp.components.AttrSet;
import ohos.agp.utils.Color;
import ohos.app.Context;

import org.junit.Test;

import java.util.Optional;

public class CircularProgressViewTest {
    private AttrSet attrSet = new AttrSet() {
        @Override
        public Optional<String> getStyle() {
            return Optional.empty();
        }

        @Override
        public int getLength() {
            return 0;
        }

        @Override
        public Optional<Attr> getAttr(int i) {
            return Optional.empty();
        }

        @Override
        public Optional<Attr> getAttr(String s) {
            return Optional.empty();
        }
    };

    private Context context = AbilityDelegatorRegistry.getAbilityDelegator().getAppContext();

    private CircularProgressView mCircularProgressView = new CircularProgressView(context, attrSet);

    @Test
    public void testSetIndeterminate() {
        mCircularProgressView.setIndeterminate(true);
        assertTrue(mCircularProgressView.isIndeterminate());
    }

    @Test
    public void testSetDeterminate() {
        mCircularProgressView.setIndeterminate(false);
        assertFalse(mCircularProgressView.isIndeterminate());
    }

    @Test
    public void testSetThickness() {
        mCircularProgressView.setThickness(25);
        assertEquals(25, mCircularProgressView.getThickness());
    }

    @Test
    public void testSetColor() {
        mCircularProgressView.setColor(Color.GREEN.getValue());
        assertEquals(Color.GREEN.getValue(), mCircularProgressView.getColor());
    }
}


