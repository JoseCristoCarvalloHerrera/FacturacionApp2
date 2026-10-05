package ni.edu.uam.facturacion.controller;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.CheckBoxTableCell;
import ni.edu.uam.facturacion.dao.CategoriaDAO;
import ni.edu.uam.facturacion.dao.ProductoDAO;
import ni.edu.uam.facturacion.model.Categoria;
import ni.edu.uam.facturacion.model.Producto;
import ni.edu.uam.facturacion.util.Alertas;
import ni.edu.uam.facturacion.util.SceneManager;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;

public class ProductoController {

    @FXML
    private TextField txtCodigo;

    @FXML
    private TextField txtNombre;

    @FXML
    private ComboBox<Categoria> cmbCategoria;

    @FXML
    private TextField txtPrecio;

    @FXML
    private TextField txtExistencia;

    @FXML
    private TextField txtRutaImagen;

    @FXML
    private CheckBox chkActivo;

    @FXML
    private TableView<Producto> tablaProductos;

    @FXML
    private TableColumn<Producto, Number> colId;

    @FXML
    private TableColumn<Producto, String> colCodigo;

    @FXML
    private TableColumn<Producto, String> colNombre;

    @FXML
    private TableColumn<Producto, String> colCategoria;

    @FXML
    private TableColumn<Producto, BigDecimal> colPrecio;

    @FXML
    private TableColumn<Producto, Number> colExistencia;

    @FXML
    private TableColumn<Producto, Boolean> colActivo;

    private final ProductoDAO productoDAO = new ProductoDAO();

    private final CategoriaDAO categoriaDAO = new CategoriaDAO();

    private final ObservableList<Producto> productos = FXCollections.observableArrayList();

    private Producto productoSeleccionado;

    @FXML
    private void initialize() {
        colId.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().getId()));
        colCodigo.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getCodigo()));
        colNombre.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getNombre()));
        colCategoria.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getCategoria().getNombre()));
        colPrecio.setCellValueFactory(c -> new SimpleObjectProperty<>(c.getValue().getPrecioVenta()));
        colExistencia.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().getExistencia()));
        colActivo.setCellValueFactory(c -> new SimpleBooleanProperty(c.getValue().isActivo()));
        colActivo.setCellFactory(CheckBoxTableCell.forTableColumn(colActivo));

        tablaProductos.setItems(productos);

        tablaProductos.getSelectionModel().selectedItemProperty()
                .addListener((obs, anterior, nuevo) -> seleccionar(nuevo));

        cargarCategorias();
        cargarProductos();
    }

    @FXML
    private void guardar() {
        Producto producto = leerFormulario();
        if (producto == null) {
            return;
        }

        try {
            productoDAO.guardar(producto);
            Alertas.exito("Producto registrado", "El producto se guardó correctamente.");
            limpiar();
            cargarProductos();
        } catch (SQLException e) {
            Alertas.error("Error de base de datos", "No se pudo guardar: " + e.getMessage());
        }
    }

    @FXML
    private void actualizar() {
        if (productoSeleccionado == null) {
            Alertas.advertencia("Seleccione un producto de la tabla.");
            return;
        }

        Producto producto = leerFormulario();
        if (producto == null) {
            return;
        }

        producto.setId(productoSeleccionado.getId());

        try {
            productoDAO.actualizar(producto);
            Alertas.exito("Producto actualizado", "El producto se actualizó correctamente.");
            limpiar();
            cargarProductos();
        } catch (SQLException e) {
            Alertas.error("Error de base de datos", "No se pudo actualizar: " + e.getMessage());
        }
    }

    @FXML
    private void eliminar() {
        if (productoSeleccionado == null) {
            Alertas.advertencia("Seleccione un producto de la tabla.");
            return;
        }

        String mensaje = "¿Eliminar el producto \"" + productoSeleccionado.getNombre() + "\"?";

        if (!Alertas.confirmar("Eliminar producto", mensaje)) {
            return;
        }

        try {
            productoDAO.eliminar(productoSeleccionado.getId());
            limpiar();
            cargarProductos();
        } catch (SQLException e) {
            Alertas.error("Error de base de datos", "No se pudo eliminar: " + e.getMessage());
        }
    }

    @FXML
    private void limpiar() {
        productoSeleccionado = null;
        txtCodigo.clear();
        txtNombre.clear();
        cmbCategoria.getSelectionModel().clearSelection();
        txtPrecio.clear();
        txtExistencia.clear();
        txtRutaImagen.clear();
        chkActivo.setSelected(true);
        tablaProductos.getSelectionModel().clearSelection();
    }

    @FXML
    private void volver() throws IOException {
        SceneManager.switchTo("/ni/edu/uam/facturacion/fxml/menu-principal.fxml");
    }

    private void seleccionar(Producto producto) {
        if (producto == null) {
            return;
        }

        productoSeleccionado = producto;
        txtCodigo.setText(producto.getCodigo());
        txtNombre.setText(producto.getNombre());
        txtPrecio.setText(producto.getPrecioVenta().toPlainString());
        txtExistencia.setText(String.valueOf(producto.getExistencia()));
        txtRutaImagen.setText(producto.getRutaImagen());
        chkActivo.setSelected(producto.isActivo());

        // Se busca por id porque el objeto del ComboBox es otra instancia
        cmbCategoria.getItems().stream()
                .filter(c -> c.getId().equals(producto.getCategoria().getId()))
                .findFirst()
                .ifPresentOrElse(
                        c -> cmbCategoria.getSelectionModel().select(c),
                        () -> cmbCategoria.getSelectionModel().clearSelection()
                );
    }

    private Producto leerFormulario() {
        String codigo = txtCodigo.getText().trim();
        String nombre = txtNombre.getText().trim();
        Categoria categoria = cmbCategoria.getValue();

        if (codigo.isEmpty() || nombre.isEmpty() || categoria == null
                || txtPrecio.getText().isBlank() || txtExistencia.getText().isBlank()) {
            Alertas.advertencia("Complete los campos obligatorios.");
            return null;
        }

        BigDecimal precio;
        int existencia;

        try {
            precio = new BigDecimal(txtPrecio.getText().trim());
        } catch (NumberFormatException e) {
            Alertas.advertencia("El precio debe ser un número, por ejemplo 25.50");
            txtPrecio.requestFocus();
            return null;
        }

        try {
            existencia = Integer.parseInt(txtExistencia.getText().trim());
        } catch (NumberFormatException e) {
            Alertas.advertencia("La existencia debe ser un número entero.");
            txtExistencia.requestFocus();
            return null;
        }

        String rutaImagen = txtRutaImagen.getText().trim();

        return new Producto(
                null,
                codigo,
                nombre,
                categoria,
                precio,
                existencia,
                rutaImagen.isEmpty() ? null : rutaImagen,
                chkActivo.isSelected()
        );
    }

    private void cargarCategorias() {
        try {
            cmbCategoria.setItems(FXCollections.observableArrayList(categoriaDAO.listarActivas()));
        } catch (SQLException e) {
            Alertas.error("Error de base de datos",
                    "No se pudieron cargar las categorías: " + e.getMessage());
        }
    }

    private void cargarProductos() {
        try {
            productos.setAll(productoDAO.listar());
        } catch (SQLException e) {
            Alertas.error("Error de base de datos",
                    "No se pudieron cargar los productos: " + e.getMessage());
        }
    }
}
