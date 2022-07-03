package app.display.dialogs.visual_editor.view.panels.menus.viewMenu;

import app.display.dialogs.visual_editor.handler.Handler;
import app.display.dialogs.visual_editor.view.panels.menus.EditorMenuBar;

import javax.swing.*;
import java.awt.event.ActionListener;

public class ViewMenu extends JMenu
{
    public ViewMenu(EditorMenuBar menuBar)
    {
        super("View");

        JMenu appearance = new JMenu("Colour Scheme");
        JMenu background = new JMenu("Background");

        add(appearance);
        add(background);

        menuBar.addJMenuItem(appearance, "Light", light);
        menuBar.addJMenuItem(appearance, "Dark", dark);
        menuBar.addJMenuItem(appearance, "High Contrast", null);

        menuBar.addJMenuItem(background, "Dot Grid", dotGrid);
        menuBar.addJMenuItem(background, "Cartesian Grid", cartesianGrid);
        menuBar.addJMenuItem(background, "No Grid", noGrid);
    }

    ActionListener dotGrid = e -> Handler.setBackground(Handler.DotGridBackground);
    ActionListener cartesianGrid = e -> Handler.setBackground(Handler.CartesianGridBackground);
    ActionListener noGrid = e -> Handler.setBackground(Handler.EmptyBackground);

    ActionListener light = e -> Handler.setPalette(Handler.lightPalette);
    ActionListener dark = e -> Handler.setPalette(Handler.darkPalette);
}