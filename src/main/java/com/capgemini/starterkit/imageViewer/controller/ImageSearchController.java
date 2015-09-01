package com.capgemini.starterkit.imageViewer.controller;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.ResourceBundle;

import com.capgemini.starterkit.imageViewer.dataprovider.ImageProvider;
import com.capgemini.starterkit.imageViewer.dataprovider.data.ImageVO;
import com.capgemini.starterkit.imageViewer.model.ImageSearchModel;

import javafx.beans.property.ReadOnlyIntegerWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.concurrent.Worker;
import javafx.concurrent.Worker.State;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.TilePane;
import javafx.stage.DirectoryChooser;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class ImageSearchController {

	@FXML
	ResourceBundle resources;

	@FXML
	URL location;

	@FXML
	Button selectButton;

	@FXML
	ImageView imageView;

	@FXML
	TilePane tilePane;

	@FXML
	ScrollPane scrollPane;

	@FXML
	TableView<ImageVO> resultTable;

	@FXML
	TableColumn<ImageVO, Number> idColumn;

	@FXML
	TableColumn<ImageVO, String> nameColumn;

	String path = "";

	private final ImageProvider imageProvider = ImageProvider.INSTANCE;

	private final ImageSearchModel model = new ImageSearchModel();

	public ImageSearchController() {
	}

	@FXML
	private void initialize() {
		initializeResultTable();

		resultTable.itemsProperty().bind(model.resultProperty());

		setDefaultImage();
	}

	private void setDefaultImage() {
		URL url = getClass().getResource("/com/capgemini/starterkit/imageViewer/images/defaultImage.png");
		if(url != null){
			String path = url.getPath();
			Image image = new Image(new File(path).toURI().toString());
			imageView.setImage(image);
		}
	}

	private void initializeResultTable() {
		idColumn.setCellValueFactory(cellData -> new ReadOnlyIntegerWrapper(cellData.getValue().getId()));
		nameColumn.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().getName()));

		resultTable.setPlaceholder(new Label(resources.getString("table.emptyText")));
	}

	@FXML
	public void selectButtonAction(ActionEvent event) {
		path = getDirectoryPath();
		Task<Collection<ImageVO>> backgroundTask = new Task<Collection<ImageVO>>() {

			@Override
			protected Collection<ImageVO> call() throws Exception {
				return imageProvider.findImages(path);
			}
		};

		backgroundTask.stateProperty().addListener(new ChangeListener<Worker.State>() {

			@Override
			public void changed(ObservableValue<? extends State> observable, State oldValue, State newValue) {
				if (newValue == State.SUCCEEDED) {

					model.setResult(new ArrayList<ImageVO>(backgroundTask.getValue()));

					resultTable.getSortOrder().clear();
					addImageViewsToTilePane(new ArrayList<ImageVO>(backgroundTask.getValue()));
					setDefaultImage();
				}
			}
		});
		new Thread(backgroundTask).start();
	}

	@FXML
	public void selectOnMousePressed(MouseEvent mouseEvent) {
		if (resultTable.getItems().size() == 0) {
			return;
		}
		displayImage();
	}

	@FXML
	public void selectPreviousImage() {
		if (resultTable.getItems().size() == 0) {
			return;
		}
		if (resultTable.getSelectionModel().getSelectedItem() == null) {
			resultTable.getSelectionModel().select(0);
			return;
		}
		resultTable.getSelectionModel().selectPrevious();
		displayImage();
	}

	@FXML
	public void selectNextImage() {
		if (resultTable.getItems().size() == 0) {
			return;
		}
		if (resultTable.getSelectionModel().getSelectedItem() == null) {
			resultTable.getSelectionModel().select(0);
			return;
		}
		resultTable.getSelectionModel().selectNext();
		displayImage();
	}

	@FXML
	public void selectOnKeyTyped() {
		if (resultTable.getItems().size() == 0) {
			return;
		}
		if (resultTable.getSelectionModel().getSelectedItem() == null) {
			resultTable.getSelectionModel().select(0);
			return;
		}
		displayImage();
	}

	private void displayImage() {
		String path = resultTable.getSelectionModel().getSelectedItem().getPath();
		Image image = new Image(new File(path).toURI().toString());
		imageView.setImage(image);
	}

	private void addImageViewsToTilePane(Collection<ImageVO> images) {
		tilePane.getChildren().clear();
		Collection<ImageView> imageViews = createImageViews(images);
		tilePane.getChildren().addAll(imageViews);
	}

	private Collection<ImageView> createImageViews(Collection<ImageVO> images) {
		Collection<ImageView> imageViews = new ArrayList<ImageView>();
		for (ImageVO imageVO : images) {
			Image image = new Image(new File(imageVO.getPath()).toURI().toString(), 190, 190, true, true);
			ImageView ImageView = new ImageView(image);
			ImageView.setOnMouseClicked((new EventHandler<MouseEvent>() {

				@Override
				public void handle(MouseEvent mouseEvent) {

					if (mouseEvent.getButton().equals(MouseButton.PRIMARY)) {
						imageView.setImage(new Image(new File(imageVO.getPath()).toURI().toString()));
						resultTable.getSelectionModel().select(imageVO);
					}
				}
			}));
			imageViews.add(ImageView);
		}
		return imageViews;
	}

	private String getDirectoryPath() {
		DirectoryChooser directoryChooser = new DirectoryChooser();
		directoryChooser.setTitle("Choose Directory");
		File folder = directoryChooser.showDialog(new Stage());
		if (folder != null) {
			return folder.getAbsolutePath();
		}
		return null;
	}

	@FXML
	public void showImageInNewWindow(MouseEvent mouseEvent) {
		if (mouseEvent.getButton().equals(MouseButton.PRIMARY)) {

			if (mouseEvent.getClickCount() == 2) {
				Image image = imageView.getImage();
				if(image != null){
					ScrollPane scrollPane = new ScrollPane();
					ImageView newImageView = new ImageView();
					Stage newStage = new Stage();

					newImageView.setImage(image);

					scrollPane.setContent(newImageView);

					setStageDimensions(newStage, image);

					Scene scene = new Scene(scrollPane);
					newStage.setScene(scene);
					newStage.show();
				}
			}
		}
	}

	private void setStageDimensions(Stage newStage, Image image) {
		int screenWidth = (int) Screen.getPrimary().getVisualBounds().getWidth();
		int screenHeight = (int) Screen.getPrimary().getVisualBounds().getHeight();

		if (image.getWidth() < screenWidth / 1.5) {
			newStage.setWidth(image.getWidth() + 20);
		} else {
			newStage.setWidth(screenWidth / 1.5);
		}

		if (image.getHeight() < screenHeight / 1.5) {
			newStage.setHeight(image.getHeight() + 47);
		} else {
			newStage.setHeight(screenHeight / 1.5);
		}
	}
}
