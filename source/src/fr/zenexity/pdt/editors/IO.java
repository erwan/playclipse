package fr.zenexity.pdt.editors;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;

public class IO {

	public interface LineReader {
		public void readLine(String line, int lineNumber, int offset);
	}

	public static void readLines(InputStream inputStream, LineReader reader) throws IOException {
		StringBuffer lineBuf = new StringBuffer();
		int c = inputStream.read();
		int lineNumber = 1;
		int offset = 0;
		while (c != -1) {
			if ((char)c == '\n') {
				String line = lineBuf.toString();
				reader.readLine(line, lineNumber++, offset);
				offset += line.length() + 1;
				lineBuf = new StringBuffer();
			} else {
				lineBuf.append((char)c);
			}
			c = inputStream.read();
		}
		inputStream.close();
	}

	public static void readLines(IFile file, LineReader reader) throws IOException, CoreException {
		readLines(file.getContents(), reader);
	}

	/**
	 * Read file content to a String (always use utf-8)
	 * @param file The file to read
	 * @return The String content
	 * @throws java.io.IOException
	 * @throws CoreException
	 */
	public static String readContentAsString(IFile file) throws IOException, CoreException {
		InputStream is = file.getContents();
		StringWriter result = new StringWriter();
		PrintWriter out = new PrintWriter(result);
		BufferedReader reader = new BufferedReader(new InputStreamReader(is, "utf-8"));
		String line = null;
		while ((line = reader.readLine()) != null) {
			out.println(line);
		}
		is.close();
		return result.toString();
	}

}
