package com.vinisoft.facesdk.detect;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.StringTokenizer;

import android.content.Context;
import android.graphics.Bitmap;

import com.vinisoft.facesdk.utils.FileUtils;
import com.vinisoft.facesdk.utils.LogUtils;

public class PersonUtils {
	ArrayList<Person> arr;
	Context context;

	public PersonUtils(Context context) {
		this.context = context;
		File file = new File(DetectSetting.PATH_LIST_PERSON);
		if (!file.exists()) {
			String data = (getMaxID() + 1) + "|" + "MINE";
			FileUtils.writeFile(DetectSetting.PATH_LIST_PERSON, data, true);
		}
		loadListPerson();
	}

	public ArrayList<Person> loadListPerson() {
		if (arr != null) {
			arr.clear();
		} else {
			arr = new ArrayList<Person>();
		}

		try {
			FileInputStream fis = new FileInputStream(DetectSetting.PATH_LIST_PERSON);
			InputStreamReader reader = new InputStreamReader(fis);
			BufferedReader buffer = new BufferedReader(reader);
			String line = "";

			while ((line = buffer.readLine()) != null) {
				StringTokenizer token = new StringTokenizer(line, "|");
				arr.add(new Person(Integer.parseInt(token.nextToken()), token.nextToken()));
			}

		} catch (Exception e) {
			LogUtils.exception(e);
		}

		return arr;
	}

	public int getMaxID() {
		if (arr != null) {
			int size = arr.size();

			if (size > 0) {
				int MaxId = 0;
				int getId = 0;

				for (int i = 0; i < size; i++) {
					getId = arr.get(i).getId();
					if (getId > MaxId) {
						MaxId = getId;
					}
				}
				return MaxId;
			}
		}
		return 0;
	}

	public int detect(Bitmap img) {
		return -1;
	}

	private void removePersonFromList(String fileList, int id) {
		try {
			File input = new File(fileList);
			if (input.exists()) {
				String tempFile = fileList + ".tmp";
				String line = "";
				FileInputStream fis = new FileInputStream(fileList);
				InputStreamReader reader = new InputStreamReader(fis);
				@SuppressWarnings("resource")
				BufferedReader buffer = new BufferedReader(reader);

				while ((line = buffer.readLine()) != null) {
					StringTokenizer token = new StringTokenizer(line, ";");
					token.nextToken();
					int tmpId = Integer.parseInt(token.nextToken());
					if (tmpId != id) {
						FileUtils.writeFile(tempFile, line, true);
					}
				}
				FileUtils.moveFile(tempFile, fileList);
			}

		} catch (Exception e) {
			LogUtils.exception(e);
		}
	}

	public void deletePerson(int id) {
		Person person;

		if (arr != null) {
			int i;
			int size = arr.size();
			if (size <= 1) {
				FileUtils.clearFileContent(DetectSetting.PATH_LIST_FACE);
				FileUtils.clearFileContent(DetectSetting.PATH_LIST_NOSE);
				FileUtils.clearFileContent(DetectSetting.PATH_LIST_PERSON);
				arr.clear();
			} else {
				String data = "";
				for (i = 0; i < size; i++) {
					person = arr.get(i);
					if (person.getId() == id) {
						arr.remove(i);
						break;
					}
				}

				FileUtils.clearFileContent(DetectSetting.PATH_LIST_PERSON);
				size = arr.size();
				for (i = 0; i < size; i++) {
					person = arr.get(i);
					data = person.getId() + "|" + person.getName();
					FileUtils.writeFile(DetectSetting.PATH_LIST_PERSON, data, true);
					data = "";
				}

				removePersonFromList(DetectSetting.PATH_LIST_FACE, id);
				removePersonFromList(DetectSetting.PATH_LIST_NOSE, id);
			}
		}

		String pathFolder = String.format(DetectSetting.PATH_IMG, id);
		FileUtils.deleteRecursive(new File(pathFolder));

		if (arr.size() <= 0) {
			FileUtils.deleteRecursive(new File(DetectSetting.PATH_DATA_FACE));
			FileUtils.deleteRecursive(new File(DetectSetting.PATH_DATA_NOSE));
		}
	}

	public int addPersonImage(int id, int pos) {
		int numberImage;
		String path = String.format(DetectSetting.PATH_CONFIG, id);
		String pathFace = String.format(DetectSetting.PATH_IMG_FACE, id, pos);
		String pathNose = String.format(DetectSetting.PATH_IMG_NOSE, id, pos);
		String dataFace = String.format(DetectSetting.STRING_DATA_FACE, id, pos, id);
		String dataNose = String.format(DetectSetting.STRING_DATA_NOSE, id, pos, id);

		FileUtils.writeFile(path, pos + "", false);
		FileUtils.moveFile(DetectSetting.PATH_TEMP_FACE, pathFace);
		FileUtils.moveFile(DetectSetting.PATH_TEMP_NOSE, pathNose);
		FileUtils.writeFile(DetectSetting.PATH_LIST_FACE, dataFace, true);
		FileUtils.writeFile(DetectSetting.PATH_LIST_NOSE, dataNose, true);

		numberImage = getPersonNumberImage(id);
		return numberImage;
	}

	public void deletePersonImage(int id, int imageId) {

	}

	public int getPersonNumberImage(int id) {
		String path = String.format(DetectSetting.PATH_CONFIG, id);

		try {
			FileInputStream fis = new FileInputStream(path);
			InputStreamReader reader = new InputStreamReader(fis);
			BufferedReader buffer = new BufferedReader(reader);
			String line = "";

			while ((line = buffer.readLine()) != null) {
				return Integer.parseInt(line.trim());
			}

			fis.close();
			fis = null;

		} catch (Exception e) {
			LogUtils.exception(e);
		}
		return 0;
	}

	public ArrayList<Person> getArr() {
		return arr;
	}

	public String getnameById(int id) {
		int size = arr.size();
		Person person;
		for (int i = 0; i < size; i++) {
			person = arr.get(i);
			if (person != null) {
				if (person.getId() == id) {
					return person.getName();
				}
			}
		}
		return "";
	}
}
