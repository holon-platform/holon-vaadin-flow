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
package com.holonplatform.vaadin.flow.test.util;

import com.holonplatform.core.datastore.DataTarget;
import com.holonplatform.core.property.PropertySet;
import com.holonplatform.core.property.StringProperty;

public final class DatastoreTestUtils {

	private DatastoreTestUtils() {
	}

	public static final DataTarget<?> TARGET1 = DataTarget.named("test1");

	public static final StringProperty CODE = StringProperty.create("code");
	public static final StringProperty DESCRIPTION = StringProperty.create("description");

	public static final PropertySet<?> TEST1 = PropertySet.builderOf(CODE, DESCRIPTION).identifier(CODE).build();
	public static final PropertySet<?> TEST1_NOID = PropertySet.of(CODE, DESCRIPTION);

}
