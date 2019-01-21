package edu.nju.cs.inform.gui;

import java.awt.*;

/**
 * Created by Xufy on 2016/3/27.
 */
public class LayoutConstants {

    static Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    final static int screenWidth = screenSize.width;
    final static int screenHeight = screenSize.height;

    //Retro Shell
    final static int retroMinSizeHeight = LayoutConstants.screenHeight * 5 / 6;
    final static int retroMinSizeWidth = LayoutConstants.screenWidth * 3 / 4;

    //codeElementsTable
    final static int everyWidth = LayoutConstants.screenWidth / 2 - LayoutConstants.screenWidth / 25;

    final static String iconLocation = "resources\\icons\\icon_small.gif";

}
