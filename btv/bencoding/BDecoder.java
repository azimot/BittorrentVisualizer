/*

	Class used to decode bencoded torrent files.

	Author: Stephan McLean

*/

package btv.bencoding;

import java.util.*;
import java.io.*;
public class BDecoder {
	private static String in = null;

	public static Object decode(String data) throws BDecodingException {
		in = data;
		return readItem(readChar());
	}

	static String readString(int length) throws BDecodingException {
		// Read a string from in given the length
		String result = "";
	
		for(int i = 0; i < length; i++) {
			char c = readChar();
			result = result + c;
		}
	
		return result;
	}

	static int readInt() throws BDecodingException {
		// Read the int until we hit an "e"
	
		String intToParse = "";
	
		char c = readChar();
		while(c != 'e') {
			intToParse = intToParse + c;
			c = readChar();
		}
	
		return Integer.parseInt(intToParse);
	}

	static ArrayList<Object> readList() throws BDecodingException {
		// Read a list of items from the file.
		ArrayList<Object> items = new ArrayList<Object>();
	
		char c = readChar();
		while(c != 'e') {
			Object i = readItem(c);
			items.add(i);
			c = readChar();
		}
	
		return items;
	}

	static Map readDictionary() throws BDecodingException {
		Map m = new LinkedHashMap();
		Object key, value;
		char c = readChar();
		while(c != 'e') {
	
			key = readItem(c);
			char d  = readChar();
			value = readItem(d);
			m.put(key, value);
			c = readChar();
		}
		return m;
	}

	/*
	Helper methods ------------------------------

	*/
	static Object readItem(char c) throws BDecodingException {
		/*
			Return an appropriate item based on c having been read
			from the file.
		*/

		Object o = new Object();
		if(Character.isDigit(c)) {
			// Begins with a digit we know it's a string.
			String len = getStringLength();
			len = c + len;
			String s = readString(Integer.parseInt(len));
			o = (Object) s;
		}
		else {
			switch(c) {
			case 'i':
				int n = readInt();
				o = (Object) n;
				break;
			case 'l':
				ArrayList<Object> list = readList();
				o = (Object) list;
				break;
			case 'd':
				Map map = readDictionary();
				o = (Object) map;
				break;
			default: 
				throw new BDecodingException("Unable to process item: " + c);
			}
		}
		return o;
	}

	static char readChar() throws BDecodingException {
		// Remove and return next character from the data
		if(in.length() > 0) {
			char c = in.charAt(0);
			in = in.substring(1, in.length());
			return c;
		}
		else {
			throw new BDecodingException("Unexpected end of file.");
		}
	}

	static String getStringLength() throws BDecodingException {
	
	
		String result = "";
		char c = readChar();

		while(c != ':') {
			result = result + c;
			c = readChar();
		}
	
		return result;
	}

	/*
		End of helper methods ----------------------------
	*/
}