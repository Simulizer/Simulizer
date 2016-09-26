package simulizer.ui.components;

import java.util.Collections;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import simulizer.ui.interfaces.Searchable;

public class SearchBox<T extends Searchable> extends StackPane {

	private final TableView<T> table;
	private final TextField searchBox = new TextField();
	private ObservableList<T> list = FXCollections.observableArrayList();

	public SearchBox(TableView<T> table) {
		this.table = table;
		searchBox.textProperty().addListener(e -> updateTableView());
		getChildren().add(searchBox);
	}

	public void setItems(ObservableList<T> list) {
		this.list = list;
		updateTableView();
	}

	private void updateTableView() {
		String searchTerm = searchBox.getText();
		ObservableList<T> filtered = FXCollections.observableArrayList();
		filtered.addAll(list);
		if (!searchTerm.equals("")) {
			filtered.removeIf(e -> e.matchesSearchTerm(searchTerm) <= 0);
			Collections.sort(filtered, (a, b) -> Math.round(b.matchesSearchTerm(searchTerm) - a.matchesSearchTerm(searchTerm)));
		}
		table.setItems(filtered);
	}

}
