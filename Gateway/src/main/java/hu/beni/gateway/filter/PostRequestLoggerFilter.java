package hu.beni.gateway.filter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PostRequestLoggerFilter  extends ZuulFilter {

	@Override
	public String filterType() {
		return "post";
	}

	@Override
	public int filterOrder() {
		return 1;
	}

	@Override
	public boolean shouldFilter() {
		return true;
	}

	@Override
	public Object run() {
		
/*		List<Pair<String, String>> headers = RequestContext.getCurrentContext().getZuulResponseHeaders();
		
		Map<String, String> map = new HashMap<>();
		
		for (Pair<String, String> header : headers) {
			map.put(header.first(), header.second());
		}
	*/	
		//log.info(String.format("Response headers: %s", map));

		return null;
	}
}
