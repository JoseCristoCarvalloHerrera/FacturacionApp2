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
import ni.edu.uam.facturacion.exception.Campo;
import ni.edu.uam.facturacion.exception.ValidacionException;
import ni.edu.uam.facturacion.model.Categoria;
import ni.edu.uam.facturacion.model.Producto;
import ni.edu.uam.facturacion.util.Alertas;
import ni.edu.uam.facturacion.util.SceneManager;
import ni.edu.uam.facturacion.validacion.ValidadorProducto;

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
        try {
            Producto producto = obtenerProductoFormulario();

            if (productoDAO.existeCodigo(producto.getCodigo(), null)) {
                Alertas.advertencia("Código duplicado",
                        "Ya existe un producto con el código \"" + producto.getCodigo() + "\".");
                enfocar(Campo.PRODUCTO_CODIGO);
                return;
            }

            productoDAO.guardar(producto);

            Alertas.exito("Producto registrado", "El producto se guardó correctamente.");
            limpiar();
            cargarProductos();
        } catch (ValidacionException e) {
            Alertas.advertencia(e.getMessage());
            enfocar(e.getCampo());
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

        try {
            Producto producto = obtenerProductoFormulario();

            producto.setId(productoSeleccionado.getId());

            if (productoDAO.existeCodigo(producto.getCodigo(), producto.getId())) {
                Alertas.advertencia("Código duplicado",
                        "Ya existe otro producto con el código \"" + producto.getCodigo() + "\".");
                enfocar(Campo.PRODUCTO_CODIGO);
                return;
            }

            productoDAO.actualizar(producto);

            Alertas.exito("Producto actualizado", "El producto se actualizó correctamente.");
            limpiar();
            cargarProductos();
        } catch (ValidacionException e) {
            Alertas.advertencia(e.getMessage());
            enfocar(e.getCampo());
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

    private Producto obtenerProductoFormulario() {

        String codigo = txtCodigo.getText().trim();
        String nombre = txtNombre.getText().trim();
        Categoria categoria = cmbCategoria.getValue();
        String imagen = txtRutaImagen.getText().trim();

        ValidadorProducto.validarCodigo(codigo);
        ValidadorProducto.validarNombre(nombre);
        ValidadorProducto.validarCategoria(categoria);
        ValidadorProducto.validarRutaImagen(imagen);

        return new Producto(
                null,
                codigo,
                nombre,
                categoria,
                ValidadorProducto.leerPrecio(txtPrecio.getText()),
                ValidadorProducto.leerExistencia(txtExistencia.getText()),
                imagen.isEmpty() ? null : imagen,
                chkActivo.isSelected()
        );
    }

    private void enfocar(Campo campo) {

        switch (campo) {

            case PRODUCTO_CODIGO -> txtCodigo.requestFocus();
            case PRODUCTO_NOMBRE -> txtNombre.requestFocus();
            case PRODUCTO_CATEGORIA -> cmbCategoria.requestFocus();
            case PRODUCTO_PRECIO -> txtPrecio.requestFocus();
            case PRODUCTO_EXISTENCIA -> txtExistencia.requestFocus();
            case PRODUCTO_IMAGEN -> txtRutaImagen.requestFocus();

            default -> txtCodigo.requestFocus();
        }
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
