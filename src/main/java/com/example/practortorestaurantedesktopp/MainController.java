package com.example.practortorestaurantedesktopp;

import com.example.practortorestaurantedesktopp.api.ApiClient;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.awt.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicReference;

public class MainController implements Initializable {

    @FXML
    private TabPane mainTabPane;

    @FXML
    private VBox pedidoContainer1;
    @FXML
    private Button cancelarPedidoBtn1;
    @FXML
    private Button enviarPedidoBtn1;

    @FXML
    private VBox chatContainer1;
    @FXML
    private TextFlow messagesArea1;
    @FXML
    private TextField inputField1;
    @FXML
    private Button sendButton1;

    @FXML
    private VBox chatContainer2;
    @FXML
    private TextFlow messagesArea2;
    @FXML
    private TextField inputField2;
    @FXML
    private Button sendButton2;

    @FXML
    private VBox chatContainer3;
    @FXML
    private TextFlow messagesArea3;
    @FXML
    private TextField inputField3;
    @FXML
    private Button sendButton3;

    @FXML
    private VBox chatContainer4;
    @FXML
    private TextFlow messagesArea4;
    @FXML
    private TextField inputField4;
    @FXML
    private Button sendButton4;

    @FXML
    private VBox chatContainer5;
    @FXML
    private TextFlow messagesArea5;
    @FXML
    private TextField inputField5;
    @FXML
    private Button sendButton5;


    @FXML
    private VBox pedidoContainer2;
    @FXML
    private Button cancelarPedidoBtn2;
    @FXML
    private Button enviarPedidoBtn2;

    @FXML
    private VBox pedidoContainer3;
    @FXML
    private Button cancelarPedidoBtn3;
    @FXML
    private Button enviarPedidoBtn3;

    @FXML
    private VBox pedidoContainer4;
    @FXML
    private Button cancelarPedidoBtn4;
    @FXML
    private Button enviarPedidoBtn4;

    @FXML
    private VBox pedidoContainer5;
    @FXML
    private Button cancelarPedidoBtn5;
    @FXML
    private Button enviarPedidoBtn5;

    @FXML
    private Button hardReset1;
    @FXML
    private Button hardReset2;
    @FXML
    private Button hardReset3;
    @FXML
    private Button hardReset4;
    @FXML
    private Button hardReset5;

    private ApiClient apiClient = null;

    private ArrayList<String> listaIdsMongo1;
    private ArrayList<String> listaIdsMongo2;
    private ArrayList<String> listaIdsMongo3;
    private ArrayList<String> listaIdsMongo4;
    private ArrayList<String> listaIdsMongo5;

    private final Color COLOR_PROPIO = Color.web("#2196F3");
    private final Color COLOR_RESTAURANTE = Color.web("#4CAF50");
    private final Color COLOR_SISTEMA = Color.web("#FF9800");
    private final Color COLOR_ERROR = Color.web("#F44336");

    @FXML
    private Slider volumenSlider;
    @FXML
    private TextArea lastPedido;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        // para cambio de estado a pedidos
        listaIdsMongo1 = new ArrayList<>();
        listaIdsMongo2 = new ArrayList<>();
        listaIdsMongo3 = new ArrayList<>();
        listaIdsMongo4 = new ArrayList<>();
        listaIdsMongo5 = new ArrayList<>();

        CommsManager.getInstance().setMainController(this);
        apiClient = ApiClient.getInstance();

        // setear handlers pa chat y butt reset
        setupChatForMesa(1, inputField1, sendButton1, messagesArea1);
        setupChatForMesa(2, inputField2, sendButton2, messagesArea2);
        setupChatForMesa(3, inputField3, sendButton3, messagesArea3);
        setupChatForMesa(4, inputField4, sendButton4, messagesArea4);
        setupChatForMesa(5, inputField5, sendButton5, messagesArea5);

        setupResetButtParaMesa(1, hardReset1);
        setupResetButtParaMesa(2, hardReset2);
        setupResetButtParaMesa(3, hardReset3);
        setupResetButtParaMesa(4, hardReset4);
        setupResetButtParaMesa(5, hardReset5);

        mainTabPane.widthProperty().addListener((obs, oldVal, newVal) -> {
            adjustTabWidths();
        });
        adjustTabWidths();

