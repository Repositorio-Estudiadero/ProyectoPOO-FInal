/*
 * Copyright (c) 2019. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package bonilla.mariela.ui;

import bonilla.mariela.bl.subasta.Subasta;
import bonilla.mariela.tl.ControllerInfoSubasta;
import bonilla.mariela.tl.ControllerSubasta;
import com.jfoenix.controls.JFXButton;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.Period;
import java.util.ResourceBundle;

public class MisSubastasVendedor implements Initializable {


    private double xSetOff;
    private double ySetOff;

    @FXML
    JFXButton verItemsSubasta;
    @FXML JFXButton verInfoSubasta;


    @FXML
    TableColumn<Subasta,String> columnFechaInicio;
    @FXML TableColumn<Subasta,String> columnFechaVencimiento;
    @FXML TableColumn<Subasta,String> columnTiempoFaltante;
    @FXML TableColumn<Subasta,String> columnCantidadItems;
    @FXML TableColumn<Subasta,String>  columnPrecioMinimo;
    @FXML TableColumn <Subasta,String> columnEstado;



    @FXML
    TableView<Subasta> tabla_mis_subastas;


    ObservableList<Subasta> listaSubastas ;

    @FXML
    public void cerrarApp() {
        Platform.exit();
        System.out.println("Se cerró la aplicación");
    }


    public void salirVentana(MouseEvent event) {
        Stage escenaPrincipal = (Stage)((Node) event.getSource()).getScene().getWindow();
        escenaPrincipal.setIconified(true);
    }



    public void mouseClickedSubastas(MouseEvent e)
    {
        int fila = -1;
        fila = tabla_mis_subastas.getSelectionModel().selectedIndexProperty().get();

        if (fila > -1) {
            verInfoSubasta.setDisable(false);
            verItemsSubasta.setDisable(false);
        } else if (fila == -1) {
            verInfoSubasta.setDisable(true);
            verItemsSubasta.setDisable(true);
        }
    }


    @Override
    public void initialize(URL location, ResourceBundle resources){
        iniciarTabla();
        listarSubastas();
    }

    private void iniciarTabla() {
        iniciarCols();
    }

    private void iniciarCols() {
        columnFechaInicio.setCellValueFactory(new PropertyValueFactory<>("fechaInicio"));
        columnFechaVencimiento.setCellValueFactory(new PropertyValueFactory<>("fechaVencimiento"));
        columnTiempoFaltante.setCellValueFactory(new PropertyValueFactory<>("tiempoFaltante"));
        columnCantidadItems.setCellValueFactory(new PropertyValueFactory<>("cantidad_items"));
        columnPrecioMinimo.setCellValueFactory(new PropertyValueFactory<>("precioMinimo"));
        columnEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));

        listaSubastas = FXCollections.observableArrayList();
        tabla_mis_subastas.setItems(listaSubastas);
    }



    public void listarSubastas() {
        int cont = 0;

        try {
            ControllerSubasta gestorSubasta = new ControllerSubasta();
            for (Subasta dato: gestorSubasta.listarSubastas()){
                dato.setTiempoFaltante(calcularTiempoFaltante(dato.getFechaVencimiento()));
                listaSubastas.add(dato);
            }
        }catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }



    /**
     * @author Mariela Bonilla
     * @param1 fechaVencimiento: Fecha que recibe de la subasta actual para calcular los años
     *          que faltan para su vencimiento
     * @version 1.0
     */
    private String calcularTiempoFaltante(LocalDate fechaVencimiento)  {
        LocalDate fechaActual = LocalDate.now();
        Period tiempo = Period.between(fechaActual,fechaVencimiento);
        String faltante = "";
        if (tiempo.getDays() <= 0 && tiempo.getMonths() <= 0 && tiempo.getYears() <= 0) {
            faltante = "0 años, 0 meses, 0 días";
        } if (tiempo.getDays() > 0 && tiempo.getMonths() <= 0 && tiempo.getYears() <= 0) {
            faltante = "0 años, 0 meses, " + tiempo.getDays() + " días";
        } else if (tiempo.getDays() > 0 && tiempo.getMonths() > 0 && tiempo.getYears() <= 0) {
            faltante = "0 años," + tiempo.getMonths() + " meses, "+tiempo.getDays()+" días";
        }
        else if (tiempo.getDays() > 0 && tiempo.getMonths() > 0 && tiempo.getYears() > 0) {
            faltante = tiempo.getYears()+" años," + tiempo.getMonths() + " meses, "+tiempo.getDays()+" días";
        }
        return faltante;
    }




    public void verInfoSubasta(ActionEvent event) throws IOException {
        Stage escenaPrincipal = (Stage)((Node) event.getSource()).getScene().getWindow();

        int fila = -1;
        fila = tabla_mis_subastas.getSelectionModel().selectedIndexProperty().get();

        Subasta subasta = tabla_mis_subastas.getSelectionModel().getSelectedItem();

        ControllerInfoSubasta gestorInfoSubasta = new ControllerInfoSubasta();

        gestorInfoSubasta.guardarDatos(subasta.getId(),subasta.getFechaVencimiento(), subasta.getIdentificacionVendedor(),
                subasta.getPrecioMinimo(), subasta.getCantidad_items(), subasta.getEstado());



        Parent ruta = FXMLLoader.load(getClass().getResource("info_subasta_vendedor.fxml"));

        ruta.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                xSetOff = event.getSceneX();
                ySetOff = event.getScreenY();
            }
        });
        ruta.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                escenaPrincipal.setX(event.getScreenX()-xSetOff);
                escenaPrincipal.setY(event.getScreenY()-ySetOff);
            }
        });

        Scene nueva_escena = new Scene(ruta);
        escenaPrincipal.hide();
        escenaPrincipal.setScene(nueva_escena);
        escenaPrincipal.show();

    }


    public void verItemsSubasta(ActionEvent event) throws IOException {
        Stage escenaPrincipal = new Stage();

        int fila = -1;
        fila = tabla_mis_subastas.getSelectionModel().selectedIndexProperty().get();

        Subasta subasta = tabla_mis_subastas.getSelectionModel().getSelectedItem();

        ControllerInfoSubasta gestorInfoSubasta = new ControllerInfoSubasta();

        gestorInfoSubasta.guardarDatos(subasta.getId(),subasta.getFechaVencimiento(), subasta.getIdentificacionVendedor(),
                subasta.getPrecioMinimo(), subasta.getCantidad_items() ,subasta.getEstado());



        Parent ruta = FXMLLoader.load(getClass().getResource("items_subasta.fxml"));


        Scene nueva_escena = new Scene(ruta);

        escenaPrincipal.setScene(nueva_escena);
        escenaPrincipal.show();

    }



}
