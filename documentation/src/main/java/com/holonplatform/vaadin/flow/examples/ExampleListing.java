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
package com.holonplatform.vaadin.flow.examples;

import java.util.Optional;
import java.util.Set;

import com.holonplatform.core.Validator;
import com.holonplatform.core.datastore.DataTarget;
import com.holonplatform.core.datastore.Datastore;
import com.holonplatform.core.property.NumericProperty;
import com.holonplatform.core.property.PropertyBox;
import com.holonplatform.core.property.PropertyRendererRegistry;
import com.holonplatform.core.property.PropertySet;
import com.holonplatform.core.property.PropertyValuePresenterRegistry;
import com.holonplatform.core.property.StringProperty;
import com.holonplatform.vaadin.flow.components.BeanListing;
import com.holonplatform.vaadin.flow.components.Components;
import com.holonplatform.vaadin.flow.components.Input;
import com.holonplatform.vaadin.flow.components.Input.InputPropertyRenderer;
import com.holonplatform.vaadin.flow.components.PropertyListing;
import com.holonplatform.vaadin.flow.components.Selectable.SelectionMode;
import com.holonplatform.vaadin.flow.components.builders.ItemListingConfigurator.ColumnAlignment;
import com.holonplatform.vaadin.flow.data.DatastoreDataProvider;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.converter.StringToLongConverter;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.renderer.TextRenderer;

@SuppressWarnings("unused")
public class ExampleListing {

	// tag::propertydef[]
	static final NumericProperty<Long> ID = NumericProperty.longType("id");
	static final StringProperty NAME = StringProperty.create("name");
	static final PropertySet<?> SUBJECT = PropertySet.of(ID, NAME);
	// end::propertydef[]

	// tag::target[]
	static final DataTarget<?> TARGET = DataTarget.named("subjects");
	// end::target[]

	public void listing1() {
		// tag::listing1[]
		PropertyListing listing = PropertyListing.builder(SUBJECT).build(); // <1>

		listing = Components.listing.properties(ID, NAME).build(); // <2>

		new VerticalLayout().add(listing.getComponent()); // <3>
		// end::listing1[]
	}

	// tag::beandef[]
	class MyBean {

		private Long id;
		private String name;

		public MyBean() {
			super();
		}

		public MyBean(Long id, String name) {
			super();
			this.id = id;
			this.name = name;
		}

		public Long getId() {
			return id;
		}

		public void setId(Long id) {
			this.id = id;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

	}
	// end::beandef[]

	public void listing2() {
		// tag::listing2[]
		BeanListing<MyBean> listing = BeanListing.builder(MyBean.class).build(); // <1>

		listing = Components.listing.items(MyBean.class).build(); // <2>

		new VerticalLayout().add(listing.getComponent()); // <3>
		// end::listing2[]
	}

	public void listing3() {
		// tag::listing3[]
		PropertyListing listing = PropertyListing.builder(SUBJECT) // <1>
				.items(PropertyBox.builder(SUBJECT).set(ID, 1L).set(NAME, "One").build()).build();

		listing = PropertyListing.builder(SUBJECT) // <2>
				.addItem(PropertyBox.builder(SUBJECT).set(ID, 1L).set(NAME, "One").build())
				.addItem(PropertyBox.builder(SUBJECT).set(ID, 2L).set(NAME, "Two").build()).build();
		// end::listing3[]
	}

	public void listing4() {
		// tag::listing4[]
		BeanListing<MyBean> listing = BeanListing.builder(MyBean.class) // <1>
				.items(new MyBean(1L, "One"), new MyBean(2L, "Two")).build();

		listing = BeanListing.builder(MyBean.class) // <2>
				.addItem(new MyBean(1L, "One")) //
				.addItem(new MyBean(2L, "Two")).build();
		// end::listing4[]
	}

	public void listing5() {
		// tag::listing5[]
		DataProvider<PropertyBox, ?> dataProvider = getPropertyBoxDataProvider(); // <1>

		PropertyListing listing = PropertyListing.builder(SUBJECT).dataSource(dataProvider) // <2>
				.build();
		// end::listing5[]
	}

