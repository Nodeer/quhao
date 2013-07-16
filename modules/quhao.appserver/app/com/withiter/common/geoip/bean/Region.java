package com.withiter.common.geoip.bean;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

import play.Play;

public class Region {
	public String countryCode;
	public String countryName;
	public String region;
	public static Map<String, String> regionMap = new HashMap<String, String>();

	static {
		String sep = System.getProperty("file.separator");
		String dir = Play.configuration.getProperty("geoip.datdir");
		String csvFilename = dir + sep + "region.csv";
		String[] row = null;
		InputStream fis;
		BufferedReader br;
		String line;
		try {
			fis = new FileInputStream(csvFilename);
			br = new BufferedReader(new InputStreamReader(fis,
					Charset.forName("UTF-8")));
			while ((line = br.readLine()) != null) {
				try {
					row = line.split(",");
					regionMap.put(row[0] + "" + row[1], row[2]);
				} catch (ArrayIndexOutOfBoundsException e) {

				}
			}
			br.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		new Region();
	}
}
