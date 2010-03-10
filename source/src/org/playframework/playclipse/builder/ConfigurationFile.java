package org.playframework.playclipse.builder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;

public class ConfigurationFile {
	private IFile file;
	
	public ConfigurationFile(IProject project) {
		this.file = project.getFile("conf/application.conf");
	}

	public Map<String, String> getModules() {
		Map<String, String> result = new HashMap<String, String>();
		List<String> lines = getLines();
		Pattern p = Pattern.compile("module\\.(\\w+)=(.+)");
		Matcher m;
		for (int i = 0; i < lines.size(); i++) {
			String line = lines.get(i);
			String moduleName;
			String modulePath;
			m = p.matcher(line);
			if (m.matches()) {
				moduleName = m.group(1);
				modulePath = m.group(2);
				result.put(moduleName, modulePath);
			}
		}
		return null;
	}

	public int getPort() {
		List<String> lines = getLines();
		Pattern p = Pattern.compile("^\\s*port\\s*=([0-9]+)");
		Matcher m;
		for (int i = 0; i < lines.size(); i++) {
			String line = lines.get(i);
			int port = 0;
			String sport;
			m = p.matcher(line);
			if (m.matches()) {
				sport = m.group(1);
				try {
					port = Integer.parseInt(sport);
				} catch (NumberFormatException ex) {
					// Bad line, just continue
				}
				if (port != 0) return port;
			}
		}
		return 9000;
	}

	private List<String> getLines() {
		List<String> result = new ArrayList<String>();
		String line;
		InputStream is;
		try {
			is = file.getContents();
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		try {
			BufferedReader reader;
			reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
			while ((line = reader.readLine()) != null) {
				result.add(line);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return result;
	}

}
