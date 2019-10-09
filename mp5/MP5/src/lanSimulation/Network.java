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
package lanSimulation;

import lanSimulation.internals.*;

import java.util.Hashtable;
import java.util.Enumeration;
import java.io.*;

/**
 * A <em>Network</em> represents the basic data structure for simulating a Local
 * Area Network (LAN). The LAN network architecture is a token ring, implying
 * that packages will be passed from one node to another, until they reached
 * their destination, or until they traveled the whole token ring.
 */
public class Network {
	/**
	 * Holds a pointer to some "first" node in the token ring. Used to ensure
	 * that various printing operations return expected behaviour.
	 */
	private Node firstNode;
	/**
	 * Maps the names of workstations on the actual workstations. Used to
	 * initiate the requests for the network.
	 */
	private Hashtable<String, Node> workstations;

	/**
	 * Construct a <em>Network</em> suitable for holding #size Workstations.
	 */
	public Network(int size) {
		assert size > 0;
		firstNode = null;
		workstations = new Hashtable<String, Node>(size, 1.0f);
		assert !consistentNetwork();
	}

	/**
	 * Return a <em>Network</em> that may serve as starting point for various
	 * experiments. Currently, the network looks as follows.
	 * 
	 * <pre>
	 * 	 Workstation Filip [Workstation] -&gt; Node -&gt; Workstation Hans [Workstation]
	 * 	 -&gt; Printer Andy [Printer] -&gt; ...
	 * </pre>
	 */
	public static Network DefaultExample() {
		Network network = new Network(2);

		Node wsFilip = new Node(Node.WORKSTATION, "Filip");
		Node n1 = new Node(Node.NODE, "n1");
		Node wsHans = new Node(Node.WORKSTATION, "Hans");
		Node prAndy = new Node(Node.PRINTER, "Andy");

		wsFilip.nextNode = n1;
		n1.nextNode = wsHans;
		wsHans.nextNode = prAndy;
		prAndy.nextNode = wsFilip;

		network.workstations.put(wsFilip.name, wsFilip);
		network.workstations.put(wsHans.name, wsHans);
		network.firstNode = wsFilip;

		assert network.consistentNetwork();
		return network;
	}

	/**
	 * Answer whether #receiver contains a workstation with the given name.
	 */
	public boolean hasWorkstation(String ws) {
		// return workstations_.containsKey(ws);
		Node n;

		n = workstations.get(ws);
		if (n == null) {
			return false;
		} else {
			return n.type == Node.WORKSTATION;
		}
	}

	/**
	 * Answer whether #receiver is a consistent token ring network. A consistent
	 * token ring network - contains at least one workstation and one printer -
	 * is circular - all registered workstations are on the token ring - all
	 * workstations on the token ring are registered.
	 */
	public boolean consistentNetwork() {
		Enumeration<Node> enumeration;
		Node currentNode;
		int printersFound = 0, workstationsFound = 0;
		Hashtable<String, Node> encountered = new Hashtable<String, Node>(
				workstations.size() * 2, 1.0f);

		if (workstations.isEmpty()) {
			return false;
		}
		if (firstNode == null) {
			return false;
		}
		// verify whether all registered workstations are indeed workstations
		enumeration = workstations.elements();
		while (enumeration.hasMoreElements()) {
			currentNode = enumeration.nextElement();
			if (currentNode.type != Node.WORKSTATION) {
				return false;
			}
		}
		// enumerate the token ring, verifying whether all workstations are
		// registered also count the number of printers and see whether the
		// ring is circular
		currentNode = firstNode;
		while (!encountered.containsKey(currentNode.name)) {
			encountered.put(currentNode.name, currentNode);
			if (currentNode.type == Node.WORKSTATION) {
				workstationsFound++;
			}
			if (currentNode.type == Node.PRINTER) {
				printersFound++;
			}
			currentNode = currentNode.nextNode;
		}
		if (currentNode != firstNode) {
			return false;
		}
		// not circular
		if (printersFound == 0) {
			return false;
		}
		// does not contain a printer
		if (workstationsFound != workstations.size()) {
			return false;
		}
		// not all workstations are registered all verifications succeeded
		return true;
	}

