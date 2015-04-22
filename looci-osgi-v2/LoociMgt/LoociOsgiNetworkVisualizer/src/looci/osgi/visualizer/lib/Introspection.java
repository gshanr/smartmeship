/**
LooCI Copyright (C) 2013 KU Leuven.
All rights reserved.

LooCI is an open-source software development kit for developing and maintaining networked embedded applications;
it is distributed under a dual-use software license model:

1. Non-commercial use:
Non-Profits, Academic Institutions, and Private Individuals can redistribute and/or modify LooCI code under the terms of the GNU General Public License version 3, as published by the Free Software Foundation
(http://www.gnu.org/licenses/gpl.html).

2. Commercial use:
In order to apply LooCI in commercial code, a dedicated software license must be negotiated with KU Leuven Research & Development.

Contact information:
  Administrative Contact: Sam Michiels, sam.michiels@cs.kuleuven.be
  Technical Contact:           Danny Hughes, danny.hughes@cs.kuleuven.be
Address:
  iMinds-DistriNet, KU Leuven
  Celestijnenlaan 200A - PB 2402,
  B-3001 Leuven,
  BELGIUM. 
 */
package looci.osgi.visualizer.lib;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

// Perform an instrospection and store the results.
public class Introspection {

	private ArrayList<IntroComp> components;
	private ArrayList<IntroLink> links;
	private String node;

	// Create the node.
	Introspection(String node) {
		this.node = node;
		components = new ArrayList<IntroComp>();
		links = new ArrayList<IntroLink>();
	}

