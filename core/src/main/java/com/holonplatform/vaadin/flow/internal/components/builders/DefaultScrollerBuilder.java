package com.holonplatform.vaadin.flow.internal.components.builders;

import java.util.Optional;

import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.vaadin.flow.components.builders.ScrollerBuilder;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasEnabled;
import com.vaadin.flow.component.HasSize;
import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.orderedlayout.Scroller.ScrollDirection;

/**
 * Default {@link ScrollerBuilder} implementation.
 * 
 * @since 5.5.0
 */
public class DefaultScrollerBuilder extends AbstractComponentConfigurator<Scroller, ScrollerBuilder>
		implements ScrollerBuilder {

	public DefaultScrollerBuilder() {
		super(new Scroller());
	}

	@Override
	protected Optional<HasSize> hasSize() {
		return Optional.of(getComponent());
	}

	@Override
	protected Optional<HasStyle> hasStyle() {
		return Optional.of(getComponent());
	}

	@Override
	protected Optional<HasEnabled> hasEnabled() {
		return Optional.empty();
	}

	@Override
	protected ScrollerBuilder getConfigurator() {
		return this;
	}

	@Override
	public ScrollerBuilder scrollDirection(ScrollDirection scrollDirection) {
		ObjectUtils.argumentNotNull(scrollDirection, "ScrollDirection must be not null");
		getComponent().setScrollDirection(scrollDirection);
		return this;
	}

	@Override
	public ScrollerBuilder content(Component content) {
		getComponent().setContent(content);
		return this;
	}

	@Override
	public Scroller build() {
		return getComponent();
	}

}
