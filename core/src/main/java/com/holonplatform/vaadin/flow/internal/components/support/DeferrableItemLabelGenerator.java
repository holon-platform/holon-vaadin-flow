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
package com.holonplatform.vaadin.flow.internal.components.support;

import java.util.Collections;
import java.util.Map;

import com.holonplatform.core.i18n.Localizable;
import com.holonplatform.vaadin.flow.i18n.LocalizationProvider;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.AttachNotifier;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.ItemLabelGenerator;

public class DeferrableItemLabelGenerator<ITEM>
		implements ItemLabelGenerator<ITEM>, ComponentEventListener<AttachEvent> {

	private static final long serialVersionUID = -4341268920830268761L;

	private final Map<ITEM, Localizable> itemCaptions;

	private boolean localize;

	public DeferrableItemLabelGenerator(Map<ITEM, Localizable> itemCaptions, AttachNotifier attachNotifier,
			boolean deferLocalization) {
		super();
		this.itemCaptions = (itemCaptions != null) ? itemCaptions : Collections.emptyMap();
		this.localize = !deferLocalization;
		if (deferLocalization && attachNotifier != null) {
			attachNotifier.addAttachListener(this);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.vaadin.flow.component.ComponentEventListener#onComponentEvent(com.vaadin.flow.component.ComponentEvent)
	 */
	@Override
	public void onComponentEvent(AttachEvent event) {
		if (event.isInitialAttach()) {
			localize = true;
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.vaadin.flow.component.ItemLabelGenerator#apply(java.lang.Object)
	 */
	@Override
	public String apply(ITEM item) {
		if (localize) {
			Localizable caption = itemCaptions.get(item);
			if (caption != null) {
				return LocalizationProvider.localize(caption).orElseGet(() -> String.valueOf(item));
			}
		} else {
			Localizable caption = itemCaptions.get(item);
			if (caption != null && caption.getMessage() != null) {
				return caption.getMessage();
			}
		}
		return String.valueOf(item);
	}

}
