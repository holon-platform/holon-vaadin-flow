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
package com.holonplatform.vaadin.flow.navigator.internal;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;

import com.holonplatform.auth.annotations.Authenticate;
import com.holonplatform.core.i18n.Caption;
import com.holonplatform.core.i18n.Localizable;
import com.holonplatform.core.internal.Logger;
import com.holonplatform.core.internal.utils.AnnotationUtils;
import com.holonplatform.core.internal.utils.ClassUtils;
import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.core.internal.utils.TypeUtils;
import com.holonplatform.vaadin.flow.internal.VaadinLogger;
import com.holonplatform.vaadin.flow.navigator.annotations.OnLeave;
import com.holonplatform.vaadin.flow.navigator.annotations.OnShow;
import com.holonplatform.vaadin.flow.navigator.annotations.URLParameterType;
import com.holonplatform.vaadin.flow.navigator.annotations.ViewParameter;
import com.holonplatform.vaadin.flow.navigator.exceptions.NavigationTargetConfigurationException;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.BeforeLeaveEvent;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.Router;

/**
 * Default {@link NavigationTargetConfiguration} implementation.
 *
 * @since 5.2.0
 */
public class DefaultNavigationTargetConfiguration implements NavigationTargetConfiguration {

	private static final long serialVersionUID = -7603415697370663443L;

	private static final Logger LOGGER = VaadinLogger.create();

	private final Class<?> navigationTarget;

	private final String routePath;

	private final Localizable caption;

	private final Map<String, NavigationParameterDefinition> queryParameterDefinitions;
	private final Collection<NavigationParameterDefinition> pathParameterDefinitions;

	private final List<Method> onShowMethods;
	private final List<Method> onLeaveMethods;

	private final boolean authenticationRequired;
	private final Authenticate authenticate;
	private final Set<String> authorization;

	private final NavigationParameterMapper navigationParameterMapper;

	/**
	 * Constructor using default {@link NavigationParameterMapper}.
	 * @param navigationTarget The navigation target class (not null)
	 * @throws NavigationTargetConfigurationException If a configuration error occurred
	 */
	public DefaultNavigationTargetConfiguration(Class<?> navigationTarget) {
		this(navigationTarget, NavigationParameterMapper.getDefault());
	}

