package com.capgemini.starterkit.imageViewer.model;

import java.util.ArrayList;
import java.util.List;
import com.capgemini.starterkit.imageViewer.dataprovider.data.ImageVO;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;

public class ImageSearchModel {

	private final ListProperty<ImageVO> result = new SimpleListProperty<>(
			FXCollections.observableList(new ArrayList<>()));

	public List<ImageVO> getResult() {
		return result.get();
	}

	public final void setResult(List<ImageVO> value) {
		result.setAll(value);
	}

	public ListProperty<ImageVO> resultProperty() {
		return result;
	}
}
