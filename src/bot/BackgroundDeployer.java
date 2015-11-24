package bot;

import java.lang.reflect.Method;

import javax.swing.SwingWorker;


public class BackgroundDeployer extends SwingWorker<Object, Object> {
	
	private Method method;
	
	private Object[] args;
	
	public BackgroundDeployer(Method method, Object[] args) {
		this.method = method;
		this.args = args;
	}

	@Override
	protected Object doInBackground() throws Exception {
		method.invoke(method.getDeclaringClass(), args);
		return true;
	}

}
