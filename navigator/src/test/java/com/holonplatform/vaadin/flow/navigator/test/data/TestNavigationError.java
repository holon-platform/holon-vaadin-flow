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
package com.holonplatform.vaadin.flow.navigator.test.data;

import jakarta.servlet.http.HttpServletResponse;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.ErrorParameter;
import com.vaadin.flow.router.HasErrorParameter;
import com.vaadin.flow.theme.NoTheme;

@SuppressWarnings("serial")
@Tag(Tag.DIV)
@NoTheme
public class TestNavigationError extends Component implements HasErrorParameter<Exception> {

	private Exception exception;

	public Exception getException() {
		return exception;
	}

	@Override
	public int setErrorParameter(BeforeEnterEvent event, ErrorParameter<Exception> parameter) {
		exception = parameter.getCaughtException();
		return HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
	}

}
