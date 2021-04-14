package bsu.rfact.java.lab7;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainFrame extends JFrame {
    private static final String FRAME_TITLE = "Instant messaging client";

    private static final int FRAME_MINIMUM_WIDTH = 500;
    private static final int FRAME_MINIMUM_HEIGHT = 500;

    private static final int FROM_FIELD_DEFAULT_COLUMNS = 10;
    private static final int TO_FIELD_DEFAULT_COLUMNS = 20;
    private static final int INCOMING_AREA_DEFAULT_ROWS = 10;
    private static final int OUTGOING_AREA_DEFAULT_ROWS = 5;

    private static final int SMALL_GAP = 5;
    private static final int MEDIUM_GAP = 10;
    private static final int LARGE_GAP = 15;
    private static final int SERVER_PORT = 4567;

    private final JTextField textFieldFrom;
    private final JTextField textFieldTo;
    private final JTextField login;

    private final JTextArea textAreaIncoming;
    private final JTextArea textAreaOutgoing;
    private String date;
    private boolean flagPrivate;
    private DialogFrame dialogFrame;
    private ChatDataBase listOfUsers;

    public MainFrame() {
        super(FRAME_TITLE);
        setMinimumSize(new Dimension(FRAME_MINIMUM_WIDTH, FRAME_MINIMUM_HEIGHT));

        final Toolkit kit = Toolkit.getDefaultToolkit();
        setLocation((kit.getScreenSize().width - getWidth()) / 2,
                (kit.getScreenSize().height - getHeight()) / 2);

        textAreaIncoming = new JTextArea(INCOMING_AREA_DEFAULT_ROWS, 0);
        textAreaIncoming.setEditable(false);

        final JScrollPane scrollPaneIncoming = new JScrollPane(textAreaIncoming);

        final JLabel labelFrom = new JLabel("Sender");
        final JLabel labelTo = new JLabel("Recipient");

        textFieldFrom = new JTextField(FROM_FIELD_DEFAULT_COLUMNS);
        textFieldTo = new JTextField(TO_FIELD_DEFAULT_COLUMNS);

        textAreaOutgoing = new JTextArea(OUTGOING_AREA_DEFAULT_ROWS, 0);

        final JScrollPane scrollPaneOutgoing = new JScrollPane(textAreaOutgoing);

        final JPanel messagePanel = new JPanel();
        messagePanel.setBorder(BorderFactory.createTitledBorder("Message"));

        listOfUsers = new ChatDataBase();
        flagPrivate = false;

        login = new JTextField(20);
        while(true) {
            JOptionPane.showMessageDialog(MainFrame.this, login,
                    "Enter your login", JOptionPane.PLAIN_MESSAGE);
            if (login.getText().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Enter login!",
                        "Error", JOptionPane.ERROR_MESSAGE);
                login.grabFocus();
            }
            else {
                textFieldFrom.setText(login.getText());
                break;
            }
        }

        final JButton sendButton = new JButton("Send message");

        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                date = getDateTime();
                sendMessage();
            }
        });

        final JButton listButton = new JButton("List of users");
        listButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Box listBox = Box.createVerticalBox();
                listBox.add(Box.createVerticalGlue());
                for(User user: listOfUsers.getUsers())
                {
                    JLabel name = new JLabel(user.getName());
                    JLabel IP = new JLabel(user.getAddress());
                    Box oneUser=Box.createHorizontalBox();
                    oneUser.add(Box.createHorizontalGlue());
                    oneUser.add(name);
                    oneUser.add(Box.createHorizontalStrut(40));
                    oneUser.add(IP);
                    oneUser.add(Box.createHorizontalGlue());
                    listBox.add(oneUser);
                    listBox.add(Box.createVerticalStrut(20));
                }
                listBox.add(Box.createVerticalGlue());
                JOptionPane.showMessageDialog(MainFrame.this, listBox,
                        "List of users", JOptionPane.INFORMATION_MESSAGE);
            }
        });


        JTextField name = new JTextField(10);
        JTextField address = new JTextField(10);
        final JButton plusUser = new JButton("+");
        plusUser.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JLabel inputName = new JLabel("Login: ");
                JLabel inputAddress = new JLabel("IP: ");

                Box register = Box.createVerticalBox();
                register.add(Box.createVerticalGlue());
                Box first = Box.createHorizontalBox();
                first.add(Box.createHorizontalGlue());
                first.add(inputName);
                first.add(Box.createHorizontalStrut(10));
                first.add(name);
                first.add(Box.createHorizontalGlue());
                Box second = Box.createHorizontalBox();
                second.add(Box.createHorizontalGlue());
                second.add(inputAddress);
                second.add(Box.createHorizontalStrut(10));
                second.add(address);
                second.add(Box.createHorizontalGlue());
                register.add(first);
                register.add(Box.createVerticalStrut(10));
                register.add(second);
                register.add(Box.createVerticalGlue());
                JOptionPane.showMessageDialog(MainFrame.this, register,
                        "User registration", JOptionPane.INFORMATION_MESSAGE);

            }
        });
        if(!name.getText().isEmpty() && !address.getText().isEmpty()) {
            listOfUsers = listOfUsers.plusUser(name.getText(),address.getText());
        }


        final JButton searchButton = new JButton("Search");
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                JLabel who = new JLabel("Searching for");
                JTextField searchName = new JTextField(10);
                Box search = Box.createHorizontalBox();
                search.add(Box.createHorizontalGlue());
                search.add(who);
                search.add(Box.createHorizontalStrut(10));
                search.add(searchName);
                search.add(Box.createHorizontalGlue());
                JOptionPane.showMessageDialog(MainFrame.this, search,
                        "Search", JOptionPane.QUESTION_MESSAGE);
                String nameToSearch = searchName.getText();
                boolean flag = false;
                for (User user : listOfUsers.getUsers())
                {
                    if(user.getName().equals(nameToSearch)) {
                        JFrame resultFrame = new JFrame("User " + user.getName() + " find");
                        resultFrame.setSize(400,200);
                        resultFrame.setLocation((kit.getScreenSize().width - resultFrame.getWidth()) / 2,
                                (kit.getScreenSize().height - resultFrame.getHeight()) / 2);

                        flag = true;
                        JLabel question = new JLabel("Do you want to start a dialogue with him?");
                        JButton yesButton = new JButton("Yes");

                        yesButton.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent actionEvent) {
                                dialogFrame = new DialogFrame(user, MainFrame.this);
                                flagPrivate=true;
                                resultFrame.setVisible(false);
                            }
                        });

                        JButton noButton = new JButton("No");
                        noButton.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent actionEvent) {
                                resultFrame.setVisible(false);
                            }
                        });
                        Box result = Box.createVerticalBox();
                        result.add(Box.createVerticalGlue());
                        result.add(question);
                        result.add(Box.createVerticalStrut(20));
                        Box answer = Box.createHorizontalBox();
                        answer.add(Box.createHorizontalGlue());
                        answer.add(yesButton);
                        answer.add(Box.createHorizontalStrut(10));
                        answer.add(noButton);
                        answer.add(Box.createHorizontalGlue());
                        result.add(answer);
                        result.add(Box.createVerticalGlue());
                        resultFrame.add(result);
                        resultFrame.setVisible(true);
                    }
                }
                if(!flag){
                    JLabel notFound= new JLabel("No such user found");
                    JOptionPane.showMessageDialog(MainFrame.this, notFound,
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });


        final GroupLayout layout2 = new GroupLayout(messagePanel);
        messagePanel.setLayout(layout2);
        layout2.setHorizontalGroup(layout2.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout2.createParallelGroup(GroupLayout.Alignment.TRAILING)
                        .addGroup(layout2
                                .createSequentialGroup()
                                .addComponent(labelFrom)
                                .addGap(SMALL_GAP)
                                .addComponent(textFieldFrom)
                                .addGap(LARGE_GAP)
                                .addComponent(labelTo)
                                .addGap(SMALL_GAP)
                                .addComponent(textFieldTo))
                        .addComponent(scrollPaneOutgoing)
                        .addGroup(layout2
                                .createSequentialGroup()
                                .addComponent(plusUser)
                                .addGap(LARGE_GAP)
                                .addComponent(listButton)
                                .addGap(LARGE_GAP)
                                .addComponent(searchButton)
                                .addGap(LARGE_GAP)
                                .addComponent(sendButton)))
                .addContainerGap());

        layout2.setVerticalGroup(layout2.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout2
                        .createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(labelFrom)
                        .addComponent(textFieldFrom)
                        .addComponent(labelTo)
                        .addComponent(textFieldTo))
                .addGap(MEDIUM_GAP)
                .addComponent(scrollPaneOutgoing)
                .addGap(MEDIUM_GAP)
                .addGroup(layout2
                        .createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(plusUser)
                        .addComponent(listButton)
                        .addComponent(searchButton)
                        .addComponent(sendButton))
                .addContainerGap());

        final GroupLayout layout1 = new GroupLayout(getContentPane());
        setLayout(layout1);
        layout1.setHorizontalGroup(layout1.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout1.createParallelGroup()
                        .addComponent(scrollPaneIncoming)
                        .addComponent(messagePanel))
                .addContainerGap());

        layout1.setVerticalGroup(layout1.createSequentialGroup()
                .addContainerGap()
                .addComponent(scrollPaneIncoming)
                .addGap(MEDIUM_GAP)
                .addComponent(messagePanel)
                .addContainerGap());

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    final ServerSocket serverSocket = new ServerSocket(SERVER_PORT);
                    while (!Thread.interrupted()) {
                        final Socket socket = serverSocket.accept();
                        final DataInputStream in = new DataInputStream(socket.getInputStream());
                        final String senderName = in.readUTF();
                        final String message = in.readUTF();
                        final String Dat = in.readUTF();
                        socket.close();

                        final String address = ((InetSocketAddress) socket
                                .getRemoteSocketAddress()).getAddress().getHostAddress();

                        if(flagPrivate && !dialogFrame.isVisible()) {
                            flagPrivate = false;
                        }
                        boolean flag = false;
                        for (User user : listOfUsers.getUsers()) {
                            if (user.getAddress().equals(textFieldTo.getText()) && !flagPrivate ) {
                                textAreaIncoming.append(Dat + "  " + senderName + " -> " + user.getName() + " : " + message + "\n");
                                flag = true;
                            }
                        }
                        if (!flag && !flagPrivate) {
                            textAreaIncoming.append(Dat + "  " + senderName + " -> Unknown (" + textFieldTo.getText() + ") : " + message + "\n");
                        }

                    }

                } catch (IOException e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(MainFrame.this, "Server error",
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }).start();
    }

    public JTextField getLogin() { return login; }
    public static int getServerPort() { return SERVER_PORT; }

    public String getDateTime() {

        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        return dateFormat.format(date);

    }
}
