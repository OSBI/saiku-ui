package main;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import org.testng.IMethodInstance;
import org.testng.IMethodInterceptor;
import org.testng.ITestContext;

/**
 * Refer to the web page below:
 * http://beust.com/weblog/2008/03/29/test-method-priorities-in-testng/
 */

public class PriorityInterceptor implements IMethodInterceptor {

	@Override
	public List<IMethodInstance> intercept(List<IMethodInstance> methods, ITestContext context) {
		Comparator<IMethodInstance> comparator = new Comparator<IMethodInstance>() {

			private int getPriority(IMethodInstance mi) {
				int result = 0;

				Method method = mi.getMethod().getConstructorOrMethod().getMethod();
				Priority a1 = method.getAnnotation(Priority.class);
				if (a1 != null) {
					result = a1.value();
				} else {
					Class<?> cls = method.getDeclaringClass();
					Priority classPriority = cls.getAnnotation(Priority.class);
					if (classPriority != null) {
						result = classPriority.value();
					}
				}
				return result;
			}

			@Override
			public int compare(IMethodInstance m1, IMethodInstance m2) {
				return getPriority(m1) - getPriority(m2);
			}

		};
		IMethodInstance[] array = methods.toArray(new IMethodInstance[methods.size()]);
		Arrays.sort(array, comparator);

		return Arrays.asList(array);
	}

}