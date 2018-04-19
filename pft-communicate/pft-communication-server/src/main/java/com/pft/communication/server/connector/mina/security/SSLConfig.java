/**
 * $RCSfile$
 * $Revision: 1217 $
 * $Date: 2005-04-11 18:11:06 -0300 (Mon, 11 Apr 2005) $
 *
 * Copyright (C) 2005-2008 Jive Software. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package  com.pft.communication.server.connector.mina.security;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.KeyStore;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

import org.apache.commons.configuration2.ImmutableConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pft.communication.server.PftCommuctionServerStarter;

/**
 * Configuration of Openfire's SSL settings.
 *
 * @author Iain Shigeoka
 */
public class SSLConfig {

	private static final Logger logger = LoggerFactory.getLogger(SSLConfig.class);

	private static String storeType;
	private static SSLContext c2sContext;

	private static KeyStore keyStore;
	private static String keyStoreLocation;
	private static String keypass;

	private static KeyStore c2sTrustStore;
	private static String c2sTrustStoreLocation;
	private static String c2sTrustpass;

	private static ImmutableConfiguration configuration = PftCommuctionServerStarter.getInstance().getConfiguration();

	private SSLConfig() {
	}

	static {

		storeType = configuration.getString("xmpp.socket.ssl.storeType", "jks");

		String confDirectory = PftCommuctionServerStarter.getInstance().getConfDirectory();

		// TODO 修改keystore文件名 keystore
		// Get the keystore location. The default location is security/keystore
		keyStoreLocation = configuration.getString("xmpp.socket.ssl.keystore", "security" + File.separator + "kserver.keystore");
		keyStoreLocation = confDirectory + keyStoreLocation;

		// Get the keystore password. The default password is "changeit".
		keypass = configuration.getString("xmpp.socket.ssl.keypass", "changeit");
		keypass = keypass.trim();

		// TODO 修改client.truststore为tserver.keystore
		// Get the truststore location for c2s connections
		c2sTrustStoreLocation = configuration.getString("xmpp.socket.ssl.client.truststore",
				"conf" + File.separator + "security" + File.separator + "tserver.keystore");
		c2sTrustStoreLocation = confDirectory + c2sTrustStoreLocation;

		c2sTrustpass = configuration.getString("xmpp.socket.ssl.client.trustpass", "changeit");
		c2sTrustpass = c2sTrustpass.trim();

		// Load s2s keystore
		try {
			keyStore = KeyStore.getInstance(storeType);
			keyStore.load(new FileInputStream(keyStoreLocation), keypass.toCharArray());
		} catch (Exception e) {
			logger.error("SSLConfig startup problem.\n" + "  storeType: [" + storeType + "]\n" + "  keyStoreLocation: [" + keyStoreLocation + "]\n"
					+ "  keypass: [" + keypass + "]\n", e);
			keyStore = null;
		}

		// Load c2s truststore
		try {
			c2sTrustStore = KeyStore.getInstance(storeType);
			c2sTrustStore.load(new FileInputStream(c2sTrustStoreLocation), c2sTrustpass.toCharArray());
		} catch (Exception e) {
			try {
				c2sTrustStore = KeyStore.getInstance(storeType);
				c2sTrustStore.load(null, c2sTrustpass.toCharArray());
			} catch (Exception ex) {
				logger.error(
						"SSLConfig startup problem.\n" + "  storeType: [" + storeType + "]\n" + "  c2sTrustStoreLocation: [" + c2sTrustStoreLocation
								+ "]\n"
								+ "  c2sTrustPass: [" + c2sTrustpass + "]",
						e);
				c2sTrustStore = null;
			}
		}
		resetFactory();
	}

	private static void resetFactory() {
		try {
			String algorithm = configuration.getString("xmpp.socket.ssl.algorithm", "TLS");

			c2sContext = SSLContext.getInstance(algorithm);

			KeyManagerFactory keyFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
			keyFactory.init(keyStore, SSLConfig.getKeyPassword().toCharArray());

			TrustManagerFactory c2sTrustFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
			c2sTrustFactory.init(c2sTrustStore);

			c2sContext.init(keyFactory.getKeyManagers(),
					c2sTrustFactory.getTrustManagers(),
					new java.security.SecureRandom());

		} catch (Exception e) {
			logger.error(
					"SSLConfig factory setup problem.\n" + "  storeType: [" + storeType + "]\n" + "  keyStoreLocation: [" + keyStoreLocation + "]\n"
							+ "  keypass: [" + keypass + "]\n" + "  c2sTrustStoreLocation: [" + c2sTrustStoreLocation + "]\n" + "  c2sTrustpass: ["
							+ c2sTrustpass
							+ "]",
					e);
			keyStore = null;
			c2sTrustStore = null;
		}
	}

	/**
	 * Get the Key Store password
	 *
	 * @return the key store password
	 */
	public static String getKeyPassword() {
		return keypass;
	}

	/**
	 * Get the Key Store
	 *
	 * @return the Key Store
	 */
	public static KeyStore getKeyStore() throws IOException {
		if (keyStore == null) {
			throw new IOException();
		}
		return keyStore;
	}

	/**
	 * Get the Trust Store for c2s connections
	 *
	 * @return the c2s Trust Store
	 */
	public static KeyStore getc2sTrustStore() throws IOException {
		if (c2sTrustStore == null) {
			throw new IOException();
		}
		return c2sTrustStore;
	}

	/**
	 * Initializes (wipes and recreates) the keystore, and returns the new keystore.
	 *
	 * @return Newly initialized keystore.
	 */
	public static KeyStore initializeKeyStore() {
		try {
			keyStore = KeyStore.getInstance(storeType);
			keyStore.load(null, keypass.toCharArray());
		} catch (Exception e) {
			logger.error("Unable to initialize keystore: ", e);
		}
		return keyStore;
	}

	/**
	 * Get the Key Store location
	 *
	 * @return the keystore location
	 */
	public static String getKeystoreLocation() {
		return keyStoreLocation;
	}

	/**
	 * Get the c2s Trust Store location
	 *
	 * @return the c2s Trust Store location
	 */
	public static String getc2sTruststoreLocation() {
		return c2sTrustStoreLocation;
	}

	public static String getStoreType() {
		return storeType;
	}

	/**
	 * Get the SSLContext for c2s connections
	 *
	 * @return the SSLContext for c2s connections
	 */
	public static SSLContext getc2sSSLContext() {
		return c2sContext;
	}
}