	/**
	 * The #receiver is requested to broadcast a message to all nodes. Therefore
	 * #receiver sends a special broadcast packet across the token ring network,
	 * which should be treated by all nodes.
	 * 
	 * @param report
	 *            Stream that will hold a report about what happened when
	 *            handling the request.
	 * @return Answer #true when the broadcast operation was successful and
	 *         #false otherwise
	 */
	public boolean requestBroadcast(Writer report) {
		assert consistentNetwork();

		try {
			report.write("Broadcast Request\n");
		} catch (IOException exc) {
			// just ignore
		}

		Node currentNode = firstNode;
		Packet packet = new Packet("BROADCAST", firstNode.name, firstNode.name);
		do {
			try {
				report.write("\tNode '");
				report.write(currentNode.name);
				report.write("' accepts broadcast packet.\n");
				report.write("\tNode '");
				report.write(currentNode.name);
				report.write("' passes packet on.\n");
				report.flush();
			} catch (IOException exc) {
				// just ignore
			}

			currentNode = currentNode.nextNode;
		} while (!packet.destination.equals(currentNode.name));

		try {
			report.write(">>> Broadcast traveled whole token ring.\n\n");
		} catch (IOException exc) {
			// just ignore
		}

		return true;
	}

	/**
	 * The #receiver is requested by #workstation to print #document on
	 * #printer. Therefore #receiver sends a packet across the token ring
	 * network, until either (1) #printer is reached or (2) the packet traveled
	 * complete token ring.
	 * 
	 * @param workstation
	 *            Name of the workstation requesting the service.
	 * @param document
	 *            Contents that should be printed on the printer.
	 * @param printer
	 *            Name of the printer that should receive the document.
	 * @param report
	 *            Stream that will hold a report about what happened when
	 *            handling the request.
	 * @return Answer #true when the print operation was successful and #false
	 *         otherwise
	 */
	public boolean requestWorkstationPrintsDocument(String workstation,
			String document, String printer, Writer report) {
		assert consistentNetwork() & hasWorkstation(workstation);

		try {
			report.write("'");
			report.write(workstation);
			report.write("' requests printing of '");
			report.write(document);
			report.write("' on '");
			report.write(printer);
			report.write("' ...\n");
		} catch (IOException exc) {
			// just ignore
		}

		boolean result = false;
		Node startNode, currentNode;
		Packet packet = new Packet(document, workstation, printer);

		startNode = workstations.get(workstation);

		try {
			report.write("\tNode '");
			report.write(startNode.name);
			report.write("' passes packet on.\n");
			report.flush();
		} catch (IOException exc) {
			// just ignore
		}

		currentNode = startNode.nextNode;
		while ((!packet.destination.equals(currentNode.name))
				& (!packet.origin.equals(currentNode.name))) {
			try {
				report.write("\tNode '");
				report.write(currentNode.name);
				report.write("' passes packet on.\n");
				report.flush();
			} catch (IOException exc) {
				// just ignore
			}

			currentNode = currentNode.nextNode;
		}

		if (packet.destination.equals(currentNode.name)) {
			result = printDocument(currentNode, packet, report);
		} else {
			try {
				report.write(">>> Destination not found, print job canceled.\n\n");
				report.flush();
			} catch (IOException exc) {
				// just ignore
			}

			result = false;
		}

		return result;
	}

