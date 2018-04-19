/**
 * $RCSfile: Connection.java,v $
 * $Revision: 3187 $
 * $Date: 2005-12-11 13:34:34 -0300 (Sun, 11 Dec 2005) $
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

package com.pft.communication.server.connector.connection;

import java.io.Serializable;
import java.net.UnknownHostException;

import com.pft.communicate.protocal.packet.PFTPacket;
import com.pft.communicate.protocal.parser.CompressStrategy;
import com.pft.communication.server.connector.session.LocalClientSession;

/**
 * Represents a connection on the server.
 *
 * @author Iain Shigeoka
 */
public interface ClientConnection extends Serializable{


	/**
	 * Verifies that the connection is still live. Typically this is done by
	 * sending a whitespace character between packets.
	 *
	 * @return true if the socket remains valid, false otherwise.
	 */
	public boolean validate();

	/**
	 * Initializes the connection with it's owning session. Allows the
	 * connection class to configure itself with session related information
	 * (e.g. stream ID).
	 *
	 * @param session
	 *            the session that owns this connection
	 */
	public void init(LocalClientSession session);

	/**
	 * Returns the raw IP address of this <code>InetAddress</code> object. The
	 * result is in network byte order: the highest order byte of the address is
	 * in <code>getAddress()[0]</code>.
	 *
	 * @return the raw IP address of this object.
	 * @throws java.net.UnknownHostException
	 *             if IP address of host could not be determined.
	 */
	public byte[] getAddress() throws UnknownHostException;

	/**
	 * Returns the IP address string in textual presentation.
	 *
	 * @return the raw IP address in a string format.
	 * @throws java.net.UnknownHostException
	 *             if IP address of host could not be determined.
	 */
	public String getHostAddress() throws UnknownHostException;

	/**
	 * Gets the host name for this IP address.
	 *
	 * <p>
	 * If this InetAddress was created with a host name, this host name will be
	 * remembered and returned; otherwise, a reverse name lookup will be
	 * performed and the result will be returned based on the system configured
	 * name lookup service. If a lookup of the name service is required, call
	 * {@link java.net.InetAddress#getCanonicalHostName() getCanonicalHostName}.
	 *
	 * <p>
	 * If there is a security manager, its <code>checkConnect</code> method is
	 * first called with the hostname and <code>-1</code> as its arguments to
	 * see if the operation is allowed. If the operation is not allowed, it will
	 * return the textual representation of the IP address.
	 *
	 * @return the host name for this IP address, or if the operation is not
	 *         allowed by the security check, the textual representation of the
	 *         IP address.
	 * @throws java.net.UnknownHostException
	 *             if IP address of host could not be determined.
	 *
	 * @see java.net.InetAddress#getCanonicalHostName
	 * @see SecurityManager#checkConnect
	 */
	public String getHostName() throws UnknownHostException;

	/**
	 * Close this session including associated socket connection. The order of
	 * events for closing the session is:
	 * <ul>
	 * <li>Set closing flag to prevent redundant shutdowns.
	 * <li>Call notifyEvent all listeners that the channel is shutting down.
	 * <li>Close the socket.
	 * </ul>
	 */
	public void closeNow();

	public void closeOnFlush();

	/**
	 * Notification message indicating that the server is being shutdown.
	 * Implementors should send a stream error whose condition is
	 * system-shutdown before closing the connection.
	 */
	public void systemShutdown();

	/**
	 * Returns true if the connection/session is closed.
	 *
	 * @return true if the connection is closed.
	 */
	public boolean isClosed();

	/**
	 * Returns true if this connection is secure.
	 *
	 * @return true if the connection is secure (e.g. SSL/TLS)
	 */
	public boolean isSecure();

	public void deliver(PFTPacket packet);

	/**
	 * 根据StreamID向客户端发送JumpPacket
	 *
	 * @param streamID
	 * @param packet
	 */
	public void deliver(String streamID, PFTPacket packet);

	/**
	 * set Compression strategy
	 *
	 * @param strategy
	 */
	void setCompressStrategy(CompressStrategy strategy);

	/**
	 * get Compression strategy
	 */
	CompressStrategy getCompressStrategy();
}
