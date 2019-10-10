/*   This file is part of lanSimulation.
 *
 *   lanSimulation is free software; you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation; either version 2 of the License, or
 *   (at your option) any later version.
 *
 *   lanSimulation is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with lanSimulation; if not, write to the Free Software
 *   Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 *
 *   Copyright original Java version: 2004 Bart Du Bois, Serge Demeyer
 *   Copyright C++ version: 2006 Matthias Rieger, Bart Van Rompaey
 */
package lanSimulation.internals;

import lanSimulation.Network;

import java.io.IOException;
import java.io.Writer;

/**
 * A <em>Packet</em> represents a unit of information to be sent over the Local
 * Area Network (LAN).
 */
public class Packet {
	/**
	 * Holds the actual message to be send over the network.
	 */
	public String message;
	/**
	 * Holds the name of the Node which initiated the request.
	 */
	public String origin;
	/**
	 * Holds the name of the Node which should receive the information.
	 */
	public String destination;

	/**
	 * Construct a <em>Packet</em> with given #message and #destination.
	 */
	public Packet(String _message, String _destination) {
		message = _message;
		origin = "";
		destination = _destination;
	}

	/**
	 * Construct a <em>Packet</em> with given #message, #origin and #receiver.
	 */
	public Packet(String _message, String _origin, String _destination) {
		message = _message;
		origin = _origin;
		destination = _destination;
	}

    public boolean printDocument(Node printer, Writer report) {
        String author = "Unknown";
        String title = "Untitled";
        int startPos = 0, endPos = 0;

        if (printer.type == Node.PRINTER) {
            try {
                if (message.startsWith("!PS")) {
                    startPos = message.indexOf("author:");
                    if (startPos >= 0) {
                        endPos = message.indexOf(".", startPos + 7);
                        if (endPos < 0) {
                            endPos = message.length();
                        }

                        author = message.substring(startPos + 7,
                                endPos);
                    }

                    startPos = message.indexOf("title:");
                    if (startPos >= 0) {
                        endPos = message.indexOf(".", startPos + 6);
                        if (endPos < 0) {
                            endPos = message.length();
                        }
                        title = message
                                .substring(startPos + 6, endPos);
                    }

                    printAccounting(report, author, title, ">>> Postscript job delivered.\n\n");
                } else {
                    title = "ASCII DOCUMENT";
                    if (message.length() >= 16) {
                        author = message.substring(8, 16);
                    }

                    printAccounting(report, author, title, ">>> ASCII Print job delivered.\n\n");
                }

            } catch (IOException exc) {
                // just ignore
            }
            return true;
        } else {
            try {
                report.write(">>> Destination is not a printer, print job canceled.\n\n");
                report.flush();
            } catch (IOException exc) {
                // just ignore
            }
            return false;
        }
    }

    private void printAccounting(Writer report, String author, String title, String s) throws IOException {
        report.write("\tAccounting -- author = '");
        report.write(author);
        report.write("' -- title = '");
        report.write(title);
        report.write("'\n");
        report.write(s);
        report.flush();
    }

    public boolean atDestination(Node currentNode) {
        return destination.equals(currentNode.name);
    }

    public boolean atOrigin(Node currentNode) {
        return origin.equals(currentNode.name);
    }
}