package com.capgemini.starterkit.imageViewer.dataprovider.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import com.capgemini.starterkit.imageViewer.dataprovider.ImageProvider;
import com.capgemini.starterkit.imageViewer.dataprovider.data.ImageVO;

public class ImageProviderImpl implements ImageProvider {

	public ImageProviderImpl() {
	}

	@Override
	public Collection<ImageVO> findImages(String directoryPath) {
		File folder = new File(directoryPath);
		Collection<ImageVO> result = new ArrayList<ImageVO>();
		int id = 1;
		String name = "";
		for (File file : folder.listFiles()) {
			name = file.getName().toString().toLowerCase();
			if (name.endsWith(".jpg") || name.endsWith(".png")) {
				result.add(new ImageVO(id, file.getName(), file.getPath()));
				id++;
			}
		}
		return result;
	}

}