	public void listing6() {
		// tag::listing6[]
		DataProvider<MyBean, ?> dataProvider = getBeanDataProvider(); // <1>

		BeanListing<MyBean> listing = BeanListing.builder(MyBean.class).dataSource(dataProvider) // <2>
				.build();
		// end::listing6[]
	}

	public void listing7() {
		PropertyBox itemToRefresh = null;
		// tag::listing7[]
		PropertyListing listing = PropertyListing.builder(SUBJECT).dataSource(getDataProvider()).build(); // <1>

		listing.refresh(); // <2>
		listing.refreshItem(itemToRefresh); // <3>
		// end::listing7[]
	}

	public void listing8() {
		// tag::listing8[]
		Datastore datastore = getDatastore();

		PropertyListing listing = PropertyListing.builder(SUBJECT) // <1>
				.dataSource(DatastoreDataProvider.create(datastore, TARGET, SUBJECT)) // <2>
				.build();
		// end::listing8[]
	}

	public void listing9() {
		// tag::listing9[]
		Datastore datastore = getDatastore();

		PropertyListing listing = PropertyListing.builder(SUBJECT) // <1>
				.dataSource(datastore, TARGET) // <2>
				.build();
		// end::listing9[]
	}

	public void listing10() {
		// tag::listing10[]
		PropertyListing listing = PropertyListing.builder(SUBJECT) //
				.dataSource(getDatastore(), TARGET) // <1>
				.withQueryFilter(NAME.isNotNull()) // <2>
				.withQuerySort(ID.asc()) // <3>
				.build();
		// end::listing10[]
	}

	public void listing11() {
		// tag::listing11[]
		PropertyListing listing = PropertyListing.builder(SUBJECT) //
				.pageSize(50) // <1>
				.columnReorderingAllowed(true) // <2>
				.resizable(true) // <3>
				.frozenColumns(1) // <4>
				.heightByRows(true) // <5>
				.verticalScrollingEnabled(true) // <6>
				.multiSort(true) // <7>
				.build();
		// end::listing11[]
	}

	public void listing11b() {
		// tag::listing11b[]
		PropertyListing listing = PropertyListing.builder(SUBJECT) //
				.withItemClickListener(event -> { // <1>
					PropertyBox clickedItem = event.getItem(); // <2>
					PropertyListing source = event.getSource(); // <3>
					event.getClickCount(); // <4>
					event.getButton(); // <5>
					event.isCtrlKey(); // <6>
					/* other getters omitted */
				}).build();
		// end::listing11b[]
	}

	public void listing12() {
		// tag::listing12[]
		PropertyListing listing = PropertyListing.builder(SUBJECT) //
				.visibleColumns(NAME, ID) // <1>
				.displayAsFirst(NAME) // <2>
				.displayAsLast(ID) // <3>
				.displayBefore(NAME, ID) // <4>
				.displayAfter(ID, NAME) // <5>
				.hidden(ID) // <6>
				.build();

		BeanListing<MyBean> listing2 = BeanListing.builder(MyBean.class) // <7>
				.visibleColumns("name", "id").displayAsFirst("name").hidden("id").build();
		// end::listing12[]
	}

	public void listing13() {
		// tag::listing13[]
		PropertyListing listing = PropertyListing.builder(SUBJECT) //
				.header(NAME, "The name") // <1>
				.header(NAME, "The name", "name.message.code") // <2>
				.headerComponent(NAME, new Button("name")) // <3>
				.width(NAME, "100px") // <4>
				.flexGrow(NAME, 1) // <5>
				.alignment(NAME, ColumnAlignment.CENTER) // <6>
				.footer(NAME, "Footer text") // <7>
				.resizable(NAME, true) // <8>
				.sortable(NAME, true) // <9>
				.sortUsing(NAME, ID) // <10>
				.sortProvider(NAME, direction ->
				/* sort logic omitted */
				null) // <11>
				.valueProvider(NAME, item -> item.getValue(NAME)) // <12>
				.renderer(NAME, new TextRenderer<>()) // <13>
				.frozen(NAME, true) // <14>
				.build();
		// end::listing13[]
	}

