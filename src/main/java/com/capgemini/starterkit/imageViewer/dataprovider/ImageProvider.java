package com.capgemini.starterkit.imageViewer.dataprovider;

import java.util.Collection;

import com.capgemini.starterkit.imageViewer.dataprovider.data.ImageVO;
import com.capgemini.starterkit.imageViewer.dataprovider.impl.ImageProviderImpl;

public interface ImageProvider {

	ImageProvider INSTANCE = new ImageProviderImpl();

	Collection<ImageVO> findImages (String directoryPath);
}
