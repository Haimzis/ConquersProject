package Server.Utils;
import GameEngine.GameEngine;
import Server.Chat.ChatManager;
import Server.Users.UserManager;


import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import static Server.Constants.Constants.INT_PARAMETER_ERROR;

public class ServletUtils {

	private static final String USER_MANAGER_ATTRIBUTE_NAME = "userManager";
	private static final String ENGINE_MANAGER_ATTRIBUTE_NAME = "engineManager";
	private static final String ROOMS_MANAGER_ATTRIBUTE_NAME = "roomManager";
	public static int ID = 1;

	/*/
	Note how the synchronization is done only on the question and\or creation of the relevant managers and once they exists -
	the actual fetch of them is remained unchronicled for performance POV
	/*/
	private static final Object userManagerLock = new Object();
	private static final Object engineManagerLock = new Object();
	private static final Object roomsManagerLock = new Object();

	public static UserManager getUserManager(ServletContext servletContext) {
		synchronized (userManagerLock) {
			if (servletContext.getAttribute(USER_MANAGER_ATTRIBUTE_NAME) == null) {
				servletContext.setAttribute(USER_MANAGER_ATTRIBUTE_NAME, new UserManager());
			}
		}
		return (UserManager) servletContext.getAttribute(USER_MANAGER_ATTRIBUTE_NAME);
	}

	public static GameEngine getGameEngine(ServletContext servletContext) {
		synchronized (engineManagerLock) {
			if (servletContext.getAttribute(ENGINE_MANAGER_ATTRIBUTE_NAME) == null) {
				servletContext.setAttribute(ENGINE_MANAGER_ATTRIBUTE_NAME, new GameEngine());
			}
		}
		return (GameEngine) servletContext.getAttribute(ENGINE_MANAGER_ATTRIBUTE_NAME);
	}

	public static RoomsContainer getRoomsContainer(ServletContext servletContext) {
		synchronized (roomsManagerLock) {
			if (servletContext.getAttribute(ROOMS_MANAGER_ATTRIBUTE_NAME) == null) {
				servletContext.setAttribute(ROOMS_MANAGER_ATTRIBUTE_NAME, new RoomsContainer());
			}
		}
		return (RoomsContainer) servletContext.getAttribute(ROOMS_MANAGER_ATTRIBUTE_NAME);
	}


	public static int getVersionIntParameter(HttpServletRequest request, String name) {
		String value = request.getParameter(name);
		if (value != null) {
			try {
				return Integer.parseInt(value);
			} catch (NumberFormatException numberFormatException) {
			}
		}
		return INT_PARAMETER_ERROR;
	}

}