	public void listing14() {
		// tag::listing14[]
		PropertyListing listing = PropertyListing.builder(SUBJECT) //
				.withColumn(item -> "Virtual: " + item.getValue(ID)) // <1>
				.header("Virtual") // <2>
				.displayBefore(NAME) // <3>
				.add() // <4>
				.build();
		// end::listing14[]
	}

	public void listing15() {
		// tag::listing15[]
		BeanListing<MyBean> listing2 = BeanListing.builder(MyBean.class) //
				.withColumn(item -> "Virtual: " + item.getId()) // <1>
				.header("Virtual") // <2>
				.displayAsFirst() // <3>
				.add() // <4>
				.build();
		// end::listing15[]
	}

	public void listing16() {
		// tag::listing16[]
		PropertyListing listing = PropertyListing.builder(SUBJECT) //
				.withComponentColumn(item -> new Button(item.getValue(NAME))) // <1>
				.header("Component") // <2>
				.sortUsing(NAME) // <3>
				.add() // <4>
				.build();
		// end::listing16[]
	}

	public void listing17() {
		// tag::listing17a[]
		PropertyValuePresenterRegistry.getDefault() // <1>
				.forProperty(ID, (property, value) -> "#" + value); // <2>
		// end::listing17a[]

		// tag::listing17b[]
		PropertyListing listing = PropertyListing.builder(SUBJECT).build(); // <1>

		String value = ID.present(1L); // <2>
		// end::listing17b[]
	}

	public void listing17c() {
		// tag::listing17c[]
		PropertyListing listing = PropertyListing.builder(SUBJECT) //
				.componentRenderer(NAME, item -> { // <1>
					return new Button(item.getValue(NAME));
				}).build();
		// end::listing17c[]
	}

	public void listing17d() {
		// tag::listing17d[]
		PropertyListing listing = PropertyListing.builder(SUBJECT) //
				.renderAsViewComponent(NAME) // <1>
				.build();
		// end::listing17d[]
	}

	public void listing18() {
		// tag::listing18[]
		PropertyListing listing = PropertyListing.builder(SUBJECT) //
				.contextMenu() // <1>
				.withItem("Context menu item 1") // <2>
				.withClickListener(e -> { // <3>
					PropertyBox rowItem = e.getItem(); // <4>
					e.getItemListing(); // <5>
					Notification.show("Context menu item clicked on row id " + rowItem.getValue(ID));
				}).add() // <6>
				.withItem("Context menu item 2", "item.message.code", e -> {
					/* do something */}) // <7>
				.withItem(new Button("Context menu item 3"), e -> {
					/* do something */}) // <8>
				.add() // <9>
				.build();
		// end::listing18[]
	}

	public void listing19() {
		// tag::listing19[]
		PropertyListing listing = PropertyListing.builder(SUBJECT) //
				.itemDetailsText(item -> "Detail of: " + item.getValue(ID)) // <1>
				.itemDetailsComponent(item -> new Button(item.getValue(NAME))) // <2>
				.build();
		// end::listing19[]
	}

	public void listing20() {
		// tag::listing20[]
		PropertyListing listing = PropertyListing.builder(SUBJECT) //
				.itemDetailsText(item -> "Detail of: " + item.getValue(ID)) // <1>
				.itemDetailsVisibleOnClick(false) // <2>
				.withItemClickListener(e -> { // <3>
					e.getSource().setItemDetailsVisible(e.getItem(), true);
				}).build();
		// end::listing20[]
	}

	public void listing21() {
		// tag::listing21[]
		PropertyListing listing = PropertyListing.builder(SUBJECT) //
				.header(header -> { // <1>
					header.prependRow().join(ID, NAME).setText("A text");
				}).footer(footer -> { // <2>
					footer.appendRow().getCell(ID).ifPresent(cell -> cell.setText("ID footer"));
				}).build();

		listing.getHeader().ifPresent(header -> { // <3>
			/* customize header */
		});
		listing.getFooter().ifPresent(footer -> { // <4>
			/* customize footer */
		});
		// end::listing21[]
	}

