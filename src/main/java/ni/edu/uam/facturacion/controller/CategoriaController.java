package ni.edu.uam.facturacion.controller;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.CheckBoxTableCell;
import ni.edu.uam.facturacion.dao.CategoriaDAO;
import ni.edu.uam.facturacion.model.Categoria;
import ni.edu.uam.facturacion.util.Alertas;
import ni.edu.uam.facturacion.util.SceneManager;

import java.io.IOException;
import java.sql.SQLException;

public class CategoriaController {

    @FXML
    private TextField txtNombre;

    @FXML
    private CheckBox chkActiva;

    @FXML
    private TableView<Categoria> tablaCategorias;

    @FXML
    private TableColumn<Categoria, Number> colId;

    @FXML
    private TableColumn<Categoria, String> colNombre;

    @FXML
    private TableColumn<Categoria, Boolean> colActiva;

    private final CategoriaDAO categoriaDAO = new CategoriaDAO();

    private final ObservableList<Categoria> categorias = FXCollections.observableArrayList();

    private Categoria categoriaSeleccionada;

    @FXML
    private void initialize() {
        colId.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().getId()));
        colNombre.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getNombre()));
        colActiva.setCellValueFactory(c -> new SimpleBooleanProperty(c.getValue().isActiva()));
        colActiva.setCellFactory(CheckBoxTableCell.forTableColumn(colActiva));

        tablaCategorias.setItems(categorias);

        tablaCategorias.getSelectionModel().selectedItemProperty()
                .addListener((obs, anterior, nueva) -> seleccionar(nueva));

        cargarCategorias();
    }

    @FXML
    private void guardar() {
        if (!validar()) {
            return;
        }

        Categoria categoria = new Categoria(
                null,
                txtNombre.getText().trim(),
                chkActiva.isSelected()
        );

        try {
            categoriaDAO.guardar(categoria);
            Alertas.exito("Categoría registrada", "La categoría se guardó correctamente.");
            limpiar();
            cargarCategorias();
        } catch (SQLException e) {
            Alertas.error("Error de base de datos", "No se pudo guardar: " + e.getMessage());
        }
    }

    @FXML
    private void actualizar() {
        if (categoriaSeleccionada == null) {
            Alertas.advertencia("Seleccione una categoría de la tabla.");
            return;
        }

        if (!validar()) {
            return;
        }

        categoriaSeleccionada.setNombre(txtNombre.getText().trim());
        categoriaSeleccionada.setActiva(chkActiva.isSelected());

        try {
            categoriaDAO.actualizar(categoriaSeleccionada);
            Alertas.exito("Categoría actualizada", "La categoría se actualizó correctamente.");
            limpiar();
            cargarCategorias();
        } catch (SQLException e) {
            Alertas.error("Error de base de datos", "No se pudo actualizar: " + e.getMessage());
        }
    }

    @FXML
    private void eliminar() {
        if (categoriaSeleccionada == null) {
            Alertas.advertencia("Seleccione una categoría de la tabla.");
            return;
        }

        String mensaje = "¿Eliminar la categoría \"" + categoriaSeleccionada.getNombre() + "\"?";

        if (!Alertas.confirmar("Eliminar categoría", mensaje)) {
            return;
        }

        try {
            categoriaDAO.eliminar(categoriaSeleccionada.getId());
            limpiar();
            cargarCategorias();
        } catch (SQLException e) {
            Alertas.error("Error de base de datos",
                    "No se puede eliminar porque tiene productos asociados. "
                            + "Desmarque \"Activa\" para desactivarla.");
        }
    }

    @FXML
    private void limpiar() {
        categoriaSeleccionada = null;
        txtNombre.clear();
        chkActiva.setSelected(true);
        tablaCategorias.getSelectionModel().clearSelection();
    }

    @FXML
    private void volver() throws IOException {
        SceneManager.switchTo("/ni/edu/uam/facturacion/fxml/menu-principal.fxml");
    }

    private void seleccionar(Categoria categoria) {
        if (categoria == null) {
            return;
        }

        categoriaSeleccionada = categoria;
        txtNombre.setText(categoria.getNombre());
        chkActiva.setSelected(categoria.isActiva());
    }

    private void cargarCategorias() {
        try {
            categorias.setAll(categoriaDAO.listar());
        } catch (SQLException e) {
            Alertas.error("Error de base de datos",
                    "No se pudieron cargar las categorías: " + e.getMessage());
        }
    }

    private boolean validar() {
        if (txtNombre.getText() == null || txtNombre.getText().isBlank()) {
            Alertas.advertencia("El nombre de la categoría es obligatorio.");
            txtNombre.requestFocus();
            return false;
        }
        return true;
    }
}
