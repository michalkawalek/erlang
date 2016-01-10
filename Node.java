package net.inaka.fetjaba;

import java.io.IOException;

import com.ericsson.otp.erlang.OtpErlangAtom;
import com.ericsson.otp.erlang.OtpErlangDecodeException;
import com.ericsson.otp.erlang.OtpErlangExit;
import com.ericsson.otp.erlang.OtpErlangObject;
import com.ericsson.otp.erlang.OtpErlangPid;
import com.ericsson.otp.erlang.OtpErlangTuple;
import com.ericsson.otp.erlang.OtpMbox;
import com.ericsson.otp.erlang.OtpNode;
import java.util.ArrayList;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.*;




/**
 * @author Fernando Benavides <elbrujohalcon@inaka.net> Entry point for this
 *         application
 */
public class Node {
	/** This Erlang node */
	public static OtpNode	NODE;

	/** Peer node name */
	public static String	PEER;

	public static ArrayList<String> atomy = new ArrayList<String>();

	public static PrintWriter zapis;

	/**
	 * @param args
	 *            command line arguments
	 */
	public static void main(String[] args) {
		String peerName = args.length >= 1 ? args[0] : "fetjaba@nohost";
		String nodeName = args.length >= 2 ? args[1] : "fetjaba_java@nohost";
		try {
			NODE = args.length >= 3 ? new OtpNode(nodeName, args[2])
					: new OtpNode(nodeName);
			PEER = peerName;
			System.out.println(args[2]);
			// We create an mbox to link
			
			zapis = new PrintWriter("/home/galaisius/erlang/fetjaba-post-1/atomy.txt");
			zapis.println("siema");
			
			
			final OtpMbox mbox = NODE.createMbox("fetjaba_server");
			new Thread(mbox.getName()) {
				@Override
				public void run() {
					boolean run = true;
					while (run) { // This thread runs forever
						try {
							OtpErlangObject msg = mbox.receive(); // Gets the
							atomy.add(((OtpErlangAtom) msg).atomValue());	//message
							run = processMsg(msg, mbox); // and deals with it,
															// before getting
															// the next one
						} catch (OtpErlangExit oee) { // Until it gets an exit
							zapis.println("siema");					// signal
							System.exit(1); // And, this is a little bit
											// violent, but it gets the job
											// done.
						} catch (OtpErlangDecodeException oede) {
							zapis.println("siema");
							oede.printStackTrace();
							System.out.println("That was a bad message, moving on...");
						}
					}
				}
			}.start();
			System.out.println("READY");
		} catch (IOException e1) {
			e1.printStackTrace();
			System.exit(1);
		}
	}

	protected static boolean processMsg(OtpErlangObject msg, OtpMbox mbox)
			throws OtpErlangDecodeException {
		if (msg instanceof OtpErlangAtom
				&& ((OtpErlangAtom) msg).atomValue().equals("stop")) { 
			
			mbox.close();
			return false;
		} else if (msg instanceof OtpErlangTuple) {
			OtpErlangObject[] elements = ((OtpErlangTuple) msg).elements();
			if (elements.length == 2) {
				if (elements[0] instanceof OtpErlangAtom
						&& ((OtpErlangAtom) elements[0]).atomValue().equals(
								"pid") && elements[1] instanceof OtpErlangPid) {
					OtpErlangPid caller = (OtpErlangPid) elements[1];
					mbox.send(caller, new OtpErlangTuple(new OtpErlangObject[] {
							elements[0], mbox.self() }));
					for(String x : atomy) {
						zapis.println(x);
						zapis.println("siema");
						}
					zapis.println("siema");
					zapis.close();
					
					return true;
				}
			}
		}
		throw new OtpErlangDecodeException("Bad message: " + msg.toString());
	}
}
