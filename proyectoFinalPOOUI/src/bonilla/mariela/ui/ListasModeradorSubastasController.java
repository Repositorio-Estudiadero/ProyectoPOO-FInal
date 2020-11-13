package bonilla.mariela.ui;

import bonilla.mariela.bl.dao.DaoFactory;
import bonilla.mariela.bl.subasta.ISubastaDao;
import bonilla.mariela.bl.subasta.Subasta;
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
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.Date;
import java.util.ResourceBundle;

public class ListasModeradorSubastasController implements Initializable {

    @FXML
    TableColumn<Subasta,String> columnFechaInicio;
    @FXML TableColumn<Subasta,String> columnFechaVencimiento;
    @FXML TableColumn<Subasta,String> columnTiempoFaltante;
    @FXML TableColumn<Subasta,String> columnCantidadItems;
    @FXML TableColumn<Subasta,String>  columnPrecioMinimo;
    @FXML TableColumn <Subasta,String> columnVendedor;
    @FXML TableColumn <Subasta,String> columnPuntuacionS;

    @FXML
    TableView<Subasta> tabla_subastas;
    ObservableList<Subasta> listaSubastas;

    @FXML JFXButton verItemsSubasta;
    @FXML JFXButton verOfertasSubasta;
    @FXML JFXButton verInfoSubasta;

    private double xSetOff;
    private double ySetOff;

    @FXML
    public void cerrarApp() {
        Platform.exit();
        System.out.println("Se cerró la aplicación");
    }


    public void salirVentana(MouseEvent event) {
        Stage escenaPrincipal = (Stage)((Node) event.getSource()).getScene().getWindow();
        escenaPrincipal.setIconified(true);
    }


    @FXML
    private void irMenuListas(ActionEvent event) throws IOException {
        Stage escenaPrincipal = (Stage)((Node) event.getSource()).getScene().getWindow();

        Parent ruta = FXMLLoader.load(getClass().getResource("listas_moderador.fxml"));

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


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        iniciarTablaSubastas();
        listasSubastas();
    }

    private void iniciarTablaSubastas() {
        iniciarColsSubastas();
    }

    private void iniciarColsSubastas() {

        columnFechaInicio.setCellValueFactory(new PropertyValueFactory<>("fechaInicio"));
        columnFechaVencimiento.setCellValueFactory(new PropertyValueFactory<>("fechaVencimiento"));
        columnTiempoFaltante.setCellValueFactory(new PropertyValueFactory<>("tiempoFaltante"));
        columnCantidadItems.setCellValueFactory(new PropertyValueFactory<>("cantidad_items"));
        columnPrecioMinimo.setCellValueFactory(new PropertyValueFactory<>("precioMinimo"));
        columnVendedor.setCellValueFactory(new PropertyValueFactory<>("numVendedor"));
        columnPuntuacionS.setCellValueFactory(new PropertyValueFactory<>("puntuacion"));

        listaSubastas = FXCollections.observableArrayList();
        tabla_subastas.setItems(listaSubastas);
    }


    public void listasSubastas() {
        try {
            DaoFactory factory = DaoFactory.getDaoFactory(DaoFactory.SQLSERVER);
            ObservableList<Subasta> listaSubastas = FXCollections.observableArrayList();

            ISubastaDao dao = factory.getSubastaDao();
            for (Subasta dato : dao.listarSubastas().values()) {
                dato.setTiempoFaltante(calcularTiempoFaltante(dato.getFechaVencimiento()));
                listaSubastas.add(dato);
            }
        }catch (SQLException e) {
            System.out.println(e.getErrorCode());
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

    public void mouseClicked(MouseEvent e)
    {
        int fila = -1;
        fila = tabla_subastas.getSelectionModel().selectedIndexProperty().get();

        if (fila > -1) {
            verInfoSubasta.setDisable(false);
            verItemsSubasta.setDisable(false);
            verOfertasSubasta.setDisable(false);
        } else if (fila == -1) {
            verInfoSubasta.setDisable(true);
            verItemsSubasta.setDisable(true);
            verOfertasSubasta.setDisable(true);
        }
    }
}
