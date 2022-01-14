package com.yl;

import io.xjar.XConstants;
import io.xjar.XKit;
import io.xjar.boot.XBoot;
import io.xjar.key.XKey;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author ：jerry
 * @date ：Created in 2021/12/31 10:29
 * @description：Xjar启动函数
 * @version: V1.1
 */
@Slf4j
public class XjarMain extends Application {

    /**3
     * 启动函数
     * jar -uvfe  xjar_fx.jar com.yl.XjarMain          赋权
     * java -jar xjar_fx.jar com.yl.XjarMain           启动
     */
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        /*--------------------1。页面布局--------------------------*/

        AnchorPane pane = new AnchorPane();         //面板
        //选择要加密的文件的title
        Label fromLabel = new Label("选择需要加密的jar:");          //标签，标签要放在布局里面
        fromLabel.setLayoutY(50);
        fromLabel.setLayoutX(10);
        pane.getChildren().add(fromLabel);

        //选择要加密的文件的输入框
        TextField fromText = new TextField("请选择需要加密的jar文件路径");
        fromText.setLayoutX(140);
        fromText.setLayoutY(40);
        pane.getChildren().add(fromText);

        //选择按钮1
        Button fromButton = new Button("选择...");
        fromButton.setLayoutX(340);
        fromButton.setLayoutY(40);
        pane.getChildren().add(fromButton);

        //保存位置的title
        Label toLabel = new Label("选择需要保存的位置:");          //标签，标签要放在布局里面
        toLabel.setLayoutY(100);
        toLabel.setLayoutX(10);
        pane.getChildren().add(toLabel);

        //保存位置的输入框
        TextField toText = new TextField("请选择需要保存的位置");
        toText.setLayoutX(140);
        toText.setLayoutY(100);
        pane.getChildren().add(toText);

        //选择按钮2
        Button toButton = new Button("选择...");
        toButton.setLayoutX(340);
        toButton.setLayoutY(100);
        pane.getChildren().add(toButton);

        //保存位置的title
        Label pwdLabel = new Label("加密密码:");          //标签，标签要放在布局里面
        pwdLabel.setLayoutX(10);
        pwdLabel.setLayoutY(150);
        pane.getChildren().add(pwdLabel);

        //保存位置的输入框
        PasswordField passwordText = new PasswordField();
//        TextField passwordText = new TextField("请输入密码");
        passwordText.setLayoutX(140);
        passwordText.setLayoutY(140);
        pane.getChildren().add(passwordText);

        //选择按钮3
        Button startButton = new Button("开始");
        startButton.setLayoutX(10);
        startButton.setLayoutY(190);
        pane.getChildren().add(startButton);

        //日志显示框
        TextArea textArea = new TextArea();
        textArea.setLayoutX(10);
        textArea.setLayoutY(250);
        pane.getChildren().add(textArea);

        /*--------------------2。业务部分的逻辑--------------------------*/
        /**
         * 选择jar按钮监听事件
         */
        fromButton.setOnAction(e -> {
            FileChooser chooser = new FileChooser(); //设置选择器
            String filepath = chooser.showOpenDialog(null).getAbsolutePath();//获取绝对路径

            if (!".jar".equals(filepath.substring(filepath.length() - 4))) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("文件格式错误");
                alert.setHeaderText("文件格式不正确，请选择jar文件！"); //设置对话框窗口里的标头文本。若设为空字符串，则表示无标头
                alert.showAndWait(); //显示对话框，并等待对话框被关闭时才继续运行之后的程序
            } else {
                fromText.setText(filepath);
            }

        });

        /**
         * 选择加密后jar保存路径按钮监听事件
         */
        toButton.setOnAction(e -> {
            FileChooser chooser = new FileChooser(); //设置选择器
            DirectoryChooser directoryChooser=new DirectoryChooser();
            File file = directoryChooser.showDialog(stage);
            String filepath = file.getAbsolutePath();//只获取目录
//             String filepath = file.getParent();     //只获取目录
            chooser.setTitle("选择加密后jar保存位置");
            toText.setText(filepath);

        });

        /**
         * 选择开始径按钮的监听事件
         */
        startButton.setOnAction(e -> {
            String fromJarPath = fromText.getText();
            String toJarPath = toText.getText() + "\\encrypt" + getNowDateTime() + ".jar";
            String password = new String(passwordText.getText());
            System.out.println("fromJarPath=" + fromJarPath);
            System.out.println("toJarPath=" + toJarPath);
            System.out.println("password=" + password);
            if (fromJarPath != null && !"".equals(fromJarPath)) {
                if (toJarPath != null && !"\\encrypt".equals(toJarPath.substring(0,8))) {
                    if (password != null && !"".equals(password)) {
                        //打印输入日志
                        StringBuilder builder = new StringBuilder();
                        builder.append("fromJarPath=" + fromJarPath + "\n")
                                .append("toJarPath=" + toJarPath + "\n") ;
                        textArea.setText(builder.toString());
                        //开始加密文件
                        encryptJarDangerMode(fromJarPath,toJarPath,password);
                        textArea.appendText("jar加密成功！\n请测试接口是否正常（注意：Swagger不可用）");
                    } else {
                        JOptionPane.showMessageDialog(null, "请输入加密的密码！", "密码不能为空！", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "保存路径不能为空!", "保存路径不能为空！", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(null, "jar文件不能为空！", "jar文件不能为空！", JOptionPane.ERROR_MESSAGE);
            }
        });


        Scene scene = new Scene(pane, 600, 500);      //创建一个场景，布局放在场景里面
        stage.setScene(scene);      //场景设置到窗体里面
        stage.setTitle("Jar加密，防止反编译");         //给窗体设置标题
        stage.show();

        //总结：组建放布局，布局放场景，场景放窗口
    }

    /**
     * 获取当前格式化时间的方法
     * @return
     */
    private static String getNowDateTime() {
        String dateNow = "";
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
        Date date = new Date();
        dateNow = format.format(date);
        return dateNow;
    }
    /**
     * jar包危险加密模式
     * 即不需要输入密码即可启动的加密方式，这种方式META-INF/MANIFEST.MF中会保留密钥，请谨慎使用！
     * @param fromJarPath 需要加密的jar
     * @param toJarPath 加密后的jar
     * @param password 加密密码
     */
    public static void encryptJarDangerMode(String fromJarPath, String toJarPath, String password) {
        log.info("fromJarPath:-------->>>>>>>"+fromJarPath);
        log.info("toJarPath:-------->>>>>>>"+toJarPath);
        log.info("password:-------->>>>>>>"+password);
        try {
            XKey xKey = XKit.key(password);
            XBoot.encrypt(fromJarPath, toJarPath, xKey, XConstants.MODE_DANGER);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
