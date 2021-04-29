import com.sun.istack.internal.NotNull;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

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
        //TODO Callable следующая ступень развития Runnable. Позволяет вернуть результаты вычислений, а так-же пробросить ошибку, в случае возникновения.
        // Запускается с помощью ThreadPoolExecutor, а не через создание новой Thread
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while (true) {
                        String strFromServer = in.readUTF();
                        //   /authok nick1
                        if (strFromServer.startsWith("/authok")) {
                            Config.nick = strFromServer.split(" ")[1];
                            //TODO работу с объектами fx запускаем через эту конструкцию
                            Platform.runLater(() -> {
                                Stage stage = (Stage) loginTF.getScene().getWindow();
                                stage.close();
                            });
                            break;

                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @FXML
    private void auth() throws IOException {
        String authString = "/auth " + loginTF.getText() + " " + passwordTF.getText();
        out.writeUTF(authString);
    }
}
