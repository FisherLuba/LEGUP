package edu.rpi.legup.ui;

import edu.rpi.legup.app.GameBoardFacade;
import edu.rpi.legup.controller.CursorController;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.URI;
import java.net.URL;

public class HomePanel extends LegupPanel {
    private final static Logger LOGGER = LogManager.getLogger(HomePanel.class.getName());
    private LegupUI legupUI;
    private JFrame frame;
    private JButton[] buttons;
    private JLabel[] text;
    private JMenuBar menuBar;

    private final int buttonSize = 100;

    private ActionListener openProofListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            Object[] items = legupUI.getProofEditor().promptPuzzle();
            String fileName = (String) items[0];
            File puzzleFile = (File) items[1];
            legupUI.displayPanel(1);
            legupUI.getProofEditor().loadPuzzle(fileName, puzzleFile);
        }
    };

    private ActionListener openPuzzleListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            Object[] items = legupUI.getPuzzleEditor().promptPuzzle();
            String fileName = (String) items[0];
            File puzzleFile = (File) items[1];
            legupUI.displayPanel(2);
            legupUI.getPuzzleEditor().loadPuzzle(fileName, puzzleFile);
        }
    };

    public HomePanel(FileDialog fileDialog, JFrame frame, LegupUI legupUI) {
        this.legupUI = legupUI;
        this.frame = frame;
        setLayout(new GridLayout(1, 2));
        initText();
        initButtons();
    }

    public JMenuBar getMenuBar() {
        this.menuBar = new JMenuBar();
        JMenu settings = new JMenu("Settings");
        menuBar.add(settings);
        JMenuItem preferences = new JMenuItem("Preferences");
        preferences.addActionListener(a -> {
            PreferencesDialog preferencesDialog = new PreferencesDialog(this.frame);
            System.out.println("Preferences clicked");
        });
        settings.addSeparator();
        settings.add(preferences);

        JMenuItem contribute = new JMenuItem("Contribute to Legup");
        contribute.addActionListener(l -> {
            try {
                java.awt.Desktop.getDesktop().browse(URI.create("https://github.com/Bram-Hub/Legup"));
            }
            catch (IOException e) {
                LOGGER.error("Can't open web page");
            }
        });
        settings.add(contribute);

        return this.menuBar;
    }

    @Override
    public void makeVisible() {
        render();
        frame.setJMenuBar(this.getMenuBar());
    }

    private static ImageIcon resizeButtonIcon(ImageIcon icon, int width, int height) {
        Image image = icon.getImage();
        Image resizedImage = image.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        return new ImageIcon(resizedImage);
    }

    private void initButtons() {
        this.buttons = new JButton[4];

        this.buttons[0] = new JButton("Solve Puzzle") {
            {
                setSize(buttonSize, buttonSize);
                setMaximumSize(getSize());
            }
        };

        URL button0IconLocation = ClassLoader.getSystemClassLoader().getResource("edu/rpi/legup/images/Legup/homepanel/proof_file.png");
        ImageIcon button0Icon = new ImageIcon(button0IconLocation);
        this.buttons[0].setIcon(resizeButtonIcon(button0Icon, this.buttonSize, this.buttonSize));
        this.buttons[0].setHorizontalTextPosition(AbstractButton.CENTER);
        this.buttons[0].setVerticalTextPosition(AbstractButton.BOTTOM);
        this.buttons[0].addActionListener(CursorController.createListener(this, openProofListener));

        this.buttons[1] = new JButton("Create Puzzle") {
            {
                setSize(buttonSize, buttonSize);
                setMaximumSize(getSize());
            }
        };
        URL button1IconLocation = ClassLoader.getSystemClassLoader().getResource("edu/rpi/legup/images/Legup/homepanel/new_puzzle_file.png");
        ImageIcon button1Icon = new ImageIcon(button1IconLocation);
        this.buttons[1].setIcon(resizeButtonIcon(button1Icon, this.buttonSize, this.buttonSize));
        this.buttons[1].setHorizontalTextPosition(AbstractButton.CENTER);
        this.buttons[1].setVerticalTextPosition(AbstractButton.BOTTOM);
        this.buttons[1].addActionListener(l -> this.openNewPuzzleDialog());

        this.buttons[2] = new JButton("Edit Puzzle") {
            {
                setSize(buttonSize, buttonSize);
                setMaximumSize(getSize());
            }
        };
        URL button2IconLocation = ClassLoader.getSystemClassLoader().getResource("edu/rpi/legup/images/Legup/homepanel/puzzle_file.png");
        ImageIcon button2Icon = new ImageIcon(button2IconLocation);
        this.buttons[2].setIcon(resizeButtonIcon(button2Icon, this.buttonSize, this.buttonSize));
        this.buttons[2].setHorizontalTextPosition(AbstractButton.CENTER);
        this.buttons[2].setVerticalTextPosition(AbstractButton.BOTTOM);
        this.buttons[2].addActionListener(CursorController.createListener(this, openPuzzleListener)); // PLACEHOLDER

        for (int i = 0; i < this.buttons.length - 1; i++) { // -1 to avoid the batch grader button
            //this.buttons[i].setPreferredSize(new Dimension(100, 100));
            this.buttons[i].setBounds(200, 200, 700, 700);
        }
        this.buttons[3] = new JButton("Batch Grader");
        this.buttons[3].setHorizontalTextPosition(AbstractButton.CENTER);
        this.buttons[3].setVerticalTextPosition(AbstractButton.BOTTOM);
    }

    /*
    This function is use to check each function xml proof file have the flag = true.
    Also, we will go to check each file is xml file or not which we have 3 result solve, unsolve and ungradeable
     */
    public void use_xml_to_check() throws Exception{
        GameBoardFacade facade = GameBoardFacade.getInstance();

        /*
         * Select dir to grade; recursively grade sub-dirs using traverseDir()
         * Selected dir must have sub-dirs for each student:
         * GradeThis
         *    |
         *    | -> Student 1
         *    |       |
         *    |       | -> Proofs
         */

        LegupPreferences preferences = LegupPreferences.getInstance();
        File preferredDirectory = new File(preferences.getUserPref(LegupPreferences.WORK_DIRECTORY));
        JFileChooser folderBrowser = new JFileChooser(preferredDirectory);


        folderBrowser.setCurrentDirectory(new File(LegupPreferences.WORK_DIRECTORY));
        folderBrowser.setDialogTitle("Select Directory");
        folderBrowser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        folderBrowser.setAcceptAllFileFilterUsed(false);
        folderBrowser.showOpenDialog(this);
        folderBrowser.setVisible(true);
        File folder = folderBrowser.getSelectedFile();

        File resultFile = new File(folder.getAbsolutePath() + File.separator +"result.csv");
        SAXParserFactory spf = SAXParserFactory.newInstance();
        SAXParser saxParser = spf.newSAXParser();
//        String path = "C:\\Users\\LiWeiJun\\Desktop\\TestSet\\TestSet\\roseh";
        //read the xml file
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(resultFile))) {
            writer.write("Name");
            writer.write(",");
            writer.write("File Name");
            writer.write(",");
            writer.write("Solved or not");
            writer.write("\n");
            for (final File folderEntry : folder.listFiles(File::isDirectory)) {
                writer.write(folderEntry.getName());
                writer.write(",");
                int count1 = 0;
                for (final File fileEntry : folderEntry.listFiles()) {
                    if (fileEntry.getName().charAt(0) == '.') {
                        continue;
                    }
                    count1++;
                    if (count1 > 1) {
                        writer.write(folderEntry.getName());
                        writer.write(",");
                    }
                    writer.write(fileEntry.getName());
                    writer.write(",");
                    String path =folderEntry.getAbsolutePath() + File.separator + fileEntry.getName();
                    System.out.println(path);
                    boolean is_xml_file = isxmlfile(fileEntry);
                    if(is_xml_file){
                        saxParser.parse(path, new DefaultHandler(){
                            @Override
                            public void startDocument() throws SAXException {
                                System.out.println("start doc");
                            }

                            @Override
                            public void endDocument() throws SAXException {
                                System.out.println("end doc");
                            }

                            @Override
                            public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
                                int length = attributes.getLength();
                                for (int i = 0 ;i<length;i++){
                                    String name = attributes.getQName(i);
                                    String value = attributes.getValue(i);
                                    if(name.equals("isSolved")){
                                        if (value.equals("true")){
                                            try {
                                                writer.write("Solve\n");
                                            }
                                            catch (IOException e) {
                                                throw new RuntimeException(e);
                                            }
                                        }
                                        else if(value.equals("false")){
                                            try {
                                                writer.write("Unsolve\n");
                                            }
                                            catch (IOException e) {
                                                throw new RuntimeException(e);
                                            }
                                        }
                                        else{
                                            try {
                                                writer.write("Ungradeale\n");
                                            }
                                            catch (IOException e) {
                                                throw new RuntimeException(e);
                                            }
                                        }

                                    }

                                }

                            }
                            @Override
                            public void endElement(String uri, String localName, String qName) throws SAXException {
                                System.out.println(qName);


                            }
                            @Override
                            public void characters(char[] ch, int start, int length) throws SAXException {
                                System.out.println(new String(ch,start,length));
                            }
                        });
                    }
                    else{
                        writer.write("wrong file ungradable");
                    }
                    writer.write("\n");
                }
            }



    private void initText() {
        // Note: until an auto-changing version label is implemented in the future, I removed
        // the version text from the home screen to avoid confusion

        // this.text = new JLabel[3];
        this.text = new JLabel[2];

        JLabel welcome = new JLabel("Welcome to Legup");
        welcome.setFont(new Font("Roboto", Font.BOLD, 23));
        welcome.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel credits = new JLabel("A project by Dr. Bram van Heuveln");
        credits.setFont(new Font("Roboto", Font.PLAIN, 12));
        credits.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel version = new JLabel("Version 3.0.0"); // This should be autochanged in the future
        version.setFont(new Font("Roboto", Font.ITALIC, 10));
        version.setAlignmentX(Component.CENTER_ALIGNMENT);

        this.text[0] = welcome;
        this.text[1] = credits;
        // this.text[2] = version;
    }

    private void render() {
        this.removeAll();

        this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        this.legupUI.setTitle("Legup: A Better Way to Learn Formal Logic");

        JPanel buttons = new JPanel();
        buttons.add(Box.createRigidArea(new Dimension(5, 0)));
        buttons.add(this.buttons[0]);
        buttons.add(Box.createRigidArea(new Dimension(5, 0)));
        buttons.add(this.buttons[1]);
        buttons.add(Box.createRigidArea(new Dimension(5, 0)));
        buttons.add(this.buttons[2]);
        buttons.add(Box.createRigidArea(new Dimension(5, 0)));

        JPanel batchGraderButton = new JPanel();
        batchGraderButton.add(this.buttons[3]);
        batchGraderButton.setAlignmentX(Component.LEFT_ALIGNMENT);

        this.add(Box.createRigidArea(new Dimension(0, 5)));
        for (int i = 0; i < this.text.length; i++) {
            this.add(this.text[i]);
        }
        this.add(buttons);
        this.add(batchGraderButton);
        this.add(Box.createRigidArea(new Dimension(0, 5)));
    }

    private void openNewPuzzleDialog() {
        CreatePuzzleDialog cpd = new CreatePuzzleDialog(this.frame, this);
        cpd.setVisible(true);
    }

    public void openEditorWithNewPuzzle(String game, int rows, int columns) throws IllegalArgumentException {
        // Validate the dimensions
        GameBoardFacade facade = GameBoardFacade.getInstance();
        boolean isValidDimensions = facade.validateDimensions(game, rows, columns);
        if (!isValidDimensions) {
            JOptionPane.showMessageDialog(null,
                    "The dimensions you entered are invalid. Please double check \n" +
                            "the number of rows and columns and try again.",
                    "ERROR: Invalid Dimensions",
                    JOptionPane.ERROR_MESSAGE);
            throw new IllegalArgumentException("ERROR: Invalid dimensions given");
        }

        // Set game type on the puzzle editor
        this.legupUI.displayPanel(2);
        this.legupUI.getPuzzleEditor().loadPuzzleFromHome(game, rows, columns);
    }
}
