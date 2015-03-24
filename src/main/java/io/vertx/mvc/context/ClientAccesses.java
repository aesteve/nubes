package io.vertx.mvc.context;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ClientAccesses {
	List<Long> history;
	
	public ClientAccesses() {
		history = new ArrayList<Long>();
	}
	
	public void newAccess() {
		history.add(System.currentTimeMillis());
	}
	
	public boolean isOverLimit(RateLimit limit) {
		long minBound = System.currentTimeMillis() - limit.getTimeUnit().toMillis(limit.getValue());
		return history.stream().filter(access -> {
			return access > minBound;
		}).count() <= limit.getCount();
	}
	
	public void clearHistory(Long keepAfter) {
		history = history.stream().filter(access -> {
			return access > keepAfter;
		}).collect(Collectors.toList());
	}
	
	public boolean noAccess() {
		return history.isEmpty();
	}
}
