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
package com.holonplatform.vaadin.flow.internal.components.builders;

import java.util.function.Consumer;

import com.holonplatform.core.i18n.Localizable;
import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.vaadin.flow.components.builders.ButtonBuilder;
import com.holonplatform.vaadin.flow.components.builders.ButtonConfigurator;
import com.holonplatform.vaadin.flow.components.builders.ButtonConfigurator.BaseButtonConfigurator;
import com.holonplatform.vaadin.flow.components.builders.DialogBuilder;
import com.holonplatform.vaadin.flow.components.builders.DialogBuilder.QuestionDialogBuilder;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;

/**
 * Default {@link QuestionDialogBuilder} implementation.
 *
 * @since 5.2.0
 */
public class DefaultQuestionDialogBuilder extends AbstractDialogConfigurator<QuestionDialogBuilder>
		implements QuestionDialogBuilder {

	private final Button confirmButton;
	private final Button denyButton;

	public DefaultQuestionDialogBuilder(QuestionDialogCallback questionDialogCallback) {
		super();
		ObjectUtils.argumentNotNull(questionDialogCallback, "Question dialog callback must be not null");
		this.confirmButton = ButtonBuilder.create()
				.text(Localizable.of("Yes", DialogBuilder.DEFAULT_CONFIRM_BUTTON_MESSAGE_CODE)).withClickListener(e -> {
					getComponent().close();
					questionDialogCallback.onUserAnswer(true);
				}).build();
		this.denyButton = ButtonBuilder.create()
				.text(Localizable.of("No", DialogBuilder.DEFAULT_DENY_BUTTON_MESSAGE_CODE)).withClickListener(e -> {
					getComponent().close();
					questionDialogCallback.onUserAnswer(false);
				}).build();
		getComponent().addToolbarComponent(this.denyButton);
		getComponent().addToolbarComponent(this.confirmButton);

		getComponent().setCloseOnEsc(false);
		getComponent().setCloseOnOutsideClick(false);

		// since 5.5.0: set modal by default
		getComponent().setModal(true);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.holonplatform.vaadin.flow.components.builders.DialogBuilder.
	 * QuestionDialogBuilder#confirmButtonConfigurator( java.util.function.Consumer)
	 */
	@Override
	public QuestionDialogBuilder confirmButtonConfigurator(Consumer<BaseButtonConfigurator> configurator) {
		ObjectUtils.argumentNotNull(configurator, "Configurator must be not null");
		configurator.accept(ButtonConfigurator.configure(confirmButton));
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.holonplatform.vaadin.flow.components.builders.DialogBuilder.
	 * QuestionDialogBuilder#denialButtonConfigurator( java.util.function.Consumer)
	 */
	@Override
	public QuestionDialogBuilder denialButtonConfigurator(Consumer<BaseButtonConfigurator> configurator) {
		ObjectUtils.argumentNotNull(configurator, "Configurator must be not null");
		configurator.accept(ButtonConfigurator.configure(denyButton));
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.holonplatform.vaadin.flow.internal.components.builders.
	 * AbstractComponentConfigurator#getConfigurator()
	 */
	@Override
	protected QuestionDialogBuilder getConfigurator() {
		return this;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.holonplatform.vaadin.flow.components.builders.DialogBuilder#build()
	 */
	@Override
	public Dialog build() {
		return getComponent();
	}

}