	public void listing22() {
		PropertyBox myItem = null;
		// tag::listing22[]
		PropertyListing listing = PropertyListing.builder(SUBJECT) //
				.selectionMode(SelectionMode.SINGLE) // <1>
				.singleSelect() // <2>
				.multiSelect() // <3>
				.withSelectionListener(event -> { // <4>
					Set<PropertyBox> items = event.getAllSelectedItems(); // <5>
					Optional<PropertyBox> item = event.getFirstSelectedItem(); // <6>
				}).build();

		listing.setSelectionMode(SelectionMode.MULTI); // <7>
		listing.addSelectionListener(event -> { // <8>
		});

		listing.select(myItem); // <9>
		listing.deselect(myItem); // <10>
		listing.deselectAll(); // <11>
		// end::listing22[]
	}

	@SuppressWarnings("null")
	public void listing23() {
		PropertyListing listing = null;
		// tag::listing23[]
		PropertyListing.builder(SUBJECT) //
				.editable() // <1>
				.editorBuffered(true) // <2>
				.withComponentColumn(item -> Components.button("Edit", e -> listing.editItem(item)) // <3>
				).editorComponent(new Div( // <4>
						Components.button("Save", e -> listing.saveEditingItem()),
						Components.button("Cancel", e -> listing.cancelEditing())))
				.displayAsFirst() // <5>
				.add() // <6>
				.withEditorSaveListener(event -> { // <7>
					PropertyBox item = event.getItem(); // <8>
					/* update the item in backend */
				}).build();
		// end::listing23[]
	}

	public void listing24() {
		// tag::listing24[]
		PropertyListing.builder(SUBJECT) //
				.editor(NAME, Input.string().build()) // <1>
				.editor(NAME, property -> Input.string().build()) // <2>
				.editor(ID, Input.string().build(), new StringToLongConverter("")) // <3>
				.editorField(NAME, new TextField()) // <4>
				.editorField(ID, new TextField(), new StringToLongConverter("")) // <5>
				.editorComponent(ID, new Button()) // <6>
				.build();
		// end::listing24[]
	}

	public void listing25() {
		// tag::listing25[]
		PropertyRendererRegistry.getDefault().forProperty(NAME, // <1>
				InputPropertyRenderer.create(property -> Input.stringArea().build()));

		PropertyListing.builder(SUBJECT).editable().build(); // <2>
		// end::listing25[]
	}

	public void listing26() {
		// tag::listing26[]
		final StringProperty NAME = StringProperty.create("name").withValidator(Validator.notBlank()); // <1>

		PropertyListing.builder(SUBJECT) //
				.withValidator(NAME, Validator.max(10)) // <2>
				.build();
		// end::listing26[]
	}

	public void listing27() {
		// tag::listing27[]
		PropertyListing.builder(SUBJECT) //
				.withValidator(Validator.create(item -> item.getValue(ID) != null, "Id value must be not null")) // <1>
				.build();
		// end::listing27[]
	}

	public void listing28() {
		// tag::listing28[]
		PropertyListing.builder(SUBJECT) //
				.validationStatusHandler(event -> { // <1>
					if (event.isInvalid()) {
						Notification.show("Validation falied: " + event.getErrorMessage());
					}
				}).validationStatusHandler(NAME, event -> { // <2>
					/* omitted */
				}).build();
		// end::listing28[]
	}

	public void listing29() {
		// tag::listing29[]
		PropertyListing.builder(SUBJECT) //
				.groupValidationStatusHandler(event -> { // <1>
					event.getGroupStatus(); // <2>
					event.getInputsValidationStatus(); // <3>
					event.getGroupErrorMessages(); // <4>
					event.getInputsValidationStatus().forEach(s -> s.getErrorMessages()); // <5>
				}).build();
		// end::listing29[]
	}

	public void listing30() {
		// tag::listing30[]
		PropertyListing.builder(SUBJECT) //
				.withValueChangeListener(NAME, event -> { // <1>
					event.getOldValue();
					event.getValue();
				}).build();
		// end::listing30[]
	}

	private static DataProvider<PropertyBox, ?> getDataProvider() {
		return null;
	}

	private static DataProvider<PropertyBox, ?> getPropertyBoxDataProvider() {
		return null;
	}

	private static DataProvider<MyBean, ?> getBeanDataProvider() {
		return null;
	}

	private static Datastore getDatastore() {
		return null;
	}

}
