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
package com.holonplatform.vaadin.flow.internal.data;

import java.lang.reflect.InvocationTargetException;
import java.util.function.Function;

import com.holonplatform.core.beans.BeanPropertySet;
import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.core.property.PropertyBox;

/**
 * A converter to convert a {@link PropertyBox} item into a bean class type.
 *
 * @param <T> Bean type
 *
 * @since 5.2.0
 */
public class BeanPropertySetItemConverter<T> implements Function<PropertyBox, T> {

	private final BeanPropertySet<T> beanPropertySet;

	/**
	 * Constructor.
	 * @param beanPropertySet Bean property set (not null)
	 */
	public BeanPropertySetItemConverter(BeanPropertySet<T> beanPropertySet) {
		super();
		ObjectUtils.argumentNotNull(beanPropertySet, "BeanPropertySet must be not null");
		this.beanPropertySet = beanPropertySet;
	}

	/*
	 * (non-Javadoc)
	 * @see java.util.function.Function#apply(java.lang.Object)
	 */
	@Override
	public T apply(PropertyBox item) {
		if (item != null) {
			try {
				return beanPropertySet.write(item,
						beanPropertySet.getBeanClass().getDeclaredConstructor().newInstance(), true);
			} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
					| InvocationTargetException | NoSuchMethodException | SecurityException e) {
				throw new RuntimeException("Failed to convert item [" + item + "] into a bean class instance ["
						+ beanPropertySet.getBeanClass().getName() + "]", e);
			}
		}
		return null;
	}

}
