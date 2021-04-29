import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.Callable;

public class AuthController {
    @FXML
    private TextField loginTF;
    @FXML
    private PasswordField passwordTF;
    private DataInputStream in;
    private DataOutputStream out;

    @FXML
    private void initialize() throws IOException {
        Socket socket = ServerConnection.getSocket();
        in = new DataInputStream(socket.getInputStream());
        out = new DataOutputStream(socket.getOutputStream());
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                try {
                    while (true) {
                        System.out.println("Ждем ответ от сервера");
                        String strFromServer = in.readUTF();
                        if (strFromServer.startsWith("/authok")) {
                            Stage stage = (Stage) loginTF.getScene().getWindow();
                            stage.close();
                            break;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

//        new Thread(() -> {
//            try {
//                while (true) {
//                    String strFromServer = in.readUTF();
//                    if (strFromServer.startsWith("/authok")) {
//                        Stage stage = (Stage) loginTF.getScene().getWindow();
//                        stage.close();
//                        break;
//                    }
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }).start();
    }

    @FXML
    private void auth() throws IOException {
        String authString = "/auth " + loginTF.getText() + " " + passwordTF.getText();
        out.writeUTF(authString);
    }
}