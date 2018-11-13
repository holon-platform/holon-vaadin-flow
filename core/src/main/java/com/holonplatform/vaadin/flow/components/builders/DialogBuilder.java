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
package com.holonplatform.vaadin.flow.components.builders;

import java.util.function.Consumer;

import com.holonplatform.vaadin.flow.components.builders.ButtonConfigurator.BaseButtonConfigurator;
import com.holonplatform.vaadin.flow.internal.components.builders.DefaultConfirmDialogBuilder;
import com.holonplatform.vaadin.flow.internal.components.builders.DefaultMessageDialogBuilder;
import com.holonplatform.vaadin.flow.internal.components.builders.DefaultQuestionDialogBuilder;
import com.vaadin.flow.component.dialog.Dialog;

/**
 * {@link Dialog} component builder.
 * 
 * @param <B> Concrete builder type
 *
 * @since 5.2.0
 */
public interface DialogBuilder<B extends DialogBuilder<B>> extends DialogConfigurator<B> {

	/**
	 * Build the {@link Dialog} component.
	 * @return The {@link Dialog} instance
	 */
	Dialog build();

	/**
	 * Build the {@link Dialog} component and open it.
	 * @return The {@link Dialog} instance
	 */
	default Dialog open() {
		final Dialog dialog = build();
		dialog.open();
		return dialog;
	}

	// ------- builders

	/**
	 * Get a builder to create a generic message dialog.
	 * @return A new {@link MessageDialogBuilder}
	 */
	static MessageDialogBuilder message() {
		return new DefaultMessageDialogBuilder();
	}

	/**
	 * Get a builder to create a message dialog with a <em>OK</em> button in the dialog toolbar which can be used to
	 * close the dialog.
	 * <p>
	 * The default <em>OK</em> button message localization code is {@link #DEFAULT_OK_BUTTON_MESSAGE_CODE}.
	 * </p>
	 * @return A new {@link ConfirmDialogBuilder}
	 */
	static ConfirmDialogBuilder confirm() {
		return new DefaultConfirmDialogBuilder();
	}

	/**
	 * Get a builder to create a question dialog, with a <em>confirm</em> button and a <em>deny</em> button in the
	 * dialog toolbar which will trigger the given <code>questionDialogCallback</code> to react to the user choice.
	 * <p>
	 * The default <em>confirm</em> button message localization code is {@link #DEFAULT_CONFIRM_BUTTON_MESSAGE_CODE}.
	 * The default <em>deny</em> button message localization code is {@link #DEFAULT_DENY_BUTTON_MESSAGE_CODE}.
	 * </p>
	 * @param questionDialogCallback The callback function use to react to the user selection (not null)
	 * @return A new {@link QuestionDialogBuilder}
	 */
	static QuestionDialogBuilder question(QuestionDialogCallback questionDialogCallback) {
		return new DefaultQuestionDialogBuilder(questionDialogCallback);
	}

	// ------- messages

	/**
	 * Default <em>ok</em> confirm dialog button message code.
	 */
	public static final String DEFAULT_OK_BUTTON_MESSAGE_CODE = "com.holonplatform.vaadin.flow.components.dialog.button.ok";

	/**
	 * Default <em>confirm</em> question dialog button message code.
	 */
	public static final String DEFAULT_CONFIRM_BUTTON_MESSAGE_CODE = "com.holonplatform.vaadin.flow.components.dialog.button.confirm";

	/**
	 * Default <em>deny</em> question dialog button message code.
	 */
	public static final String DEFAULT_DENY_BUTTON_MESSAGE_CODE = "com.holonplatform.vaadin.flow.components.dialog.button.deny";

	// ------- callbacks

	/**
	 * Question dialog user answer callback.
	 */
	@FunctionalInterface
	public interface QuestionDialogCallback {

		/**
		 * Invoked when the user selected an answer in a question dialog.
		 * @param confirmSelected <code>true</code> if the user selected the <em>confirm</em> option, <code>false</code>
		 *        if selected the <code>deny</code> option
		 */
		void onUserAnswer(boolean confirmSelected);

	}

	// ------- Specific builders

	/**
	 * Default message {@link Dialog} builder.
	 *
	 * @since 5.2.0
	 */
	public interface MessageDialogBuilder
			extends DialogBuilder<MessageDialogBuilder>, ClosableDialogConfigurator<MessageDialogBuilder> {

	}

	/**
	 * Confirm {@link Dialog} builder.
	 * <p>
	 * A message dialog provides by default a <em>OK</em> button in the dialog toolbar to close the dialog.
	 * </p>
	 *
	 * @since 5.2.0
	 */
	public interface ConfirmDialogBuilder
			extends DialogBuilder<ConfirmDialogBuilder>, ClosableDialogConfigurator<ConfirmDialogBuilder> {

		/**
		 * Provide a {@link Consumer} to configure the default <em>OK</em> button shown in the dialog toolbar.
		 * @param configurator The button configurator (not null)
		 * @return this
		 */
		ConfirmDialogBuilder okButtonConfigurator(Consumer<BaseButtonConfigurator> configurator);

	}

	/**
	 * Question {@link Dialog} builder.
	 * <p>
	 * A question dialog provides two buttons by default, shown in the dialog toolbar: one for user confirmation and one
	 * for user denial to the question contained in the dialog message. A callback can be used to react to the user
	 * choice.
	 * </p>
	 *
	 * @since 5.2.0
	 */
	public interface QuestionDialogBuilder extends DialogBuilder<QuestionDialogBuilder> {

		/**
		 * Provide a {@link Consumer} to configure the default user <em>confirmation</em> button.
		 * @param configurator The button configurator (not null)
		 * @return this
		 */
		QuestionDialogBuilder confirmButtonConfigurator(Consumer<BaseButtonConfigurator> configurator);

		/**
		 * Provide a {@link Consumer} to configure the default user <em>denial</em> button.
		 * @param configurator The button configurator (not null)
		 * @return this
		 */
		QuestionDialogBuilder denialButtonConfigurator(Consumer<BaseButtonConfigurator> configurator);

	}

}
