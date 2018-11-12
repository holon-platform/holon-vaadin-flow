/*
 * Copyright 2016-2018 Axioma srl.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.holonplatform.vaadin.flow.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.jupiter.api.Test;

import com.holonplatform.core.internal.utils.ConversionUtils;
import com.holonplatform.core.property.NumericProperty;
import com.holonplatform.core.property.Property;
import com.holonplatform.core.property.PropertySet;
import com.holonplatform.core.property.StringProperty;
import com.holonplatform.core.property.VirtualProperty;
import com.holonplatform.vaadin.flow.components.Components;
import com.holonplatform.vaadin.flow.components.PropertyListing;
import com.holonplatform.vaadin.flow.components.builders.PropertyListingBuilder;
import com.holonplatform.vaadin.flow.components.support.Unit;
import com.holonplatform.vaadin.flow.test.util.ComponentTestUtils;
import com.vaadin.flow.component.ComponentUtil;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;

public class TestPropertyListing {

	private static final NumericProperty<Long> ID = NumericProperty.longType("id");
	private static final StringProperty NAME = StringProperty.create("name");
	private static final VirtualProperty<String> VIRTUAL = VirtualProperty.create(String.class,
			pb -> pb.containsValue(NAME) ? "[" + pb.getValue(NAME) + "]" : null);

	private static final PropertySet<?> SET = PropertySet.builderOf(ID, NAME, VIRTUAL).identifier(ID).build();

	@Test
	public void testBuilders() {

		PropertyListingBuilder builder = PropertyListing.builder(SET);
		assertNotNull(builder);
		PropertyListing listing = builder.build();
		assertNotNull(listing);

		builder = PropertyListing.builder(ID, NAME, VIRTUAL);
		assertNotNull(builder);
		listing = builder.build();
		assertNotNull(listing);

		builder = Components.listing.properties(SET);
		assertNotNull(builder);
		listing = builder.build();
		assertNotNull(listing);

	}

	@Test
	public void testComponent() {

		PropertyListing listing = PropertyListing.builder(SET).id("testid").build();
		assertNotNull(listing.getComponent());

		assertTrue(listing.getComponent().getId().isPresent());
		assertEquals("testid", listing.getComponent().getId().get());

		listing = PropertyListing.builder(SET).build();
		assertTrue(listing.isVisible());

		listing = PropertyListing.builder(SET).visible(true).build();
		assertTrue(listing.isVisible());

		listing = PropertyListing.builder(SET).visible(false).build();
		assertFalse(listing.isVisible());

		listing = PropertyListing.builder(SET).hidden().build();
		assertFalse(listing.isVisible());

		final AtomicBoolean attached = new AtomicBoolean(false);

		listing = PropertyListing.builder(SET).withAttachListener(e -> {
			attached.set(true);
		}).build();

		ComponentUtil.onComponentAttach(listing.getComponent(), true);
		assertTrue(attached.get());

		final AtomicBoolean detached = new AtomicBoolean(false);

		listing = PropertyListing.builder(SET).withDetachListener(e -> {
			detached.set(true);
		}).build();

		ComponentUtil.onComponentDetach(listing.getComponent());
		assertTrue(detached.get());
	}

	@Test
	public void testStyles() {

		PropertyListing listing = PropertyListing.builder(SET).styleName("test").build();
		assertNotNull(listing);
		assertTrue(ComponentTestUtils.getClassNames(listing).contains("test"));

		listing = PropertyListing.builder(SET).styleNames("test1", "test2").build();
		assertNotNull(listing);
		assertTrue(ComponentTestUtils.getClassNames(listing).contains("test1"));
		assertTrue(ComponentTestUtils.getClassNames(listing).contains("test2"));

		listing = PropertyListing.builder(SET).withThemeVariants(GridVariant.LUMO_COMPACT).build();
		assertTrue(listing.getComponent() instanceof Grid);

		assertTrue(
				((Grid<?>) listing.getComponent()).getThemeNames().contains(GridVariant.LUMO_COMPACT.getVariantName()));

	}

	@Test
	public void testSize() {

		PropertyListing listing = PropertyListing.builder(SET).width("50em").build();
		assertEquals("50em", ComponentTestUtils.getWidth(listing));

		listing = PropertyListing.builder(SET).width(50, Unit.EM).build();
		assertEquals("50em", ComponentTestUtils.getWidth(listing));

		listing = PropertyListing.builder(SET).width(50.7f, Unit.EM).build();
		assertEquals("50.7em", ComponentTestUtils.getWidth(listing));

		listing = PropertyListing.builder(SET).height("50em").build();
		assertEquals("50em", ComponentTestUtils.getHeight(listing));

		listing = PropertyListing.builder(SET).height(50, Unit.EM).build();
		assertEquals("50em", ComponentTestUtils.getHeight(listing));

		listing = PropertyListing.builder(SET).height(50.7f, Unit.EM).build();
		assertEquals("50.7em", ComponentTestUtils.getHeight(listing));

		listing = PropertyListing.builder(SET).width("50%").height("100%").build();
		assertEquals("50%", ComponentTestUtils.getWidth(listing));
		assertEquals("100%", ComponentTestUtils.getHeight(listing));

		listing = PropertyListing.builder(SET).widthUndefined().build();
		assertNull(ComponentTestUtils.getWidth(listing));

		listing = PropertyListing.builder(SET).heightUndefined().build();
		assertNull(ComponentTestUtils.getHeight(listing));

		listing = PropertyListing.builder(SET).sizeUndefined().build();
		assertNull(ComponentTestUtils.getWidth(listing));
		assertNull(ComponentTestUtils.getHeight(listing));

		listing = PropertyListing.builder(SET).fullWidth().build();
		assertEquals("100%", ComponentTestUtils.getWidth(listing));

		listing = PropertyListing.builder(SET).fullHeight().build();
		assertEquals("100%", ComponentTestUtils.getHeight(listing));

		listing = PropertyListing.builder(SET).fullSize().build();
		assertEquals("100%", ComponentTestUtils.getWidth(listing));
		assertEquals("100%", ComponentTestUtils.getHeight(listing));

	}

	@Test
	public void testEnabled() {

		PropertyListing listing = PropertyListing.builder(SET).build();
		assertTrue(ComponentTestUtils.isEnabled(listing));

		listing = PropertyListing.builder(SET).enabled(true).build();
		assertTrue(ComponentTestUtils.isEnabled(listing));

		listing = PropertyListing.builder(SET).enabled(false).build();
		assertFalse(ComponentTestUtils.isEnabled(listing));

		listing = PropertyListing.builder(SET).disabled().build();
		assertFalse(ComponentTestUtils.isEnabled(listing));

	}

	@Test
	public void testFocus() {

		PropertyListing listing = PropertyListing.builder(SET).tabIndex(77).build();
		assertTrue(listing.getComponent() instanceof Grid);

		assertEquals(77, ((Grid<?>) listing.getComponent()).getTabIndex());

	}

	@Test
	public void testVisibleColumns() {

		PropertyListing listing = PropertyListing.builder(SET).visibleColumns(NAME, ID).build();

		List<Property<?>> properties = ConversionUtils.iterableAsList(listing.getProperties());
		assertEquals(3, properties.size());
		assertTrue(properties.contains(ID));
		assertTrue(properties.contains(NAME));
		assertTrue(properties.contains(VIRTUAL));

		List<Property<?>> visible = listing.getVisibleColumns();
		assertEquals(2, visible.size());
		assertEquals(NAME, visible.get(0));
		assertEquals(ID, visible.get(1));
		
	}

	@Test
	public void testPropertySet() {

		PropertyListing listing = PropertyListing.builder(SET).build();

		List<Property<?>> properties = ConversionUtils.iterableAsList(listing.getProperties());
		assertEquals(3, properties.size());
		assertTrue(properties.contains(ID));
		assertTrue(properties.contains(NAME));
		assertTrue(properties.contains(VIRTUAL));

	}

}
