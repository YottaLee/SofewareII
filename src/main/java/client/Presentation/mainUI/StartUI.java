package client.Presentation.mainUI;

import client.BL.LodinblService.LoginController;
import client.Presentation.AccountantUI.AccountMain.AccountantMain;
import client.Presentation.AdminUI.SetUI;
import client.Presentation.ManageUI.MainManageUI;
import client.Presentation.SalesmanUI.BillMake.newBillUI;
import client.Presentation.StockmanUI.stockmanMainUI.stockmanMainUI;
import client.RMI.link;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.bytedeco.javacpp.opencv_core;
import org.bytedeco.javacv.CanvasFrame;
import org.bytedeco.javacv.OpenCVFrameConverter;
import org.bytedeco.javacv.OpenCVFrameGrabber;
import server.Po.userPO;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.List;

/**
 * Created by Leonarda on 2017/12/5.
 */
public class StartUI extends Application {


    public static void main(String[] args) {
        link.linktoServer();
        launch(args);
    }

    private LoginController controller = new LoginController();

    public void start(Stage stage) throws RemoteException {


        VBox vBox = new VBox();

        Scene scene;
        stage.setTitle("登陆");
        stage.setWidth(500);
        stage.setHeight(657);


        HBox hbButtons = new HBox();
        hbButtons.setSpacing(10);

        Button btn1 = new Button("登录");
        Button btn2 = new Button("面部登陆");
        ComboBox<String> tfName = new ComboBox<>();
        tfName.setEditable(true);
        tfName.setPromptText("用户名");
        PasswordField pfPwd = new PasswordField();
        pfPwd.setPromptText("密码");

        tfName.setMinWidth(400);
        pfPwd.setMinWidth(400);
        tfName.setStyle("-fx-prompt-text-fill: darkgray;-fx-border-color: transparent;-fx-font-size: 35;-fx-background-color: transparent");
        pfPwd.setStyle("-fx-prompt-text-fill: darkgray;-fx-border-color: transparent;-fx-font-size: 35;-fx-background-color: transparent");

        List<userPO> userPOS = link.getRemoteHelper().getUser().findAll(15);
        for (userPO userPO : userPOS) {
            String id = userPO.getKeyname();
            tfName.getItems().add(id);
        }

        tfName.setOnAction(e -> {
            String password = null;
            try {
                password = link.getRemoteHelper().getUser().getpasswordByName(tfName.getValue());
            } catch (RemoteException e1) {
                e1.printStackTrace();
            }
            pfPwd.setText(password);
        });
        btn1.setStyle("-fx-text-fill: #a9a6a5;-fx-font: 40;-fx-background-color: #e4e9ee");
        btn2.setStyle("-fx-text-fill: #a9a6a5;-fx-font: 40;-fx-background-color: #e4e9ee");
        btn1.setOnAction((ActionEvent e) -> {
            String username = tfName.getValue();
            String password = pfPwd.getText();
            try {
                List<userPO> userPOList;
                userPOList = controller.getAlluser(username, password);

                if (userPOList.size() == 1) {
                    userPO thisPO = userPOList.get(0);
                    login(thisPO,stage);
                } else {
                    pfPwd.clear();
                    Stage failstage = new Stage();
                    Scene failscene = new Scene(new Group());
                    GridPane failpane = new GridPane();
                    Label label = new Label("未查询到该账户!");
                    failpane.getChildren().add(label);
                    ((Group) failscene.getRoot()).getChildren().add(failpane);
                    failstage.setScene(failscene);
                    failstage.setWidth(200);
                    failstage.setHeight(90);
                    failstage.setTitle("登录失败");
                    failstage.show();
                }
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        });

        btn2.setOnAction(e -> {
            stage.setIconified(true);
            try {
                Facelogin();
                stage.close();
            } catch (InterruptedException | IOException e1) {
                e1.printStackTrace();
            }
        });
        hbButtons.getChildren().addAll(btn1);
        hbButtons.setAlignment(Pos.CENTER);


        vBox.getChildren().addAll(tfName, pfPwd, btn1,btn2);

        tfName.setMaxSize(150, 20);
        pfPwd.setMaxSize(150, 20);

        vBox.setSpacing(5);
        vBox.setPadding(new Insets(10, 0, 0, 10));
        vBox.setAlignment(Pos.CENTER);
        vBox.setStyle("-fx-background-image: url(timg.jpeg)");
        scene = new Scene(vBox, 400, 500);
        stage.setResizable(false);
        stage.setScene(scene);
        stage.initStyle(StageStyle.UNDECORATED);
        stage.show();
    }

    public void login(userPO thisPO,Stage stage) throws Exception {
        HBox hBox = null;
        switch (thisPO.getKeyjob()) {
            case "stockman":
                stockmanMainUI stockmanMainUI = new stockmanMainUI();
                hBox = stockmanMainUI.start(thisPO);
                break;
            case "accountant":
                AccountantMain accountantMain = new AccountantMain();
                hBox = accountantMain.start(thisPO);
                break;
            case "saleman":
                newBillUI newBillUI = new newBillUI();
                hBox = newBillUI.start(thisPO);
                break;
            case "manager":
                MainManageUI mainManageUI = new MainManageUI();
                hBox = mainManageUI.start(thisPO);
                break;
            case "admin":
                SetUI setUI = new SetUI();
                hBox = setUI.start(thisPO);
                break;
        }

        assert hBox != null;
        Scene scene1 = new Scene(hBox);
        stage.setResizable(true);
        stage.setMaximized(true);
        stage.setScene(scene1);
    }

    private void Facelogin() throws IOException, InterruptedException {

        OpenCVFrameGrabber grabber = new OpenCVFrameGrabber(1);
        grabber.setImageWidth(500);
        grabber.setImageHeight(660);
        grabber.start();


        OpenCVFrameConverter.ToIplImage converter = new OpenCVFrameConverter.ToIplImage();
        opencv_core.IplImage grabbedImage = converter.convertToIplImage(grabber.grab());
        CanvasFrame frame = new CanvasFrame("Softer", CanvasFrame.getDefaultGamma() / grabber.getGamma());

        JPanel contentPane = new JPanel();
        Container contentPane2 = frame.getContentPane();


        JButton save_photo = new JButton("登录");
        JButton cancle = new JButton("关闭");
        Camera camera = new Camera();

        save_photo.addMouseListener(new SavePhotoMouseAdapter(grabbedImage));

        cancle.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent arg0) {
                frame.dispose();
                frame.setVisible(false);
            }
        });


        contentPane.add(save_photo, BorderLayout.SOUTH);

        contentPane.add(cancle, BorderLayout.SOUTH);

        contentPane2.add(contentPane, BorderLayout.SOUTH);

        while (frame.isVisible()) {


            if (camera.getState()) {
                grabbedImage = converter.convert(grabber.grab());
            }
            frame.showImage(converter.convert(grabbedImage));
            Thread.sleep(40);
        }

        grabber.stop();
        frame.dispose();


    }


}


