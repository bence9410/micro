package hu.beni.gateway.filter;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Component;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class PreRequestLoggerFilter extends ZuulFilter {

	@Override
	public String filterType() {
		return "pre";
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
		RequestContext ctx = RequestContext.getCurrentContext();
		HttpServletRequest request = ctx.getRequest();
		
		Map<String, String> map = new HashMap<>();
		Enumeration<String> asd = request.getHeaderNames();
		while (asd.hasMoreElements()){
			String name = asd.nextElement();
			map.put(name, request.getHeader(name));
		}
		
		log.info(String.format("%s request to %s headers: %s", request.getMethod(),
				request.getRequestURL(), map));

		return null;
	}
}
