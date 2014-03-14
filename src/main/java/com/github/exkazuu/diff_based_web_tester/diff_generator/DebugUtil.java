package com.github.exkazuu.diff_based_web_tester.diff_generator;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class DebugUtil {
	public static void writeLogFile(String suffixFileName, String content) {
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
		try {
			new File("log").mkdir();
			FileOutputStream stream = new FileOutputStream(new File("log"
					+ File.separatorChar + sdf.format(cal.getTime())
					+ suffixFileName));
			OutputStreamWriter writer = new OutputStreamWriter(stream, "UTF-16");
			writer.write(content);
			writer.close();
			stream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
