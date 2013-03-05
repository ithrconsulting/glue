package com.ithrconsulting.sp.model;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import com.google.gson.Gson;

public class ResourceRepository {
	private final File root;
	private Gson gson;

	public ResourceRepository(File rootFolder, Gson gson) {
		super();

		this.gson = gson;
		this.root = rootFolder;

		this.root.mkdirs();
		if (!this.root.exists()) {
			throw new RuntimeException("Root folder " + this.root + " not found!");
		}
	}

	public String getUniqueName() throws IOException {
		int number = 1;

		String[] fileNames = root.list();
		if (fileNames.length > 0) {
			Integer[] ids = filenamesToNumbers(fileNames);
			Arrays.sort(ids);
			number = ids[ids.length-1].intValue() + 1;
		}

		return String.valueOf(number);
	}

	private Integer[] filenamesToNumbers(String[] fileNames) {
		Integer[] numbers = new Integer[fileNames.length];
		for (int index = 0; index < fileNames.length; index++) {
			numbers[index] = Integer.valueOf(fileNames[index]);
		}
		return numbers;
	}

	public <T extends Resource> Set<T> list(Class<T> classOfT) throws IOException {
		Set<T> result = new HashSet<T>();

		String[] fileNames = root.list();
		for (String id : fileNames) {
			result.add(get(id, classOfT));
		}

		return result;
	}

	public <T extends Resource> T get(String id, Class<T> classOfT) throws IOException {
		File file = new File(root, id);
		if (!file.exists()) {
			throw new IOException("Resource " + id + " does not exist!");
		}

		String json;
		BufferedReader input = new BufferedReader(new FileReader(file));
		try {
			json = input.readLine();
		} finally {
			input.close();
		}

		return gson.fromJson(json, classOfT);
	}

	public Resource update(Resource res) throws IOException {
		File file = new File(root, res.getId());
		if (!file.exists()) {
			throw new IOException("Resource " + res.getId() + " is not present!");
		}

		Writer output = new BufferedWriter(new FileWriter(file));
		try {
			output.write(gson.toJson(res));
		} finally {
			output.close();
		}
		return res;
	}

	public Resource create(Resource res) throws IOException {
		File file = new File(root, res.getId());
		if (file.exists()) {
			throw new IOException("Resource " + res.getId() + " is already present!");
		}

		Writer output = new BufferedWriter(new FileWriter(file));
		try {
			output.write(gson.toJson(res));
		} finally {
			output.close();
		}
		return res;
	}

	public Resource delete(String id) throws IOException {
		File file = new File(root, id);
		file.delete();
		if (file.exists()) {
			throw new IOException("Resource " + id + " was NOT deleted");
		}

		return null;
	}

	public boolean exists(String id) {
		File file = new File(root, id);
		if (file.exists()) {
			return true;
		} else {
			return false;
		}
	}
}