	boolean explore() {

		String answer;        // For storing the answer.

		try {
			// Open a telnet connection with the node.
			Socket clientSocket = new Socket(node, 6667);

			DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
			BufferedReader inToServer = new BufferedReader( new InputStreamReader(clientSocket.getInputStream()));

			answer = inToServer.readLine();

			// Get the component identifiers.
			outToServer.writeBytes("getComponentIDs "+node+"\n");
			answer = inToServer.readLine();

			String aux_answer = answer.substring(4, answer.length() - 1);

			System.out.println("Components...");
			System.out.println (aux_answer);

			String[] s_array_c = aux_answer.split(",");

			// For every component in aux obtain the interfaces and the receptacles.
			for (int i = 0; i < s_array_c.length; i++) {

				String[] inter; // For interfaces.
				String[] recep; // For receptacles.

				IntroComp c = new IntroComp(s_array_c[i]); // The new introcomponent.

				// Obtain the interfaces.
				outToServer.writeBytes("getProvidedInterfaces "+s_array_c[i]+" "+node+"\n");
				answer = inToServer.readLine();

				aux_answer = answer.substring(4, answer.length() - 1);

				System.out.println("Interfaces...");
				System.out.println (aux_answer);

				inter = aux_answer.split(",");

				// Explore all the interfaces.
				for (int j = 0; j < inter.length; j++) {

					// Convert the "name" to the "number".
					if (inter[j].compareTo("any_ev") == 0) {
						inter[j] = "0";
					} else if (inter[j].compareTo("switch_ev") == 0) {
						inter[j] = "400";
					} else if (inter[j].compareTo("button_ev") == 0) {
						inter[j] = "401";
					} else if (inter[j].compareTo("temp_ev") == 0) {
						inter[j] = "402";
					} else if (inter[j].compareTo("buzz_ev") == 0) {
						inter[j] = "403";
					}		        	   

					c.addInterface(inter[j]);

					// Explore the local wirings.
					for (int k = 0 ; k < s_array_c.length; k++) {

						System.out.println("getLocalWires "+inter[j]+" "+s_array_c[i]+" "+s_array_c[k]+" "+node+"\n");

						outToServer.writeBytes("getLocalWires "+inter[j]+" "+s_array_c[i]+" "+s_array_c[k]+" "+node+"\n");
						answer = inToServer.readLine();

						System.out.println("LocalWires: "+answer);

						aux_answer = answer.substring(4, answer.length() - 1);

						if (aux_answer.length() == 0) {
							IntroLink l = new IntroLink(s_array_c[k], inter[j], s_array_c[i], inter[j]);
							links.add(l);
						}
					}

					// Ask for the wiresTo.
					outToServer.writeBytes("getWiresTo "+inter[j]+" "+s_array_c[i]+" * "+node+"\n");
					answer = inToServer.readLine();

					// Analyze the answer.
					aux_answer = answer.substring(4, answer.length() - 1);
					System.out.println(answer);

					String[] array_wiresTo = aux_answer.split(";");

					// Add the new links.
					for (int k=0; k < array_wiresTo.length; k++) {

						String[] array_wiresTo_sub = array_wiresTo[k].split(",");

						if (array_wiresTo_sub.length == 3) {

							// Check if the component is already created.
							boolean found = false;
							IntroComp ele = null;

							for (int u = 0; u < components.size() && found == false; u++) {			
								ele = components.get(u);
								if (ele.getid().compareTo(array_wiresTo_sub[3]) == 0) {
									found = true;
								}
							}

							if (found == false) {
								ele = new IntroComp(array_wiresTo_sub[3]);
							} else {
								components.remove(ele);
							}

							// Add the receptacle.
							ele.addReceptacle(inter[j]);
							components.add(ele);

							// Add the link.								
							IntroLink l = new IntroLink(array_wiresTo_sub[3], inter[j], s_array_c[i], inter[j]);
							links.add(l);
						}
					}
				}

				// Obtain the receptacles.
				outToServer.writeBytes("getRequiredInterfaces "+s_array_c[i]+" "+node+"\n");
				answer = inToServer.readLine();

				aux_answer = answer.substring(4, answer.length() - 1);

				System.out.println("Receptacles...");
				System.out.println (aux_answer);

				recep = aux_answer.split(",");

				// Explore all the receptacles.
				for (int j = 0; j < recep.length; j++) {

					// Convert the "name" to the "number".
					if (recep[j].compareTo("any_ev") == 0) {
						recep[j] = "0";
					} else if (recep[j].compareTo("switch_ev") == 0) {
						recep[j] = "400";
					} else if (recep[j].compareTo("button_ev") == 0) {
						recep[j] = "401";
					} else if (recep[j].compareTo("temp_ev") == 0) {
						recep[j] = "402";
					} else if (recep[j].compareTo("buzz_ev") == 0) {
						recep[j] = "403";
					}		        	   

					c.addReceptacle(recep[j]);

					// Ask for the wiresTo.
					outToServer.writeBytes("getWiresFrom "+recep[j]+" 0 :: "+s_array_c[i]+" "+node+"\n");
					answer = inToServer.readLine();

					// Analyze the answer.
					aux_answer = answer.substring(4, answer.length() - 1);

					String[] array_wiresTo = aux_answer.split(";");

					// Add the new links.
					for (int k=0; k < array_wiresTo.length; k++) {

						String[] array_wiresTo_sub = array_wiresTo[k].split(",");

						if (array_wiresTo_sub.length == 3) {

							// Check if the component is already created.
							boolean found = false;
							IntroComp ele = null;

							for (int u = 0; u < components.size() && found == false; u++) {			
								ele = components.get(u);
								if (ele.getid().compareTo(array_wiresTo_sub[3]) == 0) {
									found = true;
								}
							}

							if (found == false) {
								ele = new IntroComp(array_wiresTo_sub[3]);
							} else {
								components.remove(ele);
							}

							// Add the receptacle.
							ele.addReceptacle(inter[j]);
							components.add(ele);

							// Add the link.								
							IntroLink l = new IntroLink(s_array_c[i], recep[j], array_wiresTo_sub[3], recep[j]);
							links.add(l);
						}
					}
				}

				components.add(c);
			}

		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return true;
	}

	// Return the components.
	ArrayList<IntroComp> getComponents() {
		return components;
	}

	// Return the links.
	ArrayList<IntroLink> getLinks() {
		return links;
	}
}
