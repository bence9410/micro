package hu.beni.amusementpark.config;

import static hu.beni.amusementpark.constants.SpringProfileConstants.DEFAULT;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.aop.ClassFilter;
import org.springframework.aop.MethodMatcher;
import org.springframework.aop.Pointcut;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import hu.beni.amusementpark.service.GuestBookRegistryService;
import hu.beni.amusementpark.service.impl.AmusementParkServiceImpl;
import hu.beni.amusementpark.service.impl.DefaultVisitorServiceImpl;
import hu.beni.amusementpark.service.impl.MachineServiceImpl;
import hu.beni.amusementpark.service.impl.StatisticsSenderVisitorServiceImpl;
import lombok.extern.slf4j.Slf4j;

@Configuration
@Slf4j
public class TransactionLoggerConfig {

	private static final Set<Class> targetClasses;

	static {
		targetClasses = new HashSet<>();
		targetClasses.add(AmusementParkServiceImpl.class);
		targetClasses.add(MachineServiceImpl.class);
		targetClasses.add(DefaultVisitorServiceImpl.class);
		targetClasses.add(StatisticsSenderVisitorServiceImpl.class);
		targetClasses.add(GuestBookRegistryService.class);
	}

	@Bean
	@Profile(DEFAULT)
	public DefaultPointcutAdvisor transactionLoggerAdvisor() {
		return new DefaultPointcutAdvisor(createPointcut(), createMethodInterceptor());
	}

	private Pointcut createPointcut() {
		return new Pointcut() {

			@Override
			public MethodMatcher getMethodMatcher() {
				return createMethodMatcher();
			}

			@Override
			public ClassFilter getClassFilter() {
				return targetClasses::contains;
			}
		};
	}

	private MethodMatcher createMethodMatcher() {
		return new MethodMatcher() {

			@Override
			public boolean matches(Method method, Class<?> targetClass, Object... args) {
				return true;
			}

			@Override
			public boolean matches(Method method, Class<?> targetClass) {
				return true;
			}

			@Override
			public boolean isRuntime() {
				return false;
			}
		};
	}

	private MethodInterceptor createMethodInterceptor() {
		return methodInvocation -> {
			long start = System.currentTimeMillis();
			try {
				Object obj = methodInvocation.proceed();
				logResult(methodInvocation, start, "successfully");
				return obj;
			} catch (Throwable t) {
				logResult(methodInvocation, start, "exceptionally");
				throw t;
			}
		};
	}

	private void logResult(MethodInvocation methodInvocation, long start, String result) {
		log.info(methodInvocation.getThis().getClass().getSimpleName() + "." + methodInvocation.getMethod().getName()
				+ " completed " + result + " in " + Long.toString(System.currentTimeMillis() - start) + "ms.");
	}

}
