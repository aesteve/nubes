package com.github.aesteve.vertx.nubes.reflections;

import java.lang.reflect.Method;

import com.github.aesteve.vertx.nubes.annotations.filters.AfterFilter;
import com.github.aesteve.vertx.nubes.annotations.filters.BeforeFilter;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class Filter implements Comparable<Filter> {

	private final int order;
	private final Method method;

	public Filter(Method method, AfterFilter annot) {
		this.method = method;
		this.order = annot.value();
	}

	public Filter(Method method, BeforeFilter annot) {
		this.method = method;
		this.order = annot.value();
	}

	@Override
	public int compareTo(Filter other) {
		if (other == null) {
			return -1;
		}
		return Integer.compare(order, other.order);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) { return false; }
		if (obj == this) { return true; }
		if (obj.getClass() != getClass()) {
			return false;
		}
		Filter filter = (Filter) obj;
		return new EqualsBuilder()
				.append(order, filter.order)
				.append(method, filter.method)
				.isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder()
				.append(method)
				.append(order)
				.toHashCode();
	}


	public Method method() {
		return method;
	}

}
