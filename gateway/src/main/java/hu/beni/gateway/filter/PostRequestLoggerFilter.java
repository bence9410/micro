package hu.beni.gateway.filter;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
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
		HttpServletResponse response = RequestContext.getCurrentContext().getResponse();
		
		Map<String, String> map = new HashMap<>();
		for (String headerName : response.getHeaderNames()) {
			map.put(headerName, response.getHeader(headerName));
		}

		log.info(String.format("Response headers: %s", map));

		return null;
	}
}
