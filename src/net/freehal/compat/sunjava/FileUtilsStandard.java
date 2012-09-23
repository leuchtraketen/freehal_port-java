package net.freehal.compat.sunjava;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import net.freehal.core.util.FileUtilsImpl;
import net.freehal.core.util.LogUtils;

public class FileUtilsStandard implements FileUtilsImpl {

	@Override
	public List<String> readLines(File f) {
		LogUtils.d("reading line by line: " + f.getAbsolutePath());
		List<String> lines = new ArrayList<String>();
		try {
			FileInputStream in = new FileInputStream(f);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String line;

			while ((line = br.readLine()) != null) {
				lines.add(line);
			}

			br.close();

		} catch (Exception e) {
			LogUtils.e(e.getMessage());
		}
		return lines;
	}

	@Override
	public String read(File f) {
		LogUtils.d("reading whole file: " + f.getAbsolutePath());
		BufferedReader theReader = null;
		String returnString = null;

		try {
			theReader = new BufferedReader(new FileReader(f));
			char[] charArray = null;

			if (f.length() > Integer.MAX_VALUE) {
				LogUtils.e("The file is larger than int max = "
						+ Integer.MAX_VALUE);
			} else {
				charArray = new char[(int) f.length()];

				// Read the information into the buffer.
				theReader.read(charArray, 0, (int) f.length());
				returnString = new String(charArray);

			}
		} catch (FileNotFoundException e) {
			LogUtils.e(e.getMessage());
		} catch (IOException e) {
			LogUtils.e(e.getMessage());
		} finally {
			try {
				theReader.close();
			} catch (IOException e) {
				LogUtils.e(e.getMessage());
			}
		}

		return returnString;
	}

	@Override
	public void append(File filename, String string) {
		BufferedWriter bw = null;

		try {
			bw = new BufferedWriter(new FileWriter(filename, true));
			bw.write(string);
			bw.flush();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		} finally { // always close the file
			if (bw != null)
				try {
					bw.close();
				} catch (IOException ioe2) {
					// just ignore it
				}
		} // end try/catch/finally
	}

	@Override
	public void write(File filename, String string) {
		BufferedWriter bw = null;

		try {
			bw = new BufferedWriter(new FileWriter(filename, false));
			bw.write(string);
			bw.flush();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		} finally { // always close the file
			if (bw != null)
				try {
					bw.close();
				} catch (IOException ioe2) {
					// just ignore it
				}
		} // end try/catch/finally
	}
}
