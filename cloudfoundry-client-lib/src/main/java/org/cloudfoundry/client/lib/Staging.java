package org.cloudfoundry.client.lib;

import java.io.Serializable;
import java.util.List;

/**
 * The staging information related to an application. Used for creating the
 * application
 *
 * @author Jennifer Hickey
 *
 */
public class Staging implements Serializable {

	private static final long serialVersionUID = 5484882808921748989L;

	private String runtime;

	private String framework;

	private String command;

	/**
	 *
	 * @param framework
	 *            The application framework
	 */
	public Staging(String framework) {
		this.framework = framework;
	}

	/**
	 *
	 * @return The application runtime. If null, the server will use the default
	 *         runtime associated with the framework
	 */
	public String getRuntime() {
		return runtime;
	}

	/**
	 *
	 * @param runtime
	 *            The application runtime. If null, the server will use the
	 *            default runtime associated with the framwework
	 */
	public void setRuntime(String runtime) {
		this.runtime = runtime;
	}

	/**
	 *
	 * @return The application framework
	 */
	public String getFramework() {
		return framework;
	}

	/**
	 *
	 * @return The start command to use if this app is a standalone app (has
	 *         framework named "standalone")
	 */
	public String getCommand() {
		return command;
	}

	/**
	 *
	 * @param command
	 *            The start command to use if this app is a standalone app (has
	 *            framework named "standalone")
	 */
	public void setCommand(String command) {
		this.command = command;
	}

}