        setupControlVolumen();


    }

    private void setupChatForMesa(int mesaNumero, TextField inputField, Button sendButton, TextFlow messagesArea) {
        // para validar texto
        final String spanishTextRegex = "^[a-zA-ZáéíóúÁÉÍÓÚñÑüÜ\\s.,!?¿¡]*$";

        // estilo inicial boton
        sendButton.setStyle("-fx-background-color: #9E9E9E; -fx-text-fill: white;"); // Gris deshabilitado
        sendButton.setDisable(true);

        // listener para validacion
        inputField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null) return;

            String text = newValue.trim();
            boolean isValid = text.matches(spanishTextRegex) && !text.isEmpty();

            // habilitar/deshabilitar boton y cambiar color
            sendButton.setDisable(!isValid);
            if (isValid) {
                sendButton.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white;"); // Azul habilitado
            } else {
                sendButton.setStyle("-fx-background-color: #9E9E9E; -fx-text-fill: white;"); // Gris deshabilitado
            }

            // mostrar tooltip error
            if (!text.isEmpty() && !text.matches(spanishTextRegex)) {
                inputField.setStyle("-fx-border-color: red; -fx-border-width: 1px;");
                Tooltip tooltip = new Tooltip("Solo se permiten letras, acentos, espacios y signos basicos (.,!?¿¡)");
                inputField.setTooltip(tooltip);
            } else {
                inputField.setStyle("");
                inputField.setTooltip(null);
            }
        });

        // Configurar boton
        sendButton.setOnAction(event -> {
            String text = inputField.getText().trim();
            if (!text.isEmpty() && text.matches(spanishTextRegex)) {
                CommsManager.getInstance().mainAwebSocket(text, "Mesa" + mesaNumero);
                appendMensajeConColor(messagesArea, "Restaurante: " + text, COLOR_PROPIO);
                inputField.clear();

                // restablecer a estado deshabilitado despues de enviar
                sendButton.setDisable(true);
                sendButton.setStyle("-fx-background-color: #9E9E9E; -fx-text-fill: white;");
            }
        });

        // habilitar Enter en TextField
        inputField.setOnAction(event -> {
            if (!sendButton.isDisabled()) {
                sendButton.fire();
            }
        });
    }

    // para agregar mensajes con color
    private void appendMensajeConColor(TextFlow textFlow, String mensaje, Color color) {
        Platform.runLater(() -> {
            Text textNode = new Text(mensaje + "\n");
            textNode.setFill(color);
            textFlow.getChildren().add(textNode);

            // auto scroll
            textFlow.layout();
            textFlow.requestLayout();
        });
    }

    // determinar color basado en contenido de mensaje
    private Color getColorParaMensaje(String mensaje) {
        if (mensaje.startsWith("Restaurante:")) {
            return COLOR_PROPIO;
        } else if (mensaje.toLowerCase().contains("[error]") ||
                mensaje.toLowerCase().contains("error") ||
                mensaje.toLowerCase().contains("failed")) {
            return COLOR_ERROR;
        } else if (mensaje.toLowerCase().contains("conectando") ||
                mensaje.toLowerCase().contains("bienvenidos") ||
                mensaje.toLowerCase().contains("cerrando") ||
                mensaje.toLowerCase().contains("cerrado") ||
                mensaje.toLowerCase().contains("conectado") ||
                mensaje.toLowerCase().contains("disconnected")) {
            return COLOR_SISTEMA;
        } else {
            return COLOR_RESTAURANTE;
        }
    }

    // preparar botones de reset manual
    private void setupResetButtParaMesa(int mesaNumero, Button resetButt) {
        if (resetButt == null) return;

        resetButt.setOnAction(event -> {
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Resetear Mesa");
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            dialogStage.initStyle(StageStyle.UNDECORATED);
            dialogStage.setResizable(false);

            VBox mainBox = new VBox(15);
            mainBox.setPadding(new Insets(20));
            mainBox.setAlignment(Pos.CENTER);

            Label titleLabel = new Label("RESETEAR MESA");
            titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #dc3545;");

            Label messageLabel = new Label("¿Confirmar reset de la Mesa " + mesaNumero + "?");
            messageLabel.setStyle("-fx-font-size: 14px;");
            messageLabel.setWrapText(true);
            messageLabel.setMaxWidth(250);

            HBox buttonBox = new HBox(10);
            buttonBox.setAlignment(Pos.CENTER);

            Button cancelBtn = new Button("CANCELAR");
            cancelBtn.setStyle("-fx-background-color: transparent; -fx-border-color: #6c757d; -fx-text-fill: #6c757d; -fx-min-width: 100px;");

            Button resetBtn = new Button("RESETEAR");
            resetBtn.setStyle("-fx-background-color: #dc3545; -fx-text-fill: white; -fx-min-width: 100px;");

            buttonBox.getChildren().addAll(cancelBtn, resetBtn);

            Separator separator = new Separator();

            mainBox.getChildren().addAll(titleLabel, messageLabel, separator, buttonBox);

            // acciones
            cancelBtn.setOnAction(e -> dialogStage.close());

            resetBtn.setOnAction(e -> {
                dialogStage.close();

                // ejecutar exterminatus y mostrar modal de confirmacion
                exterminatus(mesaNumero, () -> {
                    Platform.runLater(() -> {
                        showMiniModalConfirmacion(mesaNumero);
                    });
                });
            });

            Scene scene = new Scene(mainBox);
            dialogStage.setScene(scene);
            dialogStage.sizeToScene();
            dialogStage.showAndWait();
        });
    }

    // FOR THE EMPEROR
    private void exterminatus(int numeroMesa, Runnable onComplete) {

        String mesaId = "Mesa" + numeroMesa;

        // primero borrar pedidos de la mesa
        apiClient.deleteMesa(mesaId)
                .thenCompose(result -> {
                    return apiClient.cambiarEstadoMesa(mesaId, false);
                })
                .thenAccept(result -> {
                    // limpieza du UI y variables
                    Platform.runLater(() -> {
                        limpiarPedidoContainer(numeroMesa);
                        appendMensajeSistema(numeroMesa, "Mesa reseteada");
                        appendMensajeSistema(numeroMesa, "----------------------------------------------------------");

                        // finalmente ejecutar callback
                        if (onComplete != null) {
                            onComplete.run();
                        }
                    });
                })
                .exceptionally(throwable -> {
                    System.err.println("Error en hard reset para Mesa " + numeroMesa + ": " + throwable.getMessage());
                    // ejecutar callback incluso en caso de error
                    Platform.runLater(() -> {
                        if (onComplete != null) {
                            onComplete.run();
                        }
                    });
                    return null;
                });
    }

    private void showMiniModalConfirmacion(int mesaNumero) {
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.initStyle(StageStyle.UNDECORATED);

        VBox box = new VBox(15);
        box.setPadding(new Insets(20));
        box.setAlignment(Pos.CENTER);

        Label label = new Label("Mesa " + mesaNumero + " reseteada");
        Button okBtn = new Button("OK");
        okBtn.setOnAction(e -> stage.close());

        box.getChildren().addAll(label, okBtn);

        Scene scene = new Scene(box);
        stage.setScene(scene);
        stage.sizeToScene();
        stage.showAndWait();
    }

    // procesado de mensajes para chat
    public void manejarChat(WebSocketController.Message mensaje) {
        String sender = mensaje.sender;
        System.out.println("MC sender: " + sender);
        int numeroMesa = 0;
        switch (sender) {
            case "Mesa1":
                numeroMesa = 1;
                break;
            case "Mesa2":
                numeroMesa = 2;
                break;
            case "Mesa3":
                numeroMesa = 3;
                break;
            case "Mesa4":
                numeroMesa = 4;
                break;
            case "Mesa5":
                numeroMesa = 5;
                break;
        }

        TextFlow targetArea = getMensaje(numeroMesa);
        if (targetArea != null) {
            String mensajeCompleto = sender + ": " + mensaje.message;
            Color color = getColorParaMensaje(mensajeCompleto);
            appendMensajeConColor(targetArea, mensajeCompleto, color);
        }

    }

    private TextFlow getMensaje(int mesaNumero) {
        switch (mesaNumero) {
            case 1:
                return messagesArea1;
            case 2:
                return messagesArea2;
            case 3:
                return messagesArea3;
            case 4:
                return messagesArea4;
            case 5:
                return messagesArea5;
            default:
                return null;
        }
    }

    // para mensajes de sistema
    public void appendMensajeSistema(int mesaNumero, String mensaje) {
        TextFlow targetFlow = getMensaje(mesaNumero);
        if (targetFlow != null) {
            appendMensajeConColor(targetFlow, mensaje, COLOR_SISTEMA);
        }
    }

    // para mensajes de error
    public void appendMensajeError(int mesaNumero, String mensaje) {
        TextFlow targetFlow = getMensaje(mesaNumero);
        if (targetFlow != null) {
            appendMensajeConColor(targetFlow, mensaje, COLOR_ERROR);
        }
    }

    // devuelve pedidos de mesa determinada
    private void leerPedidoMesaDeBBDD(ApiClient cliente, String mesa) {
        // por si acaso
        limpiarListaIdsMongo(mesa);

        // llamada API
        cliente.readMesa(mesa)
                .thenAccept(json -> {
                    try {
                        var jsonObject = json.getAsJsonObject();
                        String resultType = jsonObject.get("type").getAsString();
                        boolean success = "success".equals(resultType);
                        String message = jsonObject.has("message") ? jsonObject.get("message").getAsString() : "Sin mensaje";

                        if (success) {
                            if (jsonObject.has("data")) {
                                AtomicReference<Double> total = new AtomicReference<>(0.0);
                                var dataArray = jsonObject.getAsJsonArray("data");

                                // obtener VBox correspondiente a la mesa
                                VBox pedidoContainer = getPedidoContainer(mesa);
                                if (pedidoContainer == null) {
                                    System.err.println("No se encontró contenedor para: " + mesa);
                                    return;
                                }

                                // oobtener lista para mesa
                                ArrayList<String> listaIdsMesa = getListaIdsMongo(mesa);

                                // limpiar contenedor y agregar productos
                                Platform.runLater(() -> {
                                    pedidoContainer.getChildren().clear();

                                    dataArray.forEach(item -> {
                                        var pedidoObj = item.getAsJsonObject();

                                        // filtramos por estado productos pedidos (no servidos a mesa)
                                        boolean haSidoServido = pedidoObj.has("haSidoServido")
                                                && pedidoObj.get("haSidoServido").getAsBoolean();

                                        // filtrar por estado y obtener MongoId
                                        if (!haSidoServido) {
                                            String mongoId = pedidoObj.has("_id") ? pedidoObj.get("_id").getAsString() : "Sin ID";
                                            listaIdsMesa.add(mongoId);

                                            if (pedidoObj.has("pedidos")) {
                                                var pedidosArray = pedidoObj.getAsJsonArray("pedidos");

                                                // preparamos contenedor para productos
                                                Label idLabel = new Label("Pedido ID: " + mongoId);
                                                idLabel.setWrapText(true);
                                                idLabel.setStyle("-fx-padding: 5px; -fx-background-color: #f0f0f0; -fx-font-weight: bold;");
                                                pedidoContainer.getChildren().add(idLabel);

                                                // preparamos productos
                                                pedidosArray.forEach(p -> {
                                                    var producto = p.getAsJsonObject();
                                                    int id = producto.get("id").getAsInt();
                                                    String nombre = producto.get("nombre").getAsString();
                                                    double precio = producto.get("precio").getAsDouble();
                                                    int cantidad = producto.get("cantidad").getAsInt();
                                                    double subtotal = precio * cantidad;
                                                    total.set(total.get() + subtotal);

                                                    // lo que ve el usuario
                                                    String productoText = String.format("→ Producto %d: %s (%.2f) - Cantidad: %d = Subtotal %.2f", id, nombre, precio, cantidad, subtotal);

                                                    // agregar a contenedor
                                                    Label productoLabel = new Label(productoText);
                                                    productoLabel.setWrapText(true);
                                                    productoLabel.setStyle("-fx-padding: 5px; -fx-border-color: #ccc; -fx-border-width: 0 0 1 0;");
                                                    pedidoContainer.getChildren().add(productoLabel);
                                                });

                                                // separador entre pedidos
                                                Separator separator = new Separator();
                                                separator.setStyle("-fx-padding: 5px 0;");
                                                pedidoContainer.getChildren().add(separator);
                                            }
                                        } else {
                                            // mostrar por terminal los pedidos ya servidos, si los hubiese
                                            String mongoIdServido = pedidoObj.has("_id") ? pedidoObj.get("_id").getAsString() : "Sin ID";
                                            System.out.println("Mongo ID (servido): " + mongoIdServido);
                                        }
                                    });

                                    // mostrar total
                                    if (total.get() > 0) {
                                        Label precioLabel = new Label(String.format("→ Precio Total: %.2f", total.get()));
                                        precioLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
                                        pedidoContainer.getChildren().add(precioLabel);

                                        // mostrar botonera de operaciones
                                        mostrarBotonesPedido(obtenerNumeroMesa(mesa));
                                    } else {
                                        Label noPedidosLabel = new Label("No hay pedidos pendientes");
                                        noPedidosLabel.setStyle("-fx-padding: 10px; -fx-font-style: italic;");
                                        pedidoContainer.getChildren().add(noPedidosLabel);

                                        // ocultar botonera si no hay pedidos pendientes
                                        ocultarBotonesPedido(obtenerNumeroMesa(mesa));
                                    }
                                });
                            }
                        } else {
                            System.out.println("You Died: " + message);
                        }

                    } catch (Exception e) {
                        System.err.println("Error procesando la respuesta JSON: " + e.getMessage());
                        e.printStackTrace();
                    }
                })
                .exceptionally(throwable -> {
                    System.err.println("Error en acceso a BBDD: " + throwable.getMessage());
                    return null;
                });
    }

    // funcion auxiliar
    private VBox getPedidoContainer(String mesa) {
        switch (mesa) {
            case "Mesa1":
                return pedidoContainer1;
            case "Mesa2":
                return pedidoContainer2;
            case "Mesa3":
                return pedidoContainer3;
            case "Mesa4":
                return pedidoContainer4;
            case "Mesa5":
                return pedidoContainer5;
            default:
                return null;
        }
    }

    // cancelar pedido de usuario
    @FXML
    public void cancelarPedidoAmesa() {
        // determinar que mesa esta activa por pestaña seleccionada
        Tab selectedTab = mainTabPane.getSelectionModel().getSelectedItem();
        if (selectedTab != null) {
            String tabText = selectedTab.getText();
            String sender = "";

            switch (tabText) {
                case "Mesa 1":
                    sender = "Mesa1";
                    break;
                case "Mesa 2":
                    sender = "Mesa2";
                    break;
                case "Mesa 3":
                    sender = "Mesa3";
                    break;
                case "Mesa 4":
                    sender = "Mesa4";
                    break;
                case "Mesa 5":
                    sender = "Mesa5";
                    break;
            }

            if (!sender.isEmpty()) {
                eliminarPedidos(getListaIdsMongo(sender), sender);
            }
        }
    }

    // enviar pedido a usuario
    @FXML
    public void admitirPedidoAmesa() {
        Tab selectedTab = mainTabPane.getSelectionModel().getSelectedItem();
        if (selectedTab != null) {
            String tabText = selectedTab.getText();
            String sender = "";

            switch (tabText) {
                case "Mesa 1":
                    sender = "Mesa1";
                    break;
                case "Mesa 2":
                    sender = "Mesa2";
                    break;
                case "Mesa 3":
                    sender = "Mesa3";
                    break;
                case "Mesa 4":
                    sender = "Mesa4";
                    break;
                case "Mesa 5":
                    sender = "Mesa5";
                    break;
            }

            if (!sender.isEmpty()) {
                cambiarEstadoApedidos(getListaIdsMongo(sender), sender);
            }
        }
    }

    // funcion auxiliar
    private ArrayList<String> getListaIdsMongo(String mesa) {
        switch (mesa) {
            case "Mesa1":
                return listaIdsMongo1;
            case "Mesa2":
                return listaIdsMongo2;
            case "Mesa3":
                return listaIdsMongo3;
            case "Mesa4":
                return listaIdsMongo4;
            case "Mesa5":
                return listaIdsMongo5;
            default:
                return new ArrayList<>();
        }
    }


    // cambia estado a array de pedidos de mesa determinada
    public void cambiarEstadoApedidos(ArrayList<String> listaIdsMongo, String mesa) {
        if (listaIdsMongo == null || listaIdsMongo.isEmpty()) {

            System.out.println("No hay pedidos para actualizar en " + mesa);
            return;
        }
        for (String mongoId : listaIdsMongo) {
            apiClient.cambiarEstadoPedido(mongoId, mesa, true)
                    .thenAccept(json -> {
                        try {
                            var jsonObject = json.getAsJsonObject();
                            String resultType = jsonObject.get("type").getAsString();
                            boolean success = "success".equals(resultType);
                            String message = jsonObject.has("message") ? jsonObject.get("message").getAsString() : "Sin mensaje";

                            if (success) {
                                // ocultar botones después de enviar
                                int numeroMesa = obtenerNumeroMesa(mesa);
                                Platform.runLater(() -> {
                                    ocultarBotonesPedido(numeroMesa);
                                });

                            } else {
                                System.out.println("Error al actualizar pedido: " + mongoId + " - " + message);
                            }
                        } catch (Exception e) {
                            System.err.println("Error procesando respuesta de actualizacion: " + e.getMessage());
                            e.printStackTrace();
                        }
                    })
                    .exceptionally(throwable -> {
                        System.err.println("Error en la llamada PATCH para " + mongoId + ": " + throwable.getMessage());
                        return null;
                    });
        }

        // limpieza y notificacion
        limpiarListaIdsMongo(mesa);
        int numMesaFinal = obtenerNumeroMesa(mesa);
        limpiarPedidoContainer(numMesaFinal);
        TextFlow taMensaje = getMensaje(numMesaFinal);
        String texto = "Pedido enviado a mesa " + mesa + "\n";
        appendMensajeConColor(taMensaje, "Restaurante: " + texto, COLOR_PROPIO);

        // IMPORTANTE, envia mensaje de confirmacion de pedido al usuario
        enviarMensajesWebSocketPedidoConfirmado(mesa);

    }

    // aqui elimina los pedidos que se han solicitado pero que se cancelan
    public void eliminarPedidos(ArrayList<String> listaIdsMongo, String mesa) {
        if (listaIdsMongo == null || listaIdsMongo.isEmpty()) {
            return;
        }
        for (String mongoId : listaIdsMongo) {
            apiClient.eliminarPedido(mongoId, mesa)
                    .thenAccept(json -> {
                        try {
                            var jsonObject = json.getAsJsonObject();
                            String resultType = jsonObject.get("type").getAsString();
                            boolean success = "success".equals(resultType);
                            String message = jsonObject.has("message") ? jsonObject.get("message").getAsString() : "Sin mensaje";

                            if (success) {

                                // ocultar botones después de eliminar
                                int numeroMesa = obtenerNumeroMesa(mesa);
                                Platform.runLater(() -> {
                                    ocultarBotonesPedido(numeroMesa);
                                });

                            } else {
                                System.out.println("Error al eliminar pedido: " + mongoId + " - " + message);
                            }
                        } catch (Exception e) {
                            System.err.println("Error procesando respuesta de eliminacion: " + e.getMessage());
                            e.printStackTrace();
                        }
                    })
                    .exceptionally(throwable -> {
                        System.err.println("Error en la llamada DELETE para " + mongoId + ": " + throwable.getMessage());
                        return null;
                    });
        }

        // limpieza y notificacion
        limpiarListaIdsMongo(mesa);
        int numMesaFinal = obtenerNumeroMesa(mesa);
        limpiarPedidoContainer(numMesaFinal);

        TextFlow taMensaje = getMensaje(numMesaFinal);
        String texto = "Pedido cancelado para mesa " + mesa + "\n";
        appendMensajeConColor(taMensaje, "Restaurante: " + texto, COLOR_PROPIO);

        // IMPORTANTE, notificacion al usuario de cancelacion
        enviarMensajeWebSocketPedidoCancelado(mesa);
    }

    // funcion auxiliar de comunicacion
    private void enviarMensajeWebSocketPedidoCancelado(String mesa) {
        String mensajeChat = "Pedido cancelado para " + mesa;
        CommsManager.getInstance().mainAwebSocket(mensajeChat, mesa);
        CommsManager.getInstance().mainAwebSocketPedidoCanceladoAmesa(mesa);
    }

    // funcion auxiliar de comunicacion
    private void enviarMensajesWebSocketPedidoConfirmado(String mesa) {
        String mensajeChat = "Pedido enviado a " + mesa;
        CommsManager.getInstance().mainAwebSocket(mensajeChat, mesa);
        CommsManager.getInstance().mainAwebSocketPedidoEnviadoAmesa(mesa);
    }

    // funcion auxiliar
    private int obtenerNumeroMesa(String mesa) {
        switch (mesa) {
            case "Mesa1":
                return 1;
            case "Mesa2":
                return 2;
            case "Mesa3":
                return 3;
            case "Mesa4":
                return 4;
            case "Mesa5":
                return 5;
            default:
                return 0;
        }
    }

    // funcion auxiliar
    private void limpiarListaIdsMongo(String mesa) {
        ArrayList<String> lista = getListaIdsMongo(mesa);
        if (lista != null) {
            lista.clear();
        }
    }

    // funcion auxiliar
    public void limpiarPedidoContainer(int numeroMesa) {
        Platform.runLater(() -> {
            VBox container = getPedidoContainer("Mesa" + numeroMesa);
            if (container != null) {
                container.getChildren().clear();

                // limpia lista de mongoIds correspondiente
                ArrayList<String> listaIds = getListaIdsMongo("Mesa" + numeroMesa);
                if (listaIds != null) {
                    listaIds.clear();
                }
                // ocultar botones
                ocultarBotonesPedido(numeroMesa);
            }
        });
    }


    // funcion auxiliar de pedido
    public void manejarPedido(WebSocketController.Message mensaje) {

        String sender = mensaje.sender;

        int numeroMesa = 0;
        switch (sender) {
            case "Mesa1":
                numeroMesa = 1;
                break;
            case "Mesa2":
                numeroMesa = 2;
                break;
            case "Mesa3":
                numeroMesa = 3;
                break;
            case "Mesa4":
                numeroMesa = 4;
                break;
            case "Mesa5":
                numeroMesa = 5;
                break;
        }

        leerPedidoMesaDeBBDD(apiClient, sender);
        mostrarBotonesPedido(numeroMesa);

    }

    // funcion auxiliar de disenho
    private void adjustTabWidths() {
        double tabPaneWidth = mainTabPane.getWidth();
        int tabCount = mainTabPane.getTabs().size();

        if (tabCount > 0 && tabPaneWidth > 0) {
            double tabWidth = (tabPaneWidth / tabCount) - 2;
            mainTabPane.setTabMinWidth(tabWidth);
            mainTabPane.setTabMaxWidth(tabWidth);
        }
    }

    // funcion auxiliar
    public void mostrarBotonesPedido(int mesaNumero) {
        Button cancelarBtn = getCancelarButton(mesaNumero);
        Button enviarBtn = getEnviarButton(mesaNumero);

        if (cancelarBtn != null && enviarBtn != null) {
            cancelarBtn.setVisible(true);
            enviarBtn.setVisible(true);
        }
    }

    // funcion auxiliar
    public void ocultarBotonesPedido(int mesaNumero) {
        Button cancelarBtn = getCancelarButton(mesaNumero);
        Button enviarBtn = getEnviarButton(mesaNumero);

        if (cancelarBtn != null && enviarBtn != null) {
            cancelarBtn.setVisible(false);
            enviarBtn.setVisible(false);
        }
    }

    // funcion auxiliar
    private Button getCancelarButton(int mesaNumero) {
        switch (mesaNumero) {
            case 1:
                return cancelarPedidoBtn1;
            case 2:
                return cancelarPedidoBtn2;
            case 3:
                return cancelarPedidoBtn3;
            case 4:
                return cancelarPedidoBtn4;
            case 5:
                return cancelarPedidoBtn5;
            default:
                return null;
        }
    }


    // funcion auxiliar
    private Button getEnviarButton(int mesaNumero) {
        switch (mesaNumero) {
            case 1:
                return enviarPedidoBtn1;
            case 2:
                return enviarPedidoBtn2;
            case 3:
                return enviarPedidoBtn3;
            case 4:
                return enviarPedidoBtn4;
            case 5:
                return enviarPedidoBtn5;

            default:
                return null;
        }
    }

    //  para controlar el volumen de notificaciones
    public double getVolumenNotificacion() {
        WebSocketController wsController = CommsManager.getInstance().getWebSocketController();
        if (wsController != null) {
            return wsController.getNotificacionVolumen();
        }
        return 1.0; // por defecto si no esta disponible
    }

    public void setVolumenNotificacion(double volumen) {
        WebSocketController wsController = CommsManager.getInstance().getWebSocketController();
        if (wsController != null) {
            wsController.setNotificacionVolumen(volumen);
        }
    }

    public void cambiarVolumenNotificacion(double nuevoVolumen) {
        setVolumenNotificacion(nuevoVolumen);
    }

    // control de volumen
    private void setupControlVolumen() {
        if (volumenSlider == null) return;

        // volumen inicial desde WebSocketController
        double volumenInicial = getVolumenNotificacion();
        volumenSlider.setValue(volumenInicial);

        // listener para cambios en slider
        volumenSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            double volumen = newValue.doubleValue();
            cambiarVolumenNotificacion(volumen);
        });
    }

    // setter para TextArea lastPedido en barra inferior
    public void setLastPedido(String texto) {
        if (lastPedido != null) {
            Platform.runLater(() -> {
                lastPedido.setText(texto);
            });
        }
    }
}