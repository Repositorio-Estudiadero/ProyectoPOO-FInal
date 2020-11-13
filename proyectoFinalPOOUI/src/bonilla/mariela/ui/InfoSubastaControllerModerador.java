package bonilla.mariela.ui;

import bonilla.mariela.bl.oferta.Oferta;
import com.jfoenix.controls.JFXButton;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.controlsfx.control.Rating;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class InfoSubastaControllerModerador implements Initializable {
    private double xSetOff;
    private double ySetOff;


    @FXML
    Label   posicion_precio,
            posicion_estado,
            posicion_fecha,
            posicion_tiempo;

    @FXML
    Rating promedio_calificacion;

    @FXML
    JFXButton btn_vencer_subasta,
            btn_ver_items;

    @FXML
    TableView<Oferta> tabla_ofertas;
    @FXML
    TableColumn<Oferta,String> columnOferta;
    @FXML TableColumn<Oferta,String> columnOferente;

    @FXML
    public void cerrarApp() {
        Platform.exit();
        System.out.println("Se cerró la aplicación");
    }


    public void salirVentana(MouseEvent event) {
        Stage escenaPrincipal = (Stage)((Node) event.getSource()).getScene().getWindow();
        escenaPrincipal.setIconified(true);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        //traer el estado de la subasta, si es Activa, el boton se activa, sino es disabled
        //btn_vencer_subasta.Disable(true)

        iniciarTabla();
        listarOfertas();

    }


    @FXML
    private void ver_items_subasta() throws IOException {

        Stage escenaItemsSubasta = new Stage();
        escenaItemsSubasta.initStyle(StageStyle.TRANSPARENT);
        Scene nuevaEscena;

        Parent ruta = FXMLLoader.load(getClass().getResource("bonilla/mariela/ui/items_subasta.fxml"));

        nuevaEscena = new Scene(ruta);

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
                escenaItemsSubasta.setX(event.getScreenX()-xSetOff);
                escenaItemsSubasta.setY(event.getScreenY()-ySetOff);
            }
        });

        escenaItemsSubasta.setScene(nuevaEscena);
        escenaItemsSubasta.show();
    }


    @FXML
    private void marcarSubastaVencida() throws IOException {
        btn_vencer_subasta.setDisable(true);
        System.out.println("Subasta vencida");
        posicion_estado.setText("Vencida");
    }

    private void iniciarTabla() {

        iniciarColumnas();
    }

    /*
    Se van a iniciar las columnas
     */
    private void iniciarColumnas() {
        columnOferta.setCellValueFactory(new PropertyValueFactory<>("precio"));
        columnOferente.setCellValueFactory(new PropertyValueFactory<>("nomOferente"));

    }

    /*
    Hace la ejecucion para que se liste la informacion
     */
    private void listarOfertas() {

    }
}