	/**
	 * Constructor.
	 * @param navigationTarget The navigation target class (not null)
	 * @param navigationParameterMapper The {@link NavigationParameterMapper} to use (nt null)
	 * @throws NavigationTargetConfigurationException If a configuration error occurred
	 */
	public DefaultNavigationTargetConfiguration(Class<?> navigationTarget,
			NavigationParameterMapper navigationParameterMapper) {
		super();
		ObjectUtils.argumentNotNull(navigationTarget, "Navigation target class must be not null");
		ObjectUtils.argumentNotNull(navigationParameterMapper, "Navigation parameter mapper must be not null");
		this.navigationTarget = navigationTarget;
		this.navigationParameterMapper = navigationParameterMapper;
		this.authenticate = navigationTarget.getAnnotation(Authenticate.class);
		final Optional<Set<String>> roles = getAuthorizationRoles(navigationTarget);
		this.authorization = roles.orElse(Collections.emptySet());
		this.authenticationRequired = (this.authenticate != null) || roles.isPresent();
		if (navigationTarget.isAnnotationPresent(Route.class)) {
			this.routePath = Router.resolve(navigationTarget, navigationTarget.getAnnotation(Route.class));
		} else {
			this.routePath = null;
		}
		if (navigationTarget.isAnnotationPresent(Caption.class)) {
			final Caption captionAnnotation = navigationTarget.getAnnotation(Caption.class);
			this.caption = Localizable.builder().message(captionAnnotation.value())
					.messageCode(AnnotationUtils.getStringValue(captionAnnotation.messageCode())).build();
		} else {
			this.caption = null;
		}
		this.onShowMethods = detectAnnotatedMethods(navigationTarget, OnShow.class, AfterNavigationEvent.class);
		this.onLeaveMethods = detectAnnotatedMethods(navigationTarget, OnLeave.class, BeforeLeaveEvent.class);
		final Map<URLParameterType, Map<String, NavigationParameterDefinition>> parameters = detectURLParameters(
				navigationTarget, navigationParameterMapper);
		this.queryParameterDefinitions = parameters.get(URLParameterType.QUERY);
		this.pathParameterDefinitions = parameters.get(URLParameterType.PATH).values();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.navigator.internal.NavigationTargetConfiguration#getNavigationTarget()
	 */
	@Override
	public Class<?> getNavigationTarget() {
		return navigationTarget;
	}

	/**
	 * Get the {@link NavigationParameterMapper} bound to this configuration.
	 * @return the navigation parameter mapper
	 */
	public NavigationParameterMapper getNavigationParameterMapper() {
		return navigationParameterMapper;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.navigator.internal.NavigationTargetConfiguration#getRoutePath()
	 */
	@Override
	public Optional<String> getRoutePath() {
		return Optional.ofNullable(routePath);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.navigator.internal.NavigationTargetConfiguration#getCaption()
	 */
	@Override
	public Optional<Localizable> getCaption() {
		return Optional.ofNullable(caption);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.navigator.internal.NavigationTargetConfiguration#getURLParameters()
	 */
	@Override
	public Map<String, NavigationParameterDefinition> getQueryParameters() {
		return queryParameterDefinitions;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.navigator.internal.NavigationTargetConfiguration#getPathParameters()
	 */
	@Override
	public Collection<NavigationParameterDefinition> getPathParameters() {
		return pathParameterDefinitions;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.navigator.internal.NavigationTargetConfiguration#getOnShowMethods()
	 */
	@Override
	public List<Method> getOnShowMethods() {
		return onShowMethods;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.navigator.internal.NavigationTargetConfiguration#getOnLeaveMethods()
	 */
	@Override
	public List<Method> getOnLeaveMethods() {
		return onLeaveMethods;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.navigator.internal.NavigationTargetConfiguration#isAuthenticationRequired()
	 */
	@Override
	public boolean isAuthenticationRequired() {
		return authenticationRequired;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.navigator.internal.NavigationTargetConfiguration#getAuthentication()
	 */
	@Override
	public Optional<Authenticate> getAuthentication() {
		return Optional.ofNullable(authenticate);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.navigator.internal.NavigationTargetConfiguration#getAuthorization()
	 */
	@Override
	public Set<String> getAuthorization() {
		return authorization;
	}

	/**
	 * Get the authorization roles checking standard security annotations.
	 * @param navigationTarget Navigation target class
	 * @return Optional authorization roles
	 */
	private static Optional<Set<String>> getAuthorizationRoles(Class<?> navigationTarget) {
		if (isSecurityAnnotationsPresent(navigationTarget.getClassLoader())) {
			if (navigationTarget.isAnnotationPresent(PermitAll.class)) {
				return Optional.ofNullable(Collections.emptySet());
			}
			if (navigationTarget.isAnnotationPresent(RolesAllowed.class)) {
				return Optional.of(Arrays.asList(navigationTarget.getAnnotation(RolesAllowed.class).value()).stream()
						.filter(r -> r != null).filter(r -> !r.trim().equals("")).collect(Collectors.toSet()));
			}
		}
		return Optional.empty();
	}

	/**
	 * Detect the valid navigation target class methods with given annotation.
	 * @@param navigationTarget Navigation target class
	 * @param annotationType Method annotation type
	 * @param optionalParameterType Optional method parameter type
	 * @return List of valid methods
	 * @throws NavigationTargetConfigurationException If a method validation fails
	 */
	private static List<Method> detectAnnotatedMethods(Class<?> navigationTarget,
			Class<? extends Annotation> annotationType, Class<?> optionalParameterType)
			throws NavigationTargetConfigurationException {
		final List<Method> methods = new LinkedList<>();
		getPublicAnnotatedMethods(navigationTarget, annotationType).forEach(method -> {
			methods.add(validateMethod(navigationTarget, annotationType, optionalParameterType, method));
		});
		return methods;
	}

	/**
	 * Check given method to control if it is valid for given annotation type and optional parameter type.
	 * @param navigationTarget Navigation target class
	 * @param annotationType Method annotation type
	 * @param optionalParameterType Optional method parameter type
	 * @param method The method to check
	 * @return the validated method
	 * @throws NavigationTargetConfigurationException If method validation fails
	 */
	private static Method validateMethod(Class<?> navigationTarget, Class<? extends Annotation> annotationType,
			Class<?> optionalParameterType, Method method) throws NavigationTargetConfigurationException {
		if (method.getReturnType() != Void.class && method.getReturnType() != Void.TYPE) {
			throw new NavigationTargetConfigurationException(
					"Invalid " + annotationType.getSimpleName() + " annotated method in navigation target class ["
							+ navigationTarget.getName() + "]: method must return void");
		}
		if (method.getParameterCount() > 1) {
			throw new NavigationTargetConfigurationException("Invalid " + annotationType.getSimpleName()
					+ " annotated method in navigation target class [" + navigationTarget.getName()
					+ "]: method must have either no parameters or only one parameter of type ["
					+ optionalParameterType.getName() + "]");
		}
		if (method.getParameterCount() == 1) {
			final Parameter param = method.getParameters()[0];
			if (param.isVarArgs() || !(optionalParameterType.isAssignableFrom(param.getType()))) {
				throw new NavigationTargetConfigurationException("Invalid " + annotationType.getSimpleName()
						+ " annotated method in navigation target class [" + navigationTarget.getName()
						+ "]: method must have either no parameters or only one parameter of type ["
						+ optionalParameterType.getName() + "]");
			}
		}
		return method;
	}

	/**
	 * Detect the {@link ViewParameter} annotated fields ong given navigation target class.
	 * @param navigationTarget navigation target class
	 * @param navigationParameterMapper The {@link NavigationParameterMapper} to use
	 * @return The detected navigation parameters definitions
	 * @throws NavigationTargetConfigurationException If a parameter configuration error occurred
	 */
	private static Map<URLParameterType, Map<String, NavigationParameterDefinition>> detectURLParameters(
			Class<?> navigationTarget, NavigationParameterMapper navigationParameterMapper)
			throws NavigationTargetConfigurationException {
		try {
			final Map<String, NavigationParameterDefinition> queryParameters = new HashMap<>(8);
			final Map<String, NavigationParameterDefinition> pathParameters = new HashMap<>(8);
			// get property descriptors
			final Map<String, PropertyDescriptor> propertyDescriptors = getPropertyDescriptors(navigationTarget);
			// get annotated fields
			getAnnotatedFields(navigationTarget, ViewParameter.class).forEach(field -> {
				// get annotation
				final ViewParameter annotation = field.getAnnotation(ViewParameter.class);
				if (annotation != null) {
					// check not final
					if (Modifier.isFinal(field.getModifiers())) {
						throw new NavigationTargetConfigurationException(
								"The navigation target class [" + navigationTarget.getName() + "] field ["
										+ field.getName() + "] must not be declared as final");
					}
					// parameter name
					final String name = getNavigationParameterName(field, annotation.value());
					// parameter type
					final Class<?> type = getNavigationParameterType(navigationTarget, field);
					final ParameterContainerType containerType = getNavigationParameterContainerType(field);
					// definition
					final DefaultNavigationParameterDefinition definition = new DefaultNavigationParameterDefinition(
							name, annotation.type(), type, containerType, field);
					definition.setRequired(annotation.required());
					// default value
					navigationParameterMapper
							.deserialize(type, AnnotationUtils.getStringValue(annotation.defaultValue()))
							.ifPresent(value -> {
								definition.setDefaultValue(value);
							});
					// getter and setter
					PropertyDescriptor propertyDescriptor = propertyDescriptors.get(field.getName());
					if (propertyDescriptor != null) {
						// check methods parameter consistency
						if (propertyDescriptor.getReadMethod() != null && TypeUtils
								.isAssignable(propertyDescriptor.getReadMethod().getReturnType(), field.getType())) {
							definition.setReadMethod(propertyDescriptor.getReadMethod());
						}
						if (propertyDescriptor.getWriteMethod() != null
								&& propertyDescriptor.getWriteMethod().getParameterTypes().length == 1
								&& TypeUtils.isAssignable(propertyDescriptor.getWriteMethod().getParameterTypes()[0],
										field.getType())) {
							definition.setWriteMethod(propertyDescriptor.getWriteMethod());
						}
					}
					// check type
					switch (annotation.type()) {
					case PATH: {
						// avoid duplicates
						if (pathParameters.values().contains(definition)) {
							throw new NavigationTargetConfigurationException(
									"Duplicate " + annotation.type().name() + " parameter name: " + name
											+ " in navigation target class [" + navigationTarget.getName() + "]");
						}
						// add definition
						pathParameters.put(name, definition);
					}
						break;
					case QUERY:
					default: {
						// avoid duplicates
						if (queryParameters.values().contains(definition)) {
							throw new NavigationTargetConfigurationException(
									"Duplicate " + annotation.type().name() + " parameter name: " + name
											+ " in navigation target class [" + navigationTarget.getName() + "]");
						}
						// add definition
						queryParameters.put(name, definition);
					}
						break;
					}
				}
			});
			final Map<URLParameterType, Map<String, NavigationParameterDefinition>> parameters = new HashMap<>(2);
			parameters.put(URLParameterType.QUERY, queryParameters);
			parameters.put(URLParameterType.PATH, pathParameters);
			return parameters;
		} catch (NavigationTargetConfigurationException e) {
			throw e;
		} catch (Exception e) {
			throw new NavigationTargetConfigurationException(
					"Navigation target class [" + navigationTarget.getName() + "] parameters detection failed", e);
		}
	}

	/**
	 * Get the parameter name, either using the annotation name or the field name if empty.
	 * @param field The parameter field
	 * @param name The parameter annotation name
	 * @return The parameter name
	 */
	private static String getNavigationParameterName(Field field, String name) {
		if (name.trim().equals("")) {
			return field.getName();
		}
		return name.trim();
	}

	/**
	 * Validate and get the navigation parameter field type.
	 * @param navigationTarget the navigation target class
	 * @param field the parameter field
	 * @return the parameter field type
	 * @throws NavigationTargetConfigurationException If type validation fails
	 */
	private static Class<?> getNavigationParameterType(Class<?> navigationTarget, Field field)
			throws NavigationTargetConfigurationException {
		final Class<?> type = field.getType();
		// check container
		if (List.class.isAssignableFrom(type) || Set.class.isAssignableFrom(type)) {
			// check element type
			final Class<?> elementType = getParametrizedElementType(field);
			if (elementType != null) {
				return validateParameterType(navigationTarget, field, elementType);
			}
			return type;
		}
		return validateParameterType(navigationTarget, field, type);
	}

	/**
	 * Validate the navigation parameter field type.
	 * @param navigationTarget the navigation target class
	 * @param field the parameter field
	 * @param type The field type
	 * @return The field type
	 * @throws NavigationTargetConfigurationException If validation fails
	 */
	private static Class<?> validateParameterType(Class<?> navigationTarget, Field field, Class<?> type)
			throws NavigationTargetConfigurationException {
		if (!(TypeUtils.isString(type) || TypeUtils.isNumber(type) || TypeUtils.isBoolean(type)
				|| TypeUtils.isDate(type) || TypeUtils.isLocalTemporal(type) || TypeUtils.isEnum(type))) {
			throw new NavigationTargetConfigurationException("Invalid navigation target class ["
					+ navigationTarget.getName() + "] parameter field [" + field.getName() + "] type: " + type);
		}
		return type;
	}

	/**
	 * Get the navigation parameter container type.
	 * @param field the parameter field
	 * @return the parameter container type
	 */
	private static ParameterContainerType getNavigationParameterContainerType(Field field) {
		if (List.class.isAssignableFrom(field.getType())) {
			return ParameterContainerType.LIST;
		}
		if (Set.class.isAssignableFrom(field.getType())) {
			return ParameterContainerType.SET;
		}
		return ParameterContainerType.NONE;
	}

	private static Class<?> getParametrizedElementType(Field field) {
		try {
			final ParameterizedType pt = (ParameterizedType) field.getGenericType();
			final Type[] types = pt.getActualTypeArguments();
			if (types != null && types.length > 0) {
				return (Class<?>) types[0];
			}
			return null;
		} catch (Exception e) {
			LOGGER.warn("Failed to detect parametrized element type for field name [" + field.getName() + "]", e);
			return null;
		}
	}

	/**
	 * Get the <code>public</code> annotated class methods with given <code>annotationType</code>, inspecting the class
	 * hierarchy.
	 * @param cls The class to inspect
	 * @param annotationType The annotation class to detect
	 * @return Stream of <code>public</code> annotated class methods
	 */
	private static Stream<Method> getPublicAnnotatedMethods(Class<?> cls, Class<? extends Annotation> annotationType) {
		final List<Method> methods = new LinkedList<>();
		Class<?> currentClass = cls;
		while (currentClass != null) {
			for (final Method method : currentClass.getDeclaredMethods()) {
				if (method.isAnnotationPresent(annotationType) && Modifier.isPublic(method.getModifiers())) {
					methods.add(method);
				}
			}
			currentClass = currentClass.getSuperclass();
		}
		// reverse order to start from parent classes
		Collections.reverse(methods);
		return methods.stream();
	}

	/**
	 * Get the <code>public</code> annotated class methods with given <code>annotationType</code>, inspecting the class
	 * hierarchy.
	 * @param cls The class to inspect
	 * @param annotationType The annotation class to detect
	 * @return Stream of annotated fields
	 */
	private static Stream<Field> getAnnotatedFields(Class<?> cls, Class<? extends Annotation> annotationType) {
		final List<Field> fields = new LinkedList<>();
		Class<?> currentClass = cls;
		while (currentClass != null) {
			for (final Field field : currentClass.getDeclaredFields()) {
				if (field.isAnnotationPresent(annotationType)) {
					fields.add(field);
				}
			}
			currentClass = currentClass.getSuperclass();
		}
		return fields.stream();
	}

	/**
	 * Get a field name - property descriptor map for given class.
	 * @param cls The class to inspect
	 * @return The field property descriptors map
	 * @throws IntrospectionException If an error occurred
	 */
	private static Map<String, PropertyDescriptor> getPropertyDescriptors(Class<?> cls) throws IntrospectionException {
		final PropertyDescriptor[] propertyDescriptors = Introspector.getBeanInfo(cls).getPropertyDescriptors();
		if (propertyDescriptors != null) {
			final Map<String, PropertyDescriptor> map = new HashMap<>(propertyDescriptors.length);
			for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {
				map.put(propertyDescriptor.getName(), propertyDescriptor);
			}
			return map;
		}
		return Collections.emptyMap();
	}

	/**
	 * Security annotations presence in classpath for classloader
	 */
	private static final Map<ClassLoader, Boolean> SECURITY_ANNOTATIONS_PRESENT = new WeakHashMap<>();

	/**
	 * Checks whether security annotations are available from classpath.
	 * @param classLoader ClassLoader to use
	 * @return <code>true</code> if present
	 */
	private static boolean isSecurityAnnotationsPresent(ClassLoader classLoader) {
		if (SECURITY_ANNOTATIONS_PRESENT.containsKey(classLoader)) {
			Boolean present = SECURITY_ANNOTATIONS_PRESENT.get(classLoader);
			return (present != null && present.booleanValue());
		}
		boolean present = ClassUtils.isPresent("javax.annotation.security.RolesAllowed", classLoader);
		SECURITY_ANNOTATIONS_PRESENT.put(classLoader, present);
		return present;
	}

}
