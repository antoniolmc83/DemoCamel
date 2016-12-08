package com.example;

import org.apache.camel.Exchange;
import org.apache.camel.processor.aggregate.AggregationStrategy;

public class ExampleAggregationStrategy implements AggregationStrategy {

	@Override
	public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {
		Object originalBody = oldExchange.getIn().getBody();
        Object resourceResponse = newExchange.getOut().getBody();
        Object mergeResult = null;//... // combine original body and resource response
                if (oldExchange.getPattern().isOutCapable()) {
                	oldExchange.getOut().setBody(mergeResult);
                } else {
                	oldExchange.getIn().setBody(mergeResult);
                }
                return oldExchange;
	}

}