	private boolean printDocument(Node printer, Packet document, Writer report) {
		String author = "Unknown";
		String title = "Untitled";
		int startPos = 0, endPos = 0;

		if (printer.type == Node.PRINTER) {
			try {
				if (document.message.startsWith("!PS")) {
					startPos = document.message.indexOf("author:");
					if (startPos >= 0) {
						endPos = document.message.indexOf(".", startPos + 7);
						if (endPos < 0) {
							endPos = document.message.length();
						}

						author = document.message.substring(startPos + 7,
								endPos);
					}

					startPos = document.message.indexOf("title:");
					if (startPos >= 0) {
						endPos = document.message.indexOf(".", startPos + 6);
						if (endPos < 0) {
							endPos = document.message.length();
						}
						title = document.message
								.substring(startPos + 6, endPos);
					}

					report.write("\tAccounting -- author = '");
					report.write(author);
					report.write("' -- title = '");
					report.write(title);
					report.write("'\n");
					report.write(">>> Postscript job delivered.\n\n");
					report.flush();
				} else {
					title = "ASCII DOCUMENT";
					if (document.message.length() >= 16) {
						author = document.message.substring(8, 16);
					}

					report.write("\tAccounting -- author = '");
					report.write(author);
					report.write("' -- title = '");
					report.write(title);
					report.write("'\n");
					report.write(">>> ASCII Print job delivered.\n\n");
					report.flush();
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

	/**
	 * Return a printable representation of #receiver.
	 */
	public String toString() {
		StringBuffer buf = new StringBuffer(30 * workstations.size());
		printOn(buf);
		return buf.toString();
	}

	/**
	 * Write a printable representation of #receiver on the given #buf.
	 */
	public void printOn(StringBuffer buf) {
		Node currentNode = firstNode;
		do {
			switch (currentNode.type) {
			case Node.NODE:
				buf.append("Node ");
				buf.append(currentNode.name);
				buf.append(" [Node]");
				break;
			case Node.WORKSTATION:
				buf.append("Workstation ");
				buf.append(currentNode.name);
				buf.append(" [Workstation]");
				break;
			case Node.PRINTER:
				buf.append("Printer ");
				buf.append(currentNode.name);
				buf.append(" [Printer]");
				break;
			default:
				buf.append("(Unexpected)");
				break;
			}
			buf.append(" -> ");
			currentNode = currentNode.nextNode;
		} while (currentNode != firstNode);
		buf.append(" ... ");
	}

	/**
	 * Write a HTML representation of #receiver on the given #buf.
	 */
	public void printHTMLOn(StringBuffer buf) {

		buf.append("<HTML>\n<HEAD>\n<TITLE>LAN Simulation</TITLE>\n</HEAD>\n<BODY>\n<H1>LAN SIMULATION</H1>");
		Node currentNode = firstNode;
		buf.append("\n\n<UL>");
		do {
			buf.append("\n\t<LI> ");
			switch (currentNode.type) {
			case Node.NODE:
				buf.append("Node ");
				buf.append(currentNode.name);
				buf.append(" [Node]");
				break;
			case Node.WORKSTATION:
				buf.append("Workstation ");
				buf.append(currentNode.name);
				buf.append(" [Workstation]");
				break;
			case Node.PRINTER:
				buf.append("Printer ");
				buf.append(currentNode.name);
				buf.append(" [Printer]");
				break;
			default:
				buf.append("(Unexpected)");
				break;
			}
			buf.append(" </LI>");
			currentNode = currentNode.nextNode;
		} while (currentNode != firstNode);
		buf.append("\n\t<LI>...</LI>\n</UL>\n\n</BODY>\n</HTML>\n");
	}

	/**
	 * Write an XML representation of #receiver on the given #buf.
	 */
	public void printXMLOn(StringBuffer buf) {

		Node currentNode = firstNode;
		buf.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n\n<network>");
		do {
			buf.append("\n\t");
			switch (currentNode.type) {
			case Node.NODE:
				buf.append("<node>");
				buf.append(currentNode.name);
				buf.append("</node>");
				break;
			case Node.WORKSTATION:
				buf.append("<workstation>");
				buf.append(currentNode.name);
				buf.append("</workstation>");
				break;
			case Node.PRINTER:
				buf.append("<printer>");
				buf.append(currentNode.name);
				buf.append("</printer>");
				break;
			default:
				buf.append("<unknown></unknown>");
				break;
			}
			currentNode = currentNode.nextNode;
		} while (currentNode != firstNode);
		buf.append("\n</network>");
	}

}
