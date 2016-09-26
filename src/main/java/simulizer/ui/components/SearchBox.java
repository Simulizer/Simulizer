package simulizer.ui.components;

import java.util.Collections;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.css.PseudoClass;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import simulizer.ui.interfaces.Searchable;

public class SearchBox<T extends Searchable> extends TextField {
	private final TableView<T> table;
	private ObservableList<T> list = FXCollections.observableArrayList();

	private static final String prompt = "Enter Search Query";
	private boolean prompting = true;

	public SearchBox(TableView<T> table) {
		this.table = table;
		setText(prompt);
		pseudoClassStateChanged(PseudoClass.getPseudoClass("prompting"), prompting);
		textProperty().addListener(e -> updateTableView());
		focusedProperty().addListener((e, o, n) -> {
			if (n && prompting) {
				prompting = false;
				setText("");
			} else if (!n && getText().equals("")) {
				prompting = true;
				setText(prompt);
			}
			pseudoClassStateChanged(PseudoClass.getPseudoClass("prompting"), prompting);
		});
	}

	public void setItems(ObservableList<T> list) {
		this.list = list;
		updateTableView();
	}

	private void updateTableView() {
		ObservableList<T> filtered = FXCollections.observableArrayList();
		filtered.addAll(list);
		if (!getText().equals("") && !prompting) {
			filtered.removeIf(e -> e.matchesSearchTerm(getText()) <= 0);
			Collections.sort(filtered, (a, b) -> Math.round(b.matchesSearchTerm(getText()) - a.matchesSearchTerm(getText())));
		}
		table.setItems(filtered);
	}

}
