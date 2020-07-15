package com.holonplatform.vaadin.flow.components.builders;

import com.holonplatform.vaadin.flow.internal.components.builders.DefaultScrollerBuilder;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.orderedlayout.Scroller.ScrollDirection;

/**
 * Builder to create {@link Scroller} components.
 * 
 * @since 5.5.0
 */
public interface ScrollerBuilder extends ComponentBuilder<Scroller, ScrollerBuilder>,
		HasSizeConfigurator<ScrollerBuilder>, HasStyleConfigurator<ScrollerBuilder> {

	/**
	 * Sets the scroll direction. Defaults to {@link ScrollDirection#BOTH}.
	 * @param scrollDirection {@link ScrollDirection#BOTH} to enable both vertical
	 *                        and horizontal scrollbars.
	 *                        {@link ScrollDirection#HORIZONTAL} to enable only
	 *                        horizontal scrollbars.
	 *                        {@link ScrollDirection#VERTICAL} to enable only
	 *                        vertical scrollbars. {@link ScrollDirection#NONE} to
	 *                        disable both vertical and horizontal scrollbars. (not
	 *                        null)
	 * @return this
	 */
	ScrollerBuilder scrollDirection(ScrollDirection scrollDirection);

	/**
	 * Sets the content of the scroller.
	 * @param content A component to use as content
	 * @return this
	 */
	ScrollerBuilder content(Component content);

	/**
	 * Create a new {@link ScrollerBuilder}.
	 * @return A new {@link ScrollerBuilder} instance
	 */
	static ScrollerBuilder create() {
		return new DefaultScrollerBuilder();
	}

}
