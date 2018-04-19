package com.pft.communication.server.connector.mina;

import java.io.IOException;

/***
 * 连接管理接口
 * 
 * @author majun@12301.cc
 *
 */
public interface ConnectionManager {

	final int DEFAULT_PORT = 5222;

	final int DEFAULT_SSL_PORT = 5223;

	void createClientListener();

	void createSSLClientListener();

	void startClientListener() throws IOException;

	void startSSLClientListener() throws IOException;

	void stopClientListeners();

}
